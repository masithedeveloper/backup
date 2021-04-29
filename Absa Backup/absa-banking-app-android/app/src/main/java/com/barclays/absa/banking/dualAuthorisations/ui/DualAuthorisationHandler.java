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
package com.barclays.absa.banking.dualAuthorisations.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.databinding.ObservableBoolean;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionAcceptReject;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionDetails;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionList;
import com.barclays.absa.banking.dualAuthorisations.services.AuthorisationTransactionAcceptRejectRequest;
import com.barclays.absa.banking.dualAuthorisations.services.AuthorisationTransactionListRequest;
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthAuthorisationsPendingActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.framework.BaseActivity.mScreenName;
import static com.barclays.absa.banking.framework.BaseActivity.mSiteSection;
import static com.barclays.absa.banking.framework.app.BMBConstants.AUTHORISATION_CONFIRM_TRANSACTION;
import static com.barclays.absa.banking.framework.app.BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION;
import static com.barclays.absa.banking.framework.app.BMBConstants.DUAL_AUTHORISATION;
import static com.barclays.absa.banking.framework.app.BMBConstants.FAILURE;
import static com.barclays.absa.banking.framework.app.BMBConstants.SECOND_AUTHORISATION_REQUIRED;
import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;
import static com.barclays.absa.banking.framework.app.BMBConstants.TRANSACTION_AUTHORISED;
import static com.barclays.absa.banking.framework.app.BMBConstants.TRANSACTION_PENDING;
import static com.barclays.absa.banking.framework.app.BMBConstants.UNABLE_TO_AUTHORISE;

public class DualAuthorisationHandler {

    private static final String AUTHORISE_RESULT = "authorise result";
    private static final String ACCEPT = "accept";
    private static final String REJECT = "reject";
    private final ObservableBoolean authorisedClicked = new ObservableBoolean();
    private BaseActivity barclaysView;
    private AuthorisationTransactionDetails transactionDetails;
    private boolean isViewAuthClicked = true;
    private boolean isAuthorisationButtonClicked = true;
    private boolean isRejectButtonClicked = true;
    private boolean isCancelClicked = true;

