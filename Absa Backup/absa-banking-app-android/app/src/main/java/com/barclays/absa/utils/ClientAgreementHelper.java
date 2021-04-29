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
package com.barclays.absa.utils;

import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.StringRes;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;

import org.jetbrains.annotations.NotNull;

import styleguide.forms.CheckBoxView;

public class ClientAgreementHelper {

    public static void updatePersonalClientAgreementContainer(Context context, boolean isClientAgreementAccepted, CheckBoxView acceptClientAgreementCheckBox, String clientAgreement) {
        IAbsaCacheService absaCacheService = DaggerHelperKt.getServiceInterface(IAbsaCacheService.class);

        if (isClientAgreementAccepted) {
            acceptClientAgreementCheckBox.setChecked(true);
            acceptClientAgreementCheckBox.setVisibility(View.VISIBLE);
            acceptClientAgreementCheckBox.setCheckBoxVisibility(false);
            acceptClientAgreementCheckBox.setClickableLinkTitle((R.string.client_agreement_have_accepted), clientAgreement, actionToPerformWhenTextIsClicked(context), R.color.dark_grey);

            if (!absaCacheService.isPersonalClientAgreementAccepted()) {
                absaCacheService.setPersonalClientAgreementAccepted(true);
            }
        } else {
            acceptClientAgreementCheckBox.setVisibility(View.VISIBLE);
            acceptClientAgreementCheckBox.setClickableLinkTitle((R.string.accept_personal_client_agreement), clientAgreement, actionToPerformWhenTextIsClicked(context), R.color.dark_grey);
        }
    }

    public static void updateClientAgreementContainer(CheckBoxView checkBoxView, boolean isClientAgreementAccepted, int sentenceStringResourceId, @StringRes int textToMakeClickableResourceId,
                                                      ClickableSpan actionToPerformWhenTextIsClicked) {
        IAbsaCacheService absaCacheService = DaggerHelperKt.getServiceInterface(IAbsaCacheService.class);

        if (isClientAgreementAccepted) {
            checkBoxView.setOnCheckedListener(null);
            checkBoxView.setChecked(true);
            checkBoxView.setCheckBoxVisibility(false);
            checkBoxView.setClickableLinkTitle(sentenceStringResourceId, textToMakeClickableResourceId, actionToPerformWhenTextIsClicked, R.color.dark_grey);
            if (!absaCacheService.isPersonalClientAgreementAccepted()) {
                absaCacheService.setPersonalClientAgreementAccepted(true);
            }
        } else {
            checkBoxView.setCheckBoxVisibility(true);
            checkBoxView.setClickableLinkTitle(sentenceStringResourceId, textToMakeClickableResourceId, actionToPerformWhenTextIsClicked, R.color.dark_grey);
        }
    }

    public static String fetchClientType(Context context) {
        return ClientTypeGroupKt.isBusiness(CustomerProfileObject.getInstance().getClientTypeGroup()) ? context.getString(R.string.business_client_agreement) : context.getString(R.string.personal_client_agreement);
    }

    private static ClickableSpan actionToPerformWhenTextIsClicked(final Context context) {
        return new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                PdfUtil.INSTANCE.showTermsAndConditionsClientAgreement((BaseActivity) context, CustomerProfileObject.getInstance().getClientTypeGroup());
            }
        };
    }
}