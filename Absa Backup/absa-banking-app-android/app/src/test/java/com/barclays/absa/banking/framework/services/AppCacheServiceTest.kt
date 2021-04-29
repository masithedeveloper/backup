/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.framework.services

import android.content.Context
import com.barclays.absa.DaggerTest
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
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.funeralCover.ui.ChangePaymentDetails
import com.barclays.absa.banking.linking.services.Service
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.banking.manage.devices.services.dto.TransactionVerificationResponse
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock

class AppCacheServiceTest : DaggerTest() {

    private lateinit var appCacheService: IAppCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        appCacheService = BMBApplication.applicationComponent.getAppCacheService()
    }

    @Test
    fun isScanQRFlow() {
        assertFalse(appCacheService.isScanQRFlow()) { defaultValueChangedMessage }
        appCacheService.setScanQRFlow(true)
        assertTrue(appCacheService.isScanQRFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun getAuthCredentialType() {
        assertTrue(appCacheService.getAuthCredentialType() == 0) { defaultValueChangedMessage }
        appCacheService.setAuthCredentialType(50)
        assertTrue(appCacheService.getAuthCredentialType() == 50) { valueUpdateFailedMessage }
    }

    @Test
    fun isInNoPrimaryDeviceState() {
        assertFalse(appCacheService.isInNoPrimaryDeviceState()) { defaultValueChangedMessage }
        appCacheService.setNoPrimaryDeviceState(true)
        assertTrue(appCacheService.isInNoPrimaryDeviceState()) { valueUpdateFailedMessage }
    }

    @Test
    fun isLinkingFlow() {
        assertFalse(appCacheService.isLinkingFlow()) { defaultValueChangedMessage }
        appCacheService.setLinkingFlow(true)
        assertTrue(appCacheService.isLinkingFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun isSecondaryDevice() {
        assertFalse(appCacheService.isSecondaryDevice()) { defaultValueChangedMessage }
        appCacheService.setIsSecondaryDevice(true)
        assertTrue(appCacheService.isSecondaryDevice()) { valueUpdateFailedMessage }
    }

    @Test
    fun getUserLoggedInStatus() {
        assertFalse(appCacheService.getUserLoggedInStatus()) { defaultValueChangedMessage }
        appCacheService.setUserLoggedInStatus(true)
        assertTrue(appCacheService.getUserLoggedInStatus()) { valueUpdateFailedMessage }
    }

    @Test
    fun isCacheAvailable() {
        assertFalse(appCacheService.isCacheAvailable()) { defaultValueChangedMessage }
        appCacheService.setCacheAvailable(true)
        assertTrue(appCacheService.isCacheAvailable()) { valueUpdateFailedMessage }
    }

    @Test
    fun getPolicyDetail() {
        val policyDetail = PolicyDetail()
        assertNull(appCacheService.getPolicyDetail()) { defaultValueChangedMessage }
        appCacheService.setPolicyDetail(policyDetail)
        assertTrue(appCacheService.getPolicyDetail() == policyDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getPolicyFee() {
        assertTrue(appCacheService.getPolicyFee().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setPolicyFee("2.54")
        assertTrue(appCacheService.getPolicyFee() == "2.54") { valueUpdateFailedMessage }
    }

    @Test
    fun hasRewardsAccount() {
        assertFalse(appCacheService.hasRewardsAccount()) { defaultValueChangedMessage }
        appCacheService.setHasRewardsAccount(true)
        assertTrue(appCacheService.hasRewardsAccount()) { valueUpdateFailedMessage }
    }

    @Test
    fun isPasswordResetFlow() {
        assertFalse(appCacheService.isPasscodeResetFlow()) { defaultValueChangedMessage }
        appCacheService.setPasscodeResetFlow(true)
        assertTrue(appCacheService.isPasscodeResetFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun getCellphoneNumber() {
        assertNull(appCacheService.getCellphoneNumber()) { defaultValueChangedMessage }
        appCacheService.setCellphoneNumber("078 123 4567")
        assert(appCacheService.getCellphoneNumber() == "078 123 4567") { valueUpdateFailedMessage }
    }

    @Test
    fun hasCalledForScoreInThisSession() {
        assertFalse(appCacheService.hasCalledForScoreInThisSession()) { defaultValueChangedMessage }
        appCacheService.setAlreadyCalledForScoreInThisSession(true)
        assertTrue(appCacheService.hasCalledForScoreInThisSession()) { valueUpdateFailedMessage }
    }

    @Test
    fun getCustomerSessionId() {
        assertNull(appCacheService.getCustomerSessionId()) { defaultValueChangedMessage }
        appCacheService.setCustomerSessionId("123")
        assertTrue(appCacheService.getCustomerSessionId() == "123") { valueUpdateFailedMessage }
    }

    @Test
    fun isTransactionalUser() {
        assertFalse(appCacheService.isTransactionalUser()) { defaultValueChangedMessage }
        appCacheService.setIsTransactionalUser(true)
        assertTrue(appCacheService.isTransactionalUser()) { valueUpdateFailedMessage }
    }

    @Test
    fun shouldUpdateExploreHub() {
        assertTrue(appCacheService.shouldUpdateExploreHub()) { defaultValueChangedMessage }
        appCacheService.setShouldUpdateExploreHub(false)
        assertFalse(appCacheService.shouldUpdateExploreHub()) { valueUpdateFailedMessage }
    }

    @Test
    fun getExploreHubOffers() {
        val exploreHubOffers = OffersResponseObject()

        assertNull(appCacheService.getExploreHubOffers()) { defaultValueChangedMessage }
        appCacheService.setExploreHubOffers(exploreHubOffers)
        assertTrue(appCacheService.getExploreHubOffers() == exploreHubOffers) { valueUpdateFailedMessage }
    }

    @Test
    fun isImiSessionActive() {
        assertFalse(appCacheService.isImiSessionActive()) { defaultValueChangedMessage }
        appCacheService.setImiSessionActive(true)
        assertTrue(appCacheService.isImiSessionActive()) { valueUpdateFailedMessage }
    }

    @Test
    fun isPasscodeResetFlow() {
        assertFalse(appCacheService.isPasscodeResetFlow()) { defaultValueChangedMessage }
        appCacheService.setPasscodeResetFlow(true)
        assertTrue(appCacheService.isPasscodeResetFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun isCreditCardFlow() {
        assertFalse(appCacheService.isCreditCardFlow()) { defaultValueChangedMessage }
        appCacheService.setCreditCardFlow(true)
        assertTrue(appCacheService.isCreditCardFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun hasNoPrimaryDeviceVerificationErrorOccurred() {
        assertFalse(appCacheService.hasNoPrimaryDeviceVerificationErrorOccurred()) { defaultValueChangedMessage }
        appCacheService.setNoPrimaryDeviceVerificationErrorOccurred(true)
        assertTrue(appCacheService.hasNoPrimaryDeviceVerificationErrorOccurred()) { valueUpdateFailedMessage }
    }

    @Test
    fun isAccessAccountLogin() {
        assertFalse(appCacheService.isAccessAccountLogin()) { defaultValueChangedMessage }
        appCacheService.setAccessAccountLogin(true)
        assertTrue(appCacheService.isAccessAccountLogin()) { valueUpdateFailedMessage }
    }

    @Test
    fun isPrimarySecondFactorDevice() {
        assertFalse(appCacheService.isPrimarySecondFactorDevice()) { defaultValueChangedMessage }
        appCacheService.setPrimarySecondFactorDevice(true)
        assertTrue(appCacheService.isPrimarySecondFactorDevice()) { valueUpdateFailedMessage }
    }

    @Test
    fun hasDisplayedSureCheckCountdown() {
        assertFalse(appCacheService.hasDisplayedSureCheckCountdown()) { defaultValueChangedMessage }
        appCacheService.setDisplayedSureCheckCountDown(true)
        assertTrue(appCacheService.hasDisplayedSureCheckCountdown()) { valueUpdateFailedMessage }
    }

    @Test
    fun getPasscode() {
        assertNull(appCacheService.getPasscode()) { defaultValueChangedMessage }
        appCacheService.setPasscode("passcode")
        assertTrue(appCacheService.getPasscode() == "passcode") { valueUpdateFailedMessage }
    }

    @Test
    fun getTrustToken() {
        assertNull(appCacheService.getTrustToken()) { defaultValueChangedMessage }
        appCacheService.setTrustToken("trust-token")
        assertTrue(appCacheService.getTrustToken() == "trust-token") { valueUpdateFailedMessage }
    }

    @Test
    fun getDeviceNickname() {
        assertNull(appCacheService.getDeviceNickname()) { defaultValueChangedMessage }
        appCacheService.setDeviceNickname("device-nickname")
        assertTrue(appCacheService.getDeviceNickname() == "device-nickname") { valueUpdateFailedMessage }
    }

    @Test
    fun getCreditProtection() {
        val creditProtection = CreditProtection()
        assertNull(appCacheService.getCreditProtection()) { defaultValueChangedMessage }
        appCacheService.setCreditProtection(creditProtection)
        assertTrue(appCacheService.getCreditProtection() == creditProtection) { valueUpdateFailedMessage }
    }

    @Test
    fun getAccountDetail() {
        val accountDetail = AccountDetail()

        assertNull(appCacheService.getAccountDetail()) { defaultValueChangedMessage }
        appCacheService.setAccountDetail(accountDetail)
        assertTrue(appCacheService.getAccountDetail() == accountDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getTransactions() {
        val accountDetail = AccountDetail()

        assertNull(appCacheService.getTransactions()) { defaultValueChangedMessage }
        appCacheService.setTransactions(accountDetail)
        assertTrue(appCacheService.getTransactions() == accountDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getCashSendPlusSendMultipleResponseDetails() {
        assertTrue(appCacheService.getCashSendPlusSendMultipleResponseDetails().isEmpty()) { defaultValueChangedMessage }

        val cashSendPlusSendMultipleDetails = CashSendPlusSendMultipleDetails()
        appCacheService.setCashSendPlusSendMultipleResponseDetails(listOf(cashSendPlusSendMultipleDetails))
        assertTrue(appCacheService.getCashSendPlusSendMultipleResponseDetails().firstOrNull() == cashSendPlusSendMultipleDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getCashSendPlusSendMultipleBeneficiariesPaymentDetails() {
        assert(appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().isEmpty()) { defaultValueChangedMessage }

        val cashSendPlusSendMultiplePaymentDetails = CashSendPlusSendMultiplePaymentDetails()
        appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(listOf(cashSendPlusSendMultiplePaymentDetails))
        assertTrue(appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().firstOrNull() == cashSendPlusSendMultiplePaymentDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getFilteredCreditCardTransactions() {
        val accountDetail = AccountDetail()

        assertNull(appCacheService.getFilteredCreditCardTransactions()) { defaultValueChangedMessage }
        appCacheService.setFilteredCreditCardTransactions(accountDetail)
        assertTrue(appCacheService.getFilteredCreditCardTransactions() == accountDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getCreditCardInformation() {
        val creditCardInformation = CreditCardInformation()

        assertNull(appCacheService.getCreditCardInformation()) { defaultValueChangedMessage }
        appCacheService.setCreditCardInformation(creditCardInformation)
        assert(appCacheService.getCreditCardInformation() == creditCardInformation) { valueUpdateFailedMessage }
    }

    @Test
    fun getCurrentPrimaryDevice() {
        val device = Device()

        assertNull(appCacheService.getCurrentPrimaryDevice()) { defaultValueChangedMessage }
        appCacheService.setCurrentPrimaryDevice(device)
        assert(appCacheService.getCurrentPrimaryDevice() == device) { valueUpdateFailedMessage }
    }

    @Test
    fun getCashSendPlusRegistrationStatus() {
        val checkCashSendPlusRegistrationStatusResponse = CheckCashSendPlusRegistrationStatusResponse()

        assertNull(appCacheService.getCashSendPlusRegistrationStatus()) { defaultValueChangedMessage }
        appCacheService.setCashSendPlusRegistrationStatus(checkCashSendPlusRegistrationStatusResponse)
        assertTrue(appCacheService.getCashSendPlusRegistrationStatus() == checkCashSendPlusRegistrationStatusResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun getAnalyticsScreenName() {
        assertNull(appCacheService.getAnalyticsScreenName()) { defaultValueChangedMessage }
        appCacheService.setAnalyticsScreenName("analytics-screen-name")
        assertTrue(appCacheService.getAnalyticsScreenName() == "analytics-screen-name") { valueUpdateFailedMessage }
    }

    @Test
    fun getAnalyticsAppSection() {
        assertNull(appCacheService.getAnalyticsAppSection()) { defaultValueChangedMessage }
        appCacheService.setAnalyticsAppSection("analytics-app-section")
        assertTrue(appCacheService.getAnalyticsAppSection() == "analytics-app-section") { valueUpdateFailedMessage }
    }

    @Test
    fun getMultiplePaymentsSelectedBeneficiaryList() {
        assert(appCacheService.getMultiplePaymentsSelectedBeneficiaryList().isEmpty()) { defaultValueChangedMessage }

        val beneficiaryObject = BeneficiaryObject()
        appCacheService.setMultiplePaymentSelectedBeneficiaryList(listOf(beneficiaryObject))
        assertTrue(appCacheService.getMultiplePaymentsSelectedBeneficiaryList().firstOrNull() == beneficiaryObject) { valueUpdateFailedMessage }
    }

    @Test
    fun getDelinkedPrimaryDevice() {
        val device = Device()
        assertNull(appCacheService.getDelinkedPrimaryDevice()) { defaultValueChangedMessage }
        appCacheService.setDelinkedPrimaryDevice(device)
        assertTrue(appCacheService.getDelinkedPrimaryDevice() == device) { valueUpdateFailedMessage }
    }

    @Test
    fun getAuthCredential() {
        assertNull(appCacheService.getAuthCredential()) { defaultValueChangedMessage }
        appCacheService.setAuthCredential("authCredential")
        assertTrue(appCacheService.getAuthCredential() == "authCredential") { valueUpdateFailedMessage }
    }

    @Test
    fun getEnrollingUserAliasID() {
        assertNull(appCacheService.getEnrollingUserAliasID()) { defaultValueChangedMessage }
        appCacheService.setEnrollingUserAliasID("enrollingUserAliasId")
        assertTrue(appCacheService.getEnrollingUserAliasID() == "enrollingUserAliasId") { valueUpdateFailedMessage }
    }

    @Test
    fun getReturnToScreen() {
        val clazz = AppCacheServiceTest::class.java
        assertNull(appCacheService.getReturnToScreen()) { defaultValueChangedMessage }
        appCacheService.setReturnToScreen(clazz)
        assertTrue(appCacheService.getReturnToScreen() == clazz) { valueUpdateFailedMessage }
    }

    @Test
    fun getCreate2faAliasResponse() {
        val create2faAliasResponse = Create2faAliasResponse()
        assertNull(appCacheService.getCreate2faAliasResponse()) { defaultValueChangedMessage }
        appCacheService.setCreate2faAliasResponse(create2faAliasResponse)
        assertTrue(appCacheService.getCreate2faAliasResponse() == create2faAliasResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun getLatestResponse() {
        val responseObject = TransactionVerificationResponse()
        assertNull(appCacheService.getLatestResponse()) { defaultValueChangedMessage }
        appCacheService.setLatestResponse(responseObject)
        assertTrue(appCacheService.getLatestResponse() == responseObject) { valueUpdateFailedMessage }
    }

    @Test
    fun getChangePaymentDetails() {
        val changePaymentDetails = ChangePaymentDetails()
        assertNull(appCacheService.getChangePaymentDetails()) { defaultValueChangedMessage }
        appCacheService.setChangePaymentDetails(changePaymentDetails)
        assertTrue(appCacheService.getChangePaymentDetails() == changePaymentDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getSecurityCodeDelegate() {
        assertNull(appCacheService.getSecurityCodeDelegate()) { defaultValueChangedMessage }
        val securityCodeDelegate = object : SecurityCodeDelegate() {
            override fun onSuccess() {}
            override fun onFailure(errorMessage: String?) {}
        }
        assertTrue(appCacheService.getSecurityCodeDelegate() == securityCodeDelegate) { valueUpdateFailedMessage }
    }

    @Test
    fun hasErrorResponse() {
        assertFalse(appCacheService.hasErrorResponse()) { defaultValueChangedMessage }
        appCacheService.setHasErrorResponse(true)
        assertTrue(appCacheService.hasErrorResponse()) { valueUpdateFailedMessage }
    }

    @Test
    fun getSureCheckReferenceNumber() {
        assertTrue(appCacheService.getSureCheckReferenceNumber().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setSureCheckReferenceNumber("referenceNumber")
        assertTrue(appCacheService.getSureCheckReferenceNumber() == "referenceNumber") { valueUpdateFailedMessage }
    }

    @Test
    fun getSureCheckCellphoneNumber() {
        assertTrue(appCacheService.getSureCheckCellphoneNumber().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setSureCheckCellphoneNumber("078 123 4567")
        assertTrue(appCacheService.getSureCheckCellphoneNumber() == "078 123 4567") { valueUpdateFailedMessage }
    }

    @Test
    fun getSureCheckEmail() {
        assertTrue(appCacheService.getSureCheckEmail().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setSureCheckEmail("bank@absa.africa")
        assertTrue(appCacheService.getSureCheckEmail() == "bank@absa.africa") { valueUpdateFailedMessage }
    }

    @Test
    fun getSureCheckNotificationMethod() {
        assertTrue(appCacheService.getSureCheckNotificationMethod().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setSureCheckNotificationMethod("method")
        assertTrue(appCacheService.getSureCheckNotificationMethod() == "method") { valueUpdateFailedMessage }
    }

    @Test
    fun getNotificationDetails() {
        val notificationDetailsResponse = NotificationDetailsResponse()
        assertNull(appCacheService.getNotificationDetails()) { defaultValueChangedMessage }
        appCacheService.setNotificationDetails(notificationDetailsResponse)
        assertTrue(appCacheService.getNotificationDetails() == notificationDetailsResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun getCurrentDevice() {
        val device = Device()
        assertNull(appCacheService.getCurrentDevice()) { defaultValueChangedMessage }
        appCacheService.setCurrentDevice(device)
        assertTrue(appCacheService.getCurrentDevice() == device) { valueUpdateFailedMessage }
    }

    @Test
    fun isChangePrimaryDeviceFlow() {
        assertFalse(appCacheService.isChangePrimaryDeviceFlow()) { defaultValueChangedMessage }
        appCacheService.setChangePrimaryDeviceFlow(true)
        assertTrue(appCacheService.isChangePrimaryDeviceFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun isChangePrimaryDeviceFlowFromSureCheck() {
        assertFalse(appCacheService.isChangePrimaryDeviceFlowFromSureCheck()) { defaultValueChangedMessage }
        appCacheService.setChangePrimaryDeviceFlowFromSureCheck(true)
        assertTrue(appCacheService.isChangePrimaryDeviceFlowFromSureCheck()) { valueUpdateFailedMessage }
    }

    @Test
    fun isChangePrimaryDeviceFromNoPrimaryDeviceScreen() {
        assertFalse(appCacheService.isChangePrimaryDeviceFromNoPrimaryDeviceScreen()) { defaultValueChangedMessage }
        appCacheService.setChangePrimaryDeviceFromNoPrimaryDeviceScreen(true)
        assertTrue(appCacheService.isChangePrimaryDeviceFromNoPrimaryDeviceScreen()) { valueUpdateFailedMessage }
    }

    @Test
    fun getRequestId() {
        assertTrue(appCacheService.getRequestId().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setRequestId("requestId")
        assertTrue(appCacheService.getRequestId() == "requestId") { valueUpdateFailedMessage }
    }

    @Test
    fun getBiometricReferenceNumber() {
        assertTrue(appCacheService.getBiometricReferenceNumber().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setBiometricReferenceNumber("biometricsReferenceNumber")
        assertTrue(appCacheService.getBiometricReferenceNumber() == "biometricsReferenceNumber") { valueUpdateFailedMessage }
    }

    @Test
    fun getOriginalSureCheckType() {
        assertTrue(appCacheService.getOriginalSureCheckType().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setOriginalSureCheckType("originalSureCheckType")
        assertTrue(appCacheService.getOriginalSureCheckType() == "originalSureCheckType") { valueUpdateFailedMessage }
    }

    @Test
    fun isBioAuthenticated() {
        assertFalse(appCacheService.isBioAuthenticated()) { defaultValueChangedMessage }
        appCacheService.setIsBioAuthenticated(true)
        assertTrue(appCacheService.isBioAuthenticated()) { valueUpdateFailedMessage }
    }

    @Test
    fun getSelectedProfileToLink() {
        val service = Service().apply { accessAccount = "123" }
        assertTrue(appCacheService.getSelectedProfileToLink().accessAccount.isEmpty()) { defaultValueChangedMessage }
        appCacheService.setSelectedProfileToLink(service)
        assertTrue(appCacheService.getSelectedProfileToLink().accessAccount == "123") { valueUpdateFailedMessage }
    }

    @Test
    fun isIdentificationAndVerificationLinkingFlow() {
        assertFalse(appCacheService.isIdentificationAndVerificationLinkingFlow()) { defaultValueChangedMessage }
        appCacheService.setIsIdentificationAndVerificationLinkingFlow(true)
        assertTrue(appCacheService.isIdentificationAndVerificationLinkingFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun isIdentityAndVerificationPostLogin() {
        assertFalse(appCacheService.isIdentityAndVerificationPostLogin()) { defaultValueChangedMessage }
        appCacheService.setIsIdentityAndVerificationPostLogin(true)
        assertTrue(appCacheService.isIdentityAndVerificationPostLogin()) { valueUpdateFailedMessage }
    }

    @Test
    fun isForgotPrimaryDeviceButtonClicked() {
        assertFalse(appCacheService.isForgotPrimaryDeviceButtonClicked()) { defaultValueChangedMessage }
        appCacheService.setIsForgotPrimaryDeviceButtonClicked(true)
        assertTrue(appCacheService.isForgotPrimaryDeviceButtonClicked()) { valueUpdateFailedMessage }
    }

    @Test
    fun getLastSureCheckDelegateBeforeChangingPrimary() {
        val sureCheckDelegate = object : SureCheckDelegate(mock(Context::class.java)) {
            override fun onSureCheckProcessed() {}
        }
        assertNull(appCacheService.getLastSureCheckDelegateBeforeChangingPrimary()) { defaultValueChangedMessage }
        appCacheService.setLastSureCheckDelegateBeforeChangingPrimary(sureCheckDelegate)
        assertTrue(appCacheService.getLastSureCheckDelegateBeforeChangingPrimary() == sureCheckDelegate) { valueUpdateFailedMessage }
    }

    @Test
    fun isIdentificationAndVerificationFlow() {
        assertFalse(appCacheService.isIdentificationAndVerificationFlow()) { defaultValueChangedMessage }
        appCacheService.setIsIdentificationAndVerificationFlow(true)
        assertTrue(appCacheService.isIdentificationAndVerificationFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun getLinkingTransactionDetails() {
        val linkingTransactionDetails = LinkingTransactionDetails().apply { accountNumber = "123" }
        assertTrue(appCacheService.getLinkingTransactionDetails().accountNumber.isEmpty()) { defaultValueChangedMessage }
        appCacheService.setLinkingTransactionDetails(linkingTransactionDetails)
        assertTrue(appCacheService.getLinkingTransactionDetails().accountNumber == "123") { valueUpdateFailedMessage }
    }

    @Test
    fun getEnterpriseSessionId() {
        assertTrue(appCacheService.getEnterpriseSessionId().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setEnterpriseSessionId("enterpriseSessionId")
        assertTrue(appCacheService.getEnterpriseSessionId() == "enterpriseSessionId") { valueUpdateFailedMessage }
    }

    @Test
    fun hasPrimaryDevice() {
        assertFalse(appCacheService.hasPrimaryDevice()) { defaultValueChangedMessage }
        appCacheService.setHasPrimaryDevice(true)
        assertTrue(appCacheService.hasPrimaryDevice()) { valueUpdateFailedMessage }
    }

    @Test
    fun shouldRevertToOldLinkingFlow() {
        assertFalse(appCacheService.shouldRevertToOldLinkingFlow()) { defaultValueChangedMessage }
        appCacheService.setShouldRevertToOldLinkingFlow(true)
        assertTrue(appCacheService.shouldRevertToOldLinkingFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun getCustomerIdNumber() {
        assertTrue(appCacheService.getCustomerIdNumber().isEmpty()) { defaultValueChangedMessage }
        appCacheService.setCustomerIdNumber("customerIdNumber")
        assertTrue(appCacheService.getCustomerIdNumber() == "customerIdNumber") { valueUpdateFailedMessage }
    }

    @Test
    fun isFromManageDevicesFlow() {
        assertFalse(appCacheService.isFromManageDevicesFlow()) { defaultValueChangedMessage }
        appCacheService.setFromManageDevicesFlow(true)
        assertTrue(appCacheService.isFromManageDevicesFlow()) { valueUpdateFailedMessage }
    }

    @Test
    fun isChangePrimaryDeviceFlowFailOver() {
        assertFalse(appCacheService.isChangePrimaryDeviceFlowFailOver()) { defaultValueChangedMessage }
        appCacheService.setChangePrimaryDeviceFlowFailOver(true)
        assertTrue(appCacheService.isChangePrimaryDeviceFlowFailOver()) { valueUpdateFailedMessage }
    }

    @Test
    fun isImiProfileRegistered() {
        assertFalse(appCacheService.isImiProfileRegistered()) { defaultValueChangedMessage }
        appCacheService.setImiProfileRegistered(true)
        assertTrue(appCacheService.isImiProfileRegistered()) { valueUpdateFailedMessage }
    }

    @Test
    fun isDocumentCached() {
        val key = "42ed7b284d6c398ca2c23e9fa68c91eb"
        assertFalse(appCacheService.isDocumentCached(key)) { defaultValueChangedMessage }
        appCacheService.setDownloadId(key, 100L)
        assertTrue(appCacheService.isDocumentCached(key)) { valueUpdateFailedMessage }
    }

    @Test
    fun getDownloadId() {
        val key = "42ed7b284d6c398ca2c23e9fa68c91eb"
        assertTrue(appCacheService.getDownloadId(key) == Long.MIN_VALUE) { defaultValueChangedMessage }
        appCacheService.setDownloadId(key, 100L)
        assertTrue(appCacheService.getDownloadId(key) == 100L) { valueUpdateFailedMessage }
    }

    @Test
    fun clearAllIdentificationAndVerificationCacheValues() {
        appCacheService.clear()
        getLinkingTransactionDetails()
        isIdentificationAndVerificationFlow()
        getLastSureCheckDelegateBeforeChangingPrimary()
        isForgotPrimaryDeviceButtonClicked()
        isIdentityAndVerificationPostLogin()
        isIdentificationAndVerificationLinkingFlow()
        getSelectedProfileToLink()
        isBioAuthenticated()
        getOriginalSureCheckType()
        getBiometricReferenceNumber()
        getRequestId()
        isChangePrimaryDeviceFromNoPrimaryDeviceScreen()
        isChangePrimaryDeviceFlowFromSureCheck()
        isChangePrimaryDeviceFlow()
        getCurrentDevice()
        hasPrimaryDevice()
        isFromManageDevicesFlow()
        isChangePrimaryDeviceFlowFailOver()
    }

    @Test
    fun getSureCheckDelegate() {
        assertNull(appCacheService.getSureCheckDelegate()) { defaultValueChangedMessage }
        val sureCheckDelegate = object : SureCheckDelegate(mock(Context::class.java)) {
            override fun onSureCheckProcessed() {}
        }
        assertTrue(appCacheService.getSureCheckDelegate() == sureCheckDelegate) { valueUpdateFailedMessage }
    }

    @Test
    fun getSecureHomePageObject() {
        val secureHomePageObject = SecureHomePageObject()
        assertNull(appCacheService.getSecureHomePageObject()) { defaultValueChangedMessage }
        appCacheService.setSecureHomePageObject(secureHomePageObject)
        assertTrue(appCacheService.getSecureHomePageObject() == secureHomePageObject) { valueUpdateFailedMessage }
    }

    @Test
    fun clear() {
        appCacheService.clear()
        assertTrue(appCacheService.isCacheAvailable())
    }
}