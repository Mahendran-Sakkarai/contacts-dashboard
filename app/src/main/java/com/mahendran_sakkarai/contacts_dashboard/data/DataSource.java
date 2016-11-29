package com.mahendran_sakkarai.contacts_dashboard.data;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        HashMap<String, MCallLog> callLogs = new HashMap<>();

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onDataNotLoaded();
        } else {
            Cursor callLogCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, null);

            if (callLogCursor != null) {
                int number = callLogCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int date = callLogCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = callLogCursor.getColumnIndex(CallLog.Calls.DURATION);

                while (callLogCursor.moveToNext()) {
                    String phoneNumber = callLogCursor.getString(number);
                    long callDate = callLogCursor.getLong(date);
                    long callDuration = callLogCursor.getLong(duration);
                    MCallLog callLog = null;
                    if (callLogs.containsKey(phoneNumber)) {
                        callLog = callLogs.get(phoneNumber);
                        callLog.setTotalTalkTime(callLog.getTotalTalkTime() + callDuration);
                    } else {
                        callLog = new MCallLog();
                        callLog.setContactNumber(phoneNumber);
                        callLog.setLastContactTime(callDate);
                        callLog.setTotalTalkTime(callDuration);
                    }

                    callLogs.put(phoneNumber, callLog);
                }

                callLogCursor.close();

                for (String key : callLogs.keySet()) {
                    MCallLog callLog = callLogs.get(key);

                    String[] projection = new String[] {
                            ContactsContract.PhoneLookup.DISPLAY_NAME,
                            ContactsContract.PhoneLookup._ID
                    };

                    // To get name and contact id
                    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(key));

                    Cursor contactCursor = mContext.getContentResolver().query(
                            contactUri, projection, null, null, null
                    );

                    if (contactCursor != null) {
                        int idKey = contactCursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                        int nameKey = contactCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                        while (contactCursor.moveToNext()) {
                            callLog.setContactId(contactCursor.getString(idKey));
                            callLog.setName(contactCursor.getString(nameKey));
                        }

                        contactCursor.close();
                    }

                    // To get Email
                    if (callLog.getContactId() != null && callLog.getContactId().length() > 0) {
                        Cursor emailCursor = mContext.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{callLog.getContactId()}, null
                        );

                        if (emailCursor != null) {
                            int emailKey = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                            while (emailCursor.moveToNext()) {
                                String email = emailCursor.getString(emailKey);
                                if (email != null && email.length() != 0) {
                                    callLog.seteMail(email);
                                    break;
                                }
                            }
                            emailCursor.close();
                        }
                    }

                    callLogs.put(key, callLog);
                }
            }

            if (callLogs.size() > 0)
                callback.onLoad(callLogs);
            else
                callback.onDataNotLoaded();
        }
    }

    @Override
    public Bitmap getContactImage(String contactId) {
        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    mContext.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                            Long.valueOf(contactId)));
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return photo;
    }
}
