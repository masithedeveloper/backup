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

package com.barclays.absa.banking.funeralCover.ui;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtection;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.card.ui.creditCardInsurance.CreditCardInsurancePresenter;
import com.barclays.absa.banking.card.ui.creditCardInsurance.CreditCardInsuranceView;
import com.barclays.absa.banking.databinding.FuneralCoverCardLifePlanActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.PdfUtil;
import com.barclays.absa.utils.TextFormatUtils;

public class FuneralCoverCardLifePlanActivity extends BaseActivity implements CreditCardInsuranceView {

    private FuneralCoverCardLifePlanActivityBinding binding;
    private final String SITE_SECTION = "Credit Card Protection";
    private CreditCardInsurancePresenter presenter;
    private String creditCardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.funeral_cover_card_life_plan_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.card_life_plan));
        presenter = new CreditCardInsurancePresenter(this);
        creditCardNumber = getIntent().getStringExtra(CreditCardHubActivity.CREDIT_CARD);
        CreditProtection creditProtection = getAppCacheService().getCreditProtection();
        if (creditProtection != null) {
            displayCreditProtectionQuote(creditProtection);
        }
        initializeListeners();
    }

    private void displayCreditProtectionQuote(CreditProtection creditProtection) {
        binding.premiumAmountViewView.setTitle(getString(R.string.currency_format, TextFormatUtils.formatBasicAmount(creditProtection.getMonthlyPremium())));
        String dayOfDebit = DateUtils.getDateWithMonthNameFromStringWithoutHyphen(creditProtection.getDayOfDebit());
        binding.nextPremiumDeductionView.setContentText(DateUtils.removePeriod(dayOfDebit));
        AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_ApplicationQuote", SITE_SECTION, BMBConstants.TRUE_CONST);
    }

    private void initializeListeners() {
        binding.coverMeButton.setOnClickListener(v -> {
            if (creditCardNumber != null) {
                presenter.onSubmitButtonClicked(creditCardNumber);
            }
        });

        CommonUtils.makeTextClickable(this, R.string.agree_and_confirm_credit_terms_and_conditions, getString(R.string.agree_and_confirm_terms_and_conditions), new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String termsAndConditionsLink = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/AFS/PDF/Credit_Protection_Card_TC.pdf";
                PdfUtil.INSTANCE.showPDFInApp(FuneralCoverCardLifePlanActivity.this, termsAndConditionsLink);
            }
        }, binding.agreeToTermsCheckBoxView.getCheckBoxTextView());

        binding.agreeToTermsCheckBoxView.setOnCheckedListener(isChecked -> {
            binding.coverMeButton.setEnabled(isChecked);
        });
    }

    @Override
    public void showFailureScreen() {
        startActivity(IntentFactory.getSubmitCreditProtectionFailureScreen(this, getString(R.string.credit_protection_unsuccessful_sub_message)));
    }

    @Override
    public void showSuccessScreen(CreditProtection successResponse) {
        String dayOfDebit = DateUtils.getDateWithMonthNameFromStringWithoutHyphen(successResponse.getDayOfDebit());
        startActivity(IntentFactory.getSubmitCreditProtectionSuccessResultScreen(this, getString(R.string.credit_protection_successful_sub_message, DateUtils.removePeriod(dayOfDebit))));
    }

    @Override
    public void showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message));
        AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_ApplicationQuote", SITE_SECTION, BMBConstants.TRUE_CONST);
    }
}
