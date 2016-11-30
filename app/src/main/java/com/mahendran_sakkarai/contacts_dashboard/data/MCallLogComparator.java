package com.mahendran_sakkarai.contacts_dashboard.data;

import java.util.Comparator;

public class MCallLogComparator implements Comparator<MCallLog> {

    @Override
    public int compare(MCallLog c1, MCallLog c2) {
        return (int) (c2.getTotalTalkTime() - c1.getTotalTalkTime());
    }
}
