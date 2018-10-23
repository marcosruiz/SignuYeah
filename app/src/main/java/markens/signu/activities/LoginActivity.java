package markens.signu.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import markens.signu.CallAPISignu;
import markens.signu.Callback;
import markens.signu.GSonSavingMethods;
import markens.signu.MainActivity;
import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.StorageController;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.TokenError;
import markens.signu.objects.User;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marco on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity implements Callback {

    CoordinatorLayout coordinatorLayoutSignup;

    private static final String CLIENT_ID = "application";
    private static final String CLIENT_SECRET = "secret";
    private static final String GRANT_TYPE = "password";
    private static final String TOKEN_TYPE = "bearer";
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    private static final String URL_HEROKU = "https://signu-server.herokuapp.com/";
    private static final String UNKNOWN_ERROR = "Something went wrong";
    private Context appCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();

        setContentView(R.layout.activity_login);
        coordinatorLayoutSignup = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutLogin);

        setupFloatingLabelErrorEmail();
        setupFloatingLabelErrorPassword();

        final Button button_signup = (Button) findViewById(R.id.button_signup);
        button_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchActivitySignup();
            }
        });
        final Button button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText et_email = (EditText) findViewById(R.id.edit_email);
                final EditText et_password = (EditText) findViewById(R.id.edit_pass);

                String emailStr = et_email.getText().toString();
                String passStr = et_password.getText().toString();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL_LOCAL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SignuServerService sss = retrofit.create(SignuServerService.class);

                Call<Token> call = sss.getToken(emailStr, passStr, GRANT_TYPE, CLIENT_ID, CLIENT_SECRET);
                Response<SSResponse> response = null;

                call.enqueue(new retrofit2.Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        if(response.isSuccessful()){
                            Token myToken = response.body();
                            // TODO save myToken
                            GSonSavingMethods gSonSM = new GSonSavingMethods(appCtx);
                            gSonSM.store(myToken);
                            Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, "Welcome!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            // Go to MainActivity
                            launchPdfList();
                            launchActivityMain();
                        } else {
                            Gson g = new Gson();
                            TokenError myTokenError = g.fromJson(response.errorBody().charStream(), TokenError.class);
                            Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, "Incorrect loggin", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, UNKNOWN_ERROR, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });

            }
        });
    }

    public void callback(JSONObject jsonInfo) {

        try {
            System.out.println(jsonInfo.toString());
            String valueTokenType = jsonInfo.getString("token_type");
            if (valueTokenType.equals("bearer")) {
                //Save session
                SharedPreferences preferences = getSharedPreferences("app.signu", Context.MODE_PRIVATE);
                //Save user
                StorageController sc = new StorageController(this);
                sc.saveJSON("myToken.data", jsonInfo);
                //Go to MainActivity
                // launchActivityMain();
                Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, "Login working", Snackbar.LENGTH_LONG); //TODO
                snackbar.show();
            } else {
                //Show error message
                showSnackBar(jsonInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void launchActivitySignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void launchActivityMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void launchPdfList() {
        Intent intent = new Intent(this, PdfListActivity.class);
        startActivity(intent);
    }

    public void showSnackBar(JSONObject jsonInfo) {
        String info = "Incorrect login";
        try {
            info = jsonInfo.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, info, Snackbar.LENGTH_LONG); //TODO
        snackbar.show();
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
}
