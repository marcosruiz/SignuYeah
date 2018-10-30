package markens.signu.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import markens.signu.R;
import markens.signu.adapters.UserListCheckboxAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

import static android.app.Activity.RESULT_OK;

public class FragmentPdfUpload extends Fragment {

    Fragment fragment;
    File file;
    Token token;
    UserExt userExt;
    private static final String URL_LOCAL = "http://192.168.1.6:3000/";
    UserListCheckboxAdapter userListCheckboxAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_upload, container, false);

        fragment = this;

        Bundle bundle = getArguments();
        userExt = (UserExt) bundle.getSerializable("user_ext");
        token = (Token) bundle.getSerializable("token");


        // List signers
        ListView list = (ListView) view.findViewById(R.id.listViewUsers);
        List<User> listUsersRelated = userExt.getUsersRelated();
        userListCheckboxAdapter = new UserListCheckboxAdapter(getContext(), listUsersRelated);
        list.setAdapter(userListCheckboxAdapter);


        final Button buttonSelectPdf = (Button) view.findViewById(R.id.buttonSelectPdf);

        buttonSelectPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

                } else {
                    new MaterialFilePicker()
                            .withSupportFragment(fragment)
                            .withRequestCode(1)
                            .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
            }
        });

        final Button buttonUploadPdf = (Button) view.findViewById(R.id.buttonUploadPdf);
        buttonUploadPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (file == null) {
                    RelativeLayout layoutMain = (RelativeLayout) getActivity().findViewById(R.id.layoutMain);
                    Snackbar.make(layoutMain, "You need to select a PDF", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    uploadPdf();
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file
            final TextView editTextPathPdf = (TextView) getActivity().findViewById(R.id.textViewPathPdf);
            editTextPathPdf.setText(filePath);

            //Get file
            file = new File(filePath);

        }
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void uploadPdf() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LOCAL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        //Upload to server
        String auth = "Bearer " + token.getAccessToken();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);

        //Signers
        ArrayList<MultipartBody.Part> signers = new ArrayList<MultipartBody.Part>();
        int i = 0;
        for (String id : userListCheckboxAdapter.getUsersIdSelected()) {
            signers.add(MultipartBody.Part.createFormData("signers[" + i + "]", id));
            i++;
        }

        if (signers.size()==0) {
            Call<SSResponse> call = sss.uploadPdf(auth, body);
            call.enqueue(new Callback<SSResponse>() {
                @Override
                public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                    RelativeLayout layoutMain = (RelativeLayout) getActivity().findViewById(R.id.layoutMain);
                    if (response.isSuccessful()) {
                        Snackbar.make(layoutMain, "PDF uploaded", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        // Flag IS_MODIFIED true
                        SharedPrefsCtrl spc = new SharedPrefsCtrl(getActivity().getApplicationContext());
                        spc.store("IS_MODIFIED", true);
                    } else {
                        Snackbar.make(layoutMain, "PDF not uploaded", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }

                @Override
                public void onFailure(Call<SSResponse> call, Throwable t) {
                    RelativeLayout layoutMain = (RelativeLayout) getActivity().findViewById(R.id.layoutMain);
                    Snackbar.make(layoutMain, "Something went wrong", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });


        } else {
            Call<SSResponse> call = sss.uploadPdfWithSigners(auth, body, signers);
            call.enqueue(new Callback<SSResponse>() {
                @Override
                public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                    RelativeLayout layoutMain = (RelativeLayout) getActivity().findViewById(R.id.layoutMain);
                    if (response.isSuccessful()) {
                        Snackbar.make(layoutMain, "PDF uploaded", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        // Flag IS_MODIFIED true
                        SharedPrefsCtrl spc = new SharedPrefsCtrl(getActivity().getApplicationContext());
                        spc.store("IS_MODIFIED", true);
                    } else {
                        Snackbar.make(layoutMain, "PDF not uploaded", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }

                @Override
                public void onFailure(Call<SSResponse> call, Throwable t) {
                    RelativeLayout layoutMain = (RelativeLayout) getActivity().findViewById(R.id.layoutMain);
                    Snackbar.make(layoutMain, "Something went wrong", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }
}
