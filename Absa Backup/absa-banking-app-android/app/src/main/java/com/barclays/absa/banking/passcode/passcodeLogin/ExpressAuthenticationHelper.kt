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

package com.barclays.absa.banking.passcode.passcodeLogin

import android.content.Intent
import android.util.Base64
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.*
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.updateCustomerProfileObject
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.monitoring.MonitoringService
import com.barclays.absa.banking.express.acknowledgeDevice.AcknowledgeViewModel
import com.barclays.absa.banking.express.authentication.login.AuthenticationStatusCodes
import com.barclays.absa.banking.express.authentication.login.AuthenticationViewModel
import com.barclays.absa.banking.express.deviceRegister.DeviceRegistrationViewModel
import com.barclays.absa.banking.express.getAllBalances.CacheHeader
import com.barclays.absa.banking.express.getAllBalances.GetAllBalancesViewModel
import com.barclays.absa.banking.express.getAllBalances.dto.Account
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesExpress
import com.barclays.absa.banking.express.hello.HelloViewModel
import com.barclays.absa.banking.express.hello.dto.AppVersion
import com.barclays.absa.banking.express.hello.dto.AppVersionStatusEnum
import com.barclays.absa.banking.express.userProfile.UserProfileViewModel
import com.barclays.absa.banking.express.verify.VerifyViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.launch.SplashActivity
import com.barclays.absa.banking.presentation.shared.AppUpdateUtil
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.crypto.SymmetricCryptoHelper.KeyStoreEntryAccessException
import com.barclays.absa.utils.*
import com.newrelic.agent.android.NewRelic
import styleguide.utils.extensions.toTenDigitPhoneNumber
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.RetrofitClientFactory
import za.co.absa.networking.dto.ResponseHeader
import za.co.absa.networking.error.ApplicationErrorType
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.utils.HmacUtils.aesSymmetricKey
import za.co.absa.networking.hmac.utils.HmacUtils.decrypt
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.system.exitProcess

class ExpressAuthenticationHelper(val activity: BaseActivity) {

    private var isFromSplash = false
    private var isBiometricAuth = false

    private var retryCount: Int = 0
    private var oldHmacSecret: String = ""
    private var symmetricCryptoHelper = SymmetricCryptoHelper.getInstance()
    private lateinit var appVersionResponse: AppVersion
    private lateinit var credential: String
    private lateinit var functionToExecuteAfterHello: () -> Unit
    private lateinit var loginCallBack: LoginCallBack
    private var allowNextStep = true
    private val appCacheService: IAppCacheService = getServiceInterface()
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    companion object {
        const val PUBLIC_KEY = "publicKey"
        const val PUBLIC_KEY_ID = "publicKeyId"
        var allowMaintenanceTesting = false
    }

    fun performLogin(credential: String, callBack: LoginCallBack, isBiometricAuth: Boolean = false) {
        this.credential = credential
        this.loginCallBack = callBack
        this.isBiometricAuth = isBiometricAuth

        clearSessionData()
        performHello(::expressAuthenticate)
    }

    private fun clearSessionData() {
        RetrofitClientFactory.clearCookieJar()
        ExpressNetworkingConfig.tokenExpireTime = null
        ExpressNetworkingConfig.sessionMap = mutableMapOf()
    }

    private fun retryLastTransaction() {
        if (::credential.isInitialized && credential.isNotEmpty()) {
            performLogin(credential, loginCallBack, isBiometricAuth)
        } else {
            performHello(functionToExecuteAfterHello)
        }
    }

    private fun correctForTimeFailure(serviceName: String): Boolean {
        if (abs(DateUtils.getDateDiff(getLocalTimeWithCorrection(), ExpressNetworkingConfig.serverDateTime, TimeUnit.SECONDS)) > 30 && retryCount < 1) {
            MonitoringInteractor().logExpressHttpErrorEvent(activity.javaClass.simpleName, serviceName, "Incorrect time. (applying time correction)")
            retryCount++
            ExpressNetworkingConfig.timeCorrection = ExpressNetworkingConfig.serverDateTime.time - DateUtils.getLocalTime().time
            if (isDeviceRegistered()) {
                retryLastTransaction()
            } else {
                performHello(functionToExecuteAfterHello)
            }
            return true
        }
        return false
    }

