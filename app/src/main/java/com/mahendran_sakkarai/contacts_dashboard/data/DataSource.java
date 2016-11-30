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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class DataSource implements DataContract {
    private static DataSource INSTANCE;
    private final Context mContext;
    private HashMap<String, MCallLog> mCallLogs = new HashMap<>();
    private LoadCallLogs mCallBack;
    private HashMap<String, String> mContactNumbers = new HashMap<>();

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
    public void loadContacts(Cursor contactsCursor) {
        ArrayList<String> contactIds = new ArrayList<>();
        if (contactsCursor != null && !contactsCursor.isClosed()) {
            int idKey = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameKey = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int hasNoKey = contactsCursor.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER);

            while (contactsCursor.moveToNext()) {
                String contactId = contactsCursor.getString(idKey);
                // To get phone number by contact id in ContactsContract.CommonDataKinds.Phone
                if (contactsCursor.getInt(hasNoKey) > 0) {
                    MCallLog callLog = new MCallLog();
                    callLog.setName(contactsCursor.getString(nameKey));
                    callLog.setContactId(contactId);
                    mCallLogs.put(contactId, callLog);
                    contactIds.add(contactId);
                }
            }
            contactsCursor.close();
        }

        if (mCallBack != null)
            mCallBack.triggerLoadContactsWithPhoneNumber(contactIds);
    }

    @Override
    public void loadContactsWithPhoneNumber(Cursor phoneNumberCursor) {
        ArrayList<String> contactNumbers = new ArrayList<>();
        if (phoneNumberCursor != null && mCallLogs != null && !phoneNumberCursor.isClosed()) {
            int phoneKey = phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int contactIdKey = phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            while (phoneNumberCursor.moveToNext()) {
                String phoneNo = phoneNumberCursor.getString(phoneKey);
                String contactId = phoneNumberCursor.getString(contactIdKey);
                phoneNo = phoneNo.replaceAll("\\s+", "");
                // If the contact contains phone number add to callLog
                MCallLog callLog = mCallLogs.get(contactId);
                if (callLog != null && phoneNo != null) {
                    callLog.setContactNumber(phoneNo);
                    mCallLogs.put(contactId, callLog);
                    contactNumbers.add(phoneNo);
                    mContactNumbers.put(phoneNo, contactId);
                } else {
                    mCallLogs.remove(contactId);
                }
            }
            phoneNumberCursor.close();
        }

        if (mCallBack != null)
            mCallBack.triggerLoadCallLogsByMobileNumber(contactNumbers);
    }

    @Override
    public void loadEmailToContact(Cursor emailCursor) {
        if (emailCursor != null && !emailCursor.isClosed()) {
            int emailKey = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            int contactIdKey = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            while (emailCursor.moveToNext()) {
                String email = emailCursor.getString(emailKey);
                String contactId = emailCursor.getString(contactIdKey);
                if (email != null && email.length() != 0) {
                    MCallLog callLog = mCallLogs.get(contactId);
                    callLog.seteMail(email);
                    mCallLogs.put(contactId, callLog);
                    break;
                }
            }
            emailCursor.close();
        }

        Iterator<String> iterator = mCallLogs.keySet().iterator();
        while (iterator.hasNext()) {
            String contactId = iterator.next();
            MCallLog callLog = mCallLogs.get(contactId);
            callLog.setBitmap(getContactImage(contactId));
            mCallLogs.put(contactId, callLog);
        }

        if (mCallBack != null)
            mCallBack.onLoad(new ArrayList<>(mCallLogs.values()));
    }

    @Override
    public void loadCallLogs(final LoadCallLogs callback) {
        this.mCallBack = callback;
        mCallBack.triggerLoadContacts();
    }

    @Override
    public void loadCallLogs(Cursor callLogCursor) {
        if (callLogCursor != null && !callLogCursor.isClosed()) {
            int dateKey = callLogCursor.getColumnIndex(CallLog.Calls.DATE);
            int durationKey = callLogCursor.getColumnIndex(CallLog.Calls.DURATION);
            int numberKey = callLogCursor.getColumnIndex(CallLog.Calls.NUMBER);

            while (callLogCursor.moveToNext()) {
                long callDate = callLogCursor.getLong(dateKey);
                long callDuration = callLogCursor.getLong(durationKey);
                String number = callLogCursor.getString(numberKey);
                number = number.replaceAll("\\s+", "");
                String contactId = mContactNumbers.get(number);
                MCallLog callLog = mCallLogs.get(contactId);
                if (callLog != null) {
                    callLog.setTotalTalkTime(callLog.getTotalTalkTime() + callDuration);
                    if (callLog.getLastContactTime() < callDate)
                        callLog.setLastContactTime(callDate);
                }
            }
            callLogCursor.close();
        }

        // To check if talktime is greater than 0 else remove from list
        Iterator<String> iterator = mCallLogs.keySet().iterator();
        ArrayList<String> contactIds = new ArrayList<>();
        while (iterator.hasNext()) {
            String contactId = iterator.next();
            MCallLog callLog = mCallLogs.get(contactId);
            if (callLog.getTotalTalkTime() > 0) {
                // To get Email
                if (callLog.getContactId() != null && callLog.getContactId().length() > 0) {
                    contactIds.add(callLog.getContactId());
                }

                mCallLogs.put(contactId, callLog);
            } else {
                iterator.remove();
            }
        }

        if (mCallBack != null)
            mCallBack.triggerGetEmailFromContactId(contactIds);
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
