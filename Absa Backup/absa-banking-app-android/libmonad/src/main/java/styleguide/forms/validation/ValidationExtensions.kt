@file:JvmName("ValidationExtensions")

/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package styleguide.forms.validation

import android.view.View
import androidx.annotation.StringRes
import styleguide.forms.BaseInputView
import styleguide.forms.Form
import styleguide.forms.SelectorView

fun SelectorView<*>.validate(vararg validationRules: ValidationRule): Boolean {
    val validations = validationRules.toList()
    validations.forEach {
        it.field = this
    }
    return validate(validations)
}

fun SelectorView<*>.validate(validationRules: List<ValidationRule>): Boolean {
    var isValid = true
    run forEachBreak@{
        validationRules.forEach {
            isValid = it.execute()
            if (isValid && errorTextView?.visibility == View.VISIBLE) {
                clearError()
            } else if (!isValid) {
                if (it.errorMessage.isEmpty()) {
                    setError(it.errorMessageStringId)
                } else {
                    setError(it.errorMessage)
                }
                return@forEachBreak
            }
        }
    }
    return isValid
}

fun BaseInputView<*>.addRequiredValidationHidingTextWatcher() {
    addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(this))
}

fun BaseInputView<*>.addRequiredValidationHidingTextWatcher(changeCompletionListener: (String) -> Unit) {
    addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(this, changeCompletionListener))
}

fun SelectorView<*>.addValidationRule(validationRule: ValidationRule) {
    validationRule.field = this
    validators.add(validationRule)
}

fun BaseInputView<*>.addRequiredValidationHidingTextWatcher(@StringRes error: Int) {
    addValidationRule(FieldRequiredValidationRule(error))
    addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(this))
}

fun SelectorView<*>.addValidationRules(vararg validationRules: ValidationRule) {
    validationRules.forEach {
        it.field = this
        validators.add(it)
    }
}

/**
 * Use this option if you need to validate only specific form fields:
 * val form = Form()
 * someInputView.addToForm(form)
 * ...etc
 * form.validate()
 **/
fun SelectorView<*>.addToForm(form: Form) {
    form.addField(this)
}