/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.twoFactorAuthentication;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.push.PushMessageListener;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.verification.SureCheckAuth2faActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;
import com.entersekt.sdk.Auth;
import com.entersekt.sdk.NameValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VerificationRequestProcessor {

    private boolean hasPendingAuth;
    private static final String TAG = VerificationRequestProcessor.class.getSimpleName();
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    void processAuth(Auth auth) {
        hasPendingAuth = false;
        BMBApplication.getInstance().setVerificationRequest(auth);

        final Activity activity = BMBApplication.getInstance().getTopMostActivity();
        if (activity == null) {
            BMBLogger.d(TAG, "Turns out the activity was null. This needs to be investigated further.");
            return;
        }

        BMBLogger.d(TAG, "Transakt auth listener invoked");

        ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
            @Override
            public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                UserProfile ownerOfThisAuth = whoIsAuthFor(getBase64EncodedEncryptedAliasFromAuthMessage(auth));
                if (ownerOfThisAuth != null) {
                    if (isAuthForDifferentUserThatIsNotLoggedIn(ownerOfThisAuth)) {
                        showLogoutDialog(activity, new TransaktSdkAuthReceiver.LogoutDialogListener() {
                            @Override
                            public void onYes() {
                                hasPendingAuth = true;
                                try {
                                    ((BaseActivity) activity).logoutAndGoToStartScreen();
                                } catch (ClassCastException e) {
                                    new MonitoringInteractor().logCaughtExceptionEvent(e);
                                }
                            }

                            @Override
                            public void onNo() {
                                BMBApplication.getInstance().setVerificationRequest(null);
                            }
                        });
                    } else {
                        if (!BMBApplication.getInstance().isInForeground() && !BMBApplication.getInstance().getUserLoggedInStatus()) {
                            Map<String, String> notificationMap = new HashMap<>();
                            notificationMap.put("alert", activity.getString(R.string.default_push_message));
                            notificationMap.put("title", activity.getString(R.string.default_push_message));
                            notificationMap.put(BMBConstants.HAS_AUTH, "true");
                            new PushMessageListener().showPushNotification(notificationMap, activity);
                        } else {
                            showAuthScreen(activity);
                        }
                    }
                }
            }

            @Override
            public void onProfilesLoadFailed() {
                try {
                    ((BaseActivity) activity).showMessageError(AppConstants.GENERIC_ERROR_MSG);
                } catch (ClassCastException e) {
                    new MonitoringInteractor().logCaughtExceptionEvent(e);
                }
            }
        });
    }

    void showAuthScreen(Activity activity) {
        Intent intent = new Intent(activity, SureCheckAuth2faActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_activity_in, R.anim.fade_activity_out);
    }

    public static UserProfile whoIsAuthFor(final String base64EncodedEncryptedAliasID) {
        UserProfile userProfileForAuth = null;
        List<UserProfile> userProfiles = ProfileManager.getInstance().getUserProfiles();
        if (userProfiles != null) {
            for (UserProfile userProfile : userProfiles) {
                String base64EncodedEncryptedUserAlias;
                final SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
                try {
                    final byte[] encryptedAliasBytes = symmetricCryptoHelper.retrieveAlias(userProfile.getUserId());
                    final byte[] aliasBytes = symmetricCryptoHelper.decryptAliasWithZeroKey(encryptedAliasBytes);
                    BMBLogger.d(TAG, "whoIsAuthFor; Alias ID -> " + new String(aliasBytes));
                    base64EncodedEncryptedUserAlias = Base64.encodeToString(TransaktCryptoUtil.encrypt(aliasBytes), Base64.NO_WRAP);
                    if (base64EncodedEncryptedAliasID != null && base64EncodedEncryptedAliasID.equalsIgnoreCase(base64EncodedEncryptedUserAlias)) {
                        BMBLogger.d(TAG, "base64EncodedEncryptedUserAlias " + base64EncodedEncryptedUserAlias);
                        userProfileForAuth = userProfile;
                        break;
                    }
                } catch (Exception e) {
                    BMBLogger.e(TAG, e.getMessage());
                }
            }
        }
        return userProfileForAuth;
    }

    public static String getBase64EncodedEncryptedAliasFromAuthMessage(Auth auth) {
        if (auth != null) {
            for (NameValue nameValue : auth.getNameValues()) {
                if (nameValue.getName().equalsIgnoreCase("__aliasID__")) {
                    String encrypteAlias = nameValue.getValue();
                    BMBLogger.d(TAG, "getBase64EncodedEncryptedAliasFromAuthMessage " + encrypteAlias);
                    return encrypteAlias;
                }
            }
        }
        BMBLogger.d(TAG, "getBase64EncodedEncryptedAliasFromAuthMessage NULL!");
        return null;
    }

    private boolean isAuthForDifferentUserThatIsNotLoggedIn(UserProfile authOwner) {
        UserProfile activeProfile = ProfileManager.getInstance().getActiveUserProfile();
        boolean currentUserIsLoggedIn = appCacheService.getSecureHomePageObject() != null;
        return currentUserIsLoggedIn && activeProfile != null && activeProfile.getUserId() != null && !activeProfile.getUserId().equals(authOwner.getUserId());
    }

    private void showLogoutDialog(Activity activity, final TransaktSdkAuthReceiver.LogoutDialogListener logoutDialogListener) {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(activity.getString(R.string.auth_logout_title))
                .message(activity.getString(R.string.logout_for_auth))
                .positiveDismissListener((dialog, which) -> logoutDialogListener.onYes())
                .negativeDismissListener((dialog, which) -> logoutDialogListener.onNo()));
    }

    boolean hasPendingAuth() {
        return hasPendingAuth;
    }

    void clearPendingAuth() {
        hasPendingAuth = false;
    }
}