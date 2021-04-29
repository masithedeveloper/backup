/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.featureSwitching

class FeatureSwitching {
    var absaRewards: Int = FeatureSwitchingStates.GONE.key
    var addFamilyMember: Int = FeatureSwitchingStates.GONE.key
    var applyForRewards: Int = FeatureSwitchingStates.GONE.key
    var archivedStatements: Int = FeatureSwitchingStates.GONE.key
    var beneficiarySwitching: Int = FeatureSwitchingStates.GONE.key
    var businessBankingAuthorizations: Int = FeatureSwitchingStates.GONE.key
    var businessBankingCashSendPlus: Int = FeatureSwitchingStates.GONE.key
    var businessBankingOverdraftVCL: Int = FeatureSwitchingStates.GONE.key
    var businessEvolveIslamicRegistration: Int = FeatureSwitchingStates.GONE.key
    var businessEvolveStandardRegistration: Int = FeatureSwitchingStates.GONE.key
    var cardTravelAbroad: Int = FeatureSwitchingStates.GONE.key
    var creditCardPINRetrieval: Int = FeatureSwitchingStates.GONE.key
    var creditCardTemporaryLock: Int = FeatureSwitchingStates.GONE.key
    var creditCardVCL: Int = FeatureSwitchingStates.GONE.key
    var creditCardLifeInsurance: Int = FeatureSwitchingStates.GONE.key
    var debiCheck: Int = FeatureSwitchingStates.GONE.key
    var internationalPayments: Int = FeatureSwitchingStates.GONE.key
    var viewTaxCertificate: Int = FeatureSwitchingStates.GONE.key
    var bankConfirmationLetter: Int = FeatureSwitchingStates.GONE.key
    var manageInsurancePolicy: Int = FeatureSwitchingStates.GONE.key
    var multiplePayments: Int = FeatureSwitchingStates.GONE.key
    var ntbRegistration: Int = FeatureSwitchingStates.GONE.key
    var overdraftVCL: Int = FeatureSwitchingStates.GONE.key
    var prepaidAirtime: Int = FeatureSwitchingStates.GONE.key
    var prepaidElectricity: Int = FeatureSwitchingStates.GONE.key
    var projectOrbit: Int = FeatureSwitchingStates.GONE.key
    var redeemRewardsVoucher: Int = FeatureSwitchingStates.GONE.key
    var stampedStatements: Int = FeatureSwitchingStates.GONE.key
    var stopAndReplaceCreditCard: Int = FeatureSwitchingStates.GONE.key
    var stopAndReplaceDebitCard: Int = FeatureSwitchingStates.GONE.key
    var viewUnitTrusts: Int = FeatureSwitchingStates.GONE.key
    var buyUnitTrusts: Int = FeatureSwitchingStates.GONE.key
    var manageAccounts: Int = FeatureSwitchingStates.GONE.key
    var fixedDepositHub: Int = FeatureSwitchingStates.GONE.key
    var fixedDepositApplication: Int = FeatureSwitchingStates.GONE.key
    var fixedDepositApplicationSoleProprietor: Int = FeatureSwitchingStates.GONE.key
    var clickToCall: Int = FeatureSwitchingStates.GONE.key
    var covidBanner: Int = FeatureSwitchingStates.GONE.key
    var inAppMailbox: Int = FeatureSwitchingStates.GONE.key
    var futureDatedTransfers: Int = FeatureSwitchingStates.GONE.key
    var ultimateProtector: Int = FeatureSwitchingStates.GONE.key
    var branchQRScanning: Int = FeatureSwitchingStates.GONE.key
    var branchLocator: Int = FeatureSwitchingStates.GONE.key
    var buyMoreUnits: Int = FeatureSwitchingStates.GONE.key
    var buyNewUnitTrustFund: Int = FeatureSwitchingStates.GONE.key
    var editPrepaidElectricityBeneficiary: Int = FeatureSwitchingStates.GONE.key
    var editBeneficiaryImage: Int = FeatureSwitchingStates.GONE.key
    var appRating: Int = FeatureSwitchingStates.GONE.key
    var deviceProfiling: Int = FeatureSwitchingStates.GONE.key
    var paymentsRewrite: Int = FeatureSwitchingStates.GONE.key
    var soleProprietorRegistration: Int = FeatureSwitchingStates.GONE.key
    var debitOrderHub: Int = FeatureSwitchingStates.GONE.key
    var wimiManageBeneficiaries: Int = FeatureSwitchingStates.GONE.key
    var personalLoan: Int = FeatureSwitchingStates.GONE.key
    var personalLoanHub: Int = FeatureSwitchingStates.GONE.key
    var noticeDepositHub: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceHub: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceTransfer: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceDocumentRequest: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceAccountInformation: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceSettlementQuote: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceTaxCertificate: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinancePaidUpLetter: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceDetailedStatement: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceElectronicNatis: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceCrossBorderLetter: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceFutureDatedTransfer: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceLoanAmortization: Int = FeatureSwitchingStates.GONE.key
    var screenshots: Int = FeatureSwitchingStates.GONE.key
    var wimiLawForYou: Int = FeatureSwitchingStates.GONE.key
    var lottoAndPowerball: Int = FeatureSwitchingStates.GONE.key
    var securityCodeAvailableOnATM: Int = FeatureSwitchingStates.GONE.key
    var secondaryCardManagement: Int = FeatureSwitchingStates.GONE.key
    var secondaryCardGracePeriod: Int = FeatureSwitchingStates.GONE.key
    var standaloneCustomersEnabled: Int = FeatureSwitchingStates.GONE.key
    var creditCardHotLeads: Int = FeatureSwitchingStates.GONE.key
    var wimiBuyMoreUnitTrusts: Int = FeatureSwitchingStates.GONE.key
    var wimiRedeemUnitTrusts: Int = FeatureSwitchingStates.GONE.key
    var wimiSwitchUnitTrusts: Int = FeatureSwitchingStates.GONE.key
    var cardViewDetails: Int = FeatureSwitchingStates.GONE.key
    var addBeneficiary: Int = FeatureSwitchingStates.GONE.key
    var debitOrderHubWithMinimumValue: Int = FeatureSwitchingStates.GONE.key
    var currencyInvestmentHub: Int = FeatureSwitchingStates.GONE.key
    var wimiViewWills: Int = FeatureSwitchingStates.GONE.key
    var manageProfile: Int = FeatureSwitchingStates.GONE.key
    var wimiFlexiFuneralCover: Int = FeatureSwitchingStates.GONE.key
    var oldFuneralCoverApply: Int = FeatureSwitchingStates.GONE.key
    var swiftInternationalPayments: Int = FeatureSwitchingStates.GONE.key
    var creditCardArchivedStatements: Int = FeatureSwitchingStates.GONE.key
    var personalLoanArchivedStatements: Int = FeatureSwitchingStates.GONE.key
    var vehicleFinanceArchivedStatements: Int = FeatureSwitchingStates.GONE.key
    var homeLoanArchivedStatements: Int = FeatureSwitchingStates.GONE.key
    var studentAccountRegistration: Int = FeatureSwitchingStates.GONE.key
    var homeLoanHubInformationTab: Int = FeatureSwitchingStates.GONE.key
    var homeLoanHubFlexiReserveAmountHeader: Int = FeatureSwitchingStates.GONE.key
    var businessBankingManageCards: Int = FeatureSwitchingStates.GONE.key
    var newToBankSelfiePrivacyIndicator: Int = FeatureSwitchingStates.GONE.key
    var imiConnect: Int = FeatureSwitchingStates.GONE.key
    var displayHelpNavigation = FeatureSwitchingStates.GONE.key
    var earlyRedemption = FeatureSwitchingStates.GONE.key
    var wimiManageExergy: Int = FeatureSwitchingStates.GONE.key
    var biometricVerification: Int = FeatureSwitchingStates.GONE.key
    var freeCoverInsurance: Int = FeatureSwitchingStates.GONE.key
    var behaviouralRewards: Int = FeatureSwitchingStates.GONE.key
    var cashSend: Int = FeatureSwitchingStates.GONE.key
    var depositorPlus: Int = FeatureSwitchingStates.GONE.key
    var scanToPay: Int = FeatureSwitchingStates.ACTIVE.key
    var customiseLogin: Int = FeatureSwitchingStates.ACTIVE.key
    var monitorDeviceProfiling: Int = FeatureSwitchingStates.GONE.key
    var futurePlan = FeatureSwitchingStates.GONE.key
}