package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahendran_sakkarai.contacts_dashboard.R;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;
import com.mahendran_sakkarai.contacts_dashboard.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements ContactsContract.View {
    private ContactsContract.Presenter mPresenter;
    private RecyclerView mContactsRecyclerView;
    private ContactsAdapter mContactsRecyclerViewAdapter;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    public ContactsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contacts_fragment, container, false);

        mContactsRecyclerView = (RecyclerView) root.findViewById(R.id.contacts_list);
        mContactsRecyclerViewAdapter = new ContactsAdapter();
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mContactsRecyclerViewAdapter.getItemViewType(position)) {
                    case ContactsAdapter.CALL_LOG:
                        return 1;
                    case ContactsAdapter.MESSAGE:
                        return layoutManager.getSpanCount();
                    default:
                        return -1;
                }
            }
        });
        mContactsRecyclerView.setLayoutManager(layoutManager);
        mContactsRecyclerView.setAdapter(mContactsRecyclerViewAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.setStarted(false);
    }

    @Override
    public void setPresenter(ContactsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoadingData() {
        mContactsRecyclerViewAdapter.showMessage(getContext().getString(R.string.loading_data));
    }

    @Override
    public void showCallLogs(List<MCallLog> callLogList) {
        mContactsRecyclerViewAdapter.showData(callLogList);
    }

    @Override
    public void showErrorMessage(String message) {
        mContactsRecyclerViewAdapter.showMessage(message);
    }

    @Override
    public void triggerLoadContacts() {
        getLoaderManager().initLoader(
                ApplicationUtils.LOAD_CONTACTS_LOADER, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        String[] contactProjection = new String[]{
                                android.provider.ContactsContract.Contacts._ID,
                                android.provider.ContactsContract.Contacts.DISPLAY_NAME,
                                android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER
                        };
                        return new CursorLoader(
                                getActivity(),
                                android.provider.ContactsContract.Contacts.CONTENT_URI,
                                contactProjection,
                                null,
                                null,
                                null
                        );
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        mPresenter.loadContacts(data);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                });
    }

    @Override
    public void triggerLoadContactsWithPhoneNumber(final ArrayList<String> contactIds) {
        Bundle args = new Bundle();
        args.putStringArrayList(ApplicationUtils.CONTACT_ID, contactIds);
        getLoaderManager().initLoader(ApplicationUtils.LOAD_CONTACTS_WITH_PHONE_NUMBER_LOADER, args,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        if (args != null) {
                            ArrayList<String> contactIds = args.getStringArrayList(ApplicationUtils.CONTACT_ID);
                            if (contactIds != null) {
                                return new CursorLoader(
                                        getActivity(),
                                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        new String[]{
                                                android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER,
                                                android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                        },
                                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " IN (" + ApplicationUtils.makePlaceholders(contactIds.size()) +")",
                                        contactIds.toArray(new String[contactIds.size()]), null
                                );
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        mPresenter.loadPhoneNumber(data);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                });
    }

    @Override
    public void triggerGetEmailFromContactId(ArrayList<String> contactIds) {
        Bundle args = new Bundle();
        args.putStringArrayList(ApplicationUtils.CONTACT_ID, contactIds);
        getLoaderManager().initLoader(ApplicationUtils.LOAD_EMAIL_BY_CONTACT_ID, args,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        if (args != null) {
                            String[] emailProjection = new String[]{
                                    android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                                    android.provider.ContactsContract.CommonDataKinds.Email.DATA
                            };
                            ArrayList<String> contactIds = args.getStringArrayList(ApplicationUtils.CONTACT_ID);
                            if (contactIds != null) {
                                return new CursorLoader(
                                        getActivity(),
                                        android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                        emailProjection,
                                        android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID + " IN ("+ApplicationUtils.makePlaceholders(contactIds.size())+")",
                                        contactIds.toArray(new String[contactIds.size()]), null
                                );
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        mPresenter.loadEmailByContactId(data);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                });
    }

    @Override
    public void triggerLoadCallLogsByName(ArrayList<String> contactNames) {
        Bundle args = new Bundle();
        args.putStringArrayList(ApplicationUtils.CONTACT_NAME, contactNames);
        getLoaderManager().initLoader(ApplicationUtils.LOAD_CALL_LOG_BY_NAME_LOADER, args,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        if (args != null) {
                            String[] callLogProjection = new String[]{
                                    CallLog.Calls.NUMBER,
                                    CallLog.Calls.DATE,
                                    CallLog.Calls.DURATION,
                                    CallLog.Calls.CACHED_NAME
                            };
                            ArrayList<String> contactNames = args.getStringArrayList(ApplicationUtils.CONTACT_NAME);
                            if (contactNames != null) {
                                return new CursorLoader(
                                        getActivity(),
                                        CallLog.Calls.CONTENT_URI,
                                        callLogProjection,
                                        CallLog.Calls.CACHED_NAME + " IN (" + ApplicationUtils.makePlaceholders(contactNames.size()) + ")",
                                        contactNames.toArray(new String[contactNames.size()]), null
                                );
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        mPresenter.loadCallLogsByName(data);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                });
    }
}
