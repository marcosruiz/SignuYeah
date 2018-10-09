package markens.signu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<String, String> reqHeaders;
    private Map<String, List<String>> resHeaders = new HashMap<String, List<String>>();
    JSONObject jResult;

    public CallAPISignu(Context ctxOrigin, String uriString, String method, Map<String, String> reqHeaders) {
        //set context variables if required
        //For VM: 10.0.2.2
        //For genymotion: 10.0.3.2
        // Example to signup: http://10.0.3.2:3000/api/users/signup
        this.URI = uriString;
        this.ctxOrigin = ctxOrigin;
        callback = (Callback) ctxOrigin;
        this.METHOD = method;
        this.reqHeaders = reqHeaders;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(JSONObject... params) {
        Log.d("CallAPISignu", "doInBackground");
        String response = "";

        DataOutputStream printout;
        try {
            JSONObject jsonParam = params[0];

            URL url = new URL(URI);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("charset", "utf-8");
//            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
//            urlConnection.setRequestProperty("Content-Type", "application/json");
//            urlConnection.setRequestProperty("Accept", "application/json");

            Set<String> keySet = reqHeaders.keySet();
            for (String key : keySet) {
                conn.setRequestProperty(key, reqHeaders.get(key));
            }
            conn.setRequestMethod(METHOD);


            // Write json
            if (reqHeaders.get("Content-Type") == "application/json") {
                String jsonRequest = jsonParam.toString();
                byte[] postData = jsonRequest.getBytes(StandardCharsets.UTF_8);
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(postData);
                }
            }

            // Write x-www-form-urlencoded
            if (reqHeaders.get("Content-Type") == "application/x-www-form-urlencoded") {
                Iterator<String> bodyKeys = jsonParam.keys();
                String urlParameters = "";
                boolean first = true;
                while (bodyKeys.hasNext()) {
                    try {
                        String key = bodyKeys.next();
                        String value = jsonParam.getString(key);
                        if (!first) {
                            urlParameters = urlParameters + "&";
                        } else {
                            first = false;
                        }
                        urlParameters = urlParameters + key + "=" + value;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(postData);
                }
            }

            // Read
            StringBuilder sb = new StringBuilder();
            int HttpResult;
            try { // It is needed when 401 error server
                HttpResult = conn.getResponseCode();
            } catch (IOException e) {
                HttpResult = conn.getResponseCode();
            }
            InputStream is;
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            // Saving response headers (cookies)
            resHeaders = conn.getHeaderFields();
            StorageController sc = new StorageController(ctxOrigin);
            //sc.saveMap("resHeaders.data", resHeaders); //TODO check this

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            response = sb.toString();
            conn.disconnect();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    /**
     * Shows a textbox with info in the UI
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        try {
            jResult = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.callback(jResult);
        }
    }
}
