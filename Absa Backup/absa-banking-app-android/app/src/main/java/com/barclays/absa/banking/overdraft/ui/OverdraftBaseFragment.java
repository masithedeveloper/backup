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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftCheckSetupAndConfirmFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftCreditMarketingConsentFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftDeclarationFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftIllustrativeCostFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftSetupFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftTellUsAboutYourSelfFragment;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.PdfUtil;

import org.jetbrains.annotations.NotNull;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public abstract class OverdraftBaseFragment<T extends ViewDataBinding> extends AbsaBaseFragment<T> {
    private BaseActivity baseActivity;

    protected void show(Fragment fragment) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).startFragment(fragment, true, BaseActivity.AnimationType.FADE);
        }
    }

    public void setToolBar(String title, @NotNull View.OnClickListener onClickListener) {
        if (baseActivity != null) {
            baseActivity.setToolBarBackFragment(title, onClickListener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        baseActivity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        BMBLogger.d("x-class:", getClass().getSimpleName());
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.cancel_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            showAlertDialogCancelOverdraftApplication(getContext());

            Fragment currentFragment = getParentActivity().getCurrentFragment();

            if (currentFragment != null) {
                if (currentFragment instanceof OverdraftDeclarationFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_DeclarationScreen_CancelButtonClicked");
                } else if (currentFragment instanceof OverdraftSetupFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_OverdraftSetupScreen_CancelButtonClicked");
                } else if (currentFragment instanceof OverdraftTellUsAboutYourSelfFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TellUsAboutYourselfScreen_CancelButtonClicked");
                } else if (currentFragment instanceof OverdraftCheckSetupAndConfirmFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_ConfirmApplicationScreen_CancelButtonClicked");
                } else if (currentFragment instanceof OverdraftIncomeAndExpenseFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_CancelButtonClicked");
                } else if (currentFragment instanceof OverdraftIllustrativeCostFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IllustrativeCostScreen_CancelButtonClicked");
                } else if (currentFragment instanceof OverdraftCreditMarketingConsentFragment) {
                    AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_CancelButtonClicked");
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void animate(@NonNull View view, int resid) {
        Activity activity = getActivity();
        if (activity != null)
            view.startAnimation(AnimationUtils.loadAnimation(getActivity(), resid));
    }

    protected OverdraftStepsActivity getParentActivity() {
        OverdraftStepsActivity baseActivity = (OverdraftStepsActivity) getActivity();
        return baseActivity != null ? baseActivity : (OverdraftStepsActivity) BMBApplication.getInstance().getTopMostActivity();
    }

    protected void showPersonalClientAgreement() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            PdfUtil.INSTANCE.showTermsAndConditionsClientAgreement(baseActivity, CustomerProfileObject.getInstance().getClientTypeGroup());
        }
    }

    private void showAlertDialogCancelOverdraftApplication(final Context context) {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.overdraft_cancel_popup_title))
                .message(getString(R.string.overdraft_cancel_popup_message))
                .positiveDismissListener((dialog, which) -> {
                    Intent intent = new Intent(context, HomeContainerActivity.class);
                    context.startActivity(intent);
                }));
    }
}
