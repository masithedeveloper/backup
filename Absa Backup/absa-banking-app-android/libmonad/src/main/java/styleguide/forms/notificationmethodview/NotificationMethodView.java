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

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import za.co.absa.presentation.uilib.R;

public class NotificationMethodView extends LinearLayout implements OnSelectionChangedListener {

    private AppCompatRadioButton noneRadioButton;

    private ContactRadioEditView emailRadioEditView;
    private ContactRadioEditView smsRadioEditView;
    private ContactRadioEditView faxRadioEditView;
    private TextView titleTextView;

    private NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;

    public enum NotificationType {
        SELF,
        BENEFICIARY
    }

    public NotificationMethodView(Context context) {
        super(context);
        init(context, null);
    }

    public NotificationMethodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.notification_method_view, this);

        wireUpComponents();
        setAttributes(context, attributeSet);
    }

    private void setAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.NotificationMethodView);

        String title = typedArray.getString(R.styleable.NotificationMethodView_attribute_title);
        setTitle(title);

        typedArray.recycle();
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setTitle(int titleResourceId) {
        titleTextView.setText(titleResourceId);
    }

    public void setTitleVisibility(int visibility) {
        titleTextView.setVisibility(visibility);
    }

    private void wireUpComponents() {
        setOrientation(VERTICAL);
        titleTextView = findViewById(R.id.titleTextView);
        noneRadioButton = findViewById(R.id.noneRadioButton);

        noneRadioButton.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.radio_button_view_button));

        emailRadioEditView = findViewById(R.id.emailRadioEditView);
        emailRadioEditView.getNormalInputView().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailRadioEditView.getNormalInputView().setIconImageViewDescription(getContext().getString(R.string.talkback_add_contact_icon));
        emailRadioEditView.getNormalInputView().setImageViewVisibility(View.VISIBLE);
        emailRadioEditView.setType(AbstractContactRadioEditView.TYPE.EMAIL);
        emailRadioEditView.setOnSelectionChangedListener(this);

        smsRadioEditView = findViewById(R.id.smsRadioEditView);
        smsRadioEditView.getNormalInputView().applyMask("### ### ####");
        smsRadioEditView.getNormalInputView().setInputType(InputType.TYPE_CLASS_PHONE);
        smsRadioEditView.getNormalInputView().setIconImageViewDescription(getContext().getString(R.string.talkback_add_contact_icon));
        smsRadioEditView.getNormalInputView().setImageViewVisibility(View.VISIBLE);
        smsRadioEditView.setType(AbstractContactRadioEditView.TYPE.SMS);
        smsRadioEditView.setOnSelectionChangedListener(this);

        faxRadioEditView = findViewById(R.id.faxRadioEditView);
        faxRadioEditView.getNormalInputView().applyMask("### ### ####");
        faxRadioEditView.getNormalInputView().setInputType(InputType.TYPE_CLASS_PHONE);
        faxRadioEditView.getNormalInputView().setIconImageViewDescription(getContext().getString(R.string.talkback_add_contact_icon));
        faxRadioEditView.getNormalInputView().setImageViewVisibility(View.VISIBLE);
        faxRadioEditView.setType(AbstractContactRadioEditView.TYPE.FAX);
        faxRadioEditView.setOnSelectionChangedListener(this);

        noneRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationMethodType = NotificationMethodData.TYPE.NONE;
                    emailRadioEditView.unCheck();
                    smsRadioEditView.unCheck();
                    faxRadioEditView.unCheck();
                }
            }
        });
    }

    public void setNotificationType(NotificationType notificationType) {
        emailRadioEditView.setNotificationType(notificationType);
        smsRadioEditView.setNotificationType(notificationType);
        faxRadioEditView.setNotificationType(notificationType);
    }

    public ContactRadioEditView getEmailRadioEditView() {
        return emailRadioEditView;
    }

    public ContactRadioEditView getSmsRadioEditView() {
        return smsRadioEditView;
    }

    public ContactRadioEditView getFaxRadioEditView() {
        return faxRadioEditView;
    }

    @Override
    public void selectionChanged(@NonNull ContactRadioEditView contactRadioEditView, boolean isChecked) {
        if (isChecked) {
            if (contactRadioEditView.equals(emailRadioEditView)) {
                notificationMethodType = NotificationMethodData.TYPE.EMAIL;
                noneRadioButton.setChecked(false);
                smsRadioEditView.unCheck();
                faxRadioEditView.unCheck();
            } else if (contactRadioEditView.equals(smsRadioEditView)) {
                notificationMethodType = NotificationMethodData.TYPE.SMS;
                noneRadioButton.setChecked(false);
                emailRadioEditView.unCheck();
                faxRadioEditView.unCheck();
            } else if (contactRadioEditView.equals(faxRadioEditView)) {
                notificationMethodType = NotificationMethodData.TYPE.FAX;
                noneRadioButton.setChecked(false);
                emailRadioEditView.unCheck();
                smsRadioEditView.unCheck();
            }
        }
    }

    public NotificationMethodData getContactDetailValue() {
        NotificationMethodData notificationMethodData = new NotificationMethodData();
        notificationMethodData.setNotificationMethodType(notificationMethodType);

        if (notificationMethodType == NotificationMethodData.TYPE.NONE) {
            notificationMethodData.setNotificationMethodDetail(getContext().getString(R.string.notification_method_none));
        } else if (notificationMethodType == NotificationMethodData.TYPE.EMAIL) {
            notificationMethodData.setNotificationMethodDetail(emailRadioEditView.getFormattedValue());
        } else if (notificationMethodType == NotificationMethodData.TYPE.SMS) {
            notificationMethodData.setNotificationMethodDetail(smsRadioEditView.getFormattedValue());
        } else if (notificationMethodType == NotificationMethodData.TYPE.FAX) {
            notificationMethodData.setNotificationMethodDetail(faxRadioEditView.getFormattedValue());
        }
        return notificationMethodData;
    }

    public NotificationMethodData.TYPE getNotificationMethodType() {
        return notificationMethodType;
    }

    public void setSelectedItem(String lastSelectedType, String lastSelectedValue) {
        noneRadioButton.setChecked(false);
        smsRadioEditView.unCheck();
        emailRadioEditView.unCheck();
        faxRadioEditView.unCheck();

        switch (lastSelectedType) {
            case "N":
                noneRadioButton.setChecked(true);
                break;
            case "E":
                emailRadioEditView.onRadioViewTapped();
                emailRadioEditView.getNormalInputView().setSelectedValue(lastSelectedValue);
                break;
            case "S":
                smsRadioEditView.onRadioViewTapped();
                smsRadioEditView.getNormalInputView().setSelectedValue(lastSelectedValue);
                break;
            case "F":
                faxRadioEditView.onRadioViewTapped();
                faxRadioEditView.getNormalInputView().setSelectedValue(lastSelectedValue);
                break;
        }
    }
    
    public void setMyNotificationDetails(String cellphoneNumber, List<String> email, String fax) {
        smsRadioEditView.getNormalInputView().setSelectedValue(cellphoneNumber);
        emailRadioEditView.getNormalInputView().setSelectedValue(email.get(0));
        faxRadioEditView.getNormalInputView().setSelectedValue(fax);
    }
}