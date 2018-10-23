package markens.signu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import markens.signu.R;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;

public class PdfsToSignAdapter extends BaseAdapter {
    // Para instanciar el xml
    private static LayoutInflater inflater = null;
    UserExt userExt = null;

    public PdfsToSignAdapter(Context context, UserExt userExt) {
        this.userExt = userExt;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userExt.getPdfsToSign().size();
    }

    @Override
    public Object getItem(int position) {
        return userExt.getPdfsToSign().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Lo interesante se hace aqui
        final View view = inflater.inflate(R.layout.pdf_item, null);
        TextView pdfName = (TextView) view.findViewById(R.id.textViewNameValue);
        TextView pdfOwner = (TextView) view.findViewById(R.id.textViewOwnerValue);
        TextView pdfSigners = (TextView) view.findViewById(R.id.textViewSignersValue);

        pdfName.setText(userExt.getPdfsToSign().get(position).getOriginalName());
        pdfOwner.setText(userExt.getPdfsToSign().get(position).getOwnerId().getEmail());

        String signersStr = "";
        for (SignerExt s : userExt.getPdfsToSign().get(position).getSigners()) {
            signersStr = signersStr + s.getId().getEmail() + " ";
        }
        pdfSigners.setText(signersStr);

        return view;
    }
}
