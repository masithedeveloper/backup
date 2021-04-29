/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.services

import com.barclays.absa.banking.boundary.model.AccountDetail
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.boundary.model.SecureHomePageObject
import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtection
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.boundary.shared.dto.LinkingTransactionDetails
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultipleResponse.CashSendPlusSendMultipleDetails
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.express.notificationDetails.dto.NotificationDetailsResponse
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.funeralCover.ui.ChangePaymentDetails
import com.barclays.absa.banking.linking.services.Service
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse

class AppCacheService : IAppCacheService {

    companion object {
        private const val IMI_SESSION_ACTIVE = "imi_session_active"
        private const val IS_TRANSACTIONAL_USER = "is_transactional_users"
        private const val SHOULD_UPDATE_EXPLORE_HUB = "should_update_explore_hub"
        private const val EXPLORE_HUB_OFFERS = "explore_hub_offers"
        private const val CUSTOMER_SESSION_ID = "customer_session_id"
        private const val HAS_ALREADY_CALLED_FOR_SCORE = "hasAlreadyCalledForScore"
        private const val CELLPHONE_NUMBER = "cellphone_number"
        private const val IS_PASSWORD_RESET_FLOW = "isPasswordResetFlow"
        private const val HAS_REWARDS_ACCOUNT = "has_rewards_account"
        private const val POLICY_FEE = "policy_fee"
        private const val POLICY_DETAIL = "policyDetail"
        private const val CACHE_AVAILABLE = "cache_available"
        private const val USER_LOGGED_IN_STATUS = "user_logged_in_status"
        private const val IS_SECONDARY_DEVICE = "is_secondary_device"
        private const val LINKING_PASSCODE = "linking_passcode"
        private const val IS_IN_NO_PRIMARY_DEVICE_STATE = "no_primary_state"
        private const val AUTH_CREDENTIAL_TYPE = "auth_credential_type"
        private const val SCAN_QR_FLOW = "scan_qr_flow"
        private const val PASSCODE_RESET_FLOW = "passcode_reset_flow"
        private const val IS_CREDIT_CARD_FLOW = "is_credit_card_flow"
        private const val NO_PRIMARY_DEVICE_PASSCODE_AND_ATM_VALIDATION_FAILED = "no_primary_device_passcode_and_atm_validation_failed"
        private const val ACCESS_ACCOUNT_LOGIN_CALL = "access_account_login"
        private const val PRIMARY_SECOND_FACTOR_DEVICE = "primary_second_factor_device"
        private const val HAS_DISPLAYED_SURECHECK_COUNTDOWNN = "has_displayed_surecheck_countdown"
        private const val PASSCODE = "passcode"
        private const val TRUST_TOKEN = "trust_token"
        private const val DEVICE_NICKNAME = "deviceNickname"
        private const val CREDIT_PROTECTION = "credit_protection"
        private const val ACCOUNT_DETAIL = "accountDetail"
        private const val TRANSACTIONS = "transactions"
        private const val CASH_SEND_PLUS_SEND_MULTIPLE_RESPONSE_DETAILS = "cash_Send_plus_Send_multiple_response_details"
        private const val CASH_SEND_PLUS_SEND_MULTIPLE_BENEFICIARIES_PAYMENT_DETAILS = "cash_Send_plus_Send_multiple_beneficiaries_payment_details"
        private const val FILTERED_CREDIT_CARD_TRANSACTIONS = "screen_to_return_to"
        private const val CREDIT_CARD_INFORMATION = "credit_card_information"
        private const val CURRENT_PRIMARY_DEVICE = "current_primary_device"
        private const val CASHSEND_PLUS_REGISTRATION_STATUS = "cashsend_plus_reg_status"
        private const val ANALYTICS_SCREEN_NAME = "analytics_screen_name"
        private const val ANALYTICS_APP_SECTION = "analytics_app_section"
        private const val MULTIPLE_PAYMENT_SELECTED_BENEFICIARY_LIST = "multiple_payment_selected_beneficiary_list"
        private const val DELINKED_PRIMARY_DEVICE = "delinked_primary_device"
        private const val AUTH_CREDENTIAL = "auth_credential"
        private const val ENROLLING_USER_ALIAS_ID = "enrolling_user_alias_id"
        private const val SCREEN_TO_RETURN_TO = "screen_to_return_to"
        private const val CREATE_2FA_ALIAS_RESPONSE = "create_2fa_alias_response"
        private const val CURRENT_RESPONSE = "current_response"
        private const val SECURITY_CODE_DELEGATE = "security_code_delegate"
        private const val CHANGE_PAYMENT_DETAILS = "change_payment_details"
        private const val SECURE_HOME_PAGE_OBJECT = "secure_home_page_object"
        private const val SURE_CHECK_DELEGATE = "sure_check_delegate"
        private const val SURE_CHECK_REFERENCE_NUMBER = "sure_check_reference_number"
        private const val SURE_CHECK_CELLPHONE_NUMBER = "sure_check_cellphone_number"
        private const val SURE_CHECK_EMAIL = "sure_check_email"
        private const val SURE_CHECK_NOTIFICATION_METHOD = "sure_check_notification_method"
        private const val NOTIFICATION_DETAILS = "notification_details"

        private const val CURRENT_DEVICE = "currentDevice"
        private const val IS_CHANGE_PRIMARY_DEVICE_FLOW = "isChangePrimaryDeviceFlow"
        private const val IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_SURECHECK = "isChangePrimaryDeviceFlowFromSureCheck"
        private const val IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_FAIL_OVER = "isChangePrimaryDeviceFlowFromFailOver"
        private const val IS_CHANGE_PRIMARY_DEVICE_NO_DEVICE = "isChangePrimaryDeviceFromNoPrimaryDeviceFlow"
        private const val BIO_REFERENCE = "bioReference"
        private const val REQUEST_ID = "requestId"
        private const val ORIGINAL_SURE_CHECK_TYPE = "originalSureCheckType"
        private const val IS_BIO_AUTHENTICATED = "isBioAuthenticated"
        private const val SELECTED_PROFILE = "selectedProfile"
        private const val IS_IDENTIFICATION_AN_VERIFICATION_LINKING_FLOW = "isIdentificationAndVerificationLinkingFlow"
        private const val IS_IDENTIFICATION_AN_VERIFICATION_POST_LOGIN = "isIdentificationAndVerificationPostLogin"
        private const val IS_FORGOT_PRIMARY_DEVICE_BUTTON_CLICKED = "isForgotPrimaryDeviceButtonClicked"
        private const val LAST_SURE_CHECK_DELEGATE = "lastSureCheckDelegate"
        private const val IS_IDENTIFICATION_AND_VERIFICATION_FLOW = "isIdentificationAndVerificationFlow"
        private const val LINKING_TRANSACTION_DETAILS = "linking_transaction_details"
        private const val ENTERPRISE_SESSION_ID = "enterpriseSessionId"
        private const val LINKING_HAS_PRIMARY_DEVICE = "linkingHasPrimaryDevice"
        private const val SHOULD_REVERT_TO_OLD_FLOW = "shouldRevertToOldFlow"
        private const val CUSTOMER_ID_NUMBER = "customerIdNumber"
        private const val FROM_MANAGE_DEVICES_FLOW = "fromManageDeviceFlow"
        private const val IS_CURRENT_DEVICE_PROCESSING_SURE_CHECK = "isCurrentDeviceProcessingSureCheck"
        private const val IS_IMI_PROFILE_REGISTERED = "isImiProfileRegistered"
    }

