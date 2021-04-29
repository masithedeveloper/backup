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
package com.barclays.absa.banking.framework.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.barclays.absa.banking.R;

@Deprecated
public class ContactDialogTouchListener implements OnTouchListener {
    private final int mDialogTitle;
    private final Context mContext;
    private final int mPositiveBtnText;
    private final int mNegativeBtnText;
    private final int mRequestCode;

    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    ((Activity) mContext).startActivityForResult(intent,
                            mRequestCode);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    public ContactDialogTouchListener(int mDialogTitle, Context mContext, int mPositiveBtnText, int mNegativeBtnText, int mRequestCode) {
        this.mDialogTitle = mDialogTitle;
        this.mContext = mContext;
        this.mPositiveBtnText = mPositiveBtnText;
        this.mNegativeBtnText = mNegativeBtnText;
        this.mRequestCode = mRequestCode;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            v.requestFocus();
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
            AlertDialog alertDialog = builder.setMessage(mContext.getString(mDialogTitle)).setPositiveButton(mContext.getString(mPositiveBtnText),
                    dialogClickListener).setNegativeButton(mContext.getString(mNegativeBtnText), dialogClickListener).create();

            alertDialog.show();
            AlertBox.setTitleBar(mContext, alertDialog);
            Window dialogWindow = alertDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        }
        return false;
    }
}