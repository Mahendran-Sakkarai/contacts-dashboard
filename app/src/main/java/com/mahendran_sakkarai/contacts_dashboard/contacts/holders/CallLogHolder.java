package com.mahendran_sakkarai.contacts_dashboard.contacts.holders;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.data.DataSource;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;
import com.mahendran_sakkarai.contacts_dashboard.utils.ActivityUtils;

public class CallLogHolder extends RecyclerView.ViewHolder {
    private final ImageView mContactImage;
    private final TextView mContactName;
    private final TextView mContactNumber;
    private final TextView mContactEMail;
    private final TextView mTotalTalkTime;
    private final TextView mLastSpokenTime;
    private Bitmap mProfilePic;

    public CallLogHolder(View view) {
        super(view);
        mContactImage = (ImageView) view.findViewById(R.id.contact_image);
        mContactName = (TextView) view.findViewById(R.id.contact_name);
        mContactNumber = (TextView) view.findViewById(R.id.contact_number);
        mContactEMail = (TextView) view.findViewById(R.id.contact_e_mail);
        mTotalTalkTime = (TextView) view.findViewById(R.id.total_talk_time);
        mLastSpokenTime = (TextView) view.findViewById(R.id.last_spoken_time);
    }

    public void bindData(MCallLog callLog) {
        if (callLog != null) {
            mContactName.setText(
                    (callLog.getName() != null && callLog.getName().length() > 0)
                            ? callLog.getName() : "Unknown");
            mContactNumber.setText(callLog.getContactNumber());
            if (callLog.geteMail() != null && callLog.geteMail().length() > 0)
                mContactEMail.setText(callLog.geteMail());
            else
                mContactEMail.setText("Not Mentioned");

            if (callLog.getContactId() != null) {
                mProfilePic = DataSource.newInstance(mContactImage.getContext()).
                        getContactImage(callLog.getContactId());
                if (mProfilePic != null)
                    mContactImage.setImageBitmap(mProfilePic);
                else {
                    mContactImage.setImageResource(R.drawable.ic_face_black_24dp);
                }
            } else {
                mContactImage.setImageResource(R.drawable.ic_face_black_24dp);
            }

            mTotalTalkTime.setText(ActivityUtils.convertToTimeFormat(callLog.getTotalTalkTime()));
            mLastSpokenTime.setText(ActivityUtils.convertToDateFormat(callLog.getLastContactTime()));
        }
    }

    public void unsetBitmap() {
        mProfilePic = null;
    }
}
