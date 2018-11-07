package markens.signu.activities.user;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import markens.signu.R;
import markens.signu.adapters.UserToAddListAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchUserActivity extends AppCompatActivity {

    private Context appCtx;
    private Context myCtx;

    private Token token;
    private UserExt userExt;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        myCtx = this;

        setContentView(R.layout.activity_user_search);

        // Get myToken
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        token = spc.getToken();

        final Button button_signup = (Button) findViewById(R.id.buttonSearchUser);
        button_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchUsers();
            }
        });

    }

    private void searchUsers(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_HEROKU"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + token.getAccessToken();

        final EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        Call<SSResponse> call =  sss.searchUsers(auth, editTextEmail.getText().toString());
        call.enqueue(new Callback<SSResponse>(){

            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutUserSearch);
                if(response.isSuccessful()){
                    // Call to adapter
                    ListView list = (ListView) myLayout.findViewById(R.id.listViewUsers);
                    List<User> listUser = response.body().getData().getUsers();
                    list.setAdapter(new UserToAddListAdapter(myCtx, listUser));
                } else {
                    Snackbar snackbar = Snackbar.make(myLayout, "Bad request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.layoutUserSearch);
                Snackbar snackbar = Snackbar.make(myLayout, "Somthing went wrong", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }


}
