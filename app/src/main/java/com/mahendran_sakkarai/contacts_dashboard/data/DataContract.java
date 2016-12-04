package com.mahendran_sakkarai.contacts_dashboard.data;

import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public interface DataContract {
    void loadContacts(Cursor contactsCursor);

    void loadContactsWithPhoneNumber(Cursor phoneNumberCursor);

    void loadEmailToContact(Cursor emailData);

    void loadCallLogsByName(Cursor data);

    interface LoadCallLogs {
        void onLoad(List<MCallLog> callLogList);

        void onDataNotLoaded(String errorMessage);

        void triggerLoadContacts();

        void triggerLoadContactsWithPhoneNumber(ArrayList<String> contactId);

        void triggerLoadCallLogsByName(ArrayList<String> contactNames);

        void triggerGetEmailFromContactId(ArrayList<String> contactId);
    }

    void loadCallLogs(LoadCallLogs callback);

    Bitmap getContactImage(String contactId);
}
