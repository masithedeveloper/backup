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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.ManageCardConfirmLimit;
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.card.ui.creditCard.vcl.CreditCardVCLBaseActivity;
import com.barclays.absa.banking.databinding.SettingsCardLimitsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity.IS_FROM_CARD_HUB;
import static com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.MANAGE_CARD_ITEM;
import static com.barclays.absa.banking.card.ui.creditCard.vcl.CreditCardVCLBaseActivity.VCL_DATA;

public class SettingsCardLimitsActivity extends BaseActivity implements SettingsCardLimitsView {
    public static final String CREDIT = "Credit";
    public static final String BASE_AMOUNT = "0.00";
    public static final String CREDIT_CARD = "CC";
    public static final int MAX_PROGRESS_VALUE = 100;
    public static final int MIN_PROGRESS_VALUE = 0;

    private String currentPOSDailyLimit;
    private String currentATMDailyLimit;
    private String maxPOSDailyLimit;
    private String maxATMDailyLimit;
    private double maxAtmAmount;
    private double maxPosAmount;
    private boolean isSelfChange;
    private boolean isFromCreditCardHub;

    private SettingsChangeCardLimitPresenter presenter;
    private SettingsCardLimitsActivityBinding binding;
    private ManageCardLimitDetails cardLimitDetails;
    private ManageCardResponseObject.ManageCardItem manageCardItem;
    private NumberFormat numberFormating = new DecimalFormat("##.##");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.settings_card_limits_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.card_limits_toolbar_title));
        presenter = new SettingsChangeCardLimitPresenter(this, new CreditCardInteractor());
        manageCardItem = (ManageCardResponseObject.ManageCardItem) getIntent().getSerializableExtra(MANAGE_CARD_ITEM);
        initViews(manageCardItem.convertItemToCardLimitDetails());
        isFromCreditCardHub = getIntent().getBooleanExtra(IS_FROM_CARD_HUB, false);

        performAnalytics(MANAGE_CARD_LIMITS_OVERVIEW_CONST, SETTINGS_CONST);
    }

    @Override
    public void initViews(CreditCardInformation successResponse, ManageCardLimitDetails cardLimitDetails) {
        this.cardLimitDetails = cardLimitDetails;
        initViews(cardLimitDetails);
        binding.newLimitPrimaryContentAndLabelView.setContentText(TextFormatUtils.spaceFormattedAmount("R", successResponse.getAccount() != null ? successResponse.getAccount().getCreditLimit() : "0"));
    }

    @Override
    public void navigateToConfirmationScreen(ManageCardConfirmLimit manageCardConfirmLimit) {
        Intent settingsChangeCardLimitConfirmActivityIntent = new Intent(SettingsCardLimitsActivity.this, SettingsChangeCardLimitConfirmActivity.class);
        settingsChangeCardLimitConfirmActivityIntent.putExtra(AppConstants.RESULT, manageCardConfirmLimit);
        settingsChangeCardLimitConfirmActivityIntent.putExtra(MANAGE_CARD_ITEM, (Parcelable) manageCardItem);
        settingsChangeCardLimitConfirmActivityIntent.putExtra(IS_FROM_CARD_HUB, isFromCreditCardHub);
        settingsChangeCardLimitConfirmActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(settingsChangeCardLimitConfirmActivityIntent);
    }

    @Override
    public void saveLimits(String currentATMDailyLimit, String currentPOSDailyLimit) {
        mScreenName = MANAGE_PAYMENT_TRANSFER_LIMITS_EDIT;
        mSiteSection = SETTINGS_CONST;
        trackScreenView(MANAGE_PAYMENT_TRANSFER_LIMITS_EDIT, SETTINGS_CONST);
        presenter.saveLimitSelected(cardLimitDetails, currentPOSDailyLimit, currentATMDailyLimit);
    }

    @Override
    public void initViews(ManageCardLimitDetails cardLimitDetails) {
        this.cardLimitDetails = cardLimitDetails;
        populateViews();

        if (cardLimitDetails != null) {
            String cardType = (CREDIT_CARD.equalsIgnoreCase(cardLimitDetails.getCardType()) || getString(R.string.credit_card_type).equalsIgnoreCase(cardLimitDetails.getCardType()) || getString(R.string.credit_card).equalsIgnoreCase(cardLimitDetails.getCardType())) ?
                    StringExtensions.toTitleCase(getString(R.string.credit_card)) : getString(R.string.debit_card);
            if (getString(R.string.credit_card).equalsIgnoreCase(cardType)) {
                manageCreditCardLimit();
            }
        } else {
            finish();
        }

        if (manageCardItem != null && manageCardItem.getCurrentCreditCardLimit() != null && !manageCardItem.getCurrentCreditCardLimit().isEmpty()) {
            binding.newLimitPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(manageCardItem.getCurrentCreditCardLimit() != null ? manageCardItem.getCurrentCreditCardLimit() : "0"));
        } else {
            binding.newLimitPrimaryContentAndLabelView.setVisibility(View.GONE);
            binding.overallCreditLimitDivider.setVisibility(View.GONE);
        }
        performFeatureCheck();
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if ((featureSwitchingToggles.getCreditCardVCL() == FeatureSwitchingStates.ACTIVE.getKey()) && CREDIT_CARD.equalsIgnoreCase(manageCardItem.convertItemToCardLimitDetails().getCardType()) && manageCardItem.getVclData() != null && manageCardItem.getVclData().getNewCreditLimitAmount() != null && Double.parseDouble(manageCardItem.getVclData().getNewCreditLimitAmount()) > 0) {
            binding.increaseLimitButton.setVisibility(View.VISIBLE);
            binding.overallCreditLimitDivider.setVisibility(View.VISIBLE);
        }

        binding.atmWithdrawalLimitNormalInputView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.atmWithdrawalLimitNormalInputView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.atmWithdrawalLimitNormalInputView.showError(false);
                String amountEntered = StringExtensions.removeCurrency(editable.toString());
                double amountValue;
                if (!amountEntered.isEmpty()) {
                    amountValue = Double.parseDouble(amountEntered);
                } else {
                    amountValue = 0;
                }

                isSelfChange = true;
                binding.atmWithdrawalLimitSlider.setProgress(calculatePercentageBasedOnInput(amountEntered, maxAtmAmount));

                if (amountValue > maxAtmAmount) {
                    binding.atmWithdrawalLimitNormalInputView.setError(getString(R.string.manage_card_limit_more_error));
                    binding.atmWithdrawalLimitNormalInputView.showError(true);
                    binding.saveButton.setEnabled(false);
                } else if (amountEntered.isEmpty()) {
                    binding.atmWithdrawalLimitNormalInputView.showError(true);
                    binding.atmWithdrawalLimitNormalInputView.setError(getString(R.string.manage_card_limit_empty_error));
                    binding.saveButton.setEnabled(false);
                } else {
                    binding.atmWithdrawalLimitNormalInputView.showError(false);
                    binding.saveButton.setEnabled(true);
                }
            }
        });

        binding.pointOfSaleDailyLimitNormalInputView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.pointOfSaleDailyLimitNormalInputView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.pointOfSaleDailyLimitNormalInputView.showError(false);
                String amountEntered = StringExtensions.removeCurrency(editable.toString());
                double amountValue;
                if (!amountEntered.isEmpty()) {
                    amountValue = Double.parseDouble(amountEntered);
                } else {
                    amountValue = 0;
                }

                isSelfChange = true;
                binding.pointOfSaleDailyLimitSlider.setProgress(calculatePercentageBasedOnInput(amountEntered, maxPosAmount));

                if (amountValue > maxPosAmount) {
                    binding.pointOfSaleDailyLimitNormalInputView.setError(getString(R.string.manage_card_limit_more_error));
                    binding.pointOfSaleDailyLimitNormalInputView.showError(true);
                    binding.saveButton.setEnabled(false);
                } else if (amountEntered.isEmpty()) {
                    binding.pointOfSaleDailyLimitNormalInputView.showError(true);
                    binding.pointOfSaleDailyLimitNormalInputView.setError(getString(R.string.manage_card_limit_empty_error));
                    binding.saveButton.setEnabled(false);
                } else {
                    binding.pointOfSaleDailyLimitNormalInputView.showError(false);
                    binding.saveButton.setEnabled(true);
                }
            }
        });

        binding.atmWithdrawalLimitSlider.getSeekbar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double creditLimitIncrement = ((double) progress / (double) MAX_PROGRESS_VALUE) * maxAtmAmount;
                if (!isSelfChange) {
                    binding.atmWithdrawalLimitNormalInputView.setSelectedValue(StringExtensions.toDoubleString(numberFormating.format(creditLimitIncrement)));
                } else {
                    isSelfChange = false;
                }

                if (progress == MIN_PROGRESS_VALUE) {
                    binding.atmWithdrawalLimitNormalInputView.setSelectedValue("0");
                } else if (progress == MAX_PROGRESS_VALUE) {
                    binding.atmWithdrawalLimitNormalInputView.setSelectedValue(StringExtensions.toDoubleString(maxAtmAmount + ""));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                isSelfChange = false;
                KeyboardUtils.hideKeyboard(SettingsCardLimitsActivity.this);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.pointOfSaleDailyLimitSlider.getSeekbar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double creditLimitIncrement = ((double) progress / (double) MAX_PROGRESS_VALUE) * maxPosAmount;
                if (!isSelfChange) {
                    binding.pointOfSaleDailyLimitNormalInputView.setSelectedValue(StringExtensions.toDoubleString(numberFormating.format(creditLimitIncrement)));
                } else {
                    isSelfChange = false;
                }

                if (progress == MIN_PROGRESS_VALUE) {
                    binding.pointOfSaleDailyLimitNormalInputView.setSelectedValue("0");
                } else if (progress == MAX_PROGRESS_VALUE) {
                    binding.pointOfSaleDailyLimitNormalInputView.setSelectedValue(StringExtensions.toDoubleString(maxPosAmount + ""));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                isSelfChange = false;
                KeyboardUtils.hideKeyboard(SettingsCardLimitsActivity.this);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.saveButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Update card limits", (isFromCreditCardHub ? "CreditCardLimit" : "DebitCardLimit") + "_EditLimitsScreen_SaveButtonClicked");
            String atmWithdrawalAmount = StringExtensions.removeCurrency(StringExtensions.removeCommasAndDots(binding.atmWithdrawalLimitNormalInputView.getText()));
            String pointOfSalaryAmount = StringExtensions.removeCurrency(StringExtensions.removeCommasAndDots(binding.pointOfSaleDailyLimitNormalInputView.getText()));
            saveLimits(atmWithdrawalAmount, pointOfSalaryAmount);
        });

        binding.increaseLimitButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsCardLimitsActivity.this, CreditCardVCLBaseActivity.class);
            intent.putExtra(IS_FROM_CARD_HUB, isFromCreditCardHub);
            intent.putExtra(VCL_DATA, manageCardItem.getVclData());
            startActivity(intent);
            AnalyticsUtil.INSTANCE.trackAction("Credit Card", "Credit Card VCL from Limits");
        });
    }

    private void performFeatureCheck() {
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (!(featureSwitchingToggles.getCreditCardVCL() == FeatureSwitchingStates.ACTIVE.getKey())) {
            binding.increaseLimitButton.setVisibility(View.GONE);
        } else {
            if (manageCardItem.getCardOffers() != null && !TextUtils.isEmpty(manageCardItem.getCardOffers().getCreditLimitIncreaseAmtForCLI()) && !manageCardItem.getCardOffers().getCreditLimitIncreaseAmtForCLI().equalsIgnoreCase(manageCardItem.getCardOffers().getExistingCreditLimitOfCreditCard())) {
                binding.increaseLimitButton.setVisibility(View.VISIBLE);
                binding.overallCreditLimitDivider.setVisibility(View.VISIBLE);
            }
        }
    }

    private int calculatePercentageBasedOnInput(String value, double maxValue) {
        double decimalPercentage;
        if (value.isEmpty()) {
            decimalPercentage = 0.0;
        } else {
            decimalPercentage = Double.parseDouble(value) / maxValue;
        }
        return (int) (decimalPercentage * MAX_PROGRESS_VALUE);
    }

    private void populateViews() {

        if (cardLimitDetails != null && cardLimitDetails.getAtmMaxLimit() != null) {
            maxAtmAmount = Double.parseDouble(cardLimitDetails.getAtmMaxLimit().getAmount().replace(",", ""));
        } else {
            maxAtmAmount = 0.0;
        }
        if (cardLimitDetails != null && cardLimitDetails.getPosMaxLimit() != null) {
            maxPosAmount = Double.parseDouble(cardLimitDetails.getPosMaxLimit().getAmount().replace(",", ""));
        } else {
            maxPosAmount = 0.0;
        }
        styleguide.content.Card cardInformation = new styleguide.content.Card();
        if (cardLimitDetails != null && (CREDIT_CARD.equalsIgnoreCase(cardLimitDetails.getCardType()) || CREDIT.equalsIgnoreCase(cardLimitDetails.getCardType()) || getString(R.string.credit_card).equalsIgnoreCase(cardLimitDetails.getCardType()))) {
            cardInformation.setCardType(getString(R.string.credit_card));
        } else {
            cardInformation.setCardType(getString(R.string.debit_card));
        }

        cardInformation.setCardNumber(StringExtensions.toFormattedCardNumber(cardLimitDetails.getCardNumber()));
        binding.cardView.setCard(cardInformation);

        currentPOSDailyLimit = cardLimitDetails.getPosCurrentLimit() != null ? cardLimitDetails.getPosCurrentLimit().getAmount() : BASE_AMOUNT;
        maxPOSDailyLimit = cardLimitDetails.getPosMaxLimit() != null ? cardLimitDetails.getPosMaxLimit().getAmount() : BASE_AMOUNT;

        currentATMDailyLimit = cardLimitDetails.getAtmCurrentLimit() != null ? cardLimitDetails.getAtmCurrentLimit().getAmount() : BASE_AMOUNT;
        maxATMDailyLimit = cardLimitDetails.getAtmMaxLimit() != null ? cardLimitDetails.getAtmMaxLimit().getAmount() : BASE_AMOUNT;

        binding.atmWithdrawalLimitNormalInputView.setSelectedValue(currentATMDailyLimit);
        binding.pointOfSaleDailyLimitNormalInputView.setSelectedValue(currentPOSDailyLimit);

        populateATMWithdrawalLimitSlider();
        populatePointOfSaleDailyLimitSlider();

        binding.saveButton.setEnabled(false);
    }

    private int progressValue(double currentValue, double maxValue) {
        return (int) ((currentValue / maxValue) * MAX_PROGRESS_VALUE);
    }

    private void populateATMWithdrawalLimitSlider() {
        binding.atmWithdrawalLimitSlider.setStartText(TextFormatUtils.formatBasicAmountAsRand(BASE_AMOUNT));
        binding.atmWithdrawalLimitSlider.setEndText(TextFormatUtils.formatBasicAmountAsRand(maxATMDailyLimit));
        binding.atmWithdrawalLimitSlider.getSeekbar().setMax(MAX_PROGRESS_VALUE);
        binding.atmWithdrawalLimitSlider.getSeekbar().setProgress(progressValue(Double.parseDouble(currentATMDailyLimit), maxAtmAmount));
    }

    private void populatePointOfSaleDailyLimitSlider() {
        binding.pointOfSaleDailyLimitSlider.setStartText(TextFormatUtils.formatBasicAmountAsRand(BASE_AMOUNT));
        binding.pointOfSaleDailyLimitSlider.setEndText(TextFormatUtils.formatBasicAmountAsRand(maxPOSDailyLimit));
        binding.pointOfSaleDailyLimitSlider.getSeekbar().setMax(MAX_PROGRESS_VALUE);
        binding.pointOfSaleDailyLimitSlider.getSeekbar().setProgress(progressValue(Double.parseDouble(currentPOSDailyLimit), maxPosAmount));
    }

    private void manageCreditCardLimit() {
        binding.newLimitPrimaryContentAndLabelView.setVisibility(View.VISIBLE);
        binding.overallCreditLimitDivider.setVisibility(View.VISIBLE);
        binding.pointOfSaleDailyLimitNormalInputView.setVisibility(View.GONE);
        binding.pointOfSaleDailyLimitSlider.setVisibility(View.GONE);
    }
}
