/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;

import styleguide.content.Contact;

import static com.barclays.absa.banking.framework.utils.IntentUtil.isIntentAvailable;

public class TelephoneUtil {

    public static final String FRAUD_NUMBER = "tel:0860557557";

    public static void supportCall(Activity activity) {
        final String SUPPORT_GENERAL = "tel:0860111123";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(SUPPORT_GENERAL));
        if (isIntentAvailable(activity, intent))
            activity.startActivity(intent);
    }

    public static void insuranceRequestCall(Activity activity) {
        final String INSURANCE_REQUEST = "tel:086 010 9693";
        call(activity, INSURANCE_REQUEST);
    }

    public static void supportCallRegistrationIssues(Activity activity) {
        final String SUPPORT_REGISTRATION = "tel:0860008600";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(SUPPORT_REGISTRATION));
        if (isIntentAvailable(activity, intent))
            activity.startActivity(intent);
    }

    public static void supportDebitCardDivision(Activity activity) {
        final String SUPPORT_DEBIT_CARD_REPLACEMENT = "tel:0800111155";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(SUPPORT_DEBIT_CARD_REPLACEMENT));
        if (isIntentAvailable(activity, intent))
            activity.startActivity(intent);
    }

    public static void supportApplyOverdraftDivision(Activity activity) {
        final String SUPPORT_APPLY_OVERDRAFT = "tel:0860100372";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(SUPPORT_APPLY_OVERDRAFT));
        if (isIntentAvailable(activity, intent))
            activity.startActivity(intent);
    }

    public static void supportApplyRewardsDivision(Activity activity) {
        final String SUPPORT_APPLY_REWARD = "tel:0861788888";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(SUPPORT_APPLY_REWARD));
        if (isIntentAvailable(activity, intent))
            activity.startActivity(intent);
    }

    public static void callMeFailureContact(Activity activity) {
        final String CONTACT_ME_REQUEST = "tel:086 010 0876";
        call(activity, CONTACT_ME_REQUEST);
    }

    public static void callMeFailureContactLife(Activity activity) {
        final String CONTACT_ME_REQUEST = "tel:086 022 7253";
        call(activity, CONTACT_ME_REQUEST);
    }

    public static void callStopAndReplaceCreditCardLocalNumber(Activity activity) {
        final String LOCAL_NUMBER = "tel:0800 11 11 55";
        call(activity, LOCAL_NUMBER);
    }

    public static void callStopAndReplaceCreditCardInternationalNumber(Activity activity) {
        final String ABROAD_NUMBER = "tel:+27 11 501 5050";
        call(activity, ABROAD_NUMBER);
    }

    public static void callStopAndReplaceCreditCardNumber(Activity activity) {
        final String PHONE_NUMBER = "tel:0800 111 155";
        call(activity, PHONE_NUMBER);
    }

    public static void callWesternUnionPaymentsLocalNumber(Activity activity) {
        final String LOCAL_NUMBER = "tel:0860 151 151";
        call(activity, LOCAL_NUMBER);
    }

    public static void callWesternUnionPaymentsInternationalNumber(Activity activity) {
        final String INTERNATIONAL_NUMBER = "tel:+27 11 335 4019";
        call(activity, INTERNATIONAL_NUMBER);
    }

    public static void callFuneralSupportNumber(Activity activity) {
        final String PHONE_NUMBER = "tel:086 022 7253";
        call(activity, PHONE_NUMBER);
    }

    public static void callStopAndReplaceCardHelpline(Activity activity) {
        final String PHONE_NUMBER = "tel:011 501 5089";
        call(activity, PHONE_NUMBER);
    }

    public static void callFraudHotlineAndLostStolen(Activity activity) {
        final String PHONE_NUMBER = "tel:086 055 7557";
        call(activity, PHONE_NUMBER);
    }

    public static void callInternational(Activity activity) {
        final String PHONE_NUMBER = "tel:+27 11 501 5019";
        call(activity, PHONE_NUMBER);
    }

    public static void callInternationalEmergecy(Activity activity) {
        final String PHONE_NUMBER = "tel:+27 11 501 5089";
        call(activity, PHONE_NUMBER);
    }

    public static void callMainSwitchBoard(Activity activity) {
        call(activity, "tel:086 000 8600");
    }

    public static void call(Activity activity, @NonNull String phoneData) {
        if (!phoneData.startsWith("tel:")) {
            phoneData = "tel:" + phoneData;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneData));
        activity.startActivity(intent);
    }

    public static Contact getCallCentreContact(Context context) {
        return new Contact(context.getString(R.string.sure_check_contact_support), context.getString(R.string.sure_check_contact_support_number));
    }

    public static Contact getBankingAppSupportContact(Context context) {
        return new Contact(context.getString(R.string.banking_app_support_title), context.getString(R.string.banking_app_support_contact));
    }
}