package markens.signu.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Marcos on 05/06/2018.
 */

public class SignupActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayoutSignup;

    private static final String UNKNOWN_ERROR = "This error should not appear";
    Context myCtx;
    Context appCtx;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        coordinatorLayoutSignup = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutSignup);

        myCtx = this;
        appCtx = this.getApplicationContext();
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());

        final Button button = (Button) findViewById(R.id.button_finish_signup);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText et_email = (EditText) findViewById(R.id.edit_email);
                final EditText et_name = (EditText) findViewById(R.id.edit_name);
                final EditText et_lastname = (EditText) findViewById(R.id.edit_lastname);
                final EditText et_password = (EditText) findViewById(R.id.edit_password);

                String emailStr = et_email.getText().toString();
                String passStr = et_password.getText().toString();
                String nameStr = et_name.getText().toString();
                String lastnameStr = et_lastname.getText().toString();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(spc.get("URL_SERVER"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SignuServerService sss = retrofit.create(SignuServerService.class);

                Call<SSResponse> call = sss.createUser(emailStr, passStr, nameStr, lastnameStr);
                Response<SSResponse> response = null;

                call.enqueue(new Callback<SSResponse>() {
                    @Override
                    public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                        SSResponse ssRes = null;
                        if(response.isSuccessful()){
                            ssRes = response.body();
                        } else {
                            Gson g = new Gson();
                            ssRes = g.fromJson(response.errorBody().charStream(), SSResponse.class);
                        }
                        Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, ssRes.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    @Override
                    public void onFailure(Call<SSResponse> call, Throwable t) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, UNKNOWN_ERROR, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        });
    }
}
