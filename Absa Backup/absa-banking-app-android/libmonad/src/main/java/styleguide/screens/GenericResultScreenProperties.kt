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
 */

package styleguide.screens

import android.graphics.drawable.Drawable
import java.io.Serializable

class GenericResultScreenProperties private constructor(propertiesBuilder: PropertiesBuilder, isSuccessfulTransaction: Boolean) : Serializable {
    constructor(propertiesBuilder: PropertiesBuilder) : this(propertiesBuilder, true)

    internal val title: String
    internal val description: String
    internal val primaryButtonLabel: String
    internal val secondaryButtonLabel: String
    internal val noteText: String
    internal val primaryButtonContentDescription: String
    internal val secondaryButtonContentDescription: String
    internal val resultScreenImage: Drawable?
    internal val resultScreenAnimation: String
    internal val isSuccessfulTransaction: Boolean
    internal val textToMakeBold: Array<String>
    internal val clickableText: String
    internal val shouldAnimateOnlyOnce: Boolean
    internal val contactViewContactName: String
    internal val contactViewContactNumber: String

    class PropertiesBuilder {
        internal var title: String? = ""
        internal var description: String? = ""
        internal var noteText: String? = ""
        internal var primaryButtonLabel: String? = ""
        internal var secondaryButtonLabel: String? = ""
        internal var primaryButtonContentDescription: String? = ""
        internal var secondaryButtonContentDescription: String? = ""
        internal var resultScreenImage: Drawable? = null
        internal var resultScreenAnimation: String? = ""
        internal var textToMakeBold: Array<String>? = emptyArray()
        internal var clickableText: String? = ""
        internal var shouldAnimateOnlyOnce = false
        internal var contactViewContactName = ""
        internal var contactViewContactNumber = ""

        @Deprecated("")
        fun setResultScreenImage(resultScreenImage: Drawable?): PropertiesBuilder {
            this.resultScreenImage = resultScreenImage
            return this
        }

        fun setClickableText(clickableText: String? = ""): PropertiesBuilder {
            this.clickableText = clickableText
            return this
        }

        fun setNoteText(noteText: String?): PropertiesBuilder {
            this.noteText = noteText
            return this
        }

        fun setResultScreenAnimation(resultScreenAnimation: String? = ""): PropertiesBuilder {
            this.resultScreenAnimation = resultScreenAnimation
            return this
        }

        fun setSecondaryButtonLabel(secondaryButtonLabel: String? = ""): PropertiesBuilder {
            this.secondaryButtonLabel = secondaryButtonLabel
            return this
        }

        fun setTitle(title: String?): PropertiesBuilder {
            this.title = title
            return this
        }

        fun setDescription(description: String? = ""): PropertiesBuilder {
            this.description = description
            return this
        }

        fun setDescription(description: String, textToMakeBold: Array<String>? = emptyArray()): PropertiesBuilder {
            this.description = description
            this.textToMakeBold = textToMakeBold
            return this
        }

        fun setPrimaryButtonLabel(primaryButtonLabel: String? = ""): PropertiesBuilder {
            this.primaryButtonLabel = primaryButtonLabel
            return this
        }

        fun setPrimaryButtonContentDescription(primaryButtonContentDescription: String? = ""): PropertiesBuilder {
            this.primaryButtonContentDescription = primaryButtonContentDescription
            return this
        }

        fun setSecondaryButtonContentDescription(secondaryButtonContentDescription: String? = ""): PropertiesBuilder {
            this.secondaryButtonContentDescription = secondaryButtonContentDescription
            return this
        }

        fun setShouldAnimateOnlyOnce(shouldAnimateOnlyOnce: Boolean): PropertiesBuilder {
            this.shouldAnimateOnlyOnce = shouldAnimateOnlyOnce
            return this
        }

        fun setContactViewContactName(contactViewContactName: String): PropertiesBuilder {
            this.contactViewContactName = contactViewContactName
            return this
        }

        fun setContactViewContactNumber(contactViewContactNumber: String): PropertiesBuilder {
            this.contactViewContactNumber = contactViewContactNumber
            return this
        }

        fun build(isSuccessfulTransaction: Boolean): GenericResultScreenProperties {
            return GenericResultScreenProperties(this, isSuccessfulTransaction)
        }
    }

    init {
        title = propertiesBuilder.title ?: ""
        description = propertiesBuilder.description ?: ""
        primaryButtonLabel = propertiesBuilder.primaryButtonLabel ?: ""
        secondaryButtonLabel = propertiesBuilder.secondaryButtonLabel ?: ""
        resultScreenImage = propertiesBuilder.resultScreenImage
        primaryButtonContentDescription = propertiesBuilder.primaryButtonContentDescription ?: ""
        secondaryButtonContentDescription = propertiesBuilder.secondaryButtonContentDescription ?: ""
        resultScreenAnimation = propertiesBuilder.resultScreenAnimation ?: ""
        noteText = propertiesBuilder.noteText ?: ""
        this.isSuccessfulTransaction = isSuccessfulTransaction
        clickableText = propertiesBuilder.clickableText ?: ""
        textToMakeBold = propertiesBuilder.textToMakeBold ?: emptyArray()
        shouldAnimateOnlyOnce = propertiesBuilder.shouldAnimateOnlyOnce
        contactViewContactName = propertiesBuilder.contactViewContactName
        contactViewContactNumber = propertiesBuilder.contactViewContactNumber
    }
}