    private final ExtendedResponseListener<AuthorisationTransactionList> getAuthorisationTransactionListResponseListener = new ExtendedResponseListener<AuthorisationTransactionList>(barclaysView) {
        @Override
        public void onSuccess(final AuthorisationTransactionList authorisationTransactionList) {
            barclaysView.dismissProgressDialog();
            barclaysView.finish();

            if (isViewAuthClicked) {
                Intent dualAuthHubActivityIntent = new Intent(barclaysView, AuthorisationHubActivity.class);
                barclaysView.startActivity(dualAuthHubActivityIntent);
            } else if (isAuthorisationButtonClicked) {
                String header = barclaysView.getString(R.string.string_authorized, transactionDetails.getTransactionType());
                Intent successIntent = IntentFactory.getSuccessfulResultScreen(barclaysView, header, "", true);
                barclaysView.startActivity(successIntent);
            } else if (isRejectButtonClicked) {
                Intent successIntent = IntentFactory.getSuccessfulResultScreen(barclaysView, R.string.transaction_rejected, "");
                barclaysView.startActivity(successIntent);
            } else if (isCancelClicked) {
                Intent successIntent = IntentFactory.getSuccessfulResultScreen(barclaysView, R.string.transaction_cancelled, "");
                barclaysView.startActivity(successIntent);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            barclaysView.dismissProgressDialog();
        }
    };

    private final ExtendedResponseListener<AuthorisationTransactionAcceptReject> authorisationTransactionAcceptRejectResponseListener = new ExtendedResponseListener<AuthorisationTransactionAcceptReject>() {

        @Override
        public void onSuccess(final AuthorisationTransactionAcceptReject transactionAcceptReject) {
            if (transactionAcceptReject != null) {
                if (SUCCESS.equalsIgnoreCase(transactionAcceptReject.getTransactionStatus())) {
                    String transactionAcceptRejectMessage = transactionAcceptReject.getTransactionMessage();
                    if (AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(transactionAcceptRejectMessage) || SECOND_AUTHORISATION_REQUIRED.equalsIgnoreCase(transactionAcceptRejectMessage)) {
                        barclaysView.dismissProgressDialog();
                        mScreenName = TRANSACTION_PENDING;
                        mSiteSection = DUAL_AUTHORISATION;
                        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
                        startResultActivity(transactionAcceptReject, DualAuthAuthorisationsPendingActivity.class);

                    } else if (ACCEPT.equalsIgnoreCase(transactionAcceptReject.getAuthorisationStatusCode())) {
                        barclaysView.dismissProgressDialog();
                        mScreenName = TRANSACTION_AUTHORISED;
                        mSiteSection = DUAL_AUTHORISATION;
                        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
                        String header = barclaysView.getString(R.string.string_authorized, transactionDetails.getTransactionType());
                        Intent successIntent = IntentFactory.getSuccessfulResultScreen(barclaysView, header, "", true);
                        barclaysView.startActivity(successIntent);
                    } else if (REJECT.equalsIgnoreCase(transactionAcceptReject.getAuthorisationStatusCode())) {
                        requestAuthorizationList();
                    }
                } else if (FAILURE.equalsIgnoreCase(transactionAcceptReject.getTransactionStatus())) {
                    barclaysView.dismissProgressDialog();
                    if (barclaysView.getString(R.string.transfer_authorization_response_message).equalsIgnoreCase(transactionAcceptReject.getTransactionMessage())) {
                        mScreenName = TRANSACTION_AUTHORISED;
                        mSiteSection = DUAL_AUTHORISATION;
                        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
                        String header = barclaysView.getString(R.string.string_authorized, transactionDetails.getTransactionType());
                        Intent successIntent = IntentFactory.getSuccessfulResultScreen(barclaysView, header, "", true);
                        barclaysView.startActivity(successIntent);
                    } else {
                        mScreenName = UNABLE_TO_AUTHORISE;
                        mSiteSection = DUAL_AUTHORISATION;
                        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
                        Intent failureIntent = IntentFactory.getFailureResultScreen(barclaysView, R.string.cash_send_unable_to_auth_header, ResponseObject.extractErrorMessage(transactionAcceptReject));
                        barclaysView.startActivity(failureIntent);
                    }
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            barclaysView.dismissProgressDialog();
            displayRejectionConfirmationDialog();
        }
    };

    private DualAuthorisationHandler(BaseActivity barclaysView) {
        this.barclaysView = barclaysView;
        getAuthorisationTransactionListResponseListener.setView(barclaysView);
        authorisationTransactionAcceptRejectResponseListener.setView(barclaysView);
    }

    DualAuthorisationHandler(BaseActivity barclaysView, AuthorisationTransactionDetails transactionDetails) {
        this(barclaysView);
        this.transactionDetails = transactionDetails;
    }

    void onAuthoriseClick(View view) {
        if (view instanceof Button) {
            isAuthorisationButtonClicked = true;
            isCancelClicked = false;
            isViewAuthClicked = false;
            isRejectButtonClicked = false;
            mScreenName = AUTHORISATION_CONFIRM_TRANSACTION;
            mSiteSection = DUAL_AUTHORISATION;
            AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
            authorisedClicked.set(true);
            authorisationDecisionRequest(ACCEPT);
        }
    }

    void onRejectionClick(View view) {
        if (view instanceof Button) {
            isAuthorisationButtonClicked = false;
            isCancelClicked = false;
            isViewAuthClicked = false;
            isRejectButtonClicked = true;
            displayRejectionConfirmationDialog();
        }
    }

    void onCancelClick(View view) {
        if (view instanceof Button) {
            isAuthorisationButtonClicked = false;
            isRejectButtonClicked = false;
            isViewAuthClicked = false;
            isCancelClicked = true;
            displayRejectionConfirmationDialog();
        }
    }

    void onConfirmationClick(View view) {
        if (view instanceof Button) {
            authorisationDecisionRequest(ACCEPT);
        }
    }

    public void onHomeClick(View view) {
        if (view instanceof Button) {
            AbsaCacheManager.getInstance().setAccountsCacheStatus(false);
            barclaysView.loadAccountsAndGoHome();
        }
    }

    public void onViewAuthorisationsClick(View view) {
        if (view instanceof Button) {
            isViewAuthClicked = true;
            AbsaCacheManager.getInstance().setAccountsCacheStatus(false);
            requestAuthorizationList();
        }
    }

    public void onBackPressed() {
        if (authorisedClicked.get()) {
            authorisedClicked.set(false);
            isRejectButtonClicked = true;
        } else {
            barclaysView.finish();
        }
    }

    public int setMainUserView() {
        return AccessPrivileges.getInstance().isOperator() ? View.GONE : View.VISIBLE;
    }

    public int setCancelButtonVisibility() {
        return AccessPrivileges.getInstance().isOperator() ? View.VISIBLE : View.GONE;
    }

    int setIIPReferenceVisibility() {
        return BMBConstants.IIP_CONST.equals(transactionDetails.getTransactionTypeCode()) ? View.VISIBLE : View.GONE;
    }

    String beneficiaryNoticeOfPayment() {
        return TextUtils.isEmpty(transactionDetails.getBeneficiaryNoticeDetail()) ?
                barclaysView.getString(R.string.none) : transactionDetails.getBeneficiaryNoticeDetail();
    }

    String myNoticeOfPayment() {
        return TextUtils.isEmpty(transactionDetails.getMyNoticeDetail()) ?
                barclaysView.getString(R.string.none) : transactionDetails.getMyNoticeDetail();
    }

    private String getFormattedAccountNumber(String accountNumber) {
        return StringExtensions.toFormattedAccountNumber(accountNumber.trim());
    }

    String getDebitDate() {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat("dd MMMM, yyyy", BMBApplication.getApplicationLocale());
        try {
            builder.append(DateUtils.getFormattedDate(transactionDetails.getDebitDateTime(), sourceDateFormat, requiredDateFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    String getFromAccountTypeAndNumber() {
        return getAccountTypeAndNumber(transactionDetails.getFromAccount());
    }

    String getToAccountTypeAndNumber() {
        return getAccountTypeAndNumber(transactionDetails.getToAccount());
    }

    private String getAccountTypeAndNumber(String account) {
        StringBuilder builder = new StringBuilder();
        AccountList cachedAccountListObject = AbsaCacheManager.getInstance().getCachedAccountListObject();
        if (cachedAccountListObject != null && cachedAccountListObject.getAccountsList() != null) {
            for (AccountObject accountObject : cachedAccountListObject.getAccountsList()) {
                if (accountObject.getAccountNumber().equals(account)) {
                    builder.append(accountObject.getDescription()).append("\n").append(getFormattedAccountNumber(account));
                }
            }
        }
        return builder.toString();
    }

    private void displayRejectionConfirmationDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(AccessPrivileges.getInstance().isOperator() ? BMBApplication.getInstance().getTopMostActivity().getString(R.string.dual_authorisation_cancel_transaction) : BMBApplication.getInstance().getTopMostActivity().getString(R.string.reject_transaction))
                .positiveDismissListener((dialog, which) -> authorisationDecisionRequest(REJECT)));
    }

    private void authorisationDecisionRequest(String authorisationDecision) {
        authorisationTransactionAcceptRejectResponseListener.setView(barclaysView);
        AuthorisationTransactionAcceptRejectRequest<AuthorisationTransactionAcceptReject> transactionAcceptRejectRequest =
                new AuthorisationTransactionAcceptRejectRequest<>(transactionDetails.getTransactionType(),
                        transactionDetails.getTransactionTypeCode(), authorisationDecision,
                        transactionDetails.getTransactionDateTime(),
                        authorisationTransactionAcceptRejectResponseListener);
        ServiceClient serviceClient = new ServiceClient(transactionAcceptRejectRequest);
        serviceClient.submitRequest();
    }

    private void requestAuthorizationList() {
        getAuthorisationTransactionListResponseListener.setView(barclaysView);
        AuthorisationTransactionListRequest<AuthorisationTransactionList> getAuthorisationTransactionListRequest
                = new AuthorisationTransactionListRequest<>(getAuthorisationTransactionListResponseListener);
        ServiceClient serviceClient = new ServiceClient(getAuthorisationTransactionListRequest);
        serviceClient.submitRequest();
    }

    private void startResultActivity(AuthorisationTransactionAcceptReject transactionAcceptReject, Class nextActivity) {
        Intent authorisationAcceptedIntent = new Intent(barclaysView, nextActivity);
        authorisationAcceptedIntent.putExtra(AUTHORISE_RESULT, transactionAcceptReject);
        barclaysView.startActivity(authorisationAcceptedIntent);
    }
}
