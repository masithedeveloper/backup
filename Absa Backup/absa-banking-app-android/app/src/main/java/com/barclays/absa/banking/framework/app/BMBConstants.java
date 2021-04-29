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
package com.barclays.absa.banking.framework.app;

public interface BMBConstants {

    String LOGTAG = "BARCLAYS";
    String IMAGE = "image";

    int ATM_ACCESS_PIN_LENGTH = 6;

    String HMAC_401_UNAUTHORIZED = "HTTP 401 Unauthorized";
    String HMAC_VERIFICATION_FAILED = "Absa HMAC Verification failed";
    String CLIENT_ID_NOT_FOUND = "Client Id not Found.";

    String SUCCESS = "success";
    String FAILURE = "failure";
    String REJECTED = "rejected";
    String FAILED = "failed";

    String AUTH_TYPE_PINBLOCK = "PINBLOCK";
    String AUTH_TYPE_DIGITPIN = "DIGITPIN";
    String AUTH_TYPE_TOUCHID = "TOUCHID";
    String AUTH_TYPE_ANDROID_FINGERPRINT = "MOBILE_APP_ANDROID_FINGERPRINT";

    char ENGLISH_LANGUAGE = 'E';
    char AFRIKAANS_LANGUAGE = 'A';

    String BEN_TYPE = "BEN_TYPE";
    String PHONE = "PHONE";

    String TABLET_7_INCH = "TABLET_7_INCH";
    String TABLET_10_INCH = "TABLET_10_INCH";

    String RESULT = "RESULT";

    String SIMPLIFIED_LOGIN_YES = "Y";

    String ACCOUNT_CACHING_YES = "Y";
    String ACCOUNT_CACHING_NO = "N";
    String SERVICE_BENEFICIARY_ID = "benId";
    String SERVICE_BENEFICIARY_ATM_PIN_CASHSEND = "atmPn";
    String SERVICE_BENEFICIARY_BEN_CEL_NO_CASHSEND = "benCellNo";
    String SERVICE_DT = "dt";
    String SERVICE_BAL = "bal";
    String SERVICE_UNIQUE_EFT = "uniqueEFT";
    String SERVICE_BEN_SHORT_NAME = "benShortNam";
    String SERVICE_FROM_ACCOUNT = "frmActNo";
    String SERVICE_BENEFICIARY_NAME = "benNam";
    String SERVICE_BENEFICIARY_SUR_NAME = "benSurNam";
    String SERVICE_BENEFICIARY_ACCOUNT_NUMBER = "benAcctNo";
    String SERVICE_BENEFICIARY_IMAGE_TIMESTAMP = "imageTimeStamp";
    String SERVICE_PROFILE_IMAGE_DATA = "accountImage";
    String SERVICE_ACTIONTYPE_ADD = "add";
    String SERVICE_ACTIONTYPE_UPDATE = "update";
    String SERVICE_MY_REFERENCE = "myRef";
    String SERVICE_REF_NO = "refNo";

    /****************
     * Pass Parameter
     *****************/
    String PASS_PAYMENT = "Payment";
    String PASS_CASHSEND = "CashSend";
    String PASS_AIRTIME = "Airtime";
    String PASS_WESTERN_UNION = "westernunion";
    String PASS_PREPAID = "prepaid";
    String PASS_PREPAID_ELECTRICITY = "PPE";
    String PASS_BENEFICAIRY_TYPE = "BeneficairyType";
    String RESPONSE_ID_DEVICE_NOT_LINKED = "RES1000";
    String SORT_ON_DATE = "transactionDate";
    String SORT_IN_DESC = "DESC";
    String SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID = "RES2001";
    String YES = "Yes";
    String NO = "No";
    String OFF = "off";
    String ON = "on";
    int REQUEST_CODE_MY_REFERENCE_DETAILS = 1010;
    int THEIR_REFERENCE_DETIALS_REQUESTCODE = 1011;
    String STORED_KEY = "ABSA_PRODUCT_KEY";
    String STORED_DETAILS = "ABSA_PRODUCT_DETAILS";
    String CONST_SUCCESS = "Success";
    String CONST_WARNING = "Warning";
    String CONST_FAILURE = "Failure";
    String SMS = "SMS";
    String FAX = "FAX";
    String EMAIL = "EMAIL";
    String ABSA = "Absa";
    String ABSA_BRANCH_CODE = "632005";
    String OWN = "OWN";
    String NOW = "Now";
    String FROM_ACTIVITY = "FROM_ACTIVITY";

