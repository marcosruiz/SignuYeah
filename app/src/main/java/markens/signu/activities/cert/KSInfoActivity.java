package markens.signu.activities.cert;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.Certificate;

import markens.signu.R;
import markens.signu.activities.NavigationActivity;
import markens.signu.adapters.CertListAdapter;
import markens.signu.adapters.KSListAdapter;
import markens.signu.engine.Signature;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import markens.signu.storage.StorageCtrl;

public class KSInfoActivity extends AppCompatActivity {

    Context appCtx;
    Context myCtx;
    String routeKS;
    String password;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        myCtx = this;
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutPdf);
        setContentView(R.layout.activity_ks_info);

        // Get myUserExt
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());

        // Get ks
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        routeKS = b.getString("ks");
        password = b.getString("password");

        try {
            KeyStore ks = Signature.getKeyStore(routeKS, password);
//            String alias = (String) ks.alias().nextElement();
//            Certificate[] chain = ks.getCertificateChain(alias);

            // Get text view
            TextView textViewFileName = (TextView) findViewById(R.id.textViewKSNameValue);
            TextView textViewType = (TextView) findViewById(R.id.textViewTypeValue);
            TextView textViewProviderName = (TextView) findViewById(R.id.textViewProviderNameValue);
            TextView textViewProviderInfo = (TextView) findViewById(R.id.textViewProviderInfoValue);
            TextView textViewProviderVersion = (TextView) findViewById(R.id.textViewProviderVersionValue);
            TextView textViewCreationDate = (TextView) findViewById(R.id.textViewCreationDateValue);

//            TextView textView = (TextView) findViewById(R.id.textView);

            // Set info KeyStore
            textViewFileName.setText(new File(routeKS).getName());
            textViewType.setText(ks.getType());
            textViewProviderName.setText(ks.getProvider().getName());
            textViewProviderInfo.setText(ks.getProvider().getInfo());
            textViewProviderVersion.setText(String.valueOf(ks.getProvider().getVersion()));

            // List of certificates
            ListView listCertificates = (ListView) findViewById(R.id.listViewCertificates);
            String alias = ks.aliases().nextElement();
            Certificate[] certs = ks.getCertificateChain(alias);
            listCertificates.setAdapter(new CertListAdapter(myCtx, certs));
            textViewCreationDate.setText(ks.getCreationDate(alias).toString());

//            textViewTsa.setText("");
//            textViewCa.setText("");
//            textViewAlg.setText(pk.getAlgorithm());
//            textViewFormat.setText(pk.getFormat());
//            textViewEncoded.setText("");
//            textView.setText(chain[0].getType());

        } catch (Exception e) {
            e.printStackTrace();
            RelativeLayout layoutPdf = (RelativeLayout) findViewById(R.id.activityCertInfo);
            Snackbar.make(layoutPdf, R.string.wrong, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action, null).show();
        }



        // Get button
        final Button buttonDelete = (Button) findViewById(R.id.buttonDeleteCert);
        final Button buttonExport = (Button) findViewById(R.id.buttonExportCert);

        buttonExport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(myCtx,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity)myCtx,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {
                    File fileInternalStorage = new File(routeKS);
                    File fileExternalStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileInternalStorage.getName());

                    try {
                        RelativeLayout layoutPdf = (RelativeLayout) findViewById(R.id.activityCertInfo);
                        StorageCtrl.copy(fileInternalStorage, fileExternalStorage);
                        Snackbar.make(layoutPdf, R.string.check_downloads, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) findViewById(R.id.activityCertInfo);
                        Snackbar.make(layoutPdf, R.string.exception, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    }
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Delete cert
                File fileKS = new File(routeKS);
                StorageCtrl.delete(fileKS);
                // Update cert list
                spc.deleteCert(routeKS);
                // Close activity
                finish();
            }
        });
    }


}
