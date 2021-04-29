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
package com.barclays.absa.banking.presentation.shared.widget

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.databinding.PlayStoreDialogFragmentBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.SharedPreferenceService
import java.util.*

class PlayStoreRatingDialogFragment : DialogFragment() {

    private lateinit var dialogBinding: PlayStoreDialogFragmentBinding
    lateinit var nextAction: View.OnClickListener
    private var showCount = 0
    private val requestCode = 100
    private var mailboxActive = false

    companion object {
        @JvmStatic
        fun newInstance(): PlayStoreRatingDialogFragment {
            return PlayStoreRatingDialogFragment()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (::nextAction.isInitialized && !mailboxActive) {
            nextAction.onClick(null)
        }
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity
        if (activity != null) {
            dialogBinding = DataBindingUtil.inflate(activity.layoutInflater, R.layout.play_store_dialog_fragment, null, false)
            return AlertDialog.Builder(activity, R.style.MyDialogTheme)
                    .setView(dialogBinding.root)
                    .setCancelable(false)
                    .create()
        }
        return AlertDialog.Builder(BMBApplication.getInstance()).create()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogBinding.apply {
            ratingBar.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    if (ratingBar.rating > 0) {
                        submitTextView.visibility = View.VISIBLE
                    } else {
                        submitTextView.visibility = View.GONE
                    }
                }
                false
            }

            maybeLaterTextView.setOnClickListener {
                SharedPreferenceService.setAppLastRatedOn(Date().time)
                maybeLaterTextView.isEnabled = false
                dismiss()
            }

            submitTextView.setOnClickListener {
                setRating(ratingBar.rating)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setRating(rating: Float) {
        dialogBinding.apply {
            ratingBar.isEnabled = false
            MonitoringInteractor().logMonitoringEvent("PLAY_STORE_RATING", hashMapOf(Pair("rating", rating)))
            when {
                rating < 3 -> {
                    mailboxActive = true
                    Toast.makeText(context, getString(R.string.app_feedback_we_are_sad), Toast.LENGTH_LONG).show()
                    openMail("I have a suggestion", rating.toInt())
                }
                rating.toInt() == 3 -> {
                    titleTextView.text = getString(R.string.app_feedback_thank_you_short)
                    messageTextView.text = getString(R.string.app_feedback_we_appreciate_feedback)
                    ratingBar.visibility = GONE
                    maybeLaterTextView.visibility = GONE
                    submitTextView.visibility = GONE
                    Handler(Looper.getMainLooper()).postDelayed({ dismiss() }, 3000)
                }
                else -> {
                    titleTextView.text = getString(R.string.app_feedback_thank_you)
                    launchPlayStore()
                }
            }
            SharedPreferenceService.setAppLastRatedOn(Date().time)
        }
    }

    override fun onResume() {
        super.onResume()
        mailboxActive = false
        if (++showCount == 2) {
            dismiss()
        }
    }

    private fun openMail(subject: String, rating: Int) {
        var emailAddress = if (CommonUtils.isPrivateBanker()) "privatebanking@absa.co.za" else "bankingapp@absa.co.za"
        if (rating == 1) {
            emailAddress = "playstore@absa.africa"
        }
        val message = StringBuilder()
                .append("Hello app team,\n\n")
                .append("<Start typing here...>\n\n\n\n")
                .append("Version info:\n")
                .append("App version: ${BuildConfig.VERSION_NAME}\n")
                .append("Phone model: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                .append("OS Version: ${Build.VERSION.RELEASE}")
                .toString()

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress")).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_DATA_REMOVED, "Start typing your text here")
        }

        val baseActivity = activity as BaseActivity
        baseActivity.apply {
            val hasEmailClient = startActivityIfAvailable(Intent.createChooser(intent, "Send mail..."))
            if (!hasEmailClient) {
                toastShort("There are no email clients installed.")
            }
        }
    }

    private fun launchPlayStore() {
        val baseActivity = activity as? BaseActivity ?: BMBApplication.getInstance().topMostActivity as? BaseActivity

        baseActivity?.apply {
            val appPackageName = getString(R.string.app_package_name)
            val playstoreIntent = Intent(Intent.ACTION_VIEW)
            playstoreIntent.data = Uri.parse("market://details?id=$appPackageName")
            if (!startActivityIfAvailable(playstoreIntent, requestCode)) {
                startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")), requestCode)
            }
        }
    }

}