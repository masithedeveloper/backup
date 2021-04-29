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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.PrepaidElectricityImportantInformationFragmentBinding;
import com.barclays.absa.utils.AnalyticsUtil;

public class PrepaidElectricityImportantInformationFragment extends Fragment {

    public PrepaidElectricityImportantInformationFragment() {

    }

    public static PrepaidElectricityImportantInformationFragment newInstance() {
        return new PrepaidElectricityImportantInformationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PrepaidElectricityImportantInformationFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.prepaid_electricity_important_information_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnalyticsUtil.INSTANCE.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_ImportantInformationScreen_ScreenDisplayed");

        PrepaidElectricityView prepaidElectricityView = (PrepaidElectricityView) getActivity();
        if (prepaidElectricityView != null) {
            prepaidElectricityView.setToolbarTitle("", v -> getActivity().getSupportFragmentManager().popBackStack());
            prepaidElectricityView.setToolbarIcon(R.drawable.ic_close);
        }
    }
}
