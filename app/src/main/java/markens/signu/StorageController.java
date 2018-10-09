package markens.signu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class StorageController{

    Context ctxOrigin;

    public StorageController(Context context){
        ctxOrigin = context;
    }

    /**
     * Save a JSON
     * @param fileName
     * @return
     */
    public void saveJSON(String fileName, JSONObject jsonObject){
        try {
            FileOutputStream fos = ctxOrigin.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(jsonObject);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO delete comment
//        boolean everythingOk = false;
//        Writer output = null;
//        File file = new File(MY_PATH + File.pathSeparator + fileName);
//        try {
//            output = new BufferedWriter(new FileWriter(file));
//            output.write(content.toString());
//            output.close();
//            everythingOk = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return everythingOk;
    }


    public JSONObject getSavedJSON(String fileName){
        JSONObject result = null;
        try {
            FileInputStream fis = ctxOrigin.getApplicationContext().openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (JSONObject) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Return a String with all info of the fileName. Null if file does not exist.
     * @param fileName
     * @return
     */
    public String getRawFile(String fileName){
        String result = null;
        try {
            FileInputStream fis = ctxOrigin.getApplicationContext().openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (String) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveMap(String fileName, Map<String, List<String>> list){
        try {
            FileOutputStream fos = ctxOrigin.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getSavedMap(String fileName){
        Map<String, List<String>> result = null;
        try {
            FileInputStream fis = ctxOrigin.getApplicationContext().openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (Map<String, List<String>>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
