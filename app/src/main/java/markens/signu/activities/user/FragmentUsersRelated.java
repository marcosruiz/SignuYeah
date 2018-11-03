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
    Token myToken;

    Context myCtx;
    Context appCtx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_related, container, false);


        // Get data
        myCtx = getContext();
        appCtx = getContext().getApplicationContext();
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        myUserExt = spc.getUserExt();
        myToken = spc.getToken();

        // List users
        ListView list = (ListView) view.findViewById(R.id.listViewUsersRelated);
        List<User> listUsersRelated = myUserExt.getUsersRelated();
        userListAdapter = new UserListAdapter(getContext(), listUsersRelated);
        list.setAdapter(userListAdapter);

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
