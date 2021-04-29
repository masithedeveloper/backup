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
package com.barclays.absa.banking.card.services.card.dto;

public enum CreditCardRequestParameters {

    IS_FRESH_BUREAU_AVAILABLE_WITH_CAMS("isFreshBureauDataAvailableWithCams"),
    OFFERS_CREDIT_CARD_NUMBER("offersCreditCardNumber"),
    CREDIT_CARD_LIMIT_INCREASE_AMOUNT_FOR_CLI("creditLimitIncreaseAmtForCLI"),
    EXISTING_CREDIT_LIMIT_OF_CREDIT_CARD("existingCreditLimitOfCreditCard"),
    TOTAL_GROSS_MONTHLY_INCOME("totalGrossMonthlyIncome"),
    TOTAL_NET_MONTHLY_INCOME("totalNetMonthlyIncome"),
    TOTAL_MONTHLY_LIVING_EXPENSES("totalMonhtlyLivingExpenses"),
    TOTAL_MONTHLY_FIXED_DEPOSIT_INSTALLMENT("totalMonthlyFixedDebtInstallment"),
    TELEPHONE_EXPENSE("telephoneExpense"),
    INSURANCE_EXPENSE("insuranceExpense"),
    SECURITY_EXPENSE("securityExpense"),
    BUREAU_CATEGORY("bureauCategory"),
    BUREAU_CATEGORY_AMOUNT("bureauCategoryAmount"),
    VCL_INDICATOR("vclIndicator"),
    CREDIT_LIMIT_INCREASE_AMOUNT_REQUESTED("creditLimitIncreaseAmountRequested"),
    GROSS_INCOME("grossIncome"),
    NET_INCOME("netIncome"),
    GROCERIES_EXPENSE("groceriesExpense"),
    MUNICIPAL_LEVIES("municipalLevies"),
    DOMESTIC_WORKER_EXPENSE("domesticWorkerExpense"),
    EDUCATION_EXPENSE("educationExpense"),
    TRANSPORT_EXPENSE("transportExpense"),
    ENTERTAINMENT_EXPENSE("entertainmentExpense"),
    OTHER_LIVING_EXPENSE("otherLivingExpense"),
    MAINTENANCE_EXPENSE("maintainanceExpense"),
    TOTAL_DEBT_INSTALMENTS("totalDebtInstalments"),
    FIXED_LIVING_EXPENSE_TOTAL("fixedLivingExpenseTotal"),
    DEAL_INDICATOR("deaIndicator"),
    DEA_BANK("deaBank"),
    DEA_ACCOUNT("deaAccount"),
    CREDIT_CARD_NUMBER("creditCardNumber"),
    DEA_BRANCH("deaBranch"),
    DEA_ACCOUNT_TYPE("deaAccounttype"),
    SERVICE_EMAIL_ADDRESS("emailAddress"),
    CARD_NUMBER("cardNumber"),
    CARD_INDEX("cardIndex"),
    DEA_SEND_METHOD("deaSendMethod"),
    FROM_DATE("frmDt"),
    TO_DATE("toDt"),
    PIN_BLOCK("pinBlock");

    private final String key;

    CreditCardRequestParameters(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
