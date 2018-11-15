package markens.signu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import markens.signu.R;
import markens.signu.storage.SharedPrefsCtrl;



public class CertListAdapter extends BaseAdapter {
    View view;
    private static LayoutInflater inflater;
    Context myCtx;
    Context appCtx;

    private SharedPrefsCtrl spc;
    Certificate[] certificates;

    public CertListAdapter(Context context, Certificate[] certificates) {
        myCtx = context;
        appCtx = context.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.certificates = certificates;
    }

    @Override
    public int getCount() {
        return certificates.length;
    }

    @Override
    public Object getItem(int position) {
        return certificates[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO creation date is trash
        view = inflater.inflate(R.layout.cert_item, null);

        TextView textViewEncoded = (TextView) view.findViewById(R.id.textViewEncodedValue);
        TextView textViewFormat = (TextView) view.findViewById(R.id.textViewFormatValue);
        TextView textViewAlg = (TextView) view.findViewById(R.id.textViewAlgValue);
        TextView textViewDetails = (TextView) view.findViewById(R.id.textViewDetailsValue);
        TextView textViewType = (TextView) view.findViewById(R.id.textViewTypeValue);

        textViewFormat.setText(certificates[position].getPublicKey().getFormat());
        textViewAlg.setText(certificates[position].getPublicKey().getAlgorithm());
        textViewType.setText(certificates[position].getType());

        // X509Certificate
        X509Certificate certX509 = null;
        if(certificates[position].getType() == "X.509"){
            certX509 = (X509Certificate) certificates[position];
        }
        String certInfoString = certX509.toString();
        textViewDetails.setText(certInfoString);
        String version = Integer.toString(certX509.getVersion());
        BigInteger serialNumber = certX509.getSerialNumber();
        String issuerDN = certX509.getIssuerDN().toString();
        String subjectDN = certX509.getSubjectDN().toString();
        String publicKey = certX509.getPublicKey().toString();

        byte[] encCertInfo = null;
        try {
            encCertInfo = certificates[position].getEncoded();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(encCertInfo);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        return view;
    }
}
