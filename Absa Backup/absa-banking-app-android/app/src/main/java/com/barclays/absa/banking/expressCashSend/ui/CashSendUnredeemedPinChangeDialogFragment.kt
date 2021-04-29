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
package com.barclays.absa.banking.expressCashSend.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UnredeemPinChangeDialogfragmentBinding
import com.barclays.absa.banking.framework.BaseDialogFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.KeyboardUtils.hideSoftKeyboard
import com.barclays.absa.utils.ListenerUtil
import com.barclays.absa.utils.ListenerUtil.getFragment
import com.barclays.absa.utils.ValidationUtils

class CashSendUnredeemedPinChangeDialogFragment : BaseDialogFragment(), TextView.OnEditorActionListener {
    private lateinit var binding: UnredeemPinChangeDialogfragmentBinding
    private var sendSMS: Boolean = false

    companion object {
        private const val SEND_SMS = "sendSMS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = UnredeemPinChangeDialogfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendSMS = requireArguments().getBoolean(SEND_SMS)
        dialog?.setTitle(getString(R.string.change_access_pin_title))
        binding.unredeemedPinDialogHeaderTextView.requestFocus()
        binding.unredeemedPinDialogPositiveTextView.setOnClickListener { validateInput() }
        binding.unredeemedPinDialogNegativeTextView.setOnClickListener {
            hideSoftKeyboard(this@CashSendUnredeemedPinChangeDialogFragment.view)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            dialog?.dismiss()
        }
        binding.unredeemedPinDialogNormalInputView.setValueViewEditorActionListener(this)
    }

    private fun validateInput(): Boolean {
        return if (ValidationUtils.validateATMPin(binding.unredeemedPinDialogNormalInputView, getString(R.string.unredeemed_atm_access_pin), BMBConstants.ATM_ACCESS_PIN_LENGTH)) {
            val pinChangeInterface = ListenerUtil.getListener(getFragment(CashSendUnredeemedDetailsFragment::class.java), PinChangeInterface::class.java)
            pinChangeInterface?.pinChanged(binding.unredeemedPinDialogNormalInputView.selectedValue)
            dismiss()
            true
        } else {
            binding.unredeemedPinDialogNormalInputView.errorTextView?.text = resources.getString(R.string.calcHelp)
            false
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (validateInput()) {
            return true
        }
        return false
    }

    interface PinChangeInterface {
        fun pinChanged(newPin: String)
    }
}