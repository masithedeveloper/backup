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

package com.barclays.absa.banking.newToBank;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankChooseAccountFragmentBinding;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

public class NewToBankChooseAccountFragment extends ExtendedFragment<NewToBankChooseAccountFragmentBinding> {

    private NewToBankView newToBankView;

    public NewToBankChooseAccountFragment() {

    }

    public static NewToBankChooseAccountFragment newInstance() {
        return new NewToBankChooseAccountFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_choose_account_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarBackTitle(getToolbarTitle());
        newToBankView.trackCurrentFragment(NewToBankConstants.INCOME_SCREEN);

        setUpIncomeList();
        initViews();
    }

    private void initViews() {
        binding.nextButton.setOnClickListener(view1 -> {
            CodesLookupDetailsSelector monthlyIncomeRangeSelector = (CodesLookupDetailsSelector) binding.incomeNormalInputView.getSelectedItem();
            newToBankView.saveMonthlyIncome(monthlyIncomeRangeSelector);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        validateFields();
    }

    @SuppressWarnings("unchecked")
    private void setUpIncomeList() {
        binding.incomeNormalInputView.setList(newToBankView.getNewToBankTempData().getMonthlyIncomeRangeList(), getString(R.string.new_to_bank_select_income_range));
        binding.incomeNormalInputView.setItemSelectionInterface(index -> validateFields());
    }

    private void validateFields() {
        if (binding.incomeNormalInputView.getSelectedIndex() != -1) {
            binding.nextButton.setEnabled(true);
        } else {
            binding.nextButton.setEnabled(false);
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_choose_account);
    }
}
