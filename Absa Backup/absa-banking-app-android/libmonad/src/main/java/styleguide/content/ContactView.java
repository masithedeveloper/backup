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

package styleguide.content;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import za.co.absa.presentation.uilib.R;

public class ContactView extends ConstraintLayout implements View.OnClickListener {

    private TextView contactNameTextView;
    private TextView contactNumberTextView;
    private ImageView iconImageView;

    public ContactView(Context context) {
        super(context);
        init(context, null);
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        LayoutInflater.from(context).inflate(R.layout.contact_view, this);

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.ContactView);
        String contactName = typedArray.getString(R.styleable.ContactView_attribute_contact_name);
        String contactNumber = typedArray.getString(R.styleable.ContactView_attribute_contact_number);
        int iconResourceId = typedArray.getResourceId(R.styleable.ContactView_attribute_icon, -1);

        typedArray.recycle();
        wireUpComponents();

        if (iconResourceId != -1) {
            setIcon(iconResourceId);
        }

        if (contactName != null && contactNumber != null) {
            setContact(contactName, contactNumber);
        }
    }

    private void wireUpComponents() {
        contactNameTextView = findViewById(R.id.contact_name_text_view);
        contactNumberTextView = findViewById(R.id.contact_number_text_view);
        iconImageView = findViewById(R.id.card_icon_view);

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        this.setBackgroundResource(outValue.resourceId);

        setOnClickListener(this);
    }

    public void setContact(Contact contact) {
        contactNameTextView.setText(contact.getContactName());
        contactNumberTextView.setText(contact.getContactNumber());
    }

    public void setContact(String name, String number) {
        contactNameTextView.setText(name);
        contactNumberTextView.setText(number);
    }

    public void setIcon(int resId) {
        iconImageView.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        String phoneData = "tel:" + contactNumberTextView.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneData));
        getContext().startActivity(intent);
    }
}
