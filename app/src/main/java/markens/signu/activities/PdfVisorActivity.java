package markens.signu.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import markens.signu.activities.pdf.PdfActivity;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.R;
import markens.signu.adapters.SignerListAdapter;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.StoragePdfCtrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PdfVisorActivity extends AppCompatActivity {

    Context appCtx;
    Context myCtx;
    Activity myActivity;
    PdfExt pdfExt;
    UserExt myUserExt;
    RelativeLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up data
        super.onCreate(savedInstanceState);
//        appCtx = this.getApplicationContext();
//        myCtx = this;
//        myActivity = this;
//        setContentView(R.layout.activity_pdf_visor);
//        Intent intent = getIntent();
//        Bundle b = intent.getExtras();
//        pdfExt = (PdfExt) b.getSerializable("pdf_ext");
//        myLayout = (RelativeLayout) findViewById(R.id.layoutPdfVisor);
//
//        // Get myUserExt
//        final SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
//        myUserExt = spc.getUserExt();
//        // PdfExt myPdfExt = myUserExt.getPdfsOwned().get(index);
//
//        // Get text view
//        TextView pdfId = (TextView) findViewById(R.id.textViewPdfIdValue);
//        TextView pdfOriginalName = (TextView) findViewById(R.id.textViewOriginalNameValue);
//        TextView pdfOwnerEmail = (TextView) findViewById(R.id.textViewOwnerEmailValue);
//        TextView pdfOwnerName = (TextView) findViewById(R.id.textViewOwnerNameValue);
//        TextView pdfOwnerLastname = (TextView) findViewById(R.id.textViewOwnerLastnameValue);
//        ListView signersList = (ListView) findViewById(R.id.listSigners);
//
//        pdfId.setText(pdfExt.getId());
//        pdfOriginalName.setText(pdfExt.getOriginalName());
//        pdfOwnerEmail.setText(pdfExt.getOwnerId().getEmail());
//        pdfOwnerName.setText(pdfExt.getOwnerId().getName());
//        pdfOwnerLastname.setText(pdfExt.getOwnerId().getLastname());
//
//
//        SignerListAdapter sa = new SignerListAdapter(myCtx, pdfExt.getSigners());
//        signersList.setAdapter(sa);
//
//        // Get button
//        final Button buttonSeePdf = (Button) findViewById(R.id.buttonSeePdf);
//        final Button buttonEditPdf = (Button) findViewById(R.id.buttonEditPdf);
//        final Button buttonDeletePdf = (Button) findViewById(R.id.buttonDeletePdf);
//        final Button buttonSignPdf = (Button) findViewById(R.id.buttonSignPdf);
//        final Button buttonExportPdf = (Button) findViewById(R.id.buttonExportPdf);
//        final Button buttonAddSigners = (Button) findViewById(R.id.buttonAddSignersPdf);
//
//        if (!isSigner(myUserExt.getId(), pdfExt.getSigners())) {
//            buttonSignPdf.setEnabled(false);
//        }
//        if (!(myUserExt.getId().equals(pdfExt.getId()))) {
//            buttonEditPdf.setEnabled(false);
//            buttonDeletePdf.setEnabled(false);
//            buttonAddSigners.setEnabled(false);
//        }
//        if (pdfExt.isWithStamp()) {
//            buttonAddSigners.setEnabled(false);
//        }
//
//
//        buttonSeePdf.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                StoragePdfCtrl sPdfCtrl = new StoragePdfCtrl(appCtx);
//                String pdfName = pdfExt.getFileName() + ".pdf";
//                if(sPdfCtrl.itExists(pdfName)){
//                    startPdfViewActivity(pdfName);
//                } else{
//                    // download pdf
//                    downloadPdfAndSee();
//                }
//            }
//        });
//
//        buttonEditPdf.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // TODO
//            }
//        });
//
//        buttonDeletePdf.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // TODO
//            }
//        });
//
//        buttonSignPdf.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                StoragePdfCtrl sPdfCtrl = new StoragePdfCtrl(appCtx);
//                String pdfName = pdfExt.getFileName() + ".pdf";
//                if(sPdfCtrl.itExists(pdfName)){
//                    //
//                } else{
//                    // download pdf
//                    downloadPdfAndSee();
//                }
//
//            }
//        });
//
//
//        buttonAddSigners.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            }
//        });

    }

    private void downloadPdfAndSee() {
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_LOCAL"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);
        Token myToken = spc.getToken();
        String auth = "Bearer " + myToken.getAccessToken();

        Call<ResponseBody> call = sss.downloadPdf(auth, pdfExt.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody rb = response.body();
                    StoragePdfCtrl sPdfC = new StoragePdfCtrl(myCtx);
                    String fileName = pdfExt.getFileName() + ".pdf";
                    boolean isOk = sPdfC.writeResponseBodyToDisk(rb, fileName);
                    if (isOk) {
                        startPdfViewActivity(fileName);
                    }
                } else {
                    String errBody = null;
                    try {
                        errBody = response.errorBody().string();
                        Gson g = new Gson();
                        SSResponse ssRes = g.fromJson(errBody, SSResponse.class);
                        Snackbar.make(myLayout, ssRes.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (IOException e) {
                        Snackbar.make(myLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(myLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void downloadPdfAndSign() {
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_LOCAL"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);
        Token myToken = spc.getToken();
        String auth = "Bearer " + myToken.getAccessToken();

        Call<ResponseBody> call = sss.downloadPdf(auth, pdfExt.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody rb = response.body();
                    StoragePdfCtrl sPdfC = new StoragePdfCtrl(myCtx);
                    String pdfName = pdfExt.getFileName() + ".pdf";
                    boolean isOk = sPdfC.writeResponseBodyToDisk(rb, pdfName);
                    if (isOk) {
                        startPdfSignActivity(pdfName);
                    }
                } else {
                    String errBody = null;
                    try {
                        errBody = response.errorBody().string();
                        Gson g = new Gson();
                        SSResponse ssRes = g.fromJson(errBody, SSResponse.class);
                        Snackbar.make(myLayout, ssRes.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (IOException e) {
                        Snackbar.make(myLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(myLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void startPdfSignActivity(String fileName) {
        // Start PdfActivity
        String fileRoute = appCtx.getFilesDir().getAbsolutePath() + File.separator + fileName;
        Intent intent = new Intent(myCtx, PdfSignActivity.class);
        intent.putExtra("file_route", fileRoute);
        startActivity(intent);
    }

    private void startPdfViewActivity(String fileName) {
        // Start PdfActivity
        String fileRoute = appCtx.getFilesDir().getAbsolutePath() + File.separator + fileName;
        Intent intent = new Intent(myCtx, PdfActivity.class);
        intent.putExtra("file_route", fileRoute);
        startActivity(intent);
    }

    private boolean isSigner(String id, List<SignerExt> signers) {
        boolean isSigner = false;
        for (SignerExt signer : signers) {
            if (signer.getId().getId().equals(id)) {
                isSigner = true;
            }
        }
        return isSigner;
    }
}