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
package com.barclays.absa.banking.card.ui.creditCard.hub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtection;
import com.barclays.absa.banking.boundary.model.creditCardInsurance.LifeInsurance;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.card.ui.CardCache;
import com.barclays.absa.banking.card.ui.CardManagementView;
import com.barclays.absa.banking.card.ui.creditCard.TransactionHistoryView;
import com.barclays.absa.banking.card.ui.creditCard.vcl.CreditCardHubBackPressedListener;
import com.barclays.absa.banking.card.ui.creditCard.vcl.CreditCardVCLBaseActivity;
import com.barclays.absa.banking.card.ui.creditCard.vcl.VclInformationFragment;
import com.barclays.absa.banking.card.ui.creditCardInsurance.CreditCardInsurancePresenter;
import com.barclays.absa.banking.card.ui.creditCardInsurance.CreditCardInsuranceView;
import com.barclays.absa.banking.databinding.CreditCardHubActivityBinding;
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.SecondaryCardFetchMandateViewModel;
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse;
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel;
import com.barclays.absa.banking.shared.GenericStatementFragment;
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericStatementView;
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.SharedPreferenceService;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import styleguide.bars.CollapsingAppBarView;
import styleguide.bars.FragmentPagerItem;

import static com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity.ACCOUNT_OBJECT;

public class CreditCardHubActivity extends BaseActivity implements CreditCardHubView, CreditCardInsuranceView, TransactionHistoryFilterFragment.UpdateFilteringOptions, CardManagementView, TransactionHistoryView, GenericStatementView {

    public static final String SITE_SECTION = "Credit Card Protection";
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String MINIMUM_PAYABLE_AMOUNT = "MINIMUM_PAYABLE_AMOUNT";
    public static final String CREDIT = "Credit";
    public static final String CARD_NUMBER = "CARD_NUMBER";
    public static final String CREDIT_CARD = "CREDIT_CARD";
    public static final String ALL_TRANSACTIONS = "A";
    public static final String MONEY_IN = "I";
    public static final String MONEY_OUT = "O";
    public static final String UNCLEARED = "U";
    public static final String IS_FROM_CARD_HUB = "IS_FROM_CARD_HUB";
    public static final String CARD_ITEM = "CARD_ITEM";
    public static final int ANIMATION_DURATION_MILLIS = 1000;
    public static final String CREDIT_CARD_HUB = "CreditCard";

    private CreditCardHubActivityBinding binding;
    private CreditCardHubPresenter presenter;
    private CreditCardResponseObject creditCardResponse;
    private CreditCardHeaderFragment headerFragment;
    private TransactionHistoryFragment transactionHistoryFragment;
    private CreditCardInsurancePresenter creditCardInsurancePresenter;
    protected CreditCardHubBackPressedListener onBackPressedListener;
    private GenericTransactionHistoryViewModel genericTransactionHistoryViewModel;
    private GetSecondaryCardMandateResponse secondaryCardMandateResponse;

