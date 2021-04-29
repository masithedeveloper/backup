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

package com.barclays.absa.banking.beneficiaries.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.buy.ui.airtime.existing.PurchaseDetailsActivity;
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityActivity;
import com.barclays.absa.banking.cashSend.ui.CashSendBeneficiaryActivity;
import com.barclays.absa.banking.databinding.BeneficiaryDetailsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.payments.PaymentDetailsActivity;
import com.barclays.absa.banking.payments.PaymentsConstants;
import com.barclays.absa.banking.payments.ProofOfPaymentHistoryActivity;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.util.ArrayList;
import java.util.Date;

import styleguide.content.BeneficiaryListItem;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.payments.PaymentsConstants.IIP_STATUS;

public class BeneficiaryDetailsActivity extends BaseActivity implements BeneficiaryDetailsView {
    public static final String IS_BENEFICIARY_EDITED = "isBeneficiaryEdited";
    public static final String BENEFICIARY_DETAIL_OBJECT = "beneficiaryDetailObject";
    public static final String FROM_ACTIVITY = "fromActivity";
    public static final String MANAGE_BENEFICIARY = "MANAGE_BENEFICIARY";
    public final String TRANSACTION_STATUS_UNKNOWN = "UNKNOWN";
    public final String TRANSACTION_STATUS_UNKNOWN_AFRIKAANS = "ONBEKEND";
    private static final String ELECTRICITY_BENEFICIARY_DETAILS = "prepaidElectricityBeneficiaryDetails";
    public static final String VIEW_PREPAID_ELECTRICITY_BENEFICIARY_DETAILS = "viewPrepaidElectricityBeneficiaryDetails";
    public static final String PPE_BENEFICIARY_OBJECT = "ppeBeneficiaryObject";
    public static final String PPE_TRANSACTION_OBJECT = "ppeTransactionObject";
    private BeneficiaryDetailObject beneficiaryDetail;
    private String beneficiaryType;
    private MeterNumberObject meterNumberObject;
    private FeatureSwitching featureSwitchingToggles;

    private BeneficiaryDetailsPresenter presenter;
    private BeneficiaryDetailsActivityBinding binding;