    private fun httpErrorLiveDataSetup() {
        ApiService.httpErrorLiveData.removeObservers(activity)
        ApiService.httpErrorLiveData = MutableLiveData()
        ApiService.httpErrorLiveData.observe(activity, {
            if (it.actualMessage.equals(HMAC_401_UNAUTHORIZED, true)) {
                if (!correctForTimeFailure(it.serviceName)) {
                    showReRegisterScreen(false)
                }
            } else {
                if (it.actualMessage.isNotEmpty()) {
                    MonitoringInteractor().logExpressHttpErrorEvent(activity.javaClass.simpleName, it.serviceName, it.actualMessage)
                }

                activity.dismissProgressDialog()
                when {
                    !isDeviceRegistered() -> activity.showMessageError(it)
                    isFromSplash -> proceedToStepAfterHello()
                    it.errorType == ApplicationErrorType.CERTIFICATE_PINNING -> {
                        allowNextStep = false
                        activity.showMessageError(it)
                    }
                    else -> activity.showMessageError(it)
                }
            }
            httpErrorLiveDataSetup()
        })
    }

    private fun getLocalTimeWithCorrection(): Date {
        Calendar.getInstance().apply {
            time = DateUtils.getLocalTime()
            add(Calendar.MILLISECOND, ExpressNetworkingConfig.timeCorrection.toInt())
            return time
        }
    }

    private fun showReRegisterScreen(profileLost: Boolean) {
        if (ProfileManager.getInstance().profileCount == 0 && retryCount < 2) {
            retryCount++
            clearRegisteredDevice()
            performHello(functionToExecuteAfterHello)
            return
        }

        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            activity.finishAffinity()
            activity.startActivity(Intent(activity, SplashActivity::class.java))
        }

        GenericResultActivity.topOnClickListener = View.OnClickListener {
            activity.finish()
            exitProcess(0)
        }

