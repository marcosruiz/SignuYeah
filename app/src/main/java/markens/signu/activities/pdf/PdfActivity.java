package markens.signu.activities.pdf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import markens.signu.storage.StorageCtrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PdfActivity extends AppCompatActivity {

    Context appCtx;
    Context myCtx;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;
    PdfExt pdfExt;
    UserExt myUserExt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up data
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        myCtx = this;
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutPdf);
        setContentView(R.layout.activity_pdf);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myUserExt = spc.getUserExt();
        pdfExt = (PdfExt) b.getSerializable("pdf_ext");

        //Download pdf
        downloadPdf();

        BottomNavigationView bottomNavPdf = (BottomNavigationView) findViewById(R.id.bottom_navigation_pdf);
        bottomNavPdf.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Default fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("pdf_ext", pdfExt);
        FragmentPdfInfo selectedFragment = new FragmentPdfInfo();
        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_pdf, selectedFragment).commit();


        // Disable buttons

        if (!isSigner(myUserExt.getId(), pdfExt.getSigners())) {
            bottomNavPdf.getMenu().findItem(R.id.nav_sign_pdf).setEnabled(false);
        }
        if (pdfExt.isWithStamp()) {
            bottomNavPdf.getMenu().findItem(R.id.nav_add_signers).setEnabled(false);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            bundle.putSerializable("pdf_ext", pdfExt);
            switch (item.getItemId()) {
                case R.id.nav_info_pdf:
                    selectedFragment = new FragmentPdfInfo();
                    break;
                case R.id.nav_see_pdf:
                    selectedFragment = new FragmentPdfSee();
                    break;
                case R.id.nav_add_signers:
                    selectedFragment = new FragmentPdfAddSigners();
                    break;
                case R.id.nav_sign_pdf:
                    selectedFragment = new FragmentPdfSign();
                    break;
                case R.id.nav_edit_pdf:
                    selectedFragment = new FragmentPdfEdit();
                    break;
            }
            selectedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_pdf, selectedFragment).commit();

            return true;
        }
    };

    private void downloadPdf() {
        StorageCtrl sPdfCtrl = new StorageCtrl(appCtx);
        String pdfName = pdfExt.getFileName() + ".pdf";
        if (!sPdfCtrl.itExists(pdfName)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(spc.get("URL_HEROKU"))
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
                        StorageCtrl sPdfC = new StorageCtrl(myCtx);
                        String fileName = pdfExt.getFileName() + ".pdf";
                        boolean isOk = sPdfC.writeResponseBodyToDisk(rb, fileName);
                    } else {
                        String errBody = null;
                        try {
                            errBody = response.errorBody().string();
                            Gson g = new Gson();
                            SSResponse ssRes = g.fromJson(errBody, SSResponse.class);
                            RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutPdf);
                            Snackbar.make(myLayout, ssRes.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } catch (IOException e) {
                            RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutPdf);
                            Snackbar.make(myLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutPdf);
                    Snackbar.make(myLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    private boolean isSigner(String id, List<SignerExt> signers) {
        boolean isSigner = false;
        for (SignerExt signer : signers) {
            if (signer.getUser().getId().equals(id)) {
                isSigner = true;
            }
        }
        return isSigner;
    }
}
