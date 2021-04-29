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
package com.barclays.absa.banking.presentation.shared;

import android.content.Context;

import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;

public abstract class IntentFactoryGenericResult extends IntentFactory {

    public static IntentBuilder getFailureResultBuilder(final Context context) {
        return new IntentBuilder(getResultBuilder(context).build())
                .setGenericResultIconToFailure();
    }

    public static IntentBuilder getSuccessfulResultBuilder(final Context context) {
        return new IntentBuilder(getResultBuilder(context).build())
                .setGenericResultIconToSuccessful();
    }

    public static IntentBuilder getResultBuilder(final Context context) {
        return new IntentBuilder()
                .setClass(context, GenericResultActivity.class)
                .hideGenericResultCallSupport();
    }

    public static IntentBuilder getAlertResultBuilder(final Context context) {
        return new IntentBuilder(getResultBuilder(context).build())
                .setGenericResultIconToAlert();
    }

    public static IntentBuilder getCustomResultBuilder(final Context context, String customAnimation) {
        return new IntentBuilder(getResultBuilder(context).build())
                .setGenericResultIconToCustomAnimation(customAnimation);
    }
}