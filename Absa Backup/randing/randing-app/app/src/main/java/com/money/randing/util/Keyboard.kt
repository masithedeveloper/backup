package com.money.randing.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.clearFocusAndCloseKeyboard() {
    val inputMethodManager = ContextCompat.getSystemService(
        this.context, InputMethodManager::class.java
    )
    this.clearFocus()
    inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.requestKeyboardFocus() {
    val inputMethodManager = ContextCompat.getSystemService(
        this.context, InputMethodManager::class.java
    )
    this.requestFocus()
    inputMethodManager?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}