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
package styleguide.cards;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;

import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class ArticleCardView extends ConstraintLayout {

    private ImageView leftImageView;
    private ImageView rightImageView;
    private TextView contentTextView;
    private String description = "";
    private int imageResource;
    private boolean isLeftAligned;

    public ArticleCardView(Context context) {
        super(context);
        init(context, null);
    }

    public ArticleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArticleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.article_card_view, this);
        wireUpComponents();

        if (attrs != null) {
            TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ArticleCardView);
            description = styledAttributes.getString(R.styleable.ArticleCardView_attribute_image_content_text);
            imageResource = styledAttributes.getResourceId(R.styleable.ArticleCardView_attribute_image_resource, -1);
            isLeftAligned = styledAttributes.getBoolean(R.styleable.ArticleCardView_attribute_is_left_aligned, true);
            styledAttributes.recycle();
        }

        contentTextView.setText(description);
        if (isLeftAligned) {
            leftImageView.setImageResource(imageResource);
            ImageViewCompat.setImageTintList(leftImageView, null);
            rightImageView.setVisibility(GONE);
        } else {
            rightImageView.setImageResource(imageResource);
            ImageViewCompat.setImageTintList(rightImageView, null);
            leftImageView.setVisibility(GONE);
        }
    }

    public void setDescription(String description) {
        contentTextView.setText(description);
    }

    private void wireUpComponents() {
        leftImageView = findViewById(R.id.leftImageView);
        rightImageView = findViewById(R.id.rightImageView);
        contentTextView = findViewById(R.id.containerText);
    }
}
