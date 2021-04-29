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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList;
import com.barclays.absa.banking.boundary.model.BranchInformation;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.card.services.card.dto.BranchDeliveryDetailsListRequest;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCard;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.databinding.ActivityOrderNewCreditCardBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import styleguide.forms.ItemCheckedInterface;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

import static com.barclays.absa.banking.card.ui.creditCardReplacement.ConfirmCreditCardReplacementActivity.STOP_AND_REPLACE_CHANNEL;

public class OrderNewCreditCardActivity extends BaseActivity implements View.OnClickListener, ItemCheckedInterface {
    private final SelectorList<StringItem> branchList = new SelectorList<>();
    public static final String CREDIT_CARD_REPLACEMENT = "CREDIT_CARD_REPLACEMENT";
    public static final String ORDER_NEW_CARD_SCREEN = "Order new card screen";
    private ActivityOrderNewCreditCardBinding binding;
    private String selectedBranch = "none";
    private String selectedBranchCode;
    private int cardDeliveryOption = -1;
    private CreditCardReplacement creditCardReplacement;
    private final static String FRAUD_AFRIKAANS = "Bedrog";
    private final static String FRAUD = "Fraud";
    private boolean isCollectFromBranch;
    private String originScreen;
    private BranchDeliveryDetailsList branches;
    private SelectorList<StringItem> selectorList;

