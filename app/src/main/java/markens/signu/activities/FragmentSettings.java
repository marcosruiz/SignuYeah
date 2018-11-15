package markens.signu.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import markens.signu.LocaleManager;
import markens.signu.R;
import markens.signu.storage.SharedPrefsCtrl;


public class FragmentSettings extends android.support.v4.app.Fragment {

    Context myCtx;
    Context appCtx;
    Fragment fragment;

    final String URL_SERVER = "URL_SERVER";
    final String URL_TSA = "URL_TSA";
    final String URL_CA = "URL_CA";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        fragment = this;
        myCtx = getContext();
        appCtx = getContext().getApplicationContext();
        final SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());


        final EditText editTextUrlServer = (EditText) view.findViewById(R.id.editTextUrlServer);
        final EditText editTextUrlTSA = (EditText) view.findViewById(R.id.editTextUrlTSA);
        final EditText editTextUrlCA = (EditText) view.findViewById(R.id.editTextUrlCA);
        final RadioGroup radioGroupLanguage = (RadioGroup) view.findViewById(R.id.radioGroupLanguages);
        final Button buttonSaveSettings = (Button) view.findViewById(R.id.buttonSaveSettings);

        if (LocaleManager.getLanguage(appCtx) == "en") {
            radioGroupLanguage.check(R.id.radioButtonEnglish);
        } else if (LocaleManager.getLanguage(appCtx) == "es") {
            radioGroupLanguage.check(R.id.radioButtonSpanish);
        }

        editTextUrlServer.setText(spc.get(URL_SERVER));
        editTextUrlTSA.setText(spc.get(URL_TSA));
        editTextUrlCA.setText(spc.get(URL_CA));

        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String urlServer = editTextUrlServer.getText().toString();
                if (urlServer != null && !urlServer.equals("")) {
                    spc.store(URL_SERVER, urlServer);
                }
                String urlTSA = editTextUrlTSA.getText().toString();
                if (urlTSA != null && !urlTSA.equals("")) {
                    spc.store(URL_TSA, urlServer);
                }
                String urlCA = editTextUrlCA.getText().toString();
                if (urlCA != null && !urlCA.equals("")) {
                    spc.store(URL_CA, urlCA);
                }
                int idSelected = radioGroupLanguage.getCheckedRadioButtonId();
                String language = "en";
                if (idSelected == R.id.radioButtonEnglish) {
                    language = "en";
                    LocaleManager.setNewLocale(appCtx, language);
                } else if (idSelected == R.id.radioButtonSpanish) {
                    language = "es";
                    LocaleManager.setNewLocale(appCtx, language);
                }
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.detach(fragment).attach(fragment).commit();
                ((NavigationActivity) getActivity()).notifyLanguageChange(language);
            }
        });


        return view;
    }
}
