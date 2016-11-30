package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.database.Cursor;

import com.mahendran_sakkarai.contacts_dashboard.BasePresenter;
import com.mahendran_sakkarai.contacts_dashboard.BaseView;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.List;

public interface ContactsContract {
    interface View extends BaseView<Presenter> {
        void showLoadingData();

        void showCallLogs(List<MCallLog> callLogList);

        void showNoDataAvailable();

        void triggerLoadContacts();

        void triggerLoadContactsWithPhoneNumber(String contactId);

        void triggerLoadCallLogsByMobileNumber(String contactId, String contactNumber);

        void triggerGetEmailFromContactId(String contactId);
    }

    interface Presenter extends BasePresenter {
        void loadData();

        void callLogPermissionGranted();

        void callLogPermissionDenied();

        void contactPermissionGranted();

        void contactPermissionDenied();

        void setStarted(boolean started);

        void loadContacts(Cursor contacts);

        void loadPhoneNumber(String contactId, Cursor phoneNumberCursor);

        void loadCallLogs(String contactId, Cursor callLogsByNumber);

        void loadEmailByContactId(String contactId, Cursor emailData);
    }

    interface ActivityCommunicator{
        void checkContactPermission();

        void checkCallLogPermission();
    }
}
