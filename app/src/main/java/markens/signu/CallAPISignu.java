package markens.signu;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;

public class CallAPISignu extends AsyncTask<String, String, String> {

    private final String TAG = "post json example";
    private Context context;

    public CallAPISignu(){
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
        Log.d("CallAPISignu", "doInBackground");
        String response = "";
        //For VM: 10.0.2.2
        //For genymotion: 10.0.3.2
        String urlString = "http://10.0.3.2:3000/api/users/signup";
        String data = "{\"email\": \"sobrenombre@gmail.com\",\"name\": \"marcos\",\"lastname\": \"ruiz\"}"; //data to post


        DataOutputStream printout;
        try {
//            JSONObject jsonParam = new JSONObject();
//            jsonParam.put("email","sobrenombre@gmail.com");
//            jsonParam.put("name","fdas");
//            jsonParam.put("lastname","fds");


            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            //urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("POST");

            OutputStream output = urlConnection.getOutputStream();
            output.write(data.getBytes("UTF-8"));
//            printout = new DataOutputStream(output);
//            printout.writeBytes(URLEncoder.encode(jsonParam.toString(),"UTF-8"));
//            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(output, "UTF-8"));
//            writer.write(data);
//            printout.flush();
//            printout.close();
//            out.close();
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpsURLConnection.HTTP_OK){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            } else{
                response="";
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    public String performPostCall(String requestURL,
                                  HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");

            Log.e(TAG, "11 - url : " + requestURL);

            /*
             * JSON
             */

            JSONObject root = new JSONObject();

            Log.e(TAG, "12 - root : " + root.toString());

            String str = root.toString();
            byte[] outputBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);

            int responseCode = conn.getResponseCode();

            Log.e(TAG, "13 - responseCode : " + responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "14 - HTTP_OK");

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                Log.e(TAG, "14 - False - HTTP_OK");
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
