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

public class FragmentPdfList extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_list, container, false);
        ListView list = (ListView) view.findViewById(R.id.pdf_list);
        // Get data owned pdf_list
        Bundle bundle = getArguments();
        List<PdfExt> listPdf = (List<PdfExt>) bundle.getSerializable("list_pdf");
        list.setAdapter(new PdfsExtListAdapter(getContext(), listPdf));

        return view;
    }
}
