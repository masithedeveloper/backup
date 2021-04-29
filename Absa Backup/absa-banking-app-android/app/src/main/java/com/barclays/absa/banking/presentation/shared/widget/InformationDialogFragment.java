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

package com.barclays.absa.banking.presentation.shared.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.FragmentInfoDialogBinding;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;

public class InformationDialogFragment extends DialogFragment {
    public static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DIMISS_BUTTON_TEXT = "dimiss_text";

    View.OnClickListener retryListener;
    View.OnClickListener okListener;

    private void setRetryListener(View.OnClickListener retryListener) {
        this.retryListener = retryListener;
    }

    private void setOkListener(View.OnClickListener okListener) {
        this.okListener = okListener;
    }

    public static InformationDialogFragment newInstance(@StringRes int titleStringId, @StringRes int messageStringId, @StringRes int dismissButtonStringId) {
        InformationDialogFragment fragment = new InformationDialogFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TITLE, titleStringId);
        args.putInt(KEY_MESSAGE, messageStringId);
        args.putInt(KEY_DIMISS_BUTTON_TEXT, dismissButtonStringId);
        fragment.setArguments(args);
        return fragment;
    }

    public static InformationDialogFragment newInstanceWithRetryCancel(@StringRes int titleStringId, @StringRes int messageStringId, View.OnClickListener retryListener) {
        InformationDialogFragment fragment = newInstance(titleStringId, messageStringId, 0);
        fragment.setRetryListener(retryListener);
        return fragment;
    }

    public static InformationDialogFragment newInstanceWithOk(@StringRes int titleStringId, @StringRes int messageStringId, View.OnClickListener okListener) {
        InformationDialogFragment fragment = newInstance(titleStringId, messageStringId, 0);
        fragment.setOkListener(okListener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getActivity() != null;
        FragmentInfoDialogBinding fragmentInfoDialogBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.fragment_info_dialog, null, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int title = arguments.getInt(KEY_TITLE);
            int message = arguments.getInt(KEY_MESSAGE);
            fragmentInfoDialogBinding.tvDialogTitle.setText(title);
            fragmentInfoDialogBinding.tvDialogMessage.setText(message);
        } else {
            fragmentInfoDialogBinding.tvDialogTitle.setText(R.string.error);
            fragmentInfoDialogBinding.tvDialogMessage.setText(getString(R.string.generic_error));
        }

        if (retryListener != null) {
            //Retry
            fragmentInfoDialogBinding.btnRetry.setText(getString(R.string.retry));
            fragmentInfoDialogBinding.btnRetry.setVisibility(View.VISIBLE);
            fragmentInfoDialogBinding.btnRetry.setOnClickListener(retryListener);
            // Cancel
            fragmentInfoDialogBinding.btnDismissButton.setText(getString(R.string.cancel));
            fragmentInfoDialogBinding.btnDismissButton.setOnClickListener(view -> dismiss());
        } else if (okListener != null) {
            // Ok
            fragmentInfoDialogBinding.btnRetry.setText(getString(R.string.ok));
            fragmentInfoDialogBinding.btnRetry.setVisibility(View.VISIBLE);
            fragmentInfoDialogBinding.btnRetry.setOnClickListener(okListener);
            // - hide this
            fragmentInfoDialogBinding.btnDismissButton.setVisibility(View.GONE);
        } else {
            // default
            fragmentInfoDialogBinding.btnDismissButton.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        fragmentInfoDialogBinding.btnDismissButton.setOnClickListener(view -> {
            if (retryListener == null) {
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(fragmentInfoDialogBinding.getRoot())
                .create();
    }

    public static void showDismissAlertDialog(Activity activity, String string_dismiss, String title,
                                              String message, DialogInterface.OnDismissListener dismissListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme)
                .setTitle(title).setMessage(message)
                .setCancelable(false)
                .setPositiveButton(string_dismiss, (dialog, which) -> dialog.dismiss());
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener);
        }
        builder.create().show();
    }
}
