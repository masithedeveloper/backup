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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryType;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendUnredeemedAccounts;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.cashSend.services.CashSendService;
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusInteractor;
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusResponseData;
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse;
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusCancellationActivity;
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusLimitsActivity;
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusRegistrationActivity;
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity;
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusUtils;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AlertBox;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.adapters.BeneficiaryCustomSectionListAdapterPrepaid;
import com.barclays.absa.banking.presentation.adapters.SectionListAdapterAirtime;
import com.barclays.absa.banking.presentation.adapters.SectionListItem;
import com.barclays.absa.banking.presentation.shared.FilterResultInterface;
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet;
import com.barclays.absa.banking.presentation.transactions.RecentTransactionAdapterPrepaid;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import styleguide.buttons.OptionActionButtonView;
import styleguide.cards.Alert;
import styleguide.cards.AlertView;

@Deprecated
public class CashSendActivity extends BaseActivity implements SearchView.OnQueryTextListener, OnClickListener, FilterResultInterface {

    private SectionListViewCashSend mBeneficiarySectionListView;
    private List<BeneficiaryObject> mBeneficiaryDataObjects;
    private List<BeneficiaryObject> mLastTransactionDataObject;
    private BeneficiaryCustomSectionListAdapterPrepaid customSectionListAdapter;
    private SectionListAdapterAirtime mBeneficiarySectionAdapter;
    private View headerView, cashSendNavigationHeader;
    private Filter mFilter;
    private ConstraintLayout mNoBenFoundContainer;
    private CashSendService cashSendService = new CashSendInteractor();
    private BeneficiariesService beneficiariesService = new BeneficiariesInteractor();
    private CashSendPlusInteractor cashSendPlusService = new CashSendPlusInteractor();
    private final IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private TextView listEmptyTextView;
    private FrameLayout frameLayoutParent;
    private AlertView bannerAlertView;
    public static boolean isCashSendPlus = false;

    public final static String IS_CASH_SEND_PLUS = "isCashSendPlus";

    private ExtendedResponseListener<BeneficiaryDetailsResponse> requestBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryDetailsResponse>() {
        @Override
        public void onSuccess(final BeneficiaryDetailsResponse beneficiaryDetailObject) {
            Intent intent = new Intent(CashSendActivity.this, CashSendBeneficiaryActivity.class);
            intent.putExtra(RESULT, beneficiaryDetailObject.getBeneficiaryDetails());
            intent.putExtra(IS_CASH_SEND_PLUS, isCashSendPlus);
            startActivityIfAvailable(intent);
            dismissProgressDialog();
        }
    };

