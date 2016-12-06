/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mahendran_sakkarai.contacts_dashboard.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This provides methods to help Activities load their UI.
 */
public class ApplicationUtils {
    public static final int LOAD_CONTACTS_LOADER = 1;
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final int LOAD_CONTACTS_WITH_PHONE_NUMBER_LOADER = 2;
    public static final String CONTACT_NUMBER = "CONTACT_NUMBER";
    public static final int LOAD_CALL_LOG_BY_NUMBER_LOADER = 3;
    public static final int LOAD_CALL_LOG_BY_NAME_LOADER = 4;
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final int LOAD_EMAIL_BY_CONTACT_ID = 5;

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static String convertToTimeFormat(long totalTalkTime) {
        String time = "";
        if (totalTalkTime > 0) {
            int seconds = (int) (totalTalkTime) % 60 ;
            int minutes = (int) ((totalTalkTime / (60)) % 60);
            int hours   = (int) ((totalTalkTime / (60*60)) % 24);

            if (hours > 0)
                time += hours + "h ";

            if (minutes > 0)
                time += minutes + "m ";

            if (seconds > 0)
                time += seconds + "s ";
        } else {
            time = "0s";
        }

        return time;
    }

    public static String convertToDateFormat(long dateValue) {
        Date dateToFormat = new Date(dateValue);
        DateFormat formatter = new SimpleDateFormat("dd-MMM hh:mm a");

        String date = "";
        if (dateValue > 0) {
            date += formatter.format(dateToFormat);
        } else {
            date = "--";
        }

        return date;
    }

    public static String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
