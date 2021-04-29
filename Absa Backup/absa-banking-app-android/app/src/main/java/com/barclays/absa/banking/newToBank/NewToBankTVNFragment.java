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
import com.barclays.absa.banking.databinding.NewToBankTvnFragmentBinding;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import styleguide.utils.extensions.StringExtensions;

public class NewToBankTVNFragment extends ExtendedFragment<NewToBankTvnFragmentBinding> {

    private NewToBankView newToBankView;
    private CustomerDetails customerDetails;

    public NewToBankTVNFragment() {
        // Required empty public constructor
    }

    public static NewToBankTVNFragment newInstance() {
        return new NewToBankTVNFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_tvn_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();

        customerDetails = newToBankView != null ? newToBankView.getNewToBankTempData().getCustomerDetails() : null;

        initViews();
        setupListeners();
    }

    private void initViews() {

        String maskedNumber = StringExtensions.toMaskedCellphoneNumber(customerDetails.getCellphoneNumber());

        String labelText = getString(R.string.tvn_heading_2fa_SMS, maskedNumber);

        String contentText = getString(R.string.sure_check_title_content_tvn);
        String enterCodeTitleNormalInputViewTitleText = getString(R.string.sure_check_enter_code_tvn);
        String enterCodeHintNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_tvn);
        String resendButtonTitle = getString(R.string.sure_check_resend_button_tvn);

        binding.enterCodePrimaryContentAndLabelView.setLabelText(labelText);
        binding.enterCodePrimaryContentAndLabelView.setContentText(contentText);
        binding.enterCodeNormalInputView.setTitleText(enterCodeTitleNormalInputViewTitleText);
        binding.enterCodeNormalInputView.setHintText(enterCodeHintNormalInputViewHintText);
        binding.resendButton.setText(resendButtonTitle);
    }

    private void setupListeners() {
        binding.resendButton.setOnClickListener(v -> newToBankView.resendSecurityNotification(newToBankView.getNewToBankTempData().getCustomerDetails().getCellphoneNumber()));

        binding.submitButton.setOnClickListener(v -> newToBankView.sendTVNCode(binding.enterCodeNormalInputView.getSelectedValueUnmasked()));

        binding.cancelButton.setOnClickListener(v -> {
            newToBankView.forceBack();
            newToBankView.forceBack();
        });
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_surecheck_verification);
    }
}