    private boolean isBenEdited;
    private BeneficiaryTransactionsAdapter beneficiaryTransactionListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.beneficiary_details_activity, null, false);
        setContentView(binding.getRoot());

        presenter = new BeneficiaryDetailsPresenter(this);

        setToolBarBack(getString(R.string.beneficiary_details_toolbar));

        beneficiaryType = getIntent().getStringExtra(BeneficiaryLandingActivity.BENEFICIARY_TYPE);

        binding.buyBeneficiaryView
                .animate()
                .setStartDelay(200)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(400)
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.transactionHistoryRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(BeneficiaryDetailsActivity.this, R.anim.layout_animation_fall_down));
            binding.transactionHistoryRecyclerView.scheduleLayoutAnimation();
        }, 150);

        binding.transactionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionHistoryRecyclerView.setNestedScrollingEnabled(false);
        binding.transactionHistoryRecyclerView.setFocusable(false);

        featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(RESULT)) {
            beneficiaryDetail = (BeneficiaryDetailObject) getIntent().getSerializableExtra(AppConstants.RESULT);
            meterNumberObject = (MeterNumberObject) getIntent().getSerializableExtra(ELECTRICITY_BENEFICIARY_DETAILS);
        }
        if (extras != null && extras.containsKey(SUCCESS)) {
            final Date currentDate = new Date();
            final String datePattern = getString(R.string.date_pattern);
            String now = DateUtils.format(currentDate, datePattern);
            String message = String.format(getString(R.string.resend_notice_of_payment_success), now);
            toastLong(message);
            mSiteSection = BMBConstants.MANAGE_BENEFICIARIES_CONST;
            mScreenName = BMBConstants.RESEND_PROOF_PAYMENT_SUCCESS;
            AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
        }
        setupViews();
    }

    private void setupViews() {
        if (beneficiaryDetail != null) {
            presenter.downloadBeneficiaryImage(beneficiaryDetail);
        }

        binding.actionButton.showRightArrowImage();
        switch (beneficiaryType) {
            case PASS_PAYMENT:
                binding.actionButton.setupCaptionImageTextIcon(getString(R.string.beneficiary_details_pay), R.drawable.ic_pay_dark_new);
                break;
            case PASS_CASHSEND:
                binding.actionButton.setupCaptionImageTextIcon(getString(R.string.beneficiary_details_send_cash), R.drawable.ic_cashsend_dark_new);
                break;
            case PASS_AIRTIME:
                binding.actionButton.setupCaptionImageTextIcon(getString(R.string.beneficiary_details_buy), R.drawable.ic_mobile_dark);
                break;
            case PASS_PREPAID_ELECTRICITY:
                binding.actionButton.setupCaptionImageTextIcon(getString(R.string.buy_electricity), R.drawable.ic_electricity_dark);
                break;
        }

        setData();

        binding.actionButton.setOnClickListener(view -> {
            if (beneficiaryDetail != null) {
                switch (beneficiaryType) {
                    case PASS_PAYMENT:
                        presenter.onPaymentBeneficiaryClicked();
                        break;
                    case PASS_CASHSEND:
                        presenter.onCashSendBeneficiaryClicked(beneficiaryDetail.getBeneficiaryId());
                        break;
                    case PASS_AIRTIME:
                        presenter.onBuyBeneficiaryClicked(beneficiaryDetail.getBeneficiaryId());
                        break;
                    case PASS_PREPAID_ELECTRICITY:
                        presenter.onElectricityBeneficiaryClicked();
                        break;
                    default:
                        presenter.onPaymentBeneficiaryClicked();
                }
            }
        });
    }

    private void setData() {
        BeneficiaryListItem beneficiary = new BeneficiaryListItem();
        if (beneficiaryDetail != null) {
            beneficiary.setName(beneficiaryDetail.getBeneficiaryName());
            beneficiaryTransactionListAdapter = new BeneficiaryTransactionsAdapter(this, filterOutFailedTransactions(beneficiaryDetail.getTransactions()));
            binding.transactionHistoryRecyclerView.setAdapter(beneficiaryTransactionListAdapter);
            if (beneficiaryDetail.getTransactions() == null || beneficiaryDetail.getTransactions().isEmpty()) {
                binding.recentHeadingView.setVisibility(View.GONE);
                binding.transactionHistoryRecyclerView.setVisibility(View.GONE);
                binding.payDividerView.setVisibility(View.GONE);
            }
        } else {
            binding.recentHeadingView.setVisibility(View.GONE);
            binding.transactionHistoryRecyclerView.setVisibility(View.GONE);
            binding.payDividerView.setVisibility(View.GONE);
        }

        switch (beneficiaryType) {
            case PASS_PAYMENT:
                setupPaymentBeneficiaryDetailView(beneficiary);
                break;
            case PASS_CASHSEND:
                setupCashSendBeneficiaryDetailView(beneficiary);
                break;
            case PASS_AIRTIME:
                setupBuyBeneficiaryDetailView(beneficiary);
                break;
            case PASS_PREPAID_ELECTRICITY:
                setupPrepaidElectricityBeneficiaryDetailView(beneficiary);
                break;
        }
    }

    private void setupBuyBeneficiaryDetailView(BeneficiaryListItem beneficiary) {
        beneficiary.setAccountNumber(StringExtensions.toFormattedCellphoneNumber(beneficiaryDetail.getActNo()));
        binding.mobileOperatorLineItemView.setLineItemViewContent(beneficiaryDetail.getNetworkProviderName());
        binding.buyBeneficiaryView.setBeneficiary(beneficiary);
        binding.bankLineItemView.setVisibility(View.GONE);
        binding.branchLineItemView.setVisibility(View.GONE);
        binding.myReferenceLineItemView.setVisibility(View.GONE);
        binding.theirReferenceLineItemView.setVisibility(View.GONE);
        binding.myNotificationLineItemView.setVisibility(View.GONE);
        binding.accountHolderNameCodeLineItemView.setVisibility(View.GONE);
        binding.theirNotificationLineItemView.setVisibility(View.GONE);
        binding.institutionCodeLineItemView.setVisibility(View.GONE);
    }

    private void setupCashSendBeneficiaryDetailView(BeneficiaryListItem beneficiary) {
        beneficiary.setAccountNumber(StringExtensions.toFormattedCellphoneNumber(beneficiaryDetail.getActNo()));
        binding.buyBeneficiaryView.setBeneficiary(beneficiary);
        binding.myReferenceLineItemView.setLineItemViewContent(beneficiaryDetail.getMyReference());
        binding.bankLineItemView.setVisibility(View.GONE);
        binding.branchLineItemView.setVisibility(View.GONE);
        binding.theirReferenceLineItemView.setVisibility(View.GONE);
        binding.myNotificationLineItemView.setVisibility(View.GONE);
        binding.institutionCodeLineItemView.setVisibility(View.GONE);
        binding.theirNotificationLineItemView.setVisibility(View.GONE);
        binding.mobileOperatorLineItemView.setVisibility(View.GONE);
    }

    private void setupPaymentBeneficiaryDetailView(BeneficiaryListItem beneficiary) {
        String accountNumber = StringExtensions.toFormattedAccountNumber(beneficiaryDetail.getActNo());
        if (beneficiaryDetail.getBeneficiaryName().toLowerCase().contains("telkom")) {
            accountNumber = StringExtensions.toMaskedString(accountNumber);
        }

        if (beneficiaryDetail.getBenNoticeDetail().isEmpty()) {
            binding.theirNotificationLineItemView.setVisibility(View.GONE);
        }

        beneficiary.setAccountNumber(accountNumber);
        if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
            binding.institutionCodeLineItemView.setLineItemViewContent(beneficiaryDetail.getBranchCode());
            binding.bankLineItemView.setVisibility(View.GONE);
            binding.branchLineItemView.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(beneficiaryDetail.getActHolder())) {
                binding.accountHolderNameCodeLineItemView.setLineItemViewContent(beneficiaryDetail.getActHolder());
            }
        } else {
            binding.institutionCodeLineItemView.setVisibility(View.GONE);
        }

        binding.buyBeneficiaryView.setBeneficiary(beneficiary);
        binding.bankLineItemView.setLineItemViewContent(beneficiaryDetail.getBankName());
        binding.branchLineItemView.setLineItemViewContent(StringExtensions.insertSpaceAtIncrements(beneficiaryDetail.getBranchCode(), 3));
        binding.theirReferenceLineItemView.setLineItemViewContent(beneficiaryDetail.getBenReference());
        binding.myReferenceLineItemView.setLineItemViewContent(beneficiaryDetail.getMyReference());
        binding.theirNotificationLineItemView.setLineItemViewContent(beneficiaryDetail.getBenNoticeDetail());
        binding.myNotificationLineItemView.setLineItemViewContent(beneficiaryDetail.getMyNoticeDetail());
        binding.mobileOperatorLineItemView.setVisibility(View.GONE);
    }

    private void setupPrepaidElectricityBeneficiaryDetailView(BeneficiaryListItem beneficiary) {
        if (meterNumberObject != null && beneficiaryDetail != null) {
            beneficiary.setAccountNumber(StringExtensions.toFormattedAccountNumber(beneficiaryDetail.getActNo()));
            binding.buyBeneficiaryView.setBeneficiary(beneficiary);
            binding.customerNameForMeterSecondaryContentAndLabelView.setContentText(meterNumberObject.getCustomerName());
            binding.addressSecondaryContentAndLabelView.setContentText(meterNumberObject.getCustomerAddress());
            binding.utilityProviderSecondaryContentAndLabelView.setContentText(meterNumberObject.getUtility());
            binding.arrearsAmountSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(meterNumberObject.getArrearsAmount()));
        }
        binding.customerNameForMeterSecondaryContentAndLabelView.setVisibility(View.VISIBLE);
        binding.addressSecondaryContentAndLabelView.setVisibility(View.VISIBLE);
        binding.utilityProviderSecondaryContentAndLabelView.setVisibility(View.VISIBLE);
        binding.arrearsAmountSecondaryContentAndLabelView.setVisibility(View.VISIBLE);
        binding.bankLineItemView.setVisibility(View.GONE);
        binding.branchLineItemView.setVisibility(View.GONE);
        binding.theirReferenceLineItemView.setVisibility(View.GONE);
        binding.myReferenceLineItemView.setVisibility(View.GONE);
        binding.myNotificationLineItemView.setVisibility(View.GONE);
        binding.institutionCodeLineItemView.setVisibility(View.GONE);
        binding.theirNotificationLineItemView.setVisibility(View.GONE);
        binding.mobileOperatorLineItemView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_menu_dark, menu);
        MenuItem item = menu.findItem(R.id.action_edit);
        item.setVisible(!(featureSwitchingToggles.getEditPrepaidElectricityBeneficiary() == FeatureSwitchingStates.GONE.getKey() && PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(beneficiaryType)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            showEditBenActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditBenActivity() {
        if (PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(beneficiaryType)) {
            navigateToEditPrepaidElectricityBeneficiary();
        } else {
            Intent editBeneficiary = new Intent(this, EditBeneficiaryActivity.class);
            editBeneficiary.putExtra(AppConstants.RESULT, beneficiaryDetail);
            editBeneficiary.putExtra(BeneficiaryLandingActivity.BENEFICIARY_TYPE, beneficiaryType);
            startActivityForResult(editBeneficiary, BMBConstants.EDIT_BEN_RESPONSE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BMBConstants.EDIT_BEN_RESPONSE) {
                if (data != null && data.getExtras() != null) {
                    final Bundle extras = data.getExtras();
                    isBenEdited = extras.getBoolean(IS_BENEFICIARY_EDITED);
                    if (isBenEdited) {
                        if (PASS_CASHSEND.equals(beneficiaryType)) {
                            AnalyticsUtil.INSTANCE.tagCashSend("BeneficiaryEditSuccess_ScreenDisplayed");
                        }
                        toastLong(R.string.beneficiary_updated_success);
                        presenter.onResultReceivedFromActivity(beneficiaryDetail.getBeneficiaryId(), beneficiaryType);
                    } else {
                        showGenericErrorMessage();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        handleBackAction();
    }

    private void handleBackAction() {
        if (isBenEdited) {
            presenter.onBackClicked();
        } else {
            finish();
        }
    }

    public void viewTransactionDetails(TransactionObject transactionObject) {
        if (transactionObject != null && !PASS_AIRTIME.equalsIgnoreCase(beneficiaryType) && !PASS_CASHSEND.equalsIgnoreCase(beneficiaryType) && !PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(beneficiaryType)) {
            presenter.onTransactionItemClicked(beneficiaryDetail.getBeneficiaryId(), transactionObject.getReferenceNumber(), beneficiaryType);
        }

        if (PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(beneficiaryType)) {
            Intent intent = new Intent(this, PrepaidElectricityHistoryActivity.class);
            intent.putExtra(PPE_BENEFICIARY_OBJECT, beneficiaryDetail);
            intent.putExtra(PPE_TRANSACTION_OBJECT, transactionObject);
            startActivity(intent);
        }
    }

    @Override
    public void navigateToPaymentBeneficiaryDetail() {
        final Intent payNewBeneficiary = new Intent(this, PaymentDetailsActivity.class);
        payNewBeneficiary.putExtra(IIP_STATUS, beneficiaryDetail.getBranchIIPStatus());
        payNewBeneficiary.putExtra(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetail);
        payNewBeneficiary.putExtra(FROM_ACTIVITY, MANAGE_BENEFICIARY);
        payNewBeneficiary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        payNewBeneficiary.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        startActivity(payNewBeneficiary);
    }

    @Override
    public void navigateToCashSendBeneficiaryDetail(BeneficiaryDetailObject successResponse) {
        Intent intent = new Intent(BeneficiaryDetailsActivity.this, CashSendBeneficiaryActivity.class);
        intent.putExtra(RESULT, successResponse);
        startActivityIfAvailable(intent);
    }

    @Override
    public void navigateToAirtimeDetailView(ResponseObject successResponse) {
        AirtimeBuyBeneficiary airtimeBuyBeneficiary = (AirtimeBuyBeneficiary) successResponse;
        Intent intent = new Intent(BeneficiaryDetailsActivity.this, PurchaseDetailsActivity.class);
        intent.putExtra(AppConstants.RESULT, airtimeBuyBeneficiary);
        if (!isFinishing()) {
            startActivity(intent);
        }
    }

    @Override
    public void navigateToElectricityBeneficiaryDetail() {
        Intent intent = new Intent(this, PrepaidElectricityActivity.class);
        intent.putExtra(ELECTRICITY_BENEFICIARY_DETAILS, meterNumberObject);
        intent.putExtra(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetail);
        intent.putExtra(VIEW_PREPAID_ELECTRICITY_BENEFICIARY_DETAILS, true);
        startActivity(intent);
    }

    @Override
    public void setTransactionList(BeneficiaryDetailObject successResponse) {
        beneficiaryDetail = successResponse;
        setupViews();
        if (beneficiaryDetail != null) {
            final ArrayList<TransactionObject> transactions = filterOutFailedTransactions(beneficiaryDetail.getTransactions());
            beneficiaryTransactionListAdapter = new BeneficiaryTransactionsAdapter(BeneficiaryDetailsActivity.this, transactions);
            binding.transactionHistoryRecyclerView.setAdapter(beneficiaryTransactionListAdapter);
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    private ArrayList<TransactionObject> filterOutFailedTransactions(ArrayList<TransactionObject> transactions) {
        ArrayList<TransactionObject> filteredTransactionList = new ArrayList<>();
        if (transactions != null) {
            for (TransactionObject transaction : transactions) {
                if (TRANSACTION_STATUS_UNKNOWN.equalsIgnoreCase(transaction.getTransactionStatus()) || TRANSACTION_STATUS_UNKNOWN_AFRIKAANS.equalsIgnoreCase(transaction.getTransactionStatus())) {
                    continue;
                }
                filteredTransactionList.add(transaction);
            }
        }
        return filteredTransactionList;
    }

    @Override
    public void navigateBackToBeneficiaryList(BeneficiaryListObject successResponse) {
        BeneficiaryListObject benResponseObj = AbsaCacheManager.getInstance().getCachedBeneficiaryListObject() != null ? AbsaCacheManager.getInstance().getCachedBeneficiaryListObject() : successResponse;
        Intent intent = new Intent(BeneficiaryDetailsActivity.this, BeneficiaryLandingActivity.class);
        intent.putExtra(AppConstants.RESULT, benResponseObj);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        startActivity(intent);
    }

    @Override
    public void navigateToBeneficiaryTransactionItem(ViewTransactionDetails successResponse) {
        Intent transactionHistoryIntent = new Intent(BeneficiaryDetailsActivity.this, ProofOfPaymentHistoryActivity.class);
        transactionHistoryIntent.putExtra(BeneficiaryDetailsActivity.class.getName(), successResponse);
        transactionHistoryIntent.putExtra(BMBConstants.RESULT, beneficiaryDetail);
        startActivity(transactionHistoryIntent);
    }

    @Override
    public void setBeneficiaryImage(String beneficiaryImage) {
        byte[] benImage = Base64.decode(beneficiaryImage, Base64.URL_SAFE);
        beneficiaryDetail.setImageData(benImage);
        try {
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(benImage, 0, benImage.length);

            binding.buyBeneficiaryView.setImage(imgBitmap);

        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    public void navigateToEditPrepaidElectricityBeneficiary() {
        Intent intent = new Intent(this, EditBeneficiaryActivity.class);
        intent.putExtra(AppConstants.RESULT, beneficiaryDetail);
        intent.putExtra(BeneficiaryLandingActivity.BENEFICIARY_TYPE, beneficiaryType);
        intent.putExtra(ELECTRICITY_BENEFICIARY_DETAILS, meterNumberObject);
        startActivityForResult(intent, BMBConstants.EDIT_BEN_RESPONSE);
    }
}