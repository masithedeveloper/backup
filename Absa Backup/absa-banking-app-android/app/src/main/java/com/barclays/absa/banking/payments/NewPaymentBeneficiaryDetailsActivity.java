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
package com.barclays.absa.banking.payments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse;
import com.barclays.absa.banking.boundary.model.AccountType;
import com.barclays.absa.banking.databinding.ActivityNewBeneficiaryDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity;
import com.barclays.absa.utils.KeyboardUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.Observable;

import styleguide.bars.FragmentPagerItem;
import styleguide.bars.TabPager;

import static com.barclays.absa.banking.payments.PaymentsConstants.ONCE_OFF;

public class NewPaymentBeneficiaryDetailsActivity extends BaseActivity implements NewPaymentBeneficiaryDetailsView {

    private boolean isOnceOffPayment;
    private PaySomeoneFragment paySomeoneFragment;
    private PayBillFragment payBillFragment;
    private ActivityNewBeneficiaryDetailsBinding binding;
    private ViewModel viewModel = new ViewModel();
    private OcrResponse ocrResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_new_beneficiary_details, null, false);
        setContentView(binding.getRoot());

        setToolbar();

        Intent callingIntent = getIntent();
        if (callingIntent != null) {
            if (callingIntent.hasExtra(ONCE_OFF)) {
                isOnceOffPayment = callingIntent.getBooleanExtra(ONCE_OFF, false);
            }
            if (callingIntent.hasExtra(BeneficiaryImportBaseActivity.ocrResult)) {
                ocrResponse = callingIntent.getParcelableExtra(BeneficiaryImportBaseActivity.ocrResult);
            }
        }
        initTabView();
        getDeviceProfilingInteractor().notifyAddBeneficiary();
    }

    protected void initTabView() {
        BMBLogger.d("x-y", "parent.initTabView");
        SparseArray<FragmentPagerItem> tabs = new SparseArray<>();
        paySomeoneFragment = PaySomeoneFragment.newInstance(getString(R.string.title_private_tab), isOnceOffPayment, ocrResponse);
        tabs.append(0, paySomeoneFragment);
        payBillFragment = PayBillFragment.newInstance(getString(R.string.title_public_tab), isOnceOffPayment);
        tabs.append(1, payBillFragment);
        binding.tabBarView.addTabs(tabs);

        TabPager adapter = new TabPager(getSupportFragmentManager(), tabs);
        binding.viewPager.setAdapter(adapter);

        binding.tabBarView.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager));
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabBarView));
    }

    private void setToolbar() {
        final View toolbar = binding.toolbar.toolbar;
        if (toolbar != null) {
            toolbar.setBackgroundResource(R.drawable.gradient_light_purple_warm_purple);
            setToolBarBack(R.string.beneficiary_details);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyboardUtils.hideSoftKeyboard(findViewById(android.R.id.content));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PaySomeoneFragment.REQUEST_CODE_FOR_BANK_NAME:
                    getViewModel().onActivityResult(requestCode, data.getStringExtra(PaymentsConstants.BANK_NAME));
                    break;
                case PaySomeoneFragment.REQUEST_CODE_FOR_BRANCH_NAME:
                    getViewModel().onActivityResult(requestCode, data.getStringExtra(PaymentsConstants.BRANCH_NAME), data.getStringExtra(PaymentsConstants.BRANCH_CODE), data.getStringExtra(PaymentsConstants.IIP_STATUS));
                    break;
                case PaySomeoneFragment.REQUEST_CODE_FOR_ACCOUNT_TYPE:
                    getViewModel().onActivityResult(requestCode, data.getParcelableExtra(PaymentsConstants.ACCOUNT_TYPE));
                    break;
                case NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE:
                    paySomeoneFragment.onActivityResult(requestCode, resultCode, data);
                    break;
            }

            if (binding.tabBarView.getSelectedTabPosition() != 0) {
                if (payBillFragment != null) {
                    payBillFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onOfferButtonClicked() {
        startActivity(new Intent(this, BeneficiaryImportExplanationActivity.class));
    }

    @Override
    public void navigateToBeneficiaryImportScreen() {
        startActivity(new Intent(this, BeneficiaryImportActivity.class));
    }
}

class ViewModel extends Observable {

    public void onActivityResult(int requestCode, Object... values) {
        setChanged();
        if (requestCode == PaySomeoneFragment.REQUEST_CODE_FOR_ACCOUNT_TYPE) {
            final Pair<Integer, AccountType[]> resultPair = new Pair<>(requestCode, new AccountType[]{(AccountType) values[0]});
            notifyObservers(resultPair);
        } else {
            final Pair<Integer, Object[]> resultPair = new Pair<>(requestCode, values);
            notifyObservers(resultPair);
        }
        clearChanged();
    }
}
