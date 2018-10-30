package markens.signu.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import markens.signu.R;
import markens.signu.adapters.SignerListAdapter;
import markens.signu.api.SignuServerService;
import markens.signu.objects.SSResponse;
import markens.signu.objects.Token;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.StoragePdfCtrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PdfSignActivity extends AppCompatActivity {

    Context appCtx;
    Context myCtx;
    Activity myActivity;
    PdfExt pdfExt;
    UserExt myUserExt;
    RelativeLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up data
        super.onCreate(savedInstanceState);
        appCtx = this.getApplicationContext();
        myCtx = this;
        myActivity = this;
        setContentView(R.layout.activity_pdf);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        pdfExt = (PdfExt) b.getSerializable("pdf_ext");
        myLayout = (RelativeLayout) findViewById(R.id.layoutPdf);

        // Get myUserExt
        final SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        myUserExt = spc.getUserExt();
        // PdfExt myPdfExt = myUserExt.getPdfsOwned().get(index);


//        SignerListAdapter sa = new SignerListAdapter(myCtx, pdfExt.getSigners());
//        signersList.setAdapter(sa);


    }

    private void copy(File src, File dst) throws IOException{
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
