package markens.signu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import markens.signu.R;
import markens.signu.activities.pdf.PdfActivity;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;
import markens.signu.objects.ext.UserExt;
import markens.signu.storage.SharedPrefsCtrl;
import markens.signu.storage.SharedPrefsGeneralCtrl;

public class PdfsExtToSignListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context myCtx;
    PdfExt currentPdfExt;
    Context appCtx;
    List<PdfExt> pdfExtList;
    UserExt myUserExt;
    List<Boolean> notificationList;
    String tagNotificationList;
    private SharedPrefsGeneralCtrl spgc;
    private SharedPrefsCtrl spc;

    public PdfsExtToSignListAdapter(Context context, List<PdfExt> pdfExtList, List<Boolean> notificationList, String tagNotificationList) {
        this.pdfExtList = pdfExtList;
        myCtx = context;
        appCtx = context.getApplicationContext();
        spgc = new SharedPrefsGeneralCtrl(appCtx);
        spc = new SharedPrefsCtrl(appCtx, spgc.getUserId());
        myUserExt = spc.getUserExt();
        this.notificationList = notificationList;
        this.tagNotificationList = tagNotificationList;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pdfExtList.size();
    }

    @Override
    public Object getItem(int position) {
        return pdfExtList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        currentPdfExt = pdfExtList.get(position);
        // Lo interesante se hace aqui
        final View view = inflater.inflate(R.layout.pdf_item, null);
        TextView pdfName = (TextView) view.findViewById(R.id.textViewNameValue);
        TextView pdfId = (TextView) view.findViewById(R.id.textViewIdValue);
        TextView pdfOwnerName = (TextView) view.findViewById(R.id.textViewOwnerNameValue);
        TextView pdfOwnerLastname = (TextView) view.findViewById(R.id.textViewOwnerLastnameValue);
        TextView pdfSigners = (TextView) view.findViewById(R.id.textViewSignersValue);
        ImageView imageViewStatus = (ImageView) view.findViewById(R.id.imageView2);
        ImageView imageViewNotification = (ImageView) view.findViewById(R.id.imageViewNotification);

        pdfName.setText(currentPdfExt.getOriginalName());
        pdfOwnerName.setText(currentPdfExt.getOwner().getName());
        pdfOwnerLastname.setText(currentPdfExt.getOwner().getLastname());
        pdfId.setText(currentPdfExt.getId());

        String signersStr = "";
        boolean first = true;
        boolean isTotallySigned = true;
        boolean iAmSigner = false;
        boolean isSignedByMe = false;
        // Showing signers
        for (SignerExt s : currentPdfExt.getSigners()) {
            if (first) {
                signersStr = s.getUser().getName() + " " + s.getUser().getLastname();
                first = false;
            } else {
                signersStr = signersStr + ", " + s.getUser().getName() + " " + s.getUser().getLastname();
            }
            if (!s.getIsSigned()) {
                isTotallySigned = false;
            }
            if (s.getUser().getId().equals(myUserExt.getId())) {
                iAmSigner = true;
                isSignedByMe = s.getIsSigned();
            }
        }
        pdfSigners.setText(signersStr);

        if (isTotallySigned) {
            imageViewStatus.setImageResource(R.drawable.ic_done_all_black_24dp);
            imageViewStatus.setVisibility(imageViewStatus.VISIBLE);
        } else {
            if (iAmSigner && isSignedByMe) {
                imageViewStatus.setImageResource(R.drawable.ic_done_black_24dp);
                imageViewStatus.setVisibility(imageViewStatus.VISIBLE);
            } else if (iAmSigner && !isSignedByMe) {
                imageViewStatus.setImageResource(R.drawable.ic_timer_black_24dp);
                imageViewStatus.setVisibility(imageViewStatus.VISIBLE);
            }
        }

        // Notifications
        if (notificationList.get(position)) {
            imageViewNotification.setVisibility(View.VISIBLE);
        } else {
            imageViewNotification.setVisibility(View.INVISIBLE);
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myCtx, PdfActivity.class);
                intent.putExtra("pdf_ext", pdfExtList.get(position));
                myCtx.startActivity(intent);

                notificationList.set(position, false);
                spc.storeListBoolean(tagNotificationList, notificationList);

                // Update view
                ImageView imageViewNotification = (ImageView) v.findViewById(R.id.imageViewNotification);
                imageViewNotification.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }
}