    private boolean hasResumed;
    private String accountNumber;
    private List<Transaction> transactions;
    private RiskBasedApproachViewModel riskBasedApproachViewModel;
    private FilteringOptions filteringOptions;
    public boolean shouldShowInsuranceQuoteFailureMessage = false;
    public LifeInsurance lifeInsurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtil.INSTANCE.trackAction("Credit Card Hub", "Dashboard");
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.credit_card_hub_activity, null, false);
        setContentView(binding.getRoot());

        presenter = new CreditCardHubPresenter(new WeakReference<>(this));
        riskBasedApproachViewModel = new ViewModelProvider(this).get(RiskBasedApproachViewModel.class);
        genericTransactionHistoryViewModel = new ViewModelProvider(this).get(GenericTransactionHistoryViewModel.class);
        creditCardInsurancePresenter = new CreditCardInsurancePresenter(this);

        setupFilterOptions();

        if (getIntent().getExtras() != null) {
            accountNumber = getIntent().getExtras().getString(ACCOUNT_NUMBER);
            AccountObject accountObject = (AccountObject) getIntent().getExtras().getSerializable(ACCOUNT_OBJECT);
            if (accountObject != null) {
                genericTransactionHistoryViewModel.setAccountDetail(accountObject);
            } else if (accountNumber != null) {
                genericTransactionHistoryViewModel.getAccountObject(accountNumber);
            } else {
                finish();
                return;
            }
            presenter.onViewLoaded(accountNumber, filteringOptions);
        }
        setToolBarBack(getString(R.string.credit_card_hub_toolbar_title));

        headerFragment = CreditCardHeaderFragment.newInstance();
        binding.collapsingAppbarView.addHeaderView(headerFragment);
        binding.collapsingAppbarView.setBackground(R.drawable.gradient_light_red_warm_red);
    }

    private void setupFilterOptions() {
        filteringOptions = new FilteringOptions();
        filteringOptions.setFilterType(ALL_TRANSACTIONS);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String fromDate = DateUtils.hyphenateDate(calendar.getTime());
        String toDate = DateUtils.hyphenateDate(Calendar.getInstance().getTime());
        filteringOptions.setFromDate(fromDate);
        filteringOptions.setToDate(toDate);
    }

    public void requestSecondaryCardData(LifeInsurance lifeInsurance) {
        this.lifeInsurance = lifeInsurance;
        SecondaryCardFetchMandateViewModel fetchMandateViewModel = new ViewModelProvider(this).get(SecondaryCardFetchMandateViewModel.class);
        fetchMandateViewModel.fetchSecondaryCardMandates(accountNumber);
        fetchMandateViewModel.secondaryCardMandateLiveData.observe(this, secondaryCardMandateResponse -> {
            this.secondaryCardMandateResponse = secondaryCardMandateResponse;
            setupTabView(lifeInsurance);
        });
        fetchMandateViewModel.getFailureLiveData().observe(this, responseHeader -> {
            secondaryCardMandateResponse = new GetSecondaryCardMandateResponse();
            setupTabView(lifeInsurance);
        });
    }

    public void setupTabView(@Nullable LifeInsurance lifeInsurance) {
        List<FragmentPagerItem> fragmentItems = new ArrayList<>();
        String transactionsDescription = getString(R.string.credit_card_hub_transactions_tab);
        String cardsDescription = getString(R.string.credit_card_hub_credit_card);
        String insureDescription = getString(R.string.credit_card_hub_insure_tab);
        String contactDescription = getString(R.string.credit_card_hub_contact_tab);
        String statementsDescription = getString(R.string.statements);

        riskBasedApproachViewModel.getPersonalInformationResponse().observe(this, personalInformationResponse -> {
            PersonalInformationResponse.CustomerInformation customerInformation = personalInformationResponse.getCustomerInformation();
            if (customerInformation != null) {
                transactionHistoryFragment = TransactionHistoryFragment.newInstance(transactionsDescription, CREDIT_CARD_HUB);
                fragmentItems.add(transactionHistoryFragment);
                addCreditCardManageTab(fragmentItems, cardsDescription);
                addCreditCardStatementsTab(fragmentItems, statementsDescription);
                boolean hasCreditCardAccountNo = creditCardResponse != null &&
                        creditCardResponse.getCreditCardAccount() != null &&
                        creditCardResponse.getCreditCardAccount().getAccountNo() != null;
                boolean isInsuranceActive = lifeInsurance != null;
                if (isInsuranceActive && hasCreditCardAccountNo) {
                    fragmentItems.add(CreditCardInsuranceFragment.newInstance(insureDescription, lifeInsurance, creditCardResponse.getCreditCardAccount().getAccountNo()));
                }
                fragmentItems.add(CreditCardContactUsFragment.newInstance(contactDescription));

                SparseArray<FragmentPagerItem> tabs = new SparseArray<>();
                for (int i = 0; i < fragmentItems.size(); i++) {
                    tabs.put(i, fragmentItems.get(i));
                }

                binding.collapsingAppbarView.setUpTabs(this, tabs);
            }

            attachSearchViewCallbacks();

            binding.collapsingAppbarView.setOnPageSelectionListener((description, position) -> {
                if (getString(R.string.credit_card_hub_transactions_tab).equalsIgnoreCase(description)) {
                    attachSearchViewCallbacks();
                } else {
                    detachSearchViewCallbacks();
                }
            });
            riskBasedApproachViewModel.getPersonalInformationResponse().removeObservers(this);
            creditCardInsurancePresenter.onViewCreated(accountNumber);
        });

        riskBasedApproachViewModel.fetchPersonalInformation();
    }

    private void addCreditCardManageTab(List<FragmentPagerItem> fragmentItems, String cardsDescription) {
        FeatureSwitching featureSwitching = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (creditCardResponse != null && creditCardResponse.getCreditCardAccount() != null) {
            if (secondaryCardMandateResponse == null) {
                secondaryCardMandateResponse = new GetSecondaryCardMandateResponse();
            }
            if (!isBusinessAccount() || (isBusinessAccount() && featureSwitching.getBusinessBankingManageCards() == FeatureSwitchingStates.ACTIVE.getKey())) {
                fragmentItems.add(ManageCardFragment.newInstance(creditCardResponse.buildManageObject(), secondaryCardMandateResponse, cardsDescription));
            }
        }
    }

    private void addCreditCardStatementsTab(List<FragmentPagerItem> fragmentItems, String statementsDescription) {
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        if (featureSwitchingToggles.getCreditCardArchivedStatements() == FeatureSwitchingStates.ACTIVE.getKey()) {
            fragmentItems.add(GenericStatementFragment.newInstance(statementsDescription, CREDIT_CARD_HUB));
        }
    }

    private void slideOfferUp(VCLParcelableModel dataModel) {
        if (!SharedPreferenceService.INSTANCE.isOfferHidden(creditCardResponse.getCardNumber() != null ? creditCardResponse.getCardNumber().replace(" ", "") : accountNumber.replace(" ", ""))) {
            binding.offerBannerView.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0, 0, binding.offerBannerView.getHeight(), 0);
            String amount = TextFormatUtils.formatBasicAmountAsRand(dataModel.getNewCreditLimitAmount());
            binding.offerBannerView.getApplyButton().setOnClickListener(view -> {
                AnalyticsUtil.INSTANCE.trackAction("VCL Apply clicked");
                FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
                if (featureSwitchingToggles.getCreditCardVCL() == FeatureSwitchingStates.DISABLED.getKey()) {
                    startActivity(IntentFactory.capabilityUnavailable(CreditCardHubActivity.this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_apply_for_vcl))));
                } else {
                    navigateToVclFlow(dataModel);
                    AnalyticsUtil.INSTANCE.trackAction("Credit Card", "Credit Card VCL from Hub Banner");
                }
            });
            binding.offerBannerView.getCloseFullOfferImageView().setOnClickListener(view -> binding.offerBannerView.showBanner());
            binding.offerBannerView.getHideButton().setOnClickListener(view -> {
                AnalyticsUtil.INSTANCE.trackAction("VCL Hide clicked");
                hideOfferBanner(creditCardResponse.getCardNumber());
            });

            binding.offerBannerView.getContentTextView().setText(getString(R.string.vcl_offer_increase_limit, amount));
            startFragment(VclInformationFragment.newInstance(), za.co.absa.presentation.uilib.R.id.offerContentFrameLayout, true, AnimationType.NONE, false, "");
            animation.setDuration(ANIMATION_DURATION_MILLIS);
            binding.offerBannerView.startAnimation(animation);
        }
    }

    private void navigateToVclFlow(VCLParcelableModel dataModel) {
        Intent intent = new Intent(CreditCardHubActivity.this, CreditCardVCLBaseActivity.class);
        intent.putExtra(CreditCardVCLBaseActivity.IS_FROM_CREDIT_CARD_HUB, true);
        intent.putExtra(CreditCardVCLBaseActivity.VCL_DATA, dataModel);
        startActivity(intent);
    }

    @Override
    public void updateTransactionsList(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void updateCreditCardInformation(CreditCardResponseObject successResponse) {
        creditCardResponse = successResponse;
        setNumberOfAccounts();
        headerFragment.setupFragment(successResponse);
        hasResumed = true;
        updateTransactionsList(successResponse.buildListOfTransactions());
        requestSecondaryCardData(successResponse.getLifeInsurance());
        updateVCLModel(successResponse.buildVCLParcelableModel());
    }

    @Override
    public void onSearchRequestCompleted(List<Transaction> transactions) {
        this.transactions = transactions;
        transactionHistoryFragment.invalidateAdapter();
        transactionHistoryFragment.updateTransaction(transactions, filteringOptions);
        dismissProgressDialog();
    }

    @Override
    public void updateVCLModel(VCLParcelableModel dataModel) {
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getCreditCardVCL() == FeatureSwitchingStates.ACTIVE.getKey() && dataModel != null && dataModel.getNewCreditLimitAmount() != null && Double.parseDouble(dataModel.getNewCreditLimitAmount()) > 0) {
            slideOfferUp(dataModel);
            CardCache.getInstance().storeVCLParcelableModel(dataModel);
        }
    }

    @NonNull
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setNumberOfAccounts() {
        int numberOfAccounts;
        if (getIntent().getSerializableExtra(HomeContainerActivity.NUMBER_OF_ACCOUNTS) != null) {
            numberOfAccounts = getIntent().getIntExtra(HomeContainerActivity.NUMBER_OF_ACCOUNTS, 0);
        } else {
            SecureHomePageObject secureHomePageObject = getAppCacheService().getSecureHomePageObject();
            numberOfAccounts = secureHomePageObject == null ? 0 : secureHomePageObject.getAccounts().size();
        }
        if (numberOfAccounts == 0) {
            AccountList accountListObject = AbsaCacheManager.getInstance().getCachedAccountListObject();
            numberOfAccounts = accountListObject == null ? 0 : accountListObject.getAccountsList().size();
        }
        headerFragment.setNumberOfAccounts(numberOfAccounts);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (creditCardResponse != null && creditCardResponse.getCreditCardAccount() != null) {
            if (hasResumed && headerFragment != null) {
                headerFragment.animateCreditCardInfoAgain();
            }
        }
    }

    public void showBottomBarMenu() {
        TransactionHistoryFilterFragment filterBottomDialogFragment = TransactionHistoryFilterFragment.newInstance(this, filteringOptions, "Credit card hub");
        filterBottomDialogFragment.show(getSupportFragmentManager(), "filter_dialog_fragment");
    }

    public void updateFilteringOptions(FilteringOptions filteringOption) {
        if (!Objects.requireNonNull(filteringOption.getFromDate()).equals(filteringOptions.getFromDate()) || !Objects.requireNonNull(filteringOption.getToDate()).equals(filteringOptions.getToDate())) {
            if (creditCardResponse != null && creditCardResponse.getCreditCardAccount() != null) {
                presenter.requestCreditCardInformation(creditCardResponse.getCreditCardAccount().getAccountNo(), filteringOption.getFromDate(), filteringOption.getToDate(), true);
            }

        } else {
            transactionHistoryFragment.updateFilter(filteringOption);
        }
        filteringOptions = filteringOption;
    }

    @NonNull
    public FilteringOptions getFilterOptions() {
        return filteringOptions;
    }

    public void collapseAppBar() {
        binding.collapsingAppbarView.collapseAppBar();
    }

    @Override
    public void showFailureScreen() {
        shouldShowInsuranceQuoteFailureMessage = true;
    }

    @Override
    public void showSuccessScreen(CreditProtection successResponse) {
        String dayOfDebit = DateUtils.getDateWithMonthNameFromStringWithoutHyphen(successResponse.getDayOfDebit());
        startActivity(IntentFactory.getSubmitCreditProtectionSuccessResultScreen(this, getString(R.string.credit_protection_successful_sub_message, DateUtils.removePeriod(dayOfDebit))));
    }

    @Override
    public void showSomethingWentWrongScreen() {
        shouldShowInsuranceQuoteFailureMessage = true;
        trackScreenView("Life_CCProt_ApplicationQuote", SITE_SECTION);
    }

    private void attachSearchViewCallbacks() {
        binding.collapsingAppbarView.setOnViewPropertiesChangeListener((state) -> {
            if (state == CollapsingAppBarView.State.EXPANDED) {
                if (transactionHistoryFragment != null) {
                    transactionHistoryFragment.showCalendarFilterBar();
                }
                binding.collapsingAppbarView.hideSearchView();
            }
        });

        binding.collapsingAppbarView.setOnSearchQueryListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionHistoryFragment.searchTransactionHistory(newText);
                return true;
            }
        });
    }

    private void detachSearchViewCallbacks() {
        if (transactionHistoryFragment != null) {
            transactionHistoryFragment.showCalendarFilterBar();
        }
        binding.collapsingAppbarView.hideSearchView();
        binding.collapsingAppbarView.setOnViewPropertiesChangeListener(null);
        binding.collapsingAppbarView.setOnSearchQueryListener(null);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void collapseAppBarView() {
        binding.collapsingAppbarView.showSearchView();
        binding.collapsingAppbarView.collapseAppBar();
    }

    @Override
    public void onBackPressed() {
        if (binding.offerBannerView.isBannerExpanded()) {
            binding.offerBannerView.backNavigation();
        } else {
            if (onBackPressedListener != null) {
                onBackPressedListener.doBack();
            } else {
                navigateToPreviousScreen();
            }
        }
    }

    public void navigateToPreviousScreen() {
        if (binding.collapsingAppbarView.getSearchView().getVisibility() == View.VISIBLE && !binding.collapsingAppbarView.getSearchView().isIconified()) {
            binding.collapsingAppbarView.getSearchView().setIconified(true);
            binding.collapsingAppbarView.expandAppBar();
            binding.collapsingAppbarView.hideSearchView();
            transactionHistoryFragment.showCalendarFilterBar();
        } else {
            finish();
        }
    }

    private void hideOfferBanner(String cardNumber) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, binding.offerBannerView.getHeight());
        animation.setDuration(ANIMATION_DURATION_MILLIS / 2);
        binding.offerBannerView.startAnimation(animation);
        if (cardNumber != null) {
            SharedPreferenceService.INSTANCE.setOfferHidden(cardNumber);
        }
        binding.offerBannerView.setVisibility(View.GONE);
    }

    @Override
    public boolean isFromCardHub() {
        return true;
    }

    @NotNull
    @Override
    public GenericTransactionHistoryViewModel statementViewModel() {
        if (genericTransactionHistoryViewModel == null) {
            genericTransactionHistoryViewModel = new ViewModelProvider(this).get(GenericTransactionHistoryViewModel.class);
        }
        return genericTransactionHistoryViewModel;
    }
}
