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

package com.barclays.absa.banking.home.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.avaf.ui.AvafConstants;
import com.barclays.absa.banking.avaf.ui.AvafHubActivity;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.Entry;
import com.barclays.absa.banking.boundary.model.Header;
import com.barclays.absa.banking.boundary.model.SecondaryCardItem;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.CardListActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.cluster.ui.InsuranceClusterActivity;
import com.barclays.absa.banking.cluster.ui.InvestmentClusterActivity;
import com.barclays.absa.banking.explore.ui.ExploreHubHostInterface;
import com.barclays.absa.banking.explore.ui.ExploreHubViewModel;
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment;
import com.barclays.absa.banking.express.accountBalances.dto.AccountBalanceResponse;
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.SessionManager;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.EmailUtil;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel;
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsActivity;
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanVCLViewModel;
import com.barclays.absa.banking.presentation.contactUs.ContactUsContracts;
import com.barclays.absa.banking.presentation.contactUs.ContactUsFragment;
import com.barclays.absa.banking.presentation.contactUs.EnterSecurityCodeFragment;
import com.barclays.absa.banking.presentation.contactUs.ReportFraudFragment;
import com.barclays.absa.banking.presentation.homeLoan.HomeLoanPerilsHubActivity;
import com.barclays.absa.banking.presentation.inAppNotifications.InAppNotificationMessageFragment;
import com.barclays.absa.banking.presentation.inAppNotifications.InAppNotificationSectionFragment;
import com.barclays.absa.banking.presentation.inAppNotifications.InAppNotificationViewModel;
import com.barclays.absa.banking.presentation.inAppNotifications.InAppSection;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.banking.presentation.verification.SureCheckAuth2faActivity;
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviouralRewardsActivity;
import com.barclays.absa.banking.settings.ui.customisation.CustomiseLoginActivity;
import com.barclays.absa.banking.settings.ui.customisation.CustomiseLoginOptionActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.banking.transfer.TransferFundsActivity;
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper;
import com.barclays.absa.banking.unitTrusts.ui.view.UnitTrustAccountView;
import com.barclays.absa.banking.unitTrusts.ui.view.UnitTrustAccountsPresenter;
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustBaseActivity;
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayActivity;
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccountBalanceUpdateHelper;
import com.barclays.absa.utils.AccountHelper;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.AppShortcutsHandler;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.IAppShortcutsHandler;
import com.barclays.absa.utils.ISharedPreferenceService;
import com.barclays.absa.utils.OperatorPermissionUtils;
import com.barclays.absa.utils.PasswordGenerator;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.imimobile.connect.core.IMIconnect;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import styleguide.screens.GenericResultScreenFragment;
import styleguide.screens.GenericResultScreenProperties;
import za.co.absa.networking.ExpressNetworkingConfig;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.barclays.absa.banking.avaf.ui.AvafConstants.BALANCE_PENDING;
import static com.barclays.absa.banking.framework.push.PushMessageListener.IN_APP_SHORTCUT;
import static com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity.ACCOUNT_OBJECT;

