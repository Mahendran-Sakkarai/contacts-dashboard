package com.mahendran_sakkarai.contacts_dashboard.contacts;

/**
 * Created by Nandakumar on 11/27/2016.
 */
public class ContactsPresenter {
    private final ContactsContract.View mViewInstance;

    public ContactsPresenter(ContactsContract.View contactsView) {
        this.mViewInstance = contactsView;
    }
}
