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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransaction;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionDetails;
import com.barclays.absa.banking.dualAuthorisations.services.AuthorisationTransactionDetailsRequest;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.util.ArrayList;
import java.util.List;

import styleguide.content.SecondaryContentAndLabelView;

import static com.barclays.absa.banking.dualAuthorisations.ui.DualAuthTransactionDetailsCashSendActivity.IS_CASH_SEND_PLUS;
import static com.barclays.absa.banking.dualAuthorisations.ui.DualAuthTransactionDetailsCashSendActivity.TRANSACTION_DETAILS;
import static com.barclays.absa.banking.framework.BaseActivity.mScreenName;
import static com.barclays.absa.banking.framework.BaseActivity.mSiteSection;
import static com.barclays.absa.banking.framework.app.BMBConstants.AUTHORISATION_TRANSACTION_DETAILS;
import static com.barclays.absa.banking.framework.app.BMBConstants.DUAL_AUTHORISATION;

public class AuthorisationsListAdapter extends RecyclerView.Adapter<AuthorisationsListAdapter.BindingHolder> implements Filterable {
    private final Context context;
    private List<AuthorisationTransaction> authorisationTransactions;
    private ArrayList<AuthorisationTransaction> originalAuthorisationList;
    private AuthorisationTransaction currentTransaction;

    private final ExtendedResponseListener<AuthorisationTransactionDetails> authorisationTransactionDetailsResponseListener = new ExtendedResponseListener<AuthorisationTransactionDetails>() {
        @Override
        public void onSuccess(final AuthorisationTransactionDetails successResponse) {
            ((BaseActivity) context).dismissProgressDialog();
            mScreenName = AUTHORISATION_TRANSACTION_DETAILS;
            mSiteSection = DUAL_AUTHORISATION;
            AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
            boolean isCashSendPlus = false;
            AuthorisationTransaction authorisationTransaction = currentTransaction;
            if (authorisationTransaction.getTransactionCategoryType() != null) {
                Class nextActivity;
                switch (authorisationTransaction.getTransactionCategoryType()) {
                    case PAY_BENEFICIARY:
                    case IMMEDIATE_INTERBANK_PAYMENT:
                    case PAY_ONCE_OFF:
                        nextActivity = DualAuthTransactionDetailsPaymentActivity.class;
                        break;
                    case INTER_ACCOUNT_TRANSFER:
                        nextActivity = DualAuthTransactionDetailsTransferActivity.class;
                        break;
                    case PREPAID:
                        nextActivity = DualAuthTransactionDetailsPrepaidActivity.class;
                        break;
                    case CASH_SEND:
                        nextActivity = DualAuthTransactionDetailsCashSendActivity.class;
                        break;
                    case CASH_SEND_PLUS:
                    case CASH_SEND_PLUS_REGISTRATION:
                    case CASH_SEND_PLUS_DE_REGISTRATION:
                    case DELETE_CASH_SEND_PLUS:
                    case CASH_SEND_PLUS_LIMITS:
                        nextActivity = DualAuthTransactionDetailsCashSendActivity.class;
                        isCashSendPlus = true;
                        break;
                    default:
                        nextActivity = context.getClass();
                        break;
                }

                if (currentTransaction != null) {
                    if (successResponse.getCellNumber() != null && successResponse.getCellNumber().startsWith("27")) {
                        successResponse.setCellNumber(successResponse.getCellNumber().replace("27", "0"));
                    }
                    successResponse.setOperatorName(currentTransaction.getOperatorName());
                }

                Intent transactionDetails = new Intent(context, nextActivity);
                transactionDetails.putExtra(IS_CASH_SEND_PLUS, isCashSendPlus);
                transactionDetails.putExtra(TRANSACTION_DETAILS, successResponse);
                context.startActivity(transactionDetails);
            }
        }
    };

