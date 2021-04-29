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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftAccountObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.databinding.OverdraftSetupFragmentBinding;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.banking.overdraft.ui.OverdraftStepsActivity;
import com.barclays.absa.banking.presentation.termsAndConditions.BaseTermsAndConditionsActivity;
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.NetworkUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.lang.ref.WeakReference;
import java.util.Locale;

import styleguide.forms.ListItemDisplayDialogFragment;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftSetupFragment extends OverdraftBaseFragment<OverdraftSetupFragmentBinding>
        implements View.OnClickListener, OverdraftContracts.OverdraftSetupView {
    private static boolean hasCreditProtectionPlan;
    private double maximumAmount, minimumAmount, sliderMaximumAmount;
    private String selectAccountNumber, noticeMethod;
    private int maxProgressIndicator, overdraftAmount;
    private OverdraftSetupPresenter presenter;

    private boolean isEditTextChange = false;
    private boolean isTracking = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_setup_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar(getString(R.string.overdraft_setup), v -> getParentActivity().onBackPressed());

        presenter = new OverdraftSetupPresenter(new WeakReference<>(this));
        binding.nextButton.setOnClickListener(this);
        if (getActivity() != null) {
            ((OverdraftStepsActivity) getActivity()).setStep(3);
        }
        initViews();
        setUpSliderView();
        populateViews();
        setUpCreditProtectionOptionsRadioGroup();
        setUpCreditAgreementNoticeMethodRadioGroup();
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_ScreenDisplayed");
    }

    private void initViews() {
        minimumAmount = 500;
        maximumAmount = ((OverdraftStepsActivity) requireActivity()).getOverdraftMaximumAmount();
        overdraftAmount = (int) maximumAmount;

        sliderMaximumAmount = maximumAmount - 500;
        maxProgressIndicator = 5;
        binding.desiredOverdraftAmountView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.desiredOverdraftAmountView.setOnClickListener(v -> binding.desiredOverdraftAmountView.getEditText().requestFocus());

        binding.desiredOverdraftAmountView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String amountEntered = editable.toString().replace("R", "").replace(",", "").replace(" ", "").replace("-", "");
                double amountValue;
                if (!amountEntered.isEmpty()) {
                    amountValue = Double.parseDouble(amountEntered);
                } else {
                    amountValue = 0;
                }
                if (amountEntered.isEmpty() || amountValue < minimumAmount || amountValue > maximumAmount) {
                    binding.desiredOverdraftAmountView.setError(getString(R.string.overdraft_enter_correct_amount));
                    binding.desiredOverdraftAmountView.showError(true);
                } else {
                    binding.desiredOverdraftAmountView.showError(false);
                    binding.overdraftLimitSlider.setProgress(updateSliderProgressPosition(amountEntered));
                    overdraftAmount = (int) amountValue;
                }
            }
        });

        CommonUtils.makeTextClickable(getActivity(),
                R.string.overdraft_i_have_read_and_i_accept_the_terms_and_conditions,
                getString(R.string.terms_and_conditions),
                binding.acceptTermsAndConditionsCheckBox.getCheckBoxTextView(), R.color.color_FF666666, new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        showCreditProtectionTermsAndConditions();
                    }
                });
    }

    private void populateViews() {
        String maxAmountString = String.format(Locale.ENGLISH, "%.2f", maximumAmount);
        binding.desiredOverdraftAmountView.setSelectedValue(maxAmountString);
    }

    private void setUpSliderView() {
        binding.overdraftLimitSlider.setMax(maxProgressIndicator);
        binding.overdraftLimitSlider.setProgress(maxProgressIndicator);
        binding.overdraftLimitSlider.setStartText(getString(R.string.overdraft_minimum_amount, TextFormatUtils.formatBasicAmountAsRand((int) minimumAmount)));
        binding.overdraftLimitSlider.setEndText(getString(R.string.overdraft_maximum_amount, TextFormatUtils.formatBasicAmountAsRand((int) maximumAmount)));
        binding.overdraftLimitSlider.getSeekbar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isTracking && isEditTextChange) {
                    isEditTextChange = false;
                    return;
                }
                double selectedAmount = ((double) progress / (double) maxProgressIndicator) * sliderMaximumAmount;
                double overdraftNewAmount = selectedAmount + minimumAmount;

                binding.desiredOverdraftAmountView.setSelectedValue(new Amount(String.valueOf(overdraftNewAmount)).toString());
                getParentActivity().setOverdraftSelectedAmount(overdraftNewAmount);
                overdraftAmount = (int) overdraftNewAmount;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                clearEditFocus();
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false;
            }
        });
    }

    private int updateSliderProgressPosition(String value) {
        isEditTextChange = true;
        double decimalPercentage;
        if (value.isEmpty()) {
            decimalPercentage = 0.0;
        } else {
            decimalPercentage = (Double.parseDouble(value) - 500) / sliderMaximumAmount;
            getParentActivity().setOverdraftSelectedAmount(Double.parseDouble(value));
        }
        return (int) (decimalPercentage * maxProgressIndicator);
    }

    @Override
    public void populateAccountList(@NonNull SelectorList<OverdraftAccountObject> accounts) {
        if (isAdded()) {
            binding.chequeAccountSelector.setList(accounts, getString(R.string.please_select_account));
            binding.chequeAccountSelector.setItemSelectionInterface(index -> {
                OverdraftAccountObject accountObjectWrapper = accounts.get(index);
                if (accountObjectWrapper.getAccountObject() != null && accountObjectWrapper.getAccountObject().getAccountNumber() != null) {
                    selectAccountNumber = accountObjectWrapper.getAccountObject().getAccountNumber();
                }
            });

            binding.chequeAccountSelector.setSelectedIndex(0);
            OverdraftAccountObject selectedAccount = (OverdraftAccountObject) binding.chequeAccountSelector.getSelectedItem();
            if (selectedAccount != null && selectedAccount.getAccountObject() != null) {
                selectAccountNumber = selectedAccount.getAccountObject().getAccountNumber();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            OverdraftAccountObject accountObjectWrapper = (OverdraftAccountObject) data.getSerializableExtra(ListItemDisplayDialogFragment.SELECTED_ITEM);
            if (accountObjectWrapper != null && accountObjectWrapper.getAccountObject() != null && accountObjectWrapper.getAccountObject().getAccountNumber() != null) {
                selectAccountNumber = accountObjectWrapper.getAccountObject().getAccountNumber();
            }
        }
    }

    @Override
    public void setChequeAccount(@NonNull String accountNumberAndDescription, @NonNull String accountNumber, @NonNull String availableBalance) {
        binding.chequeAccountSelector.setSelectedValue(accountNumberAndDescription);
        binding.availableBalanceTextView.setText(availableBalance.concat(" " + getString(R.string.available_lower)));
        selectAccountNumber = accountNumber;
    }

    private void setUpCreditProtectionOptionsRadioGroup() {
        hasCreditProtectionPlan = false;
        SelectorList<StringItem> creditProtectionOptions = new SelectorList<>();
        creditProtectionOptions.add(new StringItem(getString(R.string.yes)));
        creditProtectionOptions.add(new StringItem(getString(R.string.no)));
        binding.creditProtectionRadioGroup.setDataSource(creditProtectionOptions);
        binding.creditAgreementRadioGroup.setIsRequired(true);
        binding.creditProtectionRadioGroup.setItemCheckedInterface(index -> {
            clearEditFocus();
            binding.creditProtectionRadioGroup.hideError();
            if (index == 0) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_YesCreditPlanRadioButtonChecked");
                hasCreditProtectionPlan = true;
                binding.acceptTermsAndConditionsCheckBox.setVisibility(View.VISIBLE);
                binding.acceptTermsAndConditionsCheckBox.animate().setDuration(500).scaleY(1).start();
            } else if (index == 1) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_NoCreditPlanRadioButtonChecked");
                hasCreditProtectionPlan = false;
                binding.acceptTermsAndConditionsCheckBox.animate().setDuration(100).scaleY(0).start();
                binding.acceptTermsAndConditionsCheckBox.setVisibility(View.GONE);
            }
        });
    }

    private void setUpCreditAgreementNoticeMethodRadioGroup() {
        SelectorList<StringItem> creditAgreementNoticeMethod = new SelectorList<>();
        creditAgreementNoticeMethod.add(new StringItem(getString(R.string.overdraft_registered_mail)));
        creditAgreementNoticeMethod.add(new StringItem(getString(R.string.overdraft_hand_delivered)));
        binding.creditAgreementRadioGroup.setDataSource(creditAgreementNoticeMethod);
        binding.creditAgreementRadioGroup.setIsRequired(true);
        binding.creditAgreementRadioGroup.setItemCheckedInterface(index -> {
            clearEditFocus();
            binding.creditAgreementRadioGroup.hideError();
            if (index == 0) {
                noticeMethod = "1";
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_PostalAddressRadioButtonChecked");
            } else if (index == 1) {
                noticeMethod = "2";
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_PhysicalAddressRadioButtonChecked");
            }
        });
    }

    private void clearEditFocus() {
        binding.desiredOverdraftAmountView.getEditText().clearFocus();
        KeyboardUtils.hideSoftKeyboard(binding.desiredOverdraftAmountView.getEditText());
    }

    @Override
    public void onClick(View view) {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdarftSetUpScreen_NextButtonClicked");
        clearEditFocus();
        validateInputOnView();
    }

    @Override
    public void validateInputOnView() {
        if (binding.chequeAccountSelector.getSelectedValue().isEmpty()) {
            animate(binding.chequeAccountSelector, R.anim.shake);
            binding.chequeAccountSelector.setError(R.string.please_select_account);
            binding.chequeAccountSelector.requestFocus();
        } else if (binding.creditProtectionRadioGroup.getSelectedValue() == null) {
            animate(binding.creditProtectionRadioGroup, R.anim.shake);
            binding.creditProtectionRadioGroup.setErrorMessage(getString(R.string.overdraft_select_option));
        } else if (binding.creditAgreementRadioGroup.getSelectedValue() == null) {
            animate(binding.creditAgreementRadioGroup, R.anim.shake);
            binding.creditAgreementRadioGroup.setErrorMessage(getString(R.string.overdraft_select_option));
        } else if (binding.creditProtectionRadioGroup.getSelectedValue() != null && hasCreditProtectionPlan) {
            if (!binding.acceptTermsAndConditionsCheckBox.getIsValid()) {
                animate(binding.acceptTermsAndConditionsCheckBox, R.anim.shake);
                binding.acceptTermsAndConditionsCheckBox.setErrorMessage(getString(R.string.overdraft_terms_and_conditions_not_accepted));
            } else {
                fetchMarketingConsentMethods();
            }
        } else {
            fetchMarketingConsentMethods();
        }
    }

    private void fetchMarketingConsentMethods() {
        presenter.fetchMarketingConsentMethods();
    }

    private void showCreditProtectionTermsAndConditions() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_TermsAndConditionsCheckBoxChecked");
        if (NetworkUtils.INSTANCE.isNetworkConnected()) {
            Intent termsAndConditionsIntent = new Intent(getActivity(), BaseTermsAndConditionsActivity.class);
            termsAndConditionsIntent.putExtra(BaseTermsAndConditionsActivity.link, "https://e.absa.co.za/dspt/static/pdf/Absa%205459%20EX.pdf");
            startActivity(termsAndConditionsIntent);
        }
    }

    @Override
    public void setMarketingResponse(@NonNull PersonalInformationResponse overdraftMarketingResponse) {
        OverdraftSetup overdraftSetup = new OverdraftSetup(selectAccountNumber, String.valueOf(overdraftAmount));
        overdraftSetup.setAccountNumberAndDescription(binding.chequeAccountSelector.getSelectedValue());
        overdraftSetup.setCreditAgreementNoticeMethod(noticeMethod);
        overdraftSetup.setCreditProtection(hasCreditProtectionPlan);
        show(OverdraftCheckSetupAndConfirmFragment.newInstance(overdraftSetup));
    }

    public static OverdraftSetupFragment newInstance() {
        return new OverdraftSetupFragment();
    }
}