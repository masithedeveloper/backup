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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.databinding.LinkingIdVerificationRequestFragmentBinding
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FragmentPermissionHelper
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.CameraUtil
import com.oneidentity.sdk.android.OneIdentity
import com.oneidentity.sdk.android.Security.Response
import com.oneidentity.sdk.android.activities.options.LivenessOptions
import com.oneidentity.sdk.android.interfaces.ResultListener
import styleguide.utils.AnimationHelper
import styleguide.utils.TextFormattingUtils

class LinkingIdVerificationRequestFragment : LinkingBaseFragment(R.layout.linking_id_verification_request_fragment), OnBackPressedInterface {
    private val binding by viewBinding(LinkingIdVerificationRequestFragmentBinding::bind)
    private var shouldPerformIdNumberLookup: Boolean = false
    private var idNumber: String = ""
    private var retryCount: Int = 0

    companion object {
        private const val CAMERA_PERMISSION_DENIED = "Please allow camera access for this app in Android Settings"
        private const val NOT_ENROLLED = "The user is trying to verify without being enrolled first"
        private const val AMBIGUOUS_OUTCOME = "ambiguous outcome"
        private const val CAMERA_DENIED = "Camera denied"
        private const val STREAMING_ERROR = "Streaming error"
        private const val ID_NUMBER_MISSING = "ID Number missing"
        private const val AMBIENT_LIGHT_TOO_STRONG = "Ambient light too strong"
        private const val PLEASE_DO_NOT_TALK = "Please do not talk"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.linking_verify_identity))
        appCacheService.setIsIdentificationAndVerificationFlow(true)

        idNumber = arguments?.let { LinkingIdVerificationRequestFragmentArgs.fromBundle(it).idNumber } ?: ""
        if (idNumber.isNotBlank()) {
            appCacheService.setChangePrimaryDeviceFlow(true)
            shouldPerformIdNumberLookup = true
            linkingViewModel.fetchBioReference()
        }

        showToolBar()
        setupListeners()
        setupObserver()

        if (!CameraUtil.isFrontCameraAvailable(BMBApplication.getInstance().applicationContext)) {
            getBiometricStatusViewModel.fetchBiometricStatus(LinkingIdType.ID_DOCUMENT, appCacheService.getCustomerIdNumber())

            getBiometricStatusViewModel.biometricStatusLiveData.observe(viewLifecycleOwner, {
                if (!BiometricStatus.shouldNotFallBackToOldFlow(it.biometricStatus)) {
                    appCacheService.setShouldRevertToOldLinkingFlow(true)

                    LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showFailureScreenFragmentWithNoContactDetails(R.string.linking_we_could_not_verify_your_identity, -1, R.string.ok) {
                        startActivity(Intent(requireContext(), AccountLoginActivity::class.java))
                    }
                } else {
                    LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showFailureScreenFragment(R.string.linking_we_could_not_verify_your_identity, R.string.linking_we_could_not_verify_your_identity_masked_error, R.string.ok) {
                        startActivity(Intent(requireContext(), AccountLoginActivity::class.java))
                    }
                }
            })
        }

        if (binding.faceScanDescriptionTextView.isVisible) {
            TextFormattingUtils.makeTextClickable(binding.faceScanDescriptionTextView, R.color.pink, getString(R.string.linking_camera_permission_request_instruction), getString(R.string.linking_learn_more), object : ClickableSpan() {
                override fun onClick(p0: View) {
                    AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerifyYourIdentityScreen_LearnMoreButtonClicked")
                    openFailureBottomSheetDialog()
                }
            })
        }

        TextFormattingUtils.changeTextColour(binding.faceScanDescriptionTextView, R.color.red, getString(R.string.linking_camera_permission_request_instruction), getString(R.string.linking_please_note_text_to_highlight))
        TextFormattingUtils.clearSpannableStringBuilder()

        TextFormattingUtils.makeTextClickable(binding.termsAndConditionsCheckBoxView.checkBoxTextView, R.color.graphite, getString(R.string.linking_camera_permission_terms_conditions), getString(R.string.linking_camera_permission_terms_conditions_clickable_text), object : ClickableSpan() {
            override fun onClick(p0: View) {
                AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerifyYourIdentityScreen_LearnMoreButtonClicked")
                val url = "https://www.absa.co.za/legal-and-compliance/privacy-statement/"
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
            }
        })
        TextFormattingUtils.clearSpannableStringBuilder()
    }

    private fun openFailureBottomSheetDialog() {
        val bottomSheetContent = GenericBottomSheetContent().apply {
            toolbarTitle = R.string.linking_learn_more
            toolbarActionTitle = R.string.done
            contentTitleText = R.string.linking_how_to_scan_your_face
            contentText = R.string.linking_how_to_scan_your_face_text
            bottomSheetImage = R.drawable.ic_learn_more
        }

        val addBottomDialogFragment = GenericBottomSheetDialogFragment.newInstance(bottomSheetContent)
        GenericBottomSheetDialogFragment.toolbarActionOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_ScanFaceLearnMoreScreen_DoneButtonClicked")
            addBottomDialogFragment.dismiss()
        }
        addBottomDialogFragment.show(parentFragmentManager, "")
    }

    private fun setupListeners() {
        binding.scanFaceButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerifyYourIdentityScreen_ScanFaceButtonClicked")
            if (!binding.termsAndConditionsCheckBoxView.isChecked) {
                binding.termsAndConditionsCheckBoxView.setErrorMessage(R.string.plz_accept_conditions)
                AnimationHelper.shakeShakeAnimate(binding.termsAndConditionsCheckBoxView)
                return@setOnClickListener
            }
            assertLiveliness()
        }
    }

    private fun setupObserver() {
        if (shouldPerformIdNumberLookup) {
            linkingViewModel.bioReferenceResponse = MutableLiveData()
            linkingViewModel.bioReferenceResponse.observe(viewLifecycleOwner, {
                linkingViewModel.linkingReferenceNumber = it.referenceNumber
                dismissProgressDialog()

                if (it.referenceNumber.isNotBlank()) {
                    FacialBiometricUtil.startSDK(requireContext(), it.referenceNumber)
                    MonitoringInteractor().logBiometricCheckEvent("${it.referenceNumber} OneIdentitySDK - Started")
                } else {
                    MonitoringInteractor().logBiometricCheckEvent("OneIdentitySDK - No reference")
                    showMessageError(getString(R.string.linking_we_couldnt_continue_linking_this_account))
                    return@observe
                }

                if (!OneIdentity.getInstance().isValidIDNumber(idNumber, true)) {
                    MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} isValidIDNumber - false (LinkingIdVerificationRequestFragment)")
                    showMessageError(getString(R.string.linking_we_couldnt_continue_linking_this_account))
                } else {
                    MonitoringInteractor().logBiometricCheckEvent("${it.referenceNumber} isValidIDNumber - true (LinkingIdVerificationRequestFragment)")
                }
            })
        }
    }

    private fun assertLiveliness() {
        val livelinessOptions = LivenessOptions()
        livelinessOptions.deviceId(SecureUtils.getDeviceID().dropLast(4))
        with(linkingViewModel.linkingReferenceNumber.take(3)) {
            when (this) {
                IdentificationAndVerificationState.LIVELINESS.referencePrefix -> {
                    linkingViewModel.identificationAndVerificationState = IdentificationAndVerificationState.LIVELINESS
                    livelinessOptions.type(LivenessOptions.TYPE.LA)
                    livelinessOptions.scope(LivenessOptions.SCOPE.VERIFICATION)
                    MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} scope - Verification (LIV)")
                }
                IdentificationAndVerificationState.GENUINE_PRESENCE.referencePrefix -> {
                    linkingViewModel.identificationAndVerificationState = IdentificationAndVerificationState.GENUINE_PRESENCE
                    livelinessOptions.type(LivenessOptions.TYPE.GPA)
                    livelinessOptions.scope(LivenessOptions.SCOPE.ENROLMENT)
                    MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} scope - ENROLMENT (GPE)")
                }
                IdentificationAndVerificationState.GENUINE_PRESENCE_SUBSEQUENT.referencePrefix -> {
                    linkingViewModel.identificationAndVerificationState = IdentificationAndVerificationState.GENUINE_PRESENCE_SUBSEQUENT
                    livelinessOptions.type(LivenessOptions.TYPE.GPA)
                    livelinessOptions.scope(LivenessOptions.SCOPE.VERIFICATION)
                    MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} scope - Verification (GPS)")
                }
                else -> {
                    linkingViewModel.identificationAndVerificationState = IdentificationAndVerificationState.OTHER
                    livelinessOptions.type(LivenessOptions.TYPE.LA)
                    livelinessOptions.scope(LivenessOptions.SCOPE.VERIFICATION)
                    MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} scope - Verification (Unhandled)")
                }
            }
        }

        OneIdentity.getInstance().assertLiveness(linkingActivity, false, livelinessOptions, object : ResultListener {
            override fun onSuccess(response: Response?) {
                appCacheService.setRequestId(response?.requestID.toString())
                Handler(Looper.getMainLooper()).postDelayed({
                    MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} assertLiveness - Success (${response?.requestID.toString()})")
                    navigate(LinkingIdVerificationRequestFragmentDirections.actionIdVerificationRequestFragmentToLinkingVerificationInProgressFragment())
                }, 500)
            }

            override fun onCancelled() {
                MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} assertLiveness - Cancelled")
            }

            override fun onError(reason: String?) {
                MonitoringInteractor().logBiometricCheckEvent("${appCacheService.getBiometricReferenceNumber()} assertLiveness - Failure ${reason ?: "No reason provided by SDK"}")
                Handler(Looper.getMainLooper()).postDelayed({
                    when {
                        //TODO: Remove duplicated code
                        reason?.contains(AMBIGUOUS_OUTCOME) == true -> {
                            if (retryCount == 2) {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreen(R.string.linking_you_have_maxed_outyour_retries, R.string.linking_when_you_are_ready_try_again, R.string.ok) {
                                    baseActivity.finish()
                                }))
                            } else {
                                retryCount += 1
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreenWithCancelButton(R.string.linking_your_face_scan_did_not_work, R.string.linking_we_could_not_verify_your_identity_using_your_face_scan, R.string.try_again) {
                                    linkingActivity.superOnBackPressed()
                                    assertLiveliness()
                                }))
                            }
                        }
                        reason?.contains(STREAMING_ERROR) == true -> {
                            if (retryCount == 2) {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreen(R.string.linking_you_have_maxed_outyour_retries, R.string.linking_when_you_are_ready_try_again, R.string.ok) {
                                    baseActivity.finish()
                                }))
                            } else {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreenWithCancelButton(R.string.linking_your_session_timed_out, R.string.linking_please_try_again, R.string.try_again) {
                                    retryCount += 1
                                    linkingActivity.superOnBackPressed()
                                    assertLiveliness()
                                }))
                            }
                        }
                        reason?.contains(CAMERA_PERMISSION_DENIED, true) == true || reason?.contains(CAMERA_DENIED) == true -> {
                            showPermissionsFailure()
                        }
                        reason?.contains(NOT_ENROLLED, true) == true -> {
                            //Eventually we will get a service to Call to let express know we got a problem
                            navigate(LinkingIdVerificationRequestFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showFailureScreenForLiveliness(reason) { baseActivity.finish() }))
                        }
                        reason?.contains(ID_NUMBER_MISSING) == true -> {
                            navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showFailureScreenFragmentWithNoContactDetails(R.string.linking_your_face_scan_did_not_work, R.string.linking_when_you_are_ready_try_again, R.string.ok) {
                                baseActivity.finish()
                            }))
                        }
                        reason?.contains(PLEASE_DO_NOT_TALK) == true -> {
                            if (retryCount == 2) {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreen(R.string.linking_you_have_maxed_outyour_retries, R.string.linking_when_you_are_ready_try_again, R.string.ok) {
                                    baseActivity.finish()
                                }))
                            } else {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreenWithCancelButton(R.string.linking_your_session_timed_out, R.string.linking_please_try_again, R.string.try_again) {
                                    retryCount += 1
                                    linkingActivity.superOnBackPressed()
                                    assertLiveliness()
                                }))
                            }
                        }
                        reason?.contains(AMBIENT_LIGHT_TOO_STRONG) == true -> {
                            if (retryCount == 2) {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreen(R.string.linking_you_have_maxed_outyour_retries, R.string.linking_too_light, R.string.ok) {
                                    baseActivity.finish()
                                }))
                            } else {
                                navigate(LinkingVerificationInProgressFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showConnectionErrorScreenWithCancelButton(R.string.linking_your_session_timed_out, R.string.linking_please_try_again, R.string.try_again) {
                                    retryCount += 1
                                    linkingActivity.superOnBackPressed()
                                    assertLiveliness()
                                }))
                            }
                        }
                        else -> {
                            if (retryCount == 2) {
                                navigate(LinkingIdVerificationRequestFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showFailureScreenForLiveliness(reason) { assertLiveliness() }))
                            } else {
                                retryCount += 1
                                navigate(LinkingIdVerificationRequestFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this@LinkingIdVerificationRequestFragment).showFailureScreenForLivelinessNoRetry(reason) { baseActivity.finish() }))
                            }
                        }
                    }
                }, 250)
            }
        })
    }

    private fun showPermissionsFailure() {
        navigate(LinkingIdVerificationRequestFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this).showPermissionsFailure {
            val uri = Uri.parse("package:${baseActivity.packageName}")
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
        }))
    }

    override fun onBackPressed(): Boolean {
        AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerifyYourIdentityScreen_CloseButtonClicked")
        if (appCacheService.isChangePrimaryDeviceFlow()) {
            baseActivity.finish()
            return true
        }
        return false
    }
}