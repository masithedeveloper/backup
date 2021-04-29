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
package com.barclays.absa.banking.payments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
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
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.payments.international.InternationalPaymentsPaymentsActivity;
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor;
import com.barclays.absa.banking.payments.international.services.dto.ClientTypeResponse;
import com.barclays.absa.banking.payments.multiple.MultipleBeneficiarySelectionActivity;
import com.barclays.absa.banking.presentation.adapters.BeneficiaryCustomSectionListAdapter;
import com.barclays.absa.banking.presentation.adapters.SectionListAdapter;
import com.barclays.absa.banking.presentation.adapters.SectionListItem;
import com.barclays.absa.banking.presentation.adapters.SectionListView;
import com.barclays.absa.banking.presentation.shared.FilterResultInterface;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.transactions.RecentTransactionAdapter;
import com.barclays.absa.banking.presentation.utils.ToolBarUtils;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.OperatorPermissionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import styleguide.buttons.OptionActionButtonView;
import styleguide.content.HeadingView;
import styleguide.content.SecondaryContentAndLabelView;

import static com.barclays.absa.banking.beneficiaries.ui.BeneficiaryDetailsActivity.BENEFICIARY_DETAIL_OBJECT;
import static com.barclays.absa.banking.payments.PaymentDetailsActivity.PRE_SELECTED_FROM_ACCOUNT;
import static com.barclays.absa.banking.payments.PaymentsConstants.ONCE_OFF;

public class SelectBeneficiaryPaymentActivity extends BaseActivity implements SearchView.OnQueryTextListener, FilterResultInterface {

