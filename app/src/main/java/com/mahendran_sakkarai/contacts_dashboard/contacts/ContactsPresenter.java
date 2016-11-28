package com.mahendran_sakkarai.contacts_dashboard.contacts;

import com.mahendran_sakkarai.contacts_dashboard.data.DataContract;
import com.mahendran_sakkarai.contacts_dashboard.data.DataSource;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.List;

public class ContactsPresenter implements ContactsContract.Presenter{
    private final ContactsContract.View mViewInstance;
    private final DataSource mDataSource;

    public ContactsPresenter(ContactsContract.View contactsView, DataSource dataSource) {
        this.mViewInstance = contactsView;
        this.mDataSource = dataSource;

        mViewInstance.setPresenter(this);
    }

    @Override
    public void start() {
        if (mDataSource != null && mViewInstance != null) {
            mViewInstance.showLoadingData();
            mDataSource.loadCallLogs(new DataContract.LoadCallLogs() {
                @Override
                public void onLoad(List<MCallLog> callLogList) {
                    mViewInstance.showCallLogs(callLogList);
                }

                @Override
                public void onDataNotLoaded() {
                    mViewInstance.showNoDataAvailable();
                }
            });
        }
    }
}
