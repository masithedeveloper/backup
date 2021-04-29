/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.beneficiaries.ui;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.barclays.absa.banking.framework.app.BMBConstants.ABSA;
import static com.barclays.absa.banking.framework.app.BMBConstants.ANOTHER_BANK;
import static com.barclays.absa.banking.framework.app.BMBConstants.BILL;
import static com.barclays.absa.banking.framework.app.BMBConstants.EXTERNAL;
import static com.barclays.absa.banking.framework.app.BMBConstants.INTERNAL;
import static com.barclays.absa.banking.framework.app.BMBConstants.OTHER_BANK;

public final class BeneficiaryUtils {

    public static List<BeneficiaryObject> sortBeneficiariesByFirstName(List<BeneficiaryObject> beneficiaries) {
        Collections.sort(beneficiaries, (o1, o2) -> {
            if (o1 != null && o1.getBeneficiaryName() != null && o2 != null && o2.getBeneficiaryName() != null) {
                return o1.getBeneficiaryName().compareToIgnoreCase(o2.getBeneficiaryName());
            } else {
                return 0;
            }
        });
        return beneficiaries;
    }

    private static List<BeneficiaryObject> sortBeneficiariesByTransactionDate(List<BeneficiaryObject> beneficiaryObjects) {
        Collections.sort(beneficiaryObjects, (o1, o2) -> {
            if (o1 != null && o2 != null) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale()).parse(o2.getLastTransactionDate()).compareTo(new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale()).parse(o1.getLastTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        });
        return beneficiaryObjects;
    }

    private static List<BeneficiaryObject> getAllBeneficiariesWithTransactionDate(List<BeneficiaryObject> beneficiaries) {
        List<BeneficiaryObject> allRecentList = new ArrayList<>();
        for (BeneficiaryObject beneficiaryObject : beneficiaries) {
            if (!TextUtils.isEmpty((beneficiaryObject.getLastTransactionDate()))) {
                allRecentList.add(beneficiaryObject);
            }
        }
        return allRecentList;
    }

    static List<BeneficiaryObject> computeRecents(List<BeneficiaryObject> beneficiaries) {
        final List<BeneficiaryObject> shortenedRecents = new ArrayList<>();
        List<BeneficiaryObject> beneficiariesWithTransactionDate = getAllBeneficiariesWithTransactionDate(beneficiaries);
        final List<BeneficiaryObject> beneficiariesByTransactionDate = BeneficiaryUtils.sortBeneficiariesByTransactionDate(beneficiariesWithTransactionDate);
        if (!beneficiariesByTransactionDate.isEmpty()) {
            int lastRecentItem = beneficiariesByTransactionDate.size() < 5 ? beneficiariesByTransactionDate.size() : 5;
            for (int i = 0; i < lastRecentItem; i++) {
                shortenedRecents.add(beneficiariesByTransactionDate.get(i));
            }
        }
        return shortenedRecents;
    }

    static void applyFilter(BeneficiarySectionedRecyclerAdapter.BeneficiarySectionItemViewHolder viewHolder, BeneficiaryObject beneficiaryObject, String filterText) {
        if (!TextUtils.isEmpty(filterText)) {
            Spannable searchBeneficiary = new SpannableString(beneficiaryObject.getBeneficiaryName());
            Spannable searchAccount = new SpannableString(beneficiaryObject.getBeneficiaryAccountNumber());

            int startBeneficiaryPosition = beneficiaryObject.getBeneficiaryName().toLowerCase().indexOf(filterText.toLowerCase());
            int startAccountPosition = beneficiaryObject.getBeneficiaryAccountNumber().toLowerCase().indexOf(filterText.toLowerCase());

            int endBeneficiaryPosition = startBeneficiaryPosition + filterText.length();
            int endAccountPosition = startAccountPosition + filterText.length();

            boolean found = false;

            if (endBeneficiaryPosition <= searchBeneficiary.length() && startBeneficiaryPosition != -1) {
                found = true;
                searchBeneficiary.setSpan(new ForegroundColorSpan(Color.RED), startBeneficiaryPosition, endBeneficiaryPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.beneficiaryView.getNameTextView().setText(searchBeneficiary);
            }

            if (endAccountPosition <= searchAccount.length() && startAccountPosition != -1) {
                found = true;
                searchAccount.setSpan(new ForegroundColorSpan(Color.RED), startAccountPosition, endAccountPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.beneficiaryView.getAccountNumberTextView().setText(searchAccount);
            }

            viewHolder.beneficiaryView.setVisibility(!found ? View.GONE : View.VISIBLE);
        } else {
            viewHolder.beneficiaryView.setVisibility(View.VISIBLE);
        }
    }

    public static int getMethodTypeIndex(String methodType) {
        if (BMBConstants.SMS.equalsIgnoreCase(methodType) || "S".equalsIgnoreCase(methodType))
            return 0;
        if (BMBConstants.EMAIL.equalsIgnoreCase(methodType) || "E".equalsIgnoreCase(methodType))
            return 1;
        if (BMBConstants.FAX.equalsIgnoreCase(methodType) || "F".equalsIgnoreCase(methodType))
            return 2;
        return -1;
    }

    public static String getBeneficiaryType(String beneficiaryType) {
        if (INTERNAL.equalsIgnoreCase(beneficiaryType) || ABSA.equalsIgnoreCase(beneficiaryType)) {
            return ABSA;
        } else if (EXTERNAL.equalsIgnoreCase(beneficiaryType)
                || OTHER_BANK.equalsIgnoreCase(beneficiaryType)
                || ANOTHER_BANK.equalsIgnoreCase(beneficiaryType)) {
            return OTHER_BANK;
        } else {
            return BILL;
        }
    }
}