package markens.signu.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

import markens.signu.R;
import markens.signu.activities.cert.FragmentKSList;
import markens.signu.activities.main.FragmentPdfContainer;
import markens.signu.activities.user.FragmentUserContainer;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context appCtx;

    public UserExt myUserExt;
    public Token myToken;

    private Menu menu;

    private SharedPrefsCtrl spc;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get token from Shared preferences

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        myToken = spc.getToken();
        myUserExt = spc.getUserExt();

        if (myToken == null || myUserExt == null) {
            launchLoginActivity();
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

//                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

//                invalidateOptionsMenu();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, new FragmentPdfContainer()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, new FragmentPdfContainer(), "selected_fragment_main").commit();

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
        this.menu = menu;
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
        if (id == R.id.nav_user) {
            selectedFragment = new FragmentUserContainer();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment, "selected_fragment_main").commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_ks) {
            selectedFragment = new FragmentKSList();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment, "selected_fragment_main").commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_settings) {
            selectedFragment = new FragmentSettings();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment, "selected_fragment_main").commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, R.string.dialog_share);
            startActivity(Intent.createChooser(share, "Share Text"));
        } else if (id == R.id.nav_about) {
            DrawerLayout myLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            Snackbar.make(myLayout, R.string.dialog_about, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action, null).show();
        } else if (id == R.id.nav_pdf) {
            selectedFragment = new FragmentPdfContainer();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment, "selected_fragment_main").commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_log_out) {
            logOut();
        }
        return true;
    }

    private void logOut() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService signuServerService = retrofit.create(SignuServerService.class);


        // Log out
        Call<SSResponse> call = signuServerService.logOut("Bearer " + myToken.getAccessToken());
        call.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                spc.store((Token) null);
                launchLoginActivity();
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                DrawerLayout myLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                Snackbar.make(myLayout, R.string.server_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action, null).show();
                spc.store((Token) null);
                launchLoginActivity();
            }
        });
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void notifyLanguageChange(String language) {
        Intent refresh = new Intent(this, NavigationActivity.class);
        startActivity(refresh);
        finish();
    }
}
