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

package com.barclays.absa.banking.presentation.verification;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.biometric.BiometricHelper;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.dto.LogoutRequest;
import com.barclays.absa.banking.databinding.Rebrand2faAuthActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.framework.twoFactorAuthentication.VerificationRequestProcessor;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.login.services.LoginInteractor;
import com.barclays.absa.banking.login.services.dto.AuthenticateAliasResponse;
import com.barclays.absa.banking.login.ui.passcode.SimplifiedAuthenticationHelper;
import com.barclays.absa.banking.passcode.passcodeLogin.SurecheckAuth2faTransaktDelegate;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.help.HelpActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnimationHelper;
import com.barclays.absa.utils.IAbsaCacheService;
import com.barclays.absa.utils.ImageUtils;
import com.barclays.absa.utils.LocaleHelper;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.UserSettingsManager;
import com.barclays.absa.utils.ViewAnimations;
import com.barclays.absa.utils.key.KeyTools;
import com.entersekt.sdk.Auth;
import com.entersekt.sdk.Button;
import com.entersekt.sdk.NameValue;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import styleguide.forms.OnPasscodeChangeListener;

import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.BOTTOM_BUTTON_MESSAGE;
import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.IS_SUCCESS;
import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.NOTICE_MESSAGE;

public class SureCheckAuth2faActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SureCheckAuth2faActivity.class.getSimpleName();
    public static final String TRANSACTION_MSG = "TransactionMsg";
    protected String enteredPasscode = "";
    private BiometricHelper biometricHelper;
    private BiometricManager biometricManager;
    protected ProfileManager profileManager;
    protected SimplifiedAuthenticationHelper simplifiedAuthenticationHelper;
    private Rebrand2faAuthActivityBinding binding;
    private Auth auth;
    private NotificationManager notificationManager;
    private UserProfile authOwner;
    private TransaktDelegate transaktAuthAnswerDelegate;
    private LoginInteractor authenticateAliasInteractor;
    private int passcodeTries = 3;
    private int retries = 3;
    private int profileCount;
    private AuthViewMode authViewMode = AuthViewMode.NotSet;
    private boolean hasTransactionDetailsSection = false;
    private final String ORIGIN_DEVICE = "OriginDevice";
    private final String ORIGIN_CHANNEL = "OrigChannel";
    private final String DEVICE_ID = "Device ID";
    private final String MSG_SORT_SEQ = "MsgSortSeq";
    private TransaktHandler passcodeAuthenticateAliasTransaktHandler;
    private TransaktHandler authAnswerTransaktHandler;
    private Toolbar toolbar;
    private MenuItem cancelMenuItem;
    private MenuItem suspiciousMenuItem;
    public final String authNegativeRole = "negative";
    private final String authPositiveRole = "positive";
    private ViewGroup transactionLayout;
    private ActionBar actionBar;
    private String locale = "en";
    public static boolean isFraud = false;
    private final IAbsaCacheService absaCacheService = DaggerHelperKt.getServiceInterface(IAbsaCacheService.class);

    private ExtendedResponseListener<Object> logoutResponseListener = new ExtendedResponseListener<Object>() {
        @Override
        public void onSuccess(Object successResponse) {
            BMBApplication.getInstance().signOut(ProfileManager.getInstance().getProfileCount());
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            BMBApplication.getInstance().signOut(ProfileManager.getInstance().getProfileCount());
        }
    };

    private ExtendedResponseListener<AuthenticateAliasResponse> authenticateAliasWithPasscodeResponseListener = new ExtendedResponseListener<AuthenticateAliasResponse>() {

        @Override
        public void onSuccess(AuthenticateAliasResponse successResponse) {
            passcodeAuthenticateAliasTransaktHandler.setTransaktDelegate(transaktAuthAnswerDelegate);

            if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTxnStatus())) {
                dismissProgressDialog();
                enteredPasscode = "";
                binding.numericKeypad.clearPasscode();
                --passcodeTries;
                if (passcodeTries == 0 || "Credential revoked".equalsIgnoreCase(successResponse.getTxnMessage())) {
                    binding.tvPasscodeStatus.setText(getString(R.string.passcode_revoked));
                } else {
                    String tryAgain = getString(R.string.try_again_newline);
                    String attemptsMessage = passcodeTries == 1 ?
                            getString(R.string.attempt_remaining) : getString(R.string.attempts_remaining);
                    String tryAgainText = tryAgain + passcodeTries + " " + attemptsMessage;
                    binding.tvPasscodeStatus.setText(tryAgainText);
                }
                AnimationHelper.shakeShakeAnimate(binding.cvAuthContainer);
            } else if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTxnStatus())) {
                setAuthViewMode(AuthViewMode.Complete);
            }
        }

    };

    private TransaktDelegate authenticateAliasWithPasscodeDelegate = new TransaktDelegate(this) {
        @Override
        protected void onConnected() {
            BMBLogger.d(TAG, "Hit onConnected in SureCheckAuth");
            passcodeAuthenticateAliasTransaktHandler.generateTrustToken();
        }

        @Override
        protected void onGenerateTrustTokenSuccess(String trustToken) {
            super.onGenerateTrustTokenSuccess(trustToken);
            authenticateAliasInteractor.authenticateAliasWithPasscode(authOwner, enteredPasscode, authenticateAliasWithPasscodeResponseListener);
        }
    };

    private ExtendedResponseListener<AuthenticateAliasResponse> authenticateBiometricsAliasResponseListener = new ExtendedResponseListener<AuthenticateAliasResponse>(this) {

        @Override
        public void onSuccess(final AuthenticateAliasResponse successResponse) {
            dismissProgressDialog();
            if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTxnStatus())) {
                showMessageError(successResponse.getTxnMessage() != null ? successResponse.getTxnMessage() : "");
            } else if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTxnStatus())) {
                biometricHelper.cancelPrompt();
                setAuthViewMode(AuthViewMode.Complete);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewListeners();
        setAuthViewMode(AuthViewMode.NotSet);
        actionBar = getSupportActionBar();

        auth = BMBApplication.getInstance().getVerificationRequest();
        if (auth == null) {
            finish();
            return;
        }
        BMBLogger.d(TAG, "In onCreate()");
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.rebrand_2fa_auth_activity, null, false);
        setContentView(binding.getRoot());

        transactionLayout = binding.getRoot().findViewById(R.id.transactionsLayout);
        setToolbar();

        authenticateAliasInteractor = new LoginInteractor();
        setupAuthenticationHelper();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        BMBLogger.i(TAG, "Auth contents --> " + auth);
        ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
            @Override
            public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                profileCount = userProfiles != null ? userProfiles.size() : 0;
                if (userProfiles != null && userProfiles.size() > 0) {
                    if (authenticateAliasWithPasscodeDelegate != null) {
                        authOwner = VerificationRequestProcessor.whoIsAuthFor(VerificationRequestProcessor.getBase64EncodedEncryptedAliasFromAuthMessage(auth));
                    }
                    if (authOwner == null) {
                        return;
                    }

                    BMBLogger.i(TAG, "Auth owner is: " + authOwner.toString());
                    SureCheckAuth2faActivity.this.auth = BMBApplication.getInstance().getVerificationRequest();

                    final boolean currentUserIsLoggedIn = getAppCacheService().getSecureHomePageObject() != null;
                    UserProfile activeProfile = ProfileManager.getInstance().getActiveUserProfile();
                    if (currentUserIsLoggedIn && activeProfile != null && activeProfile.getUserId() != null && activeProfile.getUserId().equals(authOwner.getUserId())) {
                        displayAuthUI();
                    } else if (currentUserIsLoggedIn) {
                        showLogoutDialog(new LogoutDialogListener() {
                            @Override
                            public void onYes() {
                                logOff();
                            }

                            @Override
                            public void onNo() {
                                BMBApplication.getInstance().clearVerificationRequest();
                                finish();
                            }
                        });
                    } else {
                        displayAuthUI();
                    }
                } else {
                    finish();
                }
            }

            @Override
            public void onProfilesLoadFailed() {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            initialiseBiometrics();
        }
    }

    private void initialiseBiometrics() {
        biometricHelper = new BiometricHelper(this, new BiometricHelper.Callback() {
            @Override
            public void onAuthenticated(BiometricPrompt.CryptoObject cryptoObject) {
                transaktHandler = BMBApplication.getInstance().getTransaktHandler();
                transaktHandler.setConnectCallbackTriggeredFlag(false);

                TransaktDelegate delegate = new SurecheckAuth2faTransaktDelegate(SureCheckAuth2faActivity.this, transaktHandler, new SurecheckAuth2faTransaktDelegate.SurecheckAuth2faTransaktDelegateListener() {
                    @Override
                    public void onError(String errorMessage) {
                    }

                    @Override
                    public void onSuccess() {
                        UserProfile user = ProfileManager.getInstance().getActiveUserProfile();
                        if (user.getMigrationVersion() > 1) {
                            byte[] biometricCipherEncryptedRandomAliasId = user.getRandomAliasId() != null ? user.getRandomAliasId() : user.getFingerprintId();
                            if (biometricCipherEncryptedRandomAliasId != null) {
                                final byte[] decryptedRandomAliasId;
                                try {
                                    decryptedRandomAliasId = cryptoObject.getCipher().doFinal(biometricCipherEncryptedRandomAliasId);
                                    authOwner.setFingerprintId(decryptedRandomAliasId); //THIS LINE SIMPLIFIES BACKWARDS COMPATIBILITY, DO NOT REMOVE IT
                                } catch (BadPaddingException | IllegalBlockSizeException | NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        authenticateAliasInteractor.authenticateAliasWithBiometrics(authOwner, authenticateBiometricsAliasResponseListener);
                    }
                });

                transaktHandler.setTransaktDelegate(delegate);
                transaktHandler.start();
            }

            @Override
            public void onError(int errorCode) {
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    hideFingerprintChange();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                --retries;
                if (retries == 0 && !SureCheckAuth2faActivity.this.isFinishing()) {
                    hideFingerprintChange();
                    biometricHelper.cancelPrompt();
                }
            }
        });
    }

    private void setViewListeners() {
        logoutResponseListener.setView(this);
        authenticateAliasWithPasscodeResponseListener.setView(this);
        authenticateBiometricsAliasResponseListener.setView(this);
    }

    private void logOff() {
        absaCacheService.clear();
        AbsaCacheManager.getInstance().clearCache();
        LogoutRequest<Object> logoutRequest = new LogoutRequest<>(logoutResponseListener);
        ServiceClient serviceClient = new ServiceClient(logoutRequest);
        serviceClient.submitRequest();
    }

    private void showLogoutDialog(final LogoutDialogListener logoutDialogListener) {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.auth_logout_title))
                .message(getString(R.string.logout_for_auth))
                .positiveDismissListener((dialog, which) -> logoutDialogListener.onYes())
                .negativeDismissListener((dialog, which) -> logoutDialogListener.onNo()));
    }

    private void displayAuthUI() {
        dismissProgressDialog();
        if (auth != null) {
            if (authOwner == null) {
                finish();
                return;
            }

            setProfileImage();
            final boolean isUserLoggedIn = getAppCacheService().getSecureHomePageObject() != null;
            String authOwnerId = authOwner.getUserId();
            UserProfile activeProfile = ProfileManager.getInstance().getActiveUserProfile();
            final boolean currentUserIsOwnerOfAuth = authOwnerId != null && activeProfile != null && authOwnerId.equals(activeProfile.getUserId());
            if (isUserLoggedIn && currentUserIsOwnerOfAuth) {
                setAuthViewMode(AuthViewMode.Complete);
            } else {
                setAuthViewMode(AuthViewMode.ViewReject);
            }

            displayRelevantFields();

            transaktAuthAnswerDelegate = new TransaktDelegate(this) {
                @Override
                protected void onAuthSucceeded(Auth auth) {
                    super.onAuthSucceeded(auth);
                    BMBApplication.getInstance().clearVerificationRequest();
                    BMBLogger.i(TAG, "Auth after being successfully processed is " + auth);
                    Button selectedButton = transaktAuthAnswerDelegate.findButtonThatWasSelected(auth);
                    if (selectedButton.getRole().equalsIgnoreCase(authPositiveRole)) {
                        if (isUserLoggedIn) {
                            if (!getAppCacheService().isCurrentDeviceProcessingSureCheck() && auth.toString().toLowerCase().contains(getString(R.string.change_verification_device_response_text))) {
                                logoutAndGoToStartScreen();
                            } else {
                                finish();
                            }
                        } else {
                            finish();
                            showTransactionVerifiedScreen();
                        }
                    } else if (selectedButton.getRole().equalsIgnoreCase(authNegativeRole)) {
                        BMBApplication.getInstance().clearVerificationRequest();
                        switch (suspiciousMenuItem.getItemId()) {
                            case R.id.i_changed_my_mind:
                                SureCheckAuth2faActivity.isFraud = false;
                                break;
                            case R.id.suspicious_transaction:
                                SureCheckAuth2faActivity.isFraud = true;
                                break;
                        }
                        navigateToTransactionRejectedActivity(SureCheckAuth2faActivity.isFraud);
                        return;
                    } else {
                        finish();
                    }
                    dismissProgressDialog();
                }

                @Override
                protected void onError(String errorMessage) {
                    super.onError(errorMessage);
                    BMBApplication.getInstance().clearVerificationRequest();
                    dismissProgressDialog();
                }

                @Override
                protected void onConnected() {
                    super.onConnected();
                    String credential = getAppCacheService().getAuthCredential();
                    if (credential != null && authAnswerTransaktHandler != null) {
                        authAnswerTransaktHandler.sendAuthorization(auth, authOwner);
                    }
                }
            };

            final List<Button> buttons = auth.getButtons();
            for (final Button button : buttons) {
                recordMonitoringEventAuthArrived(MonitoringService.MONITORING_EVENT_NAME_AUTH_RECEIVED_ON_INITIATING_DEVICE_END_TIME);
                if (button.getRole().equalsIgnoreCase(authNegativeRole)) {
                    binding.btnNegative.setText(button.getLabel());
                    binding.btnNegative.setContentDescription(button.getLabel());
                    binding.btnNegative.setOnClickListener(v -> onNegativeButtonClick());
                } else if (button.getRole().equalsIgnoreCase(authPositiveRole)) {
                    binding.btnPositive.setText(button.getLabel());
                    binding.btnPositive.setContentDescription(button.getLabel());
                    binding.btnPositive.setOnClickListener(v -> onPositiveButtonClick());
                }
            }

            dismissProgressDialog();
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    BMBApplication.getInstance().clearVerificationRequest();
                    finish();
                });
            }
        };
        Timer authTimeoutTimer = new Timer();
        authTimeoutTimer.schedule(timerTask, 55000);
        dismissProgressDialog();
    }

    private void navigateToTransactionRejectedActivity(boolean isFraud) {
        Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
        Intent intent;
        if (getAppCacheService().getUserLoggedInStatus()) {
            intent = IntentFactory.getRejectedResultScreen(topMostActivity, isFraud);
            topMostActivity.startActivity(intent);
        } else {
            intent = IntentFactory.getRejectedPreLoginResultScreen(topMostActivity, isFraud);
            topMostActivity.startActivity(intent);
            topMostActivity.finish();
        }
    }

    private void onNegativeButtonClick() {
        showSuspiciousRejectionsBottomSheet();
    }

    private void onPositiveButtonClick() {
        for (Button button : auth.getButtons()) {
            if (button.getRole().equalsIgnoreCase(authPositiveRole)) {
                onSendAuthToTransakt(button);
                return;
            }
        }
    }

    private void sendNegativeAuthToTransakt() {
        for (Button button : auth.getButtons()) {
            if (button.getRole().equalsIgnoreCase(authNegativeRole)) {
                onSendAuthToTransakt(button);
            }
        }
    }

    private void onSendAuthToTransakt(Button button) {
        button.select();
        showProgressDialog();
        notificationManager.cancelAll();

        authAnswerTransaktHandler = BMBApplication.getInstance().getTransaktHandler();
        authAnswerTransaktHandler.setConnectCallbackTriggeredFlag(false);

        authAnswerTransaktHandler.setTransaktDelegate(transaktAuthAnswerDelegate);
        authAnswerTransaktHandler.start();
        if (button.getRole().equalsIgnoreCase("neutral")) {
            BMBApplication.getInstance().clearVerificationRequest();
        }
    }

    private NameValue getNameValue(String name) {
        for (int index = 0; index < auth.getNameValues().size(); index++) {
            if (name.toLowerCase().equals(auth.getNameValues().get(index).getName().toLowerCase())) {
                return auth.getNameValues().get(index);
            }
        }
        return null;
    }

    private void recordMonitoringEventAuthArrived(String eventName) {
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, System.currentTimeMillis());
        new MonitoringInteractor().logMonitoringEvent(eventName, eventData);
    }

    private void displayAuthViewCancel() {
        binding.llSlidingTransactionView.setVisibility(View.GONE);
        binding.llViewTransactionContainer.setVisibility(View.VISIBLE);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sure_check_suspicious_transaction_title));

            actionBar.setHomeAsUpIndicator(R.drawable.ic_cross_light);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setInformationMessage();
        setMenuItemText(getString(R.string.help_title));
        setFooterViewVisibility(View.VISIBLE);
    }

    private void setViewCancelTitle() {
        final NameValue nameValue = getNameValue(TRANSACTION_MSG);
        if (nameValue != null) {
            binding.tvInformationMessage.setText(nameValue.getValue());
        } else {
            binding.tvInformationMessage.setText(auth.getText());
        }
    }

    private void displayRelevantFields() {
        String[] sortSequenceItems = new String[0];

        NameValue sortSequenceNameValue = getNameValue(MSG_SORT_SEQ);
        if (sortSequenceNameValue != null) {
            sortSequenceItems = sortSequenceNameValue.getValue().split("\\|");
        }

        binding.btnView.setOnClickListener(this);
        binding.ivChangeAuthType.setOnClickListener(this);
        binding.reportFraudButton.setOnClickListener(this);

        for (String item : sortSequenceItems) {
            NameValue itemNameValue = getNameValue(item);
            if (itemNameValue != null) {
                addNameValueItem(itemNameValue.getName(), itemNameValue.getValue());
                hasTransactionDetailsSection = true;
            }
        }

        setInformationMessage();

        NameValue origin = getNameValue(ORIGIN_DEVICE);
        if (origin != null) {
            addNameValueItem(getString(R.string.sure_check_name_value_origin_device), origin.getValue());
            hasTransactionDetailsSection = true;
        } else {
            origin = getNameValue(ORIGIN_CHANNEL);
            if (origin != null) {
                addNameValueItem(getString(R.string.sure_check_name_value_origin_device), origin.getValue());
                hasTransactionDetailsSection = true;
            }
        }

        setTransactionDetailsSectionVisibility();

        setTransactionDetailsDividerVisibility(hasTransactionDetailsSection);

        binding.footerTextView.setText(getString(R.string.sure_check_footer_message));
    }

    private void setFooterViewVisibility(int visibility) {
        binding.footerTextView.setVisibility(visibility);
    }

    private void setInformationMessage() {
        String authPrompt = auth.getText();
        if (authPrompt != null) {
            binding.tvInformationMessage.setText(authPrompt);
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.sure_check_suspicious_transaction_title));
            }
            binding.reportFraudButton.setText(R.string.report_fraud);
            binding.btnView.setText(R.string.view);
            if (!BMBApplication.getInstance().getUserLoggedInStatus()) {
                if (authPrompt.toLowerCase().contains("jy") || authPrompt.contains("Besigtig")) {
                    if (!AFRIKAANS_CODE.equals(LocaleHelper.getLanguage())) {
                        locale = AFRIKAANS_CODE;
                        BMBApplication.getInstance().updateLanguage(this, locale);
                        setLocale2();
                    }
                } else if (!ENGLISH_CODE.equals(LocaleHelper.getLanguage())) {
                    locale = ENGLISH_CODE;
                    BMBApplication.getInstance().updateLanguage(this, locale);
                    setLocale2();
                }
            }
        }
    }

    private String getAuthUsername() {
        if (authOwner != null && authOwner.getCustomerName() != null) {
            return authOwner.getCustomerName();
        }
        return "";
    }

    private void setTransactionDetailsSectionVisibility() {
        if (!hasTransactionDetailsSection) {
            transactionLayout.setVisibility(View.GONE);
        } else {
            transactionLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setTransactionDetailsDividerVisibility(boolean hasTransactionDetailsSection) {
        if (hasTransactionDetailsSection) {
            binding.dividerTop.setVisibility(View.VISIBLE);
            binding.dividerBottom.setVisibility(View.VISIBLE);
        } else {
            binding.dividerTop.setVisibility(View.GONE);
            binding.dividerBottom.setVisibility(View.GONE);
        }
    }

    private void addNameValueItem(String name, String value) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.surecheck_item, null);

        ((TextView) view.findViewById(R.id.nameTextView)).setText(name);
        ((TextView) view.findViewById(R.id.valueTextView)).setText(value);

        transactionLayout.addView(view);
    }

    private void setupAuthenticationHelper() {
        simplifiedAuthenticationHelper = new SimplifiedAuthenticationHelper(SureCheckAuth2faActivity.this);
    }

    private boolean hasMultipleUserProfiles() {
        return profileCount >= 2;
    }

    @Override
    public void onClick(View view) {
        preventDoubleClick(view);
        switch (view.getId()) {
            case R.id.btn_view:
                setAuthViewMode(AuthViewMode.Passcode);
                break;
            case R.id.report_fraud_button:
                BMBApplication.getInstance().clearVerificationRequest();
                BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                        .title(getString(R.string.report_fraud))
                        .message(getString(R.string.would_you_like_to_report_fraud))
                        .positiveDismissListener((dialog, which) -> TelephoneUtil.callFraudHotlineAndLostStolen(SureCheckAuth2faActivity.this)));
                break;
            case R.id.iv_changeAuthType:
                activateFingerprintScanner();
                break;
        }
    }

    private void showTransactionVerifiedScreen() {
        GenericResultActivity.bottomOnClickListener = v -> BMBApplication.getInstance().getTopMostActivity().finish();

        Intent intent = new Intent(this, GenericResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(IS_SUCCESS, true);
        intent.putExtra(NOTICE_MESSAGE, R.string.transaction_verified);
        intent.putExtra(BOTTOM_BUTTON_MESSAGE, R.string.done);
        startActivity(intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPasscodeScreen() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            biometricManager = BiometricManager.from(this);
        }

        binding.rivUserProfileImageView.setVisibility(View.VISIBLE);
        binding.tvInformationMessage.setVisibility(View.INVISIBLE);
        binding.tvPasscodeStatus.setText(R.string.passcode_enter);
        binding.vfLoginAnimatorScreen.setVisibility(View.VISIBLE);

        setMenuItemText(" " + getString(R.string.cancel));
        setFooterViewVisibility(View.GONE);

        PermissionFacade.requestDeviceStatePermission(SureCheckAuth2faActivity.this, () -> {
            if (isFingerprintUseAllowed()) {
                if (hasMultipleUserProfiles()) {
                    showPasscodeLoginSection();
                } else {
                    PackageManager packageManager = getPackageManager();
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                        biometricManager = BiometricManager.from(this);
                        PermissionHelper.requestFingerprintPermission(SureCheckAuth2faActivity.this, () -> {
                            ExpandPasscodeAnimations();
                            binding.ivChangeAuthType.setVisibility(View.VISIBLE);
                            activateFingerprintScanner();
                        });
                    } else {
                        showPasscodeLoginSection();
                    }
                }
            } else {
                showPasscodeLoginSection();
            }
        });

        binding.llSlidingTransactionView.setVisibility(View.GONE);

        binding.numericKeypad.setOnPasscodeChangeListener(new OnPasscodeChangeListener() {
            @Override
            public void onCompleted(String passcode) {
                showProgressDialog();
                enteredPasscode = passcode;

                passcodeAuthenticateAliasTransaktHandler = BMBApplication.getInstance().getTransaktHandler();
                passcodeAuthenticateAliasTransaktHandler.setConnectCallbackTriggeredFlag(false);

                passcodeAuthenticateAliasTransaktHandler.setTransaktDelegate(authenticateAliasWithPasscodeDelegate);
                passcodeAuthenticateAliasTransaktHandler.start();
            }

            @Override
            public void onChangedPasscode(String currentPasscode) {
            }

            @Override
            public void onKeyEntered(String currentPasscode) {
                playClickSound();
            }
        });
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setTitle(getAuthUsername());
        }
        if (toolbar != null) {
            toolbar.setContentInsetsAbsolute(getResources().getDimensionPixelSize(R.dimen.medium_space), 0);
        }

        setTransactionDetailsDividerVisibility(false);
    }

    private void showPasscodeLoginSection() {
        ExpandPasscodeAnimations();
        binding.ivChangeAuthType.setVisibility(View.GONE);
    }

    private void ExpandPasscodeAnimations() {
        ViewAnimations.collapse(binding.llViewTransactionContainer).setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showCompleteAuth() {
        dismissProgressDialog();

        setViewCancelTitle();
        binding.rivUserProfileImageView.setVisibility(View.GONE);
        binding.exclamationImageView.setVisibility(View.VISIBLE);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_cross_light);

            boolean isSureCheckOriginatedFromAnotherDevice = true;
            NameValue sureCheckOriginatedFromDevice = getNameValue(DEVICE_ID);
            if (sureCheckOriginatedFromDevice != null) {
                String deviceId = SecureUtils.INSTANCE.getDeviceID();
                if (deviceId.equalsIgnoreCase(sureCheckOriginatedFromDevice.getValue())) {
                    isSureCheckOriginatedFromAnotherDevice = false;
                }
            }
            if (isSureCheckOriginatedFromAnotherDevice) {
                actionBar.setTitle(getString(R.string.sure_check_suspicious_transaction_title));
            } else {
                actionBar.setTitle("");
            }
        }
        if (toolbar != null) {
            toolbar.setContentInsetsAbsolute(0, 0);
        }

        setTransactionDetailsDividerVisibility(hasTransactionDetailsSection);

        setMenuItemText(getString(R.string.help_title));
        setFooterViewVisibility(View.VISIBLE);

        ViewAnimations.collapse(binding.vfLoginAnimatorScreen).setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.ivChangeAuthType.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ViewAnimations.expand(binding.tvInformationMessage).setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ViewAnimations.expand(binding.llSlidingTransactionView);
                        binding.authCompleteButtonContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setMenuItemText(String text) {
        if (cancelMenuItem != null) {
            cancelMenuItem.setTitle(text);
        }
    }

    @Override
    public void onBackPressed() {
        switch (authViewMode) {
            case Complete:
                break;
            case Passcode:
            case ViewReject:
                showSuspiciousRejectionsBottomSheet();
                break;
            default:
        }
    }

    void setProfileImage() {
        binding.rivUserProfileImageView.setImageResource(R.drawable.ic_solid_profile_image);
        AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(this);
        if (authOwner != null) {
            AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary((authOwner).getImageName());
            if (null != addBeneficiaryObject) {
                byte[] oldImage = addBeneficiaryObject.getImageData();
                if (null != oldImage) {
                    Bitmap imgBitmap = BitmapFactory.decodeByteArray(oldImage, 0, oldImage.length);
                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(this.getResources(), imgBitmap);
                    ImageUtils.setImageFromBitmap(binding.rivUserProfileImageView, drawable.getBitmap());
                }
            }
        }
    }

    private void playClickSound() {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.playSoundEffect(AudioManager.FX_KEY_CLICK, volume_level);
        }
    }

    protected boolean isFingerprintUseAllowed() {
        return UserSettingsManager.INSTANCE.isFingerprintActive()
                && biometricManager != null
                && (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE || biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE)
                && biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            int permissionStatus = grantResults[0];
            if (requestCode == PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value) {
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        Toast.makeText(SureCheckAuth2faActivity.this, "ACCESS_DEVICE_STATE PERMISSION_GRANTED", Toast.LENGTH_LONG).show();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        Toast.makeText(SureCheckAuth2faActivity.this, "ACCESS_DEVICE_STATE DENIED", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_FINGERPRINT.value) {
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        Toast.makeText(SureCheckAuth2faActivity.this, "ACCESS_FINGERPRINT PERMISSION_GRANTED", Toast.LENGTH_LONG).show();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        Toast.makeText(SureCheckAuth2faActivity.this, "ACCESS_FINGERPRINT DENIED", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    protected void activateFingerprintScanner() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) && isFingerprintUseAllowed()) {
            try {
                Cipher cipher = simplifiedAuthenticationHelper.getCipher();
                if (cipher == null) {
                    UserSettingsManager.INSTANCE.setFingerprintActive(false);
                    authOwner.setFingerprintId(null);
                    profileManager.updateProfile(authOwner, new ProfileManager.OnProfileUpdateListener() {
                        @Override
                        public void onProfileUpdated(UserProfile userProfile) {
                            binding.ivChangeAuthType.setVisibility(View.GONE);
                        }

                        @Override
                        public void onProfileUpdateFailed() {

                        }
                    });
                } else {
                    biometricHelper.authenticate(cipher);
                }
            } catch (NullPointerException | KeyTools.KeyToolsException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(LOGTAG, "Failed to initialise the fingerprint manager crypto object", e);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        if (biometricHelper != null) {
            biometricHelper.cancelPrompt();
        }
        super.onPause();
    }

    private void hideFingerprintChange() {
        binding.ivChangeAuthType.setVisibility(View.GONE);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sure_check_confirmation_menu, menu);
        cancelMenuItem = menu.findItem(R.id.cancel);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                switch (authViewMode) {
                    case ViewReject:
                    case Complete:
                        Intent helpIntent = new Intent(this, HelpActivity.class);
                        startActivity(helpIntent);
                        break;
                    case Passcode:
                        showSuspiciousRejectionsBottomSheet();
                        break;
                    default:
                        break;
                }
                return true;
            case android.R.id.home:
                switch (authViewMode) {
                    case ViewReject:
                    case Complete:
                        showSuspiciousRejectionsBottomSheet();
                        break;
                    case Passcode:
                    default:
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSuspiciousRejectionsBottomSheet() {
        new BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                .title(R.string.sure_check_reason_for_rejection)
                .sheet(R.menu.sure_check_suspicious_menu)
                .listener(this::onSuspiciousMenuItemClick).show();
    }

    private boolean onSuspiciousMenuItemClick(MenuItem menuItem) {
        suspiciousMenuItem = menuItem;
        if (AuthViewMode.Complete.equals(authViewMode)) {
            if (suspiciousMenuItem.getItemId() == R.id.what_is_2fa) {
                navigateToWhatIs2FA();
            } else {
                showProgressDialog();
                sendNegativeAuthToTransakt();
            }
        } else {
            switch (suspiciousMenuItem.getItemId()) {
                case R.id.i_changed_my_mind:
                    BMBApplication.getInstance().clearVerificationRequest();
                    SureCheckAuth2faActivity.isFraud = false;
                    break;
                case R.id.suspicious_transaction:
                    BMBApplication.getInstance().clearVerificationRequest();
                    SureCheckAuth2faActivity.isFraud = true;
                    break;
                case R.id.what_is_2fa:
                    BMBApplication.getInstance().clearVerificationRequest();
                    navigateToWhatIs2FA();
                    break;
            }
            finish();
        }
        return false;
    }

    private void navigateToWhatIs2FA() {
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.absa.co.za/offers/sim-swap-fraud-protection/"));
        if (!startActivityIfAvailable(intent)) {
            Toast.makeText(this, "You do not have an application that supports opening the requested website", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void recreate() {
        super.recreate();
        startActivity(getIntent());
        finish();
    }

    interface LogoutDialogListener {
        void onYes();

        void onNo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    private void setAuthViewMode(AuthViewMode authViewMode) {
        this.authViewMode = authViewMode;
        switch (authViewMode) {
            case Passcode:
                showPasscodeScreen();
                break;
            case Complete:
                showCompleteAuth();
                break;
            case ViewReject:
                displayAuthViewCancel();
                break;
            case NotSet:
            default:
                break;
        }
    }

    private enum AuthViewMode {
        NotSet,
        ViewReject,
        Passcode,
        Complete
    }
}