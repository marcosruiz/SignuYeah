package markens.signu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

import markens.signu.R;

import static android.app.Activity.RESULT_OK;

public class FragmentPdfUpload extends Fragment {

    Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_upload, container, false);

        fragment = this;

        final Button button_signup = (Button) view.findViewById(R.id.buttonSelectPdf);
        button_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withSupportFragment(fragment)
                        .withRequestCode(1)
                        .withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(false) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
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
        }
    }
}
