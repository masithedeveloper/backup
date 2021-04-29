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
 */

package com.barclays.absa.banking.presentation.sureCheck

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LinkingSureCheckCountdownActivityBinding
import com.barclays.absa.banking.framework.BaseActivity.Companion.isAccessibilityEnabled
import com.barclays.absa.banking.framework.BaseActivity.Companion.preventDoubleClick
import com.barclays.absa.banking.framework.BaseDialogFragment
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.presentation.help.HelpActivity
import com.barclays.absa.banking.presentation.shared.widget.CountDownCircularView
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showErrorAlertDialog
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.AnalyticsUtil
import styleguide.utils.extensions.toMaskedCellphoneNumber

class LinkingSureCheckCountdownDialogFragment : BaseDialogFragment(), SureCheckCountdownView {
    private lateinit var binding: LinkingSureCheckCountdownActivityBinding
    private lateinit var presenter: SureCheckCountdownPresenter
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var appCacheService: IAppCacheService

    companion object {
        const val COUNT_DOWN_INTERVAL = 1000
        const val MILLIS_DURATION = 60000

        @JvmStatic
        fun newInstance(): LinkingSureCheckCountdownDialogFragment = LinkingSureCheckCountdownDialogFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        setHasOptionsMenu(true)
        appCacheService = baseActivity.appCacheService
    }

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LinkingSureCheckCountdownActivityBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity.setToolBarWithNoBackButton(getString(R.string.surecheck))

        var sureCheckCellphoneNumber = baseActivity.appCacheService.getSureCheckCellphoneNumber()
        when {
            sureCheckCellphoneNumber.isNotBlank() -> {
                sureCheckCellphoneNumber = sureCheckCellphoneNumber.toMaskedCellphoneNumber()

                binding.sureCheckHeaderTextView.text = getString(R.string.linking_surecheck_v_one_message, sureCheckCellphoneNumber)
                binding.sureCheckDisclaimerTextView.text = getString(R.string.sure_check_tv_second_cellphone, sureCheckCellphoneNumber)
            }
            appCacheService.getSureCheckEmail().isNotBlank() -> {
                val sureCheckEmail = appCacheService.getSureCheckEmail()
                binding.sureCheckDisclaimerTextView.text = getString(R.string.sure_check_tv_second_email, sureCheckEmail)
            }
            else -> {
                binding.sureCheckDisclaimerTextView.text = getString(R.string.sure_check_no_method_specified)
            }
        }
        setupTalkBack()

        val totalDuration = MILLIS_DURATION / COUNT_DOWN_INTERVAL
        binding.countDownCircularView.setDuration(totalDuration)
        binding.countDownCircularView.setDisplayText(totalDuration.toString())

        presenter = SureCheckCountdownPresenter(this, totalDuration)
        countDownTimer = object : CountDownTimer(MILLIS_DURATION.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / COUNT_DOWN_INTERVAL).toInt()
                presenter.onTimerTicked(secondsLeft)
            }

            override fun onFinish() {
                presenter.onTimerRanOut()
            }
        }

        appCacheService.getSureCheckDelegate()?.let {
            sureCheckDelegate = it
        }
        presenter.onViewCreated()

        setupClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sure_check_countdown_activity_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showCancelDialog()
                true
            }
            R.id.help_menu_item -> {
                val helpIntent = Intent(requireContext(), HelpActivity::class.java)
                startActivity(helpIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCancelDialog() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.cancel_surechek_dialog_title))
                .message(getString(R.string.cancel_surechek_dialog_text))
                .positiveButton(getString(R.string.yes))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _: DialogInterface?, _: Int -> presenter.onCancelClicked() }
                .build())
    }

    private fun announceTimerInfo(countDownCircularView: CountDownCircularView, secondsLeft: Int) {
        if (isAccessibilityEnabled) {
            AccessibilityUtils.announceTimerInfo(countDownCircularView, secondsLeft)
        }
    }

    private fun setupClickListeners() {
        binding.resendSureCheckButton.setOnClickListener { view ->
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_SurecheckPleaseApproveVerificationMessageScreen_ResendRequestButtonClicked")
            preventDoubleClick(view)
            presenter.onResendClicked()
        }

        binding.cancelSureCheckButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_SurecheckPleaseApproveVerificationMessageScreen_CancelButtonClicked")
            showCancelDialog()
        }
    }

    override fun startTimer() {
        countDownTimer.start()
    }

    override fun updateCountDownCircle(secondsRemaining: Int) {
        var prefix = ""
        if (secondsRemaining < 10) {
            prefix = "0"
        }
        val displayValue = prefix + secondsRemaining
        binding.countDownCircularView.setDisplayText(displayValue)
        announceTimerInfo(binding.countDownCircularView, secondsRemaining)
    }

    override fun stopTimer() {
        binding.countDownCircularView.setDisplayText("00")
        countDownTimer.cancel()
    }

    override fun displayResendOption() {
        binding.sureCheckDisclaimerTextView.visibility = View.GONE
        binding.resendSureCheckButton.visibility = View.VISIBLE
    }

    override fun displayFailureResult() {
        sureCheckDelegate.onSureCheckFailed()
        displayResendOption()
    }

    override fun sureCheckProcessed() {
        sureCheckDelegate.onSureCheckProcessed()
        close()
    }

    override fun showSureCheckRejected() {
        disableResendOption()
        sureCheckDelegate.onSureCheckRejected()

        close()
    }

    override fun cancelSureCheck() {
        sureCheckDelegate.onSureCheckCancelled()
        close()
    }

    override fun close() {
        if (isAdded) {
            dismissAllowingStateLoss()
        }
    }

    override fun disableResendOption() {
        binding.resendSureCheckButton.isEnabled = false
    }

    override fun maxRetriesExceeded() {
        binding.remainingApprovalTextView.visibility = View.VISIBLE
        binding.remainingApprovalTextView.setText(R.string.max_retries_exceeded)
        binding.resendSureCheckButton.visibility = View.GONE
        stopTimer()
    }

    override fun showError(errorMessage: String?) {
        showErrorAlertDialog(errorMessage)
    }

    private fun setupTalkBack() {
        if (isAccessibilityEnabled) {
            binding.sureCheckHeaderTextView.contentDescription = getString(R.string.talkback_surecheck_header)
            binding.sureCheckDisclaimerTextView.contentDescription = binding.sureCheckDisclaimerTextView.text.toString()
            AccessibilityUtils.announceRandValueTextFromView(binding.sureCheckDisclaimerTextView)
        }
    }
}