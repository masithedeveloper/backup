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

package com.barclays.absa.banking.settings.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimit;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitItem;
import com.barclays.absa.banking.databinding.ManageDigitalLimitsFragmentBinding;

public class ManageDigitalLimitsFragment extends Fragment {
    private ManageDigitalLimitsFragmentBinding binding;
    private AppCompatActivity activity;
    private ManageDigitalLimitsView manageDigitalLimitsView;

    public ManageDigitalLimitsFragment() {
    }

    public static ManageDigitalLimitsFragment newInstance() {
        return new ManageDigitalLimitsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        if (!AccessPrivileges.getInstance().isOperator()) {
            menuInflater.inflate(R.menu.text_edit_menu, menu);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            manageDigitalLimitsView.navigateToEditManageDigitalLimitsFragment();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
        manageDigitalLimitsView = (ManageDigitalLimitsView) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.manage_digital_limits_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DigitalLimitViewModel digitalLimitViewModel = new ViewModelProvider(activity).get(DigitalLimitViewModel.class);
        DigitalLimit digitalLimit = digitalLimitViewModel.getDigitalLimit();
        if (digitalLimit != null) {
            bindDataToViews(digitalLimit);
        }
    }

    private void bindDataToViews(DigitalLimit digitalLimit) {
        final DigitalLimitItem dailyPaymentLimit = digitalLimit.getDailyPaymentLimit();
        final DigitalLimitItem dailyInterAccountTransferLimit = digitalLimit.getDailyInterAccountTransferLimit();
        final DigitalLimitItem recurringPaymentTransactionLimit = digitalLimit.getRecurringPaymentTransactionLimit();
        final DigitalLimitItem futureDatedPaymentTransactionLimit = digitalLimit.getFutureDatedPaymentTransactionLimit();

        binding.dailyPaymentLimitTitleAndDescription.setTitle(dailyPaymentLimit.getActualLimit().toString());
        binding.dailyPaymentAvailableDescriptionView.setText(String.format("%s %s", dailyPaymentLimit.getAvailableLimit().toString(), getString(R.string.available_lower)));
        binding.dailyPaymentUsedDescriptionView.setText(String.format("%s %s", dailyPaymentLimit.getUsedLimit().toString(), getString(R.string.used).toLowerCase()));

        binding.dailyAccountTransferLimitTitleAndDescription.setTitle(dailyInterAccountTransferLimit.getActualLimit().toString());
        binding.dailyAccountTransferAvailableDescriptionView.setText(String.format("%s %s", dailyInterAccountTransferLimit.getAvailableLimit().toString(), getString(R.string.available_lower)));
        binding.dailyAccountTransferUsedDescriptionView.setText(String.format("%s %s", dailyInterAccountTransferLimit.getUsedLimit().toString(), getString(R.string.used).toLowerCase()));

        binding.recurringPaymentLimitTitleAndDescription.setTitle(recurringPaymentTransactionLimit.getActualLimit().toString());
        binding.futureDatedPaymentLimitTitleAndDescription.setTitle(futureDatedPaymentTransactionLimit.getActualLimit().toString());

        binding.dailyPaymentLimitProgressIndicatorView.setSteps(dailyPaymentLimit.getActualLimit().getAmountInt());
        binding.dailyPaymentLimitProgressIndicatorView.setNextStep(dailyPaymentLimit.getAvailableLimit().getAmountInt());
        binding.dailyAccountTransferLimitProgressIndicatorView.setSteps(dailyInterAccountTransferLimit.getActualLimit().getAmountInt());
        binding.dailyAccountTransferLimitProgressIndicatorView.setNextStep(dailyInterAccountTransferLimit.getAvailableLimit().getAmountInt());
    }
}