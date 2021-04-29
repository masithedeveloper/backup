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

package styleguide.forms.notificationmethodview;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import styleguide.forms.NormalInputView;
import za.co.absa.presentation.uilib.R;

public class ContactRadioEditView extends ConstraintLayout implements AbstractContactRadioEditView {

    public static final int PICKER_REQUEST_CODE = 3000;
    public static final int PICKER_REQUEST_CODE_SELF = 3001;

    protected TYPE type;
    protected AppCompatRadioButton radioButtonView;

    protected NormalInputView normalInputView;
    private boolean isErrorMessageVisible;
    private boolean isRequired;
    private boolean isChecked;
    private NotificationMethodView.NotificationType notificationType;

    private OnSelectionChangedListener onSelectionChangedListener;

    public ContactRadioEditView(Context context) {
        super(context);
        init(context, null);
    }

    public ContactRadioEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactRadioEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.abstract_radio_edit_view, this);
        wireUpComponents();
        setAttributes(context, attrs);
        setLayoutTransition(new LayoutTransition());
    }

    private void setAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContactRadioEditView);
        String text = typedArray.getString(R.styleable.ContactRadioEditView_android_text);
        String hint = typedArray.getString(R.styleable.ContactRadioEditView_android_hint);
        isErrorMessageVisible = typedArray.getBoolean(R.styleable.ContactRadioEditView_attribute_is_error_visible, isErrorMessageVisible);
        isRequired = typedArray.getBoolean(R.styleable.ContactRadioEditView_attribute_is_required, isRequired);

        setText(text);
        setHint(hint);
        radioButtonView.setButtonDrawable(R.drawable.radio_button_view_button);
        typedArray.recycle();
    }

    private void setText(String text) {
        radioButtonView.setText(text);
    }

    private void setHint(String hint) {
        normalInputView.setHintText(hint);
    }

    private void wireUpComponents() {
        radioButtonView = findViewById(R.id.radioButton);
        normalInputView = findViewById(R.id.inputView);

        radioButtonView.setOnClickListener(v -> onRadioViewTapped());

        normalInputView.setImageViewOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPrompt(getContext(), "Would you like to select a contact in the address book?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(Intent.ACTION_PICK);

                                if (type == TYPE.EMAIL) {
                                    intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
                                    if (notificationType == NotificationMethodView.NotificationType.BENEFICIARY) {
                                        ((Activity) getContext()).startActivityForResult(intent, PICKER_REQUEST_CODE);
                                    } else {
                                        ((Activity) getContext()).startActivityForResult(intent, PICKER_REQUEST_CODE_SELF);
                                    }
                                } else if (type == TYPE.SMS || type == TYPE.FAX) {
                                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                                    if (notificationType == NotificationMethodView.NotificationType.BENEFICIARY) {
                                        ((Activity) getContext()).startActivityForResult(intent, PICKER_REQUEST_CODE);
                                    } else {
                                        ((Activity) getContext()).startActivityForResult(intent, PICKER_REQUEST_CODE_SELF);
                                    }
                                }

                                dialog.dismiss();
                            }
                        },
                        (dialog, which) -> dialog.dismiss()
                );
            }
        });
    }

    public void setNotificationType(NotificationMethodView.NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    private static void showDialogPrompt(Context context, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton("No", negativeListener)
                .setPositiveButton("Yes", positiveListener)
                .create()
                .show();
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener checkedChangeListener) {
        onSelectionChangedListener = checkedChangeListener;
    }

    @Override
    public void unCheck() {
        if (notificationType == NotificationMethodView.NotificationType.BENEFICIARY) {
            getNormalInputView().clear();
        }
        getNormalInputView().hideError();
        setChecked(false);
        hideInputView();
    }

    @Override
    public void onRadioViewTapped() {
        setChecked(true);
        normalInputView.setVisibility(VISIBLE);
        normalInputView.requestFocus();
        if (onSelectionChangedListener != null) {
            onSelectionChangedListener.selectionChanged(this, isChecked);
        }
    }

    @Override
    public void hideInputView() {
        normalInputView.setVisibility(GONE);
    }

    @Override
    public String getValue() {
        return normalInputView.getSelectedValue();
    }

    @Override
    public String getFormattedValue() {
        return normalInputView.getSelectedValue();
    }

    @Override
    public void setType(TYPE type) {
        this.type = type;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        radioButtonView.setChecked(checked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public NormalInputView getNormalInputView() {
        return normalInputView;
    }
}
