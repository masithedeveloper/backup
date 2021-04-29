/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.KeyboardUtils
import com.barclays.absa.utils.PermissionHelper

class ContactDialogOptionListener(private val editText: EditText?, private val dialogTitle: Int, private val context: Context, private val requestCode: Int, private val fragment: Fragment?) : OnTouchListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_UP == event.action) {
            showYesNoDialog(AlertDialogProperties.Builder()
                    .message(context.getString(dialogTitle))
                    .positiveDismissListener { _, _ ->
                        dismissAlertDialog()
                        PermissionHelper.requestContactsReadPermission(context as Activity) {
                            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                            editText?.error = null
                            editText?.requestFocus()
                            if (fragment == null) {
                                context.startActivityForResult(intent, requestCode)
                            } else {
                                fragment.startActivityForResult(intent, requestCode)
                            }
                            KeyboardUtils.hideKeyboard(context)
                        }
                    }
                    .negativeDismissListener { _, _ ->
                        dismissAlertDialog()
                        editText?.let { setKeyboard(it) }
                    })
        }
        return false
    }

    private fun setKeyboard(view: View) {
        view.postDelayed({
            view.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

}