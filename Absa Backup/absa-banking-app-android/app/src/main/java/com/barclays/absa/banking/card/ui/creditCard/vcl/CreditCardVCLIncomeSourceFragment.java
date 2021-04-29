/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.AccountType;
import com.barclays.absa.banking.boundary.model.BankBranches;
import com.barclays.absa.banking.boundary.model.BankDetails;
import com.barclays.absa.banking.boundary.model.Branch;
import com.barclays.absa.banking.boundary.model.UniversalBank;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.CreditCardVclIncomeSourceFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.ChooseBankListActivity;
import com.barclays.absa.banking.payments.ChooseBranchListActivity;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.presentation.transactions.SelectAccountTypeActivity;
import com.barclays.absa.banking.transfer.AccountListItem;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.FilterAccountList;
import com.barclays.absa.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.payments.PaymentsConstants.BANK_LIST;

public class CreditCardVCLIncomeSourceFragment extends BaseVCLFragment<CreditCardVclIncomeSourceFragmentBinding>
        implements View.OnClickListener, CreditCardVCLIncomeSourceView {

    private CreditCardVCLIncomeSourcePresenter presenter;
    private String bankName;
    private String branchCode;

    public CreditCardVCLIncomeSourceFragment() {
        presenter = new CreditCardVCLIncomeSourcePresenter(this, new PaymentsInteractor());
    }

    public static CreditCardVCLIncomeSourceFragment newInstance() {
        return new CreditCardVCLIncomeSourceFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.credit_card_vcl_income_source_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnalyticsUtil.INSTANCE.trackAction("Credit Card VCL Source of income");
        getParentActivity().setOnBackPressedListener(new BaseBackPressedListener(getParentActivity()) {
            @Override
            public void doBack() {
                updateVCLDataFromViews();
            }
        });

        getParentActivity().setToolbarText(getString(R.string.vcl_income_verification_toolbar_title), true);
        getParentActivity().setCurrentProgress(2);
        binding.continueButton.setOnClickListener(this);
        setupOnClickListeners();
        setInputValidation();
        populateView();
    }

    private void populateView() {
        VCLParcelableModel vclDataModel = getParentActivity().getVCLDataModel();
        if (vclDataModel != null) {
            if (!TextUtils.isEmpty(vclDataModel.getBank())) {
                onBankResult(vclDataModel.getBank());
            }

            if (!TextUtils.isEmpty(vclDataModel.getBank())) {
                binding.bankInputView.setSelectedValue(vclDataModel.getBank());
            }

            if (!TextUtils.isEmpty(vclDataModel.getBranchCode())) {
                binding.branchInputView.setSelectedValue(vclDataModel.getBranchCode());
            }

            if (!TextUtils.isEmpty(vclDataModel.getAccountNumber())) {
                binding.accountNumberInputView.setSelectedValue(vclDataModel.getAccountNumber());
            }

            if (!TextUtils.isEmpty(vclDataModel.getEmailAddress())) {
                binding.emailInputView.setSelectedValue(vclDataModel.getEmailAddress());
            }

            if (!TextUtils.isEmpty(vclDataModel.getAccountType())) {
                binding.selectAccountTypeInputView.setSelectedValue(vclDataModel.getAccountType());
            }

            binding.acceptAccessCheckBox.setChecked(vclDataModel.isTermsAccepted());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.bankInputView.showError(false);
        binding.branchInputView.showError(false);
        binding.accountNumberInputView.showError(false);
        binding.emailInputView.showError(false);
        binding.selectAccountTypeInputView.showError(false);
    }

    private void setInputValidation() {
        binding.bankInputView.addValueViewTextWatcher(new TextChangeListener(binding.bankInputView));
        binding.branchInputView.addValueViewTextWatcher(new TextChangeListener(binding.branchInputView));
        binding.accountNumberInputView.addValueViewTextWatcher(new TextChangeListener(binding.accountNumberInputView));
        binding.emailInputView.addValueViewTextWatcher(new TextChangeListener(binding.emailInputView));
        binding.selectAccountTypeInputView.addValueViewTextWatcher(new TextChangeListener(binding.selectAccountTypeInputView));
    }

    private boolean isAllFieldsValid() {
        return (!binding.acceptAccessCheckBox.isChecked() && !binding.bankInputView.getSelectedValue().isEmpty())
                || (!binding.bankInputView.getSelectedValue().isEmpty() && !binding.acceptAccessCheckBox.isChecked() && !ABSA.equalsIgnoreCase(binding.bankInputView.getSelectedValue()) && !binding.branchInputView.getSelectedValue().isEmpty())
                || (!binding.bankInputView.getSelectedValue().isEmpty() && binding.acceptAccessCheckBox.isChecked() && !binding.selectAccountTypeInputView.getSelectedValue().isEmpty() && ((binding.accountNumberInputView.getVisibility() == View.VISIBLE && !binding.accountNumberInputView.getSelectedValue().isEmpty()) || (binding.accountListInputView.getVisibility() == View.VISIBLE && !binding.accountListInputView.getSelectedValue().isEmpty())) && binding.bankInputView.getSelectedValue().toLowerCase().contains(ABSA)
                || (!binding.bankInputView.getSelectedValue().isEmpty() && binding.acceptAccessCheckBox.isChecked() && !binding.selectAccountTypeInputView.getSelectedValue().isEmpty() && ((binding.accountNumberInputView.getVisibility() == View.VISIBLE && !binding.accountNumberInputView.getSelectedValue().isEmpty()) || (binding.accountListInputView.getVisibility() == View.VISIBLE && !binding.accountListInputView.getSelectedValue().isEmpty())) && !ABSA.equalsIgnoreCase(binding.bankInputView.getSelectedValue()) && !binding.branchInputView.getSelectedValue().isEmpty()));
    }

    private void validateInputs() {
        if (binding.bankInputView.getSelectedValue().isEmpty()) {
            showNormalInputViewError(binding.bankInputView, getString(R.string.vcl_income_verification_into_bank_hint));
        } else if (binding.branchInputView.getVisibility() == View.VISIBLE && binding.branchInputView.getSelectedValue().isEmpty()) {
            showNormalInputViewError(binding.branchInputView, getString(R.string.stop_and_replace_select_branch_list));
        } else if (binding.accountNumberInputView.getVisibility() == View.VISIBLE && binding.accountNumberInputView.getSelectedValue().isEmpty()) {
            showNormalInputViewError(binding.accountNumberInputView, getString(R.string.vcl_account_number_error));
        } else if (binding.accountListInputView.getVisibility() == View.VISIBLE && binding.accountListInputView.getSelectedValue().isEmpty()) {
            showNormalInputViewError(binding.accountListInputView, getString(R.string.vcl_account_number_error));
        } else if (binding.selectAccountTypeInputView.getSelectedValue().isEmpty() && binding.acceptAccessCheckBox.isChecked()) {
            showNormalInputViewError(binding.selectAccountTypeInputView, getString(R.string.vcl_income_verification_into_account_select_type));
        } else if (!TextUtils.isEmpty(binding.emailInputView.getSelectedValue()) && !ValidationUtils.isValidEmailAddress(binding.emailInputView.getSelectedValue())) {
            showNormalInputViewError(binding.emailInputView, getString(R.string.invalid_email_address));
        } else if (isAllFieldsValid()) {
            presenter.onContinueToAdjustCreditLimitScreen();
        }
    }

    private void showNormalInputViewError(NormalInputView normalInputView, String errorMessage) {
        ObjectAnimator.ofInt(binding.scrollView, "scrollY", normalInputView.getTop()).setDuration(FAST_SCROLL_TIME).start();
        normalInputView.setError(errorMessage);
        normalInputView.showError(true);
    }

    private void setupOnClickListeners() {
        binding.bankInputView.setCustomOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Bank clicked");
            presenter.onBankSelectorClicked();
        });
        binding.branchInputView.setCustomOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Branch clicked");
            presenter.onBranchSelectorClicked(bankName);
        });
        binding.selectAccountTypeInputView.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Account type clicked");
            presenter.onAccountTypeSelectorClick();
        });
    }

    private void updateVCLDataFromViews() {
        VCLParcelableModel vclDataModel = getParentActivity().getVCLDataModel();
        vclDataModel.setBank(binding.bankInputView.getSelectedValue());
        vclDataModel.setBranchCode(branchCode);
        vclDataModel.setTermsAccepted(binding.acceptAccessCheckBox.getIsValid());
        vclDataModel.setAccountNumber((binding.accountNumberInputView.getVisibility() == View.VISIBLE ? binding.accountNumberInputView.getSelectedValue() : binding.accountListInputView.getSelectedValue()).replace(" ", ""));
        vclDataModel.setEmailAddress(binding.emailInputView.getSelectedValue());
        vclDataModel.setAccountType(binding.selectAccountTypeInputView.getSelectedValue());
        vclDataModel.setDeaTermsAccepted(binding.acceptAccessCheckBox.isChecked());
        getParentActivity().updateVCLDataModel(vclDataModel);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueButton) {
            AnalyticsUtil.INSTANCE.trackAction("Continue button");
            updateVCLDataFromViews();
            validateInputs();
        }
    }

    @Override
    public void navigateToBankListLayout(BankDetails successResponse) {
        List<String> bankList = successResponse.getBankList();
        if (!bankList.isEmpty()) {
            Intent banksIntent = new Intent(getActivity(), ChooseBankListActivity.class);
            banksIntent.putExtra(BANK_LIST, successResponse);
            startActivityForResult(banksIntent, REQUEST_CODE_FOR_BANK_NAME);
        } else {
            showGenericErrorMessage();
        }
    }

    @Override
    public void navigateToBranchList(BankBranches successResponse) {
        List<Branch> branchesList = successResponse.getBranchList();
        if (branchesList != null && !branchesList.isEmpty()) {
            Intent branchesIntent = new Intent(getActivity(), ChooseBranchListActivity.class);
            branchesIntent.putExtra(BMBConstants.RESULT, successResponse);
            startActivityForResult(branchesIntent, REQUEST_CODE_FOR_BRANCH_NAME);
        } else {
            showGenericErrorMessage();
        }
    }

    @Override
    public void navigateToAccountTypeSelector() {
        Intent accountTypeIntent = new Intent(getActivity(), SelectAccountTypeActivity.class);
        startActivityForResult(accountTypeIntent, REQUEST_CODE_FOR_ACCOUNT_TYPE);
    }

    @Override
    public void navigateToAdjustCreditLimitScreen() {
        if (!binding.acceptAccessCheckBox.isChecked()) {
            getBaseActivity().showMessage(getString(R.string.vcl_request_documents_title), getString(R.string.vcl_income_verification), (dialog, which) -> getParentActivity().navigateToNextFragment(CreditCardVCLAdjustLimitFragment.newInstance()));
        } else {
            getParentActivity().navigateToNextFragment(CreditCardVCLAdjustLimitFragment.newInstance());
        }
    }

    private void validateBanksForDEA(String bankName) {
        if (bankName.toLowerCase().contains(ABSA)) {
            binding.accessContainer.setVisibility(View.VISIBLE);
            binding.absaAssistanceNoticeTextView.setVisibility(View.VISIBLE);
            binding.acceptAccessCheckBox.setDescription(getString(R.string.vcl_income_verification_grant_permission));
        } else if (bankName.contains(STANDARD_BANK) || bankName.contains(STANDARD_BANK_NORMAL) || NEDBANK.equalsIgnoreCase(bankName)) {
            binding.accessContainer.setVisibility(View.VISIBLE);
            binding.absaAssistanceNoticeTextView.setVisibility(View.GONE);
            binding.acceptAccessCheckBox.setDescription(getString(R.string.vcl_income_verification_grant_permission_external));
        } else {
            binding.accessContainer.setVisibility(View.GONE);
            binding.absaAssistanceNoticeTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FOR_BANK_NAME:
                    bankName = data.getStringExtra(BANK_NAME);
                    getParentActivity().getVCLDataModel().setBank(bankName);
                    onBankResult(bankName);
                    break;
                case REQUEST_CODE_FOR_BRANCH_NAME:
                    branchCode = data.getStringExtra(BRANCH_CODE);
                    getParentActivity().getVCLDataModel().setBranchCode(branchCode);
                    binding.branchInputView.setSelectedValue(branchCode);
                    break;
                case REQUEST_CODE_FOR_ACCOUNT_TYPE:
                    AccountType accountType = data.getParcelableExtra(ACCOUNT_TYPE);
                    if (accountType != null) {
                        getParentActivity().getVCLDataModel().setAccountType(accountType.getName());
                        binding.selectAccountTypeInputView.setSelectedValue(accountType.getName());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void onBankResult(String result) {
        binding.acceptAccessCheckBox.setChecked(false);
        binding.bankInputView.setSelectedValue(StringExtensions.toSentenceCase(result));
        UniversalBank universalBank = CommonUtils.getUniversalBank(result);
        if (universalBank != null) {
            branchCode = universalBank.getBranchCode();
        }

        if (result.equalsIgnoreCase(ABSA)) {
            binding.branchInputView.setVisibility(View.GONE);
            setupAccountSelectorForAbsa();
        } else if (result.toLowerCase().contains(ABSA)) {
            binding.branchInputView.setVisibility(View.VISIBLE);
            String ABSA_BRANCH_CODE = "632005";
            binding.branchInputView.setSelectedValue(ABSA_BRANCH_CODE);
            branchCode = ABSA_BRANCH_CODE;
            setupAccountSelectorForAbsa();
        } else {
            binding.branchInputView.setEnabled(true);
            binding.branchInputView.setSelectedValue("");
            binding.branchInputView.setVisibility(View.VISIBLE);
            removeAccountSelector();

        }
        validateBanksForDEA(result);
    }

    private void removeAccountSelector() {
        binding.accountListInputView.setVisibility(View.GONE);
        binding.accountNumberInputView.setVisibility(View.VISIBLE);
        binding.accountNumberInputView.setSelectedValue("");
    }

    @SuppressWarnings("unchecked")
    private void setupAccountSelectorForAbsa() {
        binding.accountNumberInputView.setVisibility(View.GONE);
        binding.accountListInputView.setVisibility(View.VISIBLE);
        binding.accountListInputView.setText("");

        SelectorList<AccountListItem> vclAccountList = new SelectorList<>();
        ArrayList<AccountObject> transactionalAccounts = FilterAccountList.getTransactionalAccounts(AbsaCacheManager.getInstance().getCachedAccountListObject().getAccountsList());

        if (transactionalAccounts != null) {
            for (AccountObject accountItem : transactionalAccounts) {
                AccountListItem accountListItem = new AccountListItem();
                accountListItem.setAccountNumber(accountItem.getAccountNumber());
                accountListItem.setAccountType(accountItem.getDescription());
                accountListItem.setAccountBalance(accountItem.getAvailableBalance().toString());
                vclAccountList.add(accountListItem);
            }
        }

        if (transactionalAccounts != null && !transactionalAccounts.isEmpty()) {
            binding.accountListInputView.setList(vclAccountList, getString(R.string.please_select_account));
            binding.accountListInputView.setItemSelectionInterface(index -> {
                AccountObject selectedAccount = transactionalAccounts.get(index);
                binding.accountListInputView.setSelectedIndex(index);
                if (selectedAccount != null) {
                    binding.accountListInputView.setSelectedValue(selectedAccount.getAccountNumber());
                }
            });
        }
    }

    class TextChangeListener implements TextWatcher {
        private NormalInputView normalInputView;

        TextChangeListener(NormalInputView normalInputView) {
            this.normalInputView = normalInputView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(normalInputView.getDescriptionTextView().getText().toString())) {
                normalInputView.showDescription(true);
            }
            normalInputView.showError(false);
            normalInputView.hideError();
        }
    }
}
