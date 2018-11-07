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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.List;

import markens.signu.R;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class FragmentPdfContainer extends android.support.v4.app.Fragment {
    Context appCtx;
    ListView list;
    Context activityCtx;

    private UserExt myUserExt;
    private Token myToken;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pdf_container, container, false);

        appCtx = getActivity().getApplicationContext();
        activityCtx = getActivity();


        // Get token from Shared preferences
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myToken = spc.getToken();


        // Bottom navigation
        setUpBottomNav();

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentPdfOwnedList()).commit();
        return view;
    }

    private void setUpBottomNav() {
        AHBottomNavigation bottomNav = (AHBottomNavigation) view.findViewById(R.id.bottom_navigation);
        bottomNav.setColored(true);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.pdfs_owned, R.drawable.ic_home_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.pdfs_to_sign, R.drawable.ic_timer_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.pdfs_signed, R.drawable.ic_done_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.upload_pdf, R.drawable.ic_file_upload_black_24dp, R.color.colorAccent);
        bottomNav.addItem(item1);
        bottomNav.addItem(item2);
        bottomNav.addItem(item3);
        bottomNav.addItem(item4);

        //Add notifications
        uploadNotifications();

        bottomNav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment selectedFragment = null;
                if (position == 0) {
                    selectedFragment = new FragmentPdfOwnedList();
                } else if (position == 1) {
                    selectedFragment = new FragmentPdfToSignList();
                } else if (position == 2) {
                    selectedFragment = new FragmentPdfSignedList();
                } else if (position == 3) {
                    selectedFragment = new FragmentPdfUpload();
                }
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
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

    public void uploadNotifications() {
        AHBottomNavigation bottomNav = (AHBottomNavigation) view.findViewById(R.id.bottom_navigation);
        List<Boolean> listNotOwned = spc.getListBoolean("LIST_PDF_NOTIFICATION_OWNED");
        List<Boolean> listNotToSign = spc.getListBoolean("LIST_PDF_NOTIFICATION_TO_SIGN");
        List<Boolean> listNotSigned = spc.getListBoolean("LIST_PDF_NOTIFICATION_SIGNED");
        if (listNotOwned != null && listNotSigned != null && listNotSigned != null) {
            int countOwned = countTrue(listNotOwned);
            int countToSign = countTrue(listNotToSign);
            int countSigned = countTrue(listNotSigned);

            if(countOwned == 0){
                bottomNav.setNotification("", 0);
            } else {
                bottomNav.setNotification("" + countOwned, 0);
            }
            if(countToSign == 0){
                bottomNav.setNotification("", 1);
            } else {
                bottomNav.setNotification("" + countToSign, 1);
            }
            if(countSigned == 0){
                bottomNav.setNotification("", 2);
            } else {
                bottomNav.setNotification("" + countSigned, 2);
            }
        }
    }

    private int countTrue(List<Boolean> list) {
        int i = 0;
        for (Boolean b : list) {
            if (b) {
                i++;
            }
        }
        return i;
    }
}
