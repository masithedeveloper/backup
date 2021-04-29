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
package com.barclays.absa.banking.payments.multiple;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper;
import com.barclays.absa.banking.databinding.ActivitySelectAccountToPayFromMultiplePaymentBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.FilterAccountList;

import java.util.ArrayList;

import styleguide.forms.SelectorList;

@Deprecated
public class MultiplePaymentsAccountSelectorActivity extends BaseActivity implements SelectAccountToPayFromMultiplePaymentView {

    private ActivitySelectAccountToPayFromMultiplePaymentBinding binding;
    private ArrayList<AccountObject> fromAccountList;
    private AccountObject selectedAccount;
    private SelectAccountToPayFromMultiplePaymentPresenter presenter;
    public static final String SELECTED_ACCOUNT = "SELECTED_ACCOUNT";
    static String CLIENT_AGREEMENT_DETAILS = "clientAgreementDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance().trackCustomScreenView("Select account", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_select_account_to_pay_from_multiple_payment, null, false);
        setContentView(binding.getRoot());

        presenter = new SelectAccountToPayFromMultiplePaymentPresenter(this);
        setUpView();
    }

    private void setUpView() {
        setToolBarBack(R.string.select_account_toolbar_title);

        if (getIntent().getExtras() != null) {
            fromAccountList = (ArrayList<AccountObject>) getIntent().getSerializableExtra(MultipleBeneficiarySelectionActivity.FROM_ACCOUNTS);
        }

        AccountList accountList = new AccountList();
        accountList.setAccountsList(fromAccountList);
        setFromAccountView(accountList);

        binding.nextButton.setOnClickListener(v -> presenter.onNextButtonClicked());
    }

    @SuppressWarnings("unchecked")
    protected void setFromAccountView(AccountList accountList) {
        if (accountList != null) {
            ArrayList<AccountObject> transactionalAccounts = FilterAccountList.getTransactionalAndCreditAccounts(accountList.getAccountsList());
            if (transactionalAccounts != null) {
                SelectorList<AccountObjectWrapper> accountObjectWrappers = new SelectorList<>();
                for (AccountObject accountObject : transactionalAccounts) {
                    accountObjectWrappers.add(new AccountObjectWrapper(accountObject));
                }
                binding.accountInputView.setList(accountObjectWrappers, getString(R.string.select_account_toolbar_title));
                binding.accountInputView.setItemSelectionInterface(index -> setFromAccount(accountObjectWrappers.get(index)));
            } else {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
            }
        }
    }

    private void setFromAccount(Object accountInfo) {
        if (accountInfo instanceof AccountObjectWrapper) {
            selectedAccount = ((AccountObjectWrapper) accountInfo).getAccountObject();
        } else if (accountInfo instanceof AccountObject) {
            selectedAccount = (AccountObject) accountInfo;
        }

        if (selectedAccount != null) {
            binding.accountInputView.setText(getFromAccountString());
            binding.nextButton.setEnabled(true);
        }
    }

    private String getFromAccountString() {
        return selectedAccount == null ? "" : selectedAccount.getAccountInformation();
    }

    @Override
    public void navigateToMultipleBeneficiaryDetailScreen() {
        presenter.clientAgreementDetails();
    }

    @Override
    public void navigateToMultipleBeneficiaryDetailScreen(ClientAgreementDetails clientAgreementDetails) {
       /* Intent intent = new Intent(this, MultiplePaymentsSelectedBeneficiariesActivity.class);
        intent.putExtra(SELECTED_ACCOUNT, selectedAccount);
        intent.putExtra(CLIENT_AGREEMENT_DETAILS, clientAgreementDetails);
        intent.putExtra(MultipleBeneficiarySelectionActivity.SELECTED_BENEFICIARY_LIST, getIntent().getSerializableExtra(MultipleBeneficiarySelectionActivity.SELECTED_BENEFICIARY_LIST));
        startActivity(intent);*/
    }
}