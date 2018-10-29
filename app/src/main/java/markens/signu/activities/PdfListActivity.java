package markens.signu.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.MyPdfRecyclerViewAdapter;
import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PdfListActivity extends Activity{
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    private static final String URL_HEROKU = "https://signu-server.herokuapp.com/";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context appCtx;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        setContentView(R.layout.fragment_pdf_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.pdf_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)


        // Get token from Shared preferences
        final SharedPrefsCtrl gSonSM = new SharedPrefsCtrl(appCtx);
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
                    mAdapter = new MyPdfRecyclerViewAdapter(pdfList,null);
                    mRecyclerView.setAdapter(mAdapter);
                } else {

                }

            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {

            }
        });


    }

}
