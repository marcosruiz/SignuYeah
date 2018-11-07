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
import markens.signu.adapters.PdfsExtListAdapter;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class FragmentPdfSignedList extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_list, container, false);
        ListView list = (ListView) view.findViewById(R.id.pdf_list);
        // Get data
        Context myCtx = getContext();
        Context appCtx = myCtx.getApplicationContext();
        SharedPrefsGeneralCtrl spgc = new SharedPrefsGeneralCtrl(appCtx);
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        UserExt myUserExt = spc.getUserExt();
        List<Boolean> listNot = spc.getListBoolean("LIST_PDF_NOTIFICATION_SIGNED");
        list.setAdapter(new PdfsExtListAdapter(getContext(), myUserExt.getPdfsSigned(), listNot, "LIST_PDF_NOTIFICATION_SIGNED"));

        return view;
    }
}
