package com.mahendran_sakkarai.contacts_dashboard.data;

import java.util.Comparator;

/**
 * Created by Nandakumar on 11/29/2016.
 */

public class MCallLogComparator implements Comparator<MCallLog> {

    @Override
    public int compare(MCallLog c1, MCallLog c2) {
        return (int) (c2.getTotalTalkTime() - c1.getTotalTalkTime());
    }
}
