package markens.signu.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.List;

import markens.signu.R;
import markens.signu.StorageController;
import markens.signu.activities.main.FragmentMain;
import markens.signu.activities.main.FragmentPdfList;
import markens.signu.activities.main.FragmentSimple;
import markens.signu.activities.main.MainActivity;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context appCtx;

    RelativeLayout layoutMain;

    public UserExt myUserExt;
    public Token myToken;
    SharedPrefsCtrl spc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get token from Shared preferences
        spc = new SharedPrefsCtrl(appCtx);
        myToken = spc.getToken();
        getInfoUserExt();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment selectedFragment = null;
        Bundle bundle = new Bundle();
        List<PdfExt> pdfList;
        if (id == R.id.nav_user) {
            selectedFragment = new FragmentSimple();
        } else if (id == R.id.nav_cert) {
            selectedFragment = new FragmentCertList();
        } else if (id == R.id.nav_settings) {
            selectedFragment = new FragmentSimple();
        } else if (id == R.id.nav_share) {
            selectedFragment = new FragmentSimple();
        } else if (id == R.id.nav_about) {
            selectedFragment = new FragmentSimple();
        } else if (id == R.id.nav_pdf) {
            selectedFragment = new FragmentMain();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getInfoUserExt() {
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

                } else {
//                    Snackbar.make(layoutMain, "Response not successful", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }

            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
//                Snackbar.make(layoutMain, "PdfExt not getted", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }
}
