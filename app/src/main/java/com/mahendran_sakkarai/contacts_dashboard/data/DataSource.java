package com.mahendran_sakkarai.contacts_dashboard.data;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Date;
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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onDataNotLoaded();
        } else {
            Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, null);

            if (managedCursor != null) {
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

                while (managedCursor.moveToNext()) {
                    String phoneNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                }

                managedCursor.close();
            }

            if (callLogs.size() > 0)
                callback.onLoad(callLogs);
            else
                callback.onDataNotLoaded();
        }
    }
}