    private var hasErrorResponse = false
    private var BACKING_MAP: HashMap<String, Any> = HashMap()

    override fun isScanQRFlow(): Boolean = BACKING_MAP[SCAN_QR_FLOW] as? Boolean ?: false
    override fun setScanQRFlow(isScanQRFlows: Boolean) {
        BACKING_MAP[SCAN_QR_FLOW] = isScanQRFlows
    }

    override fun getAuthCredentialType(): Int = BACKING_MAP[AUTH_CREDENTIAL_TYPE] as? Int ?: 0
    override fun setAuthCredentialType(credentialType: Int) {
        BACKING_MAP[AUTH_CREDENTIAL_TYPE] = credentialType
    }

    override fun isInNoPrimaryDeviceState(): Boolean = BACKING_MAP[IS_IN_NO_PRIMARY_DEVICE_STATE] as? Boolean ?: false
    override fun setNoPrimaryDeviceState(isInNoPrimaryDeviceState: Boolean) {
        BACKING_MAP[IS_IN_NO_PRIMARY_DEVICE_STATE] = isInNoPrimaryDeviceState
    }

    override fun isLinkingFlow(): Boolean = BACKING_MAP[LINKING_PASSCODE] as? Boolean ?: false
    override fun setLinkingFlow(isLinkingFlow: Boolean) {
        BACKING_MAP[LINKING_PASSCODE] = isLinkingFlow
    }

    override fun isSecondaryDevice(): Boolean = BACKING_MAP[IS_SECONDARY_DEVICE] as? Boolean ?: false
    override fun setIsSecondaryDevice(isSecondaryDevice: Boolean) {
        BACKING_MAP[IS_SECONDARY_DEVICE] = isSecondaryDevice
    }

