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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.OverdraftTermsAndConditionsFragmentBinding;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.utils.AnalyticsUtil;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftTermsAndConditionsFragment extends OverdraftBaseFragment<OverdraftTermsAndConditionsFragmentBinding>
        implements View.OnClickListener {

    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    public static OverdraftTermsAndConditionsFragment newInstance() {
        return new OverdraftTermsAndConditionsFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_terms_and_conditions_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);
        setToolBar(getString(R.string.overdraft_term_and_conditions_amp), v -> getParentActivity().onBackPressed());

        boolean hasCreditProtection = overdraftCacheService.getOverdraftSetup().getCreditProtection();
        if (hasCreditProtection) {
            binding.overdraftCreditProtectionTermsAndConditionsView.setVisibility(View.VISIBLE);
        }
        binding.nextButton.setOnClickListener(this);
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TermsAndConditionsScreen_ScreenDisplayed");
    }

    @Override
    public void onClick(View v) {
        if (!binding.termsAndConditionsCheckBox.getIsValid()) {
            animate(binding.termsAndConditionsCheckBox, R.anim.shake);
            binding.termsAndConditionsCheckBox.setErrorMessage(getString(R.string.overdraft_terms_and_conditions_not_accepted));
        } else {
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TermsAndConditionsScreen_NextButtonClicked");
            show(OverdraftSummaryConfirmationFragment.newInstance());
        }
    }
}
