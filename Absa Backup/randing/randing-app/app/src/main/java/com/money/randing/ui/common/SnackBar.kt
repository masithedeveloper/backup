package com.money.randing.ui.common

import androidx.fragment.app.Fragment
import com.money.randing.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackBar(text: String) {
    Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show()
}

fun Fragment.showUnexpectedFailureSnackBar() {
    showSnackBar(getString(R.string.unexpected_error_message))
}