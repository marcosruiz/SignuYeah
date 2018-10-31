package markens.signu.activities.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.List;

import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.R;
import markens.signu.StorageController;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{
    StorageController sc;
    Context appCtx;
    ListView list;
    Context activityCtx;

    RelativeLayout layoutMain;

    public UserExt myUserExt;
    public Token myToken;
    SharedPrefsCtrl spc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        activityCtx = this;

        layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
        setContentView(R.layout.activity_main);

        // Get token from Shared preferences
        spc = new SharedPrefsCtrl(appCtx);
        myToken = spc.getToken();

        getInfoUserExt();

        FloatingActionButton buttonUpdate = (FloatingActionButton) findViewById(R.id.floatingActionButtonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getInfoUserExt();
            }
        });

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void getInfoUserExt(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_HEROKU"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + myToken.getAccessToken();
        Call<SSResponse> call = sss.getUserExt(auth);

        call.enqueue(new retrofit2.Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if (response.isSuccessful()) {
                    myUserExt = response.body().getData().getUserExt();
                    //Save myUserExt
                    spc.store(myUserExt);

                    // Default fragment
                    Bundle bundle = new Bundle();
                    List<PdfExt> pdfList = myUserExt.getPdfsOwned();
                    bundle.putSerializable("list_pdf", (Serializable) pdfList);
                    FragmentPdfList selectedFragment = new FragmentPdfList();
                    selectedFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                } else {
                    Snackbar.make(layoutMain, "Response not successful", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                Snackbar.make(layoutMain, "PdfExt not getted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            List<PdfExt> pdfList;
            switch (item.getItemId()) {
                case R.id.nav_owned:
                    pdfList = myUserExt.getPdfsOwned();
                    bundle.putSerializable("list_pdf", (Serializable) pdfList);
                    selectedFragment = new FragmentPdfList();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.nav_to_sign:
                    pdfList = myUserExt.getPdfsToSign();
                    bundle.putSerializable("list_pdf", (Serializable) pdfList);
                    selectedFragment = new FragmentPdfList();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.nav_signed:
                    pdfList = myUserExt.getPdfsSigned();
                    bundle.putSerializable("list_pdf", (Serializable) pdfList);
                    selectedFragment = new FragmentPdfList();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.nav_upload_pdf:
                    bundle.putSerializable("user_ext", myUserExt);
                    bundle.putSerializable("token", myToken);
                    selectedFragment = new FragmentPdfUpload();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.nav_users_related:
                    bundle.putSerializable("user_ext", myUserExt);
                    selectedFragment = new FragmentUsersRelated();
                    selectedFragment.setArguments(bundle);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}
