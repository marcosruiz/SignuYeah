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
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;

public class FragmentPdfOwnedList extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_list, container, false);
        ListView list = (ListView) view.findViewById(R.id.pdf_list);
        // Get data
        Context myCtx = getContext();
        Context appCtx = myCtx.getApplicationContext();
        SharedPrefsCtrl spc = new SharedPrefsCtrl(appCtx);
        UserExt myUserExt = spc.getUserExt();
        list.setAdapter(new PdfsExtListAdapter(getContext(), myUserExt.getPdfsToSign()));

        return view;
    }
}
