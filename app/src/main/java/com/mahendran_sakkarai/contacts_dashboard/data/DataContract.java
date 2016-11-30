package com.mahendran_sakkarai.contacts_dashboard.data;

import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public interface DataContract {
    void loadContacts(Cursor contactsCursor);

    void loadContactsWithPhoneNumber(Cursor phoneNumberCursor);

    void loadEmailToContact(Cursor emailData);

    interface LoadCallLogs {
        void onLoad(List<MCallLog> callLogList);

        void onDataNotLoaded();

        void triggerLoadContacts();

        void triggerLoadContactsWithPhoneNumber(ArrayList<String> contactId);

        void triggerLoadCallLogsByMobileNumber(ArrayList<String> contactNumbers);

        void triggerGetEmailFromContactId(ArrayList<String> contactId);
    }

    void loadCallLogs(LoadCallLogs callback);

    void loadCallLogs(Cursor callLogs);

    Bitmap getContactImage(String contactId);
}