    enum AccountTypeEnum {
        savingsAccount,
        personalLoan,
        homeLoan,
        currentAccount,
        creditCard,
        GarageCard,
        absaVehicleAndAssetFinance,
        unitTrustAccount,
        OnlineShareTrading,
        OnlineShareTradingPrivate,
        OnlineShareTradingDetail,
        TelephoneShareTrading,
        ABSARewards,
        PensionBackedLoan,
        termDeposit,
        noticeDeposit,
        CorporatePropertyFinance,
        Retail,
        absaReward,
        AIMS,
        chequeAccount,
        debitAccount,
        cia
    }

    String BEN_ID = "BEN_ID";
    String AFRIKAANS_CODE = "af";
    String ENGLISH_CODE = "en";
    String SMARTPHONE_CHANNEL_IND = "S";
    String SPACE_STRING = " ";
    String NO_BREAK_SPACE = "\u00A0";
    String NEW_LINE = "\n";
    String HYPHEN = "-";
    String NA_STR = "N/A";
    String ALPHABET_Y = "Y";
    String ALPHABET_N = "N";
    String DEFAULT_USER_NO = "1";
    String MAC_ID_KEY = "MAC_ID";
    String UDID_KEY = "UDID";
    String SERIAL_NUMBER_KEY = "SERIAL_NUMBER";
    String IMEI_KEY = "IMEI";
    String NOTICE_TYPE_SMS_SHORT = "S";
    String NOTICE_TYPE_EMAIL_SHORT = "E";
    String NOTICE_TYPE_FAX_SHORT = "F";
    String NOTICE_TYPE_NONE_SHORT = "N";
    String BENEFICIARY_IMG_DATA = "beneficiary_img_data";
    String IS_EDIT_MODE = "is_edit_mode";
    String MIME_TYPE_JPG = "JPG";
    String SERVICE_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    String IMAGE_NAME = "imageName";
    String IVALUE = "iVal";
    String NVALUE = "nVal";
    String ENCRYPTED_PAYLOAD = "data";
    String HAS_IMAGE = "hasImage";
    String CHANGE_IMAGE = "changeImage";
    String PROFILE_BEN_ID = "profile_ben_id";
    String PROFILE_BEN_TYPE = "profile_ben_type";
    String AUTHORISATION_OUTSTANDING_TRANSACTION = "AUTHORISATION OUTSTANDING";
    String REDEMPTION_CODE_AIRTIME = "RWTAIR";
    String SELF = "self";
    String CUSTOMER_TYPE = "customer_type";
    String CLIENT_TYPE = "client_type";
    String PRIVATE_CUSTOMER_CODE = "E";
    String BUSINESS_CUSTOMER_CODE = "B";
    String USES_NEW_CRYPTO = "uses_new_crypto";
    String BLOCK_LOGIN = "BLOCK_LOGIN";
    String CONN_TIME_OUT = "Unable to connect to server due to bad signal. Please check your network settings";
    String CONN_TIME_OUT_AF = "Kon nie aan die bediener verbind nie omdat jou sein swak is. Gaan asseblief jou netwerk verstellings na";
    String NETWORK_UNREACHABLE = "NETWORK_UNREACHABLE";
    String NAVIGATED_FROM = "NAVIGATED_FROM";
    String DEVICE_DECOUPLE_OBJ = "DEVICE_DECOUPLE_OBJ";
    String PRE_LOGIN_LAYOUT = "preLoginLayout";
    String POST_LOGIN_LAYOUT = "postLoginLayout";
    int RESULT_CODE_SUCCESS_SURE_CHECK = 00000;
    int RESULT_CODE_REJECTED_SURE_CHECK = 00001;
    String PASSWORD = "password";
    String ONLINE_PIN = "PIN";
    String SURE_PHRASE = "SurePhrase";
    String PROFILE_SETUP_BOTH = "X";
    int EDIT_BEN_RESPONSE = 11100;
    String PASSCODE = "passcode";

