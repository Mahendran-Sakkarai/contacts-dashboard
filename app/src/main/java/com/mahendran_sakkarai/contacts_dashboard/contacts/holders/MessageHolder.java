package com.mahendran_sakkarai.contacts_dashboard.contacts.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahendran_sakkarai.contacts_dashboard.R;

public class MessageHolder extends RecyclerView.ViewHolder {
    private final TextView mMessageView;
    private final ProgressBar mProgressView;

    public MessageHolder(View view) {
        super(view);
        mMessageView = (TextView) view.findViewById(R.id.message);
        mProgressView = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

    public void bindData(String message) {
        mMessageView.setText(message);
        if (message.equals(mMessageView.getContext().getString(R.string.loading_data))) {
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
        }
    }
}
