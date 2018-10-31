package markens.signu.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import markens.signu.R;
import markens.signu.StorageController;
import markens.signu.adapters.CertListAdapter;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.StorageCtrl;

import static android.app.Activity.RESULT_OK;

public class FragmentCertList extends Fragment {

    Fragment fragment;
    Token token;
    UserExt userExt;

    CertListAdapter certListAdapter;
    Context myCtx;
    Context appCtx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cert, container, false);

        myCtx = getContext();
        appCtx = getContext().getApplicationContext();

        fragment = this;

//        Bundle bundle = getArguments();
//        userExt = (UserExt) bundle.getSerializable("user_ext");
//        token = (Token) bundle.getSerializable("token");


        // List signers
        ListView list = (ListView) view.findViewById(R.id.listViewCerts);
        certListAdapter = new CertListAdapter(getContext());
        list.setAdapter(certListAdapter);


        final Button buttonImportCert = (Button) view.findViewById(R.id.buttonImportCert);

        buttonImportCert.setOnClickListener(new View.OnClickListener() {
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
                            .withFilter(Pattern.compile(".*\\.p12$")) // Filtering files and directories by file name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);

            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            //Get file
            File fileSrc = new File(filePath);
            int i = spc.getCerts().size();
            File fileDest = new File(appCtx.getFilesDir().getAbsolutePath() + File.separator + "personal" + i + ".p12");
            try {
                StorageCtrl.copy(fileSrc, fileDest);
                spc.storeCert(fileDest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
