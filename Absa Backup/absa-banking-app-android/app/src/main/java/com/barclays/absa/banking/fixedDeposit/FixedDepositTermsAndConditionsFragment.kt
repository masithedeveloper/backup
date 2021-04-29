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

package com.barclays.absa.banking.fixedDeposit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.FixedDepositTermsAndConditionsFragmentBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.NetworkUtils
import com.barclays.absa.utils.extensions.viewBinding

class FixedDepositTermsAndConditionsFragment : BaseFragment(R.layout.fixed_deposit_terms_and_conditions_fragment) {
    private val binding by viewBinding(FixedDepositTermsAndConditionsFragmentBinding::bind)

    private lateinit var fixedDepositActivity: FixedDepositActivity
    private var termsAndConditionsWebView: WebView? = null
    private val maxAllowedRetries = 3
    private var networkRetryCounter = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fixedDepositActivity = context as FixedDepositActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Terms and Conditions Screen")

        fixedDepositActivity.apply {
            setToolbarTitle(getString(R.string.fixed_deposit_information))
            showToolbarBackArrow()
            showToolbar()
        }
        initView()
    }

    private fun initView() {
        setUpWebView()
        networkCheck()
        binding.nextButton.setOnClickListener {
            if (binding.termsAndConditionsCheckBoxView.isValid) {
                navigate(FixedDepositTermsAndConditionsFragmentDirections.actionFixedDepositTermsAndConditionsFragmentToFixedDepositNewFixedDepositConfirmationFragment())
            } else {
                binding.termsAndConditionsCheckBoxView.setErrorMessage(getString(R.string.fixed_deposit_terms_and_conditions_error_message))
            }
        }

        arguments?.let {
            if (!FixedDepositTermsAndConditionsFragmentArgs.fromBundle(it).showAgreeCheckBox) {
                binding.termsAndConditionsCheckBoxView.visibility = View.GONE
                binding.nextButton.text = getText(R.string.done)
                binding.nextButton.setOnClickListener {
                    fixedDepositActivity.onBackPressed()
                }
            }
        }
    }

    private fun loadFromUrl() {
        showProgressDialog()
        val fixedDepositTermsUrl = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/app/fixed-deposit-app-tcs.pdf?language=eforceDownload=false"
        binding.termsWebView.loadUrl("https://docs.google.com/viewer?embedded=true&url=$fixedDepositTermsUrl")
    }

    private fun networkCheck() {
        if (!NetworkUtils.isNetworkConnected()) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (networkRetryCounter < maxAllowedRetries) {
                    networkRetryCounter++
                    networkCheck()
                }
            }, 1000)
        } else {
            loadFromUrl()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        termsAndConditionsWebView = binding.termsWebView
        termsAndConditionsWebView?.settings?.javaScriptEnabled = true
        termsAndConditionsWebView?.webViewClient = PDFRendererClient()
    }

    private inner class PDFRendererClient : WebViewClient() {
        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            if (!detail.didCrash()) {
                if (termsAndConditionsWebView != null) {
                    binding.fixedDepositTermsContentView.removeView(termsAndConditionsWebView)
                    termsAndConditionsWebView?.destroy()
                    termsAndConditionsWebView = null
                }

                termsAndConditionsWebView = WebView(fixedDepositActivity)
                binding.fixedDepositTermsContentView.addView(termsAndConditionsWebView)
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            try {
                if (view.contentHeight == 0) {
                    view.reload()
                } else {
                    dismissProgressDialog()
                }
            } catch (e: IllegalStateException) {
                BMBLogger.e(javaClass.simpleName, e.message)
            }
        }
    }
}