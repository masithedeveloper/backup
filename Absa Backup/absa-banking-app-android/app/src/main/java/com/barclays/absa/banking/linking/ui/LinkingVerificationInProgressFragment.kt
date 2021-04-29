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
package com.barclays.absa.banking.linking.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.databinding.LinkingVerificationInProgressFragmentBinding
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.express.identificationAndVerification.FaceBioResultViewModel
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.viewModel
import com.oneidentity.sdk.android.OneIdentity
import styleguide.screens.GenericResultScreenFragment

class LinkingVerificationInProgressFragment : LinkingBaseFragment(R.layout.linking_verification_in_progress_fragment), OnBackPressedInterface {
    private val binding by viewBinding(LinkingVerificationInProgressFragmentBinding::bind)

    private lateinit var faceBioResultViewModel: FaceBioResultViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var linkingSureCheckHandler: LinkingSureCheckHandler

    private var isBusy: Boolean = true
    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        faceBioResultViewModel = requireActivity().viewModel()
    }

    private fun changePrimaryDelegateSetUp(context: Context) {
        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    handler.removeCallbacksAndMessages(null)
                    linkingViewModel.changePrimaryDeviceFromTransaction()
                }, 250)
            }

            override fun onSureCheckCancelled() {
                super.onSureCheckCancelled()

                handler.removeCallbacksAndMessages(null)
                navigateToSureCheckResultScreen(R.string.surecheck_two_cancelled, R.string.surecheck_cancelled_description)
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()

                handler.removeCallbacksAndMessages(null)
                navigateToSureCheckResultScreen(R.string.surecheck_failed)
            }

            override fun onSureCheckRejected() {
                super.onSureCheckRejected()

                handler.removeCallbacksAndMessages(null)
                navigateToSureCheckResultScreen(R.string.transaction_rejected)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarNoBack(R.string.linking_verify_identity)

        val deviceIdDropLast4Characters = SecureUtils.getDeviceID().dropLast(4)

        animateText(getString(R.string.linking_submitting))
        setupChangePrimary()

        if (appCacheService.isIdentificationAndVerificationLinkingFlow() && !appCacheService.isIdentityAndVerificationPostLogin()) {
            linkingSureCheckHandler = LinkingSureCheckHandler(this, linkingViewModel)
            if (appCacheService.isBioAuthenticated()) {
                handler.removeCallbacksAndMessages(null)
                linkingSureCheckHandler.requestSureCheckForLinking(-1)
                return
            }
        } else {
            changePrimaryDelegateSetUp(requireContext())
            if (appCacheService.isBioAuthenticated()) {
                linkingViewModel.changePrimaryDeviceFromTransaction()
                return
            }
        }

        faceBioResultViewModel.failureLiveData.observe(baseActivity, { failureResult ->
            handler.removeCallbacksAndMessages(null)

            if (failureResult.resultMessages.isNotEmpty()) {
                appCacheService.clearAllIdentificationAndVerificationCacheValues()
                when {
                    linkingViewModel.linkedProfilesList.isEmpty() && linkingViewModel.hasDigitalProfile -> {
                        LinkingResultFactory(this).showFailureScreenFragment(R.string.linking_unable_to_link_this_account_heading, R.string.linking_unable_to_link_this_account_description, R.string.done) {
                            baseActivity.finish()
                        }
                    }
                    linkingViewModel.linkedProfilesList.isEmpty() && linkingViewModel.hasCIFProfile -> {
                        appCacheService.clearAllIdentificationAndVerificationCacheValues()
                        navigateToAccountLogin()
                    }
                    linkingViewModel.linkedProfilesList.isEmpty() && !linkingViewModel.hasCIFProfile -> navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this).showFailureForNoActiveAccounts()))
                    failureResult.resultMessages.first().responseMessage.contains(getString(R.string.linking_revert_to_existing_process)) -> {
                        LinkingResultFactory(this).clearCache()
                        appCacheService.setShouldRevertToOldLinkingFlow(true)
                        navigateToAccountLogin()
                    }
                    else -> {
                        navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingVerificationInProgressFragment).showFailureScreenFragment(getString(R.string.linking_identity_technical_error), failureResult.resultMessages.first().responseMessage, getString(R.string.done)) { baseActivity.finish() }))
                    }
                }
            } else {
                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingVerificationInProgressFragment).showFailureScreenFragment(getString(R.string.linking_identity_technical_error), "", getString(R.string.done)) { baseActivity.finish() }))
            }
            dismissProgressDialog()
            isBusy = false
        })

        if (linkingViewModel.identificationAndVerificationState == IdentificationAndVerificationState.GENUINE_PRESENCE) {
            OneIdentity.getInstance().submitData(linkingActivity, deviceIdDropLast4Characters, {
                appCacheService.setRequestId(it.requestID)
                appCacheService.setBiometricReferenceNumber(it.reference)

                handler.removeCallbacksAndMessages(null)
                animateText(getString(R.string.linking_verifying))

                faceBioResultViewModel.pollForFaceBioResult(linkingViewModel.linkingReferenceNumber, appCacheService.getRequestId())

                setUpObserver()

                MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} - submitDataSuccess() - ${it.message} - Request Id ${it.requestID}")
            }, { errorResponse ->
                MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} ${errorResponse.errorMessage}")

                getBiometricStatusViewModel.fetchBiometricStatus(LinkingIdType.ID_DOCUMENT, appCacheService.getCustomerIdNumber())

                getBiometricStatusViewModel.biometricStatusLiveData.observe(viewLifecycleOwner, {
                    handler.removeCallbacksAndMessages(null)
                    if (!BiometricStatus.shouldNotFallBackToOldFlow(it.biometricStatus)) {
                        appCacheService.setShouldRevertToOldLinkingFlow(true)

                        LinkingResultFactory(this@LinkingVerificationInProgressFragment).showFailureScreenFragmentWithNoContactDetails(R.string.linking_we_could_not_verify_your_identity, -1, R.string.ok) {
                            startActivity(Intent(requireContext(), AccountLoginActivity::class.java))
                        }
                    } else {
                        appCacheService.setShouldRevertToOldLinkingFlow(false)
                        navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingVerificationInProgressFragment).showFailureScreenForLiveliness(errorResponse.errorMessage) { baseActivity.finish() }))
                    }
                })
            })
        } else {
            faceBioResultViewModel.pollForFaceBioResult(linkingViewModel.linkingReferenceNumber, appCacheService.getRequestId())
            setUpObserver()
        }
    }

    private fun setUpObserver() {
        faceBioResultViewModel.faceBioResultLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            binding.processingTextView.text = getString(R.string.linking_complete)
            handler.removeCallbacksAndMessages(null)

            if (it.result != BiometricStatus.VERIFIED) {
                MonitoringInteractor().logTechnicalEvent("ID&V", "VerificationService", it.result.toString())
            }

            when (it.result) {
                BiometricStatus.VERIFIED -> {
                    appCacheService.setIsBioAuthenticated(true)
                    appCacheService.setBiometricReferenceNumber(linkingViewModel.linkingReferenceNumber)

                    BMBLogger.d("DeviceProfiling: PreLoginCSID used", appCacheService.getCustomerSessionId())
                    getDeviceProfilingInteractor().notifyLogin()

                    if (appCacheService.isBioAuthenticated() && appCacheService.isIdentificationAndVerificationLinkingFlow() && !appCacheService.isIdentityAndVerificationPostLogin()) {
                        when {
                            linkingViewModel.linkedProfilesList.size == 1 -> {
                                linkingSureCheckHandler.requestSureCheckForLinking(0)
                            }
                            linkingViewModel.linkedProfilesList.isEmpty() && linkingViewModel.hasDigitalProfile -> {
                                LinkingResultFactory(this).showFailureScreenFragment(R.string.linking_unable_to_link_this_account_heading, R.string.linking_unable_to_link_this_account_description, R.string.done) {
                                    baseActivity.finish()
                                }
                            }
                            linkingViewModel.linkedProfilesList.isEmpty() && linkingViewModel.hasCIFProfile -> {
                                appCacheService.clearAllIdentificationAndVerificationCacheValues()
                                navigateToAccountLogin()
                            }
                            else -> {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionLinkingVerificationInProgressFragmentToLinkingProfileSelectionFragment())
                            }
                        }
                    } else if (appCacheService.isBioAuthenticated() && (appCacheService.isChangePrimaryDeviceFlow() || appCacheService.isChangePrimaryDeviceFromNoPrimaryDeviceScreen() || appCacheService.isChangePrimaryDeviceFlowFromSureCheck())) {
                        linkingViewModel.changePrimaryDeviceFromTransaction(appCacheService.getRequestId())
                    }
                }
                BiometricStatus.TECHNICAL_ERROR -> {
                    navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this).showFailureScreenFragment(R.string.something_went_wrong, R.string.linking_identity_technical_error, R.string.try_again) {
                        navigate(LinkingVerificationInProgressFragmentDirections.actionLinkingVerificationInProgressFragmentToIdVerificationRequestFragment(linkingViewModel.idNumber))
                    }))
                }
                else -> {
                    navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this).showFailureScreenFragmentForVerification(R.string.linking_we_couldnt_continue_linking_this_account, R.string.linking_identity_technical_error, it.result.toString(), R.string.done) {
                        navigateToWelcomeScreen()
                    }))
                }
            }
            isBusy = false
        })
    }

    private fun navigateToWelcomeScreen() {
        Intent(baseActivity, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(this)
        }
    }

    private fun animateText(textToAnimate: String) {
        val runnable: Runnable = object : Runnable {
            var count = 0
            override fun run() {
                count++
                when (count) {
                    1 -> binding.processingTextView.text = getString(R.string.linking_progress_first_step, textToAnimate)
                    2 -> binding.processingTextView.text = getString(R.string.linking_progress_second_step, textToAnimate)
                    3 -> {
                        binding.processingTextView.text = getString(R.string.linking_progress_third_step, textToAnimate)
                        count = 0
                    }
                }
                handler.postDelayed(this, 1 * 1000)
            }
        }
        handler.postDelayed(runnable, 1 * 1000)
    }

    override fun onBackPressed(): Boolean = isBusy

    private fun setupChangePrimary() {
        linkingViewModel.changePrimaryDeviceSureCheckResult.observe(viewLifecycleOwner, {
            handler.removeCallbacksAndMessages(null)

            if (it.sureCheckFlag == null) {
                if (it.deviceChanged) {
                    dismissProgressDialog()
                    val genericResultScreenProperties = if ((appCacheService.isChangePrimaryDeviceFlowFromSureCheck() && !appCacheService.isChangePrimaryDeviceFlowFailOver()) || appCacheService.isChangePrimaryDeviceFromNoPrimaryDeviceScreen()) {
                        LinkingResultFactory(this).showPrimaryDeviceChangedSuccessfullyDuringSureCheckFragment()
                    } else {
                        LinkingResultFactory(this).showPrimaryDeviceChangedSuccessfullyResultFragment()
                    }
                    findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
                it.transactionStatus?.let { transactionStatus ->
                    if (transactionStatus.toLowerCase(BMBApplication.getApplicationLocale()) == BMBConstants.FAILURE && it.transactionMessage != null) {
                        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                                .title(getString(R.string.manage_device_server_failure))
                                .message(it.transactionMessage)
                                .build())
                    }
                }
            } else {
                val verificationType = it.sureCheckFlag?.let { sureCheckFlag -> TransactionVerificationType.valueOf(sureCheckFlag) }
                with(appCacheService) {
                    setSureCheckReferenceNumber(it.referenceNumber)
                    if (it.cellnumber.isNotBlank()) {
                        setSureCheckCellphoneNumber(it.cellnumber)
                    }
                    if (it.email.isNotBlank()) {
                        setSureCheckEmail(it.email)
                    }
                }

                when (verificationType) {
                    TransactionVerificationType.SURECHECKV1, TransactionVerificationType.SURECHECKV1Required, TransactionVerificationType.SURECHECKV2Required -> goToCountDownTimerScreen(verificationType)
                    TransactionVerificationType.SURECHECKV1_FALLBACK, TransactionVerificationType.SURECHECKV1_FALLBACKRequired -> sureCheckDelegate.initiateTransactionVerificationEntryScreen()
                    TransactionVerificationType.NoPrimaryDevice -> showNoPrimaryDeviceScreen()
                    else -> {
                        MonitoringInteractor().logTechnicalEvent("LinkingVerificationInProgressFragment", "VerificationService", verificationType.toString())
                    }
                }
            }
        })
    }

    private fun goToCountDownTimerScreen(verificationType: TransactionVerificationType) {
        handler.removeCallbacksAndMessages(null)
        if (verificationType == TransactionVerificationType.SURECHECKV1 || verificationType == TransactionVerificationType.SURECHECKV1Required) {
            sureCheckDelegate.initiateV1CountDownScreen()
        } else {
            sureCheckDelegate.initiateV2CountDownScreen()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)

        super.onDestroy()
    }
}