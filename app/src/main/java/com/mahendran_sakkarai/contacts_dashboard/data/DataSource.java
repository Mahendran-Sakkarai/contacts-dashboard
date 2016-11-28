package com.mahendran_sakkarai.contacts_dashboard.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;


public class DataSource implements DataContract {
    private static DataSource INSTANCE;
    private final Context mContext;

    public static DataSource newInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataSource(context);
        }

        return INSTANCE;
    }

    private DataSource(Context context) {
        this.mContext = context;
    }


    @Override
    public void loadCallLogs(LoadCallLogs callback) {
        List<MCallLog> callLogs = new ArrayList<>();

        callback.onLoad(callLogs);
    }
}
