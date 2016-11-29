package com.mahendran_sakkarai.contacts_dashboard.data;

import android.graphics.Bitmap;

import java.util.HashMap;

public interface DataContract {
    interface LoadCallLogs {
        void onLoad(HashMap<String, MCallLog> callLogList);

        void onDataNotLoaded();
    }

    interface LoadContact {
        void onLoad(ContactDetails contact);

        void onDataNotLoaded();
    }

    void loadCallLogs(LoadCallLogs callback);

    Bitmap getContactImage(String contactId);
}
