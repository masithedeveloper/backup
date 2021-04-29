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

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.OverdraftTellUsYourselfFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.overdraft.ui.IntentFactoryOverdraft;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftTellUsAboutYourSelfFragment extends OverdraftBaseFragment<OverdraftTellUsYourselfFragmentBinding> implements OverdraftContracts.TellUsAboutYourSelfView {
    private OverdraftContracts.TellUsAboutYourSelfPresenter presenter;
    private String dateOfRehabReview;
    private boolean onDebtCounselling;

    @Override
    public void onResume() {
        super.onResume();
        getParentActivity().setStep(1);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_tell_us_yourself_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar(getString(R.string.overdraft_tell_us_about_yourself_title), v -> getParentActivity().finish());
        initViews();
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourSelfScreen_ScreenDisplayed");
    }

    private void initViews() {
        presenter = new OverdraftTellUsAboutYourSelfPresenter(new WeakReference<>(this));
        setUpDebtCounsellingOptionsRadioButtons();
        setUpDebtInsolventOptionsRadioButtons();
        initializeInsolvencyOptions();
    }

    private void initializeInsolvencyOptions() {
        SelectorList<StringItem> reasonsForDebtCounsellingInsolvent = new SelectorList<>();
        reasonsForDebtCounsellingInsolvent.add(new StringItem(getString(R.string.i_am_insolvent)));
        reasonsForDebtCounsellingInsolvent.add(new StringItem(getString(R.string.currently_under_debt_review)));
        binding.debtCounsellingReasons.setDataSource(reasonsForDebtCounsellingInsolvent);

        SelectorList<StringItem> debtReviewInsolvencyInPastOptions = new SelectorList<>();
        debtReviewInsolvencyInPastOptions.add(new StringItem(getString(R.string.yes)));
        debtReviewInsolvencyInPastOptions.add(new StringItem(getString(R.string.no)));
        binding.pastDebtReviewOptions.setDataSource(debtReviewInsolvencyInPastOptions);

        binding.nextButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourSelfScreen_BackButtonClicked");
            presenter.onNextButtonClicked();
        });

        binding.wasInsolventDateSelector.setOnClickListener(v -> openCalendarWhenUnderDebtReview());
    }

    @Override
    public void openCalendarWhenUnderDebtReview() {
        showDatePicker((datePicker, year, month, day) -> {
            dateOfRehabReview = String.format(BMBApplication.getApplicationLocale(), "%s-%s-%s", year, month + 1, day);
            binding.wasInsolventDateSelector.setSelectedValue(DateUtils.formatDateMonth(dateOfRehabReview));
            animate(binding.wasInsolventDateSelector, R.anim.bounce_long);
        });
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme
                , onDateSetListener
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public void navigateToNextScreen() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourselfScreen_NextButtonClicked");
        if (binding.creditStatusOptionsRadioGroup.getSelectedValue() == null) {
            animate(binding.creditStatusOptionsRadioGroup, R.anim.shake);
            binding.creditStatusOptionsRadioGroup.setErrorMessage(getString(R.string.overdraft_select_option));
        } else if (onDebtCounselling) {
            if (binding.creditStatusOptionsRadioGroup.getSelectedValue() == null) {
                animate(binding.creditStatusOptionsRadioGroup, R.anim.shake);
                binding.creditStatusOptionsRadioGroup.setErrorMessage(getString(R.string.overdraft_select_option));
            } else if (binding.creditStatusOptionsRadioGroup.getSelectedIndex() == 0) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourSelfScreen_UnableToContinueErrorScreenDisplayed");
                startActivity(IntentFactoryOverdraft.getUnableToContinueScreen(getActivity(), R.string.unable_to_continue,
                        R.string.overdraft_unfortunately_you_cannot_apply_for_an_overdraft));
            } else {
                show(OverdraftDeclarationFragment.newInstance());
            }
        } else if (!onDebtCounselling) {
            if (binding.pastDebtReviewOptions.getSelectedValue() == null) {
                animate(binding.pastDebtReviewOptions, R.anim.shake);
                binding.pastDebtReviewOptions.setErrorMessage(getString(R.string.overdraft_select_option));
            } else if (binding.pastDebtReviewOptions.getSelectedIndex() == 0) {
                if (binding.debtCounsellingReasons.getSelectedValue() == null) {
                    animate(binding.debtCounsellingReasons, R.anim.shake);
                    binding.debtCounsellingReasons.setErrorMessage(getString(R.string.overdraft_select_option));
                } else if (binding.debtCounsellingReasons.getSelectedValue() != null && binding.wasInsolventDateSelector.getSelectedValue().isEmpty()) {
                    animate(binding.wasInsolventDateSelector, R.anim.shake);
                } else {
                    show(OverdraftDeclarationFragment.newInstance());
                }
            } else {
                show(OverdraftDeclarationFragment.newInstance());
            }
        }
    }

    @Override
    public void showInsolventAndDebtReviewReasons() {
        binding.divider13.setVisibility(View.VISIBLE);
        binding.debtReviewOptionReasonsHeadingView.setHeadingTextView(getString(R.string.overdraft_select_reason));
        binding.debtReviewOptionReasonsHeadingView.setVisibility(View.VISIBLE);
        binding.debtCounsellingReasons.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInsolventAndDebtReviewInThePastOptions() {
        binding.divider2.setVisibility(View.VISIBLE);
        binding.debtReviewOptionHeadingView.setHeadingTextView(getString(R.string.overdraft_have_you_ever_been_insolvent));
        binding.debtReviewOptionHeadingView.setVisibility(View.VISIBLE);
        binding.debtCounsellingReasons.setVisibility(View.GONE);
        binding.pastDebtReviewOptions.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInsolventDateSelector() {
        binding.wasInsolventDateSelector.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInsolventAndDebtReviewInThePastOptions() {
        clearInsolventAndDebtReviewSelection();
        binding.divider2.setVisibility(View.GONE);
        binding.debtReviewOptionHeadingView.setVisibility(View.GONE);
        binding.pastDebtReviewOptions.setVisibility(View.GONE);
    }

    @Override
    public void hideDebtReviewReasonsOptions() {
        clearInsolventAndDebtReviewReasonsSelection();
        binding.divider13.setVisibility(View.GONE);
        binding.debtReviewOptionReasonsHeadingView.setVisibility(View.GONE);
        binding.debtCounsellingReasons.setVisibility(View.GONE);
    }

    @Override
    public void hideInsolventDateSelector() {
        clearInsolventDateSelection();
        binding.wasInsolventDateSelector.setVisibility(View.GONE);
    }

    private void setUpDebtCounsellingOptionsRadioButtons() {
        SelectorList<StringItem> debtCounsellingOptions = new SelectorList<>();
        debtCounsellingOptions.add(new StringItem(getString(R.string.yes)));
        debtCounsellingOptions.add(new StringItem(getString(R.string.no)));
        binding.creditStatusOptionsRadioGroup.setDataSource(debtCounsellingOptions, -1);
        binding.creditStatusOptionsRadioGroup.setItemCheckedInterface(index -> {
            if (index == 0) {
                onDebtCounselling = true;
                binding.creditStatusOptionsRadioGroup.hideError();
                presenter.onDebtCounsellingOrPendingDebtReviewOrInsolventYesSelected();
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourselfScreen_YesDebtCounsellingRadioButtonClicked");
            } else if (index == 1) {
                onDebtCounselling = false;
                binding.creditStatusOptionsRadioGroup.hideError();
                presenter.onDebtCounsellingOrPendingDebtReviewOrInsolventNoSelected();
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourselfScreen_NoDebtCounsellingRadioButtonClicked");
            }
        });
    }

    private void setUpDebtInsolventOptionsRadioButtons() {
        binding.pastDebtReviewOptions.setItemCheckedInterface(index -> {
            if (index == 0) {
                binding.pastDebtReviewOptions.hideError();
                presenter.onInsolventOrUnderDebtReviewYesOptionSelected();
                setUpInsolventOrDebtReviewRadioButtons();
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourselfScreen_YesHaveBeenInsolventRadioButtonClicked");
            } else if (index == 1) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourselfScreen_NoHaveBeenInsolventRadioButtonClicked");
                binding.pastDebtReviewOptions.hideError();
                presenter.onInsolventOrUnderDebtReviewNoOptionSelected();
            }
        });
    }

    private void setUpInsolventOrDebtReviewRadioButtons() {
        binding.debtCounsellingReasons.setItemCheckedInterface(index -> {
            binding.debtCounsellingReasons.hideError();
        });
    }

    public static OverdraftTellUsAboutYourSelfFragment getInstance() {
        return new OverdraftTellUsAboutYourSelfFragment();
    }

    private void clearInsolventAndDebtReviewSelection() {
        binding.pastDebtReviewOptions.clearGroupChecks();
    }

    private void clearInsolventAndDebtReviewReasonsSelection() {
        binding.debtCounsellingReasons.clearGroupChecks();
    }

    private void clearInsolventDateSelection() {
        binding.wasInsolventDateSelector.clear();
    }
}
