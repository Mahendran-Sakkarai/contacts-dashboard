package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.database.Cursor;

import com.mahendran_sakkarai.contacts_dashboard.BasePresenter;
import com.mahendran_sakkarai.contacts_dashboard.BaseView;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.ArrayList;
import java.util.List;

public interface ContactsContract {
    interface View extends BaseView<Presenter> {
        void showLoadingData();

        void showCallLogs(List<MCallLog> callLogList);

        void showErrorMessage(String message);

        void triggerLoadContacts();

        void triggerLoadContactsWithPhoneNumber(ArrayList<String> contactIds);

        void triggerLoadCallLogsByMobileNumber(ArrayList<String> contactNumbers);

        void triggerGetEmailFromContactId(ArrayList<String> contactIds);
    }

    interface Presenter extends BasePresenter {
        void loadData();

        void callLogPermissionGranted();

        void callLogPermissionDenied();

        void contactPermissionGranted();

        void contactPermissionDenied();

        void setStarted(boolean started);

        void loadContacts(Cursor contacts);

        void loadPhoneNumber(Cursor phoneNumberCursor);

        void loadEmailByContactId(Cursor emailData);

        void loadCallLogs(Cursor callLogs);
    }

    interface ActivityCommunicator{
        void checkContactPermission();

        void checkCallLogPermission();
    }
}
