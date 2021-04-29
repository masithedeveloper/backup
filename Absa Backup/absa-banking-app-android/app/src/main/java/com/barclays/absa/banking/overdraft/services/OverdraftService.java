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
package com.barclays.absa.banking.overdraft.services;

import com.barclays.absa.banking.boundary.model.OverdraftStatus;
import com.barclays.absa.banking.boundary.model.overdraft.AcceptOverdraftObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftIncomeAndExpensesConfirmationResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteSummary;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.boundary.model.overdraft.RejectOverdraftObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.model.FicaCheckResponse;

public interface OverdraftService {
    String OP2004_OVERDRAFT_FICA_CIF_STATUS = "OP2004";
    String OP2005_OVERDRAFT_SCORE = "OP2005";
    String OP2006_OVERDRAFT_QUOTE_DETAILS = "OP2006";
    String OP2007_OVERDRAFT_PDF_QUOTE = "OP2007";
    String OP2008_OVERDRAFT_ACCEPT_QUOTE = "OP2008";
    String OP2009_OVERDRAFT_REJECT_QUOTE = "OP2009";
    String OP2010_OVERDRAFT_QUOTE_STATUS = "OP2010";
    String OP2012_PERSONAL_INFORMATION = "OP2012";
    String OP2013_OVERDRAFT_QUOTE_SUMMARY = "OP2013";
    String OP2157_APPLY_BUSINESS_OVERDRAFT = "OP2157";

    String CUSTOMER_BUREA_COMMITMENTS = "customerBureauCommitments";
    String CUSTOMER_MAINTENANCE_EXPENSES = "customerMaintenanceExpenses";
    String CREDIT_DEFAULT_DELIVERY = "creditDefaultsDevileryInd";
    String CHECK_AGREE_QUOTE = "checkAgreeQuote";
    String FACILITY_REQUIRED = "facilityRequired";
    String OVERDRAFT_ACCOUNT_NUMBER = "odAccountNumber";
    String OVERDRAFT_ACCOUNT = "odAccount";
    String OVERDRAFT_TYPE = "odType";
    String OVERDRAFT_AMOUNT = "odAmount";
    String OVERDRAFT_CHECK_CPP = "checkCppOd";
    String OVERDRAFT_CPP_AMOUNT = "cppAmount";
    String OVERDRAFT_SYSTEM_DECISION = "systemDecision";
    String OVERDRAFT_SYSTEM_RESULT = "systemResult";
    String PRODUCT_CODE = "productCode";
    String PURPOSE_OF_OVERDRAFT = "purposeOfOd";
    String QUOTE_NUMBER = "quoteNumber";
    String QUOTE_NO = "quoteNo";
    String QUOTE_REFERENCE_NUMBER = "quoteRefNumber";
    String TOTAL_MONTHLY_GROSS_INCOME = "totalMonthlyGrossIncome";
    String TOTAL_MONTHLY_NET_INCOME = "totalMonthlyNetIncome";
    String TOTAL_MONTHLY_LIVING_EXPENSES = "totalMonthlyLivingExpenses";
    String TOTAL_MONTHLY_DISABLE_INCOME = "totalMonthlyDisableIncome";
    String MARKETINGUPDATESFLAG = "marketingUpdatesFlag";
    String SMSMARKETING = "smsMarketingInd";
    String TELEPHONEMAKERTING = "teleMarketingInd";
    String EMAILMARKETING = "emailMarketingInd";
    String MAILMARKETING = "mailMarkeitingInd";

    void fetchFICAAndCIFStatus(ExtendedResponseListener<FicaCheckResponse> ficaAndCifStatusResponseListener);

    void fetchOverdraftQuoteSummary(String quoteNumber, ExtendedResponseListener<OverdraftQuoteSummary> overdraftQuoteSummaryResponseListener);

    void fetchOverdraftScore(OverdraftSetup overdraftSetup, ExtendedResponseListener<OverdraftResponse> overdraftScoreResponseListener);

    void acceptOverdraftQuote(AcceptOverdraftObject object, ExtendedResponseListener<OverdraftResponse> overdraftAcceptResponseListener);

    void rejectOverdraftQuote(RejectOverdraftObject object, ExtendedResponseListener<OverdraftResponse> overdraftRejectResponseListener);

    void confirmIncomeAndExpenses(OverdraftQuoteDetailsObject object, ExtendedResponseListener<OverdraftIncomeAndExpensesConfirmationResponse> overdraftQuoteResponseListener);

    void fetchOverdraftStatus(ExtendedResponseListener<OverdraftStatus> extendedResponseListener);

    void applyBusinessOverdraft(String offersBusinessBankProduct, String existingOverdraftLimit, String newOverdraftLimit, ExtendedResponseListener<ApplyBusinessOverdraftResponse> extendedResponseListener);
}