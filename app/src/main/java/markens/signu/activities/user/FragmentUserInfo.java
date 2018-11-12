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
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class FragmentUserInfo extends android.support.v4.app.Fragment {

    Context appCtx;
    Context myCtx;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    UserExt myUserExt;

    @Override
    public void onResume() {
        super.onResume();

        myUserExt = spc.getUserExt();

        TextView textViewUserId = (TextView) getActivity().findViewById(R.id.textViewUserIdValue);
        TextView textViewUserEmail = (TextView) getActivity().findViewById(R.id.textViewUserEmailValue);
        TextView textViewUserName = (TextView) getActivity().findViewById(R.id.textViewUserNameValue);
        TextView textViewUserLastname = (TextView) getActivity().findViewById(R.id.textViewUserLastnameValue);
        TextView textViewUserCD = (TextView) getActivity().findViewById(R.id.textViewUserCDValue);
        TextView textViewUserLED = (TextView) getActivity().findViewById(R.id.textViewUserLastEditionDateValue);

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
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myUserExt = spc.getUserExt();

        // Get text view
        TextView textViewUserId = (TextView) view.findViewById(R.id.textViewUserIdValue);
        TextView textViewUserEmail = (TextView) view.findViewById(R.id.textViewUserEmailValue);
        TextView textViewUserName = (TextView) view.findViewById(R.id.textViewUserNameValue);
        TextView textViewUserLastname = (TextView) view.findViewById(R.id.textViewUserLastnameValue);
        TextView textViewUserCD = (TextView) view.findViewById(R.id.textViewUserCDValue);
        TextView textViewUserLED = (TextView) view.findViewById(R.id.textViewUserLastEditionDateValue);

        textViewUserId.setText(myUserExt.getId());
        textViewUserEmail.setText(myUserExt.getEmail());
        textViewUserName.setText(myUserExt.getName());
        textViewUserLastname.setText(myUserExt.getLastname());
        textViewUserCD.setText(myUserExt.getCreationDate());
        textViewUserLED.setText(myUserExt.getLastEditionDate());

        return view;
    }


}
