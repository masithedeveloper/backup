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
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.databinding.ImmediateInterbankPaymentFragmentBinding
import com.barclays.absa.banking.express.data.isBusiness
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsBaseFragment
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.integration.DeviceProfilingInteractor.NextActionCallback
import com.barclays.absa.utils.ClientAgreementHelper
import com.barclays.absa.utils.IAbsaCacheService
import com.barclays.absa.utils.NetworkUtils.isNetworkConnected
import com.barclays.absa.utils.PdfUtil.showTermsAndConditionsClientAgreement
import com.barclays.absa.utils.extensions.viewBinding

class MultiplePaymentsImmediateInterbankPaymentFragment : PaymentsBaseFragment(R.layout.immediate_interbank_payment_fragment) {
    private val binding by viewBinding(ImmediateInterbankPaymentFragmentBinding::bind)
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    private var clientType: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.iip_header)

        getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments(object : NextActionCallback {
            override fun onNextAction() {
                val clientAgreement = if (instance.clientTypeGroup.isBusiness()) R.string.business_client_agreement else R.string.personal_client_agreement
                if (absaCacheService.isPersonalClientAgreementAccepted()) {
                    dismissProgressDialog()
                    binding.nextButton.isEnabled = true
                    ClientAgreementHelper.updateClientAgreementContainer(binding.termsOfUseCheckBoxView, true, R.string.client_agreement_have_accepted, clientAgreement, performClickOnClientAgreement)
                } else {
                    paymentsViewModel.fetchClientAgreementDetails()

                    paymentsViewModel.clientAgreementDetailsLiveData.observe(viewLifecycleOwner, { clientAgreementDetails ->
                        clientType = clientAgreementDetails.clientType
                        dismissProgressDialog()
                        if (BMBConstants.ALPHABET_N.equals(clientAgreementDetails.clientAgreementAccepted, ignoreCase = true)) {
                            ClientAgreementHelper.updateClientAgreementContainer(binding.termsOfUseCheckBoxView, false, R.string.accept_personal_client_agreement, clientAgreement, performClickOnClientAgreement)
                        } else {
                            binding.nextButton.isEnabled = true
                            ClientAgreementHelper.updateClientAgreementContainer(binding.termsOfUseCheckBoxView, true, R.string.client_agreement_have_accepted, clientAgreement, performClickOnClientAgreement)
                        }
                    })
                }
            }
        })

        binding.nextButton.setOnClickListener {
            if (absaCacheService.isPersonalClientAgreementAccepted()) {
                navigateToPaymentOverviewScreen()
            } else {
                paymentsViewModel.updateClientAgreementStatus()

                paymentsViewModel.updatedClientAgreementLiveData.observe(viewLifecycleOwner, { clientAgreementUpdated ->
                    if (clientAgreementUpdated) {
                        navigateToPaymentOverviewScreen()
                    }
                })
            }
        }

        binding.termsOfUseCheckBoxView.setOnCheckedListener { binding.nextButton.isEnabled = it }
        binding.showIIPTermsOfUseButton.setOnClickListener { startActivity(IntentFactory.getTermsAndConditionActivity(baseActivity, true)) }
    }

    private val performClickOnClientAgreement: ClickableSpan = object : ClickableSpan() {
        override fun onClick(view: View) {
            startTermsAndConditionsActivity(clientType)
        }
    }

    private fun startTermsAndConditionsActivity(clientType: String) {
        if (isNetworkConnected()) {
            showTermsAndConditionsClientAgreement(baseActivity, clientType)
        } else {
            Toast.makeText(baseActivity, getString(R.string.network_connection_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToPaymentOverviewScreen() {
        navigate(MultiplePaymentsImmediateInterbankPaymentFragmentDirections.actionMultiplePaymentsImmediateInterbankPaymentFragmentToMultiplePaymentsConfirmationFragment())
    }
}