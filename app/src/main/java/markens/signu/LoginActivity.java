package markens.signu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

/**
 * Created by marco on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupFloatingLabelErrorEmail();
        setupFloatingLabelErrorPassword();

        final Button button_signup = (Button) findViewById(R.id.button_signup);
        button_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchActivity();
            }
        });

    }

    private void launchActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     * Input errors
     */
    private void setupFloatingLabelErrorEmail() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.input_layout_email);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                floatingUsernameLabel.setError(getString(R.string.email_required));
                if (text.length() > 0 && text.length() <= 3) {
                    floatingUsernameLabel.setErrorEnabled(true);
                } else if(!isThere(text, '@')){
                    floatingUsernameLabel.setErrorEnabled(true);
                } else if(!isThere(text,'.')){
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }

            private boolean isThere(CharSequence cs, char c){
                boolean isThere = false;
                for(int i = 0; cs.length() > i; i++){
                    if(cs.charAt(i)==c){
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

    private void setupFloatingLabelErrorPassword() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.input_layout_password);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                floatingUsernameLabel.setError(getString(R.string.password_required));
                if (text.length() > 0 && text.length() <= 3) {
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }

            private boolean isThere(CharSequence cs, char c){
                boolean isThere = false;
                for(int i = 0; cs.length() > i; i++){
                    if(cs.charAt(i)==c){
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
