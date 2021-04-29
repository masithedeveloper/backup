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
package com.barclays.absa.banking.framework.services.contracts

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
import com.barclays.absa.banking.funeralCover.ui.ChangePaymentDetails
import com.barclays.absa.banking.linking.services.Service
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse

interface IAppCacheService {

    fun isScanQRFlow(): Boolean
    fun setScanQRFlow(isScanQRFlows: Boolean)

    fun getAuthCredentialType(): Int
    fun setAuthCredentialType(credentialType: Int)

    fun isInNoPrimaryDeviceState(): Boolean
    fun setNoPrimaryDeviceState(isInNoPrimaryDeviceState: Boolean)

    fun isLinkingFlow(): Boolean
    fun setLinkingFlow(isLinkingFlow: Boolean)

    fun isSecondaryDevice(): Boolean
    fun setIsSecondaryDevice(isSecondaryDevice: Boolean)

    fun getUserLoggedInStatus(): Boolean
    fun setUserLoggedInStatus(isLoggedIn: Boolean)

    fun isCacheAvailable(): Boolean
    fun setCacheAvailable(available: Boolean)

    fun getPolicyDetail(): PolicyDetail?
    fun setPolicyDetail(policyDetail: PolicyDetail)

    fun getPolicyFee(): String
    fun setPolicyFee(policyFee: String)

    fun hasRewardsAccount(): Boolean
    fun setHasRewardsAccount(hasRewardsAccount: Boolean)

    fun isPasswordResetFlow(): Boolean
    fun setIsPasswordResetFlow(isPasswordReset: Boolean)

    fun getCellphoneNumber(): String?
    fun setCellphoneNumber(cellphoneNumber: String)

    fun hasCalledForScoreInThisSession(): Boolean
    fun setAlreadyCalledForScoreInThisSession(alreadyCalledForScoreInThisSession: Boolean)

    fun getCustomerSessionId(): String?
    fun setCustomerSessionId(customerSessionId: String)

    fun isTransactionalUser(): Boolean
    fun setIsTransactionalUser(isTransactionalUser: Boolean)

    fun shouldUpdateExploreHub(): Boolean
    fun setShouldUpdateExploreHub(shouldUpdate: Boolean)

    fun getExploreHubOffers(): OffersResponseObject?
    fun setExploreHubOffers(offersResponseObject: OffersResponseObject)

    fun isImiSessionActive(): Boolean
    fun setImiSessionActive(isImiSessionActive: Boolean)

    fun isPasscodeResetFlow(): Boolean
    fun setPasscodeResetFlow(isPasscodeResetFlow: Boolean)

    fun isCreditCardFlow(): Boolean
    fun setCreditCardFlow(isCreditCardFlow: Boolean)

    fun hasNoPrimaryDeviceVerificationErrorOccurred(): Boolean
    fun setNoPrimaryDeviceVerificationErrorOccurred(noPrimaryDevicePasscodeAndAtmCredentialsVerificationFailed: Boolean)

    fun isAccessAccountLogin(): Boolean
    fun setAccessAccountLogin(isAccessAccountLogin: Boolean)

    fun isPrimarySecondFactorDevice(): Boolean
    fun setPrimarySecondFactorDevice(isPrimaryDevice: Boolean)

    fun hasDisplayedSureCheckCountdown(): Boolean
    fun setDisplayedSureCheckCountDown(hasDisplayedSureCheckCountDown: Boolean)

    fun getPasscode(): String?
    fun setPasscode(passcode: String)

    fun getTrustToken(): String?
    fun setTrustToken(token: String)

    fun getDeviceNickname(): String?
    fun setDeviceNickname(nickName: String)

    fun getCreditProtection(): CreditProtection?
    fun setCreditProtection(creditProtection: CreditProtection)

    fun getAccountDetail(): AccountDetail?
    fun setAccountDetail(accountDetail: AccountDetail)

    fun getTransactions(): AccountDetail?
    fun setTransactions(transactionInfo: AccountDetail)

    fun getCashSendPlusSendMultipleResponseDetails(): List<CashSendPlusSendMultipleDetails>
    fun setCashSendPlusSendMultipleResponseDetails(beneficiaryList: List<CashSendPlusSendMultipleDetails>)

    fun getCashSendPlusSendMultipleBeneficiariesPaymentDetails(): List<CashSendPlusSendMultiplePaymentDetails>
    fun setCashSendPlusSendMultipleBeneficiariesPaymentDetails(beneficiaryList: List<CashSendPlusSendMultiplePaymentDetails>)

    fun getFilteredCreditCardTransactions(): AccountDetail?
    fun setFilteredCreditCardTransactions(transactionInfo: AccountDetail)

    fun getCreditCardInformation(): CreditCardInformation?
    fun setCreditCardInformation(creditCardInformation: CreditCardInformation)

    fun getCurrentPrimaryDevice(): Device?
    fun setCurrentPrimaryDevice(primaryDevice: Device)

    fun getCashSendPlusRegistrationStatus(): CheckCashSendPlusRegistrationStatusResponse?
    fun setCashSendPlusRegistrationStatus(registrationStatusResponse: CheckCashSendPlusRegistrationStatusResponse)

    fun getAnalyticsScreenName(): String?
    fun setAnalyticsScreenName(screenName: String)

    fun getAnalyticsAppSection(): String?
    fun setAnalyticsAppSection(sectionName: String)

    fun getMultiplePaymentsSelectedBeneficiaryList(): List<BeneficiaryObject>
    fun setMultiplePaymentSelectedBeneficiaryList(beneficiaryList: List<BeneficiaryObject>)

    fun getDelinkedPrimaryDevice(): Device?
    fun setDelinkedPrimaryDevice(device: Device?)

