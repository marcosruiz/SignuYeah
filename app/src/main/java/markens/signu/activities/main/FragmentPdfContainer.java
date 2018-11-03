package markens.signu.activities.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import markens.signu.R;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;

public class FragmentPdfContainer extends android.support.v4.app.Fragment{
    Context appCtx;
    ListView list;
    Context activityCtx;

    public UserExt myUserExt;
    public Token myToken;
    SharedPrefsCtrl spc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_container, container, false);

        appCtx = getActivity().getApplicationContext();
        activityCtx = getActivity();

        RelativeLayout myLayout = (RelativeLayout) view.findViewById(R.id.fragmentPdfContainer);

        // Get token from Shared preferences
        spc = new SharedPrefsCtrl(appCtx);
        myToken = spc.getToken();

        BottomNavigationView bottomNav = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentPdfOwnedList()).commit();

        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_owned:
                    selectedFragment = new FragmentPdfOwnedList();
                    break;
                case R.id.nav_to_sign:
                    selectedFragment = new FragmentPdfToSignList();
                    break;
                case R.id.nav_signed:
                    selectedFragment = new FragmentPdfSignedList();
                    break;
                case R.id.nav_upload_pdf:
                    selectedFragment = new FragmentPdfUpload();
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
