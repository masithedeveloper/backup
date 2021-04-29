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
package com.barclays.absa.banking.account.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.OverdraftGadgets;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions;
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment;
import com.barclays.absa.banking.databinding.AccountActivityBinding;
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment;
import com.barclays.absa.banking.express.transactionHistory.TransactionHistoryViewModel;
import com.barclays.absa.banking.express.transactionHistory.dto.AccountHistoryLines;
import com.barclays.absa.banking.express.transactionHistory.dto.HistoryRequest;
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.moneyMarket.ui.MoneyMarketActivity;
import com.barclays.absa.banking.moneyMarket.ui.MoneyMarketViewModel;
import com.barclays.absa.banking.overdraft.ui.IntentFactoryOverdraft;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftOffersFragment;
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.utils.ToolBarUtils;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.banking.transfer.TransferFundsActivity;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.OperatorPermissionUtils;
import com.barclays.absa.utils.SharedPreferenceService;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import styleguide.bars.CollapsingAppBarView;

import static com.barclays.absa.banking.moneyMarket.ui.MoneyMarketActivityKt.MONEY_MARKET_ACCOUNT;
import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class AccountActivity extends BaseActivity implements AccountHubView, TransactionHistoryFilterFragment.UpdateFilteringOptions {
    private String fromDate, toDate;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());

    public static final String ANALYTIC_SCREEN_NAME = "Account Summary";
    public static final String ACCOUNT_CLOSED_STATUS = "085";
    public static final int ANIMATION_DURATION_MILLIS = 500;

    private AccountActivityTransactionsFragment accountActivityTransactionsFragment;
    private FilteringOptions filteringOptions;
    private AccountHubViewModel viewModel;
    private AccountDetail accountDetail;
    private OverdraftGadgets overdraftGadgets;
    private AccountActivityBinding binding;

    private TransactionHistoryViewModel transactionHistoryViewModel;
    private MoneyMarketViewModel moneyMarketViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BMBLogger.d("x-x", "AccountActivity.onCreate");
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.account_activity, null, false);
        setContentView(binding.getRoot());

        filteringOptions = new FilteringOptions();
        filteringOptions.setFilterType(CreditCardHubActivity.ALL_TRANSACTIONS);

        final AccountObject accountObject = (AccountObject) getIntent().getSerializableExtra(IntentFactory.ACCOUNT_OBJECT);
        final AccountDetail accountDetail = (AccountDetail) getIntent().getSerializableExtra(RESULT);

        viewModel = new ViewModelProvider(this).get(AccountHubViewModel.class);
        viewModel.init(this);

        transactionHistoryViewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);
        moneyMarketViewModel = new ViewModelProvider(this).get(MoneyMarketViewModel.class);

        viewModel.getAccountDetailLiveData().observe(this, this::setUpOverdraft);

        viewModel.getOverdraftGadgetsObjectLiveData().observe(this, gadgets -> {
            overdraftGadgets = gadgets;
            int zeroOverdraftAmount = 0;
            FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
            if (featureSwitchingToggles.getOverdraftVCL() == FeatureSwitchingStates.ACTIVE.getKey() && isChequeOrCurrentAccount() && overdraftGadgets != null && overdraftGadgets.getOverdraftAmount() > zeroOverdraftAmount && NewExploreHubFragment.NEW_OVERDRAFT_LIMIT.equalsIgnoreCase(overdraftGadgets.getOffersOverdraftEnum()) && accountDetail != null && accountDetail.getAccountObject() != null && accountDetail.getAccountObject().getAccountNumber() != null && !SharedPreferenceService.INSTANCE.isOfferHidden(accountDetail.getAccountObject().getAccountNumber())) {
                showOffersBanner();
            }
        });

        if (accountObject != null) {
            viewModel.getAccountObjectLiveData().setValue(accountObject);
        } else if (accountDetail != null) {
            final AccountObject detailAccountObject = accountDetail.getAccountObject();
            viewModel.getAccountObjectLiveData().setValue(detailAccountObject);
            viewModel.getAccountDetailLiveData().setValue(accountDetail);
        }
        startFragment(OverdraftOffersFragment.Companion.newInstance(), za.co.absa.presentation.uilib.R.id.offerContentFrameLayout, true, AnimationType.NONE, false, "");
        binding.OfferBannerView.getCloseFullOfferImageView().setOnClickListener(view -> onBackPressed());
        setCollapseHeader();

        binding.moneyMarketOfferBannerView.getHideButton().setOnClickListener(view -> {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, binding.moneyMarketOfferBannerView.getHeight());
            animation.setDuration(ANIMATION_DURATION_MILLIS / 2);
            binding.moneyMarketOfferBannerView.startAnimation(animation);
            binding.moneyMarketOfferBannerView.setVisibility(View.GONE);

            if (accountObject != null && accountObject.getAccountNumber() != null) {
                moneyMarketViewModel.hideMoneyMarketOfferBanner(accountObject.getAccountNumber());
                moneyMarketViewModel.snoozeMoneyMarketNotice(accountObject.getAccountNumber());
            }
        });
        binding.moneyMarketOfferBannerView.getFindOutMoreButton().setOnClickListener(view -> {
            Intent moneyMarketIntent = new Intent(this, MoneyMarketActivity.class);
            moneyMarketIntent.putExtra(MONEY_MARKET_ACCOUNT, accountObject);
            startActivity(moneyMarketIntent);
        });
    }

    private void setUpOverdraft(AccountDetail accountDetail) {
        AccountActivity.this.accountDetail = accountDetail;
        if (accountDetail != null) {
            getAppCacheService().setTransactions(accountDetail);
        }
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        if (featureSwitchingToggles.getProjectOrbit() == FeatureSwitchingStates.ACTIVE.getKey() && accountDetail != null && accountDetail.getAccountObject() != null && moneyMarketViewModel.hasActiveMoneyMarketFund(accountDetail.getAccountObject().getAccountNumber())) {
            showMoneyMarketBanner();
            dismissProgressDialog();
            return;
        }

        if (featureSwitchingToggles.getOverdraftVCL() == FeatureSwitchingStates.ACTIVE.getKey()) {
            viewModel.fetchOverdraftStatus();
        }
    }

    private void showOffersBanner() {
        int animationDurationMilliseconds = 1500;
        binding.OfferBannerView.setVisibility(View.VISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, binding.OfferBannerView.getHeight(), 0);
        translateAnimation.setDuration(animationDurationMilliseconds);
        binding.OfferBannerView.startAnimation(translateAnimation);
        binding.OfferBannerView.getContentTextView().setText(getString(R.string.overdraft_intro_qualify_title, TextFormatUtils.formatBasicAmount(this.overdraftGadgets.getOverdraftAmount())));
        binding.OfferBannerView.getApplyButton().setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Overdraft_NeedEmergancyFundsScreen_ApplyNowButtonClicked");
            FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
            if (featureSwitchingToggles.getOverdraftVCL() == FeatureSwitchingStates.DISABLED.getKey()) {
                startActivity(IntentFactory.capabilityUnavailable(AccountActivity.this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching__apply_for_overdraft))));
            } else {
                startActivity(IntentFactoryOverdraft.getOverdraftSteps(AccountActivity.this, this.overdraftGadgets.getNewOverdraftLimitValue()));
            }
        });
        binding.OfferBannerView.getHideButton().setOnClickListener(v -> {
            if (accountDetail != null && accountDetail.getAccountObject() != null && accountDetail.getAccountObject().getAccountNumber() != null) {
                hideOfferBanner(accountDetail.getAccountObject().getAccountNumber());
            }
        });
    }

    public void showBottomBarMenu() {
        TransactionHistoryFilterFragment filterBottomDialogFragment = TransactionHistoryFilterFragment.newInstance(this, filteringOptions, "Cheque card hub");
        filterBottomDialogFragment.show(getSupportFragmentManager(), "filter_dialog_fragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.acccount_hub_menu, menu);
        setShareAction(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_share) {
            shareAccountDetails();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCollapseHeader() {
        CollapsingAppBarView collapsingAppBarView = findViewById(R.id.collapsing_appbar_view);
        collapsingAppBarView.addHeaderView(AccountActivityHeaderFragment.newInstance());
        collapsingAppBarView.setBackground(R.drawable.gradient_red_orange);
        accountActivityTransactionsFragment = AccountActivityTransactionsFragment.newInstance(getString(R.string.credit_card_hub_transactions_tab));

        collapsingAppBarView.setUpTabs(this, accountActivityTransactionsFragment, AccountActivityStatementsFragment.newInstance(getString(R.string.account_hub_statements)));

        if (getAccountObject() != null && getAccountObject().getDescription() != null) {
            ToolBarUtils.setToolBarBack(this, getAccountObject().getDescription());
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.OfferBannerView.isBannerExpanded()) {
            binding.OfferBannerView.backNavigation();
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_NeedEmergancyFundsScreen_BackButtonClicked");
        } else {
            finish();
        }
    }

    private void requestTransactionHistory() {
        BMBLogger.d("x-x", "AccountActivity.requestTransactionHistory()");

        Calendar calendar = Calendar.getInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));

        toDate = formatter.format(calendar.getTime());
        filteringOptions.setToDate(toDate);

        calendar.add(Calendar.DAY_OF_MONTH, -7);

        fromDate = formatter.format(calendar.getTime());
        filteringOptions.setFromDate(fromDate);

        viewModel.setAccountObject(getAccountObject(), fromDate, toDate);

        requestHistory();
    }

    private void requestHistory() {
        HistoryRequest historyRequest = new HistoryRequest();
        historyRequest.setFromAccountNumber(getAccountObject().getAccountNumber());
        historyRequest.setFromDate(fromDate);
        historyRequest.setToDate(toDate);
        historyRequest.setIncludeAllTransactions(true);
        historyRequest.setAccountType(getAccountObject().getAccountType());
        transactionHistoryViewModel.getFailureLiveData().observe(this, failureResponse -> {

            if (!failureResponse.getResultMessages().isEmpty()) {
                if (failureResponse.getResultMessages().get(0).getResponseMessage().contains(ACCOUNT_CLOSED_STATUS)) {
                    navigateToAccountClosedScreen();
                } else {
                    showMessageError(failureResponse.getResultMessages().get(0).getResponseMessage());
                }
            } else {
                showGenericErrorMessage();
            }
            dismissProgressDialog();
        });
        transactionHistoryViewModel.fetchTransactionHistory(historyRequest);
        setUpObservers();
    }

    private void navigateToAccountClosedScreen() {
        Intent errorIntent = IntentFactory.getGenericResultFailureBuilder(this)
                .setGenericResultHeaderMessage(R.string.account_status_closed_title)
                .setGenericResultSubMessage(R.string.account_status_closed_description)
                .setGenericResultBottomButton(R.string.ok, view -> navigateToHomeScreenAndShowAccountsList())
                .build();
        startActivity(errorIntent);
    }

    private void setUpObservers() {
        transactionHistoryViewModel.transactionHistoryLiveData.observe(this, transactionHistoryResponse -> {
            AccountDetail account = new AccountDetail();

            List<Transaction> transactions = new ArrayList<>();

            for (AccountHistoryLines accountHistoryLine : transactionHistoryResponse.getTransactionHistory().getAccountHistoryLines()) {
                Transaction transaction = new Transaction();
                transaction.setBalance(new Amount(accountHistoryLine.getBalanceAmount()));
                transaction.setDescription(accountHistoryLine.getTransactionDescription());
                transaction.setReferenceNumber(accountHistoryLine.getTransactionDescription());
                transaction.setTransactionCategory(String.valueOf(accountHistoryLine.getTransactionCategory()));
                transaction.setTransactionDate(accountHistoryLine.getTransactionDate());
                transaction.setUnclearedTransaction(accountHistoryLine.getTransactionCategory() == 1);

                Amount amount = new Amount(accountHistoryLine.getTransactionAmount());

                if (amount.getAmountDouble() > 0) {
                    transaction.setCreditAmount(amount);
                } else {
                    transaction.setCreditAmount(new Amount("0.00"));
                    transaction.setDebitAmount(amount);
                }

                transactions.add(transaction);
            }

            account.setFromDate(fromDate);
            account.setToDate(toDate);

            account.setAccountObject(getAccountObject());
            account.setTransactions(transactions);

            getAppCacheService().setFilteredCreditCardTransactions(account);
            viewModel.getAccountDetailLiveData().setValue(account);
            applyFilter();
        });

        moneyMarketViewModel.getMoneyMarketSnoozeLiveData().observe(this, responseObject -> {
            dismissProgressDialog();
        });
    }

    private void showMoneyMarketBanner() {
        binding.moneyMarketOfferBannerView.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(0, 0, binding.moneyMarketOfferBannerView.getHeight(), 0);
        animation.setDuration(ANIMATION_DURATION_MILLIS);
        binding.moneyMarketOfferBannerView.startAnimation(animation);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        BMBLogger.d("x-x", "AccountActivity.onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BMBLogger.d("x-x", "AccountActivity.onRestoreInstanceState");
    }

    private void requestTransactionHistory(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        viewModel.setAccountObject(getAccountObject(), fromDate, toDate);
        requestHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountDetail == null) {
            if (getAccountObject() != null) {
                requestTransactionHistory();
            } else {
                showGenericErrorMessageThenFinish();
            }
        }
    }

    private void shareAccountDetails() {
        startActivityIfAvailable(Intent.createChooser(createShareIntent(), getString(R.string.share_account_details)));
    }

    private Intent createShareIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_account_details));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getAccountDetailsToShare());
        return shareIntent;
    }

    @NonNull
    private String getAccountDetailsToShare() {
        return this.getAccountObject() == null ? "" : getString(R.string.share_account_details_content,
                CustomerProfileObject.getInstance().getCustomerName(),
                this.getAccountObject().getAccountNumber(),
                this.getAccountObject().getDescription());
    }

    private void setShareAction(Menu menu) {
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (shareActionProvider != null)
            shareActionProvider.setShareIntent(createShareIntent());
    }

    @Nullable
    public AccountObject getAccountObject() {
        return viewModel.getAccountObjectLiveData().getValue();
    }

    public String getAccountNumber() {
        return viewModel.getAccountNumber();
    }

    public AccountDetail getAccountDetail() {
        return viewModel.getAccountDetail();
    }

    void navigateToTransfer() {
        if (!AccessPrivileges.getInstance().getInterAccountTransferAllowed()) {
            BaseAlertDialog.INSTANCE.showRequestAccessAlertDialog(getString(R.string.transfer));
        } else {
            final Intent transferIntent = buildIntent(TransferFundsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("fromAccount", getAccountObject());
            transferIntent.putExtras(bundle);
            startActivity(transferIntent);
        }
    }

    void navigateToPay() {
        if (!AccessPrivileges.getInstance().getBeneficiaryPaymentAllowed()) {
            BaseAlertDialog.INSTANCE.showRequestAccessAlertDialog(getString(R.string.payment));
        } else {
            final Intent paymentIntent = buildIntent(PaymentsActivity.class);
            paymentIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            paymentIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT);
            Bundle bundle = new Bundle();
            bundle.putSerializable("fromAccount", getAccountObject());
            paymentIntent.putExtras(bundle);
            startActivity(paymentIntent);
        }
    }

    void navigateToCashSend() {
        if (!AccessPrivileges.getInstance().getCashSendAllowed()) {
            BaseAlertDialog.INSTANCE.showRequestAccessAlertDialog(getString(R.string.cashsend));
        } else {
            final Intent cashSendIntent = buildIntent(CashSendActivity.class);
            cashSendIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            cashSendIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);
            startActivity(cashSendIntent);
        }
    }

    void navigateToCsvStatement() {
        if (getAccountDetail() != null)
            startActivity(IntentFactory.getCsvStatement(AccountActivity.this, getAccountDetail().getAccountObject()));
    }

    void navigateToArchivedStatement() {
        if (getAccountObject() != null) {
            startActivity(IntentFactory.getArchivedStatement(this, getAccountObject()));
        }
    }

    void navigateToStampedStatement() {
        if (getAccountNumber() != null) {
            startActivity(IntentFactory.getStampedStatement(this, getAccountNumber(), accountDetail.getAccountObject() != null ? accountDetail.getAccountObject().getAccountType() : ""));
        }
    }

    private Intent buildIntent(Class<?> targetClass) {
        final Intent intent = new Intent();
        intent.setClass(this, targetClass);
        intent.putExtra(IntentFactory.ONBACKPRESSED_FINISH, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    boolean canViewBalances() {
        return OperatorPermissionUtils.canViewBalances(getAccountObject());
    }

    @Override
    public void updateFilteringOptions(FilteringOptions filteringOptions) {
        if (filteringOptions.getFromDate().equals(filteringOptions.getFromDate()) || filteringOptions.getToDate().equals(filteringOptions.getToDate())) {
            requestTransactionHistory(filteringOptions.getFromDate(), filteringOptions.getToDate());
        } else {
            accountActivityTransactionsFragment.updateFilter(filteringOptions);
        }

        this.filteringOptions = filteringOptions;
    }

    private void hideOfferBanner(String accountNumber) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, binding.OfferBannerView.getHeight());
        animation.setDuration(ANIMATION_DURATION_MILLIS);
        binding.OfferBannerView.startAnimation(animation);
        if (accountNumber != null) {
            SharedPreferenceService.INSTANCE.setOfferHidden(accountNumber);
        }
        binding.OfferBannerView.setVisibility(View.GONE);
    }

    @Override
    public void applyFilter() {
        accountActivityTransactionsFragment.updateFilter(filteringOptions);
    }

    private boolean isChequeOrCurrentAccount() {
        return viewModel.getAccountDetailLiveData() != null && (viewModel.getAccountObject().isCurrentAccount() || viewModel.getAccountObject().isChequeAccount());
    }

    @Override
    public void navigateToAccountClosedErrorScreen() {
        Intent errorIntent = IntentFactory.getGenericResultFailureBuilder(this)
                .setGenericResultHeaderMessage(R.string.fixed_deposit_account_closed)
                .setGenericResultSubMessage(R.string.fixed_deposit_account_closed_sub_message)
                .setGenericResultBottomButton(R.string.ok, view -> loadAccountsAndGoHome())
                .build();
        startActivity(errorIntent);
    }
}