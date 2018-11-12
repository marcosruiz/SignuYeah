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
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.api.SignuServerServiceCtrl;
import markens.signu.engine.Signature;
import markens.signu.engine.Signature2;
import markens.signu.exception.NoEmptySignaturesException;
import markens.signu.objects.Pdf;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;
import markens.signu.storage.StorageCtrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    TextView textViewWarning;

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

        textViewWarning = (TextView) view.findViewById(R.id.textViewWarning);

        // Lock pdf and download if is necesary
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_SERVER"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SignuServerService sss = retrofit.create(SignuServerService.class);
        String auth = "Bearer " + token.getAccessToken();
        Call<SSResponse> call = sss.lockPdf(auth, pdfExt.getId());
        call.enqueue(new Callback<SSResponse>() {
            @Override
            public void onResponse(Call<SSResponse> call, Response<SSResponse> response) {
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                if (response.isSuccessful()) {
                    Pdf resPdf = response.body().getData().getPdf();
                    if (!resPdf.getLastEditionDate().equals(pdfExt.getLastEditionDate())) {
                        downloadPdf();
                    }
                    textViewWarning.setText(R.string.pdf_locked);
                } else {
                    String errBody = null;
                    try {
                        errBody = response.errorBody().string();
                        Gson g = new Gson();
                        SSResponse ssRes = g.fromJson(errBody, SSResponse.class);
                        RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(myLayout, ssRes.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    } catch (IOException e) {
                        RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(myLayout, R.string.exception, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                        e.printStackTrace();
                    }
                    textViewWarning.setText(R.string.pdf_already_locked);
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                Snackbar.make(layoutPdf, R.string.server_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action, null).show();
            }
        });

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
                if (radioButtonSelected == null) {
                    // Show snackbar
                    RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                    Snackbar.make(layoutPdf, R.string.select_ks, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                } else {
                    String ksRouteSelected = certs[radioButtonSelected.getId()];

                    EditText editTextPass = (EditText) view.findViewById(R.id.editTextPass);
                    String pass = editTextPass.getText().toString();

                    String pdfSrc = appCtx.getFilesDir().getAbsolutePath() + File.separator + pdfExt.getFileName() + ".pdf";
                    String pdfDst = appCtx.getFilesDir().getAbsolutePath() + File.separator + "signed.pdf";

                    try {
//                        Signature.signWithCrl(ksRouteSelected, pdfSrc, pdfDst, pass, "https://signu-tsa.herokuapp.com/tsr", "https://signu-ca.herokuapp.com/ca.crl");
                        try {
                            Signature2.signEmptyField(ksRouteSelected, pdfSrc, pdfDst, pass.toCharArray(), "https://signu-tsa.herokuapp.com/tsr", "https://signu-ca.herokuapp.com/ca.crl");
                            updateSignedFile(pdfDst);
                            RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                            Snackbar.make(layoutPdf, R.string.pdf_signed, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        } catch (NoEmptySignaturesException e) {
                            e.printStackTrace();
                        }
                        String externalDst = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "exported_" + pdfExt.getOriginalName();
                        StorageCtrl.copy(new File(pdfDst), new File(externalDst));


                    } catch (IOException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                        RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                        Snackbar.make(layoutPdf, e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.action, null).show();
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
                            .setAction(R.string.action, null).show();
                } else {
                    Snackbar.make(layoutPdf, R.string.response_no_successful, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                Snackbar.make(layoutPdf, R.string.server_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action, null).show();
            }
        });
    }

    private void downloadPdf() {
        StorageCtrl sPdfCtrl = new StorageCtrl(appCtx);
        String pdfName = pdfExt.getFileName() + ".pdf";
        if (!sPdfCtrl.itExists(pdfName)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(spc.get("URL_SERVER"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SignuServerService sss = retrofit.create(SignuServerService.class);
            Token myToken = spc.getToken();
            String auth = "Bearer " + myToken.getAccessToken();

            Call<ResponseBody> call = sss.downloadPdf(auth, pdfExt.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        ResponseBody rb = response.body();
                        StorageCtrl sPdfC = new StorageCtrl(myCtx);
                        String fileName = pdfExt.getFileName() + ".pdf";
                        boolean isOk = sPdfC.writeResponseBodyToDisk(rb, fileName);
                    } else {
                        String errBody = null;
                        try {
                            errBody = response.errorBody().string();
                            Gson g = new Gson();
                            SSResponse ssRes = g.fromJson(errBody, SSResponse.class);
                            RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                            Snackbar.make(myLayout, ssRes.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                        } catch (IOException e) {
                            RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                            Snackbar.make(myLayout, R.string.exception, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action, null).show();
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    RelativeLayout myLayout = (RelativeLayout) getActivity().findViewById(R.id.fragmentPdfSign);
                    Snackbar.make(myLayout, R.string.server_error, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                }
            });
        }
    }
}