    /**
     * The alphabets string array
     */
    private static final String[] alphabets = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "#", "", " "};
    final Object lock = new Object();
    boolean isExecuteRequestDisable;

    private TextView listEmptyTextView;
    private FrameLayout frameLayoutParent;

    private final ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailResponseListener = new ExtendedResponseListener<BeneficiaryDetailsResponse>() {
        @Override
        public void onSuccess(final BeneficiaryDetailsResponse response) {
            dismissProgressDialog();
            isExecuteRequestDisable = false;
            if (response.getBeneficiaryDetails() != null) {
                showPayNewBenStep2Activity(response.getBeneficiaryDetails());
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            isExecuteRequestDisable = false;
            if (failureResponse != null && failureResponse.getErrorMessage() != null) {
                showMessageError(failureResponse.getErrorMessage());
                return;
            }
            showMessageError(getString(R.string.technical_difficulty));
        }
    };

    /**
     * The section list view
     */
    private SectionListView beneficiarySectionListView;
    /**
     * The Beneficiary services list
     */
    private List<BeneficiaryObject> beneficiaryDataObjects;
    /**
     * Last transaction services object
     */
    private List<BeneficiaryObject> lastTransactionDataObject;
    private View selectTransactionHeaderView;

    private Filter mFilter;
    private View recentTransactionBottomDivider;
    private OptionActionButtonView internationalPaymentsContainer;
    private boolean shouldShowInternationalPayments = false;
    private SectionListAdapter beneficiarySectionAdapter;
    private BeneficiaryCustomSectionListAdapter customSectionListAdapter;
    private View headerView;
    private ConstraintLayout noBenSwitchingFoundContainer;
    private LinearLayout noBenFoundContainer;
    private Bundle preSelectFromAccountBundle = new Bundle();
    private BeneficiariesInteractor beneficiariesInteractor = new BeneficiariesInteractor();
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private ExtendedResponseListener<BeneficiaryDetailObject> addBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryDetailObject>() {
        @Override
        public void onSuccess(final BeneficiaryDetailObject beneficiaryDetailObject) {
            dismissProgressDialog();
            startActivity(IntentFactory.getAddNewBeneficiaryPayment(SelectBeneficiaryPaymentActivity.this, beneficiaryDetailObject));
        }
    };

    /**
     * Create sectioned list
     *
     * @param lstBeneficiaryData : Data to bundle parent with corresponding children
     * @return sectioned list array
     */
    public static SectionListItem[] createSectionedList(List<BeneficiaryObject> lstBeneficiaryData) {
        SectionListItem[] tempArray = new SectionListItem[lstBeneficiaryData.size()];
        for (int i = 0; i < tempArray.length; i++) {

            String beneficiaryName = lstBeneficiaryData.get(i).getBeneficiaryName();
            if (beneficiaryName != null) {
                if (beneficiaryName.isEmpty()) {
                    tempArray[i] = new SectionListItem(lstBeneficiaryData.get(i), " ");
                } else if (containsSpecialCharacter(beneficiaryName.charAt(0) + "")) {
                    tempArray[i] = new SectionListItem(lstBeneficiaryData.get(i), alphabets[alphabets.length - 1]);
                } else {
                    for (String alphabetLetter : alphabets) {
                        if (alphabetLetter.equalsIgnoreCase("" + beneficiaryName.charAt(0)))
                            tempArray[i] = new SectionListItem(lstBeneficiaryData.get(i), alphabetLetter);
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
        setContentView(R.layout.activity_beneficiary_select_payment);
        beneficiaryDetailResponseListener.setView(this);
        beneficiaryListResponseListener.setView(this);
        addBeneficiaryExtendedResponseListener.setView(this);

        listEmptyTextView = findViewById(R.id.listEmptyTextView);
        frameLayoutParent = findViewById(R.id.listView);
        selectTransactionHeaderView = getLayoutInflater().inflate(R.layout.select_beneficiary_sub_view, null);
        noBenFoundContainer = selectTransactionHeaderView.findViewById(R.id.no_ben_found_container);

        mScreenName = BMBConstants.PAYMENT_HUB_CONST;
        mSiteSection = BMBConstants.PAYMENTS_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PAYMENT_HUB_CONST, BMBConstants.PAYMENTS_CONST, BMBConstants.TRUE_CONST);
        ToolBarUtils.setToolBarBack(this, getString(R.string.pay), v -> {
            setResult(RESULT_CANCELED);
            finish();
        }, false);
        populateListings(new BeneficiaryListObject());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            preSelectFromAccountBundle.putSerializable(PRE_SELECTED_FROM_ACCOUNT, extras.getSerializable("fromAccount"));
        }

        setupFeatureSwitchingVisibilityToggles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleInternationalPaymentsTab();
    }

    private void handleInternationalPaymentsTab() {
        if (getAbsaCacheService().isClientTypeCached()) {
            if (getAbsaCacheService().isInternationalPaymentsAllowed()) {
                shouldShowInternationalPayments = true;
                internationalPaymentsContainer.setVisibility(View.VISIBLE);
            } else {
                internationalPaymentsContainer.setVisibility(View.GONE);
            }
            fetchAndPopulateBeneficiaryList();
        } else {
            InternationalPaymentsInteractor interactor = new InternationalPaymentsInteractor();
            interactor.fetchClientType(clientTypeResponseListener);
        }

        if (isStandaloneCreditCardAccount()) {
            internationalPaymentsContainer.setVisibility(View.GONE);
        }

        if (!OperatorPermissionUtils.isMainUser()) {
            internationalPaymentsContainer.setVisibility(View.GONE);
        }
    }

    private final ExtendedResponseListener<ClientTypeResponse> clientTypeResponseListener = new ExtendedResponseListener<ClientTypeResponse>() {
        @Override
        public void onSuccess(ClientTypeResponse successResponse) {
            getAbsaCacheService().setInternationalPaymentsAllowed(successResponse.isInternationalPaymentsOptionVisible());
            if (successResponse.isInternationalPaymentsOptionVisible()) {
                shouldShowInternationalPayments = true;
                internationalPaymentsContainer.setVisibility(View.VISIBLE);
            } else {
                internationalPaymentsContainer.setVisibility(View.GONE);
            }
            fetchAndPopulateBeneficiaryList();
            getAbsaCacheService().setClientTypeCached(true);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            fetchAndPopulateBeneficiaryList();
        }
    };

    private void fetchAndPopulateBeneficiaryList() {
        if (AbsaCacheManager.getInstance().isBeneficiariesCached(BMBConstants.PASS_PAYMENT)) {
            refreshScreen(beneficiaryCacheService.getPaymentBeneficiaries(), beneficiaryCacheService.getPaymentRecentTransactionList());
            dismissProgressDialog();
        } else {
            requestBeneficiaries();
        }
    }

    private void requestBeneficiaries() {
        beneficiariesInteractor.fetchBeneficiaryList(PASS_PAYMENT, beneficiaryListResponseListener);
    }

    private ExtendedResponseListener<BeneficiaryListObject> beneficiaryListResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setPaymentsBeneficiaries(successResponse.getPaymentBeneficiaryList());
            beneficiaryCacheService.setPaymentRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_PAYMENT);
            refreshScreen(successResponse.getPaymentBeneficiaryList(), successResponse.getLatestTransactionBeneficiaryList());
            if (getAbsaCacheService().isClientTypeCached()) {
                dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(ResponseObject response) {
            if (response != null && AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equals(response.getResponseCode())) {
                dismissProgressDialog();
                refreshScreen(new ArrayList<>(), new ArrayList<>());
            } else
                BaseAlertDialog.INSTANCE.showRetryErrorDialog(ResponseObject.extractErrorMessage(response), new AlertBox.AlertRetryListener() {
                    @Override
                    public void retry() {
                        requestBeneficiaries();
                    }
                });
        }
    };

    private void populateListings(BeneficiaryListObject beneficiaryListObject) {
        beneficiarySectionListView = findViewById(R.id.beneficiarySectionListView);
        beneficiarySectionListView.setTextFilterEnabled(true);
        beneficiaryDataObjects = beneficiaryListObject.getPaymentBeneficiaryList() != null ? beneficiaryListObject.getPaymentBeneficiaryList() : new ArrayList<>();
        lastTransactionDataObject = beneficiaryListObject.getLatestTransactionBeneficiaryList();

        // Sort currency services alphabetically based on currency name
        Collections.sort(beneficiaryDataObjects, (beneficiaryObject1, beneficiaryObject2) -> beneficiaryObject1.getBeneficiaryName() != null ? beneficiaryObject1.getBeneficiaryName().compareToIgnoreCase(beneficiaryObject2.getBeneficiaryName() != null ? beneficiaryObject2.getBeneficiaryName() : "") : -1);

        // Adapter to bundle parent with respective children
        customSectionListAdapter = new BeneficiaryCustomSectionListAdapter(this, createSectionedList(beneficiaryDataObjects), beneficiaryDataObjects);

        beneficiarySectionAdapter = new SectionListAdapter(getLayoutInflater(), customSectionListAdapter);
        beneficiarySectionAdapter.setFilterInterface(this);

        headerView = initHeaderListView();
        beneficiarySectionListView.addHeaderView(headerView);
        beneficiarySectionListView.setTextFilterEnabled(false);
        beneficiarySectionListView.setAdapter(beneficiarySectionAdapter);
        mFilter = beneficiarySectionAdapter.getFilter();

        beneficiarySectionListView.setOnItemClickListener((parent, view, position, id) -> {
            synchronized (lock) {
                if (!isExecuteRequestDisable) {
                    isExecuteRequestDisable = true;
                    if (beneficiarySectionListView.getItemAtPosition(position) instanceof SectionListItem) {
                        BeneficiaryObject data = (BeneficiaryObject) ((SectionListItem) beneficiarySectionListView.getItemAtPosition(position)).item;
                        beneficiariesInteractor.fetchBeneficiaryDetails(data.getBeneficiaryID(), PASS_PAYMENT, beneficiaryDetailResponseListener);
                    }
                } else {
                    BMBLogger.d("x-e", "Request Not Allowed");
                }
            }
        });
    }

    private void refreshScreen(List<BeneficiaryObject> beneficiaryListObject, List<BeneficiaryObject> recentTransactionList) {
        if (beneficiarySectionAdapter != null) {
            beneficiaryDataObjects = beneficiaryListObject == null ? new ArrayList<>() : beneficiaryListObject;
            Collections.sort(beneficiaryDataObjects, (beneficiaryObject1, beneficiaryObject2) -> beneficiaryObject1.getBeneficiaryName() != null ? beneficiaryObject1.getBeneficiaryName().compareToIgnoreCase(beneficiaryObject2.getBeneficiaryName() != null ? beneficiaryObject2.getBeneficiaryName() : "") : -1);

            customSectionListAdapter.setBeneficiaryListData(beneficiaryDataObjects);
            customSectionListAdapter.setItems(SelectBeneficiaryPaymentActivity.createSectionedList(customSectionListAdapter.getBeneficiaryListData()));
            if (recentTransactionList == null) {
                lastTransactionDataObject = new ArrayList<>();
            } else {
                lastTransactionDataObject = recentTransactionList;
            }
            beneficiarySectionListView.removeHeaderView(headerView);

            headerView = initHeaderListView();
            FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
            if (beneficiaryListObject != null && beneficiaryListObject.size() > 0) {
                if (featureSwitchingToggles.getBeneficiarySwitching() != FeatureSwitchingStates.ACTIVE.getKey()) {
                    noBenSwitchingFoundContainer.setVisibility(View.GONE);
                } else {
                    noBenFoundContainer.setVisibility(View.GONE);
                }
                if (!lastTransactionDataObject.isEmpty() && recentTransactionBottomDivider != null) {
                    recentTransactionBottomDivider.setVisibility(View.VISIBLE);
                }
            } else {
                if (featureSwitchingToggles.getBeneficiarySwitching() == FeatureSwitchingStates.ACTIVE.getKey()) {
                    noBenSwitchingFoundContainer.setVisibility(View.VISIBLE);
                } else {
                    noBenFoundContainer.setVisibility(View.VISIBLE);
                }
                if (lastTransactionDataObject.isEmpty() && recentTransactionBottomDivider != null) {
                    recentTransactionBottomDivider.setVisibility(View.GONE);
                }
            }
            beneficiarySectionListView.addHeaderView(headerView);
            beneficiarySectionAdapter.updateSessionCache();
            customSectionListAdapter.notifyDataSetChanged();
            beneficiarySectionAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        }

    }

    /**
     * Init Header view contains Recent Transaction List of beneficiary.
     *
     * @return View
     */
    private View initHeaderListView() {
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        HeadingView recentTransactionHeadingView = selectTransactionHeaderView.findViewById(R.id.recentTransactionHeadingView);
        final View.OnClickListener addBeneficiaryImageViewClickListener = view -> startActivity(new Intent(this, NewPaymentBeneficiaryDetailsActivity.class));
        noBenSwitchingFoundContainer = selectTransactionHeaderView.findViewById(R.id.noBeneficiaryFoundSwitchingContainer);
        noBenSwitchingFoundContainer.findViewById(R.id.addNewBeneficiaryImageView).setOnClickListener(addBeneficiaryImageViewClickListener);
        if (featureSwitchingToggles.getBeneficiarySwitching() == FeatureSwitchingStates.ACTIVE.getKey()) {
            SecondaryContentAndLabelView beneficiaryDescriptionLabel = noBenSwitchingFoundContainer.findViewById(R.id.addNewBeneficiaryDescriptionLabel);
            beneficiaryDescriptionLabel.getContentTextView().setTextColor(ContextCompat.getColor(this, R.color.foil));
            beneficiaryDescriptionLabel.getContentTextView().setGravity(Gravity.CENTER);
            CommonUtils.makeTextClickable(this,
                    R.string.payment_beneficiaries_get_started,
                    getString(R.string.payment_taking_photograph_hyperlink),
                    beneficiaryDescriptionLabel.getContentTextView(), R.color.foil, new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            startActivity(new Intent(SelectBeneficiaryPaymentActivity.this, BeneficiaryImportExplanationActivity.class));
                        }
                    });
        } else {
            selectTransactionHeaderView.findViewById(R.id.addNewBeneficiaryButton).setOnClickListener(v -> beneficiariesInteractor.addPaymentBeneficiary(addBeneficiaryExtendedResponseListener));
        }

        LinearLayout recentTransactionContainer = selectTransactionHeaderView.findViewById(R.id.recentTransactionView);
        recentTransactionBottomDivider = selectTransactionHeaderView.findViewById(R.id.recentTransactionsBottomDivider);
        ListView recentTransactionListView = new ListView(this);
        recentTransactionListView.setDivider(null);
        recentTransactionListView.setDividerHeight(0);
        recentTransactionListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (lastTransactionDataObject != null && lastTransactionDataObject.size() == 0) {
            recentTransactionHeadingView.setVisibility(View.GONE);
            recentTransactionContainer.setVisibility(View.GONE);
            recentTransactionBottomDivider.setVisibility(View.GONE);
        } else {
            recentTransactionHeadingView.setVisibility(View.VISIBLE);
            recentTransactionContainer.setVisibility(View.VISIBLE);
            recentTransactionBottomDivider.setVisibility(View.VISIBLE);
        }

        RecentTransactionAdapter mAdapter = new RecentTransactionAdapter(this, lastTransactionDataObject);
        recentTransactionListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(recentTransactionListView, mAdapter);
        if (recentTransactionContainer.getChildCount() <= 1) {
            recentTransactionContainer.addView(recentTransactionListView);
        }
        recentTransactionListView.setOnItemClickListener((parent, view, position, id) -> {
            AnalyticsUtils.getInstance().trackRecentlyPaidEvent(BMBConstants.PAYMENT_HUB_CONST);
            BeneficiaryObject data = lastTransactionDataObject.get(position);
            beneficiariesInteractor.fetchBeneficiaryDetails(data.getBeneficiaryID(), PASS_PAYMENT, beneficiaryDetailResponseListener);
        });

        OptionActionButtonView payNewBeneficiaryContainer = selectTransactionHeaderView.findViewById(R.id.payNewBeneficiaryLink);
        payNewBeneficiaryContainer.setOnClickListener(addBeneficiaryImageViewClickListener);
        payNewBeneficiaryContainer.setContentDescription(getString(R.string.talkback_pay_new_beneficiary));

        OptionActionButtonView makeOnceOffContainer = selectTransactionHeaderView.findViewById(R.id.makeOnceOffLink);
        makeOnceOffContainer.setContentDescription(getString(R.string.talkback_pay_section_onceoff_options));

        makeOnceOffContainer.setOnClickListener(v -> {
            Intent onceOffIntent = new Intent(this, NewPaymentBeneficiaryDetailsActivity.class);
            onceOffIntent.putExtra(ONCE_OFF, true);
            startActivity(onceOffIntent);
        });

        internationalPaymentsContainer = selectTransactionHeaderView.findViewById(R.id.internationalPaymentsLink);

        if (shouldShowInternationalPayments) {
            internationalPaymentsContainer.setVisibility(View.VISIBLE);
        } else {
            internationalPaymentsContainer.setVisibility(View.GONE);
        }

        if (isBusinessAccount() || !OperatorPermissionUtils.isMainUser()) {
            internationalPaymentsContainer.setVisibility(View.GONE);
        }

        internationalPaymentsContainer.setOnClickListener(v -> {
            if (featureSwitchingToggles.getInternationalPayments() == FeatureSwitchingStates.DISABLED.getKey()) {
                startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_international_payments))));
            } else {
                navigateToWesternUnionDisclaimerScreen();
            }
        });
        internationalPaymentsContainer.setContentDescription(getString(R.string.talkback_pay_section_make_international_payments));

        OptionActionButtonView multipleBeneficiaryPayment = selectTransactionHeaderView.findViewById(R.id.multipleBeneficiaryPaymentLink);
        multipleBeneficiaryPayment.setContentDescription(getString(R.string.talkback_pay_section_multiple_payments));
        if (0 < beneficiaryDataObjects.size()) {
            multipleBeneficiaryPayment.setVisibility(View.VISIBLE);
            multipleBeneficiaryPayment.setOnClickListener(view -> {
                if (featureSwitchingToggles.getMultiplePayments() == FeatureSwitchingStates.DISABLED.getKey()) {
                    startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_multiple_payments))));
                } else {
                    getAppCacheService().setMultiplePaymentSelectedBeneficiaryList(new ArrayList<>());
                    startActivity(new Intent(SelectBeneficiaryPaymentActivity.this, MultipleBeneficiarySelectionActivity.class));
                }
            });
        } else {
            multipleBeneficiaryPayment.setVisibility(View.GONE);
        }

        dismissProgressDialog();
        return selectTransactionHeaderView;
    }

    private void setupFeatureSwitchingVisibilityToggles() {
        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getInternationalPayments() == FeatureSwitchingStates.GONE.getKey()) {
            internationalPaymentsContainer.setVisibility(View.GONE);
        }

        if (featureSwitchingToggles.getMultiplePayments() == FeatureSwitchingStates.GONE.getKey()) {
            selectTransactionHeaderView.findViewById(R.id.multipleBeneficiaryPaymentLink).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (beneficiaryDataObjects != null && beneficiaryDataObjects.size() > 0) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.search_menu, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                searchItem.setContentDescription(getString(R.string.talkback_pay_section_search_payment));
            }

            SearchManager searchManager = (SearchManager) SelectBeneficiaryPaymentActivity.this.getSystemService(Context.SEARCH_SERVICE);

            SearchView searchView = null;
            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }
            if (searchView != null && searchManager != null) {
                setupSearchView(searchView);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(SelectBeneficiaryPaymentActivity.this.getComponentName()));
            }
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
        if (beneficiaryDataObjects.size() == 0)
            return false;
        startSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (beneficiaryDataObjects.size() == 0)
            return false;
        startSearch(query);
        return false;
    }

    private void startSearch(String query) {
        if (TextUtils.isEmpty(query.trim())) {
            if (beneficiarySectionListView.getHeaderViewsCount() == 0) {
                beneficiarySectionListView.addHeaderView(selectTransactionHeaderView);
                beneficiarySectionListView.requestLayout();
                mFilter.filter(query.trim());
            }
        } else {
            beneficiarySectionListView.removeHeaderView(selectTransactionHeaderView);
            beneficiarySectionListView.requestLayout();
            mFilter.filter(query.trim());
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView, BaseAdapter mBaseAdapter) {
        if (mBaseAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < mBaseAdapter.getCount(); i++) {
            View listItem = mBaseAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight() * (mBaseAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void showPayNewBenStep2Activity(BeneficiaryDetailObject beneficiaryDetail) {
        final Intent intent = new Intent();
        intent.putExtra("iipStatus", beneficiaryDetail.getBranchIIPStatus());
        intent.putExtra(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetail);
        intent.putExtra("fromActivity", "MANAGE_BENEFICIARY");
        intent.setClass(this, PaymentDetailsActivity.class);
        intent.putExtra(BMBConstants.HAS_IMAGE, beneficiaryDetail.getHasImage());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        intent.putExtra(PRE_SELECTED_FROM_ACCOUNT, preSelectFromAccountBundle.getSerializable(PRE_SELECTED_FROM_ACCOUNT));
        this.startActivity(intent);
    }

    private void navigateToWesternUnionDisclaimerScreen() {
        Intent intent = new Intent(this, InternationalPaymentsPaymentsActivity.class);
        startActivity(intent);
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
