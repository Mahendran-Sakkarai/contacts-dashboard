package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements ContactsContract.View{
    private ContactsContract.Presenter mPresenter;
    private RecyclerView mContactsRecyclerView;
    private ContactsAdapter mContactsRecyclerViewAdapter;

    public static ContactsFragment newInstance(){
        return new ContactsFragment();
    }

    public ContactsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contacts_fragment, container, false);

        mContactsRecyclerView = (RecyclerView) root.findViewById(R.id.contacts_list);
        mContactsRecyclerViewAdapter = new ContactsAdapter();
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mContactsRecyclerViewAdapter.getItemViewType(position)) {
                    case ContactsAdapter.CALL_LOG:
                        return 1;
                    case ContactsAdapter.MESSAGE:
                        return layoutManager.getSpanCount();
                    default:
                        return -1;
                }
            }
        });
        mContactsRecyclerView.setLayoutManager(layoutManager);
        mContactsRecyclerView.setAdapter(mContactsRecyclerViewAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.setStarted(false);
        showLoadingData();
    }

    @Override
    public void setPresenter(ContactsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoadingData() {
        mContactsRecyclerViewAdapter.showMessage(getContext().getString(R.string.loading_data));
    }

    @Override
    public void showCallLogs(List<MCallLog> callLogList) {
        mContactsRecyclerViewAdapter.showData(callLogList);
    }

    @Override
    public void showNoDataAvailable() {
        mContactsRecyclerViewAdapter.showMessage(getContext()
                .getString(R.string.no_data_found));
    }
}
