package markens.signu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import markens.signu.R;
import markens.signu.objects.ext.SignerExt;

public class SignerListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    List<SignerExt> signers;
    Context myCtx;

    public SignerListAdapter(Context context, List<SignerExt> signers){
        this.signers = signers;
        myCtx = context;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return signers.size();
    }

    @Override
    public Object getItem(int position) {
        return signers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SignerExt se = signers.get(position);
        final View view = inflater.inflate(R.layout.signer_item, null);

        TextView signerEmail = (TextView) view.findViewById(R.id.textViewCert);
        TextView signerName = (TextView) view.findViewById(R.id.textViewCertDes);
        TextView signerLastname = (TextView) view.findViewById(R.id.textViewUserLastname);
        ImageView signerImg = (ImageView) view.findViewById(R.id.imageIsSigned);

        signerEmail.setText(se.getId().getEmail());
        signerName.setText(se.getId().getName());
        signerLastname.setText(se.getId().getLastname());
        if(se.getIsSigned()){
            signerImg.setImageResource(R.drawable.ic_done_black_24dp);
        }

        return view;
    }
}