    private ExtendedResponseListener<CashSendUnredeemedAccounts> viewUnredeemedExtendedResponseListener = new ExtendedResponseListener<CashSendUnredeemedAccounts>() {
        @Override
        public void onSuccess(final CashSendUnredeemedAccounts cashSendUnredeemedAccounts) {
            Intent intent = new Intent(CashSendActivity.this, UnredeemedTransactionActivity.class);
            intent.putExtra(RESULT, cashSendUnredeemedAccounts);
            intent.putExtra(IS_CASH_SEND_PLUS, isCashSendPlus);
            startActivity(intent);
            dismissProgressDialog();
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> pullBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {

        @Override
        public void onSuccess(final BeneficiaryListObject response) {
            beneficiaryCacheService.setCashSendBeneficiaries(response.getCashsendBeneficiaryList());
            beneficiaryCacheService.setCashSendRecentTransactionList(response.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_CASHSEND);
            updateCashSendListing(response.getCashsendBeneficiaryList(), response.getLatestTransactionBeneficiaryList());
            dismissProgressDialog();
        }

        @Override
        public void onFailure(final ResponseObject response) {
            dismissProgressDialog();
            if (response != null && AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equals(response.getResponseCode())) {
                updateCashSendListing(new ArrayList<>(), new ArrayList<>());
            } else {
                BaseAlertDialog.INSTANCE.showRetryErrorDialog(ResponseObject.extractErrorMessage(response), new AlertBox.AlertRetryListener() {
                    @Override
                    public void retry() {
                        requestBeneficiaries();
                    }
                });
            }
        }
    };
    private ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse> checkCashPlusRegStatusExtendedResponseListener = new ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse>() {
        @Override
        public void onSuccess(final CheckCashSendPlusRegistrationStatusResponse registrationStatusResponse) {
            navigateToCashSendPlusRegistration(registrationStatusResponse);
            getAppCacheService().setCashSendPlusRegistrationStatus(registrationStatusResponse);
            dismissProgressDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashsend_hub_activity);
        requestBeneficiaryExtendedResponseListener.setView(this);
        viewUnredeemedExtendedResponseListener.setView(this);
        pullBeneficiaryExtendedResponseListener.setView(this);

        listEmptyTextView = findViewById(R.id.listEmptyTextView);
        frameLayoutParent = findViewById(R.id.frameLayout);
        bannerAlertView = findViewById(R.id.bannerAlertView);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(IS_CASH_SEND_PLUS)) {
            isCashSendPlus = extras.getBoolean(IS_CASH_SEND_PLUS, false);
        }

        if (isCashSendPlus) {
            setToolBarBack(R.string.cash_send_plus_title, true);
        } else {
            setToolBarBack(R.string.cashsend, true);
        }
        mScreenName = BMBConstants.CASHSEND_HOME_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_HOME_CONST, BMBConstants.CASHSEND_CONST,
                BMBConstants.TRUE_CONST);

        populateListings(new BeneficiaryListObject());

        if (AbsaCacheManager.getInstance().isBeneficiariesCached(BMBConstants.PASS_CASHSEND)) {
            updateCashSendListing(beneficiaryCacheService.getCashSendBeneficiaries(), beneficiaryCacheService.getCashSendRecentTransactionList());
        } else {
            requestBeneficiaries();
        }

        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        if (isBusinessAccount() && !isOperator() && featureSwitchingToggles.getBusinessBankingCashSendPlus() == FeatureSwitchingStates.ACTIVE.getKey()) {
            CheckCashSendPlusRegistrationStatusResponse cachedRegistrationStatus = getAppCacheService().getCashSendPlusRegistrationStatus();
            if (cachedRegistrationStatus != null) {
                navigateToCashSendPlusRegistration(cachedRegistrationStatus);
            } else {
                checkCashSendPlusStatus();
            }
        }
    }

    private void navigateToCashSendPlusRegistration(CheckCashSendPlusRegistrationStatusResponse registrationStatusResponse) {
        CashSendPlusResponseData responseData = registrationStatusResponse.getCashSendPlusResponseData();
        if (CashSendPlusUtils.INSTANCE.isNotFound(responseData)) {
            showCashSendPlusBanner();
        }
    }

    private void checkCashSendPlusStatus() {
        cashSendPlusService.sendCheckCashSendPlusRegistration(checkCashPlusRegStatusExtendedResponseListener);
    }

    public void requestBeneficiaries() {
        cashSendService.getCashSendBeneficiariesList(pullBeneficiaryExtendedResponseListener);
    }

