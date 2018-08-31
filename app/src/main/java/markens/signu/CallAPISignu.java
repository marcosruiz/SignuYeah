package markens.signu;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is an api to do ¿all? POST and GET to API of Signu
 */
public class CallAPISignu extends AsyncTask<JSONObject, String, String> {

    private final String TAG = "CALL_API_SIGNU";
    private Context context;
    private String URI;
    private String METHOD;
    private Context ctxOrigin;
    private Callback callback;

    private Map <String, String> reqHeaders = new HashMap<String, String>();
    private Map <String, List<String>> resHeaders = new HashMap<String, List<String>>();
    JSONObject jResult;

    public CallAPISignu(Context ctxOrigin, String uriString, String method){
        //set context variables if required
        //For VM: 10.0.2.2
        //For genymotion: 10.0.3.2
        // Example to signup: http://10.0.3.2:3000/api/users/signup
        this.URI = uriString;
        this.ctxOrigin = ctxOrigin;
        callback = (Callback) ctxOrigin;
        this.METHOD = method;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(JSONObject... params) {
        Log.d("CallAPISignu", "doInBackground");
        String response = "";

        DataOutputStream printout;
        try {
            JSONObject jsonParam = params[0];

            URL url = new URL(URI);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
//            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "application/json");
//            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod(METHOD);

            // Write
            OutputStream os = urlConnection.getOutputStream();
            OutputStreamWriter wr= new OutputStreamWriter(os);
            wr.write(jsonParam.toString());
            wr.flush();


            // Read
            StringBuilder sb = new StringBuilder();
            int HttpResult;
            try{ // It is needed when 401 error server
                HttpResult = urlConnection.getResponseCode();
            } catch(IOException e){
                HttpResult = urlConnection.getResponseCode();
            }
            InputStream is;
            if(HttpResult ==  HttpURLConnection.HTTP_OK){
                is = urlConnection.getInputStream();
            } else{
                is = urlConnection.getErrorStream();
            }

            // Saving response headers (cookies)
            resHeaders = urlConnection.getHeaderFields();
            StorageController sc = new StorageController();
            sc.saveMap("resHeaders.data", resHeaders);

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            response = sb.toString();
            urlConnection.disconnect();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    /**
     * Shows a textbox with info in the UI
     * @param result
     */
    @Override
    protected void onPostExecute(String result){
        try {
            jResult = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(callback!=null){
            callback.callback(jResult);
        }
    }
}
