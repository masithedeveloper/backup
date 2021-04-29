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
import styleguide.utils.ValidationUtils

/*
 * Before invoking `validate()`, be sure to add `BaseFieldValidationRule` implementations
 * using addValidationRules() or addValidationRule() extension functions
 */

abstract class BaseFieldValidationRule(@StringRes errorMessageStringId: Int = -1, errorMessage: String = "") : ValidationRule(errorMessageStringId, errorMessage) {
    override fun execute() = validate()
}

open class FieldRequiredValidationRule(@StringRes errorMessageStringId: Int = -1, errorMessage: String = "") : BaseFieldValidationRule(errorMessageStringId, errorMessage) {
    override fun validate(): Boolean {
        return field.selectedValue.isNotBlank()
    }
}

open class LengthValidationRule(val length: Int, private val isMaxLengthValidation: Boolean, errorMessageStringId: Int) : BaseFieldValidationRule(errorMessageStringId) {
    override fun validate(): Boolean {
        return if (isMaxLengthValidation) {
            field.selectedValue.length <= length
        } else {
            field.selectedValue.length >= length
        }
    }
}

class MaximumLengthValidationRule(length: Int, errorMessageStringId: Int) : LengthValidationRule(length, true, errorMessageStringId)

class MinimumLengthValidationRule(length: Int, errorMessageStringId: Int) : LengthValidationRule(length, false, errorMessageStringId)

class EmailValidationRule(errorMessageStringId: Int) : BaseFieldValidationRule(errorMessageStringId) {
    override fun validate(): Boolean {
        return ValidationUtils.isValidEmailAddress(field.selectedValue)
    }
}

class FieldRequiredWhenVisibleValidationRule(@StringRes errorMessageStringId: Int = -1, errorMessage: String = "") : FieldRequiredValidationRule(errorMessageStringId, errorMessage) {
    override fun validate(): Boolean {
        val isBaseRuleValid = super.validate()
        return (field.visibility != View.VISIBLE) || (field.visibility == View.VISIBLE && isBaseRuleValid)
    }
}

class CellphoneNumberValidationRule(errorMessageStringId: Int) : BaseFieldValidationRule(errorMessageStringId) {
    override fun validate(): Boolean {
        return (field.visibility != View.VISIBLE) || (field.visibility == View.VISIBLE && field.selectedValueUnmasked.matches(Regex("^0[6-8][0-9]{8}", mutableSetOf(RegexOption.IGNORE_CASE))))
    }
}

class LandLineValidationRule(errorMessageStringId: Int) : BaseFieldValidationRule(errorMessageStringId) {
    override fun validate(): Boolean {
        return field.selectedValueUnmasked.matches(Regex("^0[1-5][0-9]{8}|^08[7]\\d{7}", mutableSetOf(RegexOption.IGNORE_CASE)))
    }
}

class CellphoneAndLandlineNumberValidationRule(errorMessageStringId: Int) : BaseFieldValidationRule(errorMessageStringId) {
    override fun validate(): Boolean {
        return field.selectedValueUnmasked.matches(Regex("^0[6-8][0-9]{8}|^0[1-5][0-9]{8}", mutableSetOf(RegexOption.IGNORE_CASE)))
    }
}

class SouthAfricaCellphoneNumberValidation(errorMessageStringId: Int) : BaseFieldValidationRule(errorMessageStringId) {
    override fun validate(): Boolean {
        return field.selectedValue.matches(Regex("^(0)[6-8][0-9]\\s[0-9]{3}\\s[0-9]{4}", mutableSetOf(RegexOption.IGNORE_CASE)))
    }
}

class PhoneNumberInputValidationRule(errorMessageStringId: Int, phoneNumber: String) : BaseFieldValidationRule(errorMessageStringId, phoneNumber) {
    override fun validate(): Boolean {
        return field.selectedValueUnmasked.length == 10 && field.selectedValueUnmasked.startsWith("0")
    }
}

class MinimumAmountValidationRule(private val minimumAmount: Double, private val isShowingDescription: Boolean, @StringRes errorMessageStringId: Int = -1, errorMessage: String = "") : BaseFieldValidationRule(errorMessageStringId, errorMessage) {
    override fun validate(): Boolean {
        return if (field.selectedValueUnmasked.toDouble() >= minimumAmount) {
            field.showDescription(isShowingDescription)
            true
        } else {
            false
        }
    }
}

class MaximumAmountValidationRule(private val maximumAmount: Double, private val isShowingDescription: Boolean, @StringRes errorMessageStringId: Int = -1, errorMessage: String = "") : BaseFieldValidationRule(errorMessageStringId, errorMessage) {
    override fun validate(): Boolean {
        return if (field.selectedValueUnmasked.toDouble() <= maximumAmount) {
            field.showDescription(isShowingDescription)
            true
        } else {
            false
        }
    }
}

class RemainderValueValidationRule(private val remainder: Int, private val isShowingDescription: Boolean, @StringRes errorMessageStringId: Int = -1, errorMessage: String = "") : BaseFieldValidationRule(errorMessageStringId, errorMessage) {
    override fun validate(): Boolean {
        return if (field.selectedValueUnmasked.toInt() % remainder == 0) {
            field.showDescription(isShowingDescription)
            true
        } else {
            false
        }
    }
}