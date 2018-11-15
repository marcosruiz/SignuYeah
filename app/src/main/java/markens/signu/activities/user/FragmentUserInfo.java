package markens.signu.activities.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import markens.signu.R;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;


public class FragmentUserInfo extends android.support.v4.app.Fragment {

    Context appCtx;
    Context myCtx;

    private SharedPrefsCtrl spc;

    UserExt myUserExt;

    TextView textViewUserId;
    TextView textViewUserEmail;
    TextView textViewUserName;
    TextView textViewUserLastname;
    TextView textViewUserCD;
    TextView textViewUserLED;

    @Override
    public void onResume() {
        super.onResume();

        myUserExt = spc.getUserExt();

        textViewUserId.setText(myUserExt.getId());
        textViewUserEmail.setText(myUserExt.getEmail());
        textViewUserName.setText(myUserExt.getName());
        textViewUserLastname.setText(myUserExt.getLastname());
        textViewUserCD.setText(myUserExt.getCreationDate());
        textViewUserLED.setText(myUserExt.getLastEditionDate());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        myCtx = getContext();
        appCtx = getContext().getApplicationContext();

        // Get myUserExt

        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());
        myUserExt = spc.getUserExt();

        // Get text view
        textViewUserId = (TextView) view.findViewById(R.id.textViewUserIdValue);
        textViewUserEmail = (TextView) view.findViewById(R.id.textViewUserEmailValue);
        textViewUserName = (TextView) view.findViewById(R.id.textViewUserNameValue);
        textViewUserLastname = (TextView) view.findViewById(R.id.textViewUserLastnameValue);
        textViewUserCD = (TextView) view.findViewById(R.id.textViewUserCDValue);
        textViewUserLED = (TextView) view.findViewById(R.id.textViewUserLastEditionDateValue);

        textViewUserId.setText(myUserExt.getId());
        textViewUserEmail.setText(myUserExt.getEmail());
        textViewUserName.setText(myUserExt.getName());
        textViewUserLastname.setText(myUserExt.getLastname());
        textViewUserCD.setText(myUserExt.getCreationDate());
        textViewUserLED.setText(myUserExt.getLastEditionDate());

        return view;
    }


}
