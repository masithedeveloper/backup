/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.api.request.params;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.barclays.absa.banking.account.services.AccountService.OP0876_GET_ARCHIVED_STATEMENT;
import static com.barclays.absa.banking.account.services.AccountService.OP2074_LINKED_UNLINKED_ACCOUNTS;
import static com.barclays.absa.banking.account.services.AccountService.OP2075_LINK_UNLINK_ACCOUNTS;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0310_BENEFICIARY_LIST;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0311_BENEFICIARY_DETAILS;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0312_VIEW_TRANSACTION_DETAILS;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0315_REMOVE_BENEFICIARY;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0401_BENEFICIARY_IMAGE_DOWNLOAD;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0402_BENEFICIARY_IMAGE_UPLOAD;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0786_MY_NOTIFICATION_DETAILS;
import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP2086_EDIT_PREPAID_ELECTRICITY_BENEFICIARY;
import static com.barclays.absa.banking.boundary.pushNotifications.PushNotificationService.OP0825_MANAGE_PUSH_NOTIFICATION_RECORD;
import static com.barclays.absa.banking.boundary.pushNotifications.PushNotificationService.OP0826_DELETE_PUSH_NOTIFICATION_RECORD;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0812_TRANSACTION_VERIFICATION_STATUS;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0820_VALIDATE_TRANSACTION_VERIFICATION_CODE;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0829_DELETE_ALIAS;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0830_PRE_LOGON_VALIDATE_TRANSACTION_VERIFICATION_CODE;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0329_ADD_AIRTIME_CONFIRM;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0330_ADD_AIRTIME_RESULT;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0331_PREPAID_NETWORK_PROVIDER_LIST;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0332_EDIT_AIRTIME_CONFIRM;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0333_EDIT_AIRTIME_RESULT;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0617_ONCE_OFF_AIRTIME;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0618_ONCE_OFF_AIRTIME_CONFIRM;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0619_ONCE_OFF_AIRTIME_RESULT;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0620_BUY_AIRTIME;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0621_BUY_AIRTIME_CONFIRM;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0622_BUY_AIRTIME_RESULT;
import static com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0335_VALIDATE_METER_NUMBER;
import static com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY;
import static com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0339_PURCHASE_PREPAID_ELECTRICITY;
import static com.barclays.absa.banking.card.services.card.CardService.OP0120_CARD_LIST;
import static com.barclays.absa.banking.card.services.card.CardService.OP2019_ENQUIRE_PAUSE_CARD_STATES;
import static com.barclays.absa.banking.card.services.card.CardService.OP2054_MANAGE_CARDS;
import static com.barclays.absa.banking.card.services.card.CardService.OP2056_UPDATE_TRAVEL_NOTIFICATION;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0124_RETRIEVE_PIN;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0125_CARD_LIST;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0842_CREDIT_CARD_INFO;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0846_DISPLAY_OVERDRAFT_SNOOZE;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0863_VIEW_POLICY_DETAILS;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0864_FETCH_CREDIT_CARD_PROTECTION_INSURANCE_QUOTE;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0865_APPLY_CREDIT_CARD_PROTECTION_INSURANCE;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0869_CREDIT_CARD_REPLACEMENT_VALIDATION;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0871_CREDIT_CARD_REPLACEMENT_CONFIRMATION;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0893_REPLACEMENT_CREDIT_CARD_NUMBERS;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0905_BUREAU_DATA_FOR_VCL_CLI_PULL;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0906_CREDIT_LIMIT_INCREASE_APPLICATION;
import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP2052_CREDIT_CARD_HUB;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0299_UNREDEEMED_CASHSEND;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0327_CASHSEND_UPDATE_ATM_PIN;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0451_CASHSEND_RESEND_WITHDRAWAL_SMS;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0520_ONCE_OFF_CASHSEND;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0610_ONCE_OFF_CASHSEND_CONFIRM;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0611_ONCE_OFF_CASHSEND_RESULT;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0612_SEND_BENEFICIARY_CASHSEND;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0614_SEND_BENEFICIARY_CASHSEND_RESULT;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0808_CANCEL_TRANSACTION;
import static com.barclays.absa.banking.debiCheck.services.DebitOrderService.OP2110_SEARCH_DEBIT_ORDER;
import static com.barclays.absa.banking.debiCheck.services.DebitOrderService.OP2111_VIEW_STOPPED_DEBIT_ORDER;
import static com.barclays.absa.banking.debiCheck.services.DebitOrderService.OP2113_STOP_DEBIT_ORDER;
import static com.barclays.absa.banking.debiCheck.services.DebitOrderService.OP2114_REVERSE_DEBIT_ORDER;
import static com.barclays.absa.banking.directMarketing.services.DirectMarketingService.OP2089_GET_MARKETING_INDICATOR;
import static com.barclays.absa.banking.directMarketing.services.DirectMarketingService.OP2090_UPDATE_MARKETING_INDICATOR;
import static com.barclays.absa.banking.explore.services.OfferService.OP2053_OFFERS;
import static com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.OP2070_RETRIEVE_INTEREST_RATE_INFO;
import static com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.OP2071_CREATE_ACCOUNT;
import static com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.OP2072_CREATE_ACCOUNT_CONFIRM;
import static com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.OP2073_CREATE_ACCOUNT_PROCESS;
import static com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.OP2076_GET_ACCOUNT_DETAIL;
import static com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP0858_FUNERAL_QUOTES;
import static com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP0859_COVER_RELATION_APPLY_PLAN;
import static com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP0860_GET_RETAIL_ACCOUNTS;
import static com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP0862_APPLY_FOR_FUNERAL_PLAN;
import static com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP2057_GET_UNIT_TRUST_ACCOUNTS;
import static com.barclays.absa.banking.funeralCover.services.InsurancePolicyService.OP0848_CHANGE_PAYMENT_DETAILS_OPCODE;
import static com.barclays.absa.banking.home.services.HomeScreenService.OP0849_LINKED_POLICIES;
import static com.barclays.absa.banking.home.services.HomeScreenService.OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY;
import static com.barclays.absa.banking.home.services.HomeScreenService.OP0993_ACCOUNT_REORDERING;
import static com.barclays.absa.banking.home.services.HomeScreenService.OP1301_ACCOUNT_DETAILS;
import static com.barclays.absa.banking.home.services.clickToCall.ClickToCallService.OP2049_REQUEST_CALLBACK_OPCODE;
import static com.barclays.absa.banking.home.services.clickToCall.ClickToCallService.OP2050_VERIFY_SECRET_CODE_OPCODE;
import static com.barclays.absa.banking.login.services.LoginService.OP0817_AUTHENTICATE_ALIAS;
import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0816_VALIDATE_PRIMARY_DEVICE_PASSCODE_AND_ATM_PIN;
import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0818_CHANGE_PRIMARY_DEVICE;
import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0822_LIST_DEVICES;
import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0823_EDIT_NICKNAME_RESULT;
import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0824_DELINK_DEVICE;
import static com.barclays.absa.banking.manage.profile.services.ManageProfileService.OP2107_UPDATE_PROFILE;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2023_CREATE_CUSTOMER_PORTFOLIO;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2025_CASA_SCREENING;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2028_RETRIEVE_ID_OCR_DETAILS_FROM_DOCUMENT;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2031_ADDRESS_LOOKUP;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2032_VALIDATE_CUSTOMER_ADDRESS;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2033_POSTAL_CODE_LOOKUP;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2035_GET_ALL_CONFIGS_FOR_APPLICATION;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2036_GET_SCORING_STATUS;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2037_NEW_TO_BANK_KEEP_ALIVE;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2038_PHOTO_MATCH_AND_MOBILE_LOOKUP;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2040_GET_FILTERED_SITE_DETAILS;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2041_CREATE_COMBI_CARD_ACCOUNT;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2044_PRE_LOGON_VALIDATE_SECURITY_NOTIFICATION;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2045_PRE_LOGON_REQUEST_SECURITY_NOTIFICATION;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2046_PRE_LOGON_RESEND_SECURITY_NOTIFICATION;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2047_REGISTRATION_NEW_APPLICATION_CUSTOMER;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2048_REGISTER_ONLINE_BANKING_PASSWORD;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2084_CASA_RISK_PROFILING;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2004_OVERDRAFT_FICA_CIF_STATUS;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2005_OVERDRAFT_SCORE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2006_OVERDRAFT_QUOTE_DETAILS;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2007_OVERDRAFT_PDF_QUOTE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2008_OVERDRAFT_ACCEPT_QUOTE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2009_OVERDRAFT_REJECT_QUOTE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2010_OVERDRAFT_QUOTE_STATUS;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2012_PERSONAL_INFORMATION;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2013_OVERDRAFT_QUOTE_SUMMARY;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0852_GET_CLIENT_TYPE;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0867_ADD_NEW_WESTERN_UNION_BENEFICIARY;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0868_PERFORM_INTERNATIONAL_PAYMENT;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0872_GET_QUOTE_DETAILS;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0878_VALIDATE_NEW_WESTERN_UNION_BENEFICIARY;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0881_GET_WESTERN_UNION_CURRENCIES;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0882_GET_WESTERN_UNION_BENEFICIARIES;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0883_GET_ALL_WESTERN_UNION_COUNTRIES;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0884_VALIDATE_PAYMENT;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0885_VALIDATE_FOR_HOLIDAYS_AND_TIME;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0886_GET_OUT_BOUNDING_PENDING_TRANSACTION;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0889_GET_WESTERN_UNION_BENEFICIARY_DETAILS;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0897_PERFORM_ONCE_OFF_INTERNATIONAL_PAYMENT;
import static com.barclays.absa.banking.payments.services.PaymentsService.OP0866_FETCH_FUTURE_DATED_PAYMENTS;
import static com.barclays.absa.banking.payments.services.PaymentsService.OP0926_RESEND_NOTICE_OF_PAYMENT;
import static com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0912_REDEEM_REWARDS_CONFIRM;
import static com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0913_REDEEM_REWARDS_RESULT;
import static com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService.OP0528_MULTIPLE_BENEFICIARIES_PAYMENT_RESULT;
import static com.barclays.absa.banking.recognition.services.dto.BranchRecognitionService.OP2080_CUSTOMER_RATING_GET_FUNCTIONS_AND_PRODUCTS;
import static com.barclays.absa.banking.recognition.services.dto.BranchRecognitionService.OP2081_CUSTOMER_RATING_BRANCH_RECOGNITION;
import static com.barclays.absa.banking.registration.services.RegistrationService.OP0815_CREATE_2FA_ALIAS_UNAUTHENTICATED_OPCODE;
import static com.barclays.absa.banking.registration.services.RegistrationService.OP0821_CREATE_2FA_ALIAS_OPCODE;
import static com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachService.OP2087_GET_CASA_STATUS;
import static com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachService.OP2088_GET_RISK_PROFILE;
import static com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachService.OP2092_GET_FICA_STATUS;
import static com.barclays.absa.banking.settings.ui.SettingsHubService.OP0202_SHP_LANGUAGE_CHANGE;
import static com.barclays.absa.banking.shared.services.SharedService.OP0861_LOOKUP;
import static com.barclays.absa.banking.shared.services.SharedService.OP2022_GET_CODES;
import static com.barclays.absa.banking.transfer.services.TransferFundsService.OP2082_PERFORM_INTER_ACCOUNT_TRANSFER;
import static com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.OP2078_RETRIEVE_QUOTE;
import static com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.OP2079_APPLY_FOR_ULTIMATE_PROTECTOR;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2059_BUY_MORE_UNITS;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2060_RETRIEVE_BUY_MORE_UNITS_LINKED_ACCOUNTS;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2061_RETRIEVE_DEBIT_DAYS;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2064_RETRIEVE_UNIT_TRUST_FUND;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2066_RETRIEVE_LINKED_ACCOUNTS;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2067_CREATE_UNIT_TRUST_ACCOUNT;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2068_VALIDATE_LUMP_SUM;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2083_BUY_MORE_UNITS_CAPPED;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2095_UNITS_REDEMPTION_ACCOUNT_STATUS;
import static com.barclays.absa.banking.unitTrusts.services.UnitTrustService.OP2096_REDEEM_FUND;

