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
package com.barclays.absa.banking.framework.utils;

public interface AppConstants {
    // server resCodes
    String BMB99999_ERROR = "BMB99999"; // on cause is interleaved requests
    String FTR00565_NO_BENEFICIARIES_FOUND = "FTR00565"; //RES0310, Response=No beneficiaries have been found.

    String RESPONSE_CODE_SUCCESS = "00000";
    String RESPONSE_CODE_POLLING_SUCCESS = "00004";
    String RESPONSE_CODE_EMS_FAILED = "Security/LogontoAbsaOnline/Waning/EMSFailed";
    String RESPONSE_CODE_SERVICE_NOT_ACTIVE = "Security/LogontoAbsaOnline/Error/ServiceNotActive";
    String RESPONSE_CODE_FRAUD_HOLD = "Security/LogontoAbsaOnline/Error/FraudHold";
    String RESPONSE_CODE_ACCOUNT_CLOSED = "Common/General/Host/Error/H0262";
    String RESPONSE_CODE_NO_ACCOUNTS = "Security/LogontoAbsaOnline/Error/NoAccounts";
    // When account transaction list empty
    String RESPONSE_CODE_PARTIAL_SUCCESS = "03000";

    String RESPONSE_CODE_SESSION_EXPIRED = "BMB0199";
    String RESPONSE_CODE_FORCE_SESSION_EXPIRED = "ATH00115";

    String LOG_TAG = "BARCLAYS";
    String RESULT = "RESULT";

    String PIN_LOCKED = "FTR_PINLOCKED";

    String IS_FROM_ADD_CASHSEND = "is_from_add_new_cash_send_flow";

    String GENERIC_ERROR_MSG = "An unexpected error has occurred. Please try again later.";
    String GENERIC_ERROR_MSG_AF = "ŉ Onverwagte fout het voorgekom. Probeer asseblief weer later.";
    String INVALID_CERTIFICATE_EXCEPTION = "The app version that you are using is no longer valid.  Please install the latest version of the app available in the app store.";
    String SUB_ACTIVITY_INDICATOR = "SUB_ACTIVITY";
    String CALLING_ACTIVITY = "CALLING_ACTIVITY";
    String CLIENT_TYPE = "client_type";

    String SHOULD_BE_SHOWING_IIP = "isIIP";
    String SHOULD_BE_SHOWING_STUDENT_TERMS = "isStudent";

    String ABSA_ONLINE_URL = "https://www.absa.co.za/";
    String NEW_TO_BANK_FLEXI_ABSA_URL = "https://www.absa.co.za/personal/bank/an-account/flexi-value-bundle-account/";
    String NEW_TO_BANK_GOLD_ABSA_URL = "https://www.absa.co.za/personal/bank/an-account/gold-account/";
    String NEW_TO_BANK_PREMIUM_ABSA_URL = "https://www.absa.co.za/personal/bank/an-account/premium-banking/";

    String NEW_TO_BANK_GOLD_ABSA_PDF_URL = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/app/gold-value-bundle.pdf";
    String NEW_TO_BANK_PREMIUM_ABSA_PDF_URL = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/app/premium-banking.pdf";
    String NEW_TO_BANK_REWARDS_TERMS_PDF_URL = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/rewards/absa-rewards-terms-and-conditions.pdf";

    String NEW_TO_BANK_FLEXI_VALUE_BUNDLE_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/flexi-value-bundle.json";
    String NEW_TO_BANK_GOLD_VALUE_BUNDLE_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/gold-value-bundle.json";
    String NEW_TO_BANK_PREMIUM_VALUE_BUNDLE_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/premium-banking.json";
    String NEW_TO_BANK_PROOF_OF_RESIDENCE_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/proof-of-residence.json";
    String NEW_TO_BANK_ABSA_REWARDS_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/absa-rewards.json";
    String NEW_TO_BANK_ABSA_FREE_REWARDS_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/free-absa-rewards.json";

    String NEW_TO_BANK_STUDENT_SILVER_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/student-account.json";
    String SSLPinningError = "SSLPinningError";
}