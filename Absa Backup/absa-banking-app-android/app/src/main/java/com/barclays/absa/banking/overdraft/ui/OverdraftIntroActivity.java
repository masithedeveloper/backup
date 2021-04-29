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
package com.barclays.absa.banking.overdraft.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.ActivityOverdraftIntroBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class OverdraftIntroActivity extends BaseActivity implements OverdraftContracts.OverdraftIntroView {
    double newLimit;
    public static final String OVERDRAFT = "Overdraft";
    OverdraftContracts.OverdraftIntroPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOverdraftIntroBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_overdraft_intro, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.need_emergency_funds));
        presenter = new OverdraftIntroPresenter(new WeakReference<>(this));
        newLimit = getIntent().getDoubleExtra(IntentFactory.NEW_OVERDRAFT_LIMIT, 0);
        CommonUtils.makeTextClickable(this,
                R.string.overdraft_intro_disclaimer,
                getString(R.string.overdraft_intro_hyperlink), performActionUponTextClicked,
                binding.overdraftDisclaimer, R.color.graphite);
        String overdraftNewLimit = TextFormatUtils.formatBasicAmount(newLimit);
        binding.overdraftOfferView.setText(getString(R.string.overdraft_intro_qualify_title, overdraftNewLimit));
        binding.applyNowButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Overdraft Introduction: get started button");
            presenter.applyNowButtonClicked();
        });
        binding.backImageView.setOnClickListener(v -> onBackPressed());
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "OverdraftHub");
    }

    private ClickableSpan performActionUponTextClicked = new ClickableSpan() {
        @Override
        public void onClick(@NotNull View widget) {
            presenter.onAbsaWebsiteClicked();
        }
    };

    @Override
    public void navigateToApplyOverdraftStep1() {
        startActivity(IntentFactoryOverdraft.getOverdraftSteps(this, newLimit));
    }

    @Override
    public void navigateToAbsaWebsite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + getString(R.string.overdraft_link)));
        startActivity(browserIntent);
    }
}