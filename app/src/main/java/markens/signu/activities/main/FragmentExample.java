package markens.signu.activities.main;

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

public class FragmentExample extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_edit, container, false);
        return view;
    }
}
