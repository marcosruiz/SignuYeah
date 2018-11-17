package markens.signu.activities.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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
import markens.signu.activities.NavigationActivity;
import markens.signu.activities.user.SearchUserActivity;
import markens.signu.adapters.UserListCheckboxAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
import markens.signu.itext.Signature2;
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

import static android.app.Activity.RESULT_OK;

public class FragmentPdfUpload extends Fragment {

    Fragment fragment;
    File originalFile;
    File file;
    String originalFileRoute;
    String fileRoute;
    Token token;
    UserExt userExt;
    View view;

    Context myCtx;
    Context appCtx;

    private SharedPrefsCtrl spc;

    CoordinatorLayout snackbarLayout;

    UserListCheckboxAdapter userListCheckboxAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pdf_upload, container, false);

        fragment = this;
        myCtx = getContext();
        appCtx = getContext().getApplicationContext();

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        userExt = spc.getUserExt();
        token = spc.getToken();

        snackbarLayout = (CoordinatorLayout) getActivity().findViewById(R.id.placeSnackBar);

        // List signers
        ListView list = (ListView) view.findViewById(R.id.listViewUsers);
        List<User> listUsersRelated = userExt.getUsersRelated();
        userListCheckboxAdapter = new UserListCheckboxAdapter(getContext(), listUsersRelated);
        list.setAdapter(userListCheckboxAdapter);

        // Switches
        final Switch switchAddSigners = (Switch) view.findViewById(R.id.switchAddSigners);
        final Switch switchStamp = (Switch) view.findViewById(R.id.switchStamp);
        final Spinner spinnerMargin = (Spinner) view.findViewById(R.id.spinnerMargin);

        switchAddSigners.setChecked(true);
        switchStamp.setEnabled(false);
        spinnerMargin.setEnabled(false);

        switchAddSigners.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchStamp.setEnabled(false);
                    switchStamp.setChecked(false);
                    spinnerMargin.setEnabled(false);
                } else {
                    switchStamp.setEnabled(true);
                    spinnerMargin.setEnabled(true);
                }
            }
        });

        switchStamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchAddSigners.setChecked(false);
                    spinnerMargin.setEnabled(true);
                } else {
                    spinnerMargin.setEnabled(false);
                }
            }
        });

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
                            .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by originalFile name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
            }
        });

        final Button buttonUploadPdf = (Button) view.findViewById(R.id.buttonUploadPdf);
        buttonUploadPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (originalFile == null) {

                    Snackbar.make(snackbarLayout, R.string.select_pdf, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                } else {
                    fileRoute = appCtx.getFilesDir().getAbsolutePath() + File.separator + originalFile.getName();
                    int qos = userListCheckboxAdapter.getUsersIdSelected().size();
                    if (switchAddSigners.isChecked()) {
                        file = new File(fileRoute);
                        uploadPdf(Boolean.TRUE, Boolean.FALSE);
                    } else {
                        if (switchStamp.isChecked()) {
                            int marginSelectedItemPosition = spinnerMargin.getSelectedItemPosition();
                            Signature2.Margin selectedMargin = Signature2.Margin.TOP;
                            if (marginSelectedItemPosition == 1) {
                                selectedMargin = Signature2.Margin.BOT;
                            } else if (marginSelectedItemPosition == 2) {
                                selectedMargin = Signature2.Margin.LEFT;
                            } else if (marginSelectedItemPosition == 3) {
                                selectedMargin = Signature2.Margin.RIGHT;
                            }
                            try {
                                Signature2.addEmptyFields(originalFileRoute, fileRoute, qos, selectedMargin, null);
                                file = new File(fileRoute);
                                uploadPdf(Boolean.FALSE, Boolean.TRUE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        } else {
                            file = new File(fileRoute);
                            uploadPdf(Boolean.FALSE, Boolean.FALSE);
                        }
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
            // Do anything with originalFile
            final TextView editTextPathPdf = (TextView) getActivity().findViewById(R.id.textViewPathPdf);
            editTextPathPdf.setText(filePath);

            //Get originalFile
            originalFileRoute = filePath;
            originalFile = new File(filePath);

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

    private void uploadPdf(Boolean addSignersEnabled, Boolean withStamp) {
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
            Snackbar.make(snackbarLayout, R.string.choose_signer, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action, null).show();
        } else {
            Call<SSResponse> call = sss.uploadPdfWithSigners(auth, body, signers, addSignersEnabled, withStamp);
            call.enqueue(new Callback<SSResponse>() {
                @Override
                public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {

                    Snackbar.make(snackbarLayout, response.body().getMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();

                    if (response.isSuccessful()) {
                        // Update user
                        FragmentManager fm = ((FragmentActivity) myCtx).getSupportFragmentManager();
                        new SignuServerServiceCtrl(appCtx, fm).updateUserExt();

                        // Notify
                        FragmentPdfContainer f = (FragmentPdfContainer) getFragmentManager().findFragmentByTag("selected_fragment_main");
                        f.onResume();
                    }
                }

                @Override
                public void onFailure(Call<SSResponse> call, Throwable t) {
                    Snackbar.make(snackbarLayout, R.string.server_error, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }
            });
        }
    }
}
