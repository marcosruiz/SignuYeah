package markens.signu.activities.cert;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import markens.signu.R;
import markens.signu.adapters.KSListAdapter;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import markens.signu.storage.StorageCtrl;

import static android.app.Activity.RESULT_OK;

public class FragmentKSList extends Fragment {

    Fragment fragment;
    Token token;
    UserExt userExt;

    KSListAdapter kSListAdapter;
    Context myCtx;
    Context appCtx;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    File fileSrc;
    File fileDest;

    @Override
    public void onResume() {
        super.onResume();
        kSListAdapter.updateData();
        kSListAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ks, container, false);

        myCtx = getContext();
        appCtx = getContext().getApplicationContext();
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());

        fragment = this;

//        Bundle bundle = getArguments();
//        userExt = (UserExt) bundle.getSerializable("user_ext");
//        token = (Token) bundle.getSerializable("token");


        // List signers
        ListView list = (ListView) view.findViewById(R.id.listViewCerts);
        kSListAdapter = new KSListAdapter(getContext());
        list.setAdapter(kSListAdapter);


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

    /**
     * It is executed when we select a .p12 file
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {


            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            //Get file
            fileSrc = new File(filePath);
            fileDest = new File(appCtx.getFilesDir().getAbsolutePath() + File.separator + fileSrc.getName());
            if (fileDest.exists()) {
                //Show dialog if you want overwrite
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(myCtx, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(myCtx);
                }
                builder.setTitle(R.string.dialog_overwrite_title)
                        .setMessage(R.string.dialog_overwrite)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with import
                                copyCertAndShowSnackbar();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .show();
            } else {
                copyCertAndShowSnackbar();
            }
        }
    }

    private void copyCertAndShowSnackbar(){
        RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentKS);
        try {
            StorageCtrl.copy(fileSrc, fileDest);
            spc.storeCert(fileDest.getAbsolutePath());
            kSListAdapter.updateData();
            kSListAdapter.notifyDataSetChanged();
            Snackbar.make(myLayout, R.string.imported_cert_ok, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } catch (IOException e) {
            Snackbar.make(myLayout, R.string.imported_cert_wrong, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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
