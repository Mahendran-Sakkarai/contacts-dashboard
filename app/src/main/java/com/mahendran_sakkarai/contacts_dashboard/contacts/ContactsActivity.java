package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.data.DataSource;
import com.mahendran_sakkarai.contacts_dashboard.utils.ApplicationUtils;
import io.fabric.sdk.android.Fabric;

public class ContactsActivity extends AppCompatActivity implements
        ContactsContract.ActivityCommunicator {
    private static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private ContactsPresenter mContactsPresenter;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        ContactsFragment mContactsFragment =
                (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.container_layout);
        if (mContactsFragment == null) {
            mContactsFragment = ContactsFragment.newInstance();
            ApplicationUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mContactsFragment, R.id.container_layout
            );
        }

        mContactsPresenter = new ContactsPresenter(mContactsFragment, DataSource.newInstance(this),
                this);
    }

    @Override
    public void checkContactPermission() {
        // Asking permission to read contacts
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            mContactsPresenter.contactPermissionGranted();
        }
    }

    @Override
    public void checkCallLogPermission() {
        // Asking permission to read call logs
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG);
        } else {
            mContactsPresenter.callLogPermissionGranted();
            checkContactPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isChecked) {
            checkCallLogPermission();
        }
        isChecked = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataSource.destroyInstance();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALL_LOG: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContactsPresenter.callLogPermissionGranted();
                    checkContactPermission();
                } else {
                    mContactsPresenter.callLogPermissionDenied();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContactsPresenter.contactPermissionGranted();
                } else {
                    mContactsPresenter.contactPermissionDenied();
                }
            }
            break;
        }
    }
}