    //Analytics Constant for event tracking
    String VIEW_ALL_ACCOUNTS_CONST = "View All Accounts";
    String BANKING_HOME_CONST = "Banking Home";
    String ACCOUNT_SUMMERY_CONST = "Account Summary";
    String CONFIRM_CONST = "Confirm";
    String HOME_CONST = "Home";
    String MANAGE_BENEFICIARIES_CONST = "Manage Beneficiaries";
    String PAYMENT_FUTURE_DATED_OVERVIEW_CONST = "Payment Future Dated Overview";
    String PAYMENT_HUB_CONST = "Payment Hub";
    String PAYMENTS_CONST = "Payments";
    String PREPAID_BENEFICIARIES_CONST = "Prepaid Beneficiaries";
    String PURCHASE_OVERVIEW_CONST = "Purchase Overview";
    String BUY_ONCEOFF_PREPAID_CONST = "Buy Once-off Prepaid";
    String PREPAID_PURCHASE_OVERVIEW_CONST = "Prepaid Purchase Overview";
    String BUY_PREPAID_FOR_NEW_BENEFICIARY_CONST = "Buy Prepaid for New Beneficiary";
    String IIP_CONST = "IIP";
    String PAYMENT_INSTITUTION_OVERVIEW_CONST = "Payment Institution Overview";
    String PAYMENT_OVERVIEW_CONST = "Payment Overview";
    String PAYMENT_IIP_SUCCESS_CONST = "Payment IIP Success";
    String PAYMENT_NORMAL_CONST = "Payment Normal Success";
    String PAYMENT_SCHEDULE_SUCCESS_CONST = "Payment Scheduled Success";
    String PAYMENT_UNSUCCESSFUL_CONST = "Payment Unsuccessful";
    String PURCHASE_SUCCESSFUL_CONST = "Purchase Successful";
    String PURCHASE_UNSUCCESSFUL_CONST = "Purchase Unsuccessful";
    String SELECT_TO_BANK_CONST = "Select To Bank";
    String SELECT_TO_BRANCH_CONST = "Select To Branch";
    String PREPAID_CONST = "Prepaid";
    String PROVIDER = "Provider";
    String BENEFICIARY_PHONE_NUMBER = "Beneficiary Phone Number";
    String PREPAID_TYPE = "Prepaid Type";
    String TVN_FALLBACK_CONST = "TvN Fallback";
    String TRUE_CONST = "1";
    String SELECT_TO_ACCOUNT_TYPE_CONST = "Select To Account Type";
    String PREPAID_AMOUNT = "Prepaid Amount";
    String SHARE_CONST = "Share";
    String CASHSEND_HOME_CONST = "CashSend Home";
    String CASHSEND_CONST = "CashSend";
    String UNREDEEMED_CASHSEND_TRANSACTIONS_CONST = "Unredeemed CashSend Transactions";
    String CASHSEND_UNREDEEMED_TRANSACTION_DETAILS_CONST = "CashSend Unredeemed Transaction Details";
    String CASHSEND_DETAILS_CONST = "CashSend Details";
    String CASHSEND_OVERVIEW_CONST = "CashSend Overview";
    String CASHSEND_SUCCESSFUL_CONST = "CashSend Sucessful";
    String CASHSEND_UNSUCCESSFUL_CONST = "CashSend Unsuccessful";
    String CASHSEND_TO_MYSELF_CONST = "CashSend to Myself";
    String CASHSEND_ONCE_OFF_CONST = "CashSend Once-off";
    String CASHSEND_TO_SOMEONE_NEW_CONST = "CashSend to Someone New";
    String MANAGE_CASHSEND_BENEFICIARIES_CONST = "Manage CashSend Beneficiaries";
    String EDIT_CASHSEND_BENEFICIARY_CONST = "Edit CashSend Beneficiary";
    String ADD_BENEFICIARY_UNSUCCESSFUL_CONST = "Add Beneficiary Unsuccessful";

