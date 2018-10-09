package markens.signu;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Callback, LoaderManager.LoaderCallbacks<Object> {
    StorageController sc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get token
        sc = new StorageController(this);
        JSONObject tokenJson = sc.getSavedJSON("myToken.data");
        // Prepare data
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        try {
            headers.put("Authorization", "Bearer " + tokenJson.getString("access_token"));

            CallAPISignu call = new CallAPISignu(MainActivity.this,"https://signu-server.herokuapp.com/api/users/info", "GET", headers);
            JSONObject jsonParam = new JSONObject();
            call.execute(jsonParam);
            //Add items to the list
//        StorageController sc = new StorageController();
//        JSONObject user = sc.getSavedJSON("user.data");

            //Carga datos
            getLoaderManager().initLoader(2,null, this);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callback(JSONObject jsonInfo) {

        try {
            if(jsonInfo.getInt("code") == 0){
                // Save data on myUser.data
                sc.saveJSON("myUser.data", jsonInfo);
                System.out.println(jsonInfo);
                // Load info on screen
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
