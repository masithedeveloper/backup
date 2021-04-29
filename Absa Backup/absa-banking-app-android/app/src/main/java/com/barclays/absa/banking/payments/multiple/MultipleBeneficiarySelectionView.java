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
package com.barclays.absa.banking.payments.multiple;

import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.framework.BaseView;

import java.util.List;

public interface MultipleBeneficiarySelectionView extends BaseView {
    BeneficiaryObject getSectionListBeneficiary(int selectedPosition);

    List<BeneficiaryObject> getSelectedBeneficiaries();

    void onBeneficiaryClicked(int adapterPosition);

    void notifyOnItemSelection();

    void notifyOnItemDeselection(int position);

    void openFromAccountChooserActivity();

    void navigateToChooseAccountScreen();

    void showNoBeneficiaryContainer();

    void showBeneficiaryContainer();

    void autoPopulateSingleAccount(ClientAgreementDetails successResponse, AccountObject accountObject);

    boolean isBusinessAccount();

    void toggleContinueButton(boolean enable);

    void onBeneficiaryListFiltered(List<BeneficiaryObject> paymentBeneficiaryList);

    void stopSearch();
}