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

        TextView userId = (TextView) getActivity().findViewById(R.id.textViewIdValue);
        TextView userEmail = (TextView) getActivity().findViewById(R.id.textViewEmailValue);
        TextView userName = (TextView) getActivity().findViewById(R.id.textViewNameValue);
        TextView userLastname = (TextView) getActivity().findViewById(R.id.textViewLastnameValue);
        TextView userCD = (TextView) getActivity().findViewById(R.id.textViewCAValue);
        TextView userLED = (TextView) getActivity().findViewById(R.id.textViewLastEditionDateValue);

        userId.setText(myUserExt.getId());
        userEmail.setText(myUserExt.getEmail());
        userName.setText(myUserExt.getName());
        userLastname.setText(myUserExt.getLastname());
        userCD.setText(myUserExt.getCreationDate());
        userLED.setText(myUserExt.getLastEditionDate());
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
        TextView userId = (TextView) view.findViewById(R.id.textViewIdValue);
        TextView userEmail = (TextView) view.findViewById(R.id.textViewEmailValue);
        TextView userName = (TextView) view.findViewById(R.id.textViewNameValue);
        TextView userLastname = (TextView) view.findViewById(R.id.textViewLastnameValue);
        TextView userCD = (TextView) view.findViewById(R.id.textViewCAValue);
        TextView userLED = (TextView) view.findViewById(R.id.textViewLastEditionDateValue);

        userId.setText(myUserExt.getId());
        userEmail.setText(myUserExt.getEmail());
        userName.setText(myUserExt.getName());
        userLastname.setText(myUserExt.getLastname());
        userCD.setText(myUserExt.getCreationDate());
        userLED.setText(myUserExt.getLastEditionDate());

        return view;
    }


}
