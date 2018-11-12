package markens.signu.engine;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import markens.signu.exception.NoEmptySignaturesException;

/**
 * This class has all the functionality of the Application: Add empty signatures to a pdf, sign empty signatures to a
 * pdf, add a image to a pdf and add a barcode to a pdf
 *
 * @author Marcos Ruiz Garcia [sobrenombre@gmail.com]
 */
public class Signature2 {

    public enum Margin {
        TOP, BOT, LEFT, RIGHT;

        @Override
        public String toString() {
            switch (this) {
                case TOP:
                    return "top";
                case BOT:
                    return "bot";
                case LEFT:
                    return "left";
                case RIGHT:
                    return "right";
                default:
                    throw new IllegalArgumentException();

            }
        }

        public static Margin parseMargin(String s) {
            s = s.toLowerCase();
            Margin ret = null;
            if (s.equals(TOP.toString())) {
                ret = TOP;
            } else if (s.equals(BOT.toString())) {
                ret = BOT;
            } else if (s.equals(RIGHT.toString())) {
                ret = RIGHT;
            } else if (s.equals(LEFT.toString())) {
                ret = LEFT;
            }
            return ret;
        }
    }

    /**
     *
     * @param keystore
     * @param src
     * @param dest
     * @param pass
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws DocumentException
     * @throws NoEmptySignaturesException
     * @throws URISyntaxException
     */
    public static void signEmptyFieldFromUri(String keystore, String src, String dest, char[] pass, String crlUrl, String tsaUrl)
            throws GeneralSecurityException, IOException, DocumentException, NoEmptySignaturesException, URISyntaxException {
        //Level
        int level = PdfSignatureAppearance.NOT_CERTIFIED;
        // Creating the reader and the stamper
        URL url = new URL(src);
        PdfReader reader = new PdfReader(url.openStream());
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);

        signEmptyField(keystore, level, reader, stamper, pass, crlUrl, tsaUrl);