    private void populateListings(BeneficiaryListObject beneficiaryListObject) {
        mBeneficiarySectionListView = findViewById(R.id.beneficiarySectionListView);
        mBeneficiarySectionListView.setTextFilterEnabled(true);
        if (beneficiaryListObject != null) {
            mBeneficiaryDataObjects = beneficiaryListObject.getCashsendBeneficiaryList();
            mLastTransactionDataObject = beneficiaryListObject.getLatestTransactionBeneficiaryList();
        }
        if (mBeneficiaryDataObjects == null) {
            mBeneficiaryDataObjects = new ArrayList<>();
        }
        if (mLastTransactionDataObject == null) {
            mLastTransactionDataObject = new ArrayList<>();
        }
        CommonUtils.sortBeneficiaryData(mBeneficiaryDataObjects);
        // Adapter to bundle parent with respective children
        customSectionListAdapter = new BeneficiaryCustomSectionListAdapterPrepaid(this, CommonUtils.createSectionedList(mBeneficiaryDataObjects), mBeneficiaryDataObjects);
        mBeneficiarySectionAdapter = new SectionListAdapterAirtime(getLayoutInflater(), customSectionListAdapter);
        headerView = populateListViewHeaderData();
        mBeneficiarySectionListView.addHeaderView(headerView);
        mBeneficiarySectionListView.setTextFilterEnabled(false);
        mBeneficiarySectionListView.setAdapter(mBeneficiarySectionAdapter);
        mBeneficiarySectionAdapter.setFilterInterface(this);
        mFilter = mBeneficiarySectionAdapter.getFilter();
        mBeneficiarySectionListView.setOnItemClickListener((parent, view, position, id) -> {
            if (mBeneficiarySectionListView.getItemAtPosition(position) instanceof SectionListItem) {
                BeneficiaryObject data = (BeneficiaryObject) ((SectionListItem) mBeneficiarySectionListView.getItemAtPosition(position)).item;
                beneficiariesService.fetchBeneficiaryDetails(data.getBeneficiaryID(), BMBConstants.PASS_CASHSEND, requestBeneficiaryExtendedResponseListener);
            }
        });
    }

    private View populateListViewHeaderData() {
        cashSendNavigationHeader = getLayoutInflater().inflate(R.layout.cash_send_navigation_layout, null);
        TextView comingSoonDisclaimer = cashSendNavigationHeader.findViewById(R.id.comingSoonDisclaimer);
        LinearLayout mRecentTransMainContainer = cashSendNavigationHeader.findViewById(R.id.recentlyPaidBeneficiaryContainer);
        mNoBenFoundContainer = cashSendNavigationHeader.findViewById(R.id.noBeneficiaryFoundContainer);
        View recentCashSendDivider = cashSendNavigationHeader.findViewById(R.id.recentCashSendDividerView);
        LinearLayout mLatestTransactionContainer = cashSendNavigationHeader.findViewById(R.id.recentTransactionContainer);
        ImageView addNewBeneficiaryImageView = cashSendNavigationHeader.findViewById(R.id.addNewBeneficiaryImageView);

        OptionActionButtonView sendMultipleButtonView = cashSendNavigationHeader.findViewById(R.id.sendMultipleButtonView);

        cashSendNavigationHeader.findViewById(R.id.sendCashToSomeoneNewView).setOnClickListener(this);
        cashSendNavigationHeader.findViewById(R.id.onceOffView).setOnClickListener(this);
        cashSendNavigationHeader.findViewById(R.id.unredeemedView).setOnClickListener(this);
        cashSendNavigationHeader.findViewById(R.id.sendCashToMyselfView).setOnClickListener(this);
        sendMultipleButtonView.setOnClickListener(this);
        addNewBeneficiaryImageView.setOnClickListener(this);

        if (isCashSendPlus && beneficiaryCacheService.getCashSendBeneficiaries().size() > 0) {
            sendMultipleButtonView.setVisibility(View.VISIBLE);
        }

        ListView mRecentTransactionListView = new ListView(this);
        mRecentTransactionListView.setDivider(null);
        mRecentTransactionListView.setDividerHeight(0);
        mRecentTransactionListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (mLastTransactionDataObject != null && mLastTransactionDataObject.size() == 0) {
            mRecentTransMainContainer.setVisibility(View.GONE);
        } else {
            comingSoonDisclaimer.setVisibility(View.GONE);
            mRecentTransMainContainer.setVisibility(View.VISIBLE);
            recentCashSendDivider.setVisibility(View.VISIBLE);
            RecentTransactionAdapterPrepaid mAdapter = new RecentTransactionAdapterPrepaid(this, mLastTransactionDataObject);
            mRecentTransactionListView.setAdapter(mAdapter);
            CommonUtils.setListViewHeightBasedOnChildren(mRecentTransactionListView, mAdapter);
            mLatestTransactionContainer.addView(mRecentTransactionListView);

            mRecentTransactionListView.setOnItemClickListener((parent, view, position, id) -> {
                BeneficiaryObject data = mLastTransactionDataObject.get(position);
                AnalyticsUtils.getInstance().trackRecentlyPaidEvent(BMBConstants.CASHSEND_HOME_CONST);
                beneficiariesService.fetchBeneficiaryDetails(data.getBeneficiaryID(),
                        BMBConstants.PASS_CASHSEND, requestBeneficiaryExtendedResponseListener);
            });
        }
        return cashSendNavigationHeader;
    }

