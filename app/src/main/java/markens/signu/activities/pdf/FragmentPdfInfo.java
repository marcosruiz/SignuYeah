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
import android.support.v4.app.FragmentManager;
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
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;

import markens.signu.storage.StorageCtrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentPdfInfo extends android.support.v4.app.Fragment {

    Context appCtx;
    Context myCtx;
    Activity myActivity;

    PdfExt pdfExt;
    UserExt myUserExt;
    Token myToken;
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

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        myUserExt = spc.getUserExt();
        myToken = spc.getToken();
        // PdfExt myPdfExt = myUserExt.getPdfsOwned().get(index);

        // Get text view
        TextView pdfId = (TextView) view.findViewById(R.id.textViewPdfIdValue);
        final TextView pdfOriginalName = (TextView) view.findViewById(R.id.textViewOriginalNameValue);
        TextView pdfOwnerEmail = (TextView) view.findViewById(R.id.textViewOwnerEmailValue);
        TextView pdfOwnerName = (TextView) view.findViewById(R.id.textViewOwnerNameValue);
        TextView pdfOwnerLastname = (TextView) view.findViewById(R.id.textViewOwnerLastnameValue);
        ListView signersList = (ListView) view.findViewById(R.id.listSigners);

        pdfId.setText(pdfExt.getId());
        pdfOriginalName.setText(pdfExt.getOriginalName());
        pdfOwnerEmail.setText(pdfExt.getOwner().getEmail());
        pdfOwnerName.setText(pdfExt.getOwner().getName());
        pdfOwnerLastname.setText(pdfExt.getOwner().getLastname());


        SignerListAdapter sa = new SignerListAdapter(myCtx, pdfExt.getSigners());
        signersList.setAdapter(sa);

        // Get button
        final Button buttonDeletePdf = (Button) view.findViewById(R.id.buttonDeletePdf);
        final Button buttonExportPdf = (Button) view.findViewById(R.id.buttonExportPdf);


        if (!(myUserExt.getId().equals(pdfExt.getOwner().getId()))) {
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
                    File fileExternalStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "exported_" + pdfExt.getOriginalName());

                    try {
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfInfo);
                        StorageCtrl.copy(fileInternalStorage, fileExternalStorage);
                        Snackbar.make(layoutPdf, R.string.check_downloads, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfInfo);
                        Snackbar.make(layoutPdf, R.string.export_exception, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    }
                }
            }
        });

        buttonDeletePdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(spc.get("URL_SERVER"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final SignuServerService sss = retrofit.create(SignuServerService.class);
                String auth = "Bearer " + myToken.getAccessToken();
                Call<SSResponse> call = sss.deletePdf(auth, pdfExt.getId());
                call.enqueue(new Callback<SSResponse>() {
                    @Override
                    public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfInfo);
                        if (response.isSuccessful()) {
                            FragmentManager fm = getFragmentManager();
                            new SignuServerServiceCtrl(appCtx, fm).updateUserExt();
                            String fileRoute = getActivity().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + pdfExt.getFileName() + ".pdf";
                            File file = new File(fileRoute);
                            StorageCtrl.delete(file);
                            Snackbar.make(layoutPdf, response.body().getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        } else {
                            Snackbar.make(layoutPdf, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<SSResponse> call, Throwable t) {
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfInfo);
                        Snackbar.make(layoutPdf, R.string.exception, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    }
                });
            }
        });


        return view;
    }


}
