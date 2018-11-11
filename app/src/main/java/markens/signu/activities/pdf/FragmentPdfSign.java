package markens.signu.activities.pdf;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
import markens.signu.engine.Signature;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import markens.signu.storage.StorageCtrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentPdfSign extends android.support.v4.app.Fragment {
    Context myCtx;
    Context appCtx;
    View view;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;
    String[] certs;
    Token token;
    PdfExt pdfExt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pdf_sign, container, false);
        myCtx = getContext();
        appCtx = myCtx.getApplicationContext();
        Bundle b = getArguments();
        pdfExt = (PdfExt) b.getSerializable("pdf_ext");
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        Set<String> setCerts = spc.getCerts();
        token = spc.getToken();
        certs = setCerts.toArray(new String[setCerts.size()]);

        // TODO lock pdf
        // TODO download pdf

        // Show pdf
        String fileRoute = getActivity().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + pdfExt.getFileName() + ".pdf";
        PDFView pdfView = (PDFView) view.findViewById(R.id.pdfView);
        File filePdf = new File(fileRoute);
        pdfView.fromFile(filePdf).load();

        // Fill RadioGroup
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupCerts);
        int i = 0;
        for (String certPath : setCerts) {
            RadioButton radioButton = new RadioButton(myCtx);
            radioButton.setId(i);
            File fileCert = new File(certPath);
            radioButton.setText(fileCert.getName());
            radioGroup.addView(radioButton, i);
            i++;
        }

        final Button buttonSignPdf = (Button) view.findViewById(R.id.buttonSignPdf);

        buttonSignPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupCerts);
                RadioButton radioButtonSelected = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
                if(radioButtonSelected==null){
                    // Show snackbar
                    RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                    Snackbar.make(layoutPdf, "You have to select a KS", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else{
                    String certRouteSelected = certs[radioButtonSelected.getId()];

                    EditText editTextPass = (EditText) view.findViewById(R.id.editTextPass);
                    String pass = editTextPass.getText().toString();

                    String pdfSrc = appCtx.getFilesDir().getAbsolutePath() + File.separator + pdfExt.getFileName() + ".pdf";
                    String pdfDst = appCtx.getFilesDir().getAbsolutePath() + File.separator + "signed.pdf";

                    try {
                        Signature.signWithCrl(certRouteSelected, pdfSrc, pdfDst, pass, "https://signu-tsa.herokuapp.com/tsr", "https://signu-ca.herokuapp.com/ca.crl");

//                        String externalDst = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "exported_" + pdfExt.getOriginalName();
//                        StorageCtrl.copy(new File(pdfDst), new File(externalDst));

                        updateSignedFile(pdfDst);

                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, "Pdf signed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });

        return view;
    }

    private void updateSignedFile(String pdfRoute) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);
        File file = new File(pdfRoute);
        // Sign
        String auth = "Bearer " + token.getAccessToken();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);
        MultipartBody.Part led = MultipartBody.Part.createFormData("last_edition_date", pdfExt.getLastEditionDate());
        Call<SSResponse> call2 = sss.signPdf(auth, body, pdfExt.getId(), led);
        call2.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                if (response.isSuccessful()) {
                    FragmentManager fm = getFragmentManager();
                    new SignuServerServiceCtrl(appCtx, fm).updateUserExt();
                    Snackbar.make(layoutPdf, response.body().getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(layoutPdf, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                Snackbar.make(layoutPdf, spc.get("UNKNOWN_ERROR"), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
