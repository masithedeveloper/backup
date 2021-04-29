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
package com.barclays.absa.banking.passcode.createPasscode;

import android.os.Handler;

import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationService;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.validators.BMBPasscodeValidator;
import com.barclays.absa.banking.home.ui.FetchPolicyAuthorizationsFromConfirmPasscode;
import com.barclays.absa.banking.login.services.LoginInteractor;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.RegisterCredentialsResponse;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.integration.DeviceProfilingInteractor;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AliasReader;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;
import com.entersekt.sdk.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import za.co.absa.networking.ExpressNetworkingConfig;

import static com.barclays.absa.banking.framework.app.BMBConstants.AFRIKAANS_CODE;
import static com.barclays.absa.banking.framework.app.BMBConstants.ENGLISH_CODE;
import static com.barclays.absa.banking.framework.app.BMBConstants.ENGLISH_LANGUAGE;

public class ConfirmPasscodePresenter {

    private static final String TAG = ConfirmPasscodePresenter.class.getSimpleName();
    private final RegistrationInteractor registrationInteractor;
    private ConfirmPasscodeView view;
    private String passcode;
    private TransaktHandler registerCredentialsTransaktHandler;
    private final TransaktDelegate transaktDelegate;
    private final SymmetricCryptoHelper symmetricCryptoHelper;
    private UserProfile userProfile;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private void setLanguageForAppAndProfile(SecureHomePageObject secureHomePageObject, UserProfile profile) {
        String currentLanguage = secureHomePageObject.getLangCode();
        String newLanguageCode = String.valueOf(ENGLISH_LANGUAGE).equals(currentLanguage) ? ENGLISH_CODE : AFRIKAANS_CODE;
        BMBApplication.getInstance().updateLanguage(view.getActivity(), newLanguageCode);

        if (profile != null) {
            if (currentLanguage != null) {
                profile.setLanguageCode(currentLanguage);
            }
            ProfileManager.getInstance().updateProfile(profile, null);
        }
    }

