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

package com.barclays.absa.banking.cashSend.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.UnredeemPinChangeDialogfragmentBinding;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.ValidationUtils;

import static com.barclays.absa.banking.framework.app.BMBConstants.ATM_ACCESS_PIN_LENGTH;

@Deprecated
public class UnredeemPinChangeDialogFragment extends DialogFragment {

    private UnredeemPinChangeDialogfragmentBinding binding;
    private boolean sendSMS = false;

    public interface OnPinChangedListener {
        void onPinChanged(String newPin, boolean sensSMS);
    }

    public UnredeemPinChangeDialogFragment() {
        // Required empty public constructor
    }

    public static UnredeemPinChangeDialogFragment newInstance(String title, boolean sendSMS) {

        UnredeemPinChangeDialogFragment fragment = new UnredeemPinChangeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putBoolean("sendSMS", sendSMS);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = UnredeemPinChangeDialogfragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = getArguments().getString("title");
        this.sendSMS = getArguments().getBoolean("sendSMS");
        getDialog().setTitle(title);
        binding.unredeemedPinDialogNormalInputView.requestFocus();
        binding.unredeemedPinDialogPositiveTextView.setOnClickListener(v -> validateInput());
        final Window window = getDialog().getWindow();

        binding.unredeemedPinDialogNegativeTextView.setOnClickListener(v -> {
            KeyboardUtils.hideSoftKeyboard(UnredeemPinChangeDialogFragment.this.getView());
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
            getDialog().dismiss();
        });
    }

    private void validateInput() {
        if (ValidationUtils.validateATMPin(binding.unredeemedPinDialogNormalInputView, getString(R.string.unredeemed_atm_access_pin), ATM_ACCESS_PIN_LENGTH)) {
            UnredeemedDetailsActivity activity = (UnredeemedDetailsActivity) getActivity();
            if (activity != null) {
                activity.onPinChanged(binding.unredeemedPinDialogNormalInputView.getText().toString(), sendSMS);
            }
            this.dismiss();
        } else {
            binding.unredeemedPinDialogNormalInputView.setError(getResources().getString(R.string.calcHelp));
        }
    }
}
