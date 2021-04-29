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

package com.barclays.absa.banking.home.ui;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.Entry;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.framework.BaseView;

import java.util.List;

public interface HomeContainerView extends BaseView {
    void dismissProgressIndicator();

    void dismissProgressDialog();

    boolean isShowFuneralCoverOffer();

    void requestHomeloanAccountHistory(AccountObject accountObject);

    void creditCardRequest(AccountObject accountObject);

    void navigateToAccountInformation(AccountObject accountObject);

    void navigateToMultipleCiaAccountInformation();

    void showErrorDialog(String errorContent);

    void onCreditCardInformationFetched(AccountObject accountObject);

    List<Entry> getAccounts();

    List<Entry> getCiaAccounts();

    void onPolicyCardInformation(Policy policy);

    void navigateToCallMeBackFragment(String description, String uniqueRef);

    void navigateToCallMeBackSuccessScreen();

    void navigateToCallMeBackFailureScreen();

    void navigateToReportFraudFragment();

    void onHomeLoanAccountCardClicked(AccountObject accountObject);

    void openPolicyDetailsScreen(PolicyDetail policyDetail);

    void onAbsaRewardsCardClicked();

    void showSomethingWentWrongScreen();

    void onQuickLinkDataReceived();

    void navigateToHomeLoanAccountHub(AccountDetail homeLoanAccountHistoryCleared);

    void onUnitTrustCardClicked(AccountObject accountObject1);

    boolean isExploreHubDisabled();

    void navigateToFixedDepositHub(AccountObject accountObject);

    void onAdvantageCardClicked();

    void onInsuranceClusterClicked(AccountObject accountObject);

    void onInvestmentClusterClicked(AccountObject accountObject);
}
