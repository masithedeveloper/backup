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
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.OverdraftDeclarationFragmentBinding;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.banking.overdraft.ui.OverdraftStepsActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftDeclarationFragment extends OverdraftBaseFragment<OverdraftDeclarationFragmentBinding> implements View.OnClickListener, OverdraftContracts.DeclarationView {
    private OverdraftDeclarationPresenter presenter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_declaration_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new OverdraftDeclarationPresenter(new WeakReference<>(this));
        setToolBar(getString(R.string.declaration), v -> getParentActivity().onBackPressed());
        binding.nextButton.setOnClickListener(this);

        CommonUtils.makeTextClickable(getActivity(),
                R.string.overdraft_declaration_content_3,
                getString(R.string.overdraft_personal_client_agreement),
                binding.declarationConsentThree.getCaptionTextView(), R.color.color_FF666666, new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        showPersonalClientAgreement();
                    }
                });
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_DeclarationScreen_ScreenDisplayed");

        binding.acceptDeclarationCheckbox.setOnCheckedListener(isChecked -> {
            if (isChecked) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_DeclarationScreen_DeclarationCheckBoxChecked");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((OverdraftStepsActivity) getActivity()).setStep(2);
        }
    }

    public static OverdraftDeclarationFragment newInstance() {
        return new OverdraftDeclarationFragment();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextButton) {
            if (!binding.acceptDeclarationCheckbox.getIsValid()) {
                animate(binding.acceptDeclarationCheckbox, R.anim.shake);
                binding.acceptDeclarationCheckbox.setErrorMessage(getString(R.string.overdraft_terms_and_conditions_not_accepted));
            } else {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_DeclarationScreen_NextButtonClicked");
                presenter.onNextButtonClicked();
            }
        }
    }

    @Override
    public void navigateToNextScreen() {
        show(OverdraftSetupFragment.newInstance());
    }

    @Override
    public void navigateToFailureScreen(@NonNull String message) {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_Errorscreen_UnableToContinue");
        startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.sorry_title, message));
    }
}