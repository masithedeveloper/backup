/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.common

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.webkit.*
import android.widget.FrameLayout
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.utils.NetworkUtils
import com.barclays.absa.utils.PermissionHelper

class PDFView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var termsWebView: WebView? = null
    private val maxAllowedRetries = 3
    private var networkRetryCounter = 0
    private lateinit var url: String
    private var baseActivity: BaseActivity = BMBApplication.getInstance().topMostActivity as BaseActivity

    companion object {
        const val PDF_EXT = ".pdf"
    }

    fun showPDF(url: String) {
        this.url = url
        baseActivity.showProgressDialog()
        setupWebView()
        networkCheck()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        termsWebView = WebView(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        termsWebView?.layoutParams = params
        addView(termsWebView)

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
                    baseActivity.dismissProgressDialog()
                }
            }, 1000)
        } else {
            loadFromUrl()
        }
    }

    private fun loadFromUrl() {
        termsWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$url")
    }

    private inner class PDFRendererClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
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
            baseActivity.dismissProgressDialog()
        }
    }

    private fun reloadWebView() {
        termsWebView?.let {
            removeView(it)
            it.destroy()
            termsWebView = null
        }

        termsWebView = WebView(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        termsWebView?.layoutParams = params
        addView(termsWebView)
    }

    private fun downloadPDF() {
        PermissionHelper.requestExternalStorageWritePermission(baseActivity) {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,
                    URLUtil.guessFileName(url, null, null))
            val downloadManager = baseActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }
    }
}