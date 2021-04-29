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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import kotlinx.android.synthetic.main.generic_results_screen_fragment.*
import styleguide.utils.TextFormattingUtils.makeMultipleTextBold
import styleguide.utils.extensions.ClickableFunction
import styleguide.utils.extensions.clickablePhrase
import za.co.absa.presentation.uilib.R

class GenericResultScreenFragment : Fragment(R.layout.generic_results_screen_fragment) {
    private lateinit var genericResultsScreenProperties: GenericResultScreenProperties
    private var shouldAnimateOnlyOnce = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = super.onCreateView(inflater, container, savedInstanceState)?.apply {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            genericResultsScreenProperties = it.getSerializable(GENERIC_RESULT_PROPERTIES_KEY) as GenericResultScreenProperties
            shouldAnimateOnlyOnce = it.getBoolean(SHOULD_ONLY_ANIMATE_ONCE)
        }

        populateComponents()
        setUpComponentListeners()

        val clickableText = genericResultsScreenProperties.clickableText
        if (clickableText.isNotEmpty()) {
            contentDescriptionView.descriptionTextView.clickablePhrase(genericResultsScreenProperties.description, clickableText, R.color.link_color, telephoneNumberClick())
        }
    }

    private fun telephoneNumberClick(): ClickableFunction {
        return object : ClickableFunction {
            override fun invoke() {
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:" + genericResultsScreenProperties.clickableText)
                    startActivity(this)
                }
            }
        }
    }

    private fun populateComponents() {
        shouldAnimateOnlyOnce = genericResultsScreenProperties.shouldAnimateOnlyOnce
        if (genericResultsScreenProperties.noteText.isNotEmpty()) {
            noteDescriptionView.setDescription(genericResultsScreenProperties.noteText)
            noteDescriptionView.visibility = View.VISIBLE
        }
        if (genericResultsScreenProperties.description.isNotEmpty()) {
            contentDescriptionView.setDescription(genericResultsScreenProperties.description)
        }
        if (genericResultsScreenProperties.textToMakeBold.isNotEmpty()) {
            makeMultipleTextBold(genericResultsScreenProperties.description, genericResultsScreenProperties.textToMakeBold, contentDescriptionView)
        }
        if (genericResultsScreenProperties.title.isNotEmpty()) {
            titleCenteredTitleView.setTitle(genericResultsScreenProperties.title)
        }
        if (genericResultsScreenProperties.primaryButtonLabel.isNotEmpty()) {
            if (genericResultsScreenProperties.secondaryButtonLabel.isNotEmpty()) {
                setSingleVisibleButtonMargins(primaryButton)
            }
            primaryButton.visibility = View.VISIBLE
            primaryButton.text = genericResultsScreenProperties.primaryButtonLabel
        }
        if (genericResultsScreenProperties.secondaryButtonLabel.isNotEmpty()) {
            if (genericResultsScreenProperties.primaryButtonLabel.isNotEmpty()) {
                setSingleVisibleButtonMargins(secondaryButton)
            }
            secondaryButton.visibility = View.VISIBLE
            secondaryButton.text = genericResultsScreenProperties.secondaryButtonLabel
        }
        if (genericResultsScreenProperties.primaryButtonContentDescription.isNotEmpty()) {
            primaryButton.contentDescription = genericResultsScreenProperties.primaryButtonContentDescription
        }
        if (genericResultsScreenProperties.secondaryButtonContentDescription.isNotEmpty()) {
            secondaryButton.contentDescription = genericResultsScreenProperties.secondaryButtonContentDescription
        }
        if (genericResultsScreenProperties.contactViewContactNumber.isNotEmpty()) {
            contactView.setContact(genericResultsScreenProperties.contactViewContactName, genericResultsScreenProperties.contactViewContactNumber)
            contactView.visibility = View.VISIBLE
        }
        when {
            genericResultsScreenProperties.resultScreenImage != null -> {
                resultLottieAnimationView.setImageDrawable(genericResultsScreenProperties.resultScreenImage)
            }
            genericResultsScreenProperties.resultScreenAnimation.isNotEmpty() -> {
                showResultAnimation(genericResultsScreenProperties.resultScreenAnimation)
            }
            else -> {
                resultLottieAnimationView.visibility = View.GONE
            }
        }
    }

    private fun showResultAnimation(resultScreenAnimation: String) {
        resultLottieAnimationView.setAnimation(resultScreenAnimation)
        resultLottieAnimationView.repeatCount = if (shouldAnimateOnlyOnce) 1 else LottieDrawable.INFINITE
        resultLottieAnimationView.playAnimation()
    }

    private fun setUpComponentListeners() {
        primaryOnClickListener?.let {
            primaryButton.setOnClickListener(it)
        }

        secondaryOnClickListener?.let {
            secondaryButton.setOnClickListener(it)
        }
    }

    private fun setSingleVisibleButtonMargins(button: Button) {
        val layoutParams = button.layoutParams as ConstraintLayout.LayoutParams
        val margins = context?.resources?.getDimension(R.dimen.medium_space)?.toInt() ?: 0
        layoutParams.setMargins(margins, margins, margins, margins)
    }

    companion object {
        val GENERIC_RESULT_PROPERTIES_KEY = GenericResultScreenFragment::class.java.simpleName
        const val SHOULD_ONLY_ANIMATE_ONCE = "shouldOnlyAnimateOnce"
        private var primaryOnClickListener: View.OnClickListener? = null
        private var secondaryOnClickListener: View.OnClickListener? = null

        @JvmStatic
        fun newInstance(genericResultsScreenProperties: GenericResultScreenProperties?, shouldOnlyAnimateOnce: Boolean, primaryOnClickListener: View.OnClickListener?, secondaryOnClickListener: View.OnClickListener?): GenericResultScreenFragment {
            Bundle().apply {
                putSerializable(GENERIC_RESULT_PROPERTIES_KEY, genericResultsScreenProperties)
                putBoolean(SHOULD_ONLY_ANIMATE_ONCE, shouldOnlyAnimateOnce)
                val genericResultsScreenFragment = GenericResultScreenFragment()
                genericResultsScreenFragment.arguments = this
                Companion.primaryOnClickListener = primaryOnClickListener
                Companion.secondaryOnClickListener = secondaryOnClickListener
                return genericResultsScreenFragment
            }
        }

        fun setPrimaryButtonOnClick(onClickEvent: () -> Unit) {
            primaryOnClickListener = View.OnClickListener { onClickEvent.invoke() }
        }

        fun setSecondaryButtonOnClick(onClickEvent: () -> Unit) {
            secondaryOnClickListener = View.OnClickListener { onClickEvent.invoke() }
        }
    }
}