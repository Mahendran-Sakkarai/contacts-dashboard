package com.mahendran_sakkarai.contacts_dashboard.data;

import android.graphics.Bitmap;

import java.util.List;

public interface DataContract {
    interface LoadCallLogs {
        void onLoad(List<MCallLog> callLogList);

        void onDataNotLoaded();

        void triggerLoadContacts();

        void triggerLoadContactsWithPhoneNumber(String contactId);

        void triggerLoadCallLogsByMobileNumber(String contactId, String contactNumber);

        void triggerGetEmailFromContactId(String contactId);
    }

    void loadCallLogs(LoadCallLogs callback);

    Bitmap getContactImage(String contactId);
}
