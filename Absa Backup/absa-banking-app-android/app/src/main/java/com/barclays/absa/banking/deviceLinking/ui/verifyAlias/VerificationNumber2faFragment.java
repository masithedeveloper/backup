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
package com.barclays.absa.banking.deviceLinking.ui.verifyAlias;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;

public class VerificationNumber2faFragment extends Fragment {
    private static final String TAB_POSITION = "position";
    private int tabPosition;

    public static VerificationNumber2faFragment newInstance(int position) {
        VerificationNumber2faFragment verificationNumberFragment = new VerificationNumber2faFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAB_POSITION, position);
        verificationNumberFragment.setArguments(bundle);
        return verificationNumberFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabPosition = getArguments().getInt(TAB_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2fa_verification_number, container, false);
        TextInputLayout etVerificationType = (TextInputLayout) rootView.findViewById(R.id.til_verificationType);
        TextInputEditText etVerificationNumber = (TextInputEditText) rootView.findViewById(R.id.et_verificationNumber);

        final int ID_TAB = 0;
        final int PASSPORT_TAB = 1;

        switch (tabPosition) {
            case ID_TAB:
                etVerificationType.setHint(getString(R.string.id_number));
                etVerificationNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;

            case PASSPORT_TAB:
                etVerificationType.setHint(getString(R.string.passport_number));
                break;

            default:
                break;
        }
        return rootView;
    }
}
