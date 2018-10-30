package markens.signu.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
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
import markens.signu.objects.ext.PdfExt;
import markens.signu.storage.GenericFileProvider;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.activities.PdfVisorActivity;
import markens.signu.api.SignuServerService;
import markens.signu.objects.Token;
import markens.signu.objects.ext.SignerExt;
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
        TextView pdfId = (TextView) view.findViewById(R.id.textViewIdValue);
        TextView pdfOwner = (TextView) view.findViewById(R.id.textViewOwnerValue);
        TextView pdfSigners = (TextView) view.findViewById(R.id.textViewSignersValue);
        Button buttonDetails = (Button) view.findViewById(R.id.buttonInfoPdf);

        pdfName.setText(currentPdfExt.getOriginalName());
        pdfOwner.setText(currentPdfExt.getOwnerId().getEmail());
        pdfId.setText(currentPdfExt.getId());

        String signersStr = "";
        for (SignerExt s : currentPdfExt.getSigners()) {
            signersStr = signersStr + s.getId().getEmail() + " ";
        }
        pdfSigners.setText(signersStr);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TextView textViewPdfId = (TextView) view.findViewById(R.id.textViewIdValue);
//                TextView textViewPdfPosition = (TextView) view.findViewById(R.id.textViewPositionValue);
//                int pdfPosition = Integer.parseInt(textViewPdfPosition.getText().toString());
                Intent intent = new Intent(myCtx, PdfVisorActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("pdf_ext", pdfExtList.get(position));
                myCtx.startActivity(intent);
            }
        });

        return view;
    }
}
