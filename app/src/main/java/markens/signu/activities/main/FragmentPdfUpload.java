package markens.signu.activities.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import markens.signu.R;
import markens.signu.adapters.UserListCheckboxAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
import markens.signu.engine.Signature2;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class FragmentPdfUpload extends Fragment {

    Fragment myFragment;
    File fileOriginal;
    File file;
    String originalFileRoute;
    String fileRoute;
    Token token;
    UserExt userExt;

    Context myCtx;
    Context appCtx;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    UserListCheckboxAdapter userListCheckboxAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_upload, container, false);

        myFragment = this;
        myCtx = getContext();
        appCtx = getContext().getApplicationContext();
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        userExt = spc.getUserExt();
        token = spc.getToken();

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
                            .withSupportFragment(myFragment)
                            .withRequestCode(1)
                            .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by fileOriginal name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
            }
        });

        final Button buttonUploadPdf = (Button) view.findViewById(R.id.buttonUploadPdf);
        buttonUploadPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fileOriginal == null) {
                    RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfUpload);
                    Snackbar.make(myLayout, R.string.select_pdf, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    fileRoute = appCtx.getFilesDir().getAbsolutePath() + File.separator + fileOriginal.getName();
                    int qos = userListCheckboxAdapter.getUsersIdSelected().size();
                    try {
                        Signature2.addEmptyFields(originalFileRoute, fileRoute, qos, Signature2.Margin.LEFT, null);
                        file = new File(fileRoute);
                        uploadPdf();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
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
            // Do anything with fileOriginal
            final TextView editTextPathPdf = (TextView) getActivity().findViewById(R.id.textViewPathPdf);
            editTextPathPdf.setText(filePath);

            //Get fileOriginal
            originalFileRoute = filePath;
            fileOriginal = new File(filePath);

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
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);

        //Upload to server
        String auth = "Bearer " + token.getAccessToken();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);

        //Signers
        ArrayList<MultipartBody.Part> signers = new ArrayList<>();
        int i = 0;
        for (String id : userListCheckboxAdapter.getUsersIdSelected()) {
            signers.add(MultipartBody.Part.createFormData("signers[" + i + "]", id));
            i++;
        }

        if (signers.size() == 0) {
            RelativeLayout layoutMain = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfUpload);
            Snackbar.make(layoutMain, R.string.choose_signer, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Call<SSResponse> call = sss.uploadPdfWithSigners(auth, body, signers);
            call.enqueue(new Callback<SSResponse>() {
                @Override
                public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                    RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfUpload);
                    Snackbar.make(myLayout, response.body().getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    if (response.isSuccessful()) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        new SignuServerServiceCtrl(appCtx, fm).updateUserExt();


                    }
                }

                @Override
                public void onFailure(Call<SSResponse> call, Throwable t) {
                    RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfUpload);
                    Snackbar.make(myLayout, R.string.server_error, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }
}
