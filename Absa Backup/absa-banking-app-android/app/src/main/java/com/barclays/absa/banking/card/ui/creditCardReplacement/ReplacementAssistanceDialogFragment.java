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
package com.barclays.absa.banking.card.ui.creditCardReplacement;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.CreditCardReplacementAssistanceDialogFragmentBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;

public class ReplacementAssistanceDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String INTERNATIONAL_NUMBER_DIAL = "International number dial";
    public static final String LOCAL_NUMBER_DIAL = "Local number dial";
    public static final String CALL_SCREEN = "Call screen";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        CreditCardReplacementAssistanceDialogFragmentBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.credit_card_replacement_assistance_dialog_fragment, container,
                false);
        binding.tvInternationalNumber.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.tvLocalNumber.setOnClickListener(this);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        ((BaseActivity) getActivity()).trackScreenView(ConfirmCreditCardReplacementActivity.STOP_AND_REPLACE_CHANNEL, CALL_SCREEN);
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_international_number:
                ((BaseActivity) getActivity()).trackButtonClick(INTERNATIONAL_NUMBER_DIAL);
                TelephoneUtil.callStopAndReplaceCreditCardInternationalNumber(getActivity());
                break;
            case R.id.tv_local_number:
                ((BaseActivity) getActivity()).trackButtonClick(LOCAL_NUMBER_DIAL);
                TelephoneUtil.callStopAndReplaceCreditCardLocalNumber(getActivity());
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}