        stamper.close();
        os.close();
        reader.close();
    }

    /**
     *
     * @param keystore
     * @param src
     * @param dest
     * @param pass
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws DocumentException
     * @throws NoEmptySignaturesException
     */
    public static void signEmptyField(String keystore, String src, String dest, char[] pass, String crlUrl, String tsaUrl)
            throws GeneralSecurityException, IOException, DocumentException, NoEmptySignaturesException {
        //Provider
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.insertProviderAt(provider, 1);
        //Level
        int level = PdfSignatureAppearance.NOT_CERTIFIED;
        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);

        signEmptyField(keystore, level, reader, stamper, pass, crlUrl, tsaUrl);

        stamper.close();
        os.close();
        reader.close();
    }

    /**
     *
     * @param keystore
     * @param level
     * @param reader
     * @param stamper
     * @param pass
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws DocumentException
     * @throws NoEmptySignaturesException
     */
    public static void signEmptyField(String keystore, int level,
                                      PdfReader reader, PdfStamper stamper, char[] pass, String crlUrl, String tsaUrl)
            throws GeneralSecurityException, IOException, DocumentException, NoEmptySignaturesException {
        //Provider
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.insertProviderAt(provider, 1);
        KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
        ks.load(new FileInputStream(keystore), pass);
        String alias = (String) ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, pass);

        // CRL list
        Certificate[] chain = ks.getCertificateChain(alias);
        CrlClient crlClient = new CrlClientOnline(crlUrl);
        List<CrlClient> crlList = new ArrayList<CrlClient>();
        crlList.add(crlClient);

        //Searching blank signature names
        AcroFields fields = reader.getAcroFields();
        ArrayList<String> sigNames = fields.getSignatureNames();
        ArrayList<String> blankSigNames = fields.getBlankSignatureNames();
        if (blankSigNames.isEmpty()) {
            throw new NoEmptySignaturesException();
        }
        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature(blankSigNames.get(0));
        appearance.setCertificationLevel(level);
        // Creating the signature
        ExternalSignature pks = new PrivateKeySignature(pk, "SHA-256", "BC");
        ExternalDigest digest = new BouncyCastleDigest();
        MakeSignature.signDetached(appearance, digest, pks, chain, crlList, null, null, 0, MakeSignature.CryptoStandard.CMS);
    }

    /**
     *
     * @param src
     * @param dest
     * @param qos
     * @param margin
     * @param img
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public static void addEmptyFieldsFromUri(String src, String dest, int qos, Margin margin, String img) throws MalformedURLException, IOException, DocumentException {
        URL url = new URL(src);
        PdfReader reader = new PdfReader(url);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));

        if (!(src == null || dest == null)) {
            addEmptyFields(reader, stamper, qos, margin, img);
        }
        stamper.close();
        reader.close();
    }
    /**
     *
     * @param reader
     * @param stamper
     * @param qos
     * @param margin
     * @param img
     */
    private static void addEmptyFields(PdfReader reader, PdfStamper stamper, int qos, Margin margin, String img) throws IOException, DocumentException {

        if (qos < 1) {
            qos = 1;
        }

        for (int i = 1; i <= qos; i++) {
            // get position of signature
            float[] coords = calcCoords(reader, margin, i);
            // get name of field
            String name = "sig" + i;
            if (img == null) {
                addEmptyField(stamper, name, coords[0], coords[1], coords[2], coords[3]);
            } else {
                addEmptyFieldWithImage(stamper, name, coords[0], coords[1], coords[2], coords[3], margin, img, 50);
            }

        }

    }

    /**
     *
     * @param src
     * @param dest
     * @param qos
     * @param margin
     * @param img
     * @throws IOException
     * @throws DocumentException
     */
    public static void addEmptyFields(String src, String dest, int qos, Margin margin, String img) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));

        addEmptyFields(reader, stamper, qos, margin, img);
        // close the stamper
        stamper.close();
        reader.close();
    }

    /**
     *
     * @param stamper
     * @param name
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param margin
     * @param img
     * @param shift
     */
    public static void addEmptyFieldWithImage(PdfStamper stamper, String name, float x1, float y1, float x2, float y2, Margin margin, String img, int shift) throws IOException, DocumentException {

        if (null == margin) {
            throw new NullPointerException();
        } else
            switch (margin) {
                case TOP:
                case BOT:
                    putImageSquare(stamper, x1 - shift, y1, img, 0, 20, 10);
                    break;
                case LEFT:
                case RIGHT:
                    putImageSquare(stamper, x1, y1 - shift, img, 0, 20, 10);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

        addEmptyField(stamper, name, x1, y1, x2, y2);
    }

    /**
     * Calculate the bottom-left and top-right corners of a signature letting a gap for the image
     *
     * @param reader
     * @param margin
     * @param pos
     * @return table of 4 ints wich correspond to x,y bottom left corner and x,y top right corners of a rectangle
     */
    public static float[] calcCoords(PdfReader reader, Margin margin, int pos) {

        Rectangle pagesize = reader.getPageSize(1);
        float rightLimit = pagesize.getRight();
        float topLimit = pagesize.getTop();
        float spaceForImage = 50;
        float spaceFromLimit1st = 10;
        float spaceFromLimit2nd = 50;
        float width = 100;
        float spaceBeetweenSigs = 10;

        float[] coords = new float[4];
        if (null == margin) {
            throw new NullPointerException();
        } else
            switch (margin) {
                case TOP:
                    coords[0] = (spaceForImage + ((pos - 1) * width)) + (spaceForImage + spaceBeetweenSigs) * pos; //x bottom-left
                    coords[1] = topLimit - spaceForImage; //y bottom-letft
                    coords[2] = (spaceForImage + (pos * width)) + (spaceForImage + spaceBeetweenSigs) * pos; //x top-right
                    coords[3] = topLimit - spaceFromLimit1st; //y top-right
                    break;
                case BOT:
                    coords[0] = (spaceForImage + ((pos - 1) * width)) + (spaceForImage + spaceBeetweenSigs) * pos;
                    coords[1] = spaceFromLimit1st;
                    coords[2] = (spaceForImage + (pos * width)) + (spaceForImage + spaceBeetweenSigs) * pos;
                    coords[3] = spaceFromLimit2nd;
                    break;
                case LEFT:
                    coords[0] = spaceFromLimit1st;
                    coords[1] = (spaceForImage + ((pos - 1) * width)) + (spaceForImage + spaceBeetweenSigs) * pos;
                    coords[2] = spaceFromLimit2nd;
                    coords[3] = (spaceForImage + (pos * width)) + (spaceForImage + spaceBeetweenSigs) * pos;
                    break;
                case RIGHT:
                    coords[0] = rightLimit - spaceFromLimit2nd;
                    coords[1] = (spaceForImage + ((pos - 1) * width)) + (spaceForImage + spaceBeetweenSigs) * pos;
                    coords[2] = rightLimit - spaceFromLimit1st;
                    coords[3] = (spaceForImage + (pos * width)) + (spaceForImage + spaceBeetweenSigs) * pos;
                    break;
                default:
                    throw new IllegalArgumentException("Margin not found");
            }
        return coords;
    }

    /**
     * Create an interactive empty rectangle to sign digitally
     *
     * @param stamper
     * @param name
     * @param xblc : x bottom-left corner
     * @param yblc : y bottom-left corner
     * @param xrtc : x right-top corner
     * @param yrtc : y right-top corner
     */
    public static void addEmptyField(PdfStamper stamper, String name, float xblc, float yblc, float xrtc, float yrtc) throws NullPointerException {
        if (stamper == null || name == null) {
            throw new java.lang.NullPointerException();
        }
        // addemptysigns a signature form field
        PdfFormField field = PdfFormField.createSignature(stamper.getWriter());
        field.setFieldName(name);
        // set the widget properties
        field.setWidget(new Rectangle(xblc, yblc, xrtc, yrtc), PdfAnnotation.HIGHLIGHT_OUTLINE);
        field.setFlags(PdfAnnotation.FLAGS_PRINT);
        // add the annotation
        stamper.addAnnotation(field, 1);
    }

    /**
     *
     * @param stamper
     * @param x coordinate x of upper-left corner of our image
     * @param y coordinate x of upper-left corner of our image
     * @param img
     * @param rotation
     * @param sizeSquare
     * @param sizeMargin
     * @throws BadPdfFormatException
     * @throws IOException
     * @throws DocumentException
     */
    public static void putImageSquare(PdfStamper stamper, float x, float y, String img, int rotation, int sizeSquare, int sizeMargin) throws BadPdfFormatException, IOException, DocumentException {
        if (stamper == null || img == null) {
            throw new java.lang.NullPointerException();
        }
        Image image = Image.getInstance(img);
        image.setAbsolutePosition(x + sizeMargin, y + sizeMargin);
        image.scaleAbsolute(sizeSquare, sizeSquare);
        image.setRotationDegrees(rotation);
        PdfContentByte over = stamper.getOverContent(1);
        over.addImage(image);
    }
    /**
     *
     * @param reader
     * @param stamper
     * @param code
     * @throws DocumentException
     */
    public static void addBarcode(PdfReader reader, PdfStamper stamper, String code) throws DocumentException {
        PdfContentByte over = stamper.getOverContent(1);
        String altText = "Cód. Validación: " + code + " | Página 1 de " + reader.getNumberOfPages();//TODO

        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode(code);
        barcode.setAltText(altText);
        barcode.setTextAlignment(Element.ALIGN_LEFT);

        Image image = barcode.createImageWithBarcode(over, BaseColor.BLACK, BaseColor.BLACK);

        Rectangle pagesize = reader.getPageSize(1);
        float x = pagesize.getRight() - 50;
        float y = pagesize.getBottom() + 20;
        PdfTemplate template
                = barcode.createTemplateWithBarcode(over, BaseColor.BLACK, BaseColor.BLACK);
        putImage(stamper, image, x, y, 500, 40, 90);
    }
    /**
     *
     * @param src
     * @param dest
     * @param code
     * @param text
     * @throws IOException
     * @throws DocumentException
     */
    public static void addTextAndBarcode(String src, String dest, String code, String text) throws IOException, DocumentException {
        //Open
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        //Put text
        if (text != null) {
            Rectangle pagesize = reader.getPageSize(1);
            float x = pagesize.getRight() - 3;
            float y = pagesize.getBottom() + 20;
            PdfContentByte over = stamper.getOverContent(1);
            over.setFontAndSize(BaseFont.createFont(), 8);
            over.beginText();
            over.showTextAligned(Element.ALIGN_LEFT, text, x, y, 90);
            over.endText();
        }
        //Put barcode
        addBarcode(reader, stamper, code);
        //Close
        stamper.close();
        reader.close();
    }
    /**
     *
     * @param stamper
     * @param text
     * @param rotation
     * @param x
     * @param y
     */
    public static void addText(PdfStamper stamper, String text, int rotation, float x, float y) {

    }

    /**
     *
     * @param src
     * @param dest
     * @param img
     * @throws IOException
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static void addImage(String src, String dest, String img) throws IOException, FileNotFoundException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        Rectangle pagesize = reader.getPageSize(1);
        float x = pagesize.getRight() - 50;
        float y = pagesize.getBottom() + 20;
        putImage(stamper, img, x, y, 500, 40, 90);
        stamper.close();
        reader.close();

    }

    /**
     *
     * @param stamper
     * @param img
     * @param posX
     * @param posY
     * @param lenX
     * @param lenY
     * @param rotation
     * @throws BadElementException
     * @throws IOException
     * @throws DocumentException
     */
    public static void putImage(PdfStamper stamper, String img, float posX, float posY, int lenX, int lenY, int rotation) throws BadElementException, IOException, DocumentException {
        if (stamper == null || img == null) {
            throw new java.lang.NullPointerException();
        }
        Image image = Image.getInstance(img);
        putImage(stamper, image, posX, posY, lenX, lenY, rotation);
        //image.scaleAbsolute(lenX, lenY);

    }

    /**
     *
     * @param stamper
     * @param image
     * @param posX
     * @param posY
     * @param lenX
     * @param lenY
     * @param rotation
     * @throws DocumentException
     */
    public static void putImage(PdfStamper stamper, Image image, float posX, float posY, int lenX, int lenY, int rotation) throws DocumentException {
        image.scaleToFit(lenX, lenY);
        image.setAbsolutePosition(posX, posY);
        image.setRotationDegrees(rotation);
        PdfContentByte over = stamper.getOverContent(1);
        over.addImage(image);
    }

}
