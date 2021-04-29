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
package com.barclays.absa.common

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.webkit.*
import android.widget.FrameLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.utils.NetworkUtils
import com.barclays.absa.utils.PdfUtil.PDF_EXT
import com.barclays.absa.utils.PdfUtil.PDF_URL
import com.barclays.absa.utils.PermissionHelper
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

class GenericPdfViewerActivity : BaseActivity() {
    private var termsWebView: WebView? = null
    private val maxAllowedRetries = 3
    private var networkRetryCounter = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        setToolBarBack(getString(R.string.pdf_viewer_title))
        showProgressDialog()

        doneButton.setOnClickListener {
            finish()
        }

        setupWebView()
        networkCheck()
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

    private fun loadFromUrl() {
        val pdfURL = intent.getStringExtra(PDF_URL)
        termsWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$pdfURL")
    }

    private inner class PDFRendererClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            //intercept viewer expansion
            if (request?.url.toString().contains(PDF_EXT, true)) {
                downloadPDF()
                return true
            }

            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            if (!detail.didCrash()) {
                reloadWebView()
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

    private fun reloadWebView() {
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

    private fun downloadPDF() {
        PermissionHelper.requestExternalStorageWritePermission(this@GenericPdfViewerActivity) {
            val url = intent.getStringExtra(PDF_URL)
            val request = DownloadManager.Request(Uri.parse(url))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,
                    URLUtil.guessFileName(url, null, null))
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }
    }
}