public class OpCodeParams extends TransactionParams {

    /**
     * The service operation code key.
     */
    public static final String OPCODE_KEY = "opCde";

    private @OpCode
    String opcode;

    @StringDef({
            NULL,
            OP0104_LOGOUT,
            OP0120_CARD_ACCOUNTS,
            OP0120_CARD_LIST,
            OP0121_CARD_FETCH_CHANGE_LIMIT,
            OP0122_CARD_CHANGE_LIMITS_CONFIRM,
            OP0123_CARD_CHANGE_LIMITS_RESULT,
            OP0124_RETRIEVE_PIN,
            OP0125_CARD_LIST,
            OP0202_SHP_LANGUAGE_CHANGE,
            OP0299_UNREDEEMED_CASHSEND,
            OP0310_BENEFICIARY_LIST,
            OP0311_BENEFICIARY_DETAILS,
            OP0312_VIEW_TRANSACTION_DETAILS,
            OP0315_REMOVE_BENEFICIARY,
            OP0327_CASHSEND_UPDATE_ATM_PIN,
            OP0328_VERSION_UPGRADE,

            // All duplicates for payment confirmations
            OP0329_ADD_AIRTIME_CONFIRM,
            OP0329_ADD_PAYMENT_CONFIRMATION,
            OP0329_ADD_CASHSEND_CONFIRM,
            OP0329_ADD_NEW_BENEFICIARY_CONFIRMATION,

            // All duplicates for creation of new beneficiary
            OP0330_ADD_NEW_BENEFICIARY_RESULT,
            OP0330_ADD_AIRTIME_RESULT,
            OP0330_ADD_PAYMENT_RESULT,
            OP0330_ADD_CASHSEND_RESULT,

            // Duplicates
            OP0331_VIEW_AIRTIME_BENEFICIARY,
            OP0331_PREPAID_NETWORK_PROVIDER_LIST,

            // Duplicates
            OP0332_EDIT_AIRTIME_CONFIRM,
            OP0332_EDIT_BENEFICIARY_CONFIRMATION,
            OP0332_EDIT_PAYMENT_CONFIRM,
            OP0332_CONFIRM_EDIT_CASH_SEND_BENEFICIARY,

            // Duplicates
            OP0333_EDIT_AIRTIME_RESULT,
            OP0333_EDIT_PAYMENT_RESULT,
            OP0333_EDIT_CASHSEND_RESULT,

            // Duplicates
            OP0334_ADD_PAYMENT_BENEFICIARY,
            OP0334_REBUILD_SAVE_BENEFICIARY,

            OP0335_VALIDATE_METER_NUMBER,
            OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY,
            OP0339_PURCHASE_PREPAID_ELECTRICITY,
            OP0401_BENEFICIARY_IMAGE_DOWNLOAD,
            OP0402_BENEFICIARY_IMAGE_UPLOAD,
            OP0403_BENEFICIARY_IMAGE_DELETE,
            OP0404_UNKNOWN,
            OP0406_LANGUAGE_UPDATE,
            OP0411_CASHSEND_TRANSACT,
            OP0412_AIRTIME_TRANSACT,
            OP0451_CASHSEND_RESEND_WITHDRAWAL_SMS,
            OP0501_INTER_ACCOUNT_TRANSFER_VALIDATE,
            OP0502_INTER_ACCOUNT_TRANSFER_EXECUTE,
            OP0520_ONCE_OFF_CASHSEND,

            // All duplicates to validate once-off payment
            OP0522_VALIDATE_ONCE_OFF_PAYMENT,
            OP0522_ONCE_OFF_PAYMENT_CONFIRM,

            // Duplicates
            OP0523_PERFORM_ONCE_OFF_PAYMENT,
            OP0523_ONCE_OFF_PAYMENT_RESULT,

            // Duplicates
            OP0525_BENEFICIARY_PAYMENT_CONFIRMATION,
            OP0525_VALIDATE_PAYMENT,

            // Duplicates
            OP0526_BENEFICIARY_PAYMENT_RESULT,
            OP0526_PERFORM_PAYMENT,

            OP0527_VALIDATE_MULTIPLE_BENEFICIARIES_PAYMENT,
            OP0528_MULTIPLE_BENEFICIARIES_PAYMENT_RESULT,
            OP0570_TRANSACTION_AUTHORISATION_LIST,
            OP0571_AUTHORISATION_TRANSACTION_DETAILS,
            OP0572_AUTHORISATION_TRANSACTION_ACCEPT_REJECT,

            // Duplicates
            OP0609_ONCEOFF_CASHSEND_ENCRYPTION,
            OP0609_CASHSEND_PIN_ENCRYPTION,

            OP0610_ONCE_OFF_CASHSEND_CONFIRM,
            OP0611_ONCE_OFF_CASHSEND_RESULT,
            OP0612_SEND_BENEFICIARY_CASHSEND,

            // Duplicates
            OP0613_SEND_BENEFICIARY_CASHSEND_CONFIRM,
            OP0613_VALIDATE_CASHSEND_TO_SELF,

            OP0614_SEND_BENEFICIARY_CASHSEND_RESULT,
            OP0617_ONCE_OFF_AIRTIME,
            OP0618_ONCE_OFF_AIRTIME_CONFIRM,
            OP0619_ONCE_OFF_AIRTIME_RESULT,
            OP0620_BUY_AIRTIME,
            OP0621_BUY_AIRTIME_CONFIRM,
            OP0622_BUY_AIRTIME_RESULT,
            OP0630_PROFILE_PAGE,
            OP0631_CHANGE_LIMITS_CONFIRM,
            OP0632_CHANGE_LIMITS_RESULT,
            OP0786_MY_NOTIFICATION_DETAILS,
            OP0788_ONCE_OFF_PAYMENT_OTHERBANK_AUTOTEXT_BANK_NAME,
            OP0789_ONCE_OFF_PAYMENT_AUTOTEXT,
            OP0790_ONCE_OFF_PAYMENT_OTHERBANK_AUTOTEXT_REBUILD,
            OP0800_GET_DEBIT_CARD_LIST,
            OP0801_GET_REASONS_FOR_DEBIT_CARD_REPLACEMENT,
            OP0802_GET_PRODUCT_TYPE_FOR_DEBIT_CARD_REPLACEMENT,
            OP0803_GET_CARD_TYPE_FOR_DEBIT_CARD_REPLACEMENT,
            OP0804_GET_CLIENT_INFORMATION,
            OP0805_GET_BRANCH_LIST,
            OP0806_CARD_REPLACE_AND_FETCH_FEES,
            OP0807_REPLACE_DEBIT_CARD_CONFIRMATION,
            OP0808_CANCEL_TRANSACTION,
            OP0809_TERMS_AND_CONDITIONS,
            OP0810_CLIENT_AGREEMENT_DETAILS,
            OP0811_QUICK_LINK_DATA,
            OP0812_TRANSACTION_VERIFICATION_STATUS,
            OP0813_UPDATE_USER_ID,
            OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS,
            OP0815_CREATE_2FA_ALIAS_UNAUTHENTICATED_OPCODE,
            OP0816_VALIDATE_PRIMARY_DEVICE_PASSCODE_AND_ATM_PIN,
            OP0817_AUTHENTICATE_ALIAS,
            OP0818_CHANGE_PRIMARY_DEVICE,
            OP0819_VALIDATE_SECURITY_CODE,
            OP0820_VALIDATE_TRANSACTION_VERIFICATION_CODE,
            OP0821_CREATE_2FA_ALIAS_OPCODE,
            OP0822_LIST_DEVICES,
            OP0823_EDIT_NICKNAME_RESULT,
            OP0824_DELINK_DEVICE,
            OP0825_MANAGE_PUSH_NOTIFICATION_RECORD,
            OP0826_DELETE_PUSH_NOTIFICATION_RECORD,
            OP0827_OVERDRAFT_STATUS,
            OP0828_OVERDRAFT_SNOOZE,
            OP0829_DELETE_ALIAS,
            OP0830_PRE_LOGON_VALIDATE_TRANSACTION_VERIFICATION_CODE,
            OP0841_REGISTER_CREDENTIALS,
            OP0842_CREDIT_CARD_INFO,
            OP0843_AUTHORISE_MANDATE,
            OP2103_CHECK_MANDATE_DISPUTABLE,
            OP2104_DISPUTE_MANDATE_TRANSACTION,
            OP0846_DISPLAY_OVERDRAFT_SNOOZE,
            OP0848_CHANGE_PAYMENT_DETAILS_OPCODE,
            OP0849_LINKED_POLICIES,
            OP0850_POLICY_DETAILS,
            OP0851_CALL_ME,
            OP0852_GET_CLIENT_TYPE,
            OP0853_VALIDATE_SUBMIT_CLAIM_NOTIFICATION,
            OP0854_VALIDATE_CLAIM_NOTIFICATION_SUBMISSION,
            OP0857_REGISTER_CLAIMS,
            OP0858_FUNERAL_QUOTES,
            OP0859_COVER_RELATION_APPLY_PLAN,
            OP0860_GET_RETAIL_ACCOUNTS,
            OP0861_LOOKUP,
            OP0862_APPLY_FOR_FUNERAL_PLAN,
            OP0863_VIEW_POLICY_DETAILS,
            OP0864_FETCH_CREDIT_CARD_PROTECTION_INSURANCE_QUOTE,
            OP0865_APPLY_CREDIT_CARD_PROTECTION_INSURANCE,
            OP0866_FETCH_FUTURE_DATED_PAYMENTS,
            OP0867_ADD_NEW_WESTERN_UNION_BENEFICIARY,
            OP0868_PERFORM_INTERNATIONAL_PAYMENT,
            OP0869_CREDIT_CARD_REPLACEMENT_VALIDATION,
            OP0871_CREDIT_CARD_REPLACEMENT_CONFIRMATION,
            OP0872_GET_QUOTE_DETAILS,
            OP0874_GET_ARCHIVED_STATEMENT_LIST,
            OP0875_CALL_ME,
            OP0876_GET_ARCHIVED_STATEMENT,
            OP0877_GET_STAMPED_STATEMENT,
            OP0878_VALIDATE_NEW_WESTERN_UNION_BENEFICIARY,

            // Duplicates
            OP0880_LOOKUP_SERVICE,
            OP0880_CREDIT_CARD_REPLACEMENT_REASONS,

            OP0881_GET_WESTERN_UNION_CURRENCIES,
            OP0882_GET_WESTERN_UNION_BENEFICIARIES,
            OP0883_GET_ALL_WESTERN_UNION_COUNTRIES,
            OP0884_VALIDATE_PAYMENT,
            OP0885_VALIDATE_FOR_HOLIDAYS_AND_TIME,
            OP0886_GET_OUT_BOUNDING_PENDING_TRANSACTION,
            OP0889_GET_WESTERN_UNION_BENEFICIARY_DETAILS,
            OP0890_APPLY_REWARDS,
            OP0891_APPLY_REWARDS_CONFIRM,
            OP0892_APPLY_REWARDS_RESULT,
            OP0893_REPLACEMENT_CREDIT_CARD_NUMBERS,
            OP0897_PERFORM_ONCE_OFF_INTERNATIONAL_PAYMENT,
            OP0905_BUREAU_DATA_FOR_VCL_CLI_PULL,
            OP0906_CREDIT_LIMIT_INCREASE_APPLICATION,
            OP0911_REDEEM_REWARDS,
            OP0912_REDEEM_REWARDS_CONFIRM,
            OP0913_REDEEM_REWARDS_RESULT,
            OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY,
            OP0916_VIEW_REWARDS_MEMBERSHIP_DETAILS,
            OP0917_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE,
            OP0918_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE_RESULT,
            OP0919_REWARDS_DETAILS,
            OP0926_RESEND_NOTICE_OF_PAYMENT,
            OP0965_DEVICES_LIST,
            OP0966_PROFILE_LINK_DEVICE_RESULT,
            OP0967_EDIT_NICKNAME_RESULT,
            OP0984_SURECHECK_REQUEST_TO_PROCESS_TVM,
            OP0985_SURECHECK_REQUEST_TO_PROCESS_TVN,
            OP0986_VALIDATE_PASSWORD,
            OP0987_CREATE_PASS_RESULT,
            OP0990_DECOUPLE_REGISTRATION_USING_ATM,
            OP0991_REGISTRATION_RESULT,
            OP0993_ACCOUNT_REORDERING,
            OP0994_SURECHECK_REQUEST_TO_RESEND_TVM,
            OP0996_GET_PROFILE_IMAGE,
            OP0997_SESSION_KEEP_ALIVE,
            OP0998_REGISTER_CREDENTIALS,
            OP0999_REGISTER_CREDENTIALS,
            OP1000_LOGIN_SECURE_HOME_PAGE,
            OP1301_ACCOUNT_DETAILS,
            OP2001_SECURE_HOME_PAGE,
            OP2004_OVERDRAFT_FICA_CIF_STATUS,
            OP2005_OVERDRAFT_SCORE,
            OP2006_OVERDRAFT_QUOTE_DETAILS,
            OP2007_OVERDRAFT_PDF_QUOTE,
            OP2008_OVERDRAFT_ACCEPT_QUOTE,
            OP2009_OVERDRAFT_REJECT_QUOTE,
            OP2010_OVERDRAFT_QUOTE_STATUS,
            OP2011_UPDATE_CLIENT_AGREEMENT_DETAILS,
            OP2012_PERSONAL_INFORMATION,
            OP2013_OVERDRAFT_QUOTE_SUMMARY,
            OP2019_ENQUIRE_PAUSE_CARD_STATES,
            OP2022_GET_CODES,
            OP2023_CREATE_CUSTOMER_PORTFOLIO,
            OP2025_CASA_SCREENING,
            OP2028_RETRIEVE_ID_OCR_DETAILS_FROM_DOCUMENT,
            OP2031_ADDRESS_LOOKUP,
            OP2032_VALIDATE_CUSTOMER_ADDRESS,
            OP2033_POSTAL_CODE_LOOKUP,
            OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE,
            OP2035_GET_ALL_CONFIGS_FOR_APPLICATION,
            OP2036_GET_SCORING_STATUS,
            OP2037_NEW_TO_BANK_KEEP_ALIVE,
            OP2038_PHOTO_MATCH_AND_MOBILE_LOOKUP,
            OP2040_GET_FILTERED_SITE_DETAILS,
            OP2041_CREATE_COMBI_CARD_ACCOUNT,
            OP2044_PRE_LOGON_VALIDATE_SECURITY_NOTIFICATION,
            OP2045_PRE_LOGON_REQUEST_SECURITY_NOTIFICATION,
            OP2046_PRE_LOGON_RESEND_SECURITY_NOTIFICATION,
            OP2047_REGISTRATION_NEW_APPLICATION_CUSTOMER,
            OP2048_REGISTER_ONLINE_BANKING_PASSWORD,
            OP2049_REQUEST_CALLBACK_OPCODE,
            OP2050_VERIFY_SECRET_CODE_OPCODE,
            OP2052_CREDIT_CARD_HUB,
            OP2053_OFFERS,
            OP2054_MANAGE_CARDS,
            OP2056_UPDATE_TRAVEL_NOTIFICATION,
            OP2057_GET_UNIT_TRUST_ACCOUNTS,
            OP2059_BUY_MORE_UNITS,
            OP2060_RETRIEVE_BUY_MORE_UNITS_LINKED_ACCOUNTS,
            OP2061_RETRIEVE_DEBIT_DAYS,
            OP2064_RETRIEVE_UNIT_TRUST_FUND,
            OP2066_RETRIEVE_LINKED_ACCOUNTS,
            OP2067_CREATE_UNIT_TRUST_ACCOUNT,
            OP2068_VALIDATE_LUMP_SUM,
            OP2070_RETRIEVE_INTEREST_RATE_INFO,
            OP2071_CREATE_ACCOUNT,
            OP2072_CREATE_ACCOUNT_CONFIRM,
            OP2073_CREATE_ACCOUNT_PROCESS,
            OP2074_LINKED_UNLINKED_ACCOUNTS,
            OP2075_LINK_UNLINK_ACCOUNTS,
            OP2076_GET_ACCOUNT_DETAIL,
            OP2078_RETRIEVE_QUOTE,
            OP2079_APPLY_FOR_ULTIMATE_PROTECTOR,
            OP2080_CUSTOMER_RATING_GET_FUNCTIONS_AND_PRODUCTS,
            OP2081_CUSTOMER_RATING_BRANCH_RECOGNITION,
            OP2082_PERFORM_INTER_ACCOUNT_TRANSFER,
            OP2083_BUY_MORE_UNITS_CAPPED,
            OP2084_CASA_RISK_PROFILING,
            OP2086_EDIT_PREPAID_ELECTRICITY_BENEFICIARY,
            OP2087_GET_CASA_STATUS,
            OP2088_GET_RISK_PROFILE,
            OP2089_GET_MARKETING_INDICATOR,
            OP2090_UPDATE_MARKETING_INDICATOR,
            OP2092_GET_FICA_STATUS,
            OP2106_DEVICE_PROFILING_LOGIN_SCORE,
            OP2301_ACCOUNT_HISTORY_CALENDAR_VIEW,
            OP3301_ACCOUNT_HISTORY_TRANSACTION_VIEW,
            OP2110_SEARCH_DEBIT_ORDER,
            OP2111_VIEW_STOPPED_DEBIT_ORDER,
            OP2113_STOP_DEBIT_ORDER,
            OP2114_REVERSE_DEBIT_ORDER,
            OP3301_ACCOUNT_HISTORY_TRANSACTION_VIEW,
            OP2051_BRANCH_LOCATOR,
            OP2095_UNITS_REDEMPTION_ACCOUNT_STATUS,
            OP2096_REDEEM_FUND,
            OP2107_UPDATE_PROFILE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface OpCode {
    }

    public static final String NULL = "";

    public static final String OP2051_BRANCH_LOCATOR = "OP2051";

    public static final String OP0104_LOGOUT = "OP0104";
    public static final String OP0120_CARD_ACCOUNTS = "OP0120";
    public static final String OP0121_CARD_FETCH_CHANGE_LIMIT = "OP0121";
    public static final String OP0122_CARD_CHANGE_LIMITS_CONFIRM = "OP0122";

    /**
     * Card change digitalLimits result
     */
    public static final String OP0123_CARD_CHANGE_LIMITS_RESULT = "OP0123";

    /**
     * Version upgrade
     */
    public static final String OP0328_VERSION_UPGRADE = "OP0328";

    // Duplication that needs to be sorted
    static final String OP0329_ADD_PAYMENT_CONFIRMATION = "OP0329";
    public static final String OP0329_ADD_CASHSEND_CONFIRM = "OP0329";
    public static final String OP0329_ADD_NEW_BENEFICIARY_CONFIRMATION = "OP0329";

    // Duplication that needs to be sorted
    static final String OP0330_ADD_PAYMENT_RESULT = "OP0330";
    public static final String OP0330_ADD_NEW_BENEFICIARY_RESULT = "OP0330";
    public static final String OP0330_ADD_CASHSEND_RESULT = "OP0330";

    /**
     * View airtime beneficiary
     */
    static final String OP0331_VIEW_AIRTIME_BENEFICIARY = "OP0331";

    // Duplication that needs to be sorted
    public static final String OP0332_CONFIRM_EDIT_CASH_SEND_BENEFICIARY = "OP0332";
    public static final String OP0332_EDIT_BENEFICIARY_CONFIRMATION = "OP0332";
    static final String OP0332_EDIT_PAYMENT_CONFIRM = "OP0332";

    // Duplication that needs to be sorted
    public static final String OP0333_EDIT_CASHSEND_RESULT = "OP0333";
    public static final String OP0333_EDIT_PAYMENT_RESULT = "OP0333";

    // Duplication that needs to be sorted
    static final String OP0334_REBUILD_SAVE_BENEFICIARY = "OP0334";
    public static final String OP0334_ADD_PAYMENT_BENEFICIARY = "OP0334";

    /**
     * Beneficiary image delete
     */
    static final String OP0403_BENEFICIARY_IMAGE_DELETE = "OP0403";

    public static final String OP0404_UNKNOWN = "OP0404";

    /**
     * Language set
     */
    public static final String OP0406_LANGUAGE_UPDATE = "OP0406";

    /**
     * Cashsend transact
     */
    static final String OP0411_CASHSEND_TRANSACT = "OP0411";

    /**
     * Airtime transact
     */
    public static final String OP0412_AIRTIME_TRANSACT = "OP0412";

    /**
     * Inter account transfer validation
     */
    public static final String OP0501_INTER_ACCOUNT_TRANSFER_VALIDATE = "OP0501";

    /**
     * Inter account transfer execute
     */
    public static final String OP0502_INTER_ACCOUNT_TRANSFER_EXECUTE = "OP0502";

    // Duplication that needs to be sorted
    static final String OP0522_ONCE_OFF_PAYMENT_CONFIRM = "OP0522";
    public static final String OP0522_VALIDATE_ONCE_OFF_PAYMENT = "OP0522";

    // Duplication that needs to be sorted
    static final String OP0523_ONCE_OFF_PAYMENT_RESULT = "OP0523";
    public static final String OP0523_PERFORM_ONCE_OFF_PAYMENT = "OP0523";

    // Duplication that needs to be sorted
    static final String OP0525_BENEFICIARY_PAYMENT_CONFIRMATION = "OP0525";
    public static final String OP0525_VALIDATE_PAYMENT = "OP0525";

    // Duplication that needs to be sorted
    static final String OP0526_BENEFICIARY_PAYMENT_RESULT = "OP0526";
    public static final String OP0526_PERFORM_PAYMENT = "OP0526";

    public static final String OP0527_VALIDATE_MULTIPLE_BENEFICIARIES_PAYMENT = "OP0527";
    public static final String OP0570_TRANSACTION_AUTHORISATION_LIST = "OP0570";
    public static final String OP0571_AUTHORISATION_TRANSACTION_DETAILS = "OP0571";
    public static final String OP0572_AUTHORISATION_TRANSACTION_ACCEPT_REJECT = "OP0572";

    // Duplication that needs to be sorted
    public static final String OP0609_ONCEOFF_CASHSEND_ENCRYPTION = "OP0609";
    public static final String OP0609_CASHSEND_PIN_ENCRYPTION = "OP0609";

    // Duplication that needs to be sorted
    static final String OP0613_SEND_BENEFICIARY_CASHSEND_CONFIRM = "OP0613";
    public static final String OP0613_VALIDATE_CASHSEND_TO_SELF = "OP0613";

    /**
     * Profile page
     */
    public static final String OP0630_PROFILE_PAGE = "OP0630";

    /**
     * Change digitalLimits confirmation
     */
    public static final String OP0631_CHANGE_LIMITS_CONFIRM = "OP0631";

    /**
     * Change digitalLimits result
     */
    public static final String OP0632_CHANGE_LIMITS_RESULT = "OP0632";

    /**
     * Once off payment otherbank autotext bank name
     */
    public static final String OP0788_ONCE_OFF_PAYMENT_OTHERBANK_AUTOTEXT_BANK_NAME = "OP0788";

    /**
     * Once off payment autotext
     */
    public static final String OP0789_ONCE_OFF_PAYMENT_AUTOTEXT = "OP0789";

    /**
     * Once off payment otherbank autotext rebuild
     */
    public static final String OP0790_ONCE_OFF_PAYMENT_OTHERBANK_AUTOTEXT_REBUILD = "OP0790";
    public static final String OP0800_GET_DEBIT_CARD_LIST = "OP0800";
    public static final String OP0801_GET_REASONS_FOR_DEBIT_CARD_REPLACEMENT = "OP0801";
    public static final String OP0802_GET_PRODUCT_TYPE_FOR_DEBIT_CARD_REPLACEMENT = "OP0802";
    public static final String OP0803_GET_CARD_TYPE_FOR_DEBIT_CARD_REPLACEMENT = "OP0803";
    public static final String OP0804_GET_CLIENT_INFORMATION = "OP0804";
    public static final String OP0805_GET_BRANCH_LIST = "OP0805";
    public static final String OP0806_CARD_REPLACE_AND_FETCH_FEES = "OP0806";
    public static final String OP0807_REPLACE_DEBIT_CARD_CONFIRMATION = "OP0807";

    /**
     * Terms and conditions
     */
    public static final String OP0809_TERMS_AND_CONDITIONS = "OP0809";
    public static final String OP0810_CLIENT_AGREEMENT_DETAILS = "OP0810";
    public static final String OP0811_QUICK_LINK_DATA = "OP0811";
    public static final String OP0813_UPDATE_USER_ID = "OP0813";

    public static final String OP0819_VALIDATE_SECURITY_CODE = "OP0819";

    /**
     * Overdraft
     */
    public static final String OP0827_OVERDRAFT_STATUS = "OP0827";
    public static final String OP0828_OVERDRAFT_SNOOZE = "OP0828";


    public static final String OP0841_REGISTER_CREDENTIALS = "OP0841";

    /**
     * Overdraft status for account
     */
    //Call me on policy details
    public static final String OP0851_CALL_ME = "OP0851";
    //Call me reference number submit on policy details
    public static final String OP0875_CALL_ME = "OP0875";

    // Duplication that needs to be sorted
    static final String OP0880_LOOKUP_SERVICE = "OP0880";
    public static final String OP0880_CREDIT_CARD_REPLACEMENT_REASONS = "OP0880";

    /**
     * Linked policies
     */
    public static final String OP0843_AUTHORISE_MANDATE = "OP0843";
    public static final String OP2104_DISPUTE_MANDATE_TRANSACTION = "OP2104";
    public static final String OP2103_CHECK_MANDATE_DISPUTABLE = "OP2103";

    /**
     * Policy Details
     */
    public static final String OP0850_POLICY_DETAILS = "OP0850";

    /**
     * Validate submit claim notification
     */
    public static final String OP0853_VALIDATE_SUBMIT_CLAIM_NOTIFICATION = "OP0853";

    /**
     * Validate submit claim notification
     */
    public static final String OP0854_VALIDATE_CLAIM_NOTIFICATION_SUBMISSION = "OP0854";

    /**
     * Register geyser claim
     */
    public static final String OP0857_REGISTER_CLAIMS = "OP0857";

    /**
     * Request Archived Statement List
     */
    public static final String OP0874_GET_ARCHIVED_STATEMENT_LIST = "OP0874";

    /**
     * Request Stamped Statement PDF
     */
    public static final String OP0877_GET_STAMPED_STATEMENT = "OP0877";

    /**
     * Apply rewards
     */
    public static final String OP0890_APPLY_REWARDS = "OP0890";

    /**
     * Apply rewards confirmation
     */
    public static final String OP0891_APPLY_REWARDS_CONFIRM = "OP0891";

    /**
     * Apply rewards result
     */
    public static final String OP0892_APPLY_REWARDS_RESULT = "OP0892";

    /**
     * Redeem rewards
     */
    public static final String OP0911_REDEEM_REWARDS = "OP0911";

    /**
     * Redeem rewards transaction history
     */
    public static final String OP0916_VIEW_REWARDS_MEMBERSHIP_DETAILS = "OP0916";

    /**
     * Redeem rewards membership details set
     */
    public static final String OP0917_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE = "OP0917";

    /**
     * Redeem rewards membership details set result
     */
    public static final String OP0918_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE_RESULT = "OP0918";
    public static final String OP0919_REWARDS_DETAILS = "OP0919";
    static final String OP0965_DEVICES_LIST = "OP0965";

    /**
     * Profile link device result
     */
    static final String OP0966_PROFILE_LINK_DEVICE_RESULT = "OP0966";

    static final String OP0967_EDIT_NICKNAME_RESULT = "OP0967";

    /**
     * Surecheck request to process tvm
     */
    static final String OP0984_SURECHECK_REQUEST_TO_PROCESS_TVM = "OP0984";

    /**
     * Surecheck request to process tvn
     */
    static final String OP0985_SURECHECK_REQUEST_TO_PROCESS_TVN = "OP0985";

    /**
     * Validate password
     */
    public static final String OP0986_VALIDATE_PASSWORD = "OP0986";

    /**
     * Create pass result
     */
    public static final String OP0987_CREATE_PASS_RESULT = "OP0987";

    /**
     * Decouple registration using atm
     */
    public static final String OP0990_DECOUPLE_REGISTRATION_USING_ATM = "OP0990";

    /**
     * Registration result
     */
    public static final String OP0991_REGISTRATION_RESULT = "OP0991";

    /**
     * Surecheck request to resend tvm
     */
    static final String OP0994_SURECHECK_REQUEST_TO_RESEND_TVM = "OP0994";

    /**
     * Get profile image
     */
    public static final String OP0996_GET_PROFILE_IMAGE = "OP0996";

    /**
     * Session keep alive
     */
    public static final String OP0997_SESSION_KEEP_ALIVE = "OP0997";

    /**
     * Register credentials
     */
    public static final String OP0998_REGISTER_CREDENTIALS = "OP0998";

    /**
     * Login secure home page
     */
    public static final String OP1000_LOGIN_SECURE_HOME_PAGE = "OP1000";

    /**
     * Secure home page
     */
    public static final String OP2001_SECURE_HOME_PAGE = "OP2001";
    public static final String OP2011_UPDATE_CLIENT_AGREEMENT_DETAILS = "OP2011";

    public static final String OP2106_DEVICE_PROFILING_LOGIN_SCORE = "OP2106";

    /**
     * Account history calendar view
     */
    public static final String OP2301_ACCOUNT_HISTORY_CALENDAR_VIEW = "OP2301";

    /**
     * Account history transaction view
     */
    static final String OP3301_ACCOUNT_HISTORY_TRANSACTION_VIEW = "OP3301";

    public static final String OP0999_REGISTER_CREDENTIALS = "OP0999";

    public void put(@OpCode String opCode) {
        this.opcode = opCode;
        put(OPCODE_KEY, opCode);
    }

    public @OpCode
    String getOpCode() {
        return opcode;
    }
}
