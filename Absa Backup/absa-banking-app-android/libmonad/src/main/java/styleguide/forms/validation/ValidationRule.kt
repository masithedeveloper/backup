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

import androidx.annotation.StringRes
import styleguide.forms.SelectorView

abstract class ValidationRule(@StringRes var errorMessageStringId: Int = -1, var errorMessage: String = "") {

    open lateinit var field: SelectorView<*>

    init {
        if (errorMessageStringId != -1 && errorMessage.isNotEmpty() || errorMessageStringId == -1 && errorMessage.isEmpty()) {
            throw IllegalStateException("You need to set EXACTLY one error message argument")
        }
    }

    abstract fun execute(): Boolean

    open fun validate() = true

    val showErrorMessageBlock: () -> Unit = {
        if (::field.isInitialized) {
            if (errorMessage.isNotEmpty()) {
                field.setError(errorMessage)
            } else {
                field.setError(field.context.getString(errorMessageStringId))
            }
        }
    }
}