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
package com.barclays.absa.banking.buy.ui.electricity;

import android.view.View;

import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;

import org.jetbrains.annotations.NotNull;

public interface PrepaidElectricityView extends BaseView {
    void doneClicked();

    void requestBeneficiaries();

    void navigateToPurchaseDetailsFragment();
    void navigateToProblemWithMeterNumberActivity();
    void navigateToRecipientMeterNumberFragment();
    void navigateToSomeoneNewBeneficiaryDetailsFragment();
    void navigateToBeneficiaryAddedResultScreen();
    void setBeneficiariesFragment(BeneficiaryListObject beneficiaryListObject);
    void navigateToConfirmPurchaseFragment(PurchasePrepaidElectricityResultObject purchasePrepaidElectricityResultObject);
    void navigateToImportantInformationFragment();
    void navigateToPurchaseReceiptFragment();
    void navigateToPurchaseSuccessfulFragment(PrepaidElectricity prepaidElectricityResponse);
    void navigateToPurchaseUnsuccessfulFragment(PrepaidElectricity prepaidElectricityResponse);
    void navigateToPurchaseHistoryFragment();
    void navigateToPurchaseHistorySelectedFragment();

    void navigateToNeedHelpFragment();

    void saveMeterNumberObject(MeterNumberObject meterNumberObject);
    void setToolbarTitle(String toolbarTitle);

    void setToolbarTitle(String title, View.OnClickListener onClickListener);

    void setToolbarIcon(int icon);

    void getMeterHistory(BeneficiaryObject beneficiaryObject);

    void showRecipientMeterError(String errorMessage);

    void validateRecipientMeterNumber(String selectedValue);

    void validateExistingMeterNumber(String beneficiaryAccountNumber, BeneficiaryObject beneficiaryListObject);

    void saveBeneficiaryDetailsObject(BeneficiaryDetailObject beneficiaryDetailObject);

    void addPrepaidElectricityBeneficiary(String selectedValue, String meterNumber, String utility);

    void showBeneficiaryExistDialog();

    void confirmElectricityPurchase();

    void setupSureCheckDelegate(@NotNull SureCheckDelegate buyElectricitySureCheckDelegate);

    MeterNumberObject getMeterNumberObject();

    BeneficiaryListObject getBeneficiaryListObject();

    BeneficiaryObject getSelectedBeneficiaryObject();

    String getString(int string);

    PurchasePrepaidElectricityResultObject getPurchasePrepaidElectricityResultObject();

    BeneficiaryDetailObject getBeneficiaryDetailObject();

    void hideToolbar();
    void showToolbar();

    PrepaidElectricity getPrepaidElectricityResponse();

    void setSelectedHistoryTransaction(TransactionObject transactionObject);
    TransactionObject getSelectedHistoryTransaction();

    BeneficiariesObservable getBeneficiariesObservable();

    void showDuplicatePurchaseErrorMessage();
    void closeResultScreen();
    void showTandemTimeoutErrorMessage();
}