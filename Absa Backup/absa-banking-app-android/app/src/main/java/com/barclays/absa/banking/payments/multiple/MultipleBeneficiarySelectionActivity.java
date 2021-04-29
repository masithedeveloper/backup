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
package com.barclays.absa.banking.payments.multiple;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Filter;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.databinding.ActivityMultipleBeneficiarySelectionBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.FilterAccountList;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MultipleBeneficiarySelectionActivity extends BaseActivity implements View.OnClickListener, SearchView.OnQueryTextListener, MultipleBeneficiarySelectionView {
    public static final String FROM_ACCOUNTS = "FROM_ACCOUNTS";
    public static final String SELECTED_BENEFICIARY_LIST = "SELECTED_BENEFICIARY_LIST";
    private ActivityMultipleBeneficiarySelectionBinding binding;
    private List<BeneficiaryObject> selectedBeneficiaryList = new ArrayList<>();
    private MultipleBeneficiarySectionAdapter multipleBeneficiarySectionAdapter;
    private SelectedBeneficiaryAdapter selectedBeneficiaryAdapter;
    private List<BeneficiaryObject> beneficiaryObjectList;
    private Filter filter;
    private SelectBeneficiaryPresenterInterface selectBeneficiaryPresenter;
    private MenuItem searchItem;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance().trackCustomScreenView("Select Beneficiaries inactive state", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_multiple_beneficiary_selection, null, false);
        setContentView(binding.getRoot());

        selectBeneficiaryPresenter = new SelectBeneficiaryPresenter(this);
        setToolBarBack(R.string.select_beneficiaries);
        setUpViews();
        setupTalkBack();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getDeviceProfilingInteractor().notifyTransaction();
    }

    private void setupTalkBack() {
        binding.beneficiaryNotFoundTextView.setContentDescription(getString(R.string.talkback_multipay_beneficiary_not_found));
        binding.toolbar.toolbar.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        binding.toolbar.toolbar.requestFocus();
    }

    private void setUpViews() {
        beneficiaryObjectList = beneficiaryCacheService.getPaymentBeneficiaries();
        populateSavedBeneficiariesList();
        selectedBeneficiaryAdapter = new SelectedBeneficiaryAdapter(selectedBeneficiaryList, selectBeneficiaryPresenter);
        LinearLayoutManager selectedBeneficiaryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.selectedBeneficiaryRecyclerView.setHasFixedSize(true);
        binding.selectedBeneficiaryRecyclerView.setLayoutManager(selectedBeneficiaryLayoutManager);
        binding.selectedBeneficiaryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.selectedBeneficiaryRecyclerView.setAdapter(selectedBeneficiaryAdapter);
        binding.continueButton.setOnClickListener(this);
    }

    private void populateSavedBeneficiariesList() {
        //int maximumAllowedBeneficiaries = isBusinessAccount() ? MAX_ALLOWED_BUSINESS_BENEFICIARIES : MAX_ALLOWED_RETAIL_BENEFICIARIES;
        //binding.selectSavedBeneficiaryTextView.setText(getString(R.string.select_saved_beneficiaries, maximumAllowedBeneficiaries));
        if (beneficiaryObjectList != null) {
            CommonUtils.sortBeneficiaryData(beneficiaryObjectList);
            multipleBeneficiarySectionAdapter = new MultipleBeneficiarySectionAdapter(this, this, beneficiaryObjectList);
            LinearLayoutManager multipleBeneficiaryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.availableBeneficiaryRecyclerView.setHasFixedSize(true);
            filter = multipleBeneficiarySectionAdapter.getFilter();
            invalidateOptionsMenu();
            binding.availableBeneficiaryRecyclerView.setLayoutManager(multipleBeneficiaryLayoutManager);
            binding.availableBeneficiaryRecyclerView.setAdapter(multipleBeneficiarySectionAdapter);
        }
    }

    private void toggleSelectedBeneficiariesVisibility() {
        if (selectedBeneficiaryList.isEmpty()) {
            binding.selectedBeneficiaryRecyclerView.setVisibility(View.GONE);
            binding.selectSavedBeneficiaryTextView.setVisibility(View.GONE);
            binding.selectedBeneficiaryAvailableBeneficiaryRecyclerViewDevider.setVisibility(View.GONE);
        } else {
            binding.selectedBeneficiaryRecyclerView.setVisibility(View.VISIBLE);
            binding.selectSavedBeneficiaryTextView.setVisibility(View.VISIBLE);
            binding.selectedBeneficiaryAvailableBeneficiaryRecyclerViewDevider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (!selectedBeneficiaryList.isEmpty()) {
            getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments(() -> {
                dismissProgressDialog();
                navigateToChooseAccountScreen();
            });
        }
    }

    @Override
    public void navigateToChooseAccountScreen() {
        selectBeneficiaryPresenter.getAccountList();
    }

    public void showNoBeneficiaryContainer() {
        if (binding.beneficiaryNotFoundTextView.getVisibility() == View.GONE) {
            AnalyticsUtils.getInstance().trackCustomScreenView("No search result", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST);
            binding.beneficiaryNotFoundTextView.setVisibility(View.VISIBLE);
        }
        if (binding.selectSavedBeneficiaryTextView.getVisibility() == View.VISIBLE) {
            binding.selectSavedBeneficiaryTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showBeneficiaryContainer() {
        if (binding.beneficiaryNotFoundTextView.getVisibility() == View.VISIBLE) {
            binding.beneficiaryNotFoundTextView.setVisibility(View.GONE);
        }
        if (binding.beneficiaryNotFoundTextView.getVisibility() == View.GONE) {
            binding.selectSavedBeneficiaryTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void autoPopulateSingleAccount(ClientAgreementDetails successResponse, AccountObject accountObject) {
      /*  Intent intent = new Intent(this, MultiplePaymentsSelectedBeneficiariesActivity.class);
        intent.putExtra(SELECTED_ACCOUNT, accountObject);
        intent.putExtra(MultiplePaymentsAccountSelectorActivity.CLIENT_AGREEMENT_DETAILS, successResponse);
        intent.putExtra(SELECTED_BENEFICIARY_LIST, (Serializable) selectedBeneficiaryList);
        startActivity(intent);*/
    }

    @Override
    public void openFromAccountChooserActivity() {
        ArrayList<AccountObject> fromAccountList = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PAYMENT);
        Intent intent = new Intent(this, MultiplePaymentsAccountSelectorActivity.class);
        intent.putExtra(FROM_ACCOUNTS, fromAccountList);
        intent.putExtra(SELECTED_BENEFICIARY_LIST, (Serializable) selectedBeneficiaryList);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (beneficiaryObjectList != null && !beneficiaryObjectList.isEmpty()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.search_menu, menu);
            searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView;
            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
                final SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
                if (searchView != null) {
                    searchView.setOnQueryTextListener(this);
                    searchView.setSubmitButtonEnabled(false);
                    searchView.setQueryRefinementEnabled(false);
                    searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    if (searchManager != null) {
                        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
                    }
                    EditText searchPlate = searchView.findViewById(R.id.search_src_text);
                    if (searchPlate != null) {
                        searchPlate.setInputType(EditorInfo.TYPE_TEXT_VARIATION_FILTER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        searchPlate.setPrivateImeOptions("nm");
                        searchPlate.setOnEditorActionListener((v, actionId, event) -> {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                searchItem.collapseActionView();
                            }
                            return false;
                        });
                    }
                }
                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        if (!selectedBeneficiaryList.isEmpty()) {
                            binding.selectedBeneficiaryRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            binding.selectSavedBeneficiaryTextView.setVisibility(View.GONE);
                            binding.selectedBeneficiaryAvailableBeneficiaryRecyclerViewDevider.setVisibility(View.GONE);
                        }
                        return true;
                    }
                });
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedBeneficiaryList = getAppCacheService().getMultiplePaymentsSelectedBeneficiaryList();
        setUpViews();
    }

    public void stopSearch() {
        if (searchItem != null)
            searchItem.collapseActionView();
    }

    private void startSearch(String query) {
        if (TextUtils.isEmpty(query.trim())) {
            filter.filter(query.trim());
        } else {
            filter.filter(query.trim());
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (beneficiaryObjectList != null && !beneficiaryObjectList.isEmpty()) {
            binding.selectedBeneficiaryRecyclerView.setVisibility(TextUtils.isEmpty(
                    query) && !selectedBeneficiaryList.isEmpty() ? View.VISIBLE : View.GONE);
            startSearch(query);
        }
        return false;
    }

    @Override
    public BeneficiaryObject getSectionListBeneficiary(int selectedPosition) {
        return multipleBeneficiarySectionAdapter.getSectionListBeneficiary(selectedPosition);
    }

    @Override
    public List<BeneficiaryObject> getSelectedBeneficiaries() {
        return selectedBeneficiaryList;
    }

    @Override
    public void onBeneficiaryClicked(int adapterPosition) {
        selectBeneficiaryPresenter.onBeneficiaryClicked(adapterPosition, selectedBeneficiaryList);
    }

    @Override
    public void toggleContinueButton(boolean enable) {
        binding.continueButton.setEnabled(enable);
    }

    @Override
    public void notifyOnItemSelection() {
        AnalyticsUtils.getInstance().trackCustomScreenView("Select beneficiaries active state", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST);
        selectedBeneficiaryAdapter.notifyItemInserted(selectedBeneficiaryList.size());
        multipleBeneficiarySectionAdapter.notifyDataSetChanged();
        toggleSelectedBeneficiariesVisibility();
    }

    @Override
    public void notifyOnItemDeselection(int position) {
        AnalyticsUtils.getInstance().trackCustomScreenView("Select Beneficiaries inactive state", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST);
        selectedBeneficiaryAdapter.notifyItemRemoved(position);
        multipleBeneficiarySectionAdapter.notifyDataSetChanged();
        toggleSelectedBeneficiariesVisibility();
    }

    public void onBeneficiaryListFiltered(List<BeneficiaryObject> paymentBeneficiaryList) {
        if (selectBeneficiaryPresenter != null) {
            selectBeneficiaryPresenter.onBeneficiaryListFiltered(paymentBeneficiaryList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        AnalyticsUtils.getInstance().trackSearchEvent("Search");
        return super.onOptionsItemSelected(item);
    }
}