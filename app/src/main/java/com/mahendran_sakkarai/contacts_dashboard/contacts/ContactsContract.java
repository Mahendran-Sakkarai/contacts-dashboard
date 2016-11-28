package com.mahendran_sakkarai.contacts_dashboard.contacts;

import com.mahendran_sakkarai.contacts_dashboard.BasePresenter;
import com.mahendran_sakkarai.contacts_dashboard.BaseView;
import com.mahendran_sakkarai.contacts_dashboard.data.MCallLog;

import java.util.List;

public interface ContactsContract {
    interface View extends BaseView<Presenter> {
        void showLoadingData();

        void showCallLogs(List<MCallLog> callLogList);

        void showNoDataAvailable();
    }

    interface Presenter extends BasePresenter {
        void callLogPermissionGranted();

        void callLogPermissionDenied();

        void contactPermissionGranted();

        void contactPermissionDenied();
    }

    interface ActivityCommunicator{
        void checkContactPermission();

        void checkCallLogPermission();
    }
}
