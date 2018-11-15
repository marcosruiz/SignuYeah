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

public class SharedPrefsCtrl {
    private static final String PREFS_NAME = "markens.signu.storage.SharePrefsCtrl";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private String myTokenTag;
    private String myTokenCreationDateTag;
    private String myUserTag;
    private String myUserExtTag;
    private String ksTag;
    private String listNotificationOwnedTag;
    private String listNotificationToSignTag;
    private String listNotificationSignedTag;
    private final String currentUserIdTag = "CURRENT_USER_ID";

    /**
     * This SharedPrefsCtrl constructor is for my user info
     *
     * @param ctx
     * @param userId
     */
    public SharedPrefsCtrl(Context ctx, String userId) {
        myTokenTag = userId + "." + "TOKEN";
        myTokenCreationDateTag = userId + "." + "TOKEN_CD";
        myUserTag = userId + "." + "USER";
        myUserExtTag = userId + "." + "USER_EXT";
        ksTag = userId + "." + "KS";
        listNotificationOwnedTag = userId + "." + "LIST_PDF_NOTIFICATION_OWNED";
        listNotificationToSignTag = userId + "." + "LIST_PDF_NOTIFICATION_TO_SIGN";
        listNotificationSignedTag = userId + "." + "LIST_PDF_NOTIFICATION_SIGNED";
        if (settings == null) {
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        editor = settings.edit();
        storeCurrentUserId(userId);
    }

    /**
     * This constructor only allows the use of storeCurrentUserId and getCurrentUserId
     *
     * @param ctx
     */
    public SharedPrefsCtrl(Context ctx) {
        if (settings == null) {
            settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        editor = settings.edit();
    }

    public void storeCurrentUserId(String userId) {
        editor.putString(currentUserIdTag, userId);
        editor.commit();
    }

    public String getCurrentUserId() {
        String value = settings.getString(currentUserIdTag, null);
        return value;
    }
    ////////////////////////////////////////////////////////////////////////////

    public void store(String key, String value) {
        editor.putString(getCurrentUserId() + "." + key, value);
        editor.commit();
    }

    public void store(String key, boolean value) {
        editor.putBoolean(getCurrentUserId() + "." + key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        boolean value = settings.getBoolean(getCurrentUserId() + "." + key, false);
        return value;
    }

    public String get(String key) {
        String value = settings.getString(getCurrentUserId() + "." + key, null);
        return value;
    }

    public void store(Token token) {
        Gson gson = new Gson();
        String myTokenStr = gson.toJson(token);
        editor.putString(myTokenTag, myTokenStr);
        editor.commit();

        Date currentTime = Calendar.getInstance().getTime();
        editor.putLong(myTokenCreationDateTag, currentTime.getTime());
        editor.commit();
    }

    public void store(User user) {
        Gson gson = new Gson();
        String tokenStr = gson.toJson(user);
        editor.putString(myUserTag, tokenStr);
        editor.commit();
    }

    public Token getToken() {
        Gson gson = new Gson();
        String myTokenStr = settings.getString(myTokenTag, null);
        Token token = gson.fromJson(myTokenStr, Token.class);

        long creationDate = settings.getLong(myTokenCreationDateTag, 0);
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
            // store((UserExt) null);
        }

        return token;
    }

    public List<Boolean> getListBooleanUser(String key) {
        Gson gson = new Gson();
        String value = settings.getString(getCurrentUserId() + "." + key, null);
        List<Boolean> list = gson.fromJson(value, List.class);
        return list;
    }

    public void storeListBooleanUser(String key, List<Boolean> value) {
        Gson gson = new Gson();
        String valueStr = gson.toJson(value);
        editor.putString(getCurrentUserId() + "." + key, valueStr);
        editor.commit();
    }

    public List<Boolean> getListBoolean(String key) {
        Gson gson = new Gson();
        String value = settings.getString(key, null);
        List<Boolean> list = gson.fromJson(value, List.class);
        return list;
    }

    public void storeListBoolean(String key, List<Boolean> value) {
        Gson gson = new Gson();
        String valueStr = gson.toJson(value);
        editor.putString(key, valueStr);
        editor.commit();
    }

    public User getUser() {
        Gson gson = new Gson();
        String myUserStr = settings.getString(myUserTag, null);
        User user = gson.fromJson(myUserStr, User.class);
        return user;
    }

    public void store(UserExt userExt) {
        Gson gson = new Gson();
        UserExt userExtOld = getUserExt();
        String userStr = gson.toJson(userExt);
        editor.putString(myUserExtTag, userStr);
        editor.commit();

        List<Boolean> listOwned = null;
        List<Boolean> listToSign = null;
        List<Boolean> listSigned = null;
        if (userExt != null && userExtOld != null) {
            // Update notifications
            listOwned = compareList(userExtOld.getPdfsOwned(), userExt.getPdfsOwned(), getListBoolean(listNotificationOwnedTag));
            listToSign = compareList(userExtOld.getPdfsToSign(), userExt.getPdfsToSign(), getListBoolean(listNotificationToSignTag));
            listSigned = compareList(userExtOld.getPdfsSigned(), userExt.getPdfsSigned(), getListBoolean(listNotificationSignedTag));
        } else if (userExtOld == null) {
            // First login so no notifications
            listOwned = getListOf(userExt.getPdfsOwned().size(), false);
            listToSign = getListOf(userExt.getPdfsToSign().size(), false);
            listSigned = getListOf(userExt.getPdfsSigned().size(), false);
        } else {
            // We want delete info user => keep null
        }

        storeListBoolean(listNotificationOwnedTag, listOwned);
        storeListBoolean(listNotificationToSignTag, listToSign);
        storeListBoolean(listNotificationSignedTag, listSigned);

//        if (listOwned == null) {
//            storeListBooleanUser(listNotificationOwnedTag, getListOfTrue(userExt.getPdfsOwned().size()));
//        } else {
//            storeListBooleanUser(listNotificationOwnedTag, listOwned);
//        }
//        if (listToSign == null) {
//            storeListBooleanUser(listNotificationOwnedTag, getListOfTrue(userExt.getPdfsToSign().size()));
//        } else {
//            storeListBooleanUser(listNotificationToSignTag, listToSign);
//        }
//        if (listSigned == null) {
//            storeListBooleanUser(listNotificationOwnedTag, getListOfTrue(userExt.getPdfsSigned().size()));
//        } else {
//            storeListBooleanUser(listNotificationSignedTag, listSigned);
//        }
    }


    private List<Boolean> getListOf(int size, boolean value) {
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(value);
        }
        return list;
    }

    /**
     * Compare and generate a new list of notifications.
     * Squared cost
     * @param listOld
     * @param listNew
     * @param oldListNotifications
     * @return
     */
    private static List<Boolean> compareList(List<PdfExt> listOld, List<PdfExt> listNew, List<Boolean> oldListNotifications) {
        List<Boolean> auxList = new ArrayList<>();
        if(listOld == null || listNew == null || oldListNotifications == null || listOld.size() != oldListNotifications.size()){
            // We generate a new listNew without notifications (everything false)
            for(PdfExt pdfExt : listNew){
                auxList.add(Boolean.FALSE);
            }
        } else {
            PdfExt pdfNew;
            PdfExt pdfOld;
            int iOld;
            for (int i = 0; i < listNew.size(); i++) {
                pdfNew = listNew.get(i);
                iOld = indexOf(pdfNew, listOld);
                if (iOld < 0) {
                    // It didn't exists before
                    auxList.add(i, Boolean.TRUE);
                } else {
                    pdfOld = listOld.get(iOld);
                    if (pdfNew.equals(pdfOld)) {
                        // It existed and it is equal
                        Boolean b = oldListNotifications.get(iOld);
                        if (b == null) {
                            auxList.add(i, Boolean.TRUE);
                        } else {
                            auxList.add(i, b);
                        }

                    } else {
                        // It existed and it is not equal
                        auxList.add(i, Boolean.TRUE);
                    }
                }
            }
        }
        return auxList;
    }

    private static int indexOf(PdfExt pdf, List<PdfExt> list) {
        int result = -1;
        int i = 0;
        boolean found = false;
        while (i < list.size() && !found) {
            if (pdf.equals(list.get(i))) {
                found = true;
                result = i;
            } else {
                i++;
            }
        }
        return result;
    }

    public UserExt getUserExt() {
        Gson gson = new Gson();
        String myUserExtStr = settings.getString(myUserExtTag, null);
        UserExt userExt = gson.fromJson(myUserExtStr, UserExt.class);
        return userExt;
    }

    public Set<String> getCerts() {
        Set<String> hashSet = new HashSet<String>();
        return settings.getStringSet(ksTag, hashSet);
    }

    public void storeCert(String routeCert) {
        Set<String> certs = getCerts();
        certs.add(routeCert);
        editor.putStringSet(ksTag, certs);
        editor.commit();
    }

    public void deleteCert(String routeCert) {
        Set<String> certs = getCerts();
        certs.remove(routeCert);
        editor.putStringSet(ksTag, certs);
        editor.commit();
    }
}
