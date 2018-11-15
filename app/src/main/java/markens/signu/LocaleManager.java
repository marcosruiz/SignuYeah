package markens.signu;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import markens.signu.storage.SharedPrefsCtrl;


public class LocaleManager {

    private static final String languageKey = "language";
    private static final String defaultLanguage = "en";

    public static void setLocale(Context c) {
        setNewLocale(c, getLanguage(c));
    }

    public static void setNewLocale(Context c, String language) {
        persistLanguage(c, language);
        updateResources(c, language);
    }

    public static String getLanguage(Context c) {
        SharedPrefsCtrl spgc = new SharedPrefsCtrl(c.getApplicationContext());
        String language = spgc.get(languageKey);
        if (language == null) {
            spgc.store(languageKey, language);
            language = defaultLanguage;
        }
        return language;
    }

    private static void persistLanguage(Context c, String language) {
        SharedPrefsCtrl spgc = new SharedPrefsCtrl(c.getApplicationContext());
        spgc.store(languageKey, language);
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}