package com.mahendran_sakkarai.contacts_dashboard.contacts;

import com.mahendran_sakkarai.contacts_dashboard.data.DataContract;
import com.mahendran_sakkarai.contacts_dashboard.data.DataSource;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsPresenter implements ContactsContract.Presenter {
    private final ContactsContract.View mViewInstance;
    private final DataSource mDataSource;
    private final ContactsContract.ActivityCommunicator mActivityCommunicator;
    private boolean mContactPermission;
    private boolean mCallLogPermission;
    private boolean isStarted = false;

    public ContactsPresenter(ContactsContract.View contactsView, DataSource dataSource,
                             ContactsContract.ActivityCommunicator communicator) {
        this.mViewInstance = contactsView;
        this.mDataSource = dataSource;
        this.mActivityCommunicator = communicator;

        mCallLogPermission = false;
        mContactPermission = false;

        mViewInstance.setPresenter(this);
    }

    @Override
    public void start() {
        if (mCallLogPermission && mContactPermission) {
            loadData();
            isStarted = true;
        }
    }

    @Override
    public void loadData() {
        if (mDataSource != null && mViewInstance != null && isStarted) {
            mViewInstance.showLoadingData();
            mDataSource.loadCallLogs(new DataContract.LoadCallLogs() {
                @Override
                public void onLoad(HashMap<String, MCallLog> callLogList) {
                    List<MCallLog> callLogs = new ArrayList<MCallLog>(callLogList.values());
                    mViewInstance.showCallLogs(callLogs);
                }

                @Override
                public void onDataNotLoaded() {
                    mViewInstance.showNoDataAvailable();
                }
            });
        }
    }

    @Override
    public void callLogPermissionGranted() {
        mCallLogPermission = true;
        if (mContactPermission)
            loadData();
    }

    @Override
    public void callLogPermissionDenied() {
        mCallLogPermission = false;
        mActivityCommunicator.checkCallLogPermission();
    }

    @Override
    public void contactPermissionGranted() {
        mContactPermission = true;
        if (mCallLogPermission)
            loadData();
    }

    @Override
    public void contactPermissionDenied() {
        mContactPermission = false;
        mActivityCommunicator.checkContactPermission();
    }
}
