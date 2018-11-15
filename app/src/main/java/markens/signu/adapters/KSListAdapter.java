package markens.signu.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Set;

import markens.signu.R;
import markens.signu.activities.cert.KSInfoActivity;
import markens.signu.itext.Signature;
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;


public class KSListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context myCtx;
    PdfExt currentPdfExt;
    Context appCtx;
    List<PdfExt> pdfExtList;
    View view;

    private SharedPrefsCtrl spc;
    String[] ksRoutes;

    public KSListAdapter(Context context) {
        this.pdfExtList = pdfExtList;
        myCtx = context;
        appCtx = context.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        updateData();
    }

    public void updateData() {
        Set<String> setCerts = spc.getCerts();
        ksRoutes = setCerts.toArray(new String[setCerts.size()]);
    }

    @Override
    public int getCount() {
        return ksRoutes.length;
    }

    @Override
    public Object getItem(int position) {
        return ksRoutes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        view = inflater.inflate(R.layout.ks_item, null);

        TextView textViewCert = (TextView) view.findViewById(R.id.textViewCert);
        TextView textViewCertDes = (TextView) view.findViewById(R.id.textViewCert);
        File file = new File(ksRoutes[position]);

        textViewCert.setText(file.getName());
//        textViewCertDes.setText((int) file.length());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(myCtx);
                final View viewPassDialog = li.inflate(R.layout.password, null);
                //Show dialog to ask for password
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(myCtx, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(myCtx);
                }
                builder.setView(viewPassDialog)
                        .setTitle(R.string.dialog_insert_pass)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with import
                                final EditText editTextPassword = (EditText) viewPassDialog.findViewById(R.id.editTextPassword);
                                String password = editTextPassword.getText().toString();
                                if(Signature.isPassCorrect(ksRoutes[position], password)){
                                    Intent intent = new Intent(myCtx, KSInfoActivity.class);
                                    intent.putExtra("ks", ksRoutes[position]);
                                    intent.putExtra("password", password);
                                    myCtx.startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .show();



            }
        });

        return view;
    }
}