public class HomeContainerActivity extends BaseActivity implements
        UnitTrustAccountView, HomeContainerView, ContactUsContracts.ContactUsView, ContactUsContracts.ReportFraudView, ExploreHubHostInterface {

    public static final int FUNERAL_COVER_MAXIMUM_DISPLAY_LIMIT = 0;
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String NUMBER_OF_ACCOUNTS = "NUMBER_OF_ACCOUNTS";
    public static final String DISPLAY_ACCOUNTS_LIST = "displayAccountsList";
    public static final String SELECT_HOME_ICON = "selectHomeIcon";
    public static final String CIA_ACCOUNT_LIST = "ciaAccountList";
    public static final String INSURANCE_CLUSTER = "insuranceCluster";
    public static final String INVESTMENT_CLUSTER = "investmentCluster";
    private static final int HEADING_TRANSITION_DURATION_MILLISECONDS = 500;
    private static final int SECONDARY_CARD_BIT = 32;
    private final List<Entry> entries = new ArrayList<>();
    private final List<Entry> filteredEntries = new ArrayList<>();
    private final List<Entry> filteredCiaEntries = new ArrayList<>();
    boolean isHeadingDarkStyle;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private SecureHomePageObject secureHomePageObject = null;
    private ProfileManager profileManager = ProfileManager.getInstance();
    private UserProfile currentUserProfile;
    private List<AccountObject> accounts;
    private HomeContainerPresenter presenter;
    private UnitTrustAccountsPresenter unitTrustPresenter;
    private boolean isHomeLoanPerilsSelected = false;
    private MenuItem logoutMenuItem;
    private Toolbar toolbar;
    private HeadingFadeType headingFadeType = HeadingFadeType.None;
    private InAppNotificationViewModel inAppNotificationViewModel;
    private String callMeBackUniqueReferenceNumber;
    private boolean isDarkHeading;
    private String secretCode;
    private int selectedNavigationBar = -1;
    private List<Policy> policies = new ArrayList<>();
    private boolean isExploreHubDisabled;
    private String currentShortcutId;
    private boolean bottomNavigationListenerHasBeenSetup;
    private PersonalLoanVCLViewModel personalLoanVCLViewModel;
    private FeatureSwitching featureSwitchingToggles;
    private ColorStateList colorStateList;
    private View notificationBadge;
    private AccountsFragment accountsFragment;
    private ExploreHubViewModel exploreHubViewModel;
    public boolean isFromInAppPushNotification;
    private boolean isDirectTransferNavRequest = false;
    private boolean isDirectPaymentNavRequest = false;
    private IAppShortcutsHandler appShortcutsHandler = AppShortcutsHandler.INSTANCE;
    private final IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);
    private final ISharedPreferenceService sharedPreferenceService = SharedPreferenceService.INSTANCE;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_container_activity_menu, menu);
        logoutMenuItem = menu.findItem(R.id.logout_menu_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_menu_item) {
            logoutDialogInterface();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateCustomiseLoginOptionActivity() {
        startActivity(new Intent(this, CustomiseLoginOptionActivity.class));
        hideToolBar();
    }

    @Override
    public void onUnitTrustCardClicked(AccountObject accountObject) {
        if (BuildConfig.TOGGLE_DEF_UNIT_TRUST) {
            if (featureSwitchingToggles.getViewUnitTrusts() == FeatureSwitchingStates.DISABLED.getKey()) {
                startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_unit_trust))));
            } else {
                if (accountObject != null) {
                    homeCacheService.setUnitTrustAccountObject(accountObject);
                }
                unitTrustPresenter.onUnitTrustAccountsHubCreated();
            }
        }
    }

    private void setToolbar(AppCompatActivity activity) {
        setToolBarBack("", v -> onBackPressed());
        ActionBar actionBar = activity.getSupportActionBar();
        toolbar = findViewById(R.id.toolbar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setTopContainerVisibility(int visibility) {
        findViewById(R.id.toolbar).setVisibility(visibility);
    }

    private void logoutDialogInterface() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.home_page_logout_option))
                .message(getString(R.string.logout_dialog))
                .positiveButton(getString(R.string.home_page_logout_option))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener((dialog, which) -> {
                    BMBApplication.getInstance().is_RVN_checked = false;
                    logoutAndGoToStartScreen();
                    trackLogoutPopUpYes(mScreenName);
                    getAppCacheService().setPrimarySecondFactorDevice(false);
                    if (!isFinishing()) {
                        dialog.dismiss();
                    }
                })
                .negativeDismissListener((dialog, which) -> {
                    trackLogoutPopUpNo(mScreenName);
                    BaseAlertDialog.INSTANCE.dismissAlertDialog();
                })
                .build());
    }

    private void initializeUserProfile() {
        if (profileManager.getActiveUserProfile() == null) {
            profileManager.loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
                @Override
                public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                    UserProfile currentUserProfileSavedInstance = sharedPreferenceService.getCurrentUserProfileSavedInstance();
                    if (currentUserProfileSavedInstance != null) {
                        currentUserProfile = currentUserProfileSavedInstance;
                        profileManager.setActiveUserProfile(currentUserProfileSavedInstance);
                    }
                }

                @Override
                public void onProfilesLoadFailed() {

                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(DISPLAY_ACCOUNTS_LIST, false)) {
            bottomNavigationView.setSelectedItemId(R.id.home_menu_item);
            showAccountsList();
        } else if (intent.getBooleanExtra(SELECT_HOME_ICON, false)) {
            bottomNavigationView.setSelectedItemId(R.id.home_menu_item);
        }
    }

    private void showAccountsList() {
        if (!AccountsFragment.class.getName().equals(getCurrentFragmentName())) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExpressNetworkingConfig.applicationLocale = BMBApplication.getApplicationLocale();
        ExpressNetworkingConfig.INSTANCE.setLoggedIn(true);
        if (!getAppCacheService().isPrimarySecondFactorDevice()) {
            BMBApplication.getInstance().stopListeningForAuth();
        }
        setContentView(R.layout.home_container_activity);
        if (isOperator()) {
            isExploreHubDisabled = true;
        }

        getAppCacheService().clearAllIdentificationAndVerificationCacheValues();

        inAppNotificationViewModel = new ViewModelProvider(this).get(InAppNotificationViewModel.class);
        if (getIntent().hasExtra("customTags")) {
            inAppNotificationViewModel.setCustomTags(getIntent().getParcelableExtra("customTags"));
        }
        setToolbar(this);
        setupInAppObservers();
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        int[][] stateArray = new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}};
        int[] colorArray = new int[]{ContextCompat.getColor(this, R.color.smile), ContextCompat.getColor(this, R.color.bottom_navigation_icon_color)};
        colorStateList = new ColorStateList(stateArray, colorArray);
        bottomNavigationView.setItemIconTintList(colorStateList);

        if (BMBApplication.getInstance().hasPendingAuth()) {
            showAuthScreen();
        }
        showProgressDialog();

        setupSession();
        CommonUtils.isOddActivityTransition = false;
        CommonUtils.setManageBeneficiaryPage(false);
        performAnalytics();
        checkSecureHomePageObjectExists();
        presenter = new HomeContainerPresenter(this);
        unitTrustPresenter = new UnitTrustAccountsPresenter(new WeakReference<UnitTrustAccountView>(this));
        featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        exploreHubViewModel = new ViewModelProvider(this).get(ExploreHubViewModel.class);

        if (getIntent().hasExtra("shortcut")) {
            currentShortcutId = getIntent().getStringExtra("shortcut");
        }
    }

    private void setupInAppObservers() {
        inAppNotificationViewModel.getRegistrationComplete().observe(this, profileSetupComplete -> {
            inAppNotificationViewModel.buildNotificationSectionList();
        });

        inAppNotificationViewModel.getInAppMessageThreads().observe(this, inAppSections -> {
            updateInAppMessages(inAppSections);
            if (!BuildConfig.STUB) {
                addBadgeView(inAppNotificationViewModel.hasUnreadMessages());
            }
        });

    }

    private void showAuthScreen() {
        Intent intent = new Intent(this, SureCheckAuth2faActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles().getImiConnect() == FeatureSwitchingStates.ACTIVE.getKey() && inAppNotificationViewModel != null) {
            if (getAppCacheService().isImiSessionActive()) {
                inAppNotificationViewModel.buildNotificationSectionList();
            } else {
                inAppNotificationViewModel.registerForInAppMessages();
            }
        }
    }

    private void initializePolicies() {
        List<Policy> usablePolicies = new ArrayList<>();
        List<Policy> policyList = homeCacheService.getInsurancePolicies();

        if (!policyList.isEmpty()) {
            for (Policy policy : policyList) {
                if (RebuildUtils.isPolicySupported(policy, this)) {
                    usablePolicies.add(policy);
                }
            }

            if (!usablePolicies.isEmpty()) {
                policies.clear();
                policies.addAll(usablePolicies);
            }
        }
    }

    private void performAnalytics() {
        mScreenName = BMBConstants.HOME_CONST;
        mSiteSection = BMBConstants.BANKING_HOME_CONST;
        // TODO: This is Firebase Analytics not sure we should use it anymore
        trackUserLogin();
        trackCustomScreenView(BMBConstants.HOME_CONST, BMBConstants.BANKING_HOME_CONST, BMBConstants.TRUE_CONST);

        AnalyticsUtil.INSTANCE.trackAction(BMBConstants.HOME_CONST, "Home Screen");
    }

    private void setupSession() {
        getAppCacheService().setLinkingFlow(false);
        initializeUserProfile();
        if (getAppCacheService().getSecureHomePageObject() != null) {
            secureHomePageObject = getAppCacheService().getSecureHomePageObject();
        }

        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        if (customerProfileObject.getCustomerType() == null && secureHomePageObject != null) {
            customerProfileObject = secureHomePageObject.getCustomerProfile();
        }

        if (currentUserProfile != null) {
            profileManager.updateProfile(currentUserProfile, null);
        }
        final BMBApplication app = BMBApplication.getInstance();
        app.setUserLoggedInStatus(true);

        if (!SessionManager.isSessionStarted()) {
            SessionManager.startSession();
        }

        // Check for customer type and save to the preferences.
        try {
            BMBApplication.getInstance().updateClientType(customerProfileObject.getClientTypeGroup());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showToolBar();
        if (!bottomNavigationListenerHasBeenSetup) {
            setBottomNavigationListener();
        }
        initializePolicies();
        if (secureHomePageObject == null) {
            secureHomePageObject = getAppCacheService().getSecureHomePageObject();
        }
        refreshMenuItemTitle();
        setBottomNavigationViewMenus();

        if (secureHomePageObject != null) {
            setupAccountsList();
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> logoutAndGoToStartScreen());
        }

        if (BMBApplication.getInstance().getUserLoggedInStatus()) {
            CommonUtils.setManageBeneficiaryPage(false);
        }

        setMenuTextAppearance(this.isDarkHeading);

        if (selectedNavigationBar == -1) {
            bottomNavigationView.setSelectedItemId(R.id.home_menu_item);
        }

        dismissProgressIndicator();

        boolean customiseLoginEnabled = !CustomiseLoginActivity.Companion.isCustomisedLoginDisabled();
        if (currentShortcutId != null) {
            navigateToCurrentShortcutActivity(currentShortcutId);
            getIntent().removeExtra("shortcutId");
            currentShortcutId = null;
        } else if (customiseLoginEnabled && appShortcutsHandler.isEmptyCachedUserShortcutList()) {
            navigateCustomiseLoginOptionActivity();
        } else {
            if (customiseLoginEnabled) {
                appShortcutsHandler.getUpgradedCachedUserShortcutList(this);
            }
            updateAccounts();
        }
    }

    private void updateAccounts() {
        if (rewardsCacheService.hasRewards() && !rewardsCacheService.getExpressRewardsDetails().getRewardsAccountBalanceSet()) {
            new AccountBalanceUpdateHelper(this).updateAccountBalance(rewardsCacheService.getExpressRewardsDetails().getRewardsMembershipNumber(), false, new AccountBalanceUpdateHelper.BalanceUpdateCallBack() {
                @Override
                public void updateSuccessful(@NotNull AccountBalanceResponse balanceUpdated) {
                    setupAccountsList();
                    accountsFragment.updateAccountCards();
                    fetchAvafBalances();
                }

                @Override
                public void updateFailed() {
                    fetchAvafBalances();
                }
            });
        } else {
            fetchAvafBalances();
        }
    }

    private void navigateToCurrentShortcutActivity(String currentShortcutId) {
        getIntent().removeExtra("shortcut");
        // Outside app is english only
        Context englishOnlyContext = getApplication();
        if (currentShortcutId.equalsIgnoreCase(englishOnlyContext.getString(R.string.pay)) || currentShortcutId.equalsIgnoreCase(getString(R.string.pay))) {
            isDirectPaymentNavRequest = true;
            updateAccounts();
        }
        if (currentShortcutId.equalsIgnoreCase(englishOnlyContext.getString(R.string.transfer)) || currentShortcutId.equalsIgnoreCase(getString(R.string.transfer))) {
            isDirectTransferNavRequest = true;
            updateAccounts();

        }
        if (currentShortcutId.equalsIgnoreCase(englishOnlyContext.getString(R.string.buy_electricity)) || currentShortcutId.equalsIgnoreCase(getString(R.string.buy_electricity))) {
            startActivity(new Intent(this, PrepaidElectricityActivity.class));
        }
        if (currentShortcutId.equalsIgnoreCase(englishOnlyContext.getString(R.string.stop_card_heading)) || currentShortcutId.equalsIgnoreCase(getString(R.string.stop_card_heading))) {
            startActivity(new Intent(this, CardListActivity.class));
        }
        if (currentShortcutId.equalsIgnoreCase(englishOnlyContext.getString(R.string.fixed_deposit_fixed_deposit)) || currentShortcutId.equalsIgnoreCase(getString(R.string.fixed_deposit_fixed_deposit))) {
            startActivity(new Intent(this, FixedDepositActivity.class));
        }
        if (currentShortcutId.equalsIgnoreCase(IN_APP_SHORTCUT)) {
            isFromInAppPushNotification = true;
        }
        if (currentShortcutId.equalsIgnoreCase(englishOnlyContext.getString(R.string.scan_to_pay_shortcut_name)) || currentShortcutId.equalsIgnoreCase(getString(R.string.scan_to_pay_shortcut_name))) {
            if (ScanToPayViewModel.Companion.scanToPayGone() || ScanToPayViewModel.Companion.scanToPayDisabled()) {
                if (!getAppCacheService().isPrimarySecondFactorDevice()) {
                    startActivity(IntentFactory.featureUnavailable(this, R.string.feature_unavailable, getString(R.string.scan_to_pay_primary_device_only_message)));
                } else {
                    startActivity(IntentFactory.featureUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.scan_to_pay))));
                }
            } else {
                startActivity(new Intent(this, ScanToPayActivity.class));
            }
        }
    }

    private void setupAccountsList() {
        accounts = null;
        AccountList cachedAccountsList = AbsaCacheManager.getInstance().getAccountsList();
        if (cachedAccountsList != null) {
            accounts = cachedAccountsList.getAccountsList();
        }
        if (accounts == null && secureHomePageObject != null && !secureHomePageObject.getAccounts().isEmpty()) {
            accounts = secureHomePageObject.getAccounts();
        } else if (AbsaCacheManager.getInstance().getCachedAccountListObject() != null) {
            accounts = AbsaCacheManager.getInstance().getCachedAccountListObject().getAccountsList();
        }
        // accounts
        if (accounts != null && !accounts.isEmpty()) {
            entries.clear();
            entries.addAll(accounts);
        }

        //secondary card
        if ((CustomerProfileObject.getInstance().getSecondaryCardAccessBits() & SECONDARY_CARD_BIT) == SECONDARY_CARD_BIT) {
            entries.add(new SecondaryCardItem());
        }
        if (accountsFragment != null) {
            accountsFragment.updateAccountCards();
        }
        setFilteredAccounts();
    }

    private void checkSecureHomePageObjectExists() {
        if (secureHomePageObject == null) {
            toastLong(R.string.sessionExpiredMessage);
            logoutAndGoToStartScreen();
        }
    }

    @Override
    public boolean isShowFuneralCoverOffer() {
        return !homeCacheService.hasAuthorizations() && shouldShowFuneralCoverOffer();
    }

    private boolean shouldShowFuneralCoverOffer() {
        boolean hasFuneralCover = homeCacheService.hasFuneralCover();
        BMBLogger.d("x-d", "hasFuneralCover " + hasFuneralCover);
        return !isBusinessAccount() && !isOperator() && !hasFuneralCover && sharedPreferenceService.getFuneralCoverDisplayCount() < FUNERAL_COVER_MAXIMUM_DISPLAY_LIMIT;
    }

    @Override
    public void requestHomeloanAccountHistory(AccountObject accountObject) {
        if (!CommonUtils.isAccountSupported(accountObject)) {
            return;
        }
        presenter.onHomeLoanAccountCardClicked(accountObject);
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getVisibility() == GONE) {
            bottomNavigationView.setVisibility(VISIBLE);
        }
        if (fragment instanceof InAppNotificationMessageFragment) {
            getSupportFragmentManager().popBackStack();
        } else if (!AccountsFragment.class.getName().equals(getCurrentFragmentName())) {
            bottomNavigationView.setSelectedItemId(R.id.home_menu_item);
        } else {
            this.moveTaskToBack(true);
        }
    }

    @Override
    public boolean isCallMeBackSessionAvailable() {
        return secretCode != null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUserProfile != null) {
            sharedPreferenceService.setCurrentUserProfileSavedInstance(currentUserProfile);
        }
    }

    @Override
    public void navigateToAccountInformation(AccountObject accountObject) {
        if (!OperatorPermissionUtils.canViewTransactions(accountObject)) {
            BaseAlertDialog.INSTANCE.showRequestAccessAlertDialog(getString(R.string.account_summary));
            return;
        }

        if (!CommonUtils.isAccountSupported(accountObject)) {
            return;
        }
        if ("cia".equalsIgnoreCase(accountObject.getAccountType()) && featureSwitchingToggles.getCurrencyInvestmentHub() == FeatureSwitchingStates.DISABLED.getKey()) {
            startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_currency_investment_accounts))));
        } else if (AccountTypes.PERSONAL_LOAN.equalsIgnoreCase(accountObject.getAccountType())) {
            if (featureSwitchingToggles.getPersonalLoanHub() == FeatureSwitchingStates.DISABLED.getKey()) {
                startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_apply_personal_loan_hub))));
            } else {
                personalLoanVCLViewModel = new ViewModelProvider(this).get(PersonalLoanVCLViewModel.class);
                personalLoanVCLViewModel.setPersonalLoanHubExtendedResponse(new MutableLiveData<>());
                personalLoanVCLViewModel.fetchPersonalLoanInformation(accountObject.getAccountNumber());
                personalLoanVCLViewModel.getPersonalLoanHubExtendedResponse().observe(this, personalLoanHubInformation -> {
                    personalLoanVCLViewModel.getPersonalLoanHubExtendedResponse().removeObservers(HomeContainerActivity.this);
                    dismissProgressIndicator();
                    startActivity(IntentFactory.getPersonalLoanHubActivity(this, accountObject, personalLoanHubInformation));
                });
            }
        } else if (AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE.equalsIgnoreCase(accountObject.getAccountType())) {
            if (!BuildConfig.TOGGLE_DEF_AVAF_HUB_ENABLED || featureSwitchingToggles.getVehicleFinanceHub() == FeatureSwitchingStates.DISABLED.getKey()) {
                startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))));
            } else if (BALANCE_PENDING.equalsIgnoreCase(accountObject.getCurrentBalance().getAmount())) {
                verifyAvafBalanceAndNavigate(accountObject);
            } else {
                startActivity(new Intent(this, AvafHubActivity.class).putExtra(ACCOUNT_OBJECT, accountObject));
            }
        } else {
            startActivity(IntentFactory.getAccountActivity(this, accountObject));
        }
    }

    private void handleDirectTransferRequest() {
        if (numberOfNoneWimiAccounts() <= 1) {
            BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                    .message(getString(R.string.transfer_unavailable))
                    .build());
        } else {
            startActivity(new Intent(this, TransferFundsActivity.class));
        }
    }

    private void fetchAvafBalances() {
        List<AccountObject> pendingAccounts = new ArrayList<>();
        for (AccountObject account : AbsaCacheManager.getInstance().getAccountsList().getAccountsList()) {
            if (BALANCE_PENDING.equalsIgnoreCase(account.getCurrentBalance().getAmount())) {
                pendingAccounts.add(account);
            }
        }

        verifyAvafBalances(pendingAccounts);
    }

    private void verifyAvafBalances(final List<AccountObject> pendingAccounts) {
        if (pendingAccounts.isEmpty()) {
            if (isDirectTransferNavRequest) {
                isDirectTransferNavRequest = false;
                handleDirectTransferRequest();
            } else if (isDirectPaymentNavRequest) {
                isDirectPaymentNavRequest = false;
                startActivity(new Intent(this, PaymentsActivity.class));
            }
            return;
        }

        AccountObject accountObject = pendingAccounts.get(0);
        AccountBalanceUpdateHelper accountBalanceUpdateHelper = new AccountBalanceUpdateHelper(this);
        accountBalanceUpdateHelper.setMaxRetries(AvafConstants.DEFAULT_BALANCE_RETRY_MAX);
        accountBalanceUpdateHelper.updateAvafAccountBalance(accountObject.getAccountNumber(), false, false, new AccountBalanceUpdateHelper.BalanceUpdateCallBack() {
            @Override
            public void updateSuccessful(@NotNull AccountBalanceResponse balanceUpdated) {
                pendingAccounts.remove(accountObject);
                setupAccountsList();
                accountsFragment.updateAccountCards();
                verifyAvafBalances(pendingAccounts);
            }

            @Override
            public void updateFailed() {
                pendingAccounts.remove(accountObject);
                setupAccountsList();
                accountsFragment.updateAccountCards();
                verifyAvafBalances(pendingAccounts);
            }
        });
    }

    private void verifyAvafBalanceAndNavigate(AccountObject accountObject) {
        AccountBalanceUpdateHelper accountBalanceUpdateHelper = new AccountBalanceUpdateHelper(this);
        accountBalanceUpdateHelper.setMaxRetries(AvafConstants.DEFAULT_BALANCE_RETRY_MAX);
        accountBalanceUpdateHelper.updateAvafAccountBalance(accountObject.getAccountNumber(), true, true, new AccountBalanceUpdateHelper.BalanceUpdateCallBack() {
            @Override
            public void updateSuccessful(@NotNull AccountBalanceResponse balanceUpdated) {
                AccountObject updatedAccountObject = AccountHelper.INSTANCE.getAccountObjectForAccountNumber(accountObject.getAccountNumber());
                startActivity(new Intent(HomeContainerActivity.this, AvafHubActivity.class).putExtra(ACCOUNT_OBJECT, updatedAccountObject));
            }

            @Override
            public void updateFailed() {
                startActivity(IntentFactory.capabilityUnavailable(HomeContainerActivity.this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))));
            }
        });
    }

    @Override
    public void navigateToMultipleCiaAccountInformation() {
        if (!BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS || (featureSwitchingToggles.getCurrencyInvestmentHub() != FeatureSwitchingStates.ACTIVE.getKey())) {
            startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_currency_investment_accounts))));
        } else {
            Intent pickCiaAccountIntent = new Intent(this, SelectCiaAccountActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(CIA_ACCOUNT_LIST, (Serializable) getCiaAccounts());
            pickCiaAccountIntent.putExtra(CIA_ACCOUNT_LIST, bundle);
            startActivity(pickCiaAccountIntent);
        }
    }

    @Override
    public void navigateToFixedDepositHub(AccountObject accountObject) {
        AnalyticsUtil.INSTANCE.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "FixedTermDeposit_HomeScreen_DashboardFixedTermDepositAccountCardClicked");
        startActivity(IntentFactory.getFixedDepositHubActivity(this, accountObject));
    }

    @Override
    public void onAdvantageCardClicked() {
        AnalyticsUtil.INSTANCE.trackAction("Behavioural Rewards", "BehaviouralRewardsNonAbsa_HomeScreen_RewardsCardClicked");
        Intent intent = new Intent(HomeContainerActivity.this, BehaviouralRewardsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onInsuranceClusterClicked(AccountObject accountObject) {
        startActivity(new Intent(HomeContainerActivity.this, InsuranceClusterActivity.class));
    }

    @Override
    public void onInvestmentClusterClicked(AccountObject accountObject) {
        startActivity(new Intent(HomeContainerActivity.this, InvestmentClusterActivity.class).addFlags(FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onAbsaRewardsCardClicked() {
        AnalyticsUtil.INSTANCE.trackAction("Behavioural Rewards", "BehaviouralRewards_HomeScreen_RewardsCardClicked");

        if (featureSwitchingToggles.getAbsaRewards() == FeatureSwitchingStates.DISABLED.getKey()) {
            startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_rewards))));
        } else {
            if (rewardsCacheService.getExpressRewardsDetails().getRewardsAccountBalanceSet()) {
                Intent intent = new Intent(HomeContainerActivity.this, BehaviouralRewardsActivity.class);
                startActivity(intent);
            } else {
                new AccountBalanceUpdateHelper(this).updateAccountBalance(rewardsCacheService.getExpressRewardsDetails().getRewardsMembershipNumber(), true, new AccountBalanceUpdateHelper.BalanceUpdateCallBack() {
                    @Override
                    public void updateSuccessful(@NotNull AccountBalanceResponse balanceUpdated) {
                        dismissProgressDialog();
                        startActivity(new Intent(HomeContainerActivity.this, BehaviouralRewardsActivity.class));
                    }

                    @Override
                    public void updateFailed() {
                        dismissProgressDialog();
                        startActivity(IntentFactory.capabilityUnavailable(HomeContainerActivity.this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_rewards))));
                    }
                });
            }
        }
    }

    @Override
    public void dismissProgressIndicator() {
        trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
        dismissProgressDialog();
    }

    @Override
    public void showErrorDialog(String errorContent) {
        showMessageError(errorContent);
    }

    @Override
    public void onCreditCardInformationFetched(AccountObject accountObject) {
        Intent creditCardHubIntent = new Intent(this, CreditCardHubActivity.class);
        creditCardHubIntent.putExtra(ACCOUNT_OBJECT, accountObject);
        creditCardHubIntent.putExtra(CreditCardHubActivity.ACCOUNT_NUMBER, accountObject.getAccountNumber());
        creditCardHubIntent.putExtra(NUMBER_OF_ACCOUNTS, accounts != null ? accounts.size() : 0);
        creditCardHubIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(creditCardHubIntent);
    }

    @Override
    public List<Entry> getAccounts() {
        return filteredEntries;
    }

    @Override
    public List<Entry> getCiaAccounts() {
        return filteredCiaEntries;
    }

    private void setFilteredAccounts() {
        filteredEntries.clear();
        boolean hasPolicy = false;
        filteredCiaEntries.clear();

        for (Entry entry : entries) {
            switch (entry.getEntryType()) {
                case Entry.HEADER:
                case Entry.OFFERS:
                    break;
                case Entry.ACCOUNT: {
                    if (((AccountObject) entry).getAccountType().equals("cia")) {
                        filteredCiaEntries.add(entry);
                    } else {
                        filteredEntries.add(entry);
                    }
                }
                break;
                case Entry.SECONDARY_CARD: {
                    filteredEntries.add(new SecondaryCardItem());
                    break;
                }
                case Entry.POLICY: {
                    hasPolicy = true;
                    filteredEntries.add(entry);
                }
            }
        }

        if (hasPolicy) {
            for (int i = 0; i < filteredEntries.size(); i++) {
                Entry entry = filteredEntries.get(i);
                if (entry.getEntryType() == Entry.POLICY) {
                    if (filteredCiaEntries.size() > 0) {
                        if (!BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS || (featureSwitchingToggles.getCurrencyInvestmentHub() != FeatureSwitchingStates.ACTIVE.getKey())) {
                            filteredEntries.addAll(i, filteredCiaEntries);
                            i = i + filteredCiaEntries.size();
                        } else {
                            filteredEntries.add(i, filteredCiaEntries.get(0));
                            i++;
                        }
                    }
                    filteredEntries.add(i, new Header(getString(R.string.insurance)));
                    break;
                }
            }
        } else if (filteredCiaEntries.size() > 0) {
            if (!BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS || (featureSwitchingToggles.getCurrencyInvestmentHub() != FeatureSwitchingStates.ACTIVE.getKey())) {
                filteredEntries.addAll(filteredCiaEntries);
            } else {
                filteredEntries.add(filteredCiaEntries.get(0));
            }
        }

        homeCacheService.setFilteredAccounts(filteredEntries);

        if (!homeCacheService.getInsurancePolicies().isEmpty()) {
            AccountObject insuranceClusterAccountObject = new AccountObject();
            insuranceClusterAccountObject.setAccountType(INSURANCE_CLUSTER);
            homeCacheService.getFilteredHomeAccounts().add(insuranceClusterAccountObject);
        }
    }

    @Override
    public void onPolicyCardInformation(Policy policy) {
        isHomeLoanPerilsSelected = false;
        presenter.fetchPolicyInformation(policy);
    }

    public void updateInAppMessages(List<InAppSection> messages) {
        if (fragment != null && messages != null) {
            if (fragment instanceof InAppNotificationSectionFragment) {
                isFromInAppPushNotification = false;
            } else if (inAppNotificationViewModel.getCustomTags().getMailboxId().equals(ProfileManager.getInstance().getActiveUserProfile().getMailboxId()) && isFromInAppPushNotification && getAppCacheService().isImiProfileRegistered()) {
                bottomNavigationView.setSelectedItemId(R.id.messages_menu_item);
            }
        }
    }

    private void setBottomNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            return navigateToSelectedNavigationItem(id);
        });
        bottomNavigationListenerHasBeenSetup = true;
    }

    private boolean navigateToSelectedNavigationItem(int id) {
        selectedNavigationBar = id;
        isHeadingDarkStyle = true;
        boolean isAllowedToReplaceFragment = true;
        switch (id) {
            case R.id.home_menu_item:
                if (isCurrentFragmentOfType(fragment, AccountsFragment.class)) {
                    isAllowedToReplaceFragment = false;
                } else {
                    setTopContainerVisibility(VISIBLE);
                    accountsFragment = AccountsFragment.newInstance();
                    fragment = accountsFragment;
                }
                break;
            case R.id.explore_menu_item:
                if (isCurrentFragmentOfType(fragment, NewExploreHubFragment.class)) {
                    isAllowedToReplaceFragment = false;
                } else {
                    setTopContainerVisibility(VISIBLE);
                    fragment = NewExploreHubFragment.newInstance();
                }
                break;
            case R.id.messages_menu_item:
                if (!BuildConfigHelper.STUB && (featureSwitchingToggles.getInAppMailbox() == FeatureSwitchingStates.DISABLED.getKey()
                        || getAppCacheService().getSecureHomePageObject() == null
                        || TextUtils.isEmpty(getAppCacheService().getSecureHomePageObject().getCustomerProfile().getMailboxProfileId()))) {
                    startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_in_app_messages)), true));
                    return false;
                } else if (getAppCacheService().isImiProfileRegistered()) {
                    if (isCurrentFragmentOfType(fragment, InAppNotificationSectionFragment.class)) {
                        isAllowedToReplaceFragment = false;
                    } else {
                        setTopContainerVisibility(GONE);
                        fragment = InAppNotificationSectionFragment.newInstance(isFromInAppPushNotification);
                        isFromInAppPushNotification = false;
                    }
                } else {
                    toastLong(R.string.in_app_messages_loading);
                    return false;
                }
                break;
            case R.id.more_menu_item:
                if (isCurrentFragmentOfType(fragment, MenuFragment.class)) {
                    isAllowedToReplaceFragment = false;
                } else {
                    findViewById(R.id.content_frame_layout).setAlpha(0);
                    fragment = MenuFragment.Companion.newInstance();
                    setTopContainerVisibility(VISIBLE);
                }
                break;
            case R.id.contact_us_menu_item:
                isHeadingDarkStyle = false;
                if (isCurrentFragmentOfType(fragment, ContactUsFragment.class)) {
                    isAllowedToReplaceFragment = false;
                } else {
                    setTopContainerVisibility(VISIBLE);
                    fragment = new ContactUsFragment();
                }
                break;
        }

        if (isCurrentFragmentOfType(fragment, ContactUsFragment.class) || isCurrentFragmentOfType(fragment, InAppNotificationSectionFragment.class) || isCurrentFragmentOfType(fragment, AccountsFragment.class)) {
            setHeadingStyle(fragment, isHeadingDarkStyle);
        } else {
            setHeadingStyle(fragment, isHeadingDarkStyle);
            bottomNavigationView.setVisibility(VISIBLE);
        }

        if (isAllowedToReplaceFragment && fragment != null) {
            startContentFragment(fragment);
            return true;
        }
        return false;
    }

    public void startContentFragment(Fragment fragment) {
        startFragment(fragment, R.id.content_frame_layout, true, AnimationType.NONE, true, fragment.getClass().getName());
        if (findViewById(R.id.content_frame_layout).getAlpha() == 0f) {
            findViewById(R.id.content_frame_layout).animate().alpha(1).setDuration(500);
        }
    }

    private void showActionBarForContactUs() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.contact_absa));
        }
    }

    @Override
    public void requestCall() {
        secretCode = generateSecretCode();
        String callBackDateTime = "NOW";
        presenter.requestCallBack(secretCode, callBackDateTime);
    }

    private String generateSecretCode() {
        PasswordGenerator passwordBuilder = new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true)
                .build();

        return passwordBuilder.generate(6);
    }

    @Override
    public void verifyCallBack(@NotNull String userSecretCode) {
        CallBackVerificationDataModel callBackVerificationDataModel = new CallBackVerificationDataModel();
        callBackVerificationDataModel.setSecretCode(userSecretCode);
        callBackVerificationDataModel.setSecretCodeVerified(true);
        callBackVerificationDataModel.setUniqueRefNo(callMeBackUniqueReferenceNumber);
        presenter.requestVerificationCallBack(callBackVerificationDataModel);
    }

    @Override
    public void showConfirmCallDialog() {
        String message;
        if (CustomerProfileObject.getInstance().getCellNumber() != null) {
            message = getString(R.string.cellphone_number_ending, secureHomePageObject.getCustomerProfile().getCellNumber());
        } else {
            message = getString(R.string.cellphone_number_ending_no_number);
        }
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(message)
                .positiveButton(getString(R.string.ok))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener((dialog, which) -> requestCall())
                .build());
    }

    @Override
    public void clearSecretCode() {
        secretCode = null;
    }

    @Override
    public void showMaxAttemptsDialog(int attemptCount, final DialogInterface.OnClickListener listener) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(getString(R.string.contact_us_attempts, attemptCount))
                .positiveDismissListener(listener != null ? listener : (dialog, which) -> BaseAlertDialog.INSTANCE.dismissAlertDialog())
                .build());
    }

    @Override
    public void navigateToReportFraudFragment() {
        showActionBarForContactUs();
        ReportFraudFragment reportFraudFragment = new ReportFraudFragment();
        bottomNavigationView.setVisibility(VISIBLE);
        setHeadingStyle(reportFraudFragment, true);
        startContentFragment(reportFraudFragment);
    }

    @Override
    public void navigateToContactUsFragment() {
        showActionBarForContactUs();
        ContactUsFragment contactUsFragment = new ContactUsFragment();
        bottomNavigationView.setVisibility(GONE);
        setHeadingStyle(contactUsFragment, false);
        startContentFragment(contactUsFragment);
    }

    @Override
    public void navigateToCallMeBackFragment(String description, String uniqueNumber) {
        callMeBackUniqueReferenceNumber = uniqueNumber;
        EnterSecurityCodeFragment enterSecurityCodeFragment = EnterSecurityCodeFragment.Companion.newInstance(description, secretCode);
        bottomNavigationView.setVisibility(VISIBLE);
        setHeadingStyle(enterSecurityCodeFragment, true);
        startContentFragment(enterSecurityCodeFragment);
    }

    @Override
    public void navigateToCallMeBackFragment(String description) {
        EnterSecurityCodeFragment enterSecurityCodeFragment = EnterSecurityCodeFragment.Companion.newInstance(description, secretCode);
        bottomNavigationView.setVisibility(VISIBLE);
        setHeadingStyle(enterSecurityCodeFragment, true);
        startContentFragment(enterSecurityCodeFragment);
    }

    @Override
    public void onHomeLoanAccountCardClicked(AccountObject accountObject) {
        isHomeLoanPerilsSelected = true;
        requestHomeloanAccountHistory(accountObject);
    }

    @Override
    public void showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message));
    }

    @Override
    public void navigateToCallMeBackSuccessScreen() {
        View.OnClickListener onClickListener = v -> onBackPressed();

        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_success.json")
                .setDescription(getString(R.string.your_call_is_secure))
                .setTitle(getString(R.string.call_verified))
                .setPrimaryButtonLabel(getString(R.string.done_action))
                .build(true);

        GenericResultScreenFragment genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, onClickListener, null);
        startContentFragment(genericResultScreenFragment);
        bottomNavigationView.setVisibility(GONE);
    }

    @Override
    public void navigateToCallMeBackFailureScreen() {
        View.OnClickListener onClickListener = v -> navigateToReportFraudFragment();

        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_failure.json")
                .setDescription(getString(R.string.your_call_is_not_secure_description))
                .setTitle(getString(R.string.your_call_is_not_secure))
                .setSecondaryButtonLabel(getString(R.string.try_again))
                .build(false);

        GenericResultScreenFragment genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, null, onClickListener);
        startContentFragment(genericResultScreenFragment);
        bottomNavigationView.setVisibility(GONE);
    }

    private boolean isCurrentFragmentOfType(Fragment currentFragment, Class<?> type) {
        return currentFragment != null && currentFragment.getClass() == type;
    }

    @Override
    public void creditCardRequest(AccountObject accountObject) {
        presenter.creditCardRequest(accountObject);
    }

    @Override
    public void openPolicyDetailsScreen(PolicyDetail policyDetail) {
        if (policyDetail != null) {
            getAppCacheService().setPolicyDetail(policyDetail);
        }
        if (isHomeLoanPerilsSelected) {
            startActivity(new Intent(this, HomeLoanPerilsHubActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP).putExtra(ACCOUNT_OBJECT, homeCacheService.getSelectedHomeLoanAccount()));
        } else {
            Intent homeLoanAccountSummaryIntent = new Intent(HomeContainerActivity.this, InsurancePolicyClaimsBaseActivity.class);
            homeLoanAccountSummaryIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeLoanAccountSummaryIntent);
        }
    }

    public void setBottomNavigationViewMenus() {
        if (bottomNavigationView.getMenu().size() == 0) {
            bottomNavigationView.getMenu().add(1, R.id.home_menu_item, 1, R.string.home).setIcon(R.drawable.ic_home);
            if (!isExploreHubDisabled) {
                bottomNavigationView.getMenu().add(1, R.id.explore_menu_item, 2, R.string.home_container_explore_menu_item).setIcon(R.drawable.ic_explore_bottom_nav);
            }
            if (featureSwitchingToggles.getInAppMailbox() != FeatureSwitchingStates.GONE.getKey()) {
                bottomNavigationView.getMenu().add(1, R.id.messages_menu_item, 3, R.string.home_container_messages_menu_item).setIcon(R.drawable.ic_notification).setOnMenuItemClickListener(item -> false);
                bottomNavigationView.setItemIconTintList(colorStateList);
            }
            bottomNavigationView.getMenu().add(1, R.id.contact_us_menu_item, 4, R.string.contact_us_heading).setIcon(R.drawable.ic_contactus_dark);
            bottomNavigationView.getMenu().add(1, R.id.more_menu_item, 5, R.string.home_container_more_menu_item).setIcon(R.drawable.ic_menu_home);

            bottomNavigationView.setItemIconTintList(colorStateList);
            return;
        }

        setNavigationMenuTitle(R.id.home_menu_item, R.string.home);
        if (!isExploreHubDisabled) {
            setNavigationMenuTitle(R.id.explore_menu_item, R.string.home_container_explore_menu_item);
        }

        if (IMIconnect.isRegistered() || BuildConfigHelper.STUB) {
            setNavigationMenuTitle(R.id.messages_menu_item, R.string.home_container_messages_menu_item);
        }

        setNavigationMenuTitle(R.id.contact_us_menu_item, R.string.contact_us_heading);
        setNavigationMenuTitle(R.id.more_menu_item, R.string.home_container_more_menu_item);

        ViewGroup items = (ViewGroup) bottomNavigationView.getChildAt(0);
        View homeView = items.getChildAt(0);
        if (homeView instanceof MenuView.ItemView) {
            homeView.setContentDescription(getString(R.string.home));
        }
        View messagesView = items.getChildAt(1);
        if (messagesView instanceof MenuView.ItemView) {
            messagesView.setContentDescription(getString(R.string.home_container_messages_menu_item));
        }

        View contactView = items.getChildAt(2);
        if (contactView instanceof MenuView.ItemView) {
            contactView.setContentDescription(getString(R.string.home_container_contact_us_menu_item));
        }
        View moreView = items.getChildAt(3);
        if (moreView instanceof MenuView.ItemView) {
            moreView.setContentDescription(getString(R.string.home_container_more_menu_content_description));
        }
        bottomNavigationView.setItemIconTintList(colorStateList);
    }

    private void setNavigationMenuTitle(@IdRes int item, @StringRes int title) {
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(item);
        if (menuItem != null) {
            menuItem.setTitle(title);
        }
    }

    public void addBadgeView(boolean hasUnreadMessages) {
        if (notificationBadge == null) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
            notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge_fragment, menuView, false);
            //Temp fix for stub crash
            if (!BuildConfig.STUB && itemView != null) {
                itemView.addView(notificationBadge);
            }
        }

        if (hasUnreadMessages) {
            notificationBadge.setVisibility(VISIBLE);
        } else {
            notificationBadge.setVisibility(GONE);
        }
    }

    void setHeadingStyle(Fragment fragment, boolean isDark) {
        this.isDarkHeading = isDark;
        if (fragment instanceof ContactUsFragment) {
            headingFadeType = HeadingFadeType.FadeToContactUs;
        } else if (headingFadeType.equals(HeadingFadeType.FadeToContactUs)) {
            headingFadeType = HeadingFadeType.FadeFromContactUs;
        }

        switch (headingFadeType) {
            case None:
                toolbar.setBackgroundColor(Color.TRANSPARENT);
                break;
            case FadeFromContactUs:
                invokeHeadingTransition(false, R.drawable.home_container_tranparent_warm_purple_transition);
                break;
            case FadeToContactUs:
                invokeHeadingTransition(true, R.drawable.home_container_warm_purple_transparent_transition);
                break;
        }

        setMenuTextAppearance(isDark);
    }

    private void setMenuTextAppearance(boolean isDark) {
        if (isDark) {
            toolbar.setTitleTextAppearance(this, za.co.absa.presentation.uilib.R.style.ToolbarDarkTheme_TitleTextStyle);
            setTextColorForMenuItem(logoutMenuItem, ContextCompat.getColor(this, za.co.absa.presentation.uilib.R.color.graphite_light_theme_item_color));
        } else {
            toolbar.setTitleTextAppearance(this, za.co.absa.presentation.uilib.R.style.ToolbarLiteTheme_TitleTextStyle);
            setTextColorForMenuItem(logoutMenuItem, Color.WHITE);
        }
    }

    private void setTextColorForMenuItem(MenuItem menuItem, int colorId) {
        if (menuItem != null) {
            SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(colorId), 0, spanString.length(), 0);
            menuItem.setTitle(spanString);
        }
    }

    private void invokeHeadingTransition(boolean isReverseTransition, int transitionDrawableId) {
        toolbar.setBackgroundResource(transitionDrawableId);
        TransitionDrawable transition = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(this, transitionDrawableId)});
        if (isReverseTransition) {
            transition.reverseTransition(HEADING_TRANSITION_DURATION_MILLISECONDS);
        } else {
            transition.startTransition(HEADING_TRANSITION_DURATION_MILLISECONDS);
        }
    }

    public void hideBadge() {
        if (notificationBadge != null && notificationBadge.getVisibility() == VISIBLE) {
            notificationBadge.setVisibility(GONE);
        }
    }

    @Override
    public void emailAppSupportOrGeneralEnquiry(@NotNull String emailAddress) {
        EmailUtil.email(this, emailAddress);
    }

    @Override
    public void call(@NotNull String phoneNumber) {
        TelephoneUtil.call(this, phoneNumber);
    }

    @Override
    public void initView() {
    }

    public void onQuickLinkDataReceived() {
        Intent intent = new Intent(HomeContainerActivity.this, BehaviouralRewardsActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToHomeLoanAccountHub(AccountDetail homeLoanAccountHistoryCleared) {
        if (homeLoanAccountHistoryCleared != null) {
            getAppCacheService().setAccountDetail(homeLoanAccountHistoryCleared);
        }
        startActivity(new Intent(this, HomeLoanPerilsHubActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP).putExtra(ACCOUNT_OBJECT, homeLoanAccountHistoryCleared.getAccountObject()));
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @NotNull
    @Override
    public ExploreHubViewModel exploreHubViewModel() {
        return exploreHubViewModel;
    }

    public enum HeadingFadeType {
        None,
        FadeFromContactUs,
        FadeToContactUs
    }

    void refreshMenuItemTitle() {
        if (logoutMenuItem != null) {
            logoutMenuItem.setTitle(R.string.logout);
        }
    }

    @Override
    public void displayUnitTrustAccount(@NotNull UnitTrustAccountsWrapper successResponse) {
        Intent viewUnitTrustIntent = new Intent(this, ViewUnitTrustBaseActivity.class);
        homeCacheService.setUnitTrustResponseModel(successResponse);
        startActivity(viewUnitTrustIntent);
    }

    @Override
    public void displaySomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message));
    }

    public boolean isExploreHubDisabled() {
        return isExploreHubDisabled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (fragment instanceof MenuFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}