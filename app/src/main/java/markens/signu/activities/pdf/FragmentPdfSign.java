package markens.signu.activities.pdf;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

import markens.signu.R;
import markens.signu.api.SignuServerService;
import markens.signu.engine.Signature;
import markens.signu.objects.Pdf;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.SharedPrefsCtrl;
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
    SharedPrefsCtrl spc;
    String[] certs;
    String certPathSelected;



    Token token;

    PdfExt pdfExt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pdf_sign, container, false);
//        ListView list = (ListView) view.findViewById(R.id.pdf_list);
//        // Get data owned pdf_list
//        Bundle bundle = getArguments();
//        List<PdfExt> listPdf = (List<PdfExt>) bundle.getSerializable("list_pdf");
//        list.setAdapter(new PdfsExtListAdapter(getContext(), listPdf));

        myCtx = getContext();
        appCtx = myCtx.getApplicationContext();

        Bundle b = getArguments();
        pdfExt = (PdfExt) b.getSerializable("pdf_ext");

        RelativeLayout myLayout = (RelativeLayout) view.findViewById(R.layout.fragment_pdf_sign);


        spc = new SharedPrefsCtrl(appCtx);
        Set<String> setCerts = spc.getCerts();
        token = spc.getToken();
        certs = setCerts.toArray(new String[setCerts.size()]);

        // Fill RadioGroup
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupCerts);
        int i = 0;
        for (String certPath : setCerts) {
            RadioButton radioButton = new RadioButton(myCtx);
            radioButton.setId(i);
            File file = new File(certPath);
            if (file.getName().equals("personal1.p12")) {
                certPathSelected = certPath;
            }
            radioButton.setText(file.getName());
            radioGroup.addView(radioButton, i);
            i++;
        }

        final Button buttonSignPdf = (Button) view.findViewById(R.id.buttonSignPdf);

        buttonSignPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupCerts);
                EditText editTextPass = (EditText) view.findViewById(R.id.editTextPass);
                String pass = editTextPass.getText().toString();

                String pdfSrc = appCtx.getFilesDir().getAbsolutePath() + File.separator + pdfExt.getFileName() + ".pdf";
                String pdfDst = appCtx.getFilesDir().getAbsolutePath() + File.separator + "signed.pdf";
                try {
                    Signature.signWithCrl(certPathSelected, pdfSrc, pdfDst, pass, "https://signu-tsa.heroku.com/tsr", "https://signu-ca.heroku.com/ca.crl");
                    updateSignedFile(pdfDst);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void updateSignedFile(String pdfRoute) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(spc.get("URL_HEROKU"))
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
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.layoutPdf);
                if (response.isSuccessful()) {
                    Snackbar.make(layoutPdf, "PDF updated", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(layoutPdf, "PDF uploaded", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<SSResponse> call, Throwable t) {
                RelativeLayout layoutPdf = (RelativeLayout) getActivity().findViewById(R.id.layoutPdf);
                Snackbar.make(layoutPdf, "Something went wrong", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
