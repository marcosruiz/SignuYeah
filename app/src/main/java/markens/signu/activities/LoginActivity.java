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

import markens.signu.LocaleManager;
import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marco on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayoutSignup;

    private Context myCtx;
    private Context appCtx;
    private SharedPrefsCtrl spc;

    /**
     * Save global variables
     *
     * @param spc
     */
    private void saveGlobalVars(SharedPrefsCtrl spc) {
        if (spc.get("URL_SERVER") == null || spc.get("URL_SERVER").equals("")) {
            spc.store("URL_SERVER", getString(R.string.URL_SERVER_LOCAL));
        }
        if (spc.get("URL_TSA") == null || spc.get("URL_TSA").equals("")) {
            spc.store("URL_TSA", getString(R.string.URL_TSA));
        }
        if (spc.get("URL_CA") == null || spc.get("URL_CA").equals("")) {
            spc.store("URL_CA", getString(R.string.URL_CA));
        }
        spc.store("GRANT_TYPE", getString(R.string.GRANT_TYPE));
        spc.store("TOKEN_TYPE", getString(R.string.TOKEN_TYPE));
        spc.store("CLIENT_ID", getString(R.string.CLIENT_ID));
        spc.store("CLIENT_SECRET", getString(R.string.CLIENT_SECRET));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCtx = this;
        appCtx = this.getApplicationContext();

        setContentView(R.layout.activity_login);
        coordinatorLayoutSignup = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutLogin);

        //Save global vars
        LocaleManager.setLocale(appCtx);
        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        saveGlobalVars(spc);

        // Check if we are already logged
        Token myToken = spc.getToken();
        UserExt myUserExt = spc.getUserExt();

        if (myToken != null && myUserExt != null) {
            launchActivityNavigation();
        } else {
            setupFloatingLabelErrorEmail();
            setupFloatingLabelErrorPassword();

            final Button buttonSignup = (Button) findViewById(R.id.buttonSignup);
            buttonSignup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    launchActivitySignup();
                }
            });
            final Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final EditText et_email = (EditText) findViewById(R.id.editTextEmail);
                    final EditText et_password = (EditText) findViewById(R.id.editTextPassword);

                    String emailStr = et_email.getText().toString();
                    String passStr = et_password.getText().toString();
                    getToken(emailStr, passStr);
                }
            });
        }
    }

    private void getToken(String email, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);

        Call<Token> call = sss.getToken(email, password, getString(R.string.GRANT_TYPE), getString(R.string.CLIENT_ID), getString(R.string.CLIENT_SECRET));
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
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, R.string.incorrect_login, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, R.string.server_error, Snackbar.LENGTH_LONG);
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
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
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
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
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
     *
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
                    spc.storeCurrentUserId(myUserExt.getId());
                    spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
                    spc.store(myUserExt);
                    // Launch ActivityNavigation
                    launchActivityNavigation();
                } else {
                    DrawerLayout myLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }

            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                DrawerLayout myLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                Snackbar.make(myLayout, R.string.server_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action, null).show();
            }
        });
    }
}
