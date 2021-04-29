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
package com.barclays.absa.banking.presentation.sureCheckV2

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.monitoring.MonitoringService
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus.Companion.shouldAllowIdentifyFlow
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.linking.ui.GenericBottomSheetContent
import com.barclays.absa.banking.linking.ui.GenericBottomSheetDialogFragment.Companion.newInstance
import com.barclays.absa.banking.linking.ui.GenericBottomSheetDialogFragment.Companion.toolbarActionOnClickListener
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.manage.devices.ReasonDeviceNotAvailable2faActivity
import com.barclays.absa.banking.presentation.help.HelpActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.sure_check_countdown_dialog_fragment.*
import java.util.*

class SureCheck2CountDownDialogFragment : DialogFragment(), SureCheckHandlerView {

    private var startTimeMillis: Long = 0
    private var countDownTimer: CountDownTimer? = null
    private val appCacheService: IAppCacheService = getServiceInterface()

    companion object {
        private const val TOTAL_DURATION_SECONDS = 60

        @JvmStatic
        fun newInstance(): SureCheck2CountDownDialogFragment {
            return SureCheck2CountDownDialogFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        SureCheckHandler.start(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        setHasOptionsMenu(true)
    }

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.sure_check_countdown_dialog_fragment, null, false)
        return inflate.rootView
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        if (!SureCheckHandler.isActive) {
            close()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appCacheService.setDisplayedSureCheckCountDown(true)
        initializeToolbar()

        if (appCacheService.isPrimarySecondFactorDevice()) {
            notReceivingButton.visibility = View.INVISIBLE
        } else {
            notReceivingButton.setOnClickListener {
                AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerificationRequiredScreen_NotReceivingAVerifictionButtonClicked")
                SureCheckHandler.stopTimer()
                navigateToUnavailableReasonsScreen()
            }
        }

        setupCountDown()
        startTimeMillis = System.currentTimeMillis()

        resendSureCheckButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerificationTimedOutScreen_ResendVerificationButtonClicked")
            SureCheckHandler.stopTimer()
            resendSureCheckButton.isEnabled = false
            SureCheckHandler.resendSureCheck()
        }

        if (BuildConfig.TOGGLE_DEF_BIOMETRIC_VERIFICATION_ENABLED && FeatureSwitchingCache.featureSwitchingToggles.biometricVerification == FeatureSwitchingStates.ACTIVE.key && (appCacheService.isIdentityAndVerificationPostLogin() || shouldAllowIdentifyFlow(instance.biometricStatus))) {
            iDoNotHaveMyDeviceButton.visibility = View.VISIBLE

            notReceivingButton.setOnClickListener {
                openFailureBottomSheetDialog()
            }

            iDoNotHaveMyDeviceButton.setOnClickListener {
                AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_VerificationRequiredScreen_IDontHaveMyDeviceButtonClicked")
                SureCheckHandler.stopTimer()
                navigateToUnavailableReasonsScreen()
                appCacheService.setIsForgotPrimaryDeviceButtonClicked(true)
            }
        }
    }

    private fun openFailureBottomSheetDialog() {
        val failureBottomSheetContent = GenericBottomSheetContent().apply {
            toolbarTitle = R.string.linking_error_not_receiving_surecheck
            toolbarActionTitle = R.string.ok
            contentTitleText = R.string.linking_error_some_wifi_networks_block_notifications
            contentText = R.string.linking_error_please_check_your_connection
            bottomSheetImage = R.drawable.absa_placeholder_logo_red_large
        }

        val addBottomDialogFragment = newInstance(failureBottomSheetContent)
        toolbarActionOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_SomeWiFiNetworksBlockNotificationsScreen_OKButtonClicked")
            addBottomDialogFragment.dismiss()
        }
        addBottomDialogFragment.show(parentFragmentManager, "")
    }

    private fun navigateToUnavailableReasonsScreen() {
        startActivity(Intent(activity, ReasonDeviceNotAvailable2faActivity::class.java))
        close()
    }

    private fun setupCountDown() {
        countDownCircularView.visibility = View.VISIBLE
        remainingApprovalTextView.visibility = View.VISIBLE
        countDownCircularView.setDuration(TOTAL_DURATION_SECONDS)
        countDownCircularView.setDisplayText(TOTAL_DURATION_SECONDS.toString())
    }

    private fun updateCountDownCircle(secondsRemaining: Int) {
        var prefix = ""
        if (secondsRemaining < 10) {
            prefix = "0"
        }
        val displayValue = prefix + secondsRemaining
        countDownCircularView?.setDisplayText(displayValue)
    }

    override fun timerTick(secondsLeft: Int) {
        updateCountDownCircle(secondsLeft)
    }

    private fun stopTimer() {
        SureCheckHandler.stopTimer()
        countDownTimer?.cancel()
    }

    override fun sureCheckProcessed() {
        recordSuccessMonitoringEvent()
        close()
    }

    @Suppress("DEPRECATION")
    private fun recordSuccessMonitoringEvent() {
        val endTimeMillis = System.currentTimeMillis()
        val duration = endTimeMillis - startTimeMillis
        val dataMap: MutableMap<String, Long> = HashMap()
        dataMap[MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME] = duration
        MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TIME_TAKEN_FOR_AUTH_TO_BE_PROCESSED, dataMap)
    }

    @Suppress("DEPRECATION")
    private fun recordFailureMonitoringEvent() {
        val endTimeMillis = System.currentTimeMillis()
        val duration = endTimeMillis - startTimeMillis
        val dataMap: MutableMap<String, Long> = HashMap()
        dataMap[MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME] = duration
        MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TIME_TAKEN_FOR_AUTH_TO_FAIL, dataMap)
    }

    override fun sureCheckFailed() {
        recordFailureMonitoringEvent()
        close()
    }

    override fun showSureCheckRejected() {
        close()
    }

    override fun close() {
        if (isAdded) {
            dismissAllowingStateLoss()
        }
    }

    private fun initializeToolbar() {
        (toolbar as Toolbar).apply {
            inflateMenu(R.menu.sure_check_request_2fa_activity_menu)
            setTitle(R.string.surecheck)
            setNavigationIcon(R.drawable.ic_cross_dark)

            setNavigationOnClickListener { showCancelDialog() }
            setOnMenuItemClickListener {
                activity?.startActivity(Intent(activity, HelpActivity::class.java))
                true
            }
        }
    }

    private fun showCancelDialog() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.cancel_surechek_dialog_title))
                .message(getString(R.string.cancel_surechek_dialog_text))
                .positiveButton(getString(R.string.yes))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _: DialogInterface?, _: Int ->
                    stopTimer()
                    appCacheService.getSureCheckDelegate()?.onSureCheckCancelled()
                    close()
                }
                .build())
    }

    override fun displayResendOption() {
        if (isVisible) {
            sureCheckContentConstraintLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))
            val surecheckNoConnection = "${getString(R.string.we_could_not_connect_to_the_app)}\n\n${getString(R.string.please_try_resending)}"

            approveRequestMessageTextView.text = surecheckNoConnection
            countDownCircularView.visibility = View.GONE
            remainingApprovalTextView.visibility = View.GONE
            resendSureCheckButton.visibility = View.VISIBLE
            resendSureCheckButton.isEnabled = true
        }
    }
}