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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.databinding.ActivityOrderCreditCardConfirmDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import styleguide.utils.extensions.StringExtensions;

public class ConfirmCreditCardReplacementActivity extends BaseActivity implements View.OnClickListener, ConfirmCreditCardReplacementView {
    public static final String STOP_AND_REPLACE_CHANNEL = "Stop and Replace Process";
    public static final String CONFIRM_DETAILS_SCREEN = "Confirm details screen";
    public static final String CONFIRM_DETAILS_BUTTON = "Confirm details button";
    public static final String CANCEL_BUTTON = "Cancel button";
    public static final String BACK_BUTTON = "Back button";
    private ActivityOrderCreditCardConfirmDetailsBinding binding;
    private CreditCardReplacement creditCardReplacement;
    private final String CREDIT_CARD_REPLACEMENT = "CREDIT_CARD_REPLACEMENT";
    private ConfirmCreditCardReplacementPresenter presenter;
    private String originScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_order_credit_card_confirm_details, null, false);
        setContentView(binding.getRoot());

        if (getIntent().getSerializableExtra(CREDIT_CARD_REPLACEMENT) != null) {
            creditCardReplacement = (CreditCardReplacement) getIntent().getSerializableExtra(CREDIT_CARD_REPLACEMENT);
            originScreen = getIntent().getStringExtra(ORIGIN_SCREEN);
        }
        setupView();
        setToolBarBack(R.string.order_new_card);
        presenter = new ConfirmCreditCardReplacementPresenter(this, new CreditCardInteractor());
        trackScreenView(STOP_AND_REPLACE_CHANNEL, CONFIRM_DETAILS_SCREEN);
    }

    private void setupView() {
        String selectedReason = creditCardReplacement.getReasonForReplacement();
        binding.replacementReasonView.setContentText(selectedReason);
        if (FRAUD.equalsIgnoreCase(selectedReason) || FRAUD_AFRIKAANS.equalsIgnoreCase(selectedReason)) {
            binding.replacementFee.setContentText(getString(R.string.replace_charge_no_charge));
        } else {
            binding.replacementFee.setContentText(getString(R.string.replacement_charge_not_fraud));
        }
        binding.incidentDate.setContentText(convertDateFormat(creditCardReplacement.getIncidentDate()));
        binding.whenWasCardLastUsed.setContentText(convertDateFormat(creditCardReplacement.getLastUsedDate()));
        binding.cardTypeToBeReplaced.setContentText(creditCardReplacement.getCardType());

        if (getString(R.string.face_to_face_delivery).equals(creditCardReplacement.getDeliveryMethod())) {
            binding.branchToDeliverTo.setVisibility(View.GONE);
            binding.deliveryMethod.setContentText(getString(R.string.face_to_face_delivery));
        } else {
            binding.branchToDeliverTo.setVisibility(View.VISIBLE);
            binding.deliveryMethod.setContentText(getString(R.string.collect_from_branch));
            binding.branchToDeliverTo.setContentText(creditCardReplacement.getSelectedBranch());
        }
        binding.contactNumber.setContentText(StringExtensions.toFormattedCellphoneNumber(creditCardReplacement.getContactNumber()));

        binding.confirmDetailsButton.setOnClickListener(this);
    }

    private String convertDateFormat(String date) {
        String inputDatePattern = "yyyy/MM/dd";
        String outputDatePattern = "dd MMM yyyy";
        SimpleDateFormat inputDateFormatting = new SimpleDateFormat(inputDatePattern, BMBApplication.getApplicationLocale());
        SimpleDateFormat outputDateFormatting = new SimpleDateFormat(outputDatePattern, BMBApplication.getApplicationLocale());

        try {
            Date dateToBeShown = inputDateFormatting.parse(date);
            return outputDateFormatting.format(dateToBeShown);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void navigateToStopAndReplaceWarningScreen() {
        trackButtonClick("S&R CC - Order new card - continue button");
        Intent goToWarningScreenIntent = new Intent(this, CreditCardStopAndReplaceWarningActivity.class);
        goToWarningScreenIntent.putExtra(CREDIT_CARD_REPLACEMENT, creditCardReplacement);
        goToWarningScreenIntent.putExtra(ORIGIN_SCREEN, originScreen);
        startActivity(goToWarningScreenIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.combined_cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel) {
            trackButtonClick("S&R CC - Order new card - cancel button");
            handleBackAction();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBackAction() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.credit_card_replacement_confirmation))
                .positiveDismissListener((dialog, which) -> {
                    if (CreditCardHubActivity.class.getSimpleName().equalsIgnoreCase(originScreen)) {
                        startActivity(IntentFactory.goBackToCreditCardHub(ConfirmCreditCardReplacementActivity.this, creditCardReplacement.getCreditCardnumber()));
                    } else {
                        startActivity(IntentFactory.goBackToCardDetailScreen(ConfirmCreditCardReplacementActivity.this, creditCardReplacement.getCreditCardnumber()));
                    }
                }));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmDetailsButton:
                trackButtonClick(CONFIRM_DETAILS_BUTTON);
                presenter.validateReplacementInvoked(creditCardReplacement);
                break;
        }
    }

    @Override
    public void navigateToWarningScreen() {
        navigateToStopAndReplaceWarningScreen();
    }

    @Override
    public void openAssistanceDialog() {
        ReplacementAssistanceDialogFragment replacementAssistanceDialogFragment = new ReplacementAssistanceDialogFragment();
        replacementAssistanceDialogFragment.show(getFragmentManager(), "");
    }

    @Override
    public void showTechnicalDifficultiesErrorMessage() {
        BaseAlertDialog.INSTANCE.showErrorAlertDialog(getString(R.string.technical_difficulties_try_shortly));
    }
}
