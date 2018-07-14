package markens.signu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

/**
 * Created by Marcos on 05/06/2018.
 */

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        final Button button = (Button) findViewById(R.id.button_finish_signup);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText et_email = (EditText) findViewById(R.id.edit_email);
                final EditText et_name = (EditText) findViewById(R.id.edit_name);
                final EditText et_lastname= (EditText) findViewById(R.id.edit_lastname);

                CallAPISignu call = new CallAPISignu();
                call.execute(et_email.getText().toString(), et_name.getText().toString(), et_lastname.getText().toString());

            }
        });

    }

    private void launchActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
