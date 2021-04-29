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

package com.barclays.absa.banking.avaf.documentRequest.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.AvafDocumentRequestBaseFragmentBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.validation.*
import java.util.*

abstract class AvafDocumentRequestBaseFragment : BaseFragment(R.layout.avaf_document_request_base_fragment) {
    private val binding by viewBinding(AvafDocumentRequestBaseFragmentBinding::bind)

    abstract var toolbarTitleResId: Int
    abstract val analyticsScreen: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackScreen()
        showToolBar()
        setToolBar(toolbarTitleResId)
        initViews()
        setupListeners()
        setupValidationRules()
    }

    abstract fun initViews()

    abstract fun setupListeners()

    private fun trackScreen() {
        if (analyticsScreen.isNotBlank()) {
            AnalyticsUtil.trackAction(analyticsScreen)
        }
    }

    private fun setupValidationRules() {
        binding.emailNormalInputView.addValidationRules(
                FieldRequiredValidationRule(R.string.invalid_email_address),
                EmailValidationRule(R.string.invalid_email_address)
        )

        binding.documentDateNormalInputView.addValidationRule(FieldRequiredWhenVisibleValidationRule(R.string.avaf_document_request_select_date))
    }

    fun getDate(): Date {
        val now = Calendar.getInstance(BMBApplication.getApplicationLocale()).time

        return if (binding.documentDateNormalInputView.visibility != View.VISIBLE) {
            now
        } else {
            binding.documentDateNormalInputView.tag as? Date ?: now
        }
    }

    fun getEmail() = binding.emailNormalInputView.selectedValue.trim()

    fun hasValidationErrors(): Boolean = !binding.documentDateNormalInputView.validate() || !binding.emailNormalInputView.validate()
}