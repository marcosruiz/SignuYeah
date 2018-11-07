package markens.signu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Set;

import markens.signu.R;
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class CertListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context myCtx;
    PdfExt currentPdfExt;
    Context appCtx;
    List<PdfExt> pdfExtList;
    View view;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;
    String[] certs;
    public CertListAdapter(Context context) {
        this.pdfExtList = pdfExtList;
        myCtx = context;
        appCtx = context.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        Set<String> setCerts = spc.getCerts();
        certs = setCerts.toArray(new String[setCerts.size()]);
    }

    @Override
    public int getCount() {
        return certs.length;
    }

    @Override
    public Object getItem(int position) {
        return certs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        view = inflater.inflate(R.layout.cert_item, null);

        TextView textViewCert = (TextView) view.findViewById(R.id.textViewCert);
        TextView textViewCertDes = (TextView) view.findViewById(R.id.textViewCert);
        File file = new File(certs[position]);

        textViewCert.setText(file.getName());
//        textViewCertDes.setText((int) file.length());

        return view;
    }
}