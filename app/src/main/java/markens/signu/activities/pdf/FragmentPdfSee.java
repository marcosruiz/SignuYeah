package markens.signu.activities.pdf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import markens.signu.R;
import markens.signu.objects.ext.PdfExt;

public class FragmentPdfSee extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_see, container, false);

        Bundle bundle = getArguments();
        PdfExt pdfExt = (PdfExt) bundle.getSerializable("pdf_ext");
        String fileRoute = getActivity().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + pdfExt.getFileName() + ".pdf";
        PDFView pdfView = (PDFView) view.findViewById(R.id.pdfView);
        File file = new File(fileRoute);
        pdfView.fromFile(file).load();

        return view;
    }
}
