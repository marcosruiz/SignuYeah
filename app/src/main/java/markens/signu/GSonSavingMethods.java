package markens.signu;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.UserExt;

public class GSonSavingMethods {
    private static final String PREFS_NAME = "markens.signu.GSonSavingMethods";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static String myTokenId = "TOKEN";
    private static String myUserId = "USER";
    private static String myUserExtId = "USER_EXT";

    public GSonSavingMethods(Context ctx){
        if(settings == null){
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE );
        }
        editor = settings.edit();
    }

    public void store(Token token) {
        Gson gson = new Gson();
        String myTokenStr = gson.toJson(token);
        editor.putString(myTokenId, myTokenStr);
        editor.commit();
    }

    public void store(User user) {
        Gson gson = new Gson();
        String tokenStr = gson.toJson(user);
        editor.putString(myUserId, tokenStr);
        editor.commit();
    }

    public Token getToken(){
        Gson gson = new Gson();
        String myTokenStr = settings.getString(myTokenId, "");
        Token token = gson.fromJson(myTokenStr, Token.class);
        return token;
    }

    public User getUser(){
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

    public UserExt getUserExt(){
        Gson gson = new Gson();
        String myUserStr = settings.getString(myUserId, "");
        UserExt userExt = gson.fromJson(myUserStr, UserExt.class);
        return userExt;
    }
}
