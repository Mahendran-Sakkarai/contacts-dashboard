package com.mahendran_sakkarai.contacts_dashboard.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahendran_sakkarai.contacts_dashboard.R;

/**
 * Created by Nandakumar on 11/27/2016.
 */
public class ContactsFragment extends Fragment implements ContactsContract.View{
    private ContactsContract.Presenter mPresenter;
    private RecyclerView mContactsRecyclerView;

    public static ContactsFragment newInstance(){
        return new ContactsFragment();
    }

    public ContactsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contacts_fragment, container, false);

        mContactsRecyclerView = (RecyclerView) root.findViewById(R.id.contacts_list);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void setPresenter(ContactsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