    private final ExtendedResponseListener<CreditCardInformation> creditCardInformationResponseListener = new ExtendedResponseListener<CreditCardInformation>() {
        @Override
        public void onSuccess(CreditCardInformation successResponse) {
            CreditCard card = successResponse.getAccount();
            if (card != null && SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                creditCardReplacement.setCardType(card.getAccountTypeDescription());
                binding.cardTypeWidget.setSelectedValue(card.getAccountTypeDescription());

                String phoneNumber = CustomerProfileObject.getInstance().getCellNumber();
                creditCardReplacement.setContactNumber(phoneNumber);
                binding.contactNumberWidget.setSelectedValue(phoneNumber);
            } else {
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .message(successResponse.getErrorMessage() != null ? successResponse.getErrorMessage() : successResponse.getTransactionMessage())
                        .positiveDismissListener((dialog, which) -> loadAccountsAndGoHome())
                        .build());
            }
            dismissProgressDialog();
        }
    };

    private final ExtendedResponseListener<BranchDeliveryDetailsList> branchListResponseListener = new ExtendedResponseListener<BranchDeliveryDetailsList>() {
        @Override
        public void onSuccess(final BranchDeliveryDetailsList branches) {
            BaseView view = getBaseView();
            if (view != null)
                view.dismissProgressDialog();
            if (branches != null && branches.getBranchInformation() != null) {
                AsyncTask.execute(() -> {
                    for (BranchInformation branch : branches.getBranchInformation()) {
                        if (branch.getBranchName() != null) {
                            OrderNewCreditCardActivity.this.branches = branches;
                            branchList.add(new StringItem(branch.getBranchName(), branch.getBranchCode()));
                        } else {
                            BaseAlertDialog.INSTANCE.showGenericErrorDialog();
                        }
                    }
                    runOnUiThread(OrderNewCreditCardActivity.this::startSelector);
                });
            } else {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog();
            }
        }
    };

    @SuppressWarnings("unchecked")
    private void startSelector() {
        binding.branchSelector.setItemSelectionInterface(index -> {
            if (branches != null && branches.getBranchInformation() != null && index != -1) {
                selectedBranch = branches.getBranchInformation().get(index).getBranchName();
                selectedBranchCode = branches.getBranchInformation().get(index).getBranchCode();
                binding.branchSelector.setSelectedValue(selectedBranch);
                changeButtonState();
            }
        });

        binding.branchSelector.setList(branchList, getString(R.string.stop_and_replace_select_branch_list));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_order_new_credit_card, null, false);
        setContentView(binding.getRoot());

        selectorList = new SelectorList<>();
        binding.continueButton.setOnClickListener(this);
        binding.continueButton.setEnabled(false);
        binding.acceptChargeCheckbox.setOnCheckedListener(isChecked -> changeButtonState());

        creditCardInformationResponseListener.setView(this);
        branchListResponseListener.setView(this);

        if (getIntent().getSerializableExtra(CREDIT_CARD_REPLACEMENT) != null) {
            creditCardReplacement = (CreditCardReplacement) getIntent().getSerializableExtra(CREDIT_CARD_REPLACEMENT);
            originScreen = getIntent().getStringExtra(ORIGIN_SCREEN);
            CreditCardInteractor creditCardInteractor = new CreditCardInteractor();
            creditCardInteractor.fetchCreditCardInformation(creditCardReplacement.getCreditCardnumber(), creditCardInformationResponseListener);
        }

        setReplacementFee();
        setRadioButtons();
        setToolBarBack(R.string.order_new_card);
        trackScreenView(STOP_AND_REPLACE_CHANNEL, ORDER_NEW_CARD_SCREEN);
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
            handleCancelAction();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleCancelAction() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.credit_card_replacement_confirmation))
                .positiveDismissListener((dialog, which) -> {
                    if (CreditCardHubActivity.class.getSimpleName().equalsIgnoreCase(originScreen)) {
                        startActivity(IntentFactory.goBackToCreditCardHub(OrderNewCreditCardActivity.this, creditCardReplacement.getCreditCardnumber()));
                    } else {
                        startActivity(IntentFactory.goBackToCardDetailScreen(OrderNewCreditCardActivity.this, creditCardReplacement.getCreditCardnumber()));
                    }
                }));
    }

    private void navigateToConfirmOrderDetails() {
        Intent navigateToOrderConfirmation = new Intent(this, ConfirmCreditCardReplacementActivity.class);
        navigateToOrderConfirmation.putExtra(CREDIT_CARD_REPLACEMENT, creditCardReplacement);
        navigateToOrderConfirmation.putExtra(ORIGIN_SCREEN, originScreen);
        startActivity(navigateToOrderConfirmation);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.continueButton) {
            navigateToNextScreen();
        }
    }

    private void navigateToNextScreen() {
        int FACE_TO_FACE_DELIVERY = 0;
        int COLLECT_FROM_BRANCH = 1;
        if (cardDeliveryOption == COLLECT_FROM_BRANCH) {
            trackCustomAction("S&R CC - Face to face");
            creditCardReplacement.setSelectedBranch(selectedBranch);
            creditCardReplacement.setSelectedBranchCode(selectedBranchCode);
            creditCardReplacement.setDeliveryMethod(getString(R.string.collect_from_branch));
        } else if (cardDeliveryOption == FACE_TO_FACE_DELIVERY) {
            trackCustomAction("S&R CC - Branch delivery");
            creditCardReplacement.setDeliveryMethod(getString(R.string.face_to_face_delivery));
        }
        navigateToConfirmOrderDetails();
    }

    public void changeButtonState() {
        if (isFraud()) {
            if (!isCollectFromBranch && cardDeliveryOption != -1) {
                binding.continueButton.setEnabled(true);
            } else if (isCollectFromBranch && !binding.branchSelector.getSelectedValue().isEmpty()) {
                binding.continueButton.setEnabled(true);
            } else if (isCollectFromBranch && binding.branchSelector.getSelectedValue().isEmpty()) {
                binding.continueButton.setEnabled(false);
            } else {
                boolean shouldEnableContinueButton = !isCollectFromBranch && binding.branchSelector.getSelectedValue().isEmpty();
                binding.continueButton.setEnabled(shouldEnableContinueButton);
            }
        } else {
            if (!isCollectFromBranch && binding.acceptChargeCheckbox.getIsValid() && cardDeliveryOption != -1) {
                binding.continueButton.setEnabled(true);
            } else {
                boolean shouldEnableContinueButton = isCollectFromBranch && binding.acceptChargeCheckbox.getIsValid() && !binding.branchSelector.getSelectedValue().isEmpty() && cardDeliveryOption != -1;
                binding.continueButton.setEnabled(shouldEnableContinueButton);
            }
        }
    }

    private void setReplacementFee() {
        if (isFraud()) {
            binding.replacementFeeAmountTextView.setText(getString(R.string.replace_charge_no_charge));
            binding.acceptChargeCheckbox.setVisibility(View.GONE);
        } else {
            binding.replacementFeeAmountTextView.setText(getString(R.string.replacement_charge_not_fraud));
            binding.acceptChargeCheckbox.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFraud() {
        return FRAUD.equalsIgnoreCase(creditCardReplacement.getReasonForReplacement()) ||
                FRAUD_AFRIKAANS.equalsIgnoreCase(creditCardReplacement.getReasonForReplacement());
    }

    @SuppressWarnings("unchecked")
    private void setRadioButtons() {
        selectorList.add(new StringItem(getString(R.string.face_to_face_delivery)));
        selectorList.add(new StringItem(getString(R.string.branch_collection_method)));

        binding.deliveryMethodRadioGroup.setDataSource(selectorList);

        binding.deliveryMethodRadioGroup.setItemCheckedInterface(this);
    }

    private void showFaceToFaceLayoutItems() {
        binding.branchSelector.setVisibility(View.GONE);
        binding.faceToFaceDisclaimer.setVisibility(View.VISIBLE);
    }

    private void showCollectFromBranchLayout() {
        binding.faceToFaceDisclaimer.setVisibility(View.GONE);
        binding.branchSelector.setVisibility(View.VISIBLE);
    }

    private void fetchBranches() {
        BranchDeliveryDetailsListRequest<BranchDeliveryDetailsList> branchDeliveryDetailsListRequest = new BranchDeliveryDetailsListRequest<>(branchListResponseListener);
        ServiceClient serviceClient = new ServiceClient(branchDeliveryDetailsListRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void onChecked(int index) {
        if (index == 0) {
            isCollectFromBranch = false;
            cardDeliveryOption = 0;
            showFaceToFaceLayoutItems();
        } else if (index == 1) {
            if (branchList.isEmpty()) {
                fetchBranches();
            }
            isCollectFromBranch = true;
            cardDeliveryOption = 1;
            showCollectFromBranchLayout();
        } else {
            isCollectFromBranch = false;
            cardDeliveryOption = -1;
            binding.continueButton.setEnabled(false);
        }
        changeButtonState();
    }
}