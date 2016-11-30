package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.contacts.holders.CallLogHolder;
import com.mahendran_sakkarai.contacts_dashboard.contacts.holders.MessageHolder;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter{
    public static final int MESSAGE = 1;
    public static final int CALL_LOG = 2;
    private List<Object> mItems = new ArrayList<>();

    public ContactsAdapter() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case MESSAGE:
                viewHolder = new MessageHolder(
                        inflater.inflate(R.layout.message_layout, parent, false));
                break;
            case CALL_LOG:
                viewHolder = new CallLogHolder(
                        inflater.inflate(R.layout.call_log_layout, parent, false));
                break;
            default:
                viewHolder = new MessageHolder(
                        inflater.inflate(R.layout.message_layout, parent, false));
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == MESSAGE) {
            ((MessageHolder) holder).bindData(getItem(position).toString());
        } else if (getItemViewType(position) == CALL_LOG) {
            ((CallLogHolder) holder).bindData((MCallLog)getItem(position));
        } else if (getItemViewType(position) == -1) {
            ((MessageHolder) holder).bindData("Issue on displaying details.");
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof String) {
            return MESSAGE;
        } else if (getItem(position) instanceof MCallLog) {
            return CALL_LOG;
        }

        return -1;
    }

    private Object getItem(int position) {
        return mItems.get(position);
    }

    void showMessage(String message) {
        mItems.clear();

        mItems.add(message);

        notifyDataSetChanged();
    }

    void showData(List<MCallLog> callLogList) {
        mItems.clear();

        for (MCallLog callLog : callLogList) {
            mItems.add(callLog);
        }

        notifyDataSetChanged();
    }
}
