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
package com.barclays.absa.banking.buy.ui.airtime.buyNew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.buy.ui.airtime.existing.PurchaseDetailsActivity;
import com.barclays.absa.banking.databinding.AddNewAirtimeBenRebuildBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;

import java.lang.ref.WeakReference;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.buy.ui.airtime.existing.PurchaseDetailsActivity.ADD_BENEFICIARY_COMPLETE;

public class AddNewAirtimeBeneficiaryDetailActivity extends BaseActivity implements AddNewAirtimeBeneficiaryDetailView {

    private Bitmap benImage;
    private Bundle bundle;
    public static final String BEN_DETAILS = "BEN_DETAILS";
    public static final String BEN_NAME = "BEN_NAME";
    public static final String BEN_NETWORK_PROVIDER = "BEN_NETWORK_PROVIDER";
    public static final String BEN_MOBILE_NUMBER = "BEN_MOBILE_NUMBER";
    public static final String TRANSACTION_REF_NO = "TRANSACTION_REF_NO";
    public static final String BEN_PROFILE_IMAGE = "BEN_PROFILE_IMAGE";

    private AddNewAirtimeBeneficiaryDetailPresenter presenter;
    private String beneficiaryReference;
    private AddBeneficiaryDAO addBeneficiaryDAO;
    private BeneficiaryView prepaidBeneficiaryView;
    private AddNewAirtimeBenRebuildBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.add_new_airtime_ben_rebuild, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.airtime_buy_prepaid);

        prepaidBeneficiaryView = findViewById(R.id.prepaidBeneficiaryView);

        mScreenName = BMBConstants.NEW_BENEFICIARY_DETAILS_OVERVIEW_CONST;
        mSiteSection = BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.NEW_BENEFICIARY_DETAILS_OVERVIEW_CONST,
                BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST, BMBConstants.TRUE_CONST);
        init();
        populateData();
        setupTalkBack();
        addBeneficiaryDAO = new AddBeneficiaryDAO(this);
        presenter = new AddNewAirtimeBeneficiaryDetailPresenter(new WeakReference<>(this), addBeneficiaryDAO, benImage);
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String name = binding.nameLabelView.getContentTextViewValue();
            final String phoneNumber = binding.numberLabelView.getContentTextViewValue();
            final String mobileNetwork = binding.networkLabelView.getContentTextViewValue();
            binding.nameLabelView.setContentDescription(getString(R.string.talkback_prepaid_beneficiary_name_entered, name));
            binding.numberLabelView.setContentDescription(getString(R.string.talkback_prepaid_mobile_number_entered, phoneNumber));
            binding.networkLabelView.setContentDescription(getString(R.string.talkback_prepaid_network_selected, mobileNetwork));
            binding.saveBeneficiaryButton.setContentDescription(getString(R.string.talkback_prepaid_save_beneficiary));
        }
    }

    private void init() {
        Button addNewBeneficiaryButton = findViewById(R.id.saveBeneficiaryButton);
        addNewBeneficiaryButton.setOnClickListener(v -> presenter.requestAddAirtimeBenConfirmation(beneficiaryReference));
    }

    private void populateData() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(BEN_DETAILS)) {
            bundle = getIntent().getBundleExtra(BEN_DETAILS);
            if (bundle == null) {
                return;
            }

            beneficiaryReference = bundle.getString(TRANSACTION_REF_NO);
            byte[] bitMapByteArray = bundle.getByteArray(BEN_PROFILE_IMAGE);
            benImage = BitmapFactory.decodeByteArray(bitMapByteArray, 0, bitMapByteArray.length);
            if (benImage != null) {
                binding.prepaidBeneficiaryView.getRoundedImageView().setImageBitmap(benImage);
                prepaidBeneficiaryView.setImage(benImage);
            }
            String name = bundle.getString(BEN_NAME);
            String number = StringExtensions.toFormattedCellphoneNumber(bundle.getString(BEN_MOBILE_NUMBER));
            String network_provider = bundle.getString(BEN_NETWORK_PROVIDER);

            prepaidBeneficiaryView.setBeneficiary(new BeneficiaryListItem(name, number, null));
            binding.nameLabelView.setContentText(name);
            binding.numberLabelView.setContentText(number);
            binding.networkLabelView.setContentText(network_provider);
        }

    }

    @Override
    public void showResultScreen(String transactionStatus, String beneficiaryId, String message) {
        Intent resultIntent;
        if (BMBConstants.SUCCESS.equalsIgnoreCase(transactionStatus)) {
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_AIRTIME);
            mScreenName = BMBConstants.BENEFICIARY_ADDED_SUCCESS_CONST;
            mSiteSection = BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST;
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.BENEFICIARY_ADDED_SUCCESS_CONST,
                    BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST, BMBConstants.TRUE_CONST);
            resultIntent = IntentFactoryGenericResult.getSuccessfulResultScreenBuilder(this, R.string.add_new_prepaid_success_msg)
                    .setGenericResultHomeButtonTop(this)
                    .setGenericResultBottomButton(R.string.add_new_prepaid_result_screen_buy_new, v -> presenter.requestAirtimeBeneficiaryDetails(beneficiaryId)).build();
            startActivity(resultIntent);
        } else {
            showErrorResultScreen(message);
        }
    }

    private void showErrorResultScreen(String message) {
        Intent resultIntent;
        mScreenName = BMBConstants.ADD_BENEFICIARY_UNSUCCESSFUL_CONST;
        mSiteSection = BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_UNSUCCESSFUL_CONST,
                BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST, BMBConstants.TRUE_CONST);
        resultIntent = IntentFactoryGenericResult.getFailureResultScreen(this, R.string.unable_to_save_prepaid_ben, message);
        startActivity(resultIntent);
    }

    @Override
    public void showResultScreenWithMessage(String transactionStatus, String beneficiaryId, String msg) {
        showErrorResultScreen(getString(R.string.image_size_limit));
    }

    @Override
    public void navigateToPrepaidPurchaseDetailsScreen(AirtimeBuyBeneficiary airtimeBuyBeneficiaryDetails) {
        Intent intent = new Intent(this, PurchaseDetailsActivity.class);
        intent.putExtra(RESULT, airtimeBuyBeneficiaryDetails);
        intent.putExtra(ADD_BENEFICIARY_COMPLETE, true);
        startActivity(intent);
    }

    @Override
    public void showErrorScreen(String message) {
        showErrorResultScreen(message);
    }
}