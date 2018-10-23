package markens.signu;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import markens.signu.adapters.PdfsToSignAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.UserExt;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    StorageController sc;
    Context appCtx;
    ListView lista;
    Context activityCtx;
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        activityCtx = this;
        setContentView(R.layout.activity_main);

        lista = (ListView) findViewById(R.id.pdf_list);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // Get token from Shared preferences
        final GSonSavingMethods gSonSM = new GSonSavingMethods(appCtx);
        Token myToken = gSonSM.getToken();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + myToken.getAccessToken();
        Call<SSResponse> call = sss.getUserExt(auth);
        Response<SSResponse> response = null;

        call.enqueue(new retrofit2.Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if(response.isSuccessful()){
                    UserExt myUserExt = response.body().getData().getUserExt();
                    //Save myUserExt
                    gSonSM.store(myUserExt);
                    List<PdfExt> pdfList = myUserExt.getPdfsToSign();

                    //UI
                    lista.setAdapter(new PdfsToSignAdapter(activityCtx, myUserExt));

                } else {

                }

            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {

            }
        });



    }
}
