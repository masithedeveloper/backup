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

package com.barclays.absa.banking.newToBank;

public class NewToBankConstants {
    static final int IMAGE_QUALITY = 40;
    static final int IMAGE_SIZE = 600;

    static final String TIM_ID_NUMBER = "idNumber";
    static final String TIM_IMAGE = "image";
    static final String TIM_FRONT = "timFront";
    static final String TIM_BACK = "timBack";
    static final String SELECTED_ITEM = "selectedItem";
    static final String RETRIEVED_BRANCHES = "retrieved branches";
    static final String ABSA_BRANCH_CODE = "632 005";

    public static String STUDENT_SILVER_CHEQUE = "STUDENT_SILVER_CHEQUE";
    static String GOLD_ACCOUNT = "GOLD_CHEQUE";
    static String PREMIUM_ACCOUNT = "PLATINUM_CHEQUE";
    public static String BUSINESS_EVOLVE_ACCOUNT = "BUSINESS_EVOLVE_ACCOUNT";
    public static String BUSINESS_EVOLVE_ISLAMIC_ACCOUNT = "BUSINESS_EVOLVE_ISLAMIC_ACCOUNT";
    public static boolean ON_NEW_TO_BANK_FLOW;

    public static String OTHER_OCCUPATION_CODE = "10";
    public static String STUDENT_DEFAULT_MONTHLY_INCOME_CODE = "01";
    public static String ALLOWANCE_SOURCE_OF_INCOME_CODE = "27";
    public static String STUDENT_EMPLOYMENT_CODE = "05";
    public static String BOARDER_RESIDENTIAL_STATUS_CODE = "B";

    static final String SOURCE_OF_FUNDS = "SOURCE_OF_FUNDS";
    static final String OCCUPATION_CODE = "OCCUPATION_CODE";
    static final String EMPLOYMENT_STATUS = "EMPLOYMENT_STATUS";
    static final String MONTHLY_INCOME_RANGE = "MONTHLY_INCOME_RANGE";
    static final String NATIONALITY = "NATIONALITY";
    static final String COUNTRY_OF_BIRTH = "COUNTRY_OF_BIRTH";
    static final String MEDICAL_OCCUPATION = "MEDICAL_OCCUPATION";
    static final String RESIDENTIAL_STATUS = "RESIDENTIAL_STATUS";
    static final String OCCUPATION_LEVEL = "OCCUPATION_LEVEL";
    static final String EMPLOYMENT_SECTOR = "EMPLOYMENT_SECTOR";
    static final String REWARDS_CONFIG_DATE_KEY = "AXOBMOBAPP_REWARDS_FREE_UNTIL_DATE";
    static final String REWARDS_CONFIG_KEY = "AXOBMOBAPP_REWARDS_TRANSACTION_CHARGE";

    //Analytics constants

    public static final String OPEN_ACCOUNT_ACTION = "NTB_OpenAccount_Click";

    public static final String ANALYTICS_CHANNEL = "New To Bank";

    static final String PRE_REGISTRATION = "NTB_BeforeWeStart";

    static final String INCOME_SCREEN = "NTB_IncomeRange";

    static final String INCOME_AND_EXPENSES = "NTB_Income&Expenses";

    static final String CHOOSE_ACCOUNT_SCREEN = "NTB_ChooseYourAccountScreen";

    static final String CLIENT_ACCOUNT_SELECTION_ACTION_PB = "NTB_First_PB_ApplyClick";

    static final String CLIENT_ACCOUNT_SELECTION_ACTION_GVB = "NTB_First_GVB_ApplyClick";

    static final String CLIENT_ABSA_LINK_CLICK_ACTION = "NTB_AbsacozaClick";

    static final String CLIENT_ABSA_FEES_SELECTION_CLICK = "NTB_PricingBrochureClick";

    static final String CLIENT_MORE_INFO_VIEW_PB = "NTB_PB_MoreInfo";

    static final String CLIENT_MORE_INFO_VIEW_GVB = "NTB_GVB_MoreInfo";

    static final String CLIENT_MORE_INFO_SELECTION_ACTION_PB = "NTB_PB_Additional_MoreInfo";

    static final String CLIENT_MORE_INFO_SELECTION_ACTION_GVB = "NTB_GVB_Additional_MoreInfo";

    static final String CLIENT_PB_APPLICATION_SELECTION_ACTION = "NTB_Second_PB_ApplyClick";

    static final String CLIENT_GVB_APPLICATION_SELECTION_ACTION = "NTB_Second_GVB_ApplyClick";

    static final String VERIFY_IDENTITY_SCREEN = "NTB_WhoYouAre";

    static final String IDENTITY_SCREEN_TERMS_CLICKACTION = "NTB_Terms&Conditions_Click";

    static final String IDENTITY_SCREEN_ID_GOLDENSOURCE_FAILURE = "NTB_IDFailure";

