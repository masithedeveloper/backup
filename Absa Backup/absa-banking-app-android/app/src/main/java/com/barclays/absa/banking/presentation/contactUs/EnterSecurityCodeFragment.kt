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
package com.barclays.absa.banking.presentation.contactUs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.EnterSecurityCodeFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.utils.IAbsaCacheService

class EnterSecurityCodeFragment : AbsaBaseFragment<EnterSecurityCodeFragmentBinding>() {

    companion object {
        private const val DESCRIPTION = "description"
        private const val SECRET_CODE = "secret code"

        fun newInstance(description: String?, secretCode: String?): EnterSecurityCodeFragment {
            return EnterSecurityCodeFragment().apply {
                arguments = Bundle().apply {
                    putString(DESCRIPTION, description)
                    putString(SECRET_CODE, secretCode)
                }
            }
        }
    }

    private lateinit var reportFraudView: ContactUsContracts.ReportFraudView
    private var description: String? = null
    private var secretCode: String? = null
    private var attemptCount = 3
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    override fun getLayoutResourceId(): Int = R.layout.enter_security_code_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportFraudView = activity as ContactUsContracts.ReportFraudView

        retrieveArguments()
        populateViews()
        initViews()
    }

    private fun retrieveArguments() {
        arguments?.apply {
            description = getString(DESCRIPTION)
            secretCode = getString(SECRET_CODE)
        }
    }

    private fun populateViews() {
        if (!description.isNullOrEmpty()) {
            binding.securityCodeDescription.setDescription(description)
        }
    }

    private fun initViews() {
        binding.securityCodeNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                binding.apply {
                    confirmButton.isEnabled = securityCodeNormalInputView.selectedValue.isNotEmpty()
                }
            }
        })

        binding.confirmButton.setOnClickListener { validateSecretCode(binding.securityCodeNormalInputView.selectedValue) }
    }

    private fun validateSecretCode(userCode: String) {
        attemptCount--
        if (secretCode == userCode && attemptCount > 0) {
            absaCacheService.setCallMeBackRequested(false)
            reportFraudView.let {
                it.verifyCallBack(userCode)
                it.clearSecretCode()
            }
        } else if (attemptCount > 0) {
            binding.securityCodeNormalInputView.setError(getString(R.string.incorrect_security_code))
        } else {
            absaCacheService.setCallMeBackRequested(false)
            reportFraudView.let {
                it.clearSecretCode()
                it.onBackPressed()
            }
            showIncorrectSecurityCodeFailureScreen()
        }
    }

    private fun showIncorrectSecurityCodeFailureScreen() {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { BMBApplication.getInstance().topMostActivity.finish() }
        startActivity(Intent(activity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.your_call_is_not_secure)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.your_call_is_not_secure_description)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, -1)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close)
        })
    }
}