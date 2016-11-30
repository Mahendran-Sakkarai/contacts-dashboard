package com.mahendran_sakkarai.contacts_dashboard.data;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;


public class DataSource implements DataContract {
    private static DataSource INSTANCE;
    private final Context mContext;

    public static DataSource newInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataSource(context);
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private DataSource(Context context) {
        this.mContext = context;
    }

    @Override
    public void loadCallLogs(final LoadCallLogs callback) {
        AsyncQueryHandler contactQueryHandler =
                new AsyncQueryHandler(mContext.getContentResolver()) {
                    @Override
                    protected void onQueryComplete(int token, Object cookie, Cursor contactsCursor) {
                        List<MCallLog> callLogs = new ArrayList<>();
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG)
                                != PackageManager.PERMISSION_GRANTED) {
                            callback.onDataNotLoaded();
                        } else {
                            if (contactsCursor != null) {
                                // To get all contacts
                                int idKey = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);
                                int nameKey = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                                int hasNoKey = contactsCursor.getColumnIndex(
                                        ContactsContract.Contacts.HAS_PHONE_NUMBER);

                                while (contactsCursor.moveToNext()) {
                                    String contactId = contactsCursor.getString(idKey);
                                    // To get phone number by contact id in ContactsContract.CommonDataKinds.Phone
                                    if (contactsCursor.getInt(hasNoKey) > 0) {
                                        Cursor phoneNumberCursor = mContext.getContentResolver().query(
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                new String[]{contactId}, null);
                                        while (phoneNumberCursor.moveToNext()) {
                                            String phoneNo = phoneNumberCursor.getString(
                                                    phoneNumberCursor.getColumnIndex(
                                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                                            phoneNo = phoneNo.replaceAll("\\s+", "");
                                            // If the contact contains phone number add to callLog
                                            MCallLog callLog = new MCallLog();
                                            callLog.setName(contactsCursor.getString(nameKey));
                                            callLog.setContactId(contactId);
                                            callLog.setContactNumber(phoneNo);

                                            callLogs.add(callLog);
                                        }
                                        phoneNumberCursor.close();
                                    }
                                }
                                contactsCursor.close();

                                // To get call logs by a number and update the callLogs list
                                String[] callLogProjection = new String[]{
                                        CallLog.Calls.NUMBER,
                                        CallLog.Calls.DATE,
                                        CallLog.Calls.DURATION
                                };

                                for (int i = 0; i < callLogs.size(); i++) {
                                    MCallLog callLog = callLogs.get(i);
                                    if (callLog != null) {
                                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                                        }
                                        Cursor callLogCursor = mContext.getContentResolver().query(
                                                CallLog.Calls.CONTENT_URI,
                                                callLogProjection,
                                                CallLog.Calls.NUMBER + " = ?",
                                                new String[]{callLog.getContactNumber()}, null);

                                        int date = callLogCursor.getColumnIndex(CallLog.Calls.DATE);
                                        int duration = callLogCursor.getColumnIndex(CallLog.Calls.DURATION);

                                        while (callLogCursor.moveToNext()) {
                                            long callDate = callLogCursor.getLong(date);
                                            long callDuration = callLogCursor.getLong(duration);
                                            callLog.setTotalTalkTime(callLog.getTotalTalkTime() + callDuration);
                                            if (callLog.getLastContactTime() < callDate)
                                                callLog.setLastContactTime(callDate);
                                        }
                                        callLogs.set(i, callLog);
                                        callLogCursor.close();
                                    }
                                }

                                // To check if talktime is greater than 0 else remove from list
                                ListIterator<MCallLog> iterator = callLogs.listIterator();
                                while (iterator.hasNext()) {
                                    int iteratorIndex = iterator.nextIndex();
                                    MCallLog callLog = iterator.next();
                                    if (callLog.getTotalTalkTime() > 0) {
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

                                        callLogs.set(iteratorIndex, callLog);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }

                            if (callLogs.size() > 0) {
                                Collections.sort(callLogs, new MCallLogComparator());
                                callback.onLoad(callLogs);
                            } else
                                callback.onDataNotLoaded();

                        }
                    }
                };
        String[] contactProjection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        contactQueryHandler.startQuery(1, null,
                ContactsContract.Contacts.CONTENT_URI, contactProjection, null, null, null);
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
