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
package com.barclays.absa.banking.dualAuthorisations.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransaction;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionList;
import com.barclays.absa.banking.databinding.AuthorisationHubActivityBinding;
import com.barclays.absa.banking.dualAuthorisations.services.AuthorisationTransactionListRequest;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.data.ResponseObject;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.ItemSelectionInterface;
import styleguide.forms.SelectorList;

public class AuthorisationHubActivity extends BaseActivity implements ItemSelectionInterface, SearchView.OnQueryTextListener {
    private AuthorisationHubActivityBinding binding;
    private SelectorList<AuthorisationContainer> originalTransactionCategories = new SelectorList<>();
    private AuthorisationsListAdapter adapter;
    private AuthorisationTransactionList originalAuthorisationTransactionList;
    private Filter filter;

    private final ExtendedResponseListener<ResponseObject> getAuthorisationTransactionListResponseListener = new ExtendedResponseListener<ResponseObject>() {

        @Override
        public void onSuccess(final ResponseObject responseObject) {
            dismissProgressDialog();
            if (responseObject instanceof AuthorisationTransactionList) {
                setUpLayout((AuthorisationTransactionList) responseObject);
            } else {
                noAuthorisation();
            }
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.authorisation_hub_activity, null, false);
        setContentView(binding.getRoot());

        getAuthorisationTransactionListResponseListener.setView(this);
        binding.authTypeSelectorView.setItemSelectionInterface(this);

        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.pause_card_all_transactions), AuthorisationTransaction.TransactionTypeCategory.ALL_TRANSACTIONS));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.pay_beneficiary), AuthorisationTransaction.TransactionTypeCategory.PAY_BENEFICIARY));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.inter_account_transfer), AuthorisationTransaction.TransactionTypeCategory.INTER_ACCOUNT_TRANSFER));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.prepaid), AuthorisationTransaction.TransactionTypeCategory.PREPAID));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.iip), AuthorisationTransaction.TransactionTypeCategory.IMMEDIATE_INTERBANK_PAYMENT));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.cashsend), AuthorisationTransaction.TransactionTypeCategory.CASH_SEND));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.cash_send_plus_title), AuthorisationTransaction.TransactionTypeCategory.CASH_SEND_PLUS));
        originalTransactionCategories.add(new AuthorisationContainer(getString(R.string.pay_once_off), AuthorisationTransaction.TransactionTypeCategory.PAY_ONCE_OFF));
        binding.authTypeSelectorView.setList(originalTransactionCategories, "");
        binding.authTypeSelectorView.setSelectedIndex(0);

        AuthorisationTransactionListRequest<ResponseObject> getAuthorisationTransactionListRequest = new AuthorisationTransactionListRequest<>(getAuthorisationTransactionListResponseListener);
        ServiceClient serviceClient = new ServiceClient(getAuthorisationTransactionListRequest);
        serviceClient.submitRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            setupSearchView(searchView);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(this.getComponentName()) : null);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryRefinementEnabled(false);
    }

    private List<AuthorisationTransaction> getListForType(AuthorisationTransaction.TransactionTypeCategory transactionTypeCategory, AuthorisationTransactionList authorisationTransactionList) {
        List<AuthorisationTransaction> typeAuthList = new ArrayList<>();
        if (!AuthorisationTransaction.TransactionTypeCategory.ALL_TRANSACTIONS.equals(transactionTypeCategory)) {
            if (authorisationTransactionList != null && authorisationTransactionList.getAuthorisationTransactionList() != null && !authorisationTransactionList.getAuthorisationTransactionList().isEmpty()) {
                for (AuthorisationTransaction authorisationTransaction : authorisationTransactionList.getAuthorisationTransactionList()) {
                    final AuthorisationTransaction.TransactionTypeCategory transactionCategoryType = authorisationTransaction.getTransactionCategoryType();
                    if (transactionCategoryType != null) {
                        if (AuthorisationTransaction.TransactionTypeCategory.CASH_SEND_PLUS.equals(transactionTypeCategory) && transactionCategoryType.name().contains(transactionTypeCategory.name())) {
                            typeAuthList.add(authorisationTransaction);
                        } else if (transactionCategoryType.name().equals(transactionTypeCategory.name())) {
                            typeAuthList.add(authorisationTransaction);
                        }
                    }
                }
            }
        } else if (authorisationTransactionList != null) {
            typeAuthList = authorisationTransactionList.getAuthorisationTransactionList();
        }
        return typeAuthList;
    }

    public void setUpLayout(AuthorisationTransactionList authorisationList) {
        if (authorisationList != null && authorisationList.getAuthorisationTransactionList() != null && !authorisationList.getAuthorisationTransactionList().isEmpty()) {
            originalAuthorisationTransactionList = authorisationList;
            setToolBarBack(getString(R.string.authorisation_count, authorisationList.getAuthorisationTransactionList().size()));
            binding.authorisations.setVisibility(View.VISIBLE);
        } else {
            noAuthorisation();
            return;
        }

        binding.authorisations.setLayoutManager(new LinearLayoutManager(this));
        binding.authorisations.setNestedScrollingEnabled(false);
        binding.authorisations.setFocusable(false);
        adapter = new AuthorisationsListAdapter(this, authorisationList.getAuthorisationTransactionList());
        binding.authorisations.setAdapter(adapter);
        filter = adapter.getFilter();
    }

    private void noAuthorisation() {
        setToolBarBack(getString(R.string.authorisation_count, 0));
        binding.noAuthTextView.setVisibility(View.VISIBLE);
        binding.authorisations.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(int index) {
        final List<AuthorisationTransaction> authorisationTransactions = (index == 0)
                ? getListForType(AuthorisationTransaction.TransactionTypeCategory.ALL_TRANSACTIONS, originalAuthorisationTransactionList)
                : getListForType(originalTransactionCategories.get(index).getTransactionCategoryItem(), originalAuthorisationTransactionList);

        if (authorisationTransactions.isEmpty()) {
            noAuthorisation();
        } else {
            binding.authorisations.setVisibility(View.VISIBLE);
            binding.noAuthTextView.setVisibility(View.GONE);
            delayedRefresh(authorisationTransactions);
            setToolBarBack(getString(R.string.authorisation_count, authorisationTransactions.size()));
        }
    }

    private void delayedRefresh(List<AuthorisationTransaction> authorisationTransactions) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> adapter.refreshItemList(authorisationTransactions), 500);
    }

    private void startSearch(String query) {
        if (filter == null && adapter != null) {
            filter = adapter.getFilter();
        }
        if (filter != null) {
            filter.filter(query.trim());
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        startSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        startSearch(newText);
        return false;
    }
}