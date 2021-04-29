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
package com.barclays.absa.banking.buy.ui.airtime;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeAddBeneficiary;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOff;
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.buy.ui.airtime.buyNew.AddBeneficiaryAirtimeActivity;
import com.barclays.absa.banking.buy.ui.airtime.buyOnceOff.OnceOffAirtimeActivity;
import com.barclays.absa.banking.buy.ui.airtime.existing.PurchaseDetailsActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.payments.services.RewardsRedemptionInteractor;
import com.barclays.absa.banking.presentation.adapters.BeneficiaryCustomSectionListAdapterPrepaid;
import com.barclays.absa.banking.presentation.adapters.SectionListAdapterAirtime;
import com.barclays.absa.banking.presentation.adapters.SectionListItem;
import com.barclays.absa.banking.presentation.adapters.SectionListViewPrepaid;
import com.barclays.absa.banking.presentation.shared.FilterResultInterface;
import com.barclays.absa.banking.presentation.transactions.RecentTransactionAdapterPrepaid;
import com.barclays.absa.banking.rewards.ui.BuyAirtimeWithAbsaRewardsActivity;
import com.barclays.absa.utils.AbsaCacheManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyPrepaidActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnClickListener, FilterResultInterface {

    private static final String[] alphabets = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "#"};
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);
    private final ExtendedResponseListener<RedeemRewards> redeemRewardsResponseListener = new ExtendedResponseListener<RedeemRewards>() {
        @Override
        public void onSuccess(final RedeemRewards redeemRewards) {
            dismissProgressDialog();

            if (rewardsCacheService.getRewardsRedemption() == null) {
                rewardsCacheService.setRewardsRedemption(redeemRewards);
            }
            Intent redeemAirtimeIntent = new Intent(BuyPrepaidActivity.this, BuyAirtimeWithAbsaRewardsActivity.class);
            startActivity(redeemAirtimeIntent);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
        }
    };

    private SectionListViewPrepaid beneficiarySectionListView;
    private List<BeneficiaryObject> beneficiaryDataObjects;
    private List<BeneficiaryObject> lastTransactionDataObject;
    private SectionListAdapterAirtime beneficiarySectionAdapter;
    private BeneficiaryCustomSectionListAdapterPrepaid customSectionListAdapter;
    private Filter filter;
    private View headerView;
    private View selectTransactionHeaderView;
    private String paymentType = "";
    private ConstraintLayout noBenFoundContainer;
    private View buyAirtimeWithAbsaRewards;
    private FrameLayout frameLayoutParent;
    private TextView listEmptyTextView;
    private final PrepaidInteractor interactor = new PrepaidInteractor();
    private final BeneficiariesInteractor beneficiariesInteractor = new BeneficiariesInteractor();
    private final IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private final ExtendedResponseListener<AccountList> accountsExtendedResponseListener = new ExtendedResponseListener<AccountList>() {
        @Override
        public void onSuccess(final AccountList successResponse) {
            dismissProgressDialog();

            showBuyWithAbsaRewardsOptionIfApplicable(successResponse);
        }

    };

    private void showBuyWithAbsaRewardsOptionIfApplicable(AccountList successResponse) {
        if (successResponse != null && successResponse.getAccountsList() != null) {
            for (AccountObject accountObject : successResponse.getAccountsList()) {
                if (AccountTypeEnum.absaReward.toString().equalsIgnoreCase(accountObject.getAccountType()) && rewardsCacheService.getExpressRewardsDetails().getRewardsAccountBalanceSet()) {
                    buyAirtimeWithAbsaRewards.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    private final ExtendedResponseListener<AirtimeAddBeneficiary> requestAirtimeBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AirtimeAddBeneficiary>() {
        @Override
        public void onSuccess(final AirtimeAddBeneficiary airtimeAddBeneficiary) {
            Intent intent = new Intent(BuyPrepaidActivity.this, AddBeneficiaryAirtimeActivity.class);
            intent.putExtra(RESULT, airtimeAddBeneficiary);
            startActivity(intent);
            dismissProgressDialog();
        }
    };
    private final ExtendedResponseListener<AirtimeOnceOff> onceOffAirtimeExtendedResponseListener = new ExtendedResponseListener<AirtimeOnceOff>() {
        @Override
        public void onSuccess(final AirtimeOnceOff successResponse) {
            Intent intent = new Intent(BuyPrepaidActivity.this, OnceOffAirtimeActivity.class);
            intent.putExtra(RESULT, successResponse);
            startActivity(intent);
            dismissProgressDialog();
        }
    };
    private final ExtendedResponseListener<AirtimeBuyBeneficiary> airtimeBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AirtimeBuyBeneficiary>() {
        @Override
        public void onSuccess(final AirtimeBuyBeneficiary successResponse) {
            Intent intent = new Intent(BuyPrepaidActivity.this, PurchaseDetailsActivity.class);
            intent.putExtra(RESULT, successResponse);
            startActivity(intent);
            dismissProgressDialog();
        }
    };

    @Override
    public void filterIsEmpty() {
        frameLayoutParent.setVisibility(View.GONE);
        listEmptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void filterIsNotEmpty() {
        listEmptyTextView.setVisibility(View.GONE);
        frameLayoutParent.setVisibility(View.VISIBLE);
    }

    private class ManageBeneficiariesResponseListener extends ExtendedResponseListener<BeneficiaryListObject> {

        @Override
        public void onSuccess(final BeneficiaryListObject beneficiaryListObject) {
            dismissProgressDialog();
            if (beneficiaryListObject != null && beneficiaryListObject.getAirtimeBeneficiaryList() != null) {
                beneficiaryCacheService.setPrepaidBeneficiaries(beneficiaryListObject.getAirtimeBeneficiaryList());
                beneficiaryCacheService.setPrepaidRecentTransactionList(beneficiaryListObject.getLatestTransactionBeneficiaryList());
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_AIRTIME);
                refreshScreen(beneficiaryListObject.getAirtimeBeneficiaryList(), beneficiaryListObject.getLatestTransactionBeneficiaryList());
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            if (AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equalsIgnoreCase(failureResponse.getResponseCode())) {
                refreshScreen(new ArrayList<>(), new ArrayList<>());
            } else {
                ManageBeneficiariesResponseListener.super.onFailure(failureResponse);
            }
        }
    }

    private final ManageBeneficiariesResponseListener manageBeneficiaryExtendedResponseListener = new ManageBeneficiariesResponseListener();

    /**
     * Create sectioned list
     *
     * @param lstBeneficiaryData : Data to bundle parent with corresponding children
     * @return sectioned list array
     */
    public static SectionListItem[] createSectionedList(List<BeneficiaryObject> lstBeneficiaryData) {
        SectionListItem[] tempArray = new SectionListItem[lstBeneficiaryData.size()];
        for (int i = 0; i < tempArray.length; i++) {
            BeneficiaryObject beneficiaryData = lstBeneficiaryData.get(i);
            String beneficiaryName = beneficiaryData.getBeneficiaryName();
            if (beneficiaryName != null && !beneficiaryName.isEmpty()) {
                if (containsSpecialCharacter(String.valueOf(beneficiaryName.charAt(0)))) {
                    tempArray[i] = new SectionListItem(beneficiaryData, alphabets[alphabets.length - 1]);
                } else {
                    for (String alphabetCharacter : alphabets) {
                        if (alphabetCharacter.equalsIgnoreCase(String.valueOf(beneficiaryName.charAt(0)))) {
                            tempArray[i] = new SectionListItem(beneficiaryData, alphabetCharacter);
                        }
                    }
                }
            }
        }
        return tempArray;
    }

    private static boolean containsSpecialCharacter(String s) {
        return s != null && s.matches("[^A-Za-z0-9 ]");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_view);
        redeemRewardsResponseListener.setView(this);
        accountsExtendedResponseListener.setView(this);
        requestAirtimeBeneficiaryExtendedResponseListener.setView(this);
        onceOffAirtimeExtendedResponseListener.setView(this);
        airtimeBeneficiaryExtendedResponseListener.setView(this);
        manageBeneficiaryExtendedResponseListener.setView(this);

        listEmptyTextView = findViewById(R.id.listEmptyTextView);
        frameLayoutParent = findViewById(R.id.listView);

        setToolBarBack(R.string.add_new_prepaid_result_screen_buy_new);
        mScreenName = BMBConstants.PREPAID_BENEFICIARIES_CONST;
        mSiteSection = BMBConstants.PREPAID_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PREPAID_BENEFICIARIES_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(BMBConstants.PASS_BENEFICAIRY_TYPE)) {
            paymentType = getIntent().getStringExtra(BMBConstants.PASS_BENEFICAIRY_TYPE);
        }

        if (AbsaCacheManager.getInstance().isBeneficiariesCached(PASS_AIRTIME)) {
            refreshScreen(beneficiaryCacheService.getPrepaidBeneficiaries(), beneficiaryCacheService.getPrepaidRecentTransactionList());
        } else {
            beneficiariesInteractor.fetchBeneficiaryList(PASS_PREPAID, manageBeneficiaryExtendedResponseListener);
        }
    }

    private void setupTalkBack() {
        buyAirtimeWithAbsaRewards.setContentDescription(getString(R.string.talkback_buy_prepaid_with_absa_rewards));
    }

    private void populateListings(List<BeneficiaryObject> beneficiaryListObject, List<BeneficiaryObject> recentTransactionList) {

        beneficiarySectionListView = findViewById(R.id.beneficiarySectionListView);
        beneficiarySectionListView.setTextFilterEnabled(true);
        beneficiaryDataObjects = beneficiaryListObject != null ? beneficiaryListObject : new ArrayList<>();
        if (recentTransactionList == null) {
            lastTransactionDataObject = new ArrayList<>();
        } else {
            lastTransactionDataObject = recentTransactionList;
        }

        // Sort currency services alphabetically based on currency name
        Collections.sort(beneficiaryDataObjects, (beneficiaryObject1, beneficiaryObject2) -> beneficiaryObject1.getBeneficiaryName() != null ? beneficiaryObject1.getBeneficiaryName().compareToIgnoreCase(beneficiaryObject2.getBeneficiaryName()) : -1);

        // Adapter to bundle parent with respective children
        customSectionListAdapter = new BeneficiaryCustomSectionListAdapterPrepaid(this, createSectionedList(beneficiaryDataObjects), beneficiaryDataObjects);

        beneficiarySectionAdapter = new SectionListAdapterAirtime(getLayoutInflater(), customSectionListAdapter);
        beneficiarySectionAdapter.setFilterInterface(this);

        headerView = initHeaderListView();
        beneficiarySectionListView.addHeaderView(headerView);
        beneficiarySectionListView.setTextFilterEnabled(false);
        beneficiarySectionListView.setAdapter(beneficiarySectionAdapter);
        filter = beneficiarySectionAdapter.getFilter();

        beneficiarySectionListView.setOnItemClickListener((parent, view, position, id) -> {
                    if (beneficiarySectionListView.getItemAtPosition(position) instanceof SectionListItem) {
                        BeneficiaryObject data = (BeneficiaryObject) ((SectionListItem) beneficiarySectionListView.getItemAtPosition(position)).item;
                        final Bundle bundle = new Bundle();
                        bundle.putString(BEN_ID, data.getBeneficiaryID());
                        interactor.fetchAirtimeBeneficiaryDetails(data.getBeneficiaryID(), airtimeBeneficiaryExtendedResponseListener);
                    }
                }
        );
    }

    /**
     * Init Header view contains Recent Transaction List of beneficiary.
     *
     * @return View
     */
    private View initHeaderListView() {
        selectTransactionHeaderView = getLayoutInflater().inflate(R.layout.activity_beneficiary_select_prepaid, null);
        LinearLayout mRecentTransMainContainer = selectTransactionHeaderView.findViewById(R.id.recent_paid_container_main);
        LinearLayout latestTransactionContainer = selectTransactionHeaderView.findViewById(R.id.recent_tras_container);

        buyAirtimeWithAbsaRewards = selectTransactionHeaderView.findViewById(R.id.buyWithRewardView);
        buyAirtimeWithAbsaRewards.setOnClickListener(this);
        noBenFoundContainer = selectTransactionHeaderView.findViewById(R.id.no_ben_found_container);
        ImageView addNewBeneficiaryImageView = selectTransactionHeaderView.findViewById(R.id.addNewBeneficiaryImageView);

        addNewBeneficiaryImageView.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putString("FROM_ACTIVITY", "PrepaidFragment");
            interactor.prepaidMobileNetworkProviderList(requestAirtimeBeneficiaryExtendedResponseListener);
        });

        selectTransactionHeaderView.findViewById(R.id.payNewBeneficiaryContainer).setOnClickListener(view -> interactor.prepaidMobileNetworkProviderList(requestAirtimeBeneficiaryExtendedResponseListener));
        selectTransactionHeaderView.findViewById(R.id.onceOffView).setOnClickListener(v -> interactor.requestOnceOffAirtimeVoucherList(onceOffAirtimeExtendedResponseListener));

        //check if absa rewards available for the account
        AccountList cachedAccountList = AbsaCacheManager.getInstance().getCachedAccountListObject();
        if (AbsaCacheManager.getInstance().isAccountsCached() && cachedAccountList != null && !cachedAccountList.getAccountsList().isEmpty()) {
            showBuyWithAbsaRewardsOptionIfApplicable(cachedAccountList);
        } else {
            showGenericErrorMessageThenFinish();
        }

        ListView mRecentTransactionListView = new ListView(this);
        mRecentTransactionListView.setDivider(null);
        mRecentTransactionListView.setDividerHeight(0);
        mRecentTransactionListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (lastTransactionDataObject != null && lastTransactionDataObject.size() == 0) {
            mRecentTransMainContainer.setVisibility(View.GONE);
        } else {
            mRecentTransMainContainer.setVisibility(View.VISIBLE);
            RecentTransactionAdapterPrepaid mAdapter = new RecentTransactionAdapterPrepaid(this, lastTransactionDataObject);
            mRecentTransactionListView.setAdapter(mAdapter);
            setListViewHeightBasedOnChildren(mRecentTransactionListView, mAdapter);
            latestTransactionContainer.addView(mRecentTransactionListView);
            mRecentTransactionListView.setOnItemClickListener((parent, view, position, id) -> {
                BeneficiaryObject data = lastTransactionDataObject.get(position);
                AnalyticsUtils.getInstance().trackRecentlyPaidEvent(BMBConstants.PREPAID_BENEFICIARIES_CONST);

                final Bundle bundle = new Bundle();
                bundle.putString(BEN_ID, data.getBeneficiaryID());
                interactor.fetchAirtimeBeneficiaryDetails(data.getBeneficiaryID(), airtimeBeneficiaryExtendedResponseListener);
            });
        }
        setupTalkBack();
        return selectTransactionHeaderView;
    }

    private void setListViewHeightBasedOnChildren(ListView listView, BaseAdapter baseAdapter) {
        if (baseAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < baseAdapter.getCount(); i++) {
                View listItem = baseAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + ((listView.getDividerHeight() * (baseAdapter.getCount() - 1)));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (beneficiaryDataObjects != null && beneficiaryDataObjects.size() <= 0) {
            return super.onCreateOptionsMenu(menu);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu_dark, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) BuyPrepaidActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            setupSearchView(searchView);
        }
        if (searchView != null && searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(BuyPrepaidActivity.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            AnalyticsUtils.getInstance().trackSearchEvent(mScreenName);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView(SearchView mSearchView) {
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryRefinementEnabled(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (beneficiaryDataObjects == null || beneficiaryDataObjects.size() == 0)
            return false;
        startSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (beneficiaryDataObjects == null || beneficiaryDataObjects.size() == 0)
            return false;
        startSearch(query);
        return false;
    }

    private void startSearch(String query) {
        if (TextUtils.isEmpty(query.trim())) {
            if (beneficiarySectionListView.getHeaderViewsCount() == 0) {
                beneficiarySectionListView.addHeaderView(selectTransactionHeaderView);
                beneficiarySectionListView.requestLayout();
                filter.filter(query.trim());
            }
        } else {
            beneficiarySectionListView.removeHeaderView(selectTransactionHeaderView);
            beneficiarySectionListView.requestLayout();
            filter.filter(query.trim());
        }
    }

    private void refreshScreen(List<BeneficiaryObject> beneficiaryListObject, List<BeneficiaryObject> recentTransactionList) {
        populateListings(beneficiaryListObject, recentTransactionList);
        if (beneficiarySectionAdapter != null) {
            beneficiaryDataObjects = beneficiaryListObject;

            Collections.sort(beneficiaryDataObjects, (beneficiaryObject1, beneficiaryObject2) -> beneficiaryObject1.getBeneficiaryName() == null ? -1 : beneficiaryObject1.getBeneficiaryName().compareToIgnoreCase(beneficiaryObject2.getBeneficiaryName()));

            customSectionListAdapter.setBeneficiaryListData(beneficiaryDataObjects);
            customSectionListAdapter.setItems(BuyPrepaidActivity.createSectionedList(customSectionListAdapter.getBeneficiaryListData()));

            beneficiarySectionListView.removeHeaderView(headerView);
            headerView = initHeaderListView();
            if (beneficiaryListObject.size() > 0) {
                noBenFoundContainer.setVisibility(View.GONE);
            } else {
                noBenFoundContainer.setVisibility(View.VISIBLE);
            }

            beneficiarySectionListView.addHeaderView(headerView);
            beneficiarySectionAdapter.updateSessionCache();
            customSectionListAdapter.notifyDataSetChanged();
            beneficiarySectionAdapter.notifyDataSetChanged();

            if (AbsaCacheManager.getInstance().isBeneficiaryCachingAllowed()) {
                AbsaCacheManager.getInstance().updateBeneficiaryList(paymentType, new ArrayList<>(beneficiaryDataObjects));
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buyWithRewardView) {
            new RewardsRedemptionInteractor().pullRewards(redeemRewardsResponseListener);
            mScreenName = BMBConstants.BUY_HUB_CONSTANT;
            mSiteSection = BMBConstants.BUY_PREPAID_REWARDS_CHANNEL_CONSTANT;
            AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
        }
    }
}