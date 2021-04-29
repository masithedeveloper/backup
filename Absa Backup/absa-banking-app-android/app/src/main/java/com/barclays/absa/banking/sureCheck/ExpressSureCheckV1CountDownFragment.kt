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
package com.barclays.absa.banking.sureCheck

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseDialogFragment
import com.barclays.absa.banking.presentation.help.HelpActivity
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckHandlerView
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AccessibilityUtils
import kotlinx.android.synthetic.main.linking_sure_check_countdown_activity.*
import styleguide.utils.extensions.toMaskedCellphoneNumber

class ExpressSureCheckV1CountDownFragment : BaseDialogFragment(), SureCheckHandlerView {

    companion object {
        private const val TOTAL_DURATION_SECONDS = 60
        fun newInstance(): ExpressSureCheckV1CountDownFragment = ExpressSureCheckV1CountDownFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        setHasOptionsMenu(true)
    }

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.linking_sure_check_countdown_activity, container, false).rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity.setToolBarWithNoBackButton(getString(R.string.surecheck))

        var sureCheckCellphoneNumber = ExpressSureCheckHandler.securityNotificationResponse.cellNumber
        when {
            sureCheckCellphoneNumber.isNotBlank() -> {
                sureCheckCellphoneNumber = sureCheckCellphoneNumber.toMaskedCellphoneNumber()

                sureCheckHeaderTextView.text = getString(R.string.linking_surecheck_v_one_message, sureCheckCellphoneNumber)
                sureCheckDisclaimerTextView.text = getString(R.string.sure_check_tv_second_cellphone, sureCheckCellphoneNumber)
            }
            baseActivity.appCacheService.getSureCheckEmail().isNotBlank() -> sureCheckDisclaimerTextView.text = getString(R.string.sure_check_tv_second_email, baseActivity.appCacheService.getSureCheckEmail())
            else -> sureCheckDisclaimerTextView.text = getString(R.string.sure_check_no_method_specified)
        }
        setupTalkBack()
        setupCountDown()
        setupClickListeners()
    }

    private fun setupCountDown() {
        countDownCircularView.setDuration(TOTAL_DURATION_SECONDS)
        countDownCircularView.setDisplayText(TOTAL_DURATION_SECONDS.toString())
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
        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.cancel_surechek_dialog_title))
                .message(getString(R.string.cancel_surechek_dialog_text))
                .positiveButton(getString(R.string.yes))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _: DialogInterface?, _: Int -> ExpressSureCheckHandler.cancelSureCheck() }
                .build())
    }

    private fun setupClickListeners() {
        resendSureCheckButton.setOnClickListener { view ->
            BaseActivity.preventDoubleClick(view)
            setupCountDown()
            ExpressSureCheckHandler.resendSureCheck()
        }

        cancelSureCheckButton.setOnClickListener {
            showCancelDialog()
        }
    }

    override fun displayResendOption() {
        sureCheckDisclaimerTextView.visibility = View.GONE
        resendSureCheckButton.visibility = View.VISIBLE
    }

    override fun timerTick(secondsLeft: Int) {
        val prefix = if (secondsLeft < 10) "0" else ""
        val displayValue = prefix + secondsLeft
        countDownCircularView?.setDisplayText(displayValue)
    }

    override fun sureCheckProcessed() {
        close()
    }

    override fun sureCheckFailed() {
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

    private fun setupTalkBack() {
        if (BaseActivity.isAccessibilityEnabled) {
            sureCheckHeaderTextView.contentDescription = getString(R.string.talkback_surecheck_header)
            sureCheckDisclaimerTextView.contentDescription = sureCheckDisclaimerTextView.text.toString()
            AccessibilityUtils.announceRandValueTextFromView(sureCheckDisclaimerTextView)
        }
    }
}