/*
 * Copyright (c) 2018 Barclays Bank Plc, All Rights Reserved.
 *
 * This code is confidential to Barclays Bank Plc and shall not be disclosed
 * outside the Bank without the prior written permission of the Director of
 * CIO
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Barclays Bank Plc.
 */
package com.barclays.absa.banking.framework.api.request.params;

public enum NewToBankParams {
    CODES_LOOKUP_TYPE("codesLookupType"),
    NTB_SURNAME("surName"),
    NTB_CELLPHONE_NUMBER("cellphoneNumber"),
    AREA("area"),
    ADDRESS_CHANGED("addressChanged"),
    NATIONALITY("nationality"),
    SEARCH_FIELD("searchField"),
    SEARCH_VALUE("searchValue"),
    NAME("name"),
    DIRECTION("direction"),
    DELIVERY_METHOD("deliveryMethod"),
    DELIVERY_BRANCH("deliveryBranch"),
    IN_BRANCH_INDICATOR("inBranchIndicator"),
    IN_BRANCH_NAME("inBranchName"),
    IN_BRANCH_SITE("inBranchSite"),
    PERSONALISED_CARD("personalisedCard"),
    DOCUMENT_TYPE("documentType"),
    BARCODE_ID_NUMBER("barcodeIdNumber"),
    CELL_NUMBER("cellNumber"),
    SECURITY_NOTIFICATION_TYPE("securityNotificationType"),
    SECURITY_FUNCTION_TYPE("securityFunctionType"),
    PRODUCT_TYPE("productType"),
    SOURCE_OF_INCOME("sourceOfIncome"),
    SOURCE_OF_FUNDS("sourceOfFunds"),
    SOURCE_OF_FUNDS_RULE("sourceOfFundsRule"),
    EMPLOYMENT_SECTOR("employmentSector"),
    MONTHLY_INCOME_RANGE("monthlyIncomeRange"),
    OCCUPATION_CODE("occupationCode"),
    MEDICAL_OCCUPATION_CODE("medicalOccupationCode"),
    OCCUPATION_STATUS("occupationStatus"),
    OCCUPATION_LEVEL("occupationLevel"),
    REGISTERED_FOR_TAX("registeredForTax"),
    FOREIGN_NATIONAL_TAX_RESIDENT("foreignNationalTaxResident"),
    FOREIGN_NATIONAL_RESIDENT_COUNTRY("foreignNationalResidentCountry"),
    PERSONAL_CLIENT_AGREEMENT_ACCEPTED("personalClientAgreementAccepted"),
    CREDIT_CHECK_CONSENT("creditCheckConsent"),
    UNDER_DEBT_COUNSELING("underDebtCounseling"),
    MARKETING_INDICATOR("marketingIndicator"),
    MAIL_MARKETING_INDICATOR("mailMarketingIndicator"),
    EMAIL_MARKETING_INDICATOR("emailMarketingIndicator"),
    SMS_MARKETING_INDICATOR("smsMarketingIndicator"),
    TELE_MARKETING_INDICATOR("teleMarketingIndicator"),
    VOICE_MARKETING_INDICATOR("voiceMarketingIndicator"),
    FEES_LINK_DISPLAYED("feesLinkDisplayed"),
    ACCOUNT_FEATURE_DISPLAYED("accountFeatureDisplayed"),
    TOTAL_MONTHLY_INCOME("totalMonthlyIncome"),
    TOTAL_MONTHLY_EXPENSES("totalMonthlyExpenses"),
    MARKET_PROPERTY_VALUE("marketPropertyValue"),
    PREFERRED_MARKETING_COMMUNICATION("preferredMarketingcommunication"),
    ENABLE_REWARDS("enableRewards"),
    REWARDS_TERMS_ACCEPTED("rewardsTermsAccepted"),
    REWARDS_DAY_OF_DEBIT_ORDER("rewardsDayOfDebitOrder"),
    RESIDENTIAL_ADDRESS_SINCE("residentialAddressSince"),
    CURRENT_EMPLOYMENT_SINCE("currentEmploymentSince"),
    RESIDENTIAL_STATUS("residentialStatus"),
    OVERDRAFT_REQUIRED("overdraftRequired"),
    E_STATEMENT_REQUIRED("eStatementRequired"),
    NOTIFY_ME_REQUIRED("notifyMeRequired"),
    REWARDS_MARKETING_CONSENT("rewardsMarketingConsent"),
    OTP_TO_BE_VERIFIED("otpToBeVerified"),
    TOKEN_NUMBER("tokenNumber"),
    NONCE("nonce"),
    CLIENT_TYPE_GROUP("clientTypeGroup"),
    SIC_CODES_LOOKUP_TYPE("sicCodesLookupType"),
    SIC_CODE("sicCode"),
    PHYSICAL_ADDRESS_COUNTRY("physicalAddressCountry"),
    BUSINESS_BANKER_SITE("businessBankerSite"),
    TAX_NUMBER("taxNumber"),
    VAT_NUMBER("vatNumber"),
    TRADING_NAME("tradingName"),
    ADDRESS_TYPE("addressType"),
    FOREIGN_TRADING("foreignTrading"),
    USESELFIE("useSelfie"),
    SHORT_AND_MEDIUM_TERM_FUNDING_REWARDS_VALUE_ADDED_SERVICE("rewardsVas1"),
    ASSET_FINANCE_REWARDS_VALUE_ADDED_SERVICE("rewardsVas2"),
    MAKE_AND_RECEIVE_PAYMENTS_REWARDS_VALUE_ADDED_SERVICE("rewardsVas3"),
    SAVING_AND_INVESTMENT_PRODUCTS_REWARDS_VALUE_ADDED_SERVICE("rewardsVas4");

    private final String key;

    NewToBankParams(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
