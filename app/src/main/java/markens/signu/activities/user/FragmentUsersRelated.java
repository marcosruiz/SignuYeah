package markens.signu.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import markens.signu.R;
import markens.signu.adapters.UserListAdapter;
import markens.signu.objects.Token;
import markens.signu.objects.User;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;


public class FragmentUsersRelated extends android.support.v4.app.Fragment {

    UserListAdapter userListAdapter;
    UserExt myUserExt;
    Context myCtx;
    Context appCtx;
    View view;

    private SharedPrefsCtrl spc;

    @Override
    public void onResume() {
        super.onResume();

        myUserExt = spc.getUserExt();

        // List users
        List<User> listUsersRelated = myUserExt.getUsersRelated();
        ListView list = (ListView) view.findViewById(R.id.listViewUsersRelated);
        userListAdapter = new UserListAdapter(getContext(), listUsersRelated);
        list.setAdapter(userListAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users_related, container, false);

        // Get data
        myCtx = getContext();
        appCtx = getContext().getApplicationContext();

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());

        // Button
        final Button buttonSelectPdf = (Button) view.findViewById(R.id.buttonAddUsersRelated);
        buttonSelectPdf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchActivityAddUsers();
            }
        });

        return view;
    }

    private void launchActivityAddUsers() {
        Intent intent = new Intent(getActivity(), SearchUserActivity.class);
        startActivity(intent);
    }
}
