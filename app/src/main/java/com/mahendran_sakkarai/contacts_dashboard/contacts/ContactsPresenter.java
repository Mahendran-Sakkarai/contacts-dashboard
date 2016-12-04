package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.database.Cursor;

import com.mahendran_sakkarai.contacts_dashboard.data.DataContract;
import com.mahendran_sakkarai.contacts_dashboard.data.DataSource;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLogComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ContactsPresenter implements ContactsContract.Presenter {
    private final ContactsContract.View mViewInstance;
    private final DataSource mDataSource;
    private final ContactsContract.ActivityCommunicator mActivityCommunicator;
    private boolean mContactPermission;
    private boolean mCallLogPermission;
    private boolean isStarted = false;
    private List<MCallLog> mCallLogList = new ArrayList<>();

    public ContactsPresenter(ContactsContract.View contactsView, DataSource dataSource,
                             ContactsContract.ActivityCommunicator communicator) {
        this.mViewInstance = contactsView;
        this.mDataSource = dataSource;
        this.mActivityCommunicator = communicator;

        mViewInstance.setPresenter(this);
    }

    @Override
    public void start() {
        isStarted = true;
        if (mCallLogPermission && mContactPermission) {
            loadData();
        }
    }

    @Override
    public void loadData() {
        if (mDataSource != null && mViewInstance != null && isStarted) {
            mViewInstance.showLoadingData();
            mDataSource.loadCallLogs(new DataContract.LoadCallLogs() {
                @Override
                public void onLoad(List<MCallLog> callLogList) {
                    Collections.sort(callLogList, new MCallLogComparator());
                    mCallLogList = callLogList;
                    mViewInstance.showCallLogs(callLogList);
                }

                @Override
                public void onDataNotLoaded(String message) {
                    mViewInstance.showErrorMessage(message);
                }

                @Override
                public void triggerLoadContacts() {
                    mViewInstance.triggerLoadContacts();
                }

                @Override
                public void triggerLoadContactsWithPhoneNumber(ArrayList<String> contactId) {
                    mViewInstance.triggerLoadContactsWithPhoneNumber(contactId);
                }

                @Override
                public void triggerLoadCallLogsByName(ArrayList<String> contactNames) {
                    mViewInstance.triggerLoadCallLogsByName(contactNames);
                }

                @Override
                public void triggerGetEmailFromContactId(ArrayList<String> contactIds) {
                    mViewInstance.triggerGetEmailFromContactId(contactIds);
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
        mViewInstance.showErrorMessage("Call Log Permission is required to run this app! Could you please enable that in the settings!");
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
        mViewInstance.showErrorMessage("Contact Permission is required to run this app! Could you please enable that in the settings!");
    }

    @Override
    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    @Override
    public void loadContacts(Cursor contacts) {
        mDataSource.loadContacts(contacts);
    }

    @Override
    public void loadPhoneNumber(Cursor phoneNumberCursor) {
        mDataSource.loadContactsWithPhoneNumber(phoneNumberCursor);
    }

    @Override
    public void loadEmailByContactId(Cursor emailData) {
        mDataSource.loadEmailToContact(emailData);
    }

    @Override
    public void loadCallLogsByName(Cursor data) {
        mDataSource.loadCallLogsByName(data);
    }
}