    fun getAuthCredential(): String?
    fun setAuthCredential(authCredential: String)

    fun getEnrollingUserAliasID(): String?
    fun setEnrollingUserAliasID(aliasID: String)

    fun getReturnToScreen(): Class<*>?
    fun setReturnToScreen(classToReturnTo: Class<*>?)

    fun getCreate2faAliasResponse(): Create2faAliasResponse?
    fun setCreate2faAliasResponse(create2faAliasResponse: Create2faAliasResponse)

    fun getLatestResponse(): ResponseObject?
    fun setLatestResponse(responseObject: ResponseObject)

    fun getSecurityCodeDelegate(): SecurityCodeDelegate?
    fun setSecurityCodeDelegate(delegate: SecurityCodeDelegate)

    fun getChangePaymentDetails(): ChangePaymentDetails?
    fun setChangePaymentDetails(changePaymentDetails: ChangePaymentDetails)

    fun getSecureHomePageObject(): SecureHomePageObject?
    fun setSecureHomePageObject(secureHomePageObject: SecureHomePageObject)

    fun getSureCheckDelegate(): SureCheckDelegate?
    fun setSureCheckDelegate(sureCheckDelegate: SureCheckDelegate)

    fun hasErrorResponse(): Boolean
    fun setHasErrorResponse(hasErrorResponse: Boolean)

    fun getSureCheckReferenceNumber(): String
    fun setSureCheckReferenceNumber(referenceNumber: String)

    fun getSureCheckCellphoneNumber(): String
    fun setSureCheckCellphoneNumber(cellphoneNumber: String)

    fun getSureCheckEmail(): String
    fun setSureCheckEmail(emailAddress: String)

    fun getSureCheckNotificationMethod(): String
    fun setSureCheckNotificationMethod(notificationMethod: String)

    fun getNotificationDetails(): NotificationDetailsResponse?
    fun setNotificationDetails(details: NotificationDetailsResponse)

    fun getCurrentDevice(): Device?
    fun setCurrentDevice(currentDevice: Device)

    fun isChangePrimaryDeviceFlow(): Boolean
    fun setChangePrimaryDeviceFlow(isPrimaryDeviceFlow: Boolean)

    fun isChangePrimaryDeviceFlowFromSureCheck(): Boolean
    fun setChangePrimaryDeviceFlowFromSureCheck(isPrimaryDeviceFlowFromSureCheck: Boolean)

    fun isChangePrimaryDeviceFlowFailOver(): Boolean
    fun setChangePrimaryDeviceFlowFailOver(isFailOverFlow: Boolean)

    fun isChangePrimaryDeviceFromNoPrimaryDeviceScreen(): Boolean
    fun setChangePrimaryDeviceFromNoPrimaryDeviceScreen(isPrimaryDeviceFromNoPrimaryDeviceScreen: Boolean)

    fun getRequestId(): String
    fun setRequestId(requestId: String)

    fun getBiometricReferenceNumber(): String
    fun setBiometricReferenceNumber(bioReference: String)

    fun getOriginalSureCheckType(): String
    fun setOriginalSureCheckType(transactionVerificationType: String)

    fun isBioAuthenticated(): Boolean
    fun setIsBioAuthenticated(isBioAuthenticated: Boolean)

    fun getSelectedProfileToLink(): Service
    fun setSelectedProfileToLink(selectedProfile: Service)

    fun isIdentificationAndVerificationLinkingFlow(): Boolean
    fun setIsIdentificationAndVerificationLinkingFlow(isIdentificationAndVerificationLinkingFlow: Boolean)

    fun isIdentityAndVerificationPostLogin(): Boolean
    fun setIsIdentityAndVerificationPostLogin(isPostLogin: Boolean)

    fun isForgotPrimaryDeviceButtonClicked(): Boolean
    fun setIsForgotPrimaryDeviceButtonClicked(isForgotPrimaryDeviceButtonClicked: Boolean)

    fun getLastSureCheckDelegateBeforeChangingPrimary(): SureCheckDelegate?
    fun setLastSureCheckDelegateBeforeChangingPrimary(sureCheckDelegate: SureCheckDelegate)

    fun isIdentificationAndVerificationFlow(): Boolean
    fun setIsIdentificationAndVerificationFlow(isIdentificationAndVerificationFlow: Boolean)

    fun getLinkingTransactionDetails(): LinkingTransactionDetails
    fun setLinkingTransactionDetails(linkingTransactionDetails: LinkingTransactionDetails)

    fun getEnterpriseSessionId(): String
    fun setEnterpriseSessionId(enterpriseSessionId: String)

    fun hasPrimaryDevice(): Boolean
    fun setHasPrimaryDevice(hasPrimaryDevice: Boolean)

    fun shouldRevertToOldLinkingFlow(): Boolean
    fun setShouldRevertToOldLinkingFlow(shouldRevertToOldFlow: Boolean)

    fun getCustomerIdNumber(): String
    fun setCustomerIdNumber(idNumber: String)

    fun isFromManageDevicesFlow(): Boolean
    fun setFromManageDevicesFlow(isFromChangeManageDevicesFlow: Boolean)

    fun isCurrentDeviceProcessingSureCheck(): Boolean
    fun setCurrentDeviceProcessingSureCheck(isCurrentDeviceProcessingSureCheck: Boolean)

    fun isImiProfileRegistered(): Boolean
    fun setImiProfileRegistered(isImiSessionRegistered: Boolean)

    fun clearAllIdentificationAndVerificationCacheValues()

    fun isDocumentCached(key: String): Boolean
    fun getDownloadId(key: String): Long
    fun setDownloadId(key: String, id: Long)

    fun clear()
}