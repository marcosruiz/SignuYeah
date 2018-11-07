package markens.signu.activities.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import markens.signu.R;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class FragmentUserEdit extends android.support.v4.app.Fragment {

    Context appCtx;
    Context myCtx;
    Activity myActivity;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    UserExt myUserExt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_edit, container, false);

        appCtx = getContext().getApplicationContext();
        myCtx = getContext();
        myActivity = getActivity();

        // Get myUserExt
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myUserExt = spc.getUserExt();

        // Get EditText
        EditText userNewEmail = (EditText) view.findViewById(R.id.editTextNewEmail);
        EditText userNewName = (EditText) view.findViewById(R.id.editTextNewName);
        EditText userNewLastname = (EditText) view.findViewById(R.id.editTextNewLastname);
        EditText userNewPassword = (EditText) view.findViewById(R.id.editTextNewPassword);
        EditText userOldPassword = (EditText) view.findViewById(R.id.editTextOldPassword);

        return view;
    }


}
