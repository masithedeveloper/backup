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

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReason;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReasonsList;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.databinding.ActivityCreditCardReplacementBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import styleguide.content.Card;
import styleguide.content.Contact;
import styleguide.forms.ItemSelectionInterface;
import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

public class CreditCardReplacementOverviewActivity extends BaseActivity implements CreditCardReplacementView, View.OnClickListener, ItemSelectionInterface {
    private SelectorList<StringItem> replacementReasons = new SelectorList<>();
    public static final String CREDIT_CARD_REPLACEMENT = "CREDIT_CARD_REPLACEMENT";
    public static final String WHEN_DID_THIS_HAPPEN_FIELD = "When did this happen field";
    public static final String CARD_LAST_USED_FIELD = "Card last used field";
    private String creditCardNumber;
    private ActivityCreditCardReplacementBinding binding;
    private static final String OTHER = "Other";
    private static final String OTHER_AFRIKAANS = "Ander";
    private String originScreen;
    private String replacementReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_credit_card_replacement, null, false);
        setContentView(binding.getRoot());

        String CARD_NUMBER = "CARD_NUMBER";
        if (getIntent().getStringExtra(CARD_NUMBER) != null) {
            creditCardNumber = getIntent().getStringExtra(CARD_NUMBER);
            originScreen = getIntent().getStringExtra(ORIGIN_SCREEN);
        }
        setupToolbar();
        loadView();
    }

    private void setupToolbar() {
        setToolBarBack(R.string.credit_card_replacement_overview);
    }

    public void loadView() {
        CreditCardReplacementPresenter replacementPresenter = new CreditCardReplacementPresenter(this, new CreditCardInteractor());
        replacementPresenter.viewLoaded(creditCardNumber);

        Card cardInformation = new Card();
        cardInformation.setCardNumber(StringExtensions.toFormattedCardNumber(creditCardNumber));
        cardInformation.setCardType(getString(R.string.credit_card));

        binding.cardInformation.setCard(cardInformation);

        Contact contactLocal = new Contact();
        contactLocal.setContactName(getString(R.string.south_africa));
        contactLocal.setContactNumber(getString(R.string.stop_and_replace_local_number));

        Contact contactInternational = new Contact();
        contactInternational.setContactName(getString(R.string.international));
        contactInternational.setContactNumber(getString(R.string.stop_and_replace_international_number));

        binding.confirmButton.setOnClickListener(this);
        binding.whenDidThisHappenDateSelector.setOnClickListener(this);
        binding.lastUsedDateSelector.setOnClickListener(this);
        binding.contactLocalView.setOnClickListener(this);
        binding.contactInternationalView.setOnClickListener(this);
        binding.contactLocalView.setContact(contactLocal);
        binding.contactInternationalView.setContact(contactInternational);
        binding.confirmButton.setEnabled(false);
        binding.contactLocalView.setOnClickListener(this);
        binding.contactInternationalView.setOnClickListener(this);
        binding.replacementReasonSelector.setItemSelectionInterface(this);

        trackScreenView(ConfirmCreditCardReplacementActivity.STOP_AND_REPLACE_CHANNEL, "Replacement overview screen");
    }

    private void openDatePickerDialog(NormalInputView inputView) {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", CommonUtils.getCurrentApplicationLocale());
        DatePickerDialog datePicker = new DatePickerDialog(this, R.style.DatePickerDialogTheme, (stopAndReplaceDatePicker, year, month, day) -> {
            calendar.set(year, month, day);
            inputView.setSelectedValue(dateFormat.format(calendar.getTime()));
            validateAndChangeButtonStatus();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
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
            if (CreditCardHubActivity.class.getSimpleName().equalsIgnoreCase(originScreen)) {
                startActivity(IntentFactory.goBackToCreditCardHub(CreditCardReplacementOverviewActivity.this, creditCardNumber));
            } else {
                startActivity(IntentFactory.goBackToCardDetailScreen(CreditCardReplacementOverviewActivity.this, creditCardNumber));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.whenDidThisHappenDateSelector:
                trackButtonClick(WHEN_DID_THIS_HAPPEN_FIELD);
                openDatePickerDialog(binding.whenDidThisHappenDateSelector);
                break;
            case R.id.lastUsedDateSelector:
                trackButtonClick(CARD_LAST_USED_FIELD);
                openDatePickerDialog(binding.lastUsedDateSelector);
                break;
            case R.id.confirmButton:
                trackButtonClick("S&R CC - Replacement details - confirm button");
                navigateToOrderNewCardScreen();
                break;
            case R.id.contactLocalView:
                callInternationalNumber(false);
                break;
            case R.id.contactInternationalView:
                callInternationalNumber(true);
                break;
        }
    }

    private void callInternationalNumber(boolean internationalNumber) {
        if (internationalNumber) {
            trackButtonClick(ReplacementAssistanceDialogFragment.INTERNATIONAL_NUMBER_DIAL);
            TelephoneUtil.callStopAndReplaceCreditCardInternationalNumber(this);
        } else {
            trackButtonClick(ReplacementAssistanceDialogFragment.LOCAL_NUMBER_DIAL);
            TelephoneUtil.callStopAndReplaceCreditCardLocalNumber(this);
        }
    }

    @Override
    public void navigateToOrderNewCardScreen() {
        String lastUsedDate = binding.lastUsedDateSelector.getSelectedValue();
        String replacementIncidentDate = binding.whenDidThisHappenDateSelector.getSelectedValue();
        Intent intent = new Intent(this, OrderNewCreditCardActivity.class);
        CreditCardReplacement creditCardReplacement = new CreditCardReplacement();
        creditCardReplacement.setCreditCardnumber(creditCardNumber);
        creditCardReplacement.setLastUsedDate(lastUsedDate);
        creditCardReplacement.setReasonForReplacement(replacementReason);
        if (replacementReason == null) {
            return;
        }
        creditCardReplacement.setReasonForReplacement(replacementReason);
        creditCardReplacement.setIncidentDate(replacementIncidentDate);
        intent.putExtra(ORIGIN_SCREEN, originScreen);
        intent.putExtra(CREDIT_CARD_REPLACEMENT, creditCardReplacement);
        startActivity(intent);
    }

    private void validateAndChangeButtonStatus() {
        String selectedDate = binding.whenDidThisHappenDateSelector.getSelectedValue();
        String lastUsedDate = binding.lastUsedDateSelector.getSelectedValue();
        boolean datesFilled = !TextUtils.isEmpty(selectedDate) && !TextUtils.isEmpty(lastUsedDate)
                && !selectedDate.matches("yyyy/mm/dd")
                && !lastUsedDate.matches("yyyy/mm/dd")
                && !TextUtils.isEmpty(getString(R.string.hint_reason_for_replacement))
                && !"".equalsIgnoreCase(binding.replacementReasonSelector.getSelectedValue());
        binding.confirmButton.setEnabled(datesFilled);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void showCreditCardReplacementReasons(CreditCardReplacementReasonsList creditCardReplacementList) {
        if (creditCardReplacementList != null) {
            List<CreditCardReplacementReason> replacementReasons = creditCardReplacementList.getReplacementReasons();
            if (replacementReasons != null) {
                for (int i = 0; i < replacementReasons.size(); i++) {
                    if (replacementReasons.get(i).getReasonDescription() != null &&
                            OTHER.equalsIgnoreCase(replacementReasons.get(i).getReasonDescription()) ||
                            OTHER_AFRIKAANS.equalsIgnoreCase(replacementReasons.get(i).getReasonDescription())) {
                        replacementReasons.remove(i);
                    } else {
                        this.replacementReasons.add(new StringItem(replacementReasons.get(i).getReasonDescription()));
                    }
                }
            }
        }
        binding.replacementReasonSelector.setList(replacementReasons, "");
        if (creditCardReplacementList != null && !creditCardReplacementList.getReplacementInfo().isEmpty()) {
            binding.offerTextView.setVisibility(View.VISIBLE);
            binding.offerTextView.setText(creditCardReplacementList.getReplacementInfo());
        } else {
            binding.offerTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(int index) {
        replacementReason = replacementReasons.get(index).getItem();
        validateAndChangeButtonStatus();
    }
}