/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.login.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils
import com.barclays.absa.utils.NetworkUtils
import com.barclays.absa.utils.SharedPreferenceService
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

open class TermsAndConditionsActivity : BaseActivity() {
    protected var termsWebView: WebView? = null
    private val maxAllowedRetries = 3
    private var networkRetryCounter = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)

        showProgressDialog()
        setupWebView()
        networkCheck()

        doneButton.setOnClickListener {
            finish()
        }

        RebuildUtils.setupToolBar(this, getString(R.string.termsAndCondition), -1, false, null)
    }

    private fun setupWebView() {
        termsWebView = WebView(applicationContext)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        termsWebView?.layoutParams = params
        termsContentView.addView(termsWebView)

        termsWebView?.settings?.javaScriptEnabled = true
        termsWebView?.webViewClient = PDFRendererClient()
    }

    private fun networkCheck() {
        if (!NetworkUtils.isNetworkConnected()) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (networkRetryCounter < maxAllowedRetries) {
                    networkRetryCounter++
                    networkCheck()
                } else {
                    dismissProgressDialog()
                }
            }, 1000)
        } else {
            loadFromUrl()
        }
    }

    protected open fun loadFromUrl() {
        var language = SharedPreferenceService.getCurrentAppLanguage()
        language = if (BMBConstants.ENGLISH_LANGUAGE.toString().equals(language, ignoreCase = true) || BMBConstants.ENGLISH_CODE.equals(language, ignoreCase = true)) BMBConstants.ENGLISH_CODE else BMBConstants.AFRIKAANS_CODE

        if (intent != null && intent.getBooleanExtra(AppConstants.SHOULD_BE_SHOWING_IIP, false)) {
            termsWebView?.loadUrl("file:///android_asset/iip_conditions.html")
        } else if (intent != null && intent.getBooleanExtra(AppConstants.SHOULD_BE_SHOWING_STUDENT_TERMS, false)) {
            val termsUrl = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/product-tc/student-silver-vas.pdf"
            termsWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$termsUrl")
        } else {
            var agreementLink = "https://ib.absa.co.za/absa-online/reportUnAuth/clientAgreement/docfusion.pdf?clientAgreementIn=%s&language=%s&forceDownload=false"
            val clientType = intent.getStringExtra(AppConstants.CLIENT_TYPE) ?: "I"

            agreementLink = String.format(agreementLink, clientType, language)
            termsWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$agreementLink")
        }
    }

    private inner class PDFRendererClient : WebViewClient() {
        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            if (!detail.didCrash()) {
                termsWebView?.let {
                    termsContentView.removeView(it)
                    it.destroy()
                    termsWebView = null
                }

                termsWebView = WebView(applicationContext)
                val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                termsWebView?.layoutParams = params
                termsContentView.addView(termsWebView)
            }
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            try {
                if (view.contentHeight == 0) {
                    view.reload()
                }
            } catch (e: IllegalStateException) {
                BMBLogger.e(javaClass.simpleName, e.message)
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            dismissProgressDialog()
        }
    }
}