package markens.signu.activities.pdf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import markens.signu.R;
import markens.signu.adapters.SignerListAdapter;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import markens.signu.storage.StorageCtrl;

public class FragmentPdfInfo extends android.support.v4.app.Fragment {

    Context appCtx;
    Context myCtx;
    Activity myActivity;

    PdfExt pdfExt;
    UserExt myUserExt;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_info, container, false);
        appCtx = getContext().getApplicationContext();
        myCtx = getContext();
        myActivity = getActivity();

        Bundle b = getArguments();
        pdfExt = (PdfExt) b.getSerializable("pdf_ext");

        // Get myUserExt
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myUserExt = spc.getUserExt();
        // PdfExt myPdfExt = myUserExt.getPdfsOwned().get(index);

        // Get text view
        TextView pdfId = (TextView) view.findViewById(R.id.textViewPdfIdValue);
        TextView pdfOriginalName = (TextView) view.findViewById(R.id.textViewOriginalNameValue);
        TextView pdfOwnerEmail = (TextView) view.findViewById(R.id.textViewOwnerEmailValue);
        TextView pdfOwnerName = (TextView) view.findViewById(R.id.textViewCAValue);
        TextView pdfOwnerLastname = (TextView) view.findViewById(R.id.textViewOwnerLastnameValue);
        ListView signersList = (ListView) view.findViewById(R.id.listSigners);

        pdfId.setText(pdfExt.getId());
        pdfOriginalName.setText(pdfExt.getOriginalName());
        pdfOwnerEmail.setText(pdfExt.getOwnerId().getEmail());
        pdfOwnerName.setText(pdfExt.getOwnerId().getName());
        pdfOwnerLastname.setText(pdfExt.getOwnerId().getLastname());


        SignerListAdapter sa = new SignerListAdapter(myCtx, pdfExt.getSigners());
        signersList.setAdapter(sa);

        // Get button
        final Button buttonDeletePdf = (Button) view.findViewById(R.id.buttonDeletePdf);
        final Button buttonExportPdf = (Button) view.findViewById(R.id.buttonExportPdf);


        if (!(myUserExt.getId().equals(pdfExt.getId()))) {
            buttonDeletePdf.setEnabled(false);
        }


        buttonExportPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {
                    String pdfName = pdfExt.getFileName() + ".pdf";
                    File fileInternalStorage = new File(appCtx.getFilesDir().getAbsolutePath() + File.separator + pdfName);
                    File fileExternalStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + pdfExt.getOriginalName());

                    try {
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.layoutPdf);
                        StorageCtrl.copy(fileInternalStorage, fileExternalStorage);
                        Snackbar.make(layoutPdf, "See your Downloads folder", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.layoutPdf);
                        Snackbar.make(layoutPdf, "Something went wrong", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });

        buttonDeletePdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO
            }
        });


        return view;
    }


}
