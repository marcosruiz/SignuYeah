package markens.signu;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marco on 25/04/2017.
 */

public class ListViewPdfLoader extends ListActivity{
    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        //Get data
        StorageController sc = new StorageController();
        JSONObject user = sc.getSavedJSON("myUser.data");

        // Show data pdfs_to_sign
        try {
            JSONArray pdfsToSign = user.getJSONArray("pdfs_to_sign");
            if(pdfsToSign.length()==0){

                //I will mock pdfs
                pdfsToSign.put("5b890186fb7ba53460b6dae4");

                for(int i=0; i < pdfsToSign.length(); i++){
                    String pdfId = pdfsToSign.getString(i);
                    
                }

            }
            else{
                //There are pdfs
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }
}