    /**
     * Update list of cashSend beneficiaries.
     */
    private void updateCashSendListing(List<BeneficiaryObject> beneficiaryListObject, List<BeneficiaryObject> recentTransactionList) {
        if (mBeneficiarySectionAdapter != null) {
            mBeneficiaryDataObjects = beneficiaryListObject == null ? new ArrayList<>() : beneficiaryListObject;
            CommonUtils.sortBeneficiaryData(mBeneficiaryDataObjects);
            customSectionListAdapter.setBeneficiaryListData(mBeneficiaryDataObjects);
            customSectionListAdapter.setItems(CommonUtils.createSectionedList(customSectionListAdapter.getBeneficiaryListData()));

            mLastTransactionDataObject = recentTransactionList != null ? recentTransactionList : new ArrayList<>();

            mBeneficiarySectionListView.removeHeaderView(headerView);
            headerView = populateListViewHeaderData();
            if (beneficiaryListObject != null && beneficiaryListObject.size() > 0) {
                mNoBenFoundContainer.setVisibility(View.GONE);
            } else {
                mNoBenFoundContainer.setVisibility(View.VISIBLE);
            }
            mBeneficiarySectionListView.addHeaderView(headerView);
            mBeneficiarySectionAdapter.updateSessionCache();
            customSectionListAdapter.notifyDataSetChanged();
            mBeneficiarySectionAdapter.notifyDataSetChanged();
            if (AbsaCacheManager.getInstance().isBeneficiaryCachingAllowed()) {
                AbsaCacheManager.getInstance().updateBeneficiaryList(BMBConstants.PASS_CASHSEND, new ArrayList<>(mBeneficiaryDataObjects));
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isCashSendPlus && mBeneficiaryDataObjects != null && mBeneficiaryDataObjects.isEmpty()) {
            return super.onCreateOptionsMenu(menu);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu_dark, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (isCashSendPlus) {
            MenuItem moreMenuItem = menu.findItem(R.id.action_more);
            moreMenuItem.setVisible(true);
            searchItem.setVisible(false);
        }

        SearchManager searchManager = (SearchManager) CashSendActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView mSearchView = null;
        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setIconifiedByDefault(true);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setSubmitButtonEnabled(false);
            mSearchView.setQueryRefinementEnabled(false);
        }
        if (searchManager != null && mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(CashSendActivity.this.getComponentName()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                AnalyticsUtils.getInstance().trackSearchEvent(mScreenName);
                break;
            case R.id.action_more:
                openBottomSheet();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void openBottomSheet() {
        new BottomSheet.Builder(this, R.style.BottomSheet_StyleDialog)
                .title(R.string.manage_cash_send_service)
                .sheet(R.menu.cash_send_plus_bottom_sheet_menu)
                .listener(item -> {
                    switch (item.getItemId()) {
                        case R.id.manageBeneficiaryMenuItem:
                            navigateToCashSendBeneficiaries();
                            break;
                        case R.id.manageCashSendPlusLimitMenuItem:
                            navigateToCashSendPlusManageLimits();
                            break;
                        case R.id.cancelCashSendPlusItemMenu:
                            navigateToCashSendPlusCancellation();
                            break;
                        case R.id.cancelBottomSheetItemMenu:
                            break;
                    }
                    return false;
                })
                .build()
                .show();
    }

    private void startSearch(String query) {
        if (TextUtils.isEmpty(query.trim())) {
            if (mBeneficiarySectionListView.getHeaderViewsCount() == 0) {
                mBeneficiarySectionListView.addHeaderView(cashSendNavigationHeader);
                mBeneficiarySectionListView.requestLayout();
                mFilter.filter(query.trim());
            }
        } else {
            mBeneficiarySectionListView.removeHeaderView(cashSendNavigationHeader);
            mBeneficiarySectionListView.requestLayout();
            mFilter.filter(query.trim());
        }
    }

    private void navigateToAddNewCashSendBeneficiary() {
        AnalyticsUtil.INSTANCE.tagCashSend("BeneficiaryCashSendPlusTab_AddBeneficiaryClicked");
        Intent addNewBeneficiaryIntent = new Intent(CashSendActivity.this, CashSendToNewBeneficaryActivity.class);
        addNewBeneficiaryIntent.putExtra("isCashSendFlow", true);
        startActivity(addNewBeneficiaryIntent);
    }

    private void navigateToCashSendPlusRegistration() {
        AnalyticsUtil.INSTANCE.trackAction(CASHSEND_CONST, "CSPRegister_CashSendMenu_RegisterClicked");
        startActivity(new Intent(this, CashSendPlusRegistrationActivity.class));
    }

    private void navigateToCashSendPlusCancellation() {
        startActivity(new Intent(this, CashSendPlusCancellationActivity.class));
    }

    private void navigateToCashSendPlusManageLimits() {
        startActivity(new Intent(this, CashSendPlusLimitsActivity.class));
    }

    private void navigateToCashSendBeneficiaries() {
        Intent intent = new Intent(this, BeneficiaryLandingActivity.class);
        intent.putExtra(BeneficiaryLandingActivity.TAB_TO_SELECT_EXTRA, BeneficiaryType.BENEFICIARY_TYPE_CASHSEND);
        startActivity(intent);
    }

    private void navigateToCashSendPlusSendMultiple() {
        startActivity(new Intent(this, CashSendPlusSendMultipleActivity.class));
    }

    private void navigateToSendCashToMyself() {
        Intent sendSelfIntent = new Intent(CashSendActivity.this, CashSendSelfActivity.class);
        sendSelfIntent.putExtra(IS_CASH_SEND_PLUS, isCashSendPlus);
        startActivity(sendSelfIntent);
    }

    private void navigateToSendOnceOffCashSend() {
        Intent onceOffIntent = new Intent(CashSendActivity.this, CashSendOnceOffActivity.class);
        onceOffIntent.putExtra(IS_CASH_SEND_PLUS, isCashSendPlus);
        startActivity(onceOffIntent);
    }

    private void showCashSendPlusBanner() {
        bannerAlertView.setVisibility(View.VISIBLE);
        bannerAlertView.setAlert(new Alert("", getString(R.string.register_for_cash_send_now)));
        bannerAlertView.hideRightImage();
        bannerAlertView.setOnClickListener(view -> navigateToCashSendPlusRegistration());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewBeneficiaryImageView:
            case R.id.sendCashToSomeoneNewView:
                navigateToAddNewCashSendBeneficiary();
                break;
            case R.id.sendCashToMyselfView:
                navigateToSendCashToMyself();
                break;
            case R.id.onceOffView:
                navigateToSendOnceOffCashSend();
                break;
            case R.id.sendMultipleButtonView:
                navigateToCashSendPlusSendMultiple();
                break;
            case R.id.unredeemedView:
                cashSendService.fetchUnredeemedCashSendList(isCashSendPlus, viewUnredeemedExtendedResponseListener);
                break;
            default:
                break;
        }
    }

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
}