package com.mahendran_sakkarai.contacts_dashboard.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

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

        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
    }
}
