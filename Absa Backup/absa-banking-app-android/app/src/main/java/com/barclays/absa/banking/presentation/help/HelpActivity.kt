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
package com.barclays.absa.banking.presentation.help

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import kotlinx.android.synthetic.main.activity_help_option.*

class HelpActivity : BaseActivity(R.layout.activity_help_option) {

    private var showHelpNavigationButtons: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHelpNavigationButtons = FeatureSwitchingCache.featureSwitchingToggles.displayHelpNavigation == FeatureSwitchingStates.ACTIVE.key
        setToolBarBack(R.string.help_title)

        with(helpWebView) {
            webViewClient = HelpWebView()
            loadUrl(BuildConfig.helpUrlPath + BMBApplication.getApplicationLocale())
        }

        feedbackFloatingActionButtonView.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        webNavigationView.let {
            if (showHelpNavigationButtons) {
                it.setOnNextClick {
                    if (helpWebView.canGoForward()) {
                        helpWebView.goForward()
                    }
                }
                it.setOnPreviousClick {
                    if (helpWebView.canGoBack()) {
                        helpWebView.goBack()
                    }
                }
            } else {
                it.visibility = View.GONE
            }
        }
    }

    private inner class HelpWebView : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showProgressDialog()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            dismissProgressDialog()
            scrollView.fullScroll(View.FOCUS_UP)
            if (showHelpNavigationButtons) {
                with(webNavigationView) {
                    deactivateBackwardNavigationButton(helpWebView.canGoBack())
                    deactivateForwardNavigationButton(helpWebView.canGoForward())
                }
            }
        }
    }
}