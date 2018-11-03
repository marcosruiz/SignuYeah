package markens.signu.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.UserExt;

public class SharedPrefsCtrl {
    //private static final String PREFS_NAME = "markens.signu.storage.SharedPrefsCtrl";
    private static final String PREFS_NAME = "";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static String myTokenId = "TOKEN";
    private static String myTokenCreationDateId = "TOKEN_CD";
    private static String myUserId = "USER";
    private static String myUserExtId = "USER_EXT";
    private static String certsId = "CERTS";
    private List<String> certs;

    public SharedPrefsCtrl(Context ctx) {
        if (settings == null) {
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        editor = settings.edit();
    }

    public void store(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void store(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        boolean value = settings.getBoolean(key, false);
        return value;
    }

    public String get(String key) {
        String value = settings.getString(key, null);
        return value;
    }

    public void store(Token token) {
        Gson gson = new Gson();
        String myTokenStr = gson.toJson(token);
        editor.putString(myTokenId, myTokenStr);
        editor.commit();

        Date currentTime = Calendar.getInstance().getTime();
        editor.putLong(myTokenCreationDateId, currentTime.getTime());
        editor.commit();
    }

    public void store(User user) {
        Gson gson = new Gson();
        String tokenStr = gson.toJson(user);
        editor.putString(myUserId, tokenStr);
        editor.commit();
    }

    public Token getToken() {
        Gson gson = new Gson();
        String myTokenStr = settings.getString(myTokenId, "");
        Token token = gson.fromJson(myTokenStr, Token.class);

        long creationDate = settings.getLong(myTokenCreationDateId, 0);
        long expiresIn = 3600;
        if (token != null) {
            expiresIn = token.getExpiresIn();
        }
        long expirationDate = creationDate + (expiresIn * 1000);
        long now = Calendar.getInstance().getTime().getTime();

        //Check if token already expired
        if (now >= expirationDate) {
            token = null;
            store(token);
            store((UserExt) null);
        }

        return token;
    }

    public User getUser() {
        Gson gson = new Gson();
        String myUserStr = settings.getString(myUserId, "");
        User user = gson.fromJson(myUserStr, User.class);
        return user;
    }

    public void store(UserExt userExt) {
        Gson gson = new Gson();
        String tokenStr = gson.toJson(userExt);
        editor.putString(myUserExtId, tokenStr);
        editor.commit();
    }

    public UserExt getUserExt() {
        Gson gson = new Gson();
        String myUserExtStr = settings.getString(myUserExtId, "");
        UserExt userExt = gson.fromJson(myUserExtStr, UserExt.class);
        return userExt;
    }

    public Set<String> getCerts() {
        Set<String> hashSet = new HashSet<String>();
        return settings.getStringSet(certsId, hashSet);
    }

    public void storeCert(String routeCert) {
        Set<String> certs = getCerts();
        certs.add(routeCert);
        editor.putStringSet(certsId, certs);
        editor.commit();
    }
}