    override fun getUserLoggedInStatus(): Boolean = BACKING_MAP[USER_LOGGED_IN_STATUS] as? Boolean ?: false
    override fun setUserLoggedInStatus(isLoggedIn: Boolean) {
        BACKING_MAP[USER_LOGGED_IN_STATUS] = isLoggedIn
    }

    override fun isCacheAvailable(): Boolean = BACKING_MAP[CACHE_AVAILABLE] as? Boolean ?: false
    override fun setCacheAvailable(available: Boolean) {
        BACKING_MAP[CACHE_AVAILABLE] = available
    }

    override fun getPolicyDetail(): PolicyDetail? = BACKING_MAP[POLICY_DETAIL] as? PolicyDetail
    override fun setPolicyDetail(policyDetail: PolicyDetail) {
        BACKING_MAP[POLICY_DETAIL] = policyDetail
    }

    override fun getPolicyFee(): String = BACKING_MAP[POLICY_FEE] as? String ?: ""
    override fun setPolicyFee(policyFee: String) {
        BACKING_MAP[POLICY_FEE] = policyFee
    }

    override fun hasRewardsAccount(): Boolean = BACKING_MAP[HAS_REWARDS_ACCOUNT] as? Boolean ?: false
    override fun setHasRewardsAccount(hasRewardsAccount: Boolean) {
        BACKING_MAP[HAS_REWARDS_ACCOUNT] = hasRewardsAccount
    }

    override fun isPasswordResetFlow(): Boolean = BACKING_MAP[IS_PASSWORD_RESET_FLOW] as? Boolean ?: false
    override fun setIsPasswordResetFlow(isPasswordReset: Boolean) {
        BACKING_MAP[IS_PASSWORD_RESET_FLOW] = isPasswordReset
    }

    override fun getCellphoneNumber(): String? = BACKING_MAP[CELLPHONE_NUMBER] as? String
    override fun setCellphoneNumber(cellphoneNumber: String) {
        BACKING_MAP[CELLPHONE_NUMBER] = cellphoneNumber
    }

    override fun hasCalledForScoreInThisSession(): Boolean = (BACKING_MAP[HAS_ALREADY_CALLED_FOR_SCORE] as? Boolean) ?: false
    override fun setAlreadyCalledForScoreInThisSession(alreadyCalledForScoreInThisSession: Boolean) {
        BACKING_MAP[HAS_ALREADY_CALLED_FOR_SCORE] = alreadyCalledForScoreInThisSession
    }

    override fun getCustomerSessionId(): String? = BACKING_MAP[CUSTOMER_SESSION_ID] as? String
    override fun setCustomerSessionId(customerSessionId: String) {
        BACKING_MAP[CUSTOMER_SESSION_ID] = customerSessionId
    }

    override fun isTransactionalUser(): Boolean = BACKING_MAP[IS_TRANSACTIONAL_USER] as? Boolean ?: false
    override fun setIsTransactionalUser(isTransactionalUser: Boolean) {
        BACKING_MAP[IS_TRANSACTIONAL_USER] = isTransactionalUser
    }

    override fun shouldUpdateExploreHub(): Boolean = BACKING_MAP[SHOULD_UPDATE_EXPLORE_HUB] as? Boolean ?: true
    override fun setShouldUpdateExploreHub(shouldUpdate: Boolean) {
        BACKING_MAP[SHOULD_UPDATE_EXPLORE_HUB] = shouldUpdate
    }

    override fun getExploreHubOffers(): OffersResponseObject? = BACKING_MAP[EXPLORE_HUB_OFFERS] as OffersResponseObject?
    override fun setExploreHubOffers(offersResponseObject: OffersResponseObject) {
        BACKING_MAP[EXPLORE_HUB_OFFERS] = offersResponseObject
    }

    override fun isImiSessionActive(): Boolean = BACKING_MAP[IMI_SESSION_ACTIVE] as? Boolean ?: false
    override fun setImiSessionActive(isImiSessionActive: Boolean) {
        BACKING_MAP[IMI_SESSION_ACTIVE] = isImiSessionActive
    }

    override fun isPasscodeResetFlow(): Boolean = BACKING_MAP[PASSCODE_RESET_FLOW] as? Boolean ?: false
    override fun setPasscodeResetFlow(isPasscodeResetFlow: Boolean) {
        BACKING_MAP[PASSCODE_RESET_FLOW] = isPasscodeResetFlow
    }

    override fun isCreditCardFlow(): Boolean = BACKING_MAP[IS_CREDIT_CARD_FLOW] as? Boolean ?: false
    override fun setCreditCardFlow(isCreditCardFlow: Boolean) {
        BACKING_MAP[IS_CREDIT_CARD_FLOW] = isCreditCardFlow
    }

