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
 */

package styleguide.content

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import za.co.absa.presentation.uilib.R

class PrimaryContentAndLabelWithImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseContentAndLabelView(context, attrs, defStyleAttr) {
    var secondaryImageView: ImageView

    init {
        inflate(context, R.layout.primary_content_and_label_with_image_view, this)
        contentTextView = findViewById(R.id.contentTextView)
        labelTextView = findViewById(R.id.labelTextView)
        secondaryImageView = findViewById(R.id.rightHandSideSecondaryImageView)

        context.obtainStyledAttributes(R.styleable.PrimaryContentAndLabelWithImageView).apply {
            val image = getInt(R.styleable.PrimaryContentAndLabelWithImageView_attribute_secondary_image, R.drawable.ic_arrow_right_dark)
            val imageVisibility = getBoolean(R.styleable.PrimaryContentAndLabelWithImageView_attribute_secondary_image_visible, false)
            val primaryContentText = getString(R.styleable.PrimaryContentAndLabelWithImageView_attribute_primary_with_image_content)
            val primaryLabelText = getString(R.styleable.PrimaryContentAndLabelWithImageView_attribute_primary_with_image_label)
            val shouldDisableClickAnimations = getBoolean(R.styleable.PrimaryContentAndLabelWithImageView_attribute_disable_click_animations, false)
            recycle()

            setImage(image)
            if (imageVisibility) {
                showSecondaryImage()
            } else {
                hideSecondaryImage()
            }

            primaryContentText?.let { setContentText(it) }
            primaryLabelText?.let { setLabelText(it) }

            if (shouldDisableClickAnimations) {
                disableClickAnimation()
            } else {
                defaultClickAnimation()
            }
        }
    }

    private fun defaultClickAnimation() {
        val outValue = TypedValue()
        context.theme?.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
        isClickable = true
    }

    fun disableClickAnimation() {
        isClickable = false
    }

    fun showSecondaryImage() {
        secondaryImageView.visibility = View.VISIBLE
    }

    fun hideSecondaryImage() {
        secondaryImageView.visibility = View.GONE
    }

    fun setImage(@DrawableRes image: Int) {
        secondaryImageView.setImageResource(image)
    }
}