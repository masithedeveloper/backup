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

import android.os.Build
import com.barclays.absa.banking.express.hello.dto.Configurations
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.BMBLogger

@Suppress("SpellCheckingInspection", "ConstantConditionIf")
class ExpressConfigurationHelper {

    internal fun processConfiguration(configurationList: List<Configurations>) {
        FeatureSwitchingCache.featureSwitchingToggles = FeatureSwitching().apply {
            configurationList.forEach {
                when (it.category) {
                    "ANDROID" -> {
                        if (it.key == "APPVERSION") {
                            BMBApplication.getInstance().latestAppVersion = it.value
                        } else if (it.key == "MINIMUM_SUPPORTED_OS") {
                            BMBApplication.getInstance().minimumSupportedSDK = it.value.toIntOrNull() ?: Build.VERSION.SDK_INT
                        }
                    }
                    "IOS" -> {
                        // DONT CARE
                    }
                    "DEVICEPROFILING_ANDROID" -> BMBApplication.getInstance().isDeviceProfilingActive = getFeatureSwitchState(it.value) == FeatureSwitchingStates.ACTIVE.key
                    "DOWNTIMETYPE" -> {
                        if (it.key == DownTimeType.MAINTENANCE.name) {
                            DownTimeHelper.downTimeMessageMaintenance = it.value
                        } else if (it.key == DownTimeType.UNPLANNED.name) {
                            DownTimeHelper.downTimeMessageUnplanned = it.value
                        }
                    }
                    "SYSTEM" -> {
                        if (it.key == "DOWNTIME_ENABLED") {
                            DownTimeHelper.isDownTimeEnabled = it.value.toBoolean()
                        } else if (it.key == "DOWNTIMETYPE") {
                            DownTimeHelper.downTimeType = it.value
                        }
                    }
                    // Feature Toggles
                    "ABSAREWARDS" -> absaRewards = getFeatureSwitchState(it.value)
                    "ADDBENEFICIARY" -> addBeneficiary = getFeatureSwitchState(it.value)
                    "ADDFAMILYMEMBER" -> addFamilyMember = getFeatureSwitchState(it.value)
                    "APPLYFORREWARDS" -> applyForRewards = getFeatureSwitchState(it.value)
                    "APPRATING" -> appRating = getFeatureSwitchState(it.value)
                    "ARCHIVEDSTATEMENTS" -> archivedStatements = getFeatureSwitchState(it.value)
                    "BANKCONFIRMATIONLETTER" -> bankConfirmationLetter = getFeatureSwitchState(it.value)
                    "BEHAVIOURALREWARDS" -> behaviouralRewards = getFeatureSwitchState(it.value)
                    "BENEFICIARYSWITCHING" -> beneficiarySwitching = getFeatureSwitchState(it.value)
                    "BIOMETRICVERIFICATION" -> biometricVerification = getFeatureSwitchState(it.value)
                    "BRANCHLOCATOR" -> branchLocator = getFeatureSwitchState(it.value)
                    "BRANCHQRSCANNING" -> branchQRScanning = getFeatureSwitchState(it.value)
                    "BUSINESSBANKINGAUTHORIZATIONS" -> businessBankingAuthorizations = getFeatureSwitchState(it.value)
                    "BUSINESSBANKINGCASHSENDPLUS" -> businessBankingCashSendPlus = getFeatureSwitchState(it.value)
                    "BUSINESSBANKINGOVERDRAFTVCL" -> businessBankingOverdraftVCL = getFeatureSwitchState(it.value)
                    "BUSINESSBANKINGMANAGECARDS" -> businessBankingManageCards = getFeatureSwitchState(it.value)
                    "BUSINESSEVOLVEISLAMICREGISTRATION" -> businessEvolveIslamicRegistration = getFeatureSwitchState(it.value)
                    "BUSINESSEVOLVESTANDARDREGISTRATION" -> businessEvolveStandardRegistration = getFeatureSwitchState(it.value)
                    "BUYMOREUNITS" -> buyMoreUnits = getFeatureSwitchState(it.value)
                    "BUYNEWUNITTRUSTFUND" -> buyNewUnitTrustFund = getFeatureSwitchState(it.value)
                    "BUYUNITTRUSTS" -> buyUnitTrusts = getFeatureSwitchState(it.value)
                    "CARDTRAVELABROAD" -> cardTravelAbroad = getFeatureSwitchState(it.value)
                    "CARDVIEWDETAILS" -> cardViewDetails = getFeatureSwitchState(it.value)
                    "CLICKTOCALL" -> clickToCall = getFeatureSwitchState(it.value)
                    "COVID19HOMESCREENBANNER" -> clickToCall = getFeatureSwitchState(it.value)
                    "CREDITCARDARCHIVEDSTATEMENTS" -> creditCardArchivedStatements = getFeatureSwitchState(it.value)
                    "CREDITCARDHOTLEADS" -> creditCardHotLeads = getFeatureSwitchState(it.value)
                    "CREDITCARDLIFEINSURANCE" -> creditCardLifeInsurance = getFeatureSwitchState(it.value)
                    "CREDITCARDPINRETRIEVAL" -> creditCardPINRetrieval = getFeatureSwitchState(it.value)
                    "CREDITCARDTEMPORARYLOCK" -> creditCardTemporaryLock = getFeatureSwitchState(it.value)
                    "CREDITCARDVCL" -> creditCardVCL = getFeatureSwitchState(it.value)
                    "CURRENCYINVESTMENTHUB" -> currencyInvestmentHub = getFeatureSwitchState(it.value)
                    "CUSTOMISELOGIN" -> customiseLogin = getFeatureSwitchState(it.value)
                    "DEBICHECK" -> debiCheck = getFeatureSwitchState(it.value)
                    "DEBITORDERHUB" -> debitOrderHub = getFeatureSwitchState(it.value)
                    "DEBITORDERHUBWITHMINIMUMVALUE" -> debitOrderHubWithMinimumValue = getFeatureSwitchState(it.value)
                    "DEPOSITORPLUS" -> depositorPlus = getFeatureSwitchState(it.value)
                    "DISPLAYHELPNAVIGATION" -> displayHelpNavigation = getFeatureSwitchState(it.value)
                    "EARLYREDEMPTION" -> earlyRedemption = getFeatureSwitchState(it.value)
                    "EDITBENEFICIARYIMAGE" -> editBeneficiaryImage = getFeatureSwitchState(it.value)
                    "EDITPREPAIDELECTRICITYBENEFICIARY" -> editPrepaidElectricityBeneficiary = getFeatureSwitchState(it.value)
                    "FIXEDDEPOSITAPPLICATION" -> fixedDepositApplication = getFeatureSwitchState(it.value)
                    "FIXEDDEPOSITAPPLICATIONSOLEPROPRIETOR" -> fixedDepositApplicationSoleProprietor = getFeatureSwitchState(it.value)
                    "FIXEDDEPOSITHUB" -> fixedDepositHub = getFeatureSwitchState(it.value)
                    "FUTUREPLAN" -> futurePlan = getFeatureSwitchState(it.value)
                    "FREECOVERINSURANCE" -> freeCoverInsurance = getFeatureSwitchState(it.value)
                    "FUTUREDATEDTRANSFERS" -> futureDatedTransfers = getFeatureSwitchState(it.value)
                    "HOMELOANARCHIVEDSTATEMENTS" -> homeLoanArchivedStatements = getFeatureSwitchState(it.value)
                    "HOMELOANHUBFLEXIRESERVEAMOUNTHEADER" -> homeLoanHubFlexiReserveAmountHeader = getFeatureSwitchState(it.value)
                    "HOMELOANHUBINFORMATIONTAB" -> homeLoanHubInformationTab = getFeatureSwitchState(it.value)
                    "IMICONNECT" -> imiConnect = getFeatureSwitchState(it.value)
                    "INAPPMAILBOX" -> inAppMailbox = getFeatureSwitchState(it.value)
                    "INTERNATIONALPAYMENTS" -> internationalPayments = getFeatureSwitchState(it.value)
                    "LOTTOANDPOWERBALL" -> lottoAndPowerball = getFeatureSwitchState(it.value)
                    "MANAGEACCOUNTS" -> manageAccounts = getFeatureSwitchState(it.value)
                    "MANAGEDPROFILE" -> manageProfile = getFeatureSwitchState(it.value)
                    "MANAGEINSURANCEPOLICY" -> manageInsurancePolicy = getFeatureSwitchState(it.value)
                    "MULTIPLEPAYMENTS" -> multiplePayments = getFeatureSwitchState(it.value)
                    "NOTICEDEPOSITHUB" -> noticeDepositHub = getFeatureSwitchState(it.value)
                    "NTBREGISTRATION" -> ntbRegistration = getFeatureSwitchState(it.value)
                    "NEWTOBANKSELFIEPRIVACYINDICATOR" -> newToBankSelfiePrivacyIndicator = getFeatureSwitchState(it.value)
                    "OLDFUNERALCOVERAPPLY" -> oldFuneralCoverApply = getFeatureSwitchState(it.value)
                    "OVERDRAFTVCL" -> overdraftVCL = getFeatureSwitchState(it.value)
                    "PAYMENTSREWRITE" -> paymentsRewrite = getFeatureSwitchState(it.value)
                    "PERSONALLOAN" -> personalLoan = getFeatureSwitchState(it.value)
                    "PERSONALLOANARCHIVEDSTATEMENTS" -> personalLoanArchivedStatements = getFeatureSwitchState(it.value)
                    "PERSONALLOANHUB" -> personalLoanHub = getFeatureSwitchState(it.value)
                    "PREPAIDAIRTIME" -> prepaidAirtime = getFeatureSwitchState(it.value)
                    "PREPAIDELECTRICITY" -> prepaidElectricity = getFeatureSwitchState(it.value)
                    "PROJECTORBIT" -> projectOrbit = getFeatureSwitchState(it.value)
                    "REDEEMREWARDSVOUCHER" -> redeemRewardsVoucher = getFeatureSwitchState(it.value)
                    "SCANTOPAY" -> scanToPay = getFeatureSwitchState(it.value)
                    "SCREENSHOTS" -> screenshots = getFeatureSwitchState(it.value)
                    "SECONDARYCARDMANAGEMENT" -> secondaryCardManagement = getFeatureSwitchState(it.value)
                    "SECONDARYCARDGRACEPERIOD" -> secondaryCardGracePeriod = getFeatureSwitchState(it.value)
                    "SECURITYCODEAVAILABLEONATM" -> securityCodeAvailableOnATM = getFeatureSwitchState(it.value)
                    "SOLEPROPRIETORREGISTRATION" -> soleProprietorRegistration = getFeatureSwitchState(it.value)
                    "STAMPEDSTATEMENTS" -> stampedStatements = getFeatureSwitchState(it.value)
                    "STANDALONECUSTOMERSENABLED" -> standaloneCustomersEnabled = getFeatureSwitchState(it.value)
                    "STOPANDREPLACECREDITCARD" -> stopAndReplaceCreditCard = getFeatureSwitchState(it.value)
                    "STOPANDREPLACEDEBITCARD" -> stopAndReplaceDebitCard = getFeatureSwitchState(it.value)
                    "STUDENTACCOUNTREGISTRATION" -> studentAccountRegistration = getFeatureSwitchState(it.value)
                    "SWIFTINTERNATIONALPAYMENTS" -> swiftInternationalPayments = getFeatureSwitchState(it.value)
                    "ULTIMATEPROTECTOR" -> ultimateProtector = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEARCHIVEDSTATEMENTS" -> vehicleFinanceArchivedStatements = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEHUB" -> vehicleFinanceHub = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCETRANSFER" -> vehicleFinanceTransfer = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEDOCUMENTREQUEST" -> vehicleFinanceDocumentRequest = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEHUBACCOUNTINFORMATION" -> vehicleFinanceAccountInformation = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCESETTLEMENTQUOTE" -> vehicleFinanceSettlementQuote = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCETAXCERTIFICATE" -> vehicleFinanceTaxCertificate = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEPAIDUPLETTER" -> vehicleFinancePaidUpLetter = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEDETAILEDSTATEMENT" -> vehicleFinanceDetailedStatement = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEELECTRONICNATIS" -> vehicleFinanceElectronicNatis = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCECROSSBORDERLETTER" -> vehicleFinanceCrossBorderLetter = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEFUTUREDATEDTRANSFER" -> vehicleFinanceFutureDatedTransfer = getFeatureSwitchState(it.value)
                    "VEHICLEFINANCEAMORTIZATIONSCHEDULE" -> vehicleFinanceLoanAmortization = getFeatureSwitchState(it.value)
                    "VIEWTAXCERTIFICATE" -> viewTaxCertificate = getFeatureSwitchState(it.value)
                    "VIEWUNITTRUSTS" -> viewUnitTrusts = getFeatureSwitchState(it.value)
                    "WIMIBUYMOREUNITTRUSTS" -> wimiBuyMoreUnitTrusts = getFeatureSwitchState(it.value)
                    "WIMIFLEXIFUNERALCOVER" -> wimiFlexiFuneralCover = getFeatureSwitchState(it.value)
                    "WIMILAWFORYOU" -> wimiLawForYou = getFeatureSwitchState(it.value)
                    "WIMIMANAGEBENEFICIARIES" -> wimiManageBeneficiaries = getFeatureSwitchState(it.value)
                    "WIMIMANAGEEXERGY" -> wimiManageExergy = getFeatureSwitchState(it.value)
                    "WIMIREDEEMUNITTRUSTS" -> wimiRedeemUnitTrusts = getFeatureSwitchState(it.value)
                    "WIMISWITCHUNITTRUSTS" -> wimiSwitchUnitTrusts = getFeatureSwitchState(it.value)
                    "WIMIVIEWWILLS" -> wimiViewWills = getFeatureSwitchState(it.value)
                    "MONITORDEVICEPROFILING" -> monitorDeviceProfiling = getFeatureSwitchState(it.value)
                    else -> BMBLogger.e("NO MATCHING CATEGORY: " + it.category)
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun getFeatureSwitchState(state: String): Int {
        return when (state) {
            "ACTIVE" -> FeatureSwitchingStates.ACTIVE.key
            "HIDDEN" -> FeatureSwitchingStates.GONE.key
            "DISABLED" -> FeatureSwitchingStates.DISABLED.key
            else -> FeatureSwitchingStates.ACTIVE.key
        }
    }
}