package markens.signu.activities.main;

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
import markens.signu.adapters.PdfsExtListAdapter;
import markens.signu.adapters.UserListAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchUserActivity extends AppCompatActivity {

    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    private RelativeLayout myLayout;
    private Context appCtx;
    private Context myCtx;

    private Token token;
    private UserExt userExt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        myCtx = this;

        setContentView(R.layout.activity_user_search);
        myLayout = (RelativeLayout) findViewById(R.id.layoutUserSearch);

        // Get token
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
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
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + token.getAccessToken();

        final EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        Call<SSResponse> call =  sss.searchUsers(auth, editTextEmail.getText().toString());
        call.enqueue(new Callback<SSResponse>(){

            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if(response.isSuccessful()){

                    // Call to adapter
                    ListView list = (ListView) myLayout.findViewById(R.id.listViewUsers);
                    List<User> listUser = response.body().getData().getUsers();
                    list.setAdapter(new UserListAdapter(myCtx, listUser));

                } else {
                    Snackbar snackbar = Snackbar.make(myLayout, "Bad request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(myLayout, "Somthing went wrong", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }


}
