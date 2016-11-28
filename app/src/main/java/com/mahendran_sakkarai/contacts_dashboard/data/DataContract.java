package com.mahendran_sakkarai.contacts_dashboard.data;

import java.util.List;

public interface DataContract {
    interface LoadCallLogs {
        void onLoad(List<MCallLog> callLogList);

        void onDataNotLoaded();
    }

    void loadCallLogs(LoadCallLogs callback);
}
