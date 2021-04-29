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

package com.barclays.absa.banking.settings.ui;

import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.ProfileSetupResult;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.pushNotifications.PushNotificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.settings.services.responseListeners.AccountDetailsExtendedResponseListener;
import com.barclays.absa.banking.settings.services.responseListeners.ChangeLanguageExtendedResponseListener;
import com.barclays.absa.banking.settings.services.responseListeners.InsurancePolicyListExtendedResponseListener;
import com.barclays.absa.banking.settings.services.responseListeners.ProfileSetupExtendedResponseListener;
import com.barclays.absa.banking.settings.services.responseListeners.PushNotificationsToggleExtendedResponseListener;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper;

import org.jetbrains.annotations.TestOnly;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.framework.app.BMBConstants.CONST_SUCCESS;

public class SettingsHubPresenter extends AbstractPresenter {

    private PushNotificationInteractor pushNotificationInteractor;
    private AccountInteractor accountInteractor;
    private SettingsHubService settingsHubInteractor;
    private ChangeLanguageExtendedResponseListener changeLanguageExtendedResponseListener;
    private ProfileSetupExtendedResponseListener profileSetupExtendedResponseListener;
    private PushNotificationsToggleExtendedResponseListener pushNotificationsToggleExtendedResponseListener;
    private AccountDetailsExtendedResponseListener accountDetailsExtendedResponseListener;
    private InsurancePolicyListExtendedResponseListener insurancePolicyListExtendedResponseListener;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    SettingsHubPresenter(WeakReference<SettingsHubView> weakReference) {
        super(weakReference);
        pushNotificationInteractor = new PushNotificationInteractor();
        accountInteractor = new AccountInteractor();
        settingsHubInteractor = new SettingsHubInteractor();
        changeLanguageExtendedResponseListener = new ChangeLanguageExtendedResponseListener(this);
        pushNotificationsToggleExtendedResponseListener = new PushNotificationsToggleExtendedResponseListener(this);
        profileSetupExtendedResponseListener = new ProfileSetupExtendedResponseListener(this);
        insurancePolicyListExtendedResponseListener = new InsurancePolicyListExtendedResponseListener(this);
        accountDetailsExtendedResponseListener = new AccountDetailsExtendedResponseListener(this);
        accountDetailsExtendedResponseListener.setView(weakReference.get());
    }

    public void onPushNotificationToggleSuccess(final TransactionResponse successResponse) {
        SettingsHubView view = (SettingsHubView) viewWeakReference.get();
        if (view != null) {
            dismissProgressIndicator();
            view.switchOffPushNotification(successResponse);
        }
    }

    public void onResponseLanguageUpdateSuccessful(final SecureHomePageObject successResponse) {
        SettingsHubView view = (SettingsHubView) viewWeakReference.get();
        if (view != null) {
            view.responseLanguageUpdate(successResponse != null && CONST_SUCCESS.equalsIgnoreCase(successResponse.getResponseMessage()));
        }
    }

    public void onProfileSetupSuccess(final ProfileSetupResult profileSetupResult) {
        SettingsHubView view = (SettingsHubView) viewWeakReference.get();
        if (view != null) {
            if (profileSetupResult != null) {
                view.onProfileImageViewInvalidate(profileSetupResult.getImageName());
            }
        }
        dismissProgressIndicator();
    }

    public void onAccountListResponseSuccess(final AccountList successResponse) {
        if (successResponse != null) {
            appCacheService.getSecureHomePageObject().setAccounts(successResponse.getAccountsList());
            AbsaCacheManager.getInstance().updateAccountList(successResponse.getAccountsList());
            AbsaCacheManager.getInstance().setAccountsCacheStatus(true);
        }
    }

    @TestOnly
    SettingsHubPresenter(WeakReference<SettingsHubView> weakReference, SettingsHubInteractor interactor, AccountInteractor accountInteractor, PushNotificationInteractor pushNotificationInteractor) {
        super(weakReference);
        settingsHubInteractor = interactor;
        this.accountInteractor = accountInteractor;
        this.pushNotificationInteractor = pushNotificationInteractor;
        changeLanguageExtendedResponseListener = new ChangeLanguageExtendedResponseListener(this);
        profileSetupExtendedResponseListener = new ProfileSetupExtendedResponseListener(this);
        accountDetailsExtendedResponseListener = new AccountDetailsExtendedResponseListener(this);
        accountDetailsExtendedResponseListener.setView(weakReference.get());
        insurancePolicyListExtendedResponseListener = new InsurancePolicyListExtendedResponseListener(this);
        pushNotificationsToggleExtendedResponseListener = new PushNotificationsToggleExtendedResponseListener(this);
    }

    void switchOffPushNotifications(String pushNotificationRegistrationToken) {
        if (pushNotificationInteractor != null) {
            pushNotificationInteractor.removePushNotificationRecord(pushNotificationRegistrationToken, pushNotificationsToggleExtendedResponseListener);
        }
    }

    void requestLanguageUpdate(char languageCode) {
        showProgressIndicator();
        settingsHubInteractor.requestLanguageUpdate(languageCode, changeLanguageExtendedResponseListener);
    }

    void fireProfileSetupRequest(String languageCode, String customerName, String backgroundImageId, ProfileViewImageHelper profileViewImageHelper) {
        showProgressIndicator();
        settingsHubInteractor.requestProfileSetup(profileViewImageHelper.isPhotoDeleted(), profileViewImageHelper.isPhotoUpdated(),
                profileViewImageHelper.getBitmap(), languageCode, customerName, backgroundImageId, profileSetupExtendedResponseListener);
    }

    void fetchAccountsAndPolicies() {
        accountInteractor.prefetchHomeScreenData(insurancePolicyListExtendedResponseListener);
    }

    public void onResponseLanguageUpdateFailure() {
        SettingsHubView view = (SettingsHubView) viewWeakReference.get();
        if (view != null) {
            view.responseLanguageUpdate(false);
        }
        dismissProgressIndicator();
    }

    public void insurancePolicyListCallCompleted() {
        SettingsHubView view = (SettingsHubView) viewWeakReference.get();
        if (view != null) {
            view.policyListUpdated();
        }
    }
}