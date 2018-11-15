package markens.signu.activities.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import markens.signu.R;
import markens.signu.adapters.PdfsExtToSignListAdapter;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;


public class FragmentPdfToSignList extends android.support.v4.app.Fragment {

    PdfsExtToSignListAdapter pdfsExtListAdatper;
    SharedPrefsCtrl spc;
    View view;

    @Override
    public void onResume() {
        super.onResume();
        UserExt myUserExt = spc.getUserExt();

        ListView list = (ListView) view.findViewById(R.id.pdf_list);
        List<Boolean> listNot = spc.getListBooleanUser(getString(R.string.key_list_pdf_not_to_sign));
        pdfsExtListAdatper = new PdfsExtToSignListAdapter(getContext(), myUserExt.getPdfsToSign(), listNot, getString(R.string.key_list_pdf_not_to_sign));
        list.setAdapter(pdfsExtListAdatper);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pdf_list, container, false);

        // Get data
        Context myCtx = getContext();
        Context appCtx = myCtx.getApplicationContext();
        spc = new SharedPrefsCtrl(appCtx, new SharedPrefsCtrl(appCtx).getCurrentUserId());

        return view;
    }
}
