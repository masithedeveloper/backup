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
package com.barclays.absa.banking.cashSend.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.TransactionUnredeem;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendUnredeemedAccounts;
import com.barclays.absa.banking.boundary.model.cashSend.CashsendUnredeemTransactions;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.cashSend.services.CashSendService;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import styleguide.forms.RoundedSelectorView;
import styleguide.forms.SelectorList;

import static com.barclays.absa.banking.cashSend.ui.CashSendActivity.IS_CASH_SEND_PLUS;

public class UnredeemedTransactionActivity extends BaseActivity implements SearchView.OnQueryTextListener, UnredeemedItemClickListener {

    private CashSendUnredeemedAccounts cashSendUnredeemedAccounts;
    private UnredeemedRecyclerViewAdapter unredeemedRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView noResults;
    private AccountObject selectAccountObject = null;
    private ResponseObject responseObject = null;
    private RoundedSelectorView<AccountObjectWrapper> accountSelectorView;
    private CashSendService cashSendService;
    private boolean isCashSendPlus = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_send_unredeemed_transactions_fragment);
        cashSendService = new CashSendInteractor();
        setToolBarBack(R.string.title_unredeemed_cash_send, v -> {
            Intent cashSendIntent = new Intent(UnredeemedTransactionActivity.this, CashSendActivity.class);
            cashSendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            cashSendIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            cashSendIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);
            startActivity(cashSendIntent);
        });
        accountDetailResponseListener.setView(this);
        accountSelectorView = findViewById(R.id.unredeemedAccountSelectionView);
        mScreenName = BMBConstants.UNREDEEMED_CASHSEND_TRANSACTIONS_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        isCashSendPlus = getIntent().getBooleanExtra(IS_CASH_SEND_PLUS, false);
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.UNREDEEMED_CASHSEND_TRANSACTIONS_CONST,
                BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST);
        cashSendUnredeemedAccounts = (CashSendUnredeemedAccounts) this.getIntent().getSerializableExtra(BMBConstants.RESULT);
        initViews();
        invalidateOptionsMenu();
        setupTalkBack();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            accountSelectorView.setContentDescription(getString(R.string.talkback_unredeemed_cashsends_account_info, accountSelectorView.getEditText().getText().toString()));
        }
    }

    @Override
    public void onBackPressed() {
        Intent cashSendIntent = new Intent(UnredeemedTransactionActivity.this, CashSendActivity.class);
        cashSendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        cashSendIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        cashSendIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);
        startActivity(cashSendIntent);
    }

    private void initViews() {
        noResults = findViewById(R.id.unredeemedNoResultsTextView);
        recyclerView = findViewById(R.id.unredeemedCashSendsRecyclerView);
        unredeemedRecyclerViewAdapter = new UnredeemedRecyclerViewAdapter(UnredeemedTransactionActivity.this, cashSendUnredeemedAccounts.getUnredeemTransactionList() != null ? cashSendUnredeemedAccounts.getUnredeemTransactionList() : new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(unredeemedRecyclerViewAdapter);

        if (cashSendUnredeemedAccounts.getFromAccountList() != null && cashSendUnredeemedAccounts.getFromAccountList().size() > 0) {
            selectAccountObject = cashSendUnredeemedAccounts.getFromAccountList().get(0);
            populateHeaderData(selectAccountObject);
        }
        if (null != cashSendUnredeemedAccounts.getUnredeemTransactionList() && cashSendUnredeemedAccounts.getUnredeemTransactionList().size() > 0) {
            noResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }

        getAccounts();
    }

    private void populateHeaderData(AccountObject accountObject) {
        if (accountObject != null) {
            accountSelectorView.setSelectedValue(accountObject.getAccountInformation());
        } else {
            accountSelectorView.setSelectedValue(getResources().getString(R.string.account_selection_error));
        }
    }

    private void populateRow(ArrayList<TransactionUnredeem> mList) {
        if (unredeemedRecyclerViewAdapter != null) {
            unredeemedRecyclerViewAdapter.setTransactionsList(mList);
            unredeemedRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private ExtendedResponseListener<CashsendUnredeemTransactions> accountDetailResponseListener = new ExtendedResponseListener<CashsendUnredeemTransactions>(this) {

        @Override
        public void onSuccess(final CashsendUnredeemTransactions successResponse) {
            dismissProgressDialog();
            responseObject = successResponse;
            if (responseObject != null) {
                if (successResponse.getTransactionUnredeem() != null && successResponse.getTransactionUnredeem().size() > 0) {
                    cashSendUnredeemedAccounts.setUnredeemTransactionList(successResponse.getTransactionUnredeem());
                    populateRow(successResponse.getTransactionUnredeem());
                    recyclerView.setVisibility(View.VISIBLE);
                    noResults.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    cashSendUnredeemedAccounts.setUnredeemTransactionList(new ArrayList<>());
                    noResults.setVisibility(View.VISIBLE);
                    animate(noResults, R.anim.bounce_long);
                }
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            responseObject = failureResponse;
            if (TextUtils.isEmpty(responseObject.getResponseMessage())) {
                recyclerView.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .title(getString(R.string.alert))
                        .message(responseObject.getResponseMessage())
                        .build());
            }
        }
    };

    private void sendTransactionRequest(String accountNumber) {
        cashSendService.requestUnredeemedTransactions(accountDetailResponseListener, accountNumber, isCashSendPlus);
    }

    private void getAccounts() {
        if (cashSendUnredeemedAccounts.getFromAccountList() != null && cashSendUnredeemedAccounts.getFromAccountList().size() > 0) {
            SelectorList<AccountObjectWrapper> accounts = new SelectorList<>();
            for (AccountObject accountObject : cashSendUnredeemedAccounts.getFromAccountList()) {
                accounts.add(new AccountObjectWrapper(accountObject));
            }
            accountSelectorView.setList(accounts, getString(R.string.select_account_toolbar_title));
            accountSelectorView.setItemSelectionInterface(index -> {
                final AccountObjectWrapper accountObject = accounts.get(index);
                if (accountObject != null) {
                    recyclerView.postDelayed(() -> {
                        selectAccountObject = accountObject.getAccountObject();
                        populateHeaderData(selectAccountObject);
                        sendTransactionRequest(selectAccountObject.getAccountNumber());
                    }, 100);
                }
            });
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(@NotNull Menu menu) {
        if (cashSendUnredeemedAccounts.getUnredeemTransactionList() != null && cashSendUnredeemedAccounts.getUnredeemTransactionList().size() <= 0) {
            return super.onPrepareOptionsMenu(menu);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) UnredeemedTransactionActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setSubmitButtonEnabled(true);
            setupSearchView(searchView);
        }
        if (searchView != null && searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(UnredeemedTransactionActivity.this.getComponentName()));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setupSearchView(SearchView mSearchView) {
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryRefinementEnabled(false);
    }

    private void startSearch(String query) {
        if (TextUtils.isEmpty(query.trim())) {
            unredeemedRecyclerViewAdapter.filterText("");
        } else {
            unredeemedRecyclerViewAdapter.filterText(query.trim());
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        startSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        startSearch(query);
        return false;
    }

    @Override
    public void onItemClick(TransactionUnredeem transaction) {
        Intent transactionIntent = new Intent(UnredeemedTransactionActivity.this, UnredeemedDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("UnredeemTransaction", transaction);
        bundle.putString("actNumToDisplay", selectAccountObject.getDescription());
        bundle.putBoolean(IS_CASH_SEND_PLUS, isCashSendPlus);
        final ArrayList<TransactionUnredeem> unredeemTransactionList = cashSendUnredeemedAccounts.getUnredeemTransactionList();
        bundle.putString("actNum", unredeemTransactionList != null ? unredeemTransactionList.get(0).getBeneficiaryAccountNumber() : null);
        bundle.putString("myRef", "");
        transactionIntent.putExtras(bundle);
        startActivity(transactionIntent);
    }

    public void showNoResultsView() {
        noResults.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void hideNoResultsView() {
        noResults.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
