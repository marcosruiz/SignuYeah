package markens.signu.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserListAdapter extends BaseAdapter {
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    private static LayoutInflater inflater = null;
    List<User> users;
    Context myCtx;
    Context appCtx;
    View view;

    public UserListAdapter(Context context, List<User> users) {
        this.users = users;
        myCtx = context;
        appCtx = context.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
        TextView userEmail = (TextView) view.findViewById(R.id.textViewUserEmail);
        TextView userName = (TextView) view.findViewById(R.id.textViewUserName);
        TextView userLastname = (TextView) view.findViewById(R.id.textViewUserLastname);
        Button buttonAddUser = (Button) view.findViewById(R.id.buttonAddUser);


        userId.setText(u.getId());
        userEmail.setText(u.getEmail());
        userName.setText(u.getName());
        userLastname.setText(u.getLastname());

        // Check if is already an user related
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        UserExt userExt = spc.getUserExt();
        if (isIdThere(u.getId(), userExt.getUsersRelated())) {
            buttonAddUser.setEnabled(false);
            buttonAddUser.setText("Added");
        }

        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView userId = (TextView) view.findViewById(R.id.textViewUserId);
                addUser(userId.getText().toString());
            }
        });

        return view;
    }

    private void addUser(String userId) {
        // Get token
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        Token token = spc.getToken();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        String auth = "Bearer " + token.getAccessToken();

        Call<SSResponse> call = sss.addRelatedUser(auth, userId);
        call.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(view, "User added", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Button buttonAddUser = (Button) view.findViewById(R.id.buttonAddUser);
                    buttonAddUser.setEnabled(false);
                    buttonAddUser.setText("Added");
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Somthing went wrong", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(view, "Somthing went wrong", Snackbar.LENGTH_LONG);
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