        activity.dismissProgressDialog()
        Intent(activity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.something_went_wrong)
            if (profileLost) {
                putExtra(GenericResultActivity.SUB_MESSAGE_STRING, activity.getString(R.string.profile_lost))
            } else {
                putExtra(GenericResultActivity.SUB_MESSAGE_STRING, activity.getString(R.string.something_went_wrong_message))
            }
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.close)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.retry)
            activity.startActivity(this)
        }
    }

    private fun clearRegisteredDevice() {
        symmetricCryptoHelper.deleteKey(SymmetricCryptoHelper.EXPRESS_SECRET_KEY)
        ProfileManager.getInstance().deleteAllProfiles()
        DeviceUtils.resetDeviceUuid()
    }

    fun performSplashHello(functionToExecuteAfterHello: () -> Unit) {
        this.isFromSplash = true
        performHello(functionToExecuteAfterHello)
    }

    private fun isDeviceRegistered() = symmetricCryptoHelper.containsKey(SymmetricCryptoHelper.EXPRESS_SECRET_KEY)

    fun performHello(functionToExecuteAfterHello: () -> Unit) {
        this.functionToExecuteAfterHello = functionToExecuteAfterHello

        if (isDeviceRegistered()) {
            ExpressNetworkingConfig.apply {
                hMacSecret = String(symmetricCryptoHelper.getKey(SymmetricCryptoHelper.EXPRESS_SECRET_KEY))
                publicKey = String(symmetricCryptoHelper.getKey(PUBLIC_KEY))
                publicKeyId = String(symmetricCryptoHelper.getKey(PUBLIC_KEY_ID))
            }

            val helloViewModel: HelloViewModel = activity.viewModel()
            helloViewModel.performHello()
            helloViewModel.fetchBMGIValAndNVal()

            httpErrorLiveDataSetup()

            helloViewModel.failureLiveData.observe(activity, {
                val failureMessage = getFailureMessage(it)

                when {
                    failureMessage == HMAC_VERIFICATION_FAILED -> correctForTimeFailure(helloViewModel.repository.javaClass.simpleName)
                    failureMessage == CLIENT_ID_NOT_FOUND -> showReRegisterScreen(true)
                    isFromSplash -> proceedToStepAfterHello()
                    else -> activity.showMessageError(failureMessage)
                }
            })

            helloViewModel.helloLiveData.observe(activity, {

                if (it.publicKey.isNotEmpty()) {
                    ExpressNetworkingConfig.publicKey = it.publicKey
                    symmetricCryptoHelper.storeData(PUBLIC_KEY, it.publicKey.toByteArray())
                }

                ExpressConfigurationHelper().processConfiguration(it.configurationList)
                BMBLogger.d("DeviceProfiling: ExpressCSID", it.customerSessionId)
                appCacheService.setCustomerSessionId(it.customerSessionId)

                appVersionResponse = it.appVersionVO

                if (it.hmacSecret.isNotEmpty()) {
                    decodeDecryptAndSaveHmacSecret(it.hmacSecret, true)
                } else {
                    checkAppUpdateStatus()
                }

                helloViewModel.helloLiveData.removeObservers(activity)
            })
        } else {
            registerDevice()
        }
    }

    private fun checkAppUpdateStatus() {
        when (appVersionResponse.upgradeStatus) {
            AppVersionStatusEnum.NONE -> {
                if (AppUpdateUtil.shouldShowNagScreenUpdate()) {
                    SharedPreferenceService.setLastNagDate(Date().time)

                    if (!AppUpdateUtil.isUsingSupportedSDKVersion()) {
                        usingUnsupportedSDKVersion()
                    } else {
                        val updateMessage = if (appVersionResponse.message.isNotEmpty()) appVersionResponse.message else activity.getString(R.string.nag_screen_update_message)

                        AppUpdateUtil.launchOptionalUpdateScreen(activity, updateMessage, object : AppUpdateUtil.DismissCallback {
                            override fun onDismiss() {
                                proceedToStepAfterHello()
                            }
                        })
                    }
                } else {
                    proceedToStepAfterHello()
                }
            }
            AppVersionStatusEnum.OPTIONAL -> {
                if (!AppUpdateUtil.isUsingSupportedSDKVersion()) {
                    usingUnsupportedSDKVersion()
                } else {
                    AppUpdateUtil.launchOptionalUpdateScreen(activity, appVersionResponse.message, object : AppUpdateUtil.DismissCallback {
                        override fun onDismiss() {
                            proceedToStepAfterHello()
                        }
                    })
                }
            }
            AppVersionStatusEnum.FORCE_UPGRADE -> AppUpdateUtil.showForceUpdateDialog(activity, appVersionResponse.message)
        }
    }

    private fun usingUnsupportedSDKVersion() {
        if (SharedPreferenceService.getUnsupportedSDKCount() < 5) {
            SharedPreferenceService.incrementUnsupportedSDKCount()

            activity.showMessageError(activity.getString(R.string.sdk_not_supported_message)) { dialog, _ ->
                dialog.dismiss()
                proceedToStepAfterHello()
            }
        } else {
            proceedToStepAfterHello()
        }
    }

    private fun proceedToStepAfterHello() {
        when {
            allowMaintenanceTesting -> functionToExecuteAfterHello()
            DownTimeHelper.isDownTimeEnabled -> DownTimeHelper.showMaintenancePage(activity)
            allowNextStep -> functionToExecuteAfterHello()
            else -> showReRegisterScreen(false)
        }
    }

    private fun registerDevice() {
        val deviceRegistrationViewModel: DeviceRegistrationViewModel = activity.viewModel()

        ExpressNetworkingConfig.hMacSecret = ""
        ExpressNetworkingConfig.publicKeyId = ExpressNetworkingConfig.REG_PUBLIC_KEY_ID
        ExpressNetworkingConfig.publicKey = ExpressNetworkingConfig.REG_PUBLIC_KEY

        deviceRegistrationViewModel.registerDevice()

        httpErrorLiveDataSetup()

        deviceRegistrationViewModel.failureLiveData.observe(activity, {
            if (!correctForTimeFailure(deviceRegistrationViewModel.repository.javaClass.simpleName)) {
                showReRegisterScreen(false)
            }
        })

        deviceRegistrationViewModel.deviceRegistrationLiveData.observe(activity, {
            decodeDecryptAndSaveHmacSecret(it.hmacSecret, false)
            ExpressNetworkingConfig.publicKey = it.publicKey
            ExpressNetworkingConfig.publicKeyId = it.publicKeyId
            symmetricCryptoHelper.storeData(PUBLIC_KEY, it.publicKey.toByteArray())
            symmetricCryptoHelper.storeData(PUBLIC_KEY_ID, it.publicKeyId.toByteArray())
            performHello(functionToExecuteAfterHello)
        })
    }

    fun getAllBalances(cacheHeader: CacheHeader, balanceCallBack: BalanceCallBack) {
        val viewModel: GetAllBalancesViewModel = activity.viewModel()

        viewModel.fetchAllBalances(cacheHeader)

        viewModel.failureLiveData.observe(activity, {
            balanceCallBack.callFailed(getFailureMessage(it))
        })

        viewModel.balancesLiveData.observe(activity, { balanceResponse ->

            absaCacheService.setPersonalClientAgreementAccepted(balanceResponse.clientAgreementAccepted)

            val expressAccountList = handleBalanceAccountList(balanceResponse.accountList)
            AbsaCacheManager.getInstance().appendAccountList(expressAccountList)
            updateSecureHomeCache()

            balanceCallBack.callComplete()
        })
    }

    fun updateAccount(account: Account) {
        AbsaCacheManager.getInstance().apply {
            val index = accountsList.accountsList.indexOfFirst { it.accountNumber == account.number }
            val expressAccountList = handleBalanceAccountList(mutableListOf<Account>().apply { add(account) }, false)

            if (index != -1) {
                accountsList.accountsList.removeAt(index)
                appendAccountList(expressAccountList, index)
            } else {
                appendToEndAccountList(expressAccountList)
            }
            updateSecureHomeCache()
        }
    }

    fun updateAccountBalance(account: Account) {
        AbsaCacheManager.getInstance().apply {
            val index = accountsList.accountsList.indexOfFirst { it.accountNumber == account.number }
            if (index != -1) {
                val accountObject = accountsList.accountsList.elementAt(index).apply {
                    currentBalance = Amount(account.balance)
                    availableBalance = Amount(account.available)
                    unclearedBalance = Amount(account.uncleared)
                }

                accountsList.accountsList.removeAt(index)
                appendAccountList(arrayListOf(accountObject), index)
            } else {
                appendToEndAccountList(handleBalanceAccountList(mutableListOf<Account>().apply { add(account) }, false))
            }

            updateSecureHomeCache()
        }
    }

    private fun updateSecureHomeCache() {
        AbsaCacheManager.getInstance().apply {
            accountsList.accountsList.let { accountList ->
                appCacheService.getSecureHomePageObject()?.apply {
                    fromAccounts = filterFromAccountList(accountList, "")
                    toAccounts = filterToAccountList(accountList)
                    accounts = accountList
                }
            }
        }
    }

    private fun expressAuthenticate() {
        val viewModel: AuthenticationViewModel = activity.viewModel()

        if (!symmetricCryptoHelper.hasAliasRegistered()) {
            val activeUserProfile = ProfileManager.getInstance().activeUserProfile
            val zeroKeyEncryptedAliasId = symmetricCryptoHelper.retrieveAlias(activeUserProfile?.userId ?: "")

            if (zeroKeyEncryptedAliasId == null) {
                MonitoringInteractor().logTechnicalEvent(activity.javaClass.simpleName, "", "AliasID Null")
                showReRegisterScreen(true)
                return
            }
        }

        viewModel.authenticate(credential, isBiometricAuth)

        viewModel.failureLiveData.observe(activity, {
            val authenticationStatusCode = AuthenticationStatusCodes.values().find { statusCodeEnum -> statusCodeEnum.statusCode == it.statuscode }
            loginCallBack.loginCallFailure(authenticationStatusCode, getFailureMessage(it))
        })

        viewModel.authenticateLiveData.observe(activity, {
            appCacheService.setCustomerSessionId(it.customerSessionId)

            ExpressNetworkingConfig.accessToken = it.accessToken
            ExpressNetworkingConfig.setTokenExpiryTime(it.expiresInSeconds)
            viewModel.authenticateLiveData.removeObservers(activity)
            loginCallBack.loginCallComplete()
        })
    }

    private fun getFailureMessage(it: ResponseHeader): String {
        return if (it.resultMessages.isNotEmpty()) it.resultMessages.first().responseMessage else ""
    }

    fun getUserProfile(userProfileCallBack: UserProfileCallBack) {
        val viewModel: UserProfileViewModel = activity.viewModel()
        viewModel.fetchUserProfile()

        viewModel.failureLiveData.observe(activity, {
            userProfileCallBack.userProfileCallFailed(getFailureMessage(it))
        })

        viewModel.userProfileLiveData.observe(activity, {
            val languageCode: String = if ("af" == it.language) "A" else "E"

            val accessPrivileges = AccessPrivileges().apply {
                isOperator = "O" == it.userType
                interAccountTransferAllowed = it.userAuthorisations.interAccountTransferAllowed
                cashSendAllowed = it.userAuthorisations.cashSendBeneficiaryAllowed
                beneficiaryPaymentAllowed = it.userAuthorisations.paymentBeneficiaryAllowed
                isPrepaidAllowed = it.userAuthorisations.prepaidMobileBeneficiaryAllowed
                isPrepaidElectricityAllowed = it.userAuthorisations.prepaidElectricityBeneficiaryAllowed
            }

            val customerProfile = CustomerProfileObject().apply {
                idNumberRequired = it.idNumberRequired
                cellNumber = it.cellNumber.toTenDigitPhoneNumber()
                clientType = it.clientType
                clientTypeGroup = it.clientTypeGroup
                customerSessionId = it.customerSessionId
                val fullName = it.firstNames + " " + it.surname
                customerName = fullName.trim().ifEmpty { it.surephrase }
                secondFactorState = it.secondFactorState
                surePhrase = it.surephrase
                userId = it.userId
                newMailboxProfileId = it.newMailBoxProfileId
                mailboxProfileId = it.mailBoxProfileId
                numberOfAuthorisations = it.numberOfAuthorisationsRequired.toString()
                lastLoginTime = it.lastLoggedIn
                limitsNotSet = (!it.limitsSet).toString()
                isTransactionalUser = !it.standaloneCustomer
                accessAccount = it.header.accessAccount
                this.languageCode = languageCode
                secondaryCardAccessBits = it.secondaryCardAccessBits

                title = it.title
                initials = it.initials
                sbuSegment = it.sbuSegment
                sbuSubSegment = it.sbuSubSegment

                idType = it.idType
                idNumber = it.idNumber
                biometricStatus = it.biometricStatus

                if (accessPrivileges.isOperator) {
                    @Suppress("ConstantConditionIf")
                    clientTypeGroup = if (it.serviceType == "B") "N" else it.serviceType
                }

                with(ProfileManager.getInstance()) {
                    if (activeUserProfile.clientType.isNullOrEmpty()) {
                        activeUserProfile.clientType = clientTypeGroup
                        updateProfile(activeUserProfile)
                    }
                }
            }

            rewardsCacheService.setExpressRewardsDetails(it.rewardsDetails)

            val expressAccountList = handleBalanceAccountList(it.accountList)

            val secureHomePageObject = SecureHomePageObject().apply {
                @Suppress("DEPRECATION")
                this.customerProfile = customerProfile
                this.accessPrivileges = accessPrivileges
                isPrimarySecondFactorDevice = it.primarySecondFactorDevice
                langCode = languageCode

                AbsaCacheManager.getInstance().apply {
                    accountsList.accountsList.let { accountList ->
                        fromAccounts = filterFromAccountList(accountList, "")
                        toAccounts = filterToAccountList(accountList)
                        accounts = accountList
                    }
                }
            }

            absaCacheService.setPersonalClientAgreementAccepted(it.clientAgreementAccepted)
            AccessPrivileges.updateInstance(accessPrivileges)
            updateCustomerProfileObject(customerProfile)
            appCacheService.setSecureHomePageObject(secureHomePageObject)
            NewRelic.setAttribute(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ACCESS_ACCOUNT, SymmetricCryptoHelper.getInstance().encryptString(CustomerProfileObject.instance.accessAccount, SymmetricCryptoHelper.NEW_RELIC_SECURITY_KEY))
            userProfileCallBack.userProfileCallComplete(expressAccountList)
        })
    }

    private fun handleRewardsDetails(accountList: MutableList<AccountObject>) {
        val rewardsDetails = rewardsCacheService.getExpressRewardsDetails()
        if (rewardsDetails.rewardsMembershipNumber.isNotEmpty() && !accountList.any { it.accountNumber == rewardsDetails.rewardsMembershipNumber }) {
            rewardsDetails.rewardsAccountBalanceSet = false
            accountList.add(AccountObject().apply {
                accessTypeBit = "0"
                description = if (rewardsDetails.rewardsAccountDetailSet) rewardsDetails.accountName else ABSA_REWARDS
                accountType = AccountTypesBMG.absaReward.name
                accountNumber = rewardsDetails.rewardsMembershipNumber
            })
        } else if (FeatureSwitchingCache.featureSwitchingToggles.behaviouralRewards == FeatureSwitchingStates.ACTIVE.key && rewardsDetails.rewardsMembershipNumber.isEmpty()) {
            accountList.add(AccountObject().apply {
                accessTypeBit = "0"
                description = "Advantage"
                accountType = "advantage"
                accountNumber = ""
            })
        }
    }

    private fun handleBalanceAccountList(balanceAccountList: MutableList<Account>): ArrayList<AccountObject> {
        return handleBalanceAccountList(balanceAccountList, true)
    }

    private fun handleBalanceAccountList(balanceAccountList: MutableList<Account>, shouldHandleRewardsDetails: Boolean): ArrayList<AccountObject> {
        val accountList = ArrayList<AccountObject>()

        balanceAccountList.forEach {
            var accessBitInfo = 0
            var accessTypeBitInfo = 0
            AccountObject().apply {
                accountType = it.accountType
                currentBalance = Amount(it.balance)
                availableBalance = Amount(it.available)
                displayName = it.name
                description = it.name
                accountNumber = it.number
                unclearedBalance = Amount(it.uncleared)

                if (it.accountActionsAllowedList.contains("ACC_INF")) {
                    setViewAcctHistoryAllowed("Y")
                }

                it.accountActionsAllowedList.forEach { allowedAction ->
                    accessBitInfo += FilterAccountList.accountActionsAndAccessBitsMapping[allowedAction] ?: 0
                    accessTypeBitInfo += FilterAccountList.accountAccessBitsToAccountTypeBitsMapping[FilterAccountList.accountActionsAndAccessBitsMapping[allowedAction] ?: 0] ?: 0
                }
                accessBits = "$accessBitInfo"
                accessTypeBit = "$accessTypeBitInfo"
                isBalanceMasked = if (it.accountActionsAllowedList.contains("BAL")) "N" else "Y"

                val enumIndex = AccountTypesExpress.valueOf(it.accountType).ordinal
                val accountTypeBmg = AccountTypesBMG.values()[enumIndex]
                accountType = accountTypeBmg.toString()

                if (accountType == AccountTypesBMG.absaReward.name) {
                    rewardsCacheService.getExpressRewardsDetails().rewardsAccountBalanceSet = true
                    availableBalance = currentBalance
                    isBalanceMasked = "N"
                    rewardsCacheService.setRewardsAccount(this)
                }

                accountList.add(this)
            }
        }

        if (shouldHandleRewardsDetails) {
            handleRewardsDetails(accountList)
        }

        return accountList
    }

    private fun acknowledge() {
        val viewModel: AcknowledgeViewModel = activity.viewModel()

        viewModel.failureLiveData.observe(activity, {
            verify()
        })

        viewModel.acknowledgeLiveData().observe(activity, {
            symmetricCryptoHelper.storeExpressSecretKey(ExpressNetworkingConfig.hMacSecret.toByteArray())
            proceedToStepAfterHello()
        })
    }

    private fun verify() {
        val viewModel: VerifyViewModel = activity.viewModel()

        viewModel.failureLiveData.observe(activity, {
            ExpressNetworkingConfig.hMacSecret = oldHmacSecret
            performHello(functionToExecuteAfterHello)
        })

        viewModel.performVerify()
        viewModel.verifyLiveData.observe(activity, {
            symmetricCryptoHelper.storeExpressSecretKey(ExpressNetworkingConfig.hMacSecret.toByteArray())
            proceedToStepAfterHello()
        })
    }

    private fun decodeDecryptAndSaveHmacSecret(hmacSecret: String, shouldAcknowledge: Boolean) {
        if (hmacSecret.isNotEmpty()) {

            val decodedHMacSecret = Base64.decode(hmacSecret, Base64.URL_SAFE)

            val bytes = if (BuildConfigHelper.STUB) "stubByteArray".toByteArray() else decrypt(aesSymmetricKey, decodedHMacSecret)

            bytes?.let {
                try {
                    ExpressNetworkingConfig.hMacSecret = String(it)
                    if (shouldAcknowledge) {
                        oldHmacSecret = String(symmetricCryptoHelper.getKey(SymmetricCryptoHelper.EXPRESS_SECRET_KEY))
                        acknowledge()
                    } else {
                        symmetricCryptoHelper.storeExpressSecretKey(ExpressNetworkingConfig.hMacSecret.toByteArray())
                    }
                } catch (e: KeyStoreEntryAccessException) {
                    showReRegisterScreen(false)
                }
            }
        }
    }

    interface BalanceCallBack {
        fun callComplete()
        fun callFailed(failureMessage: String)
    }

    interface LoginCallBack {
        fun loginCallComplete()
        fun loginCallFailure(authenticationStatusCodes: AuthenticationStatusCodes?, failureMessage: String)
    }

    interface UserProfileCallBack {
        fun userProfileCallComplete(expressAccountList: ArrayList<AccountObject>)
        fun userProfileCallFailed(failureMessage: String)
    }
}