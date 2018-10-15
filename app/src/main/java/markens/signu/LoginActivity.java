package markens.signu;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import markens.signu.objects.Token;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by marco on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity implements Callback {

    CoordinatorLayout coordinatorLayoutSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                //Sending POST to login
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                CallAPISignu call = new CallAPISignu(LoginActivity.this, "https://signu-server.herokuapp.com/oauth2/token", "POST", headers); //TODO esto no deberia ir a pelo
                JSONObject jsonParam = new JSONObject();
                final EditText et_email = (EditText) findViewById(R.id.edit_email);
                final EditText et_pass = (EditText) findViewById(R.id.edit_pass);
//                try {
//                    jsonParam.put("grant_type", "password");
//                    jsonParam.put("client_id", "application");
//                    jsonParam.put("client_secret", "secret");
//                    jsonParam.put("username", et_email.getText().toString());
//                    jsonParam.put("password", et_pass.getText().toString());
//                    call.execute(jsonParam);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://signu-server.herokuapp.com/")
                        .build();
                SignuServerService service = retrofit.create(SignuServerService.class);
                Call<Token> res = service.getToken(et_email.getText().toString(), et_pass.getText().toString(), "password", "application", "secret");

                // Show info
//                if(token.isSuccessful()){
//                    Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, "Login working", Snackbar.LENGTH_LONG); //TODO
//                    snackbar.show();
//                }
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
