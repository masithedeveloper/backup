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
package com.barclays.absa.banking.framework;

import com.barclays.absa.banking.framework.app.BMBApplication;

public class MockFactory {
    public static String login() {//OP1000
        final int choice = 0;
        switch (choice) {
            default:
                return "login/op1000_login_secure_home_page_success.json"; // Has credit card
            case 0:
                return "login/op1000_login_secure_home_page_success_express.json";
            case 1:
                return "login/op1000_login_secure_home_page_success_short.json";
            case 3:
                return "login/op1000_login_secure_home_page_success_business_operator.json";
            case 4:
                return "login/op1000_connection_error.json";
            case 5:
                return "invalid_auth_level.json";
            case 6:
                return "login/op1000_login_secure_home_page_failure.json";
            case 7:
                return "login/op1000_login_secure_home_page_success_one_account.json";
            case 8:
                return "login/op1000_login_secure_home_page_success_two_accounts_rewards.json";
            case 10:
                return "login/op1000_login_secure_home_page_success_operator_history_not_allowed.json";
            case 11:
                return "login/op1000_login_secure_home_page_success_operator_mask_balance.json";
            case 13:
                return BMBApplication.getApplicationLocale().getLanguage().equalsIgnoreCase("en") ? "login/op1000_login_secure_home_page_success_various_accounts.json" : "login/op1000_login_secure_home_page_success_various_accounts_afrikaans.json";
            case 19:
                return "login/op1000_login_partial_reg_limits_set.json";
            case 20:
                return "login/op1000_login_secure_home_page_success_retail_operator.json";
            case 22:
                return "login/op1000_login_secure_home_page_success_business.json"; // Has home loan
            case 23:
                return "login/op1000_login_secure_home_page_unpaid_suspended.json";
            case 24:
                return "login/op1000_password_reset.json";
        }
    }

    public static String secureHomePage() {//OP2001
        final int choice = -1;
        switch (choice) {
            default:
                return login();
            case 1:
                return "login/op1000_connection_error.json";
        }
    }

    public static String authorizationsPending() {
        final int choice = -1;
        switch (choice) {
            default:
                return "op0570_transaction_authorisation_list_5.json";
            case 1:
                return "op0570_transaction_authorisation_list.json";
        }
    }

    public static String accountDetails() {
        return accountDetails(-1);
    }

    private static String accountDetails(int choice) {
        switch (choice) {
            default:
                return "account/op1301_account_details.json";
            case 1:
                return "account/op1301_account_details_short_custom_one_day.json";
            case 14:
                return "account/op1301_account_details_short_custom_one.json";
            case 17:
                return "account/op1301_account_details_short.json";
            case 22:
                return "account/op1301_account_details_short_custom.json"; // negatif then positive balancwe
            case 26:
                return "account/op1301_account_details_short_custom_negatif.json";
            case 3:
                return "account/op1301_account_details_account_closed.json";
            case 4:
                return "account/op1301_account_details_short_custom_two.json";
            case 5:
                return "account/op1301_account_details_short_custom_three.json";
            case 7:
                return "account/op1301_account_details_short_account_history_not_allowed.json";
            case 8:
                return "bmb99999_connection_error.json";
            case 9:
                return "invalid_auth_level.json";
        }
    }

    public static String validateMeterNumber() {
        final int choice = -1;
        switch (choice) {
            default:
                return "electricity/op0335_validate_meter_number.json";
            case 1:
                return "electricity/op0335_valid_meter_number_error_invalid_or_blocked.json";
        }
    }

    private static String creditCardProtection(int choice) {
        switch (choice) {
            case 1:
                return "credit_card_insurance/op0864_fetch_credit_card_protection_insurance.json";
            default:
                return "credit_card_insurance/op0864_fetch_credit_card_protection_insurance_failed.json";
        }
    }

    public static String funeralCoverQuotes() {
        final int choice = -1;
        return funeralCoverQuotes(choice);
    }

    private static String funeralCoverQuotes(int choice) {
        switch (choice) {
            case 1:
                return "funeral_cover/op0858_funeral_cover_quotes_age_below_limit.json";
            case 2:
                return "funeral_cover/op0858_funeral_quotes_age_over_limit.json";
            default:
                return "funeral_cover/opo858_funeral_quotes.json";
        }
    }

    public static String userAccountLinkedPolicies() {
        int choice = 1;
        return linkedPolicies(choice);
    }

    private static String linkedPolicies(int choice) {
        switch (choice) {
            case -1:
                return "funeral_cover/op0849_no_linked_policies.json";
            default:
                return "funeral_cover/op0849_linked_policies.json";
        }
    }

    public static String pendingMandates() {
        return "debicheck/op2108_get_number_of_pending_mandates.json";
    }

    public static String register() {
        final int choice = -1;
        switch (choice) {
            default:
                return "registration/op0990_register_success.json";
            case 10: // UAT error
                return "registration/op0990_register_uat_error.json";
            case 20: // Prod error
                return "registration/op0990_register_prod_error.json";
        }
    }

    public static String verifyCallBackCode() {
        return "click_to_call/op2050_success.json";
    }

    public static String requestCallBack() {
        return "click_to_call/op2049_call_back_request.json";
    }

    public static String onceOffAirtimeResult() {
        final int choice = -1;
        switch (choice) {
            default:
                return "airtime/op0619_once_off_airtime_result_success.json";
            case 10:
                return "airtime/op0619_once_off_airtime_result_surecheck2required.json";
            case 11:
                return "airtime/op0619_once_off_airtime_failure.json";
        }
    }

    public static String cashSendUnredeemedTransactions() {
        return "cash_send/op0299_cash_send_unredeem_test.json";
    }

    public static String getUnitTrustAccountList() {
        return "funeral_cover/op2057_view_unit_trust_account.json";
    }

    public static String ficaStatus() {
        final int choice = -1;
        switch (choice) {
            default:
                return "risk_based_approach/op2092_get_fica_status.json";
            case 1:
                return "risk_based_approach/op2092_get_fica_status_failure.json";
        }
    }

    public static String lifeCoverQuotation() {
        final int choice = -1;
        switch (choice) {
            default:
                return "ultimate_protector/op2078_get_quote_success.json";
            case 0:
                return "ultimate_protector/op2078_get_quote_failure.json";
        }
    }

    public static String lifeCoverApplicationResult(final int choice) {
        switch (choice) {
            default:
                return "ultimate_protector/op2079_apply_for_ultimate_protector_failure.json";
            case 0:
                return "ultimate_protector/op2079_apply_for_ultimate_protector_success_surecheck_required.json";
            case 1:
                return "ultimate_protector/op2079_apply_for_ultimate_protector_success.json";
        }
    }

    public static String exploreHubOffers() {
        final int option = -1;
        switch (option) {
            default:
                return "offer/op2053_list_of_offers.json";
            case 1:
                return "offer/op2053_list_of_offers_with_direct_marketing.json";
        }
    }

    public static String getCasaRiskProfilingResult(final int choice) {
        switch (choice) {
            case 0:
                return "new_to_bank/op2084_casa_risk_profiling_success.json";
            case 1:
                return "op2084_casa_risk_profiling.json";
            default:
                return "new_to_bank/op2084_casa_risk_profiling_success.json";
        }
    }

    public static String suburbs() {
        return "shared/op2099_get_suburbs.json";
    }
}