    override fun hasNoPrimaryDeviceVerificationErrorOccurred(): Boolean = BACKING_MAP[NO_PRIMARY_DEVICE_PASSCODE_AND_ATM_VALIDATION_FAILED] as? Boolean ?: false
    override fun setNoPrimaryDeviceVerificationErrorOccurred(noPrimaryDevicePasscodeAndAtmCredentialsVerificationFailed: Boolean) {
        BACKING_MAP[NO_PRIMARY_DEVICE_PASSCODE_AND_ATM_VALIDATION_FAILED] = noPrimaryDevicePasscodeAndAtmCredentialsVerificationFailed
    }

    override fun isAccessAccountLogin(): Boolean = BACKING_MAP[ACCESS_ACCOUNT_LOGIN_CALL] as? Boolean ?: false
    override fun setAccessAccountLogin(isAccessAccountLogin: Boolean) {
        BACKING_MAP[ACCESS_ACCOUNT_LOGIN_CALL] = isAccessAccountLogin
    }

    override fun isPrimarySecondFactorDevice(): Boolean = BACKING_MAP[PRIMARY_SECOND_FACTOR_DEVICE] as? Boolean ?: false
    override fun setPrimarySecondFactorDevice(isPrimaryDevice: Boolean) {
        BACKING_MAP[PRIMARY_SECOND_FACTOR_DEVICE] = isPrimaryDevice
    }

    override fun hasDisplayedSureCheckCountdown(): Boolean = BACKING_MAP[HAS_DISPLAYED_SURECHECK_COUNTDOWNN] as? Boolean ?: false
    override fun setDisplayedSureCheckCountDown(hasDisplayedSureCheckCountDown: Boolean) {
        BACKING_MAP[HAS_DISPLAYED_SURECHECK_COUNTDOWNN] = hasDisplayedSureCheckCountDown
    }

    override fun getPasscode(): String? = BACKING_MAP[PASSCODE] as? String
    override fun setPasscode(passcode: String) {
        BACKING_MAP[PASSCODE] = passcode
    }

    override fun getTrustToken(): String? = BACKING_MAP[TRUST_TOKEN] as? String
    override fun setTrustToken(token: String) {
        BACKING_MAP[TRUST_TOKEN] = token
    }

    override fun getDeviceNickname(): String? = BACKING_MAP[DEVICE_NICKNAME] as? String
    override fun setDeviceNickname(nickName: String) {
        BACKING_MAP[DEVICE_NICKNAME] = nickName
    }

    override fun getCreditProtection(): CreditProtection? = BACKING_MAP[CREDIT_PROTECTION] as? CreditProtection
    override fun setCreditProtection(creditProtection: CreditProtection) {
        BACKING_MAP[CREDIT_PROTECTION] = creditProtection
    }

    override fun getAccountDetail(): AccountDetail? = BACKING_MAP[ACCOUNT_DETAIL] as? AccountDetail
    override fun setAccountDetail(accountDetail: AccountDetail) {
        BACKING_MAP[ACCOUNT_DETAIL] = accountDetail
    }