    // For Handling Session popup Menu
    String SCREEN_NAME_CONST = "screen_name";
    String LONG_TERM_POLICY_TYPE = "LI";
    String EXERGY_POLICY_TYPE = "Exergy";
    String SHORT_TERM_POLICY_TYPE = "ST";
    String HAS_AUTH = "hasAuth";
    String PUSH_NOTIFICATION_APP_START = "pushAppStart";
    String PUSH_NOTIFICATION_TIME = "pushNotificationTime";
    String SIMPLIFIED_LOGIN_CONST = "Simplified Login";
    String FIRST_LOGIN_CONST = "First Login";
    String NEW_LOGIN_INTRO_CONST = "New Login Intro";
    String CREATE_PASSCODE_CONST = "Create Passcode";
    String RE_ENTER_PASSCODE_CONST = "Re-enter Passcode";
    String CREATE_PASSCODE_SUCCESS_CONST = "Create Passcode Success";
    String PASSCODE_DO_NOT_MATCH_CONST = "Passcodes do not match";
    String PASSWORD_CONST = "Password";
    String DEVICE_NICKNAME_CONST = "Device Nickname";
    String PROFILE_BLOCK_CONST = "Profile Block";
    String DEVICE_LIMIT_CONST = "Device Limit";
    String DELINK_DEVICE_CONST = "Delink Device";
    String LOGIN_CONST = "Login";
    String EDIT_BENEFICIARY_CONST = "editbeneficiary";
    String DELETE_BENEFICIARY_CONST = "deletebeneficiary";
    String MANAGE_PREPAID_BENEFICIARIES_CONST = "Manage Prepaid Beneficiaries";
    String NEW_BENEFICIARY_DETAILS_CONST = "New Beneficiary Details";
    String NEW_BENEFICIARY_DETAILS_OVERVIEW_CONST = "New Beneficiary Details Overview";
    String BENEFICIARY_ADDED_SUCCESS_CONST = "Beneficiary Added Success";
    String PAYMENT_IIP_DELAYED_SUCCESS_CONST = "Payment IIP Delayed Success";
    String SETTINGS_CONST = "Settings";
    String PASSCODE_STRING = "Passcode";

    int REQUESTCODE_CUSTOMER_PHOTO_AFTER_CROP = 11101;

    //Analytics Constant for Regisration flow.
    String REGISTER_CONST = "Register";
    String CARD_AND_PIN_CONST = "Card and PIN";
    String CONFIRM_CONTACT_DETAILS_CONST = "Confirm Contact Details";
    String CREATE_PASSWORD_AND_SUREPHRASE_CONST = "Create Password and SurePhrase";
    String REGISTER_SUCCESS_CONST = "Register Success";
    String PARTIAL_REGISTER_SUCCESS_CONST = "Partial Register Success";
    String CREATE_ONLINE_BANKING_PIN_CONST = "Create Online Banking PIN";

    //Analytics Constant for Rewards as airtime
    String BUY_PREPAID_REWARDS_CHANNEL_CONSTANT = "Buy prepaid";
    String BUY_PREPAID_REWARDS_CHANNEL_BUY_CONSTANT = "Buy";
    String BUY_HUB_CONSTANT = "Buy hub";
    String ABSA_REWARDS_BUY_AIRTIME_CONSTANT = "Absa Rewards buy airtime";
    String PURCHASE_UNSUCCESSFUL_CONSTANT = "Purchase unsuccessful";
    String PURCHASE_SUCCESSFUL_CONSTANT = "Purchase successful";

    //Analytics constant for prepaid prepaidElectricity flow
    String BUY_HUB_CONST = "Buy Hub";
    String BUY_PREPAID_ELECTRICITY_CONST = "Buy Prepaid Electricity";
    String BUY_ELECTRICITY_FOR_SOMEONE_NEW_ENTER_METER_NUMBER_CONST = "Buy prepaidElectricity for someone new enter meter number";
    String WHATS_NEW_CONST = "What’s New";

    //Anlytics constant for App Settings flow
    String EDIT_PHOTO_CONST = "edit photo";
    String MANAGE_CARD_LIMITS_OVERVIEW_CONST = "Manage card digitalLimits - overview";
    String MANAGE_PAYMENT_TRANSFER_LIMITS_EDIT = "Manage payment / transfer digitalLimits - edit";
    String MANAGE_PAYMENT_TRANSFER_LIMITS_CONFIRM = "Manage payment / transfer digitalLimits - confirm";

    String INTERNAL = "internal";
    String EXTERNAL = "external";
    String OTHER_BANK = "Other bank";
    String ANOTHER_BANK = "Another Bank";
    String BILL = "BILL";

    //Analytics for Stop and Replace Debit cards
    String STOP_AND_REPLACE_CARDS_CHANNEL_CONSTANT = "Manage Cards";
    String NEW_CARD_DETAILS = "New Card Details";

