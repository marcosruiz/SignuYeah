package markens.signu.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.UserExt;

public class SharedPrefsGeneralCtrl {
    private static String PREFS_NAME = "markens.signu.storage.SharePrefsCtrl";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static final String userIdTag = "USER_ID";

    /**
     * Constructor
     * @param ctx
     */
    public SharedPrefsGeneralCtrl(Context ctx) {
        if (settings == null) {
            // Diferent PREFS_NAME for each user
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        editor = settings.edit();
    }

    public void store(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String get(String key) {
        String value = settings.getString(key, null);
        return value;
    }

    public void storeUserId(String userId){
        editor.putString(userIdTag, userId);
        editor.commit();
    }

    public String getUserId(){
        String value = settings.getString(userIdTag, null);
        return value;
    }
}
