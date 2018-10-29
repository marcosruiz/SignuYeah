package markens.signu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import markens.signu.PdfFragment.OnListFragmentInteractionListener;
import markens.signu.objects.ext.PdfExt;
import markens.signu.objects.ext.SignerExt;

import java.util.List;

public class MyPdfRecyclerViewAdapter extends RecyclerView.Adapter<MyPdfRecyclerViewAdapter.ViewHolder> {

    private final List<PdfExt> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPdfRecyclerViewAdapter(List<PdfExt> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pdf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.mPdfNameView.setText(mValues.get(position).getOriginalName());
        holder.mPdfOwnerView.setText(mValues.get(position).getOwnerId().getEmail());
        String signersStr = "";
        for (SignerExt s : mValues.get(position).getSigners()) {
            signersStr = signersStr + " " + s.getId().getEmail();
        }
        holder.mPdfSignersView.setText(signersStr);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPdfNameView;
        public final TextView mPdfOwnerView;
        public final TextView mPdfSignersView;
        public PdfExt mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPdfNameView = (TextView) view.findViewById(R.id.pdf_name);
            mPdfOwnerView = (TextView) view.findViewById(R.id.pdf_owner);
            mPdfSignersView = (TextView) view.findViewById(R.id.pdf_signers);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPdfOwnerView.getText() + "'";
        }
    }
}
