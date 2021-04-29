/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.account.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.account.ui.graph.BalanceGraphWidget;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.databinding.AccountActivityHeaderFragmentBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants.AccountTypeEnum;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.OperatorPermissionUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static com.barclays.absa.banking.framework.app.BMBConstants.AccountTypeEnum.noticeDeposit;

public class AccountActivityHeaderFragment extends Fragment implements View.OnClickListener {
    private AccountActivityHeaderFragmentBinding binding;
    private AccountHubViewModel viewModel;
    private Animation graphAnimation;
    private AccountObject account;
    private StatementDialogUtils statementDialogUtils;

    public static AccountActivityHeaderFragment newInstance() {
        return new AccountActivityHeaderFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(AccountHubViewModel.class);
            viewModel.getAccountObjectLiveData().observe(this, this::storeAccountObject);
            viewModel.getAccountDetailLiveData().observe(this, accountDetail -> {
                if (!isBalanceShowing()) {
                    graphData(accountDetail);
                }
            });
        }
    }

    private void graphData(AccountDetail accountDetail) {
        binding.balanceGraphWidget.setAccountDetail(accountDetail);
        binding.balanceGraphWidget.startGraphing();
        setGraphNumberOfDays(accountDetail);
    }

    private void setGraphNumberOfDays(AccountDetail accountDetail) {
        if (accountDetail != null) {
            String fromDateString = accountDetail.getFromDate(), toDateString = accountDetail.getToDate();
            AccountActivity accountActivity = getAccountActivity();
            statementDialogUtils = new StatementDialogUtils(accountActivity);
            if (binding != null && accountActivity != null) {
                if (fromDateString != null && toDateString != null) {
                    Calendar fromCalendar = DateUtils.getCalendarObj(fromDateString);
                    Calendar toCalendar = DateUtils.getCalendarObj(toDateString);
                    if (fromCalendar != null && toCalendar != null) {
                        statementDialogUtils.setFromDate(fromCalendar.getTime());
                        statementDialogUtils.setToDate(toCalendar.getTime());
                        if (!accountDetail.getTransactions().isEmpty()) {
                            try {
                                String firsDateString = accountDetail.getTransactions().get(accountDetail.getTransactions().size() - 1).getTransactionDate();
                                Date firstDate = BalanceGraphWidget.transactionDateFormat.parse(firsDateString);
                                long diffInDays = DateUtils.getDateDiff(firstDate, statementDialogUtils.getToDate()) + 1;

                                if (accountActivity.canViewBalances()) {
                                    binding.numberOfDaysHeaderTextView.setText(String.format(Locale.US, "%s (%s %s)",
                                            accountActivity.getString(R.string.account_hub_balance),
                                            diffInDays, accountActivity.getString(R.string.account_hub_days)));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BMBLogger.d("x-c", "onCreate" + getClass().getSimpleName());
        graphAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_graph);
    }

    @Override
    public void onStart() {
        super.onStart();
        BMBLogger.d("x-c", "onStart" + getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        BMBLogger.d("x-c", "onResume" + getClass().getSimpleName());
        String balance = "R 0.00";
        if (account != null) {
            balance = account.getCurrentBalance().getAmount();
            if (AccountTypeEnum.homeLoan.toString().equalsIgnoreCase(account.getAccountType())) {
                balance = account.getBalance().getAmount();
            }
            if (AccountTypeEnum.cia.toString().equalsIgnoreCase(account.getAccountType())) {
                binding.togglerImageView.setVisibility(View.GONE);
                binding.payImageView.setVisibility(View.GONE);
                binding.transferImageView.setVisibility(View.GONE);
                binding.cashsendImageView.setVisibility(View.GONE);
                binding.currencyTitleTextView.setVisibility(View.VISIBLE);
                binding.currencyTitleTextView.setText(account.getCurrency());
            }
            String currency = account.getCurrency().equalsIgnoreCase("R") ? account.getCurrency() : Currency.getInstance(account.getCurrency()).getSymbol();
            binding.currentBalanceAmountTextView.setText(getAccountActivity().getString(R.string.account_hub_balance_amount, currency, TextFormatUtils.formatBasicAmount(balance)));

            applyPermissions();
        } else {
            binding.currentBalanceAmountTextView.setText(balance);
        }
    }

    private void storeAccountObject(AccountObject accountObject) {
        account = accountObject;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, Bundle savedInstanceState) {
        BMBLogger.d("x-c", "onCreateView" + getClass().getSimpleName());
        binding = DataBindingUtil.inflate(inflater, R.layout.account_activity_header_fragment, container, false);

        binding.togglerImageView.setOnClickListener(this);
        binding.payImageView.setOnClickListener(this);
        binding.transferImageView.setOnClickListener(this);
        binding.cashsendImageView.setOnClickListener(this);
        binding.requestAccessTextView.setOnClickListener(this);
        return binding.getRoot();
    }

    private void applyPermissions() {
        if (!canViewBalances()) {
            binding.numberOfDaysHeaderTextView.setText(getAccountActivity().getString(R.string.account_hub_balance));
            binding.numberOfDaysHeaderTextView.setVisibility(View.VISIBLE);
            binding.requestAccessTextView.setVisibility(View.VISIBLE);
            binding.notAuthorisedTextView.setVisibility(View.VISIBLE);

            binding.currentBalanceTextView.setVisibility(View.GONE);
            binding.balanceGraphWidget.setVisibility(View.GONE);
            binding.togglerImageView.setVisibility(View.GONE);
            binding.containsUnclearedTextView.setVisibility(View.GONE);
            binding.currentBalanceAmountTextView.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.balanceLayout.getLayoutParams();
            layoutParams.topMargin = (int) getResources().getDimension(R.dimen.dimen_80dp);
            binding.balanceLayout.setLayoutParams(layoutParams);
            if (noticeDeposit.toString().equals(account.getAccountType())) {
                binding.payImageView.setVisibility(View.GONE);
                binding.transferImageView.setVisibility(View.GONE);
                binding.cashsendImageView.setVisibility(View.GONE);
            }
        }

        if (getActivity() != null) {
            if (((BaseActivity) getActivity()).numberOfNoneWimiAccounts() <= 1 || AccountTypeEnum.cia.toString().equalsIgnoreCase(account.getAccountType())) {
                binding.transferImageView.setVisibility(View.GONE);
            } else {
                binding.transferImageView.setVisibility(View.VISIBLE);
            }
        }

        if (!OperatorPermissionUtils.canViewBalances(account) || noticeDeposit.toString().equals(account.getAccountType())) {
            binding.payImageView.setVisibility(View.GONE);
            binding.transferImageView.setVisibility(View.GONE);
            binding.cashsendImageView.setVisibility(View.GONE);
        }
    }

    private AccountActivity getAccountActivity() {
        return (AccountActivity) getActivity();
    }

    @Override
    public void onClick(View view) {
        getAccountActivity().animate(view, R.anim.contract_horizontal);
        if (view.getId() == R.id.togglerImageView) {
            toggleBetweenGraphAndCurrentBalance();
        } else {
            BaseActivity.preventDoubleClick(view);
            switch (view.getId()) {
                case R.id.requestAccessTextView:
                    getAccountActivity().animate(view, R.anim.expand_horizontal);
                    BaseAlertDialog.INSTANCE.showRequestAccessAlertDialog(getString(R.string.account_balance));
                    break;
                case R.id.cashsendImageView:
                    getAccountActivity().navigateToCashSend();
                    getAccountActivity().trackButtonClick("Header CashSend button");
                    break;
                case R.id.payImageView:
                    getAccountActivity().navigateToPay();
                    getAccountActivity().trackButtonClick("Header Pay button");
                    break;
                case R.id.transferImageView:
                    getAccountActivity().navigateToTransfer();
                    getAccountActivity().trackButtonClick("Header Transfer button");
                    break;
            }
        }
    }

    private boolean canViewBalances() {
        return OperatorPermissionUtils.canViewBalances(account);
    }

    private void toggleBetweenGraphAndCurrentBalance() {
        if (canViewBalances()) {
            boolean isBalanceShowing = isBalanceShowing();
            if (!isBalanceShowing) {
                binding.containsUnclearedTextView.setVisibility(View.GONE);
            }
            for (int v : new int[]{R.id.balanceGraphWidget, R.id.numberOfDaysHeaderTextView}) {
                binding.getRoot().findViewById(v).setVisibility(isBalanceShowing ? View.VISIBLE : View.GONE);
            }
            if (isBalanceShowing) {
                showBalanceGraph();
            } else {
                graphAnimation.cancel();
            }

            binding.balanceLayout.setVisibility(isBalanceShowing ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isBalanceShowing() {
        return binding != null && binding.balanceLayout.getVisibility() == View.VISIBLE;
    }

    private void showBalanceGraph() {
        AccountDetail accountDetail = viewModel.getAccountDetailLiveData().getValue();
        if (accountDetail != null) {
            graphData(accountDetail);
        }
    }
}
