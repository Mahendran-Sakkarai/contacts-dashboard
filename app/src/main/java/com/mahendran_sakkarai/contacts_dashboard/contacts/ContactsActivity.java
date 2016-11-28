package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.data.DataSource;
import com.mahendran_sakkarai.contacts_dashboard.utils.ActivityUtils;

public class ContactsActivity extends AppCompatActivity implements ContactsContract.ActivityCommunicator{
    private static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private ContactsPresenter mContactsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContactsFragment contactsFragment =
                (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.container_layout);
        if (contactsFragment == null) {
            contactsFragment = ContactsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), contactsFragment, R.id.container_layout
            );
        }

        mContactsPresenter = new ContactsPresenter(contactsFragment, DataSource.newInstance(this),
                this);

        checkCallLogPermission();

        checkContactPermission();
    }

    @Override
    public void checkContactPermission() {
        // Asking permission to read contacts
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
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
            return;
        } else {
            mContactsPresenter.callLogPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALL_LOG: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContactsPresenter.callLogPermissionGranted();
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
