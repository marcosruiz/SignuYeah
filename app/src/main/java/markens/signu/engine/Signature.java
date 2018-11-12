package markens.signu.engine;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Signature {

    public static void signWithCrl(String routeKS, String pdfSrc, String pdfDst, String pass, String tsaUrl, String crlUrl) throws IOException, GeneralSecurityException, DocumentException {
        TSAClient tsaClient = new TSAClientBouncyCastle(tsaUrl);
        char[] passCharArray = pass.toCharArray();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.insertProviderAt(provider, 1);
        KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
        ks.load(new FileInputStream(routeKS), passCharArray);
        String alias = (String) ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, passCharArray);
        Certificate[] chain = ks.getCertificateChain(alias);
        CrlClient crlClient = new CrlClientOnline(crlUrl);
        //CrlClient crlClient = new CrlClientOnline("http://crl.certum.pl/ca.crl");
        List<CrlClient> crlList = new ArrayList<CrlClient>();
        crlList.add(crlClient);
        sign(pdfSrc, pdfDst, chain, pk, DigestAlgorithms.SHA256, provider.getName(), MakeSignature.CryptoStandard.CMS, "Test", "Zaragoza", crlList, null, null, 0);
    }

    public static void sign(String src, String dest, Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider, MakeSignature.CryptoStandard subfilter, String reason, String location, Collection<CrlClient> crlList, OcspClient ocspClient, TSAClient tsaClient, int estimatedSize) throws GeneralSecurityException, IOException, DocumentException {
        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig");
        // Creating the signature
        ExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        ExternalDigest digest = new BouncyCastleDigest();
        MakeSignature.signDetached(appearance, digest, pks, chain, crlList, ocspClient, tsaClient, estimatedSize, subfilter);
    }

    public static KeyStore getKeyStore(String route, String password) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.insertProviderAt(provider, 1);
        KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
        char[] passCharArray = password.toCharArray();
        ks.load(new FileInputStream(route), passCharArray);
        return ks;
    }

    public static boolean isPassCorrect(String route, String password){
        try{
            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.insertProviderAt(provider, 1);
            KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
            char[] passCharArray = password.toCharArray();
            ks.load(new FileInputStream(route), passCharArray);
            return true;
        } catch(Exception e){
            return false;
        }

    }



}