    static final String IDENTITY_SCREEN_EXISTING_CUSTOMER_ERROR = "NTB_ExistingCustomer";

    static final String VERIFY_CONTACT_SURECHECK_SCREEN = "NTB_SureCheck";

    static final String VERIFY_CONTACT_SURECHECK_RETRY_SCREEN = "NTB_SureCheck_Retry";

    static final String SELFIE_INSTRUCTION_SCREEN = "NTB_SelfieInstructions";

    static final String SELFIE_SCREEN = "NTB_TakeSelfie";

    static final String SELFIE_PHOTO = "NTB_Selfie_";

    public static final String SELFIE_NO_FRONT_CAMERA_ERROR = "NTB_NoFrontEndCamera";

    static final String SELFIE_FAILED = "NTB_FailedSelfie";

    static final String SELFIE_NOT_CLEAR_RETRY_ERROR = "NTB_SelfieNotClear_Retry";

    static final String SELFIE_NOT_CLEAR_FAILED_ERROR = "NTB_SelfieNotClear_Failed";

    static final String SELFIE_HEAD_OUT_OF_FRAME_ERROR = "NTB_HeadOutFrame";

    static final String ID_PHOTO_INSTRUCTIONS = "NTB_IDPhotoInstructions";

    static final String ID_FAILED_ERROR = "NTB_IDFailed";

    static final String ID_TIM_PLATFORM_DOWN = "NTB_TIMDown";

    static final String CONFIRM_IDENTITY_SCREEN = "NTB_IsThisYou";

    static final String CONFIRM_IDENTITY_ACTION_CONFIRM_YES = "NTB_IsThisYou_Yes";

    static final String CONFIRM_IDENTITY_ACTION_CONFIRM_NO = "NTB_IsThisYou_No";

    static final String CONFIRM_IDENTITY_ERROR_SCREEN = "NTB_IsThisYou_ErrorScreen";

    static final String CONFIRM_ADDRESS_SCREEN = "NTB_ShowAddress";

    static final String CONFIRM_ADDRESS_CASA_FAIL = "NTB_CasaFailScreen";

    static final String PROOF_OF_RESIDENCE_SCREEN = "NTB_PoR_Instructions";

    static final String PROOF_OF_RESIDENCE_SHOWMORE = "NTB_PoRExamples_ShowMeMore";

    static final String PROOF_OF_RESIDENCE_DOCHANDLER_FAILURE = "NTB_PoR_SomethingWentWrong";

    static final String REWARDS_OFFER = "NTB_RewardsOffer";

    static final String REWARDS_SHOW_MORE = "NTB_Rewards_ShowMeMoreInfo";

    static final String REWARDS_TERMS_ACTION_CLICK = "NTB_RewardsT&C_Click";

    static final String EMPLOYMENT_SCREEN = "NTB_WhatYouDo";

    static final String SIGNUP_RESULT_REFERRAL = "NTB_ReferralOutcome";

    static final String SIGNUP_RESULT_UNSUCCESSFUL = "NTB_Unsuccessful_Result";

    static final String SIGNUP_RESULT_SUCCESSFUL = "NTB_StraightThrough_Success";

    static final String BANKING_NEXT_STEPS = "NTB_NextSteps";

    static final String BANKING_CARD_SELECTION_IN_BRANCH = "NTB_InBranch_CardSelection";

    static final String BANKING_STANDARD_CARD_SELECTION = "NTB_InBranch_StdCard_JourneyEndScreen";

    static final String BANKING_CARD_ORDER_BRANCH_SELECTION = "NTB_OrderCard_ChooseBranch";

    static final String BANKING_CARD_ORDER_SUCCESS = "NTB_OrderCard_SuccessScreen";

    static final String BANKING_CARD_ORDER_FAIL = "NTB_CardOrdering_Fail";

    public static final String SOLE_PROPRIETOR_ANALYTICS_CHANNEL = "SoleProprietor";

    static final String STUDENT_ACCOUNT_ANALYTICS_CHANNEL = "StudentAccount";

    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_STANDARD_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/business_evolve_standard_account.json";
    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_STANDARD_AF_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/business_evolve_standard_account_af.json";
    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_ISLAMIC_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/business_evolve_islamic_account.json";
    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_ISLAMIC_AF_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/business_evolve_islamic_account_af.json";
    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_OPTIONAL_EXTRAS_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/business_evolve_optional_extra.json";
    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_OPTIONAL_EXTRAS_AF_URL = "https://www.absa.co.za/content/dam/south-africa/absa/json/business_evolve_optional_extra_af.json";

    public static final String NEW_TO_BANK_BUSINESS_EVOLVE_TERMS_OF_USE_URL = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/business/business-current-account-business-evolve-account-terms-of-use.pdf";
}