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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.card.ui.CardIntentFactory;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.databinding.ActivityStopAndReplaceCreditCardWarningBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import static com.barclays.absa.banking.card.ui.creditCardReplacement.ConfirmCreditCardReplacementActivity.STOP_AND_REPLACE_CHANNEL;

public class CreditCardStopAndReplaceWarningActivity extends BaseActivity implements StopAndReplaceCreditCardWarningView, View.OnClickListener {
    public static final String WARNING_SCREEN = "Warning screen";
    public static final String CONFIRM = "Confirm";
    public static final String CANCEL = "Cancel";
    private ActivityStopAndReplaceCreditCardWarningBinding binding;
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    private CreditCardReplacement creditCardReplacement;
    private CreditCardStopAndReplaceWarningPresenter presenter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String originScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_stop_and_replace_credit_card_warning, null, false);
        setContentView(binding.getRoot());

        binding.cancelReplacementButton.setOnClickListener(this);
        binding.confirmReplacementButton.setOnClickListener(this);

        String CREDIT_CARD_REPLACEMENT = "CREDIT_CARD_REPLACEMENT";
        if (getIntent().getSerializableExtra(CREDIT_CARD_REPLACEMENT) != null) {
            creditCardReplacement = (CreditCardReplacement) getIntent().getSerializableExtra(CREDIT_CARD_REPLACEMENT);
            originScreen = getIntent().getStringExtra(ORIGIN_SCREEN);
        }

        SureCheckDelegate sureCheckDelegate = new SureCheckDelegate(this) {
            @Override
            public void onSureCheckProcessed() {
                handler.postDelayed(() -> presenter.stopCardInvoked(creditCardReplacement), 250);
            }

            @Override
            public void onSureCheckCancelled(BaseActivity activity) {
                super.onSureCheckCancelled(activity);
                CardIntentFactory.showSurecheckFailedResultScreen(CreditCardStopAndReplaceWarningActivity.this);
            }

            @Override
            public void onSureCheckFailed() {
                super.onSureCheckFailed();
                CardIntentFactory.showSurecheckFailedResultScreen(CreditCardStopAndReplaceWarningActivity.this);
            }
        };

        presenter = new CreditCardStopAndReplaceWarningPresenter(this, sureCheckDelegate);
        trackScreenView(STOP_AND_REPLACE_CHANNEL, WARNING_SCREEN);
        loadView();
    }

    private void loadView() {
        if (FRAUD.equalsIgnoreCase(creditCardReplacement.getReasonForReplacement())) {
            binding.stopAndReplaceWarningAssistanceMessage.setText(getString(R.string.fraud_stop_and_replace_warning_assistance_message));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmReplacementButton:
                trackButtonClick(CONFIRM);
                if (creditCardReplacement != null) {
                    presenter.stopCardInvoked(creditCardReplacement);
                }
                break;
            case R.id.cancelReplacementButton:
                trackButtonClick(CANCEL);
                presenter.cancelReplacementInvoked();
                break;
        }
    }

    @Override
    public void finishStopAndReplace() {
        if (CreditCardHubActivity.class.getSimpleName().equalsIgnoreCase(originScreen)) {
            startActivity(IntentFactory.goBackToCreditCardHub(CreditCardStopAndReplaceWarningActivity.this, creditCardReplacement.getCreditCardnumber()));
        } else {
            startActivity(IntentFactory.goBackToCardDetailScreen(CreditCardStopAndReplaceWarningActivity.this, creditCardReplacement.getCreditCardnumber()));
        }
    }

    @Override
    public void onBackPressed() {
        finishStopAndReplace();
    }

    @Override
    public void showSuccessScreen() {
        if (creditCardReplacement != null && getString(R.string.collect_from_branch).equalsIgnoreCase(creditCardReplacement.getDeliveryMethod())) {
            trackScreenView("stop and replace", "S&R CC - Success screen");
            startActivity(CardIntentFactory.showStopAndReplaceSuccessScreen(this, true));
        } else if (creditCardReplacement != null && getString(R.string.face_to_face_delivery).equalsIgnoreCase(creditCardReplacement.getDeliveryMethod())) {
            trackScreenView("stop and replace", "S&R CC - Failure screen");
            startActivity(CardIntentFactory.showStopAndReplaceSuccessScreen(this, false));
        }
    }

    @Override
    public void showErrorScreen() {
        startActivity(CardIntentFactory.showStopAndReplaceFailureScreen(this));
    }

    @Override
    public void showMessageError() {
        BaseAlertDialog.INSTANCE.showErrorAlertDialog(getString(R.string.technical_difficulties_try_shortly), (dialog, which) -> finish());
    }

    @Override
    public void showCancelConfirmationScreen() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.credit_card_replacement_confirmation))
                .positiveDismissListener((dialog, which) -> finishStopAndReplace()));
    }
}