    override fun getTransactions(): AccountDetail? = BACKING_MAP[TRANSACTIONS] as? AccountDetail
    override fun setTransactions(transactionInfo: AccountDetail) {
        BACKING_MAP[TRANSACTIONS] = transactionInfo
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCashSendPlusSendMultipleResponseDetails(): List<CashSendPlusSendMultipleDetails> = BACKING_MAP[CASH_SEND_PLUS_SEND_MULTIPLE_RESPONSE_DETAILS] as? List<CashSendPlusSendMultipleDetails> ?: emptyList()
    override fun setCashSendPlusSendMultipleResponseDetails(beneficiaryList: List<CashSendPlusSendMultipleDetails>) {
        BACKING_MAP[CASH_SEND_PLUS_SEND_MULTIPLE_RESPONSE_DETAILS] = beneficiaryList
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCashSendPlusSendMultipleBeneficiariesPaymentDetails(): List<CashSendPlusSendMultiplePaymentDetails> = BACKING_MAP[CASH_SEND_PLUS_SEND_MULTIPLE_BENEFICIARIES_PAYMENT_DETAILS] as? List<CashSendPlusSendMultiplePaymentDetails> ?: emptyList()
    override fun setCashSendPlusSendMultipleBeneficiariesPaymentDetails(beneficiaryList: List<CashSendPlusSendMultiplePaymentDetails>) {
        BACKING_MAP[CASH_SEND_PLUS_SEND_MULTIPLE_BENEFICIARIES_PAYMENT_DETAILS] = beneficiaryList
    }

    override fun getFilteredCreditCardTransactions(): AccountDetail? = BACKING_MAP[FILTERED_CREDIT_CARD_TRANSACTIONS] as? AccountDetail
    override fun setFilteredCreditCardTransactions(transactionInfo: AccountDetail) {
        BACKING_MAP[FILTERED_CREDIT_CARD_TRANSACTIONS] = transactionInfo
    }

    override fun getCreditCardInformation(): CreditCardInformation? = BACKING_MAP[CREDIT_CARD_INFORMATION] as? CreditCardInformation
    override fun setCreditCardInformation(creditCardInformation: CreditCardInformation) {
        BACKING_MAP[CREDIT_CARD_INFORMATION] = creditCardInformation
    }

    override fun getCurrentPrimaryDevice(): Device? = BACKING_MAP[CURRENT_PRIMARY_DEVICE] as? Device
    override fun setCurrentPrimaryDevice(primaryDevice: Device) {
        BACKING_MAP[CURRENT_PRIMARY_DEVICE] = primaryDevice
    }

    override fun getCashSendPlusRegistrationStatus(): CheckCashSendPlusRegistrationStatusResponse? = BACKING_MAP[CASHSEND_PLUS_REGISTRATION_STATUS] as? CheckCashSendPlusRegistrationStatusResponse
    override fun setCashSendPlusRegistrationStatus(registrationStatusResponse: CheckCashSendPlusRegistrationStatusResponse) {
        BACKING_MAP[CASHSEND_PLUS_REGISTRATION_STATUS] = registrationStatusResponse
    }

    override fun getAnalyticsScreenName(): String? = BACKING_MAP[ANALYTICS_SCREEN_NAME] as? String
    override fun setAnalyticsScreenName(screenName: String) {
        BACKING_MAP[ANALYTICS_SCREEN_NAME] = screenName
    }

    override fun getAnalyticsAppSection(): String? = BACKING_MAP[ANALYTICS_APP_SECTION] as? String
    override fun setAnalyticsAppSection(sectionName: String) {
        BACKING_MAP[ANALYTICS_APP_SECTION] = sectionName
    }

    override fun getChangePaymentDetails(): ChangePaymentDetails? = BACKING_MAP[CHANGE_PAYMENT_DETAILS] as? ChangePaymentDetails
    override fun setChangePaymentDetails(changePaymentDetails: ChangePaymentDetails) {
        BACKING_MAP[CHANGE_PAYMENT_DETAILS] = changePaymentDetails
    }

    override fun getSecureHomePageObject(): SecureHomePageObject? = BACKING_MAP[SECURE_HOME_PAGE_OBJECT] as? SecureHomePageObject
    override fun setSecureHomePageObject(secureHomePageObject: SecureHomePageObject) {
        BACKING_MAP[SECURE_HOME_PAGE_OBJECT] = secureHomePageObject
    }

    @Suppress("UNCHECKED_CAST")
    override fun getMultiplePaymentsSelectedBeneficiaryList(): List<BeneficiaryObject> = BACKING_MAP[MULTIPLE_PAYMENT_SELECTED_BENEFICIARY_LIST] as? List<BeneficiaryObject> ?: emptyList()
    override fun setMultiplePaymentSelectedBeneficiaryList(beneficiaryList: List<BeneficiaryObject>) {
        BACKING_MAP[MULTIPLE_PAYMENT_SELECTED_BENEFICIARY_LIST] = beneficiaryList
    }

    override fun getDelinkedPrimaryDevice(): Device? = BACKING_MAP[DELINKED_PRIMARY_DEVICE] as? Device
    override fun setDelinkedPrimaryDevice(device: Device?) {
        if (device != null) {
            BACKING_MAP[DELINKED_PRIMARY_DEVICE] = device
        } else {
            BACKING_MAP.remove(DELINKED_PRIMARY_DEVICE)
        }
    }

    override fun getAuthCredential(): String? = BACKING_MAP[AUTH_CREDENTIAL] as? String
    override fun setAuthCredential(authCredential: String) {
        BACKING_MAP[AUTH_CREDENTIAL] = authCredential
    }

    override fun getEnrollingUserAliasID(): String? = BACKING_MAP[ENROLLING_USER_ALIAS_ID] as? String
    override fun setEnrollingUserAliasID(aliasID: String) {
        BACKING_MAP[ENROLLING_USER_ALIAS_ID] = aliasID
    }

    // Not happy with the set functions below, will make changes to these going forward
    override fun getReturnToScreen(): Class<*>? = try {
        BACKING_MAP[SCREEN_TO_RETURN_TO] as? Class<*>
    } catch (e: ClassCastException) {
        null
    }

    override fun setReturnToScreen(classToReturnTo: Class<*>?) {
        if (classToReturnTo != null) {
            BACKING_MAP[SCREEN_TO_RETURN_TO] = classToReturnTo
        } else {
            BACKING_MAP.remove(SCREEN_TO_RETURN_TO)
        }
    }

    override fun getCreate2faAliasResponse(): Create2faAliasResponse? = BACKING_MAP[CREATE_2FA_ALIAS_RESPONSE] as? Create2faAliasResponse
    override fun setCreate2faAliasResponse(create2faAliasResponse: Create2faAliasResponse) {
        BACKING_MAP[CREATE_2FA_ALIAS_RESPONSE] = create2faAliasResponse
    }

    override fun getLatestResponse(): ResponseObject? = BACKING_MAP[CURRENT_RESPONSE] as? ResponseObject
    override fun setLatestResponse(responseObject: ResponseObject) {
        BACKING_MAP[CURRENT_RESPONSE] = responseObject
    }

    override fun getSecurityCodeDelegate(): SecurityCodeDelegate? = BACKING_MAP[SECURITY_CODE_DELEGATE] as? SecurityCodeDelegate
    override fun setSecurityCodeDelegate(delegate: SecurityCodeDelegate) {
        BACKING_MAP[SECURITY_CODE_DELEGATE] = delegate
    }

    override fun getSureCheckDelegate(): SureCheckDelegate? = BACKING_MAP[SURE_CHECK_DELEGATE] as? SureCheckDelegate
    override fun setSureCheckDelegate(sureCheckDelegate: SureCheckDelegate) {
        BACKING_MAP[SURE_CHECK_DELEGATE] = sureCheckDelegate
    }

    override fun hasErrorResponse(): Boolean = hasErrorResponse
    override fun setHasErrorResponse(hasErrorResponse: Boolean) {
        this.hasErrorResponse = hasErrorResponse
    }

    override fun getSureCheckReferenceNumber(): String = (BACKING_MAP[SURE_CHECK_REFERENCE_NUMBER] as? String) ?: ""
    override fun setSureCheckReferenceNumber(referenceNumber: String) {
        BACKING_MAP[SURE_CHECK_REFERENCE_NUMBER] = referenceNumber
    }

    override fun getSureCheckCellphoneNumber(): String = (BACKING_MAP[SURE_CHECK_CELLPHONE_NUMBER] as? String) ?: ""
    override fun setSureCheckCellphoneNumber(cellphoneNumber: String) {
        BACKING_MAP[SURE_CHECK_CELLPHONE_NUMBER] = cellphoneNumber
    }

    override fun getSureCheckEmail(): String = (BACKING_MAP[SURE_CHECK_EMAIL] as? String) ?: ""
    override fun setSureCheckEmail(emailAddress: String) {
        BACKING_MAP[SURE_CHECK_EMAIL] = emailAddress
    }

    override fun getSureCheckNotificationMethod(): String = (BACKING_MAP[SURE_CHECK_NOTIFICATION_METHOD] as? String) ?: ""
    override fun setSureCheckNotificationMethod(notificationMethod: String) {
        BACKING_MAP[SURE_CHECK_NOTIFICATION_METHOD] = notificationMethod
    }

    override fun getNotificationDetails(): NotificationDetailsResponse? = BACKING_MAP[NOTIFICATION_DETAILS] as? NotificationDetailsResponse
    override fun setNotificationDetails(details: NotificationDetailsResponse) {
        BACKING_MAP[NOTIFICATION_DETAILS] = details
    }

    override fun getCurrentDevice(): Device? = BACKING_MAP[CURRENT_DEVICE] as? Device
    override fun setCurrentDevice(currentDevice: Device) {
        BACKING_MAP[CURRENT_DEVICE] = currentDevice
    }

    override fun isChangePrimaryDeviceFlow(): Boolean = BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_FLOW] as? Boolean ?: false
    override fun setChangePrimaryDeviceFlow(isPrimaryDeviceFlow: Boolean) {
        BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_FLOW] = isPrimaryDeviceFlow
    }

