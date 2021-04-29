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

package com.barclays.absa.banking.funeralCover.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.CallMeOverview;
import com.barclays.absa.banking.boundary.model.CallMeResult;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.databinding.FuneralCoverPolicyClaimDetailsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.ValidationUtils;

import java.util.Calendar;
import java.util.Date;

import styleguide.bars.CollapsingAppBarView;
import styleguide.bars.FragmentPagerItem;
import styleguide.content.Contact;
import styleguide.utils.extensions.StringExtensions;

public class InsurancePolicyClaimsBaseActivity extends BaseActivity {
    public static final String ABSA_IDIRECT_NAME = "ABSA IDIRECT";
    FuneralCoverPolicyClaimDetailsActivityBinding binding;
    CollapsingAppBarView collapsingAppBarView;
    private SparseArray<FragmentPagerItem> tabs;
    private InsurancePolicyInteractor insurancePolicyInteractor;
    public static String POLICY_DETAIL = "policyDetail";
    public static String FROM_PROPERTY_INSURANCE = "fromPropertyInsurance";
    public static PolicyType policyType;

    public ExtendedResponseListener<BeneficiaryDetailObject> beneficiaryDetailListener = new ExtendedResponseListener<BeneficiaryDetailObject>() {
        @Override
        public void onSuccess(final BeneficiaryDetailObject beneficiaryDetail) {
            dismissProgressDialog();
            if (beneficiaryDetail != null && beneficiaryDetail.getActualCellNo() != null && ValidationUtils.isValidMobileNumber(beneficiaryDetail.getActualCellNo())) {
                String contactNumber = StringExtensions.getUnFormattedPhoneNumber(beneficiaryDetail.getActualCellNo());
                final StringBuilder builder = new StringBuilder(contactNumber);
                builder.insert(3, " ");
                builder.insert(7, " ");

                dismissProgressDialog();
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .title(getString(R.string.confirm_call_me_back))
                        .message(String.format("%s\n%s", getString(R.string.absa_will_call_you_back), StringExtensions.toMaskedCellphoneNumber(builder.toString())))
                        .positiveButton(getString(R.string.ok_call_me))
                        .positiveDismissListener((dialog, which) -> insurancePolicyInteractor.invokeCallMeService(builder.toString(),
                                beneficiaryDetail.getEmail(), callMeOverviewResponseListener))
                        .negativeButton(getString(R.string.cancel))
                        .build());
            } else {
                showGenericErrorMessage();
            }
        }
    };

    private ExtendedResponseListener<CallMeResult> callMeResultListener = new ExtendedResponseListener<CallMeResult>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final CallMeResult successResponse) {
            dismissProgressDialog();
            if (Boolean.parseBoolean(successResponse.getSuccessFlag())) {
                launchSuccessScreen();
            } else {
                launchFailureScreen();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            launchFailureScreen();
        }
    };

    private void launchFailureScreen() {
        startActivity(IntentFactory.getFailureResultScreen(InsurancePolicyClaimsBaseActivity.this, R.string.call_me_registered_failure_title, String.format(getString(R.string.funeral_failure_message), getString(R.string.claim_support_number))));
    }

    private void launchSuccessScreen() {
        startActivity(IntentFactory.getSuccessResultScreen(InsurancePolicyClaimsBaseActivity.this, R.string.call_me_registered_success_title, R.string.call_me_registered_success_msg));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.funeral_cover_policy_claim_details_activity, null, false);
        setContentView(binding.getRoot());

        int contactsTabPosition = 1;
        beneficiaryDetailListener.setView(this);
        callMeResultListener.setView(this);
        insurancePolicyInteractor = new InsurancePolicyInteractor();

        final PolicyDetail policyDetail = getAppCacheService().getPolicyDetail();
        final Policy policy = policyDetail == null ? null : policyDetail.getPolicy();
        final String policyTypeValue = policy == null ? null : policy.getType();
        policyType = TextUtils.isEmpty(policyTypeValue) ? null : PolicyType.getPolicyType(policyTypeValue);
        final ContactItem contactItem = new ContactItem();

        if (policy != null) {
            setToolBarBack(StringExtensions.toTitleCaseRemovingCommas(policy.getDescription()));
        }

        tabs = new SparseArray<>();
        collapsingAppBarView = binding.claimNotificationAppBarView;
        collapsingAppBarView.addHeaderView(FuneralCoverPolicyHeaderFragment.newInstance(policyDetail));
        tabs.append(0, InsurancePolicyDetailsFragment.newInstance(getString(R.string.details), policyDetail));

        contactItem.setCallBackContactItem(new Contact(getString(R.string.let_us_call_you), getString(R.string.for_further_assistance).toLowerCase()));
        if (policyType == PolicyType.SHORT_TERM) {
            if (!TextUtils.isEmpty(policy.getDescription()) && ABSA_IDIRECT_NAME.equalsIgnoreCase(policy.getDescription())) {
                contactItem.setCallCentreContactItem(new Contact(getString(R.string.short_term_absa_i_direct_contact), getString(R.string.call_centre)));
            } else {
                contactItem.setCallCentreContactItem(new Contact(getString(R.string.short_term_hoc_contact), getString(R.string.call_centre)));
            }
        } else {
            FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
            contactItem.setCallCentreContactItem(new Contact(getString(R.string.life_policy_contact), getString(R.string.call_centre)));

            if ((policyType == PolicyType.EXERGY && BuildConfig.TOGGLE_DEF_WIMI_MANAGE_EXERGY_ENABLED && featureSwitchingToggles.getWimiManageExergy() == FeatureSwitchingStates.ACTIVE.getKey())) {
                contactsTabPosition = setUpManageTab(contactsTabPosition, policyDetail);
            }

            if (policyType == PolicyType.LONG_TERM && featureSwitchingToggles.getWimiManageBeneficiaries() == FeatureSwitchingStates.ACTIVE.getKey()) {
                contactsTabPosition = setUpManageTab(contactsTabPosition, policyDetail);
            }
        }
        tabs.append(contactsTabPosition, InsurancePolicyContactFragment.newInstance(getString(R.string.contact), contactItem, policyType));
        collapsingAppBarView.setUpTabs(this, tabs);
        collapsingAppBarView.setBackground(R.drawable.gradient_light_purple_warm_purple);
    }

    private int setUpManageTab(int contactsTabPosition, PolicyDetail policyDetail) {
        Date currentDate = Calendar.getInstance().getTime();
        Date inceptionDate = DateUtils.parseDate(policyDetail.getInceptionDate());

        if (!currentDate.before(inceptionDate)) {
            tabs.append(1, InsuranceManagePaymentDetailsFragment.newInstance(getString(R.string.manage)));
            contactsTabPosition = 2;
        }
        return contactsTabPosition;
    }

    private ExtendedResponseListener<CallMeOverview> callMeOverviewResponseListener = new ExtendedResponseListener<CallMeOverview>() {
        @Override
        public void onSuccess(CallMeOverview successResponse) {
            if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getResponseMessage())) {
                if (successResponse.getReferenceNumber() == null) {
                    successResponse.getReferenceNumber();
                }
                insurancePolicyInteractor.invokeSubmitTransactionReferenceCallMe(successResponse, callMeResultListener);
            }
        }
    };
}
