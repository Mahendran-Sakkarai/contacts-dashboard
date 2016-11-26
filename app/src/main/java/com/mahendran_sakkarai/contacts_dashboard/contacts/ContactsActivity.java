package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.utils.ActivityUtils;

public class ContactsActivity extends AppCompatActivity {
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

        mContactsPresenter = new ContactsPresenter(contactsFragment);
    }
}
