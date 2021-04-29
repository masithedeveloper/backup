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

package styleguide.forms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import org.jetbrains.annotations.NotNull;

import styleguide.widgets.RoundedImageView;
import za.co.absa.presentation.uilib.R;

public class EditBeneficiaryInputView<T extends SelectorInterface> extends BaseInputView {

    private RoundedImageView beneficiaryImageView;

    public EditBeneficiaryInputView(Context context) {
        super(context);
        init(context, null);
    }

    public EditBeneficiaryInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditBeneficiaryInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void init(@NotNull Context context, AttributeSet attributes) {
        LayoutInflater.from(context).inflate(R.layout.edit_beneficiary_input_view, this);

        InputViewAttributes inputViewAttributes = new InputViewAttributes();
        inputViewAttributes.setTitleResourceId(R.styleable.EditBeneficiaryInputView_attribute_title_text);
        inputViewAttributes.setDescriptionId(R.styleable.EditBeneficiaryInputView_attribute_description_text);
        inputViewAttributes.setErrorTextId(R.styleable.EditBeneficiaryInputView_attribute_error_text);
        inputViewAttributes.setEditableId(R.styleable.EditBeneficiaryInputView_attribute_editable);
        inputViewAttributes.setHintResourceId(R.styleable.EditBeneficiaryInputView_attribute_hint_text);
        inputViewAttributes.setImageResourceId(R.styleable.EditBeneficiaryInputView_attribute_image);
        inputViewAttributes.setInputType(R.styleable.EditBeneficiaryInputView_android_inputType);
        inputViewAttributes.setMaxLength(R.styleable.EditBeneficiaryInputView_android_maxLength);
        inputViewAttributes.setSelectorViewType(R.styleable.EditBeneficiaryInputView_attribute_selector_type);
        inputViewAttributes.setAllowedCharacters(R.styleable.EditBeneficiaryInputView_android_digits);
        inputViewAttributes.setMaskId(R.styleable.EditBeneficiaryInputView_attribute_mask);

        super.init(context, attributes, R.styleable.EditBeneficiaryInputView, inputViewAttributes);

        initViews();
    }

    public void initViews() {
        super.initViews();
        beneficiaryImageView = findViewById(R.id.beneficiaryImageView);
    }

    public void setBeneficiaryImageViewOnClickListener(OnClickListener onClickListener) {
        beneficiaryImageView.setOnClickListener(onClickListener);
    }

    public void setBeneficiaryImage(Bitmap beneficiaryImage) {
        if (beneficiaryImageView != null) {
            beneficiaryImageView.setImageBitmap(beneficiaryImage);
        }
    }

    public void setBeneficiaryImage(Drawable beneficiaryImage) {
        if (beneficiaryImageView != null) {
            beneficiaryImageView.setImageDrawable(beneficiaryImage);
        }
    }

    public RoundedImageView getBeneficiaryImageView() {
        return beneficiaryImageView;
    }
}