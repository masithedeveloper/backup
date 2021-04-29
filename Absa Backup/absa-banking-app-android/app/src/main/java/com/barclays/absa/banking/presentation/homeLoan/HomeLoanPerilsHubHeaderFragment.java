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

package com.barclays.absa.banking.presentation.homeLoan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.databinding.HomeLoanPerilsHubHeaderFragmentBinding;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.home.ui.IHomeCacheService;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.banking.transfer.TransferFundsActivity;
import com.barclays.absa.utils.FilterAccountList;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.Nullable;

public class HomeLoanPerilsHubHeaderFragment extends Fragment {

    private static final String HOME_LOAN_ACCOUNT_OBJECT = "homeLoanAccountObject";
    private static final int FLEXI_HOME_LOAN_ACCOUNT_ACCESS_BITS = 59423;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private final IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);

    public HomeLoanPerilsHubHeaderFragment() {
    }

    public static HomeLoanPerilsHubHeaderFragment newInstance() {
        HomeLoanPerilsHubHeaderFragment homeLoanPerilsHubHeaderFragment = new HomeLoanPerilsHubHeaderFragment();
        Bundle arguments = new Bundle();
        homeLoanPerilsHubHeaderFragment.setArguments(arguments);
        return homeLoanPerilsHubHeaderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        HomeLoanPerilsHubHeaderFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.home_loan_perils_hub_header_fragment, container, false);

        AccountObject homeLoanAccount = homeCacheService.getSelectedHomeLoanAccount();

        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (homeLoanAccount != null) {
            binding.outstandingAmountTextView.setText(TextFormatUtils.formatBasicAmount(homeLoanAccount.getCurrentBalance()));
            int homeLoanAccessBits = FilterAccountList.accessBitToInt(homeLoanAccount.getAccessBits());
            boolean isFlexiReserveAvailable = (homeLoanAccessBits & FLEXI_HOME_LOAN_ACCOUNT_ACCESS_BITS) == FLEXI_HOME_LOAN_ACCOUNT_ACCESS_BITS;
            if (isFlexiReserveAvailable && featureSwitchingToggles.getHomeLoanHubFlexiReserveAmountHeader() == FeatureSwitchingStates.ACTIVE.getKey()) {
                binding.availableBalanceTitleTextView.setVisibility(View.VISIBLE);
                binding.availableBalanceAmountTextView.setVisibility(View.VISIBLE);
                binding.availableBalanceAmountTextView.setText(TextFormatUtils.formatBasicAmount(homeLoanAccount.getAvailableBalance()));
            }
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog();
        }

        SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();
        if (secureHomePageObject != null && secureHomePageObject.getCustomerProfile().isTransactionalUser()) {
            binding.transferButton.setVisibility(View.VISIBLE);
            binding.transferButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TransferFundsActivity.class);
                intent.putExtra(HOME_LOAN_ACCOUNT_OBJECT, homeLoanAccount);
                startActivity(intent);
            });
        } else {
            binding.transferButton.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }
}
