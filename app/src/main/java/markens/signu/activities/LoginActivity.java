package markens.signu.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.TokenError;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marco on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayoutSignup;

    private static final String CLIENT_ID = "application";
    private static final String CLIENT_SECRET = "secret";
    private static final String GRANT_TYPE = "password";
    private static final String TOKEN_TYPE = "bearer";
    private static final String URL_SERVER = "https://signu-server.herokuapp.com/";
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    private static final String URL_TSA = "https://signu-tsa.herokuapp.com/";
    private static final String URL_CA = "https://signu-ca.herokuapp.com/";
    private static final String UNKNOWN_ERROR = "Something went wrong";

    private Context myCtx;
    private Context appCtx;

    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    /**
     * Save global variables
     * @param spc
     */
    private void saveGlobalVars(SharedPrefsCtrl spc){
        spc.store("URL_SERVER", URL_LOCAL);
        spc.store("URL_TSA", URL_TSA);
        spc.store("URL_CA", URL_CA);
        spc.store("GRANT_TYPE", GRANT_TYPE);
        spc.store("TOKEN_TYPE", TOKEN_TYPE);
        spc.store("CLIENT_ID", CLIENT_ID);
        spc.store("CLIENT_SECRET", CLIENT_SECRET);
        spc.store("UNKNOWN_ERROR", UNKNOWN_ERROR);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCtx = this;
        appCtx = this.getApplicationContext();

        setContentView(R.layout.activity_login);
        coordinatorLayoutSignup = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutLogin);

        //Save global vars
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        saveGlobalVars(spc);

        // Check if we are already logged
        Token myToken = spc.getToken();
        UserExt myUserExt = spc.getUserExt();

        if(myToken != null && myUserExt != null){
            launchActivityNavigation();
        } else {
            setupFloatingLabelErrorEmail();
            setupFloatingLabelErrorPassword();

            final Button buttonSignup = (Button) findViewById(R.id.button_signup);
            buttonSignup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    launchActivitySignup();
                }
            });
            final Button buttonLogin = (Button) findViewById(R.id.button_login);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final EditText et_email = (EditText) findViewById(R.id.edit_email);
                    final EditText et_password = (EditText) findViewById(R.id.edit_pass);

                    String emailStr = et_email.getText().toString();
                    String passStr = et_password.getText().toString();
                    getToken(emailStr, passStr);
                }
            });
        }
    }

    private void getToken(String email, String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);

        Call<Token> call = sss.getToken(email, password, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
        Response<SSResponse> response = null;

        call.enqueue(new retrofit2.Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token myToken = response.body();
                    // save myToken
                    spc.store(myToken);
                    getUserExt(myToken);
                } else {
                    Gson g = new Gson();
                    TokenError myTokenError = g.fromJson(response.errorBody().charStream(), TokenError.class);
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, "Incorrect loggin", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, spc.get("UNKNOWN_ERROR"), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void launchActivitySignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void launchActivityNavigation() {
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows input errors
     */
    private void setupFloatingLabelErrorEmail() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.input_layout_email);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                floatingUsernameLabel.setError(getString(R.string.email_required));
                if (text.length() > 0 && text.length() <= 3 && !floatingUsernameLabel.isErrorEnabled()) {
                    floatingUsernameLabel.setErrorEnabled(true);
                } else if (!isThere(text, '@') && !floatingUsernameLabel.isErrorEnabled()) {
                    floatingUsernameLabel.setErrorEnabled(true);
                } else if (!isThere(text, '.') && !floatingUsernameLabel.isErrorEnabled()) {
                    floatingUsernameLabel.setErrorEnabled(true);
                } else if (floatingUsernameLabel.isErrorEnabled()) {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }

            private boolean isThere(CharSequence cs, char c) {
                boolean isThere = false;
                for (int i = 0; cs.length() > i; i++) {
                    if (cs.charAt(i) == c) {
                        isThere = true;
                    }
                }
                return isThere;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Shows password error
     */
    private void setupFloatingLabelErrorPassword() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.input_layout_password);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                floatingUsernameLabel.setError(getString(R.string.password_required));
                if (text.length() > 0 && text.length() <= 3) {
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    if (floatingUsernameLabel.isErrorEnabled()) {
                        floatingUsernameLabel.setErrorEnabled(false);
                    }
                }
            }

            private boolean isThere(CharSequence cs, char c) {
                boolean isThere = false;
                for (int i = 0; cs.length() > i; i++) {
                    if (cs.charAt(i) == c) {
                        isThere = true;
                    }
                }
                return isThere;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * Get info user from server and save it in sharedprefs and launch NavigationActivity
     * @param token
     */
    private void getUserExt(Token token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.getUserExt(auth);

        call.enqueue(new retrofit2.Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if (response.isSuccessful()) {
                    UserExt myUserExt = response.body().getData().getUserExt();
                    //Save myUserExt
                    spgc.storeUserId(myUserExt.getId());
                    spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
                    spc.store(myUserExt);
                    // Launch ActivityNavigation
                    launchActivityNavigation();
                } else {
                    DrawerLayout myLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    Snackbar.make(myLayout, "Response not successful", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                DrawerLayout myLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                Snackbar.make(myLayout, spc.get("UNKNOWN_ERROR"), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