    override fun isChangePrimaryDeviceFlowFromSureCheck(): Boolean = BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_SURECHECK] as? Boolean ?: false
    override fun setChangePrimaryDeviceFlowFromSureCheck(isPrimaryDeviceFlowFromSureCheck: Boolean) {
        BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_SURECHECK] = isPrimaryDeviceFlowFromSureCheck
    }

    override fun isChangePrimaryDeviceFlowFailOver(): Boolean = BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_FAIL_OVER] as? Boolean ?: false
    override fun setChangePrimaryDeviceFlowFailOver(isFailOverFlow: Boolean) {
        BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_FAIL_OVER] = isFailOverFlow
    }

    override fun isChangePrimaryDeviceFromNoPrimaryDeviceScreen(): Boolean = BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_NO_DEVICE] as? Boolean ?: false
    override fun setChangePrimaryDeviceFromNoPrimaryDeviceScreen(isPrimaryDeviceFromNoPrimaryDeviceScreen: Boolean) {
        BACKING_MAP[IS_CHANGE_PRIMARY_DEVICE_NO_DEVICE] = isPrimaryDeviceFromNoPrimaryDeviceScreen
    }

    override fun getRequestId(): String = BACKING_MAP[REQUEST_ID] as? String ?: ""
    override fun setRequestId(requestId: String) {
        BACKING_MAP[REQUEST_ID] = requestId
    }

    override fun getBiometricReferenceNumber(): String = BACKING_MAP[BIO_REFERENCE] as? String ?: ""
    override fun setBiometricReferenceNumber(bioReference: String) {
        BACKING_MAP[BIO_REFERENCE] = bioReference
    }

    override fun getOriginalSureCheckType(): String = BACKING_MAP[ORIGINAL_SURE_CHECK_TYPE] as? String ?: ""
    override fun setOriginalSureCheckType(transactionVerificationType: String) {
        BACKING_MAP[ORIGINAL_SURE_CHECK_TYPE] = transactionVerificationType
    }

    override fun isBioAuthenticated(): Boolean = BACKING_MAP[IS_BIO_AUTHENTICATED] as? Boolean ?: false
    override fun setIsBioAuthenticated(isBioAuthenticated: Boolean) {
        BACKING_MAP[IS_BIO_AUTHENTICATED] = isBioAuthenticated
    }

    override fun getSelectedProfileToLink(): Service = BACKING_MAP[SELECTED_PROFILE] as? Service ?: Service()
    override fun setSelectedProfileToLink(selectedProfile: Service) {
        BACKING_MAP[SELECTED_PROFILE] = selectedProfile
    }

    override fun isIdentificationAndVerificationLinkingFlow(): Boolean = BACKING_MAP[IS_IDENTIFICATION_AN_VERIFICATION_LINKING_FLOW] as? Boolean ?: false
    override fun setIsIdentificationAndVerificationLinkingFlow(isIdentificationAndVerificationLinkingFlow: Boolean) {
        BACKING_MAP[IS_IDENTIFICATION_AN_VERIFICATION_LINKING_FLOW] = isIdentificationAndVerificationLinkingFlow
    }

    override fun isIdentityAndVerificationPostLogin(): Boolean = BACKING_MAP[IS_IDENTIFICATION_AN_VERIFICATION_POST_LOGIN] as? Boolean ?: false
    override fun setIsIdentityAndVerificationPostLogin(isPostLogin: Boolean) {
        BACKING_MAP[IS_IDENTIFICATION_AN_VERIFICATION_POST_LOGIN] = isPostLogin
    }

    override fun isForgotPrimaryDeviceButtonClicked(): Boolean = BACKING_MAP[IS_FORGOT_PRIMARY_DEVICE_BUTTON_CLICKED] as? Boolean ?: false
    override fun setIsForgotPrimaryDeviceButtonClicked(isForgotPrimaryDeviceButtonClicked: Boolean) {
        BACKING_MAP[IS_FORGOT_PRIMARY_DEVICE_BUTTON_CLICKED] = isForgotPrimaryDeviceButtonClicked
    }

    override fun getLastSureCheckDelegateBeforeChangingPrimary(): SureCheckDelegate? = BACKING_MAP[LAST_SURE_CHECK_DELEGATE] as? SureCheckDelegate
    override fun setLastSureCheckDelegateBeforeChangingPrimary(sureCheckDelegate: SureCheckDelegate) {
        BACKING_MAP[LAST_SURE_CHECK_DELEGATE] = sureCheckDelegate
    }

    override fun isIdentificationAndVerificationFlow(): Boolean = BACKING_MAP[IS_IDENTIFICATION_AND_VERIFICATION_FLOW] as? Boolean ?: false
    override fun setIsIdentificationAndVerificationFlow(isIdentificationAndVerificationFlow: Boolean) {
        BACKING_MAP[IS_IDENTIFICATION_AND_VERIFICATION_FLOW] = isIdentificationAndVerificationFlow
    }

    override fun getLinkingTransactionDetails(): LinkingTransactionDetails = BACKING_MAP[LINKING_TRANSACTION_DETAILS] as? LinkingTransactionDetails ?: LinkingTransactionDetails()
    override fun setLinkingTransactionDetails(linkingTransactionDetails: LinkingTransactionDetails) {
        BACKING_MAP[LINKING_TRANSACTION_DETAILS] = linkingTransactionDetails
    }

    override fun getEnterpriseSessionId(): String = BACKING_MAP[ENTERPRISE_SESSION_ID] as? String ?: ""
    override fun setEnterpriseSessionId(enterpriseSessionId: String) {
        BACKING_MAP[ENTERPRISE_SESSION_ID] = enterpriseSessionId
    }

    override fun hasPrimaryDevice(): Boolean = BACKING_MAP[LINKING_HAS_PRIMARY_DEVICE] as? Boolean ?: false
    override fun setHasPrimaryDevice(hasPrimaryDevice: Boolean) {
        BACKING_MAP[LINKING_HAS_PRIMARY_DEVICE] = hasPrimaryDevice
    }

    override fun shouldRevertToOldLinkingFlow(): Boolean = BACKING_MAP[SHOULD_REVERT_TO_OLD_FLOW] as? Boolean ?: false
    override fun setShouldRevertToOldLinkingFlow(shouldRevertToOldFlow: Boolean) {
        BACKING_MAP[SHOULD_REVERT_TO_OLD_FLOW] = shouldRevertToOldFlow
    }

    override fun getCustomerIdNumber(): String = BACKING_MAP[CUSTOMER_ID_NUMBER] as? String ?: ""
    override fun setCustomerIdNumber(idNumber: String) {
        BACKING_MAP[CUSTOMER_ID_NUMBER] = idNumber
    }

    override fun isFromManageDevicesFlow(): Boolean = BACKING_MAP[FROM_MANAGE_DEVICES_FLOW] as? Boolean ?: false
    override fun setFromManageDevicesFlow(isFromChangeManageDevicesFlow: Boolean) {
        BACKING_MAP[FROM_MANAGE_DEVICES_FLOW] = isFromChangeManageDevicesFlow
    }

    override fun isCurrentDeviceProcessingSureCheck(): Boolean = BACKING_MAP[IS_CURRENT_DEVICE_PROCESSING_SURE_CHECK] as? Boolean ?: false
    override fun setCurrentDeviceProcessingSureCheck(isCurrentDeviceProcessingSureCheck: Boolean) {
        BACKING_MAP[IS_CURRENT_DEVICE_PROCESSING_SURE_CHECK] = isCurrentDeviceProcessingSureCheck
    }

    override fun isImiProfileRegistered(): Boolean = BACKING_MAP[IS_IMI_PROFILE_REGISTERED] as? Boolean ?: false
    override fun setImiProfileRegistered(isImiSessionRegistered: Boolean) {
        BACKING_MAP[IS_IMI_PROFILE_REGISTERED] = isImiSessionRegistered
    }

    override fun isDocumentCached(key: String): Boolean = BACKING_MAP.containsKey(key)
    override fun getDownloadId(key: String): Long = BACKING_MAP[key] as? Long ?: Long.MIN_VALUE
    override fun setDownloadId(key: String, id: Long) {
        BACKING_MAP[key] = id
    }

    override fun clearAllIdentificationAndVerificationCacheValues() {
        LINKING_TRANSACTION_DETAILS.removeValueFromCache()
        IS_IDENTIFICATION_AND_VERIFICATION_FLOW.removeValueFromCache()
        LAST_SURE_CHECK_DELEGATE.removeValueFromCache()
        IS_FORGOT_PRIMARY_DEVICE_BUTTON_CLICKED.removeValueFromCache()
        IS_IDENTIFICATION_AN_VERIFICATION_POST_LOGIN.removeValueFromCache()
        IS_IDENTIFICATION_AN_VERIFICATION_LINKING_FLOW.removeValueFromCache()
        SELECTED_PROFILE.removeValueFromCache()
        IS_BIO_AUTHENTICATED.removeValueFromCache()
        ORIGINAL_SURE_CHECK_TYPE.removeValueFromCache()
        BIO_REFERENCE.removeValueFromCache()
        REQUEST_ID.removeValueFromCache()
        IS_CHANGE_PRIMARY_DEVICE_NO_DEVICE.removeValueFromCache()
        IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_SURECHECK.removeValueFromCache()
        IS_CHANGE_PRIMARY_DEVICE_FLOW.removeValueFromCache()
        CURRENT_DEVICE.removeValueFromCache()
        LINKING_HAS_PRIMARY_DEVICE.removeValueFromCache()
        FROM_MANAGE_DEVICES_FLOW.removeValueFromCache()
        IS_CURRENT_DEVICE_PROCESSING_SURE_CHECK.removeValueFromCache()
        IS_CHANGE_PRIMARY_DEVICE_FLOW_FROM_FAIL_OVER.removeValueFromCache()
    }

    override fun clear() {
        BMBLogger.d("x-class", "AppCacheService.clear()")
        BACKING_MAP = HashMap()
        BACKING_MAP[CACHE_AVAILABLE] = true
    }

    private fun String.removeValueFromCache() {
        if (BACKING_MAP[this] != null) {
            BACKING_MAP.remove(this)
        }
    }
}