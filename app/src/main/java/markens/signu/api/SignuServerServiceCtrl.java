package markens.signu.api;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import markens.signu.activities.main.FragmentPdfContainer;
import markens.signu.objects.SSResponse;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignuServerServiceCtrl {
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;
    Context context;
    FragmentManager fm;

    public SignuServerServiceCtrl(Context context, FragmentManager fm) {

        spgc = new SharedPrefsGeneralCtrl(context);
        spc = new SharedPrefsCtrl(context, spgc.getUserId());
        this.context = context;
        this.fm = fm;
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
                    UserExt ueNew = response.body().getData().getUserExt();
                    UserExt ueOld = spc.getUserExt();
                    spc.store(ueNew);

                    // Update notifications on bottom navigation bar
                    FragmentPdfContainer fragment = (FragmentPdfContainer) fm.findFragmentByTag("selected_fragment_main");
                    fragment.uploadNotifications();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {

            }
        });
    }
}
