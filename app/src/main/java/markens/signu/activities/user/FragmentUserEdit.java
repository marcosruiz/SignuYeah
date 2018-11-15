package markens.signu.activities.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import markens.signu.R;
import markens.signu.activities.LoginActivity;
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentUserEdit extends android.support.v4.app.Fragment {

    private static final int MIN_LENGTH = 4;
    Context appCtx;
    Context myCtx;
    Activity myActivity;

    private SharedPrefsCtrl spc;
    RelativeLayout myLayout;

    UserExt myUserExt;
    Token token;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_edit, container, false);

        appCtx = getContext().getApplicationContext();
        myCtx = getContext();
        myActivity = getActivity();
        myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentUserEdit);

        // Get myUserExt

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        myUserExt = spc.getUserExt();
        token = spc.getToken();

        // Get EditText
        final EditText editTextUserNewEmail = (EditText) view.findViewById(R.id.editTextNewEmail);
        final EditText editTextUserNewEmail2 = (EditText) view.findViewById(R.id.editTextNewEmail2);
        final EditText editTextUserNewName = (EditText) view.findViewById(R.id.editTextNewName);
        final EditText editTextUserNewLastname = (EditText) view.findViewById(R.id.editTextNewLastname);
        final EditText editTextUserNewPassword = (EditText) view.findViewById(R.id.editTextNewPassword);
        final EditText editTextUserNewPassword2 = (EditText) view.findViewById(R.id.editTextNewPassword2);
        final EditText editTextPasswordToDelete = (EditText) view.findViewById(R.id.editTextPasswordToDelete);

        // Buttons
        final Button buttonNewEmail = (Button) view.findViewById(R.id.buttonNewEmail);
        final Button buttonNewPassword = (Button) view.findViewById(R.id.buttonNewPassword);
        final Button buttonNewName = (Button) view.findViewById(R.id.buttonNewName);
        final Button buttonNewLastname = (Button) view.findViewById(R.id.buttonNewLastname);
        final Button buttonDeleteUser = (Button) view.findViewById(R.id.buttonDeleteUser);

        buttonNewEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentUserEdit);

                String email = editTextUserNewEmail.getText().toString();
                String email2 = editTextUserNewEmail2.getText().toString();

                if (email != null && email.equals(email2) && email.length() > MIN_LENGTH) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(spc.get("URL_SERVER"))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final SignuServerService sss = retrofit.create(SignuServerService.class);
                    String auth = "Bearer " + token.getAccessToken();
                    Call<SSResponse> call = sss.editUserEmail(auth, email);
                    call.enqueue(new Callback<SSResponse>() {

                        @Override
                        public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(myLayout, response.body().getMessage(), Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            } else {
                                Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SSResponse> call, Throwable t) {
                            Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        }
                    });
                } else {
                    Snackbar.make(myLayout, R.string.dialog_data_not_valid, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }

            }
        });

        buttonNewPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentUserEdit);
                String password = editTextUserNewPassword.getText().toString();
                String password2 = editTextUserNewPassword2.getText().toString();

                if (password != null && password.equals(password2) && password.length() > MIN_LENGTH) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(spc.get("URL_SERVER"))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final SignuServerService sss = retrofit.create(SignuServerService.class);
                    String auth = "Bearer " + token.getAccessToken();
                    Call<SSResponse> call = sss.editUserPassword(auth, password);
                    call.enqueue(new Callback<SSResponse>() {

                        @Override
                        public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(myLayout, response.body().getMessage(), Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            } else {
                                Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SSResponse> call, Throwable t) {
                            Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        }
                    });
                } else {
                    Snackbar.make(myLayout, R.string.dialog_insert_data, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }


            }
        });

        buttonNewName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentUserEdit);
                String name = editTextUserNewName.getText().toString();
                if (name != null && name.length() >= MIN_LENGTH) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(spc.get("URL_SERVER"))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final SignuServerService sss = retrofit.create(SignuServerService.class);
                    String auth = "Bearer " + token.getAccessToken();
                    Call<SSResponse> call = sss.editUser(auth, name, null);
                    call.enqueue(new Callback<SSResponse>() {

                        @Override
                        public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(myLayout, response.body().getMessage(), Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                                FragmentManager fm = getFragmentManager();
                                new SignuServerServiceCtrl(appCtx, fm).updateUserExt();
                            } else {
                                Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SSResponse> call, Throwable t) {
                            Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        }
                    });
                } else {
                    Snackbar.make(myLayout, R.string.dialog_insert_data, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }


            }
        });

        buttonNewLastname.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentUserEdit);
                String lastname = editTextUserNewLastname.getText().toString();

                if (lastname != null && lastname.length() >= MIN_LENGTH) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(spc.get("URL_SERVER"))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final SignuServerService sss = retrofit.create(SignuServerService.class);
                    String auth = "Bearer " + token.getAccessToken();
                    Call<SSResponse> call = sss.editUser(auth, null, lastname);
                    call.enqueue(new Callback<SSResponse>() {

                        @Override
                        public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(myLayout, response.body().getMessage(), Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                                FragmentManager fm = getFragmentManager();
                                new SignuServerServiceCtrl(appCtx, fm).updateUserExt();
                            } else {
                                Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SSResponse> call, Throwable t) {
                            Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        }
                    });
                } else {
                    Snackbar.make(myLayout, R.string.dialog_insert_data, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }


            }
        });

        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentUserEdit);
                String passwordToDelete = editTextPasswordToDelete.getText().toString();

                if (passwordToDelete != null && passwordToDelete.length() > 0) {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(spc.get("URL_SERVER"))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final SignuServerService sss = retrofit.create(SignuServerService.class);
                    String auth = "Bearer " + token.getAccessToken();
                    Call<SSResponse> call = sss.deleteUser(auth, passwordToDelete);
                    call.enqueue(new Callback<SSResponse>() {

                        @Override
                        public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(myLayout, response.body().getMessage(), Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();

                                // Go to LoginActivity
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG).setAction(R.string.action, null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SSResponse> call, Throwable t) {
                            Snackbar.make(myLayout, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        }
                    });

                } else {
                    Snackbar.make(myLayout, R.string.dialog_insert_data, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }


            }
        });

        return view;
    }


}
