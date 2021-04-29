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
package com.barclays.absa.banking.presentation.whatsNew

import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.express.data.ClientTypeGroup
import com.barclays.absa.banking.express.data.ClientTypePrefix
import com.barclays.absa.banking.express.data.ResidenceType
import com.barclays.absa.banking.express.data.isBusiness
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.OperatorPermissionUtils

object WhatsNewHelper {

    fun getEnabledWhatsScreens(): List<WhatsNewPage> = getWhatsNewData().filter { whatsNewPage -> whatsNewPage.isEnabled && whatsNewPage.meetBusinessRequirements() }

    private fun getWhatsNewData(): List<WhatsNewPage> = mutableListOf(
            WhatsNewPage(R.string.depositor_plus_whats_new_title, R.string.depositor_plus_whats_new_content, "whats_new_clock_and_coins_animation.json", null,
                    isEnabledFeature(featureSwitchingToggles.depositorPlus) && isSouthAfricanResident() && getServiceInterface<IAppCacheService>().isPrimarySecondFactorDevice(),
                    WhatsNewAccessPrivileges(individual = true, operator = false, business = false, joint = false, soleProprietor = false)),

            WhatsNewPage(R.string.identification_and_verification_whats_new_title, R.string.identification_and_verification_whats_new_content, "id_and_v_second_animation.json", null,
                    isEnabledFeature(featureSwitchingToggles.biometricVerification),
                    WhatsNewAccessPrivileges(individual = true, operator = true, business = true, joint = true, soleProprietor = true)),

            WhatsNewPage(R.string.future_plan_whats_new_title, R.string.future_plan_whats_new_content, "whats_new_contractual_accounts_animation.json", null,
                    isEnabledFeature(featureSwitchingToggles.futurePlan) && isSouthAfricanResident() && getServiceInterface<IAppCacheService>().isPrimarySecondFactorDevice(),
                    WhatsNewAccessPrivileges(individual = true, operator = false, business = false, joint = false, soleProprietor = false)),

            WhatsNewPage(R.string.avaf_amortisation_whats_new_title, R.string.avaf_amortisation_whats_new_content, "whats_new_avaf_amortisation.json", null,
                    isEnabledFeature(featureSwitchingToggles.vehicleFinanceLoanAmortization),
                    WhatsNewAccessPrivileges(individual = true, operator = true, business = true, joint = true, soleProprietor = true)),

            WhatsNewPage(R.string.avaf_detailed_statement_whats_new_title, R.string.avaf_detailed_statement_whats_new_content, "whats_new_avaf_detailed_statement.json", null,
                    isEnabledFeature(featureSwitchingToggles.vehicleFinanceDetailedStatement),
                    WhatsNewAccessPrivileges(individual = true, operator = true, business = true, joint = true, soleProprietor = true)),

            WhatsNewPage(R.string.business_evolve_whats_new_title, R.string.business_evolve_whats_new_content, "business_evolve_whats_new_animation.json", null,
                    isEnabledFeature(featureSwitchingToggles.soleProprietorRegistration),
                    WhatsNewAccessPrivileges(individual = true, operator = false, business = true, joint = true, soleProprietor = true)),

            WhatsNewPage(R.string.solidarity_whats_new_title, R.string.solidarity_whats_new_content, "whats_new_pay_anytime.json", null,
                    true,
                    WhatsNewAccessPrivileges(individual = true, operator = true, business = false, joint = true, soleProprietor = true)),

            WhatsNewPage(R.string.fraud_notifications_letter_whats_new_title, R.string.fraud_notifications_letter_whats_new_content, "whats_new_fraud_alert.json", null,
                    true,
                    WhatsNewAccessPrivileges(individual = true, operator = true, business = true, joint = true, soleProprietor = true)),

            ScanToPayViewModel.scanToPayWhatsNewPage
    )

    fun isEnabledFeature(featureSwitchingStates: Int): Boolean = featureSwitchingStates != FeatureSwitchingStates.GONE.key

    fun WhatsNewPage.meetBusinessRequirements(): Boolean {
        val clientTypeGroup = CustomerProfileObject.instance.clientTypeGroup
        // Sole props and joint accounts don't have operators
        when {
            clientTypeGroup.equals(ClientTypeGroup.INDIVIDUAL_CLIENT.value, true) && permissions.individual -> {
                if (OperatorPermissionUtils.isMainUser()) {
                    return true
                } else if (!OperatorPermissionUtils.isMainUser() && permissions.operator) {
                    return true
                }
            }
            clientTypeGroup.equals(ClientTypeGroup.JOINT_AND_SEVERAL.value, true) && permissions.joint -> return true

            clientTypeGroup.isBusiness() -> {
                if (OperatorPermissionUtils.isMainUser() && !permissions.operator && permissions.business) {
                    return true
                } else if (!OperatorPermissionUtils.isMainUser() && permissions.operator && permissions.business) {
                    return true
                } else if (OperatorPermissionUtils.isMainUser() && permissions.soleProprietor) {
                    return true
                }
            }
        }
        return false
    }

    private fun isSouthAfricanResident(): Boolean = when (CustomerProfileObject.instance.clientType) {
        "${ClientTypePrefix.PRIVATE_INDIVIDUAL.clientTypeCode}${ResidenceType.SA_RESIDENT.residenceCode}",
        "${ClientTypePrefix.STAFF.clientTypeCode}${ResidenceType.SA_RESIDENT.residenceCode}" -> true
        else -> false
    }
}