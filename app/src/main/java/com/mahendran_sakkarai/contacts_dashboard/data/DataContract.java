package com.mahendran_sakkarai.contacts_dashboard.data;

import android.graphics.Bitmap;

import java.util.List;

public interface DataContract {
    interface LoadCallLogs {
        void onLoad(List<MCallLog> callLogList);

        void onDataNotLoaded();
    }

    void loadCallLogs(LoadCallLogs callback);

    Bitmap getContactImage(String contactId);
}