    AuthorisationsListAdapter(Context context, List<AuthorisationTransaction> authorisationTransactions) {
        this.context = context;
        authorisationTransactionDetailsResponseListener.setView(((BaseActivity) context));
        this.authorisationTransactions = authorisationTransactions;
    }

    @NonNull
    @Override
    public AuthorisationsListAdapter.BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.authorisation_list_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(AuthorisationsListAdapter.BindingHolder bindingHolder, int position) {
        AuthorisationTransaction authTransactionViewModel = authorisationTransactions.get(position);
        String transactionTypeAndAmount = String.format("%s - %s", authTransactionViewModel.getTransactionType(), authTransactionViewModel.getAmount());
        String operatorName = authTransactionViewModel.getOperatorName();
        String contentSubTitle = authTransactionViewModel.getOperatorName();

        if (authTransactionViewModel.getTransactionCategoryType() != null) {
            switch (authTransactionViewModel.getTransactionCategoryType()) {
                case CASH_SEND_PLUS_REGISTRATION:
                    transactionTypeAndAmount = context.getString(R.string.cash_send_plus_title);
                    contentSubTitle = String.format("%s - %s", context.getString(R.string.cash_send_plus_registration_label), operatorName);
                    break;
                case CASH_SEND_PLUS_DE_REGISTRATION:
                    transactionTypeAndAmount = context.getString(R.string.cash_send_plus_title);
                    contentSubTitle = String.format("%s - %s", context.getString(R.string.cash_send_plus_cancellation), operatorName);
                    break;
                case CASH_SEND_PLUS_LIMITS:
                    transactionTypeAndAmount = context.getString(R.string.cash_send_plus_title);
                    contentSubTitle = context.getString(R.string.manage_limits);
                    break;
            }
        }

        bindingHolder.contentHolder.setLabelText(contentSubTitle);
        bindingHolder.contentHolder.setContentText(transactionTypeAndAmount);
        bindingHolder.authorisationsContainer.setOnClickListener(v -> {
            currentTransaction = authTransactionViewModel;
            AuthorisationTransactionDetailsRequest<AuthorisationTransactionDetails> transactionDetailsRequest =
                    new AuthorisationTransactionDetailsRequest<>(authTransactionViewModel.getTransactionTypeCode(),
                            authTransactionViewModel.getTransactionDate(), authorisationTransactionDetailsResponseListener);
            ServiceClient serviceClient = new ServiceClient(transactionDetailsRequest);
            serviceClient.submitRequest();
        });
    }

    public void refreshItemList(List<AuthorisationTransaction> authorisationTransactions) {
        this.authorisationTransactions = authorisationTransactions;
        if (originalAuthorisationList != null) {
            originalAuthorisationList = new ArrayList<>(authorisationTransactions);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return authorisationTransactions == null ? 0 : authorisationTransactions.size();
    }

    static class BindingHolder extends RecyclerView.ViewHolder {

        SecondaryContentAndLabelView contentHolder;
        ConstraintLayout authorisationsContainer;

        BindingHolder(View view) {
            super(view);
            contentHolder = view.findViewById(R.id.creditorNameContentView);
            authorisationsContainer = view.findViewById(R.id.authorisationsContainer);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                ArrayList<AuthorisationTransaction> results = new ArrayList<>();
                if (constraint != null) {
                    if (originalAuthorisationList == null) {
                        originalAuthorisationList = (ArrayList<AuthorisationTransaction>) authorisationTransactions;
                    }
                    if (originalAuthorisationList != null && originalAuthorisationList.size() > 0) {
                        for (final AuthorisationTransaction transactionItem : originalAuthorisationList) {
                            if (transactionItem.getTransactionType().toLowerCase().contains(constraint.toString().toLowerCase()) || transactionItem.getAuthorisedUserName().toLowerCase().contains(constraint.toString().toLowerCase()) || transactionItem.getOperatorName() != null && transactionItem.getOperatorName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                results.add(transactionItem);
                            }
                        }
                    }
                    filterResults.values = results;
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                authorisationTransactions = (List<AuthorisationTransaction>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
