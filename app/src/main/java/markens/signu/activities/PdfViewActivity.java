package markens.signu.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import markens.signu.R;
import markens.signu.adapters.SignerListAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.GenericFileProvider;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.StoragePdfCtrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PdfViewActivity extends AppCompatActivity {

    Context appCtx;
    Context myCtx;
    RelativeLayout myLayout;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up data
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        myCtx = this;
        setContentView(R.layout.activity_pdf_view);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
//        file = (File) b.getSerializable("file");
        String fileRoute = b.getString("file_route");
        myLayout = (RelativeLayout) findViewById(R.id.layoutPdfView);
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        file = new File(fileRoute);
        System.out.println(fileRoute);
        pdfView.fromFile(file).load();
    }
}
