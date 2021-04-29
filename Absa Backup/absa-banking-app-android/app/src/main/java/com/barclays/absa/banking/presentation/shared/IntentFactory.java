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
package com.barclays.absa.banking.presentation.shared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentTransaction;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.account.ui.AccountActivity;
import com.barclays.absa.banking.account.ui.AccountSearchActivity;
import com.barclays.absa.banking.account.ui.ArchivedStatementsActivity;
import com.barclays.absa.banking.account.ui.CsvStatementActivity;
import com.barclays.absa.banking.account.ui.StampedStatementActivity;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryDetailsActivity;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.card.ui.creditCard.hub.CardListActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.fixedDeposit.FixedDepositHubActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.login.ui.TermsAndConditionsActivity;
import com.barclays.absa.banking.manage.devices.DeviceListActivity;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.payments.NewPaymentBeneficiaryDetailsActivity;
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubActivity;
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubInformation;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity;
import com.barclays.absa.banking.presentation.shared.widget.PlayStoreRatingDialogFragment;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.SharedPreferenceService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class IntentFactory {

    public static final String NEW_OVERDRAFT_LIMIT = "NEW_OVERDRAFT_LIMIT";
    public static final String RESULT = "RESULT";
    public static final String TRANSACTION_ARRAY_LIST = "TRANSACTION_ARRAY_LIST";
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String ACCOUNT_OBJECT = "ACCOUNT_OBJECT";
    public static final String PERSONAL_LOAN_OBJECT = "PERSONAL_LOAN_OBJECT";
    public static final String FROM_DATE = "FROM_DATE";
    public static final String TO_DATE = "TO_DATE";
    public static final String ONBACKPRESSED_FINISH = "ONBACKPRESSED_FINISH";
    public static String EXTRA_USER_PROFILE = "userProfileExtra";
    public static String FEATURE_NAME = "featureName";

    public IntentFactory() {
        throw new AssertionError("IntentFactory does not support initialization...");
    }

    public static Intent getArchivedStatement(Activity activity, AccountObject accountObject) {
        return getArchivedStatement(activity, accountObject, "");
    }

    public static Intent getCsvStatement(Activity activity, AccountObject accountObject) {
        return getCsvStatement(activity, accountObject, "");
    }

    public static Intent getArchivedStatement(Activity activity, AccountObject accountObject, String featureName) {
        return new IntentBuilder()
                .setAccountObject(accountObject)
                .setClass(activity, ArchivedStatementsActivity.class)
                .build()
                .putExtra(FEATURE_NAME, featureName);
    }

    public static Intent getCsvStatement(Activity activity, AccountObject accountObject, String featureName) {
        return new IntentBuilder()
                .setAccountObject(accountObject)
                .setClass(activity, CsvStatementActivity.class)
                .build()
                .putExtra(FEATURE_NAME, featureName);
    }

    public static Intent getManageDevices(Activity activity) {
        return new IntentBuilder()
                .setClass(activity, DeviceListActivity.class)
                .build();
    }

    public static Intent getAccountActivity(Activity activity, AccountObject accountObject) {
        return new IntentBuilder()
                .setAccountObject(accountObject)
                .setClass(activity, AccountActivity.class)
                .build();
    }

    public static Intent getPersonalLoanHubActivity(Activity activity, AccountObject accountObject, PersonalLoanHubInformation personalLoanHubInformation) {
        return new IntentBuilder()
                .setAccountObject(accountObject)
                .setPersonalLoanObject(personalLoanHubInformation)
                .setClass(activity, PersonalLoanHubActivity.class)
                .build();
    }

    public static Intent getFixedDepositHubActivity(Activity activity, AccountObject accountObject) {
        return new IntentBuilder()
                .setAccountObject(accountObject)
                .setClass(activity, FixedDepositHubActivity.class)
                .build();
    }

    public static Intent getAddNewBeneficiaryPayment(Activity activity, BeneficiaryDetailObject beneficiaryDetailObject) {
        return new IntentBuilder(new Intent().putExtra(RESULT, beneficiaryDetailObject))
                .setClass(activity, NewPaymentBeneficiaryDetailsActivity.class)
                .build();
    }

    public static Intent getTermsAndConditionActivity(Activity activity, boolean shouldBeShowingIIP) {
        return new IntentBuilder(new Intent().putExtra(AppConstants.CLIENT_TYPE, CustomerProfileObject.getInstance().getClientTypeGroup())
                .putExtra(AppConstants.SHOULD_BE_SHOWING_IIP, shouldBeShowingIIP))
                .setClass(activity, TermsAndConditionsActivity.class)
                .build();
    }

    public static Intent getStudentSilverAccountTermsAndConditionActivity(Activity activity) {
        return new IntentBuilder(new Intent().putExtra(AppConstants.CLIENT_TYPE, CustomerProfileObject.getInstance().getClientTypeGroup())
                .putExtra(AppConstants.SHOULD_BE_SHOWING_STUDENT_TERMS, true))
                .setClass(activity, TermsAndConditionsActivity.class)
                .build();
    }

    public static Intent getStampedStatement(Activity activity, String accountNumber, String featureName) {
        return new IntentBuilder()
                .setAccountNumber(accountNumber)
                .setClass(activity, StampedStatementActivity.class)
                .build()
                .putExtra(FEATURE_NAME, featureName);
    }

    public static Intent getLogOutDialog(Context context) {
        Intent intent = new Intent(context, SessionTimeOutDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BMBConstants.SCREEN_NAME_CONST, BaseActivity.mScreenName);
        return intent;
    }

    private static IntentBuilder getGenericResultSuccessfulBuilder(final Activity activity) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .hideGenericResultCallSupport();
    }

    private static IntentBuilder getGenericResultPendingBuilder(final Activity activity) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToAlert()
                .hideGenericResultCallSupport();
    }

    public static IntentBuilder getGenericResultFailureBuilder(final Activity activity) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .hideGenericResultCallSupport();
    }

    public static Intent getAccountSearch(final Activity activity, ArrayList<Transaction> transactionList) {
        return new IntentBuilder(new Intent().putExtra(TRANSACTION_ARRAY_LIST, transactionList))
                .setClass(activity, AccountSearchActivity.class)
                .singleTop()
                .build();
    }

    public static Intent getVCLIncreaseAcceptedResultScreen(final Activity activity, String creditIncreaseValue) {
        return getGenericResultSuccessfulBuilder(activity)
                .setGenericResultIcon(R.drawable.ic_vcl_terms_and_conditions)
                .setGenericResultHeaderMessage(R.string.vcl_result_increase_approved)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(activity.getString(R.string.vcl_result_limit_increased_to, creditIncreaseValue.replaceAll(" ", "\u00A0")))
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getVCLRejectedResultScreen(final Activity activity, boolean isFromCreditCardHub, boolean isFromExplore, String accountNumber) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultHeaderMessage(activity.getString(R.string.vcl_result_rejected_title))
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(activity.getString(R.string.vcl_result_rejected_description))
                .setGenericResultDoneButton(activity, v -> {
                    if (isFromExplore) {
                        reloadAccountsAndGoHome(activity);
                    } else if (!isFromCreditCardHub) {
                        Intent intent = new Intent(activity, CardListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    } else if (isFromCreditCardHub) {
                        Intent intent = new Intent(activity, CreditCardHubActivity.class);
                        intent.putExtra(ACCOUNT_NUMBER, accountNumber);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    }
                })
                .build();
    }

    private static void reloadAccountsAndGoHome(Activity activity) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).loadAccountsAndGoHome();
        }
    }

    public static Intent getVCLIncreaseFailedResultScreen(final Activity activity) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.vcl_result_increase_failed)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getVCLIncreaseInvalidNumberResultScreen(final Activity activity) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.error)
                .setGenericResultSubMessage(activity.getString(R.string.vcl_result_limit_invalid))
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getVCLIncreasePendingResultScreen(final Activity activity, String creditIncreaseValue) {
        return getGenericResultPendingBuilder(activity)
                .setGenericResultIcon(R.drawable.ic_vcl_terms_and_conditions)
                .setGenericResultHeaderMessage(R.string.vcl_result_increase_pending_title)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(activity.getString(R.string.vcl_result_increase_pending_body, creditIncreaseValue.replaceAll(" ", "\u00A0")))
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getVCLIncreaseDeclinedResultScreen(final Activity activity) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.vcl_result_increase_declined_title)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultSubMessage(R.string.vcl_result_increase_declined_body)
                .setGenericResultsSubMessageFontSize()
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getClaimNotificationSuccessfulScreen(final Activity activity, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .hideGenericResultCallSupport()
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(R.string.report_incident_success_screen_title)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultSubMessage(message)
                .setGenericResultBottomButton(R.string.done, v -> reloadAccountsAndGoHomeShowingAccountsList(activity))
                .build();
    }

    public static Intent getClaimNotificationFailureScreen(final Activity activity, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .hideGenericResultCallSupport()
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.report_incident_failure_screen_title)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultSubMessage(message)
                .setGenericResultBottomButton(R.string.done, v -> reloadAccountsAndGoHomeShowingAccountsList(activity))
                .build();
    }

    public static Intent getSubmitCreditProtectionSuccessResultScreen(final Activity activity, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .hideGenericResultCallSupport()
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(R.string.funeral_application_success)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultSubMessage(message)
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getSubmitCreditProtectionFailureScreen(final Activity activity, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .hideGenericResultCallSupport()
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.application_not_successful)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(message)
                .setMakeContactClickableFlag()
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getPaymentTransactionRejectedResultScreen(final Activity activity) {
        return new IntentBuilder().setClass(activity, GenericResultActivity.class)
                .hideGenericResultCallSupport()
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.transaction_rejected)
                .setGenericResultHomeButton(activity)
                .singleTop()
                .build();
    }

    public static Intent getPaymentTransactionFailedResultScreen(final Activity activity) {
        return new IntentBuilder(getPaymentTransactionRejectedResultScreen(activity))
                .setGenericResultHeaderMessage(R.string.transaction_failed)
                .build();
    }

    public static Intent getRejectedResultScreen(final Activity activity, boolean isFraud) {
        IntentBuilder intentBuilder = new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.transaction_rejected)
                .setGenericResultHomeButton(activity);

        if (isFraud) {
            intentBuilder.setGenericResultSubMessage(R.string.sure_check_rejected_copy);
        }

        return intentBuilder.build();
    }

    public static Intent getRejectedPreLoginResultScreen(final Activity activity, boolean isFraud) {
        Intent intent = getRejectedResultScreen(activity, isFraud);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        GenericResultActivity.bottomOnClickListener = v -> BMBApplication.getInstance().getTopMostActivity().finish();
        return intent;
    }

    public static Intent getSuccessfulResultScreen(final Activity activity, String header, String message, boolean shouldRefreshAccountList) {
        return new IntentBuilder(getSuccessfulResultScreen(activity, header, shouldRefreshAccountList))
                .setGenericResultSubMessage(message)
                .build();
    }

    //GenericResultActivity.TOP_BUTTON_MESSAGE
    public static Intent getSuccessfulResultScreen(final Activity activity, @StringRes int header, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultTopButton(R.string.done, v -> reloadAccountsAndGoHomeShowingAccountsList(activity))
                .build();
    }

    public static Intent getPrepaidElectricitySuccessfulResultScreen(final Activity activity, @StringRes int header, String message) {
        IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultTopButton(R.string.done, v ->
                {
                    Intent intent = new Intent(activity, BeneficiaryDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("RESULT", beneficiaryCacheService.getBeneficiaryDetails());
                    intent.putExtra("prepaidElectricityBeneficiaryDetails", beneficiaryCacheService.getPrepaidElectricityMeterNumber());
                    intent.putExtra("beneficiaryType", "PPE");
                    activity.startActivity(intent);
                })
                .build();
    }

    public static Intent getSuccessfulResultScreen(final Activity activity, String header, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultDoneButton(activity, v -> reloadAccountsAndGoHomeShowingAccountsList(activity))
                .build();
    }

    protected static Intent getPendingResultScreen(final Activity activity, @StringRes int header, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToWaitingState()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getSuccessResultScreen(final Activity activity, @StringRes int header, @StringRes int message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultDoneButton(activity)
                .build();
    }

    public static Intent getSuccessfulResultScreen(final Activity activity, @StringRes int header, @StringRes int message, boolean shouldRefreshAccountList) {
        return new IntentBuilder(getSuccessfulResultScreen(activity, header, shouldRefreshAccountList))
                .setGenericResultSubMessage(message)
                .build();
    }

    public static Intent getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(final Activity activity, @StringRes int header, String subMessage, @StringRes int homeButtonText, boolean shouldRefreshAccountList) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToPaymentSuccessful()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(subMessage)
                .hideGenericResultCallSupport()
                .setShouldRefreshAccountList(shouldRefreshAccountList)
                .setGenericResultHomeButton(activity, homeButtonText)
                .build();
    }

    private static Intent getSuccessfulResultScreen(final Activity activity, @StringRes int header, boolean shouldRefreshAccountList) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(header)
                .hideGenericResultCallSupport()
                .setShouldRefreshAccountList(shouldRefreshAccountList)
                .setGenericResultHomeButton(activity)
                .build();
    }

    private static Intent getSuccessfulResultScreen(final Activity activity, String header, boolean shouldRefreshAccountList) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(header)
                .hideGenericResultCallSupport()
                .setShouldRefreshAccountList(shouldRefreshAccountList)
                .setGenericResultHomeButton(activity)
                .build();
    }

    public static IntentBuilder getSuccessfulResultScreenBuilder(final Activity activity, @StringRes int header) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(header)
                .hideGenericResultCallSupport()
                .setGenericResultHomeButton(activity);
    }

    public static Intent getSuccessfulCallHelpLineResultScreen(final Activity activity, @StringRes int header, @StringRes int message) {
        return new IntentBuilder(getSuccessfulResultScreen(activity, header, false))
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultHomeButton(activity)
                .setGenericResultTopButton(R.string.call_helpline, view -> TelephoneUtil.supportCallRegistrationIssues(activity))
                .build();
    }

    public static Intent getFailureResultScreen(final Activity activity, @StringRes int header, @StringRes int message) {
        return getFailureResultScreen(activity, header, activity.getString(message));
    }

    public static Intent getAlertResultScreenPrimaryButton(final Activity activity, @StringRes int header, @StringRes int message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToAlert()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultBottomButton(R.string.done, v -> reloadAccountsAndGoHomeShowingAccountsList(activity))
                .hideGenericResultCallSupport()
                .build();
    }

    public static Intent getAlertResultScreenWithEventHandlers(final Activity activity,
                                                               @StringRes int header,
                                                               @StringRes int message,
                                                               View.OnClickListener primaryOnClickListener,
                                                               View.OnClickListener secondaryOnClickListener) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToAlert()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultTopButton(R.string.business_banking_go_back, secondaryOnClickListener)
                .setGenericResultBottomButton(R.string.continue_button, primaryOnClickListener)
                .hideGenericResultCallSupport()
                .build();
    }

    public static Intent getFailureResultScreen(final Activity activity, @StringRes int header, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultTopButton(R.string.home, v -> reloadAccountsAndGoHomeShowingAccountsList(activity))
                .hideGenericResultCallSupport()
                .build();
    }

    public static Intent getFailureResultScreen(final Activity activity, @StringRes int header, @StringRes int message, View.OnClickListener onClickListener) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .setGenericResultTopButton(R.string.done, onClickListener)
                .hideGenericResultCallSupport()
                .build();
    }

    public static Intent getUnableToContinueScreen(final Activity activity, @StringRes int header, @StringRes int message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToAlert()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .hideGenericResultCallSupport()
                .setGenericResultTopButton(R.string.close, v -> reloadAccountsAndGoHome(activity))
                .build();
    }

    public static Intent getUnableToContinueScreenWithPrimaryButton(final Activity activity, @StringRes int header, @StringRes int message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToAlert()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .hideGenericResultCallSupport()
                .setGenericResultBottomButton(R.string.close, v -> reloadAccountsAndGoHome(activity))
                .build();
    }

    public static Intent featureUnavailable(final Activity activity, @StringRes int noticeMessage, String subMessage) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(noticeMessage)
                .setGenericResultSubMessage(subMessage)
                .hideGenericResultCallSupport()
                .setGenericResultTopButton(R.string.close, v -> BMBApplication.getInstance().getTopMostActivity().finish())
                .build();
    }

    public static Intent outsideAppCapabilityUnavailable(final Activity activity, @StringRes int noticeMessage, String subMessage) {
        GenericResultActivity.topOnClickListener = v -> {
            BMBApplication.getInstance().getTopMostActivity().finish();
        };
        return (new Intent(activity, GenericResultActivity.class))
                .putExtra(GenericResultActivity.IS_FAILURE, true)
                .putExtra(GenericResultActivity.NOTICE_MESSAGE, noticeMessage)
                .putExtra(GenericResultActivity.SUB_MESSAGE_STRING, subMessage)
                .putExtra(GenericResultActivity.IS_CALL_SUPPORT_GONE, true)
                .putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.close);
    }

    public static Intent featureUnavailable(final Activity activity, @StringRes int subMessage) {
        return capabilityUnavailable(activity, R.string.feature_unavailable, activity.getString(R.string.feature_unavailable_message, activity.getString(subMessage)));
    }

    public static Intent capabilityUnavailable(final Activity activity, @StringRes int noticeMessage, String subMessage) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(noticeMessage)
                .setGenericResultSubMessage(subMessage)
                .hideGenericResultCallSupport()
                .setGenericResultTopButton(R.string.close, v -> reloadAccountsAndGoHome(activity))
                .build();
    }

    public static Intent capabilityUnavailable(final Activity activity, @StringRes int noticeMessage, String subMessage, boolean displayAccountList) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(noticeMessage)
                .setGenericResultSubMessage(subMessage)
                .hideGenericResultCallSupport()
                .setGenericResultTopButton(R.string.close, v -> {
                    if (displayAccountList) {
                        reloadAccountsAndGoHomeShowingAccountsList(activity);
                    } else {
                        reloadAccountsAndGoHome(activity);
                    }
                })
                .build();
    }

    public static Intent getSomethingWentWrongScreen(final Activity activity, @StringRes int header, @StringRes int message) {
        return getSomethingWentWrongScreen(activity, header, activity.getString(message), false);
    }

    private static Intent getSomethingWentWrongScreen(final Activity activity, @StringRes int header, String message, boolean shouldBePrimaryButton) {
        IntentBuilder intent = new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToError()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .hideGenericResultCallSupport();
        if (shouldBePrimaryButton) {
            intent.setGenericResultBottomButton(R.string.done, v -> reloadAccountsAndGoHomeShowingAccountsList(activity));
        } else {
            intent.setGenericResultTopButton(R.string.done, v -> reloadAccountsAndGoHomeShowingAccountsList(activity));
        }
        return intent.build();
    }

    public static IntentBuilder getFailureResultScreenBuilder(final Activity activity, @StringRes int header, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .hideGenericResultCallSupport()
                .setGenericResultHomeButton(activity);
    }

    public static IntentBuilder getNoApplicationToOpenFileError(final Activity activity, @StringRes int header, String message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(header)
                .setGenericResultSubMessage(message)
                .hideGenericResultCallSupport()
                .setFinishActivityOnDone(activity);
    }

    public static Intent getPreLoginFailureScreen(final Activity activity, @StringRes int headerMessage, @StringRes int subMessage) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultHeaderMessage(headerMessage)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(subMessage)
                .setGenericResultBottomButton(R.string.close, view -> CommonUtils.callWelcomeActivity(activity))
                .build();
    }

    public static Intent getPartialRegistrationFailureScreen(final Activity activity, @StringRes int headerMessage, String subMessage) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultHeaderMessage(headerMessage)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(subMessage)
                .setGenericResultBottomButton(R.string.close, view -> CommonUtils.callWelcomeActivity(activity))
                .build();
    }

    public static Intent getSecondaryCardRegistrationFailureScreen(final Activity activity) {
        return getGenericResultFailureBuilder(activity)
                .setGenericResultHeaderMessage(R.string.secondary_card_registration_failed_title)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(R.string.secondary_card_registration_failed_message)
                .setGenericResultBottomButton(R.string.done, view -> CommonUtils.callWelcomeActivity(activity))
                .build();
    }

    public static Intent getSimplifiedLogonActivity(Context context, UserProfile userProfile) {
        return new IntentBuilder()
                .setClass(context, SimplifiedLoginActivity.class)
                .singleTop()
                .addExtra(EXTRA_USER_PROFILE, userProfile)
                .build();
    }

    public static Intent goBackToCreditCardHub(Context context, String creditCardNumber) {
        return new IntentBuilder()
                .setClass(context, CreditCardHubActivity.class)
                .addExtra("ACCOUNT_NUMBER", creditCardNumber)
                .clearTop()
                .build();
    }

    public static Intent goBackToCardDetailScreen(Context context, String cardNumber) {
        return new IntentBuilder()
                .setClass(context, CardListActivity.class)
                .addExtra("CARD_NUMBER", cardNumber)
                .addExtra("CARD_TYPE", "Credit")
                .singleTop()
                .build();
    }

    public static final class IntentBuilder {
        Intent intent;

        public IntentBuilder() {
            intent = new Intent();
            clearTop();
        }

        public IntentBuilder(Intent intent) {
            this.intent = intent;
            clearTop();
        }

        public IntentBuilder setGenericResultHeaderMessage(@StringRes int resid) {
            this.intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, resid);
            return this;
        }

        public IntentBuilder setGenericResultHeaderMessage(String headerMessage) {
            this.intent.putExtra(GenericResultActivity.NOTICE_MESSAGE_STRING, headerMessage);
            return this;
        }

        IntentBuilder setGenericResultIconToPaymentSuccessful() {
            intent.putExtra(GenericResultActivity.IS_PAYMENT_SUCCESS, true);
            return this;
        }

        public IntentBuilder setGenericResultIconToSuccessful() {
            intent.putExtra(GenericResultActivity.IS_SUCCESS, true);
            return this;
        }

        public IntentBuilder setGenericResultIconToFailure() {
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
            return this;
        }

        public IntentBuilder setGenericResultIconToAlert() {
            intent.putExtra(GenericResultActivity.IS_GENERAL_ALERT, true);
            return this;
        }

        IntentBuilder setGenericResultIconToWaitingState() {
            intent.putExtra(GenericResultActivity.IS_WAITING_STATE, true);
            return this;
        }

        public IntentBuilder setGenericResultIconToError() {
            intent.putExtra(GenericResultActivity.IS_ERROR, true);
            return this;
        }

        IntentBuilder setGenericResultIconToCustomAnimation(String customAnimation) {
            intent.putExtra(GenericResultActivity.CUSTOM_FAILURE_ANIMATION, customAnimation);
            return this;
        }

        IntentBuilder setGenericResultIcon(int iconResId) {
            this.intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, iconResId);
            return this;
        }

        public IntentBuilder setGenericResultHeaderMessageFontSize() {
            this.intent.putExtra(GenericResultActivity.HEADER_MESSAGE_FONT_SIZE, 24);
            return this;
        }

        public IntentBuilder setGenericResultsSubMessageFontSize() {
            this.intent.putExtra(GenericResultActivity.SUB_MESSAGE_FONT_SIZE, 16);
            return this;
        }

        IntentBuilder setMakeContactClickableFlag() {
            this.intent.putExtra(GenericResultActivity.SHOULD_MAKE_CONTACT_CLICKABLE, true);
            return this;
        }

        public IntentBuilder setGenericResultSubMessage(String message) {
            this.intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, message);
            return this;
        }

        public IntentBuilder setGenericResultSubMessage(@StringRes int resid) {
            this.intent.putExtra(GenericResultActivity.SUB_MESSAGE, resid);
            return this;
        }

        public IntentBuilder setIsFromCardHub(boolean isFromCardHub) {
            this.intent.putExtra(CreditCardHubActivity.IS_FROM_CARD_HUB, isFromCardHub);
            return this;
        }

        public IntentBuilder setGenericResultTopButton(@StringRes int stringResourceId, View.OnClickListener onClickListener) {
            this.intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, stringResourceId);
            GenericResultActivity.topOnClickListener = onClickListener;
            return this;
        }

        public IntentBuilder setGenericResultBottomButton(@StringRes int stringResourceId, View.OnClickListener onClickListener) {
            this.intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, stringResourceId);
            GenericResultActivity.bottomOnClickListener = onClickListener;
            return this;
        }

        public IntentBuilder setGenericResultDoneButton(final Activity activity, View.OnClickListener onClickListener) {
            return setGenericResultDoneButton(activity, onClickListener, false);
        }

        public IntentBuilder setGenericResultDoneButton(final Activity activity, View.OnClickListener onClickListener, boolean shouldShowStoreRatingDialog) {
            this.intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);

            if (intent.getExtras() != null && intent.getExtras().getBoolean(GenericResultActivity.IS_FAILURE, false)) {
                shouldShowStoreRatingDialog = false;
            }

            if (shouldShowStoreRatingDialog && activity instanceof BaseActivity) {
                GenericResultActivity.bottomOnClickListener = v -> {
                    if (shouldShowAppRatingDialog()) {
                        showAppRatingDialog(onClickListener);
                    } else {
                        onClickListener.onClick(v);
                    }
                };
            } else {
                GenericResultActivity.bottomOnClickListener = onClickListener;
            }
            return this;
        }

        public IntentBuilder setGenericResultDoneButton(final Activity activity) {
            return setGenericResultHomeButton(activity, R.string.done);
        }

        public IntentBuilder setGenericResultDoneTopButton(View.OnClickListener onClickListener) {
            this.intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.done);
            GenericResultActivity.topOnClickListener = onClickListener;
            return this;
        }

        IntentBuilder setGenericResultHomeButton(final Activity activity, @StringRes int stringResId) {
            this.intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, stringResId);
            GenericResultActivity.bottomOnClickListener = v -> {

                boolean shouldShowAppRating = true;
                if (intent.getExtras() != null && intent.getExtras().getBoolean(GenericResultActivity.IS_FAILURE, false)) {
                    shouldShowAppRating = false;
                }

                if (shouldShowAppRating && activity instanceof BaseActivity && shouldShowAppRatingDialog()) {
                    activity.finish();
                    showAppRatingDialog(view -> reloadAccountsAndGoHomeShowingAccountsList(activity));
                } else {
                    reloadAccountsAndGoHomeShowingAccountsList(activity);
                }
            };
            return this;
        }

        public IntentBuilder setGenericResultHomeButton(final Activity activity) {
            this.intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
            return setGenericResultHomeButton(activity, R.string.home);
        }

        public IntentBuilder setGenericResultHomeButtonTop(final Activity activity) {
            this.intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.home);
            return setGenericResultTopButton(R.string.home, v -> reloadAccountsAndGoHomeShowingAccountsList(activity));
        }

        public IntentBuilder hideGenericResultCallSupport() {
            this.intent.putExtra(GenericResultActivity.IS_CALL_SUPPORT_GONE, true);
            return this;
        }

        IntentBuilder clearTop() {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return this;
        }

        IntentBuilder singleTop() {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return this;
        }

        public IntentBuilder setFinishOnBackPressed() {
            intent.putExtra(ONBACKPRESSED_FINISH, true);
            return this;
        }

        public IntentBuilder setAccountObject(AccountObject accountObject) {
            intent.putExtra(ACCOUNT_OBJECT, accountObject);
            return this;
        }

        public IntentBuilder setPersonalLoanObject(PersonalLoanHubInformation personalLoanObject) {
            intent.putExtra(PERSONAL_LOAN_OBJECT, (Parcelable) personalLoanObject);
            return this;
        }

        IntentBuilder setShouldRefreshAccountList(boolean shouldRefreshAccountList) {
            AbsaCacheManager.getInstance().setAccountsCacheStatus(!shouldRefreshAccountList);
            return this;
        }

        public IntentBuilder setAccountNumber(String accountNumber) {
            intent.putExtra(ACCOUNT_NUMBER, accountNumber);
            return this;
        }

        public IntentBuilder setFromDate(Date fromDate) {
            intent.putExtra(FROM_DATE, fromDate);
            return this;
        }

        public IntentBuilder setToDate(Date fromDate) {
            intent.putExtra(TO_DATE, fromDate);
            return this;
        }

        public IntentBuilder setClass(Context context, Class<?> target) {
            intent.setClass(context, target);
            return this;
        }

        public Intent build() {
            return intent;
        }

        IntentBuilder addExtra(String extraName, Serializable extraValue) {
            intent.putExtra(extraName, extraValue);
            return this;
        }

        public IntentBuilder setFinishActivityOnDone(Activity activity) {
            setGenericResultDoneButton(activity, (v) -> {
                BMBApplication.getInstance().getTopMostActivity().finish();
                activity.finish();
            });
            return this;
        }
    }

    private static void reloadAccountsAndGoHomeShowingAccountsList(Activity activity) {
        AbsaCacheManager.getInstance().setAccountsCacheStatus(false);
        ((BaseActivity) activity).loadAccountsAndShowHomeScreenWithAccountsList();
    }

    private static void showAppRatingDialog(View.OnClickListener onClickListener) {
        BaseActivity topMostActivity = (BaseActivity) BMBApplication.getInstance().getTopMostActivity();
        PlayStoreRatingDialogFragment playStoreRatingDialogFragment = PlayStoreRatingDialogFragment.newInstance();
        playStoreRatingDialogFragment.setNextAction(onClickListener);
        FragmentTransaction transaction = topMostActivity.getSupportFragmentManager().beginTransaction().add(playStoreRatingDialogFragment, "infoDialog");
        if (!topMostActivity.commitFragmentSafely(transaction)) {
            topMostActivity.finish();
        }
    }

    private static boolean shouldShowAppRatingDialog() {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

        FeatureSwitching featureSwitching = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        if (appCacheService.isPrimarySecondFactorDevice() && featureSwitching.getAppRating() == FeatureSwitchingStates.ACTIVE.getKey()) {
            long lastRatedDateMillis = SharedPreferenceService.INSTANCE.getAppLastRatedOn();
            return lastRatedDateMillis == -1 || DateUtils.givenDateMillisIsMoreThanGivenDaysAgo(lastRatedDateMillis, 91);
        }
        return false;
    }

}