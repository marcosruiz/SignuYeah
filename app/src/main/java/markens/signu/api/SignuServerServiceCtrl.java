package markens.signu.api;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.RelativeLayout;

import markens.signu.R;
import markens.signu.objects.SSResponse;
import markens.signu.storage.SharedPrefsCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignuServerServiceCtrl {
    SharedPrefsCtrl spc;
    public SignuServerServiceCtrl(SharedPrefsCtrl spc){
        this.spc = spc;
    }

    public void updateUserExt() {
        // Update myUserExt
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_HEROKU"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);
        String auth = "Bearer " + spc.getToken().getAccessToken();
        Call<SSResponse> call2 = sss.getUserExt(auth);
        call2.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if (response.isSuccessful()) {
                    spc.store(response.body().getData().getUserExt());
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {

            }
        });
    }
}