    private ExtendedResponseListener<SecureHomePageObject> loginResponseListener = new ExtendedResponseListener<SecureHomePageObject>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final SecureHomePageObject successResponse) {
            createDeviceProfilingPostLoginSession(() -> {
                SharedPreferenceService.INSTANCE.setFirstLoginStatus(true);
                if (!(AppConstants.RESPONSE_CODE_SUCCESS.equalsIgnoreCase(successResponse.getResponseCode()) || AppConstants.RESPONSE_CODE_EMS_FAILED.equalsIgnoreCase(successResponse.getResponseCode()))) {
                    view.dismissProgressDialog();
                    view.showErrorMessage(successResponse.getResponseMessage());
                } else {
                    AbsaCacheManager absaCacheManager = AbsaCacheManager.getInstance();
                    ArrayList<AccountObject> accountsList = absaCacheManager.getAccountsList().getAccountsList();

                    SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();

                    secureHomePageObject.setFromAccounts(absaCacheManager.filterFromAccountList(accountsList, ""));
                    secureHomePageObject.setToAccounts(absaCacheManager.filterToAccountList(accountsList));
                    secureHomePageObject.setAccounts(accountsList);

                    String cellphoneNumber = secureHomePageObject.getCustomerProfile().getCellNumber();
                    if (cellphoneNumber != null) {
                        appCacheService.setCellphoneNumber(cellphoneNumber);
                    }

                    boolean isTransactionsUser = secureHomePageObject.getCustomerProfile().isTransactionalUser();
                    appCacheService.setIsTransactionalUser(isTransactionsUser);

                    String customerSessionId = secureHomePageObject.getCustomerProfile().getCustomerSessionId();
                    if (customerSessionId != null) {
                        appCacheService.setCustomerSessionId(customerSessionId);
                    }

                    if (passcode != null) {
                        appCacheService.setAuthCredential(passcode);
                    }
                    appCacheService.setAuthCredentialType(TransaktDelegate.CREDENTIAL_TYPE_5_DIGIT_PASSCODE);

                    //cache the primary second factor device flag
                    appCacheService.setPrimarySecondFactorDevice(secureHomePageObject.isPrimarySecondFactorDevice());

                    final ProfileManager profileManager = ProfileManager.getInstance();
                    UserProfile activeUser = profileManager.getActiveUserProfile();
                    if (secureHomePageObject.getCustomerProfile().getCustomerName() != null) {
                        activeUser.setCustomerName(secureHomePageObject.getCustomerProfile().getCustomerName());
                    } else {
                        activeUser.setCustomerName("");
                    }
                    setLanguageForAppAndProfile(appCacheService.getSecureHomePageObject(), activeUser);

                    profileManager.updateProfile(activeUser, new ProfileManager.OnProfileUpdateListener() {
                        int retry = 0;

                        @Override
                        public void onProfileUpdated(UserProfile userProfile) {
                            view.loginComplete();
                        }

                        @Override
                        public void onProfileUpdateFailed() {
                            view.dismissProgressDialog();
                            if (retry++ < 1) {
                                view.loginComplete();
                            } else {
                                view.showGenericErrorMessage();
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            if (failureResponse != null && failureResponse.getResponseMessage() != null) {
                view.showErrorMessage(failureResponse.getResponseMessage());
            } else {
                view.showErrorMessage(failureResponse != null ? failureResponse.getErrorMessage() : "Technical error in deviceLinking. Please try again");
            }
        }
    };

    void fetchAuthorizations() {
        new FetchPolicyAuthorizationsFromConfirmPasscode(ConfirmPasscodePresenter.this).fetch();
    }

    private final ExtendedResponseListener<RegisterCredentialsResponse> registerCredentialsResponseListener = new ExtendedResponseListener<RegisterCredentialsResponse>() {

        @Override
        public void onSuccess(final RegisterCredentialsResponse successResponse) {

            if ("Failure".equals(successResponse.getTransactionStatus())) {
                if (userProfile != null) {
                    TransactionVerificationService transactionVerificationService = new TransactionVerificationInteractor();
                    byte[] alias = userProfile.getAlias();
                    transactionVerificationService.deleteAlias(new String(alias != null ? alias : new byte[]{}), ExpressNetworkingConfig.deviceId, new ExtendedResponseListener<TransactionVerificationValidateCodeResponse>() {
                        @Override
                        public void onSuccess(TransactionVerificationValidateCodeResponse deleteAliasResponse) {
                            ProfileManager.getInstance().deleteProfile(userProfile, null);
                            view.dismissProgressDialog();
                            view.showDeviceLinkingFailedScreen(successResponse.getTransactionMessage());
                        }
                    });
                } else {
                    view.dismissProgressDialog();
                    view.showDeviceLinkingFailedScreen(successResponse.getTransactionMessage());
                }
            } else {
                if (appCacheService.isPasscodeResetFlow()) {
                    view.showPasscodeResetSuccessMessage();
                    appCacheService.setPasscodeResetFlow(false);
                    return;
                }

                appCacheService.setIsSecondaryDevice(!successResponse.getPrimarySecondFactorDevice());
                String otpSeed = successResponse.getOtpSeed();
                Handler backgroundHandler = new Handler(BMBApplication.getInstance().getBackgroundHandlerThread().getLooper());
                backgroundHandler.post(() -> {
                    if (otpSeed != null) {
                        try {
                            byte[] zeroEncryptedOtpSeed = symmetricCryptoHelper.encryptAliasWithZeroKey(otpSeed.getBytes());
                            symmetricCryptoHelper.storeAlias(BMBConstants.OTP_SEED, zeroEncryptedOtpSeed);
                        } catch (SymmetricCryptoHelper.EncryptionFailureException | SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
                            BMBLogger.e(TAG, e.getMessage());
                        }
                    }
                });
                view.performExpressLogin(userProfile);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (userProfile != null) {
                ProfileManager.getInstance().deleteProfile(userProfile, null);
            }
            view.dismissProgressDialog();
            view.showDeviceLinkingFailedScreen();
        }
    };

    private void createLocalUserProfile() {
        try {
            String alias = AliasReader.readLinkingAlias();
            if (ProfileManager.getInstance().getProfileCount() < SimplifiedLoginActivity.MAXIMUM_PER_DEVICE_USER_PROFILES) {
                try {
                    if (alias != null) {
                        userProfile = ProfileManager.getInstance().initialiseUserProfile(alias);
                        ProfileManager.getInstance().setActiveUserProfile(userProfile);
                        if (userProfile != null) {
                            ProfileManager.getInstance().addProfile(userProfile, new ProfileManager.OnProfileCreateListener() {
                                private int retry = 0;

                                @Override
                                public void onProfileCreated(final UserProfile userProfile) {
                                    loadProfiles(userProfile);
                                }

                                private void loadProfiles(final UserProfile userProfile) {
                                    ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
                                        @Override
                                        public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                                            ProfileManager.getInstance().setActiveUserProfile(userProfile);
                                        }

                                        @Override
                                        public void onProfilesLoadFailed() {
                                            if (retry++ < 1) {
                                                loadProfiles(userProfile);
                                            } else {
                                                view.showGenericErrorMessage();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onProfileCreateFailed() {
                                    view.dismissProgressDialog();
                                }
                            });
                        }
                    }
                } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException | SymmetricCryptoHelper.EncryptionFailureException e) {
                    view.dismissProgressDialog();
                    BMBLogger.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            } else {
                view.dismissProgressDialog();
                view.showMessageError("You already have the maximum number of user profiles on this device. Please delete one and try to link again.");
            }
        } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException | SymmetricCryptoHelper.DecryptionFailureException | IOException e) {
            view.dismissProgressDialog();
            BMBLogger.e(TAG, e.getMessage());
        }
    }

    ConfirmPasscodePresenter(ConfirmPasscodeView confirmPasscodeView) {
        view = confirmPasscodeView;
        registerCredentialsResponseListener.setView(confirmPasscodeView);
        loginResponseListener.setView(confirmPasscodeView);
        registrationInteractor = new RegistrationInteractor();
        symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        transaktDelegate = new TransaktDelegate(view.getActivity()) {
            @Override
            public void onConnected() {
                super.onConnected();
                registerCredentialsTransaktHandler.generateTrustToken();
            }

            @Override
            protected void onGenerateTrustTokenSuccess(String trustToken) {
                super.onGenerateTrustTokenSuccess(trustToken);
                if (appCacheService.isPasscodeResetFlow()) {
                    registrationInteractor.passcodeReset(passcode, registerCredentialsResponseListener);
                } else {
                    createLocalUserProfile();
                    if (userProfile != null) {
                        appCacheService.setLinkingFlow(true);
                        String randomAliasId = "";
                        if (userProfile.getRandomAliasId() != null) {
                            randomAliasId = new String(userProfile.getRandomAliasId());
                        }
                        appCacheService.setPasscode(passcode);
                        registrationInteractor.registerCredentials(passcode, randomAliasId, registerCredentialsResponseListener);
                    }
                }
            }

            @Override
            protected void onGenerateTrustTokenFailure(Error error) {
                BMBLogger.e(TAG, "Generate token error" + error.toString());
            }
        };
    }

    private void validatePasscode(final String passCode) {
        passcode = passCode;
        final String errorMessage = BMBPasscodeValidator.validatePasscodeWithMessage(passCode);
        if (!errorMessage.isEmpty()) {
            view.showPasscodeErrorMessage(errorMessage);
        } else {
            registerCredentialsTransaktHandler = BMBApplication.getInstance().getTransaktHandler();
            registerCredentialsTransaktHandler.setConnectCallbackTriggeredFlag(false);
            registerCredentialsTransaktHandler.setTransaktDelegate(transaktDelegate);
            registerCredentialsTransaktHandler.start();
        }
    }

    void passcodeEntered(String passcode) {
        validatePasscode(passcode);
    }

    public void onLoginSuccessRespnse() {
        view.dismissProgressDialog();
        SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();
        if (view.userIsEligibleForBiometrics()) {
            view.goToUseFingerprintScreen();
        } else if (secureHomePageObject != null && secureHomePageObject.getAccessPrivileges() != null && secureHomePageObject.getAccessPrivileges().isOperator()) {
            view.showOperatorLinkingSuccessScreen();
        } else {
            view.navigateToPrimaryScreens();
        }
    }

    void performAOlLogin() {
        LoginInteractor loginInteractor = new LoginInteractor();
        loginInteractor.performPasscodeLogin(ProfileManager.getInstance().getActiveUserProfile(), passcode, loginResponseListener);
    }

    private void createDeviceProfilingPostLoginSession(DeviceProfilingInteractor.NextActionCallback nextActionCallback) {
        CustomerProfileObject customerProfile = CustomerProfileObject.getInstance();
        if (BMBApplication.getInstance().isDeviceProfilingActive()) {
            BMBLogger.d(TAG, "Creating post login session...");
            view.getDeviceProfilingInteractor().createPostLoginSession(customerProfile.getCustomerSessionId(), customerProfile.getPermanentUserId(), customerProfile.getUserId());
            appCacheService.setAlreadyCalledForScoreInThisSession(true);
            view.getDeviceProfilingInteractor().callForDeviceProfilingScoreForLogin(nextActionCallback);
        } else {
            view.getDeviceProfilingInteractor().disable();
            nextActionCallback.onNextAction();
        }
    }
}