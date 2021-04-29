/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package styleguide.utils.extensions

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import za.co.absa.presentation.uilib.R

typealias ClickableFunction = () -> Unit

fun TextView.clickablePhrase(@StringRes fullTextId: Int, @StringRes clickablePhraseId: Int, @ColorRes colorId: Int = R.color.graphite, onPhraseClick: ClickableFunction) {
    val clickablePhrase = context.getString(clickablePhraseId)
    val fullText = context.getString(fullTextId)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onPhraseClick()
        }
    }
    val color = ContextCompat.getColor(context, if (context.isDarkMode()) R.color.white else colorId)
    val clickablePhraseStartIndex = fullText.indexOf(clickablePhrase)
    val clickablePhraseEndIndex = clickablePhraseStartIndex + clickablePhrase.length

    if (clickablePhraseStartIndex > -1) {
        text = SpannableString(fullText).apply {
            setSpan(clickableSpan, clickablePhraseStartIndex, clickablePhraseEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) //make the selected phrase clickable
            setSpan(UnderlineSpan(), clickablePhraseStartIndex, clickablePhraseEndIndex, 0) // underlines the text
            setSpan(ForegroundColorSpan(color), clickablePhraseStartIndex, clickablePhraseEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // set the foreground color
        }
        movementMethod = LinkMovementMethod.getInstance()
    }
}

fun TextView.clickablePhrase(fullText: String, clickablePhraseId: Int, @ColorRes colorId: Int = R.color.graphite, onPhraseClick: ClickableFunction) {
    val clickablePhrase = context.getString(clickablePhraseId)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onPhraseClick()
        }
    }
    val color = ContextCompat.getColor(context, if (context.isDarkMode()) R.color.white else colorId)
    val clickablePhraseStartIndex = fullText.indexOf(clickablePhrase)
    val clickablePhraseEndIndex = clickablePhraseStartIndex + clickablePhrase.length

    if (clickablePhraseStartIndex > -1) {
        text = SpannableString(fullText).apply {
            setSpan(clickableSpan, clickablePhraseStartIndex, clickablePhraseEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) //make the selected phrase clickable
            setSpan(UnderlineSpan(), clickablePhraseStartIndex, clickablePhraseEndIndex, 0) // underlines the text
            setSpan(ForegroundColorSpan(color), clickablePhraseStartIndex, clickablePhraseEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // set the foreground color
        }
        movementMethod = LinkMovementMethod.getInstance()
    }
}

fun TextView.clickablePhrase(fullText: String, clickablePhraseText: String, @ColorRes colorId: Int = R.color.graphite, onPhraseClick: ClickableFunction) {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onPhraseClick()
        }
    }
    val color = ContextCompat.getColor(context, if (context.isDarkMode()) R.color.white else colorId)
    val clickablePhraseStartIndex = fullText.indexOf(clickablePhraseText)
    val clickablePhraseEndIndex = clickablePhraseStartIndex + clickablePhraseText.length

    if (clickablePhraseStartIndex > -1) {
        text = SpannableString(fullText).apply {
            setSpan(clickableSpan, clickablePhraseStartIndex, clickablePhraseEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) //make the selected phrase clickable
            setSpan(UnderlineSpan(), clickablePhraseStartIndex, clickablePhraseEndIndex, 0) // underlines the text
            setSpan(ForegroundColorSpan(color), clickablePhraseStartIndex, clickablePhraseEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // set the foreground color
        }
        movementMethod = LinkMovementMethod.getInstance()
    }
}

fun TextView.clickablePhrases(@StringRes fullTextId: Int, @StringRes clickablePhraseIds: IntArray, @ColorRes colorId: Int = R.color.graphite, onPhraseClick: Array<ClickableFunction>) {
    val fullText = context.getString(fullTextId)
    val color = ContextCompat.getColor(context, if (context.isDarkMode()) R.color.white else colorId)
    val spannableString = SpannableString(fullText)

    clickablePhraseIds.forEachIndexed { index, id ->
        val clickablePhrase = context.getString(id)
        val clickablePhraseStartIndex = fullText.indexOf(clickablePhrase)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onPhraseClick[index]()
            }
        }
        val clickablePhraseEndIndex = clickablePhraseStartIndex + clickablePhrase.length
        if (clickablePhraseStartIndex > -1) {
            spannableString.apply {
                setSpan(clickableSpan, clickablePhraseStartIndex, clickablePhraseEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) //make the selected phrase clickable
                setSpan(UnderlineSpan(), clickablePhraseStartIndex, clickablePhraseEndIndex, 0) // underlines the text
                setSpan(ForegroundColorSpan(color), clickablePhraseStartIndex, clickablePhraseEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // set the foreground color
            }
        }
    }
    text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
}