package markens.signu.activities.user;

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
import android.widget.RelativeLayout;

import markens.signu.R;
import markens.signu.objects.Token;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class FragmentUserContainer extends Fragment{
    Context appCtx;
    Context activityCtx;

    public UserExt myUserExt;
    public Token myToken;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_container, container, false);

        appCtx = getActivity().getApplicationContext();
        activityCtx = getActivity();

        RelativeLayout myLayout = (RelativeLayout) view.findViewById(R.id.fragmentUserContainer);

        // Get myToken from Shared preferences
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myToken = spc.getToken();

        BottomNavigationView bottomNav = (BottomNavigationView) view.findViewById(R.id.bottom_navigation_user);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Default fragment
        getFragmentManager().beginTransaction().replace(R.id.frameLayoutUserContainer, new FragmentUserInfo()).commit();

        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_user_info:
                    selectedFragment = new FragmentUserInfo();
                    break;
                case R.id.nav_user_edit:
                    selectedFragment = new FragmentUserEdit();
                    break;
                case R.id.nav_users_related:
                    selectedFragment = new FragmentUsersRelated();
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.frameLayoutUserContainer, selectedFragment).commit();

            return true;
        }
    };
}
