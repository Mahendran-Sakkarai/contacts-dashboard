package com.mahendran_sakkarai.contacts_dashboard.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class DataSource implements DataContract {
    private static DataSource INSTANCE;
    private final Context mContext;
    private HashMap<String, MCallLog> mCallLogs = new HashMap<>();
    private LoadCallLogs mCallBack;
    private HashMap<String, String> mContactNames = new HashMap<>();
    private ArrayList<ArrayList<String>> mContactIds = new ArrayList<>();
    private ArrayList<ArrayList<String>> mContactNamesList = new ArrayList<>();
    private int mCallLogsSize = 0;
    private int mContactRepeatCount = 0;
    private int mGettingPhoneNumberRepeatCount = 0;
    private int mGettingPhoneNumberRepeatedCount = 0;
    private int mCallLogsRepeatedCount = 0;

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
        mContactIds.clear();
        mContactNamesList.clear();
        mCallLogs.clear();
        mContactNames.clear();
        mContactRepeatCount = 0;
        mGettingPhoneNumberRepeatCount = 0;
        mGettingPhoneNumberRepeatedCount = 0;
        ArrayList<String> contactIds = new ArrayList<>();
        if (contactsCursor != null && !contactsCursor.isClosed()) {
            int idKey = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameKey = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int hasNoKey = contactsCursor.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER);

            contactsCursor.moveToFirst();
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

                if (contactIds.size() == 999 || contactsCursor.isLast()) {
                    mContactIds.add(contactIds);
                    contactIds = new ArrayList<>();
                }
            }

            mContactRepeatCount = mContactIds.size();
            if (mCallBack != null && mContactIds.size() > 0)
                mCallBack.triggerLoadContactsWithPhoneNumber(mContactIds.get(0));
        } else {
            mCallBack.onDataNotLoaded("No contacts found!!");
        }
    }

    @Override
    public void loadContactsWithPhoneNumber(Cursor phoneNumberCursor) {
        mCallLogsSize = 1;
        mGettingPhoneNumberRepeatedCount++;
        mCallLogsRepeatedCount = 0;
        ArrayList<String> contactNames = new ArrayList<>();
        if (phoneNumberCursor != null && mCallLogs != null && !phoneNumberCursor.isClosed()) {
            int phoneKey = phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int contactIdKey = phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            phoneNumberCursor.moveToFirst();
            while (phoneNumberCursor.moveToNext()) {
                String phoneNo = phoneNumberCursor.getString(phoneKey);
                String contactId = phoneNumberCursor.getString(contactIdKey);
                // If the contact contains phone number add to callLog
                MCallLog callLog = mCallLogs.get(contactId);
                if (callLog != null && phoneNo != null) {
                    callLog.setContactNumber(phoneNo);
                    mCallLogs.put(contactId, callLog);
                    contactNames.add(callLog.getName());
                    mContactNames.put(callLog.getName(), contactId);
                    if (contactNames.size() == 999) {
                        mContactNamesList.add(contactNames);
                        contactNames = new ArrayList<>();
                    }
                } else {
                    mCallLogs.remove(contactId);
                }
                if (phoneNumberCursor.isLast() && contactNames.size() > 0) {
                    mContactNamesList.add(contactNames);
                    contactNames = new ArrayList<>();
                }
            }
        }

        if (mContactNamesList.get(0).size() > 0 && mCallBack != null && --mContactRepeatCount == 0) {
            mGettingPhoneNumberRepeatCount = mContactNamesList.size();
            mContactIds = new ArrayList<>();
            mCallBack.triggerLoadCallLogsByName(mContactNamesList.get(0));
        } else if (mContactIds.size() > mGettingPhoneNumberRepeatedCount) {
            mCallBack.triggerLoadContactsWithPhoneNumber(mContactIds.get(mGettingPhoneNumberRepeatedCount));
        } else {
            mCallBack.onDataNotLoaded("No Contacts found!!");
        }
    }

    @Override
    public void loadEmailToContact(Cursor emailCursor) {
        if (emailCursor != null && !emailCursor.isClosed()) {
            int emailKey = emailCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Email.DATA);
            int contactIdKey = emailCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            emailCursor.moveToFirst();
            while (emailCursor.moveToNext()) {
                try {
                    String email = emailCursor.getString(emailKey);
                    String contactId = emailCursor.getString(contactIdKey);
                    if (email != null && email.length() != 0) {
                        MCallLog callLog = mCallLogs.get(contactId);
                        callLog.seteMail(email);
                        mCallLogs.put(contactId, callLog);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
    public void loadCallLogsByName(Cursor callLogCursor) {
        mCallLogsSize++;
        mCallLogsRepeatedCount++;
        if (callLogCursor != null && !callLogCursor.isClosed()) {
            int dateKey = callLogCursor.getColumnIndex(CallLog.Calls.DATE);
            int durationKey = callLogCursor.getColumnIndex(CallLog.Calls.DURATION);
            int numberKey = callLogCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int nameKey = callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

            callLogCursor.moveToFirst();
            while (callLogCursor.moveToNext()) {
                long callDate = callLogCursor.getLong(dateKey);
                long callDuration = callLogCursor.getLong(durationKey);
                String name = callLogCursor.getString(nameKey);
                String contactId = mContactNames.get(name);
                MCallLog callLog = mCallLogs.get(contactId);
                if (callLog != null) {
                    callLog.setTotalTalkTime(callLog.getTotalTalkTime() + callDuration);
                    if (callLog.getLastContactTime() < callDate)
                        callLog.setLastContactTime(callDate);

                    mCallLogs.put(contactId, callLog);
                }
            }
        }

        if (--mGettingPhoneNumberRepeatCount == 0) {
            // To check if talktime is greater than 0 else remove from list
            Iterator<String> iterator = mCallLogs.keySet().iterator();
            ArrayList<ArrayList<String>> contactIdsList = new ArrayList<>();
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

                if (contactIds.size() == 999 || !iterator.hasNext()) {
                    contactIdsList.add(contactIds);
                    contactIds = new ArrayList<>();
                }
            }

            mContactNamesList = new ArrayList<>();
            if (mCallLogs.size() > 0 && mCallBack != null && contactIdsList.size() > 0) {
                for (ArrayList<String> contactIdList : contactIdsList) {
                    if (contactIdList.size() > 0)
                        mCallBack.triggerGetEmailFromContactId(contactIdList);
                }
            } else if (mCallBack != null) {
                mCallBack.onDataNotLoaded("OOUCH!! No contacts found with the call logs.");
            }
        } else if (mContactNamesList.size() > mCallLogsRepeatedCount) {
            mCallBack.triggerLoadCallLogsByName(mContactNamesList.get(mCallLogsRepeatedCount));
        } else {
            mCallBack.onDataNotLoaded("Issue on fetching call logs!!");
        }
    }

    @Override
    public void loadCallLogs(final LoadCallLogs callback) {
        this.mCallBack = callback;
        mCallBack.triggerLoadContacts();
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
