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

package styleguide.utils

import android.content.res.Configuration
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import styleguide.content.DescriptionView
import styleguide.utils.extensions.isDarkMode
import za.co.absa.presentation.uilib.R

object TextFormattingUtils {

    var spannableStringBuilder: SpannableStringBuilder? = null

    fun makeTextClickable(textView: TextView, colorId: Int, fullText: String, clickableText: String, clickableTextAction: ClickableSpan) {
        if (fullText.contains(clickableText)) {
            val startIndex = fullText.indexOf(clickableText)
            val endIndex = startIndex + clickableText.length
            val darkMode = textView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(textView.context, if (darkMode) R.color.white else colorId))

            if (spannableStringBuilder == null) {
                spannableStringBuilder = SpannableStringBuilder(fullText)
            }
            spannableStringBuilder?.apply {
                setSpan(clickableTextAction, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(UnderlineSpan(), startIndex, endIndex, 0)
                setSpan(foregroundColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            textView.apply {
                text = spannableStringBuilder
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    fun makeMultipleTextBold(originalString: String, textToBold: Array<String>, descriptionView: DescriptionView) {
        val resultString = SpannableStringBuilder(originalString)
        for (i in textToBold.indices) {
            val indexOfString = originalString.toLowerCase().indexOf(textToBold[i].toLowerCase())
            if (indexOfString > -1) {
                resultString.setSpan(StyleSpan(Typeface.BOLD), indexOfString, indexOfString + textToBold[i].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        descriptionView.setDescription(resultString)
    }

    fun formatToBulletList(text: String, character: String): SpannableStringBuilder {
        val lines = TextUtils.split(text, character)
        val spannableStringBuilder = SpannableStringBuilder()

        lines.forEachIndexed { index, line ->
            run {
                val length = spannableStringBuilder.length
                spannableStringBuilder.append(line)
                if (index != lines.lastIndex) {
                    spannableStringBuilder.append(character)
                } else if (line.isEmpty()) {
                    spannableStringBuilder.append("\u200B")
                }
                spannableStringBuilder.setSpan(BulletSpan(30), length, length + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
        return spannableStringBuilder
    }

    fun changeTextColour(textView: TextView, colorId: Int, fullText: String, textToHighlight: String) {
        if (fullText.contains(textToHighlight)) {
            val startIndex = fullText.indexOf(textToHighlight)
            val endIndex = startIndex + textToHighlight.length
            val darkMode = textView.context.isDarkMode()
            val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(textView.context, if (darkMode) R.color.white else colorId))

            if (spannableStringBuilder == null) {
                spannableStringBuilder = SpannableStringBuilder(fullText)
            }

            spannableStringBuilder?.apply {
                setSpan(textToHighlight, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(foregroundColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            textView.apply {
                text = spannableStringBuilder
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    //Due to the wonderful way objects work, please set spannableStringBuilder back to null after use to ensure it doesn't highlight random text
    fun clearSpannableStringBuilder() {
        spannableStringBuilder = null
    }
}