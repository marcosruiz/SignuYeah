package markens.signu.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
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

public class UserToAddListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    List<User> users;
    Context myCtx;
    Context appCtx;
    View view;

    private SharedPrefsCtrl spc;

    public UserToAddListAdapter(Context context, List<User> users) {
        this.users = users;
        myCtx = context;
        appCtx = context.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User u = users.get(position);
        view = inflater.inflate(R.layout.user_item, null);

        TextView userId = (TextView) view.findViewById(R.id.textViewUserId);
        TextView userName = (TextView) view.findViewById(R.id.textViewUserName);
        TextView userLastname = (TextView) view.findViewById(R.id.textViewUserLastname);
//        Button buttonAddUser = (Button) view.findViewById(R.id.buttonAddUser);
        ImageButton imageButtonAddUser = (ImageButton) view.findViewById(R.id.imageButtonAddUser);
        ImageButton imageButtonDeleteUser = (ImageButton) view.findViewById(R.id.imageButtonDeleteUser);


        userId.setText(u.getId());
        userName.setText(u.getName());
        userLastname.setText(u.getLastname());

        // Check if is already an user related

        UserExt userExt = spc.getUserExt();
        if (isIdThere(u.getId(), userExt.getUsersRelated())) {
            // Is added
            imageButtonAddUser.setVisibility(View.INVISIBLE);
            imageButtonDeleteUser.setVisibility(View.VISIBLE);
        } else {
            imageButtonAddUser.setVisibility(View.VISIBLE);
            imageButtonDeleteUser.setVisibility(View.INVISIBLE);
        }

        imageButtonAddUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView userId = (TextView) view.findViewById(R.id.textViewUserId);
                addUser(userId.getText().toString());
            }
        });

        imageButtonDeleteUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView userId = (TextView) view.findViewById(R.id.textViewUserId);
                deleteUser(userId.getText().toString());
            }
        });

        return view;
    }

    private void deleteUser(String userId) {
        Token token = spc.getToken();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + token.getAccessToken();

        Call<SSResponse> call = sss.deleteRelatedUser(auth, userId);
        call.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(view, R.string.user_added, Snackbar.LENGTH_LONG);
                    snackbar.show();

                    ImageButton imageButtonAddUser = (ImageButton) view.findViewById(R.id.imageButtonAddUser);
                    ImageButton imageButtonDeleteUser = (ImageButton) view.findViewById(R.id.imageButtonDeleteUser);
                    imageButtonAddUser.setVisibility(View.VISIBLE);
                    imageButtonDeleteUser.setVisibility(View.INVISIBLE);

                    // Update user
                    FragmentManager fm = ((FragmentActivity) myCtx).getSupportFragmentManager();
                    new SignuServerServiceCtrl(appCtx, fm).updateUserExt();

                } else {
                    Snackbar snackbar = Snackbar.make(view, R.string.response_no_successful, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(view, R.string.server_error, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }


    private void addUser(String userId) {
        // Get token
        Token token = spc.getToken();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + token.getAccessToken();

        Call<SSResponse> call = sss.addRelatedUser(auth, userId);
        call.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();

                    ImageButton imageButtonAddUser = (ImageButton) view.findViewById(R.id.imageButtonAddUser);
                    ImageButton imageButtonDeleteUser = (ImageButton) view.findViewById(R.id.imageButtonDeleteUser);
                    imageButtonAddUser.setVisibility(View.INVISIBLE);
                    imageButtonDeleteUser.setVisibility(View.VISIBLE);

                    // Update user
                    FragmentManager fm = ((FragmentActivity) myCtx).getSupportFragmentManager();
                    new SignuServerServiceCtrl(myCtx, fm).updateUserExt();

                } else {
                    Snackbar snackbar = Snackbar.make(view, R.string.response_no_successful, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(view, R.string.server_error, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private boolean isIdThere(String id, List<User> listUser) {
        boolean isIn = false;
        for (User user : listUser) {
            if (user.getId().equals(id)) {
                isIn = true;
            }
        }

        return isIn;
    }
}