    //Add cashSend Beneficiary constants
    String CASHSEND_AMOUNT = "cashSendAmount";
    String ATM_ACCESS_PIN_KEY = "atmAccessPin";
    String ATM_PIN_KEY = "ATM_PIN";
    String DEFAULT_USER_NUMBER = "1";

    String REWARDS_ACCOUNT_TYPE = "Absa Rewards";

    String DUPLICATE_BENEFICIARY_ERRORCODE = "FTR00336";
    int INSERT_SPACE_AFTER_FOUR_DIGIT = 4;

    String SUCCESSFUL = "successful";
    String SUCCESSFUL_AFRIKAANS = "SUKSESVOL";

    String FACE_TO_FACE_DELIVERY = "F";
    String BRANCH_DELIVERY = "B";

    //Rewards Hub Constants
    String ACCOUNT_NUMBER = "accountNumber";

    //Manage devices Analytics
    String MANAGE_DEVICE = "Manage Devices";

    String IS_SELF = "isSelf";

    String TRANSACTION_TYPE_TRANSFER = "Transfer";
    String TRANSACTION_TYPE_PAYMENT = "Payment";
    String TRANSACTION_TYPE_PREPAID = "Purchase";
    String TRANSACTION_TYPE_CASH_SEND = "CashSend";
    String TRANSACTION_TYPE = "transactionType";
    String SECOND_AUTHORISATION_REQUIRED = "SECOND AUTHORISATION REQUIRED";

    //Analytics Strings for Rewards
    String ABSA_REWARDS = "Absa Rewards";
    String ABSA_REWARDS_HUB = "Absa Rewards Hub";
    String ABSA_REWARDS_MEMBERSHIP_DETAILS = "Absa Rewards Membership Details";

    //Dual Auth Analytics
    String DUAL_AUTHORISATION = "Dual Authorisation";
    String AUTHORISATIONS_LIST = "Authorisations List";
    String AUTHORISATION_TRANSACTION_DETAILS = "Authorisation Transaction Details";
    String AUTHORISATION_CONFIRM_TRANSACTION = "Authorisation Confirm Transaction";
    String TRANSACTION_AUTHORISED = "Transaction Authorised";
    String TRANSACTION_PENDING = "Transaction Pending";
    String UNABLE_TO_AUTHORISE = "Unable To Authorise";

    String BUSINESS_USER = "business";
    String ANALYTICS_SCREEN_NAME = "Archived Statements";

    //Resend proof of payment Analytics
    String RESEND_PROOF_PAYMENT_HISTORY = "Payment history";
    String RESEND_PROOF_PAYMENT_SUCCESS = "Re-send proof of payment success screen";
    String RESEND_PROOF_PAYMENT_OVERVIEW = "Re-send proof of payment overview screen";

    String POLICY_KEY = "policy";
    String FUNERAL_QUOTE_KEY = "funeralQuote";

    //2FA constants
    String OTP_SEED = "OTP_SEED";
    String IS_GENERATE_TOKEN = "Is Generate Token Offline";
    String CLIENT_APPLICATION_KEY_ID = "MobileApp_v1";

    String EMPTY_STRING = "";

    //Funeral cover constants
    String PLAN_CODE = "planCode";

    //Server Errors
    String FUNCTION_NOT_DEFINED = "Security/AccessControl/Error/FuntionNotDefined";
    String INVALID_RESPONSE_DATA_FORMAT = "Invalid response data format";
    String BMB_FRAMEWORK_ERROR = "BMBFRAMEWORKERROR";
    String REQUEST_TIMEOUT = "The request timed out";
    String SYSTEM_OFFLINE = "Security/LogontoAbsaOnline/Error/SystemOffline";
    String TECHNICAL_DIFFICULTIES = "Oops! We're experiencing technical difficulties. Please try again shortly.";

    //Stop and replace credit card constants
    String FACE_TO_FACE_DELIVERY_METHOD = "Face-to-face delivery";
    String COLLECT_FROM_BRANCH = "Collect from branch";
    String ORIGIN_SCREEN = "ORIGIN_SCREEN";
    String FRAUD = "Fraud";
    String FRAUD_AFRIKAANS = "Bedrog";

    // Unredeemed Transactions
    String UNREDEEMED_TRANSACTION = "unredeemedTransaction";
    String ACCOUNT_NUMBER_TO_DISPLAY = "accountNumberToDisplay";
    String MY_REFERENCE = "myReference";
}
