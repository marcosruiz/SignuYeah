package markens.signu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Marcos on 05/06/2018.
 */

public class SignupActivity extends AppCompatActivity implements Callback{

    CoordinatorLayout coordinatorLayoutSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        coordinatorLayoutSignup = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutSignup);


        final Button button = (Button) findViewById(R.id.button_finish_signup);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText et_email = (EditText) findViewById(R.id.edit_email);
                final EditText et_name = (EditText) findViewById(R.id.edit_name);
                final EditText et_lastname= (EditText) findViewById(R.id.edit_lastname);

                CallAPISignu call = new CallAPISignu(SignupActivity.this,"http://10.0.3.2:3000/api/users/signup", "POST"); //TODO esto no deberia ir a pelo
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("email", et_email.getText().toString());
                    jsonParam.put("name", et_name.getText().toString());
                    jsonParam.put("lastname", et_lastname.getText().toString());
                    call.execute(jsonParam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public void callback(JSONObject jsonInfo){
        String info = "Unknown error :/";
        try {
            if(jsonInfo.getInt("code") == 0){
                //Everything right
                info = "YEAH! Check your email and log in for first time";
            } else{
                //Error on server
                info = jsonInfo.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Snackbar snackbar = Snackbar.make(coordinatorLayoutSignup, info, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void launchActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
