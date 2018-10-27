package markens.signu.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import markens.signu.R;
import markens.signu.objects.SSResponse;
import markens.signu.objects.TokenError;
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.GenericFileProvider;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.activities.PdfVisorActivity;
import markens.signu.api.SignuServerService;
import markens.signu.objects.Token;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.StoragePdfCtrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PdfsExtListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context myCtx;
    PdfExt currentPdfExt;
    Context appCtx;
    List<PdfExt> pdfExtList;

    public PdfsExtListAdapter(Context context, List<PdfExt> pdfExtList) {
        this.pdfExtList = pdfExtList;
        myCtx = context;
        appCtx = context.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pdfExtList.size();
    }

    @Override
    public Object getItem(int position) {
        return pdfExtList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(pdfExtList.get(position).getId());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        currentPdfExt = pdfExtList.get(position);
        // Lo interesante se hace aqui
        final View view = inflater.inflate(R.layout.pdf_item, null);
        TextView pdfName = (TextView) view.findViewById(R.id.textViewNameValue);
        TextView pdfOwner = (TextView) view.findViewById(R.id.textViewOwnerValue);
        TextView pdfSigners = (TextView) view.findViewById(R.id.textViewSignersValue);
        Button buttonDetails = (Button) view.findViewById(R.id.buttonInfoPdf);

        pdfName.setText(currentPdfExt.getOriginalName());
        pdfOwner.setText(currentPdfExt.getOwnerId().getEmail());

        String signersStr = "";
        for (SignerExt s : currentPdfExt.getSigners()) {
            signersStr = signersStr + s.getId().getEmail() + " ";
        }
        pdfSigners.setText(signersStr);

        // Add button functionality
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myCtx, PdfVisorActivity.class);
                intent.putExtra("index", position);
                // pdf_to_sign = 0
                intent.putExtra("list", 0);
                myCtx.startActivity(intent);
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPdfExt = pdfExtList.get(position);
                // Check if is downloaded

                // Download pdf and show it
                SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(spc.get("URL_LOCAL"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SignuServerService sss = retrofit.create(SignuServerService.class);
                Token myToken = spc.getToken();
                String auth = "Bearer " + myToken.getAccessToken();

                Call<ResponseBody> call = sss.downloadPdf(auth, currentPdfExt.getId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            ResponseBody rb = response.body();
                            StoragePdfCtrl sPdfC = new StoragePdfCtrl(myCtx);
                            boolean isOk = sPdfC.writeResponseBodyToDisk(rb, currentPdfExt);
                            if (isOk) {
                                File file = new File(appCtx.getFilesDir().getAbsolutePath() + File.separator + currentPdfExt.getId() + ".pdf");
//                                System.out.println(file.getAbsolutePath());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri pdfURI = Uri.parse(GenericFileProvider.CONTENT_URI + currentPdfExt.getId() + ".pdf");
                                intent.setDataAndType(pdfURI, "application/pdf");
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try {
                                    myCtx.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Snackbar.make(view, "Install a PDF reader", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            String errBody = null;
                            try {
                                errBody = response.errorBody().string();
                                Gson g = new Gson();
                                SSResponse ssRes = g.fromJson(errBody, SSResponse.class);
                                Snackbar.make(view, ssRes.getMessage(), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } catch (IOException e) {
                                Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

            }
        });

        return view;
    }
}