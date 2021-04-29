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
package com.barclays.absa.banking.card.services.card.dto.creditCard;

import com.barclays.absa.banking.card.services.card.dto.CreditCardRequestParameters;
import com.barclays.absa.banking.card.ui.creditCard.vcl.BaseVCLFragment;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0906_CREDIT_LIMIT_INCREASE_APPLICATION;

public class CreditLimitApplicationRequest<T> extends ExtendedRequest<T> {
    private static final String SAVINGS_INDICATOR = "S";
    private static final String CHEQUE_INDICATOR = "C";
    private static final String SAVINGS = "Savings";
    private RequestParams.Builder params;

    public CreditLimitApplicationRequest(VCLParcelableModel requestModel, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        requestParams(requestModel);
        printRequest();
    }

    private void requestParams(VCLParcelableModel requestModel) {
        params = new RequestParams.Builder()
                .put(OP0906_CREDIT_LIMIT_INCREASE_APPLICATION)
                .put(CreditCardRequestParameters.VCL_INDICATOR.getKey(), "true");

        String BASE_REQUEST_VALUE = "0.00";
        if (requestModel != null) {
            String bankName = requestModel.getBank();
            String EMAIL_INDICATOR = "E";
            String IS_DEA_BANK = "Y";
            String IS_NOT_DEA_BANK = "N";
            String ABSA = "absa";
            params.put(CreditCardRequestParameters.CREDIT_CARD_NUMBER.getKey(), requestModel.getCreditCardNumber())
                    .put(CreditCardRequestParameters.CREDIT_LIMIT_INCREASE_AMOUNT_REQUESTED.getKey(), requestModel.getCreditLimitIncreaseAmount() != null ? StringExtensions.removeCurrency(requestModel.getCreditLimitIncreaseAmount()) : "0")
                    .put(CreditCardRequestParameters.GROCERIES_EXPENSE.getKey(), requestModel.getGroceriesExpense() != null ? StringExtensions.toDoubleString(requestModel.getGroceriesExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.MUNICIPAL_LEVIES.getKey(), requestModel.getMunicipalLevies() != null ? StringExtensions.toDoubleString(requestModel.getMunicipalLevies()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.DOMESTIC_WORKER_EXPENSE.getKey(), requestModel.getDomesticWorkerExpense() != null ? StringExtensions.toDoubleString(requestModel.getDomesticWorkerExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.EDUCATION_EXPENSE.getKey(), requestModel.getEducationExpense() != null ? StringExtensions.toDoubleString(requestModel.getEducationExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.TRANSPORT_EXPENSE.getKey(), requestModel.getTransportExpense() != null ? StringExtensions.toDoubleString(requestModel.getTransportExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.ENTERTAINMENT_EXPENSE.getKey(), requestModel.getEducationExpense() != null ? StringExtensions.toDoubleString(requestModel.getEducationExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.EXISTING_CREDIT_LIMIT_OF_CREDIT_CARD.getKey(), requestModel.getCurrentCreditLimit() != null ? StringExtensions.toDoubleString(requestModel.getCurrentCreditLimit()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.MAINTENANCE_EXPENSE.getKey(), requestModel.getMaintenanceExpense() != null ? StringExtensions.toDoubleString(requestModel.getMaintenanceExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.OTHER_LIVING_EXPENSE.getKey(), requestModel.getOtherLivingExpense() != null ? StringExtensions.toDoubleString(requestModel.getOtherLivingExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.DEA_BANK.getKey(), requestModel.getBank())
                    .put(CreditCardRequestParameters.DEA_BRANCH.getKey(), requestModel.getBranchCode())
                    .put(CreditCardRequestParameters.DEA_ACCOUNT.getKey(), requestModel.getAccountNumber())
                    .put(CreditCardRequestParameters.DEA_ACCOUNT_TYPE.getKey(), requestModel.getAccountType() != null ? (SAVINGS.contains(requestModel.getAccountType()) ? SAVINGS_INDICATOR : CHEQUE_INDICATOR) : CHEQUE_INDICATOR)
                    .put(CreditCardRequestParameters.DEA_SEND_METHOD.getKey(), EMAIL_INDICATOR)
                    .put(CreditCardRequestParameters.DEAL_INDICATOR.getKey(), (requestModel.getDeaTermsAccepted() && bankName != null && (bankName.toLowerCase().contains(ABSA) || bankName.contains(BaseVCLFragment.STANDARD_BANK) || bankName.contains(BaseVCLFragment.STANDARD_BANK_NORMAL) || BaseVCLFragment.NEDBANK.equalsIgnoreCase(bankName))) ? IS_DEA_BANK : IS_NOT_DEA_BANK)
                    .put(CreditCardRequestParameters.SERVICE_EMAIL_ADDRESS.getKey(), requestModel.getEmailAddress());
        } else {
            params.put(CreditCardRequestParameters.CREDIT_LIMIT_INCREASE_AMOUNT_REQUESTED.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.SERVICE_EMAIL_ADDRESS.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.GROCERIES_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.MUNICIPAL_LEVIES.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.DOMESTIC_WORKER_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.EDUCATION_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.TRANSPORT_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.ENTERTAINMENT_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.OTHER_LIVING_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.EXISTING_CREDIT_LIMIT_OF_CREDIT_CARD.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.MAINTENANCE_EXPENSE.getKey(), BASE_REQUEST_VALUE);
        }

        if (requestModel != null && requestModel.getIncomeAndExpense() != null) {
            params.put(CreditCardRequestParameters.GROSS_INCOME.getKey(), requestModel.getCreditCardVCLGadget() != null && requestModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null && requestModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalGrossMonthlyIncome() != null ? StringExtensions.toDoubleString(requestModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalGrossMonthlyIncome()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.NET_INCOME.getKey(), requestModel.getCreditCardVCLGadget() != null && requestModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null && requestModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalNetMonthlyIncome() != null ? StringExtensions.toDoubleString(requestModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalNetMonthlyIncome()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.TELEPHONE_EXPENSE.getKey(), requestModel.getIncomeAndExpense().getTelephoneExpense() != null ? StringExtensions.toDoubleString(requestModel.getIncomeAndExpense().getTelephoneExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.INSURANCE_EXPENSE.getKey(), requestModel.getIncomeAndExpense().getInsuranceExpense() != null ? StringExtensions.toDoubleString(requestModel.getIncomeAndExpense().getInsuranceExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.TOTAL_DEBT_INSTALMENTS.getKey(), requestModel.getIncomeAndExpense().getTotalMonthlyFixedDebtInstallment() != null ? StringExtensions.toDoubleString(requestModel.getIncomeAndExpense().getTotalMonthlyFixedDebtInstallment()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.SECURITY_EXPENSE.getKey(), requestModel.getIncomeAndExpense().getSecurityExpense() != null ? StringExtensions.toDoubleString(requestModel.getIncomeAndExpense().getSecurityExpense()) : BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.FIXED_LIVING_EXPENSE_TOTAL.getKey(), requestModel.getCreditCardVCLGadget() != null && requestModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null && requestModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses() != null ? StringExtensions.toDoubleString(requestModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses()) : BASE_REQUEST_VALUE);
        } else {
            params.put(CreditCardRequestParameters.GROSS_INCOME.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.NET_INCOME.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.TELEPHONE_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.INSURANCE_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.TOTAL_DEBT_INSTALMENTS.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.SECURITY_EXPENSE.getKey(), BASE_REQUEST_VALUE)
                    .put(CreditCardRequestParameters.FIXED_LIVING_EXPENSE_TOTAL.getKey(), BASE_REQUEST_VALUE);
        }
    }

    @Override
    public RequestParams getRequestParams() {
        return params.build();
    }

    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) CreditLimitApplicationResult.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
