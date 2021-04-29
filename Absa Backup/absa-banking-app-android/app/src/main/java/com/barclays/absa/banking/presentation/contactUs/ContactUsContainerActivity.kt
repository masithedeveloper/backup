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
 *
 */
package com.barclays.absa.banking.presentation.contactUs

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.utils.EmailUtil
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel
import com.barclays.absa.banking.home.ui.HomeContainerActivity.HeadingFadeType
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.PasswordGenerator
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toMaskedCellphoneNumber

class ContactUsContainerActivity : BaseActivity(),
        ContactUsContainerView,
        ContactUsContracts.ReportFraudView,
        ContactUsContracts.ContactUsView {

    private var headingFadeType: HeadingFadeType = HeadingFadeType.None

    private lateinit var toolbar: Toolbar
    private lateinit var presenter: ContactUsContainerPresenter

    private var secretCode: String? = null
    private var callMeBackUniqueReferenceNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_us_container_activity)
        toolbar = findViewById(R.id.toolbar)
        presenter = ContactUsContainerPresenter(this)
        setToolBarBack(R.string.contact_absa)
        if (absaCacheService.isCallMeBackRequested()) {
            navigateToCallMeBackFragment(null)
        } else {
            navigateToReportFraudFragment()
        }
    }

    private fun startFragment(fragment: Fragment) = startFragment(fragment, R.id.fragmentConstraintLayout,
            true, AnimationType.FADE, true, fragment.javaClass.name)

    override fun navigateToContactUsFragment() {
        setHeadingStyle(true, true)
        startFragment(ContactUsFragment())
    }

    override fun navigateToReportFraudFragment() = startFragment(ReportFraudFragment())

    override fun navigateToCallMeBackFragment(description: String?) {
        setHeadingStyle(false, true)
        startFragment(EnterSecurityCodeFragment.newInstance(description, secretCode))
    }

    private fun generateSecretCode(): String = PasswordGenerator.PasswordGeneratorBuilder()
            .useDigits(true).build().generate(6)

    override fun requestCall() {
        val generatedSecretCode = generateSecretCode()
        secretCode = generatedSecretCode
        absaCacheService.setCallMeBackRequested(true)
        val callBackDateTime = "NOW"
        presenter.requestCallBack(generatedSecretCode, callBackDateTime)
    }

    override fun verifyCallBack(secretCode: String) {
        presenter.requestVerificationCallBack(CallBackVerificationDataModel().apply {
            this.secretCode = secretCode
            secretCodeVerified = true
            uniqueRefNo = callMeBackUniqueReferenceNumber
        })
    }

    override fun showConfirmCallDialog() {
        showCallMeBackDialog()
    }

    override fun showMaxAttemptsDialog(attemptCount: Int, listener: DialogInterface.OnClickListener?) {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(getString(R.string.contact_us_attempts, attemptCount))
                .positiveDismissListener(listener ?: DialogInterface.OnClickListener { _, _ -> BaseAlertDialog.dismissAlertDialog() })
                .build())
    }

    override fun clearSecretCode() {
        secretCode = null
    }

    fun showCallMeBackDialog() {
        val cellphoneNumber = CustomerProfileObject.instance.cellNumber.toMaskedCellphoneNumber()
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(getString(R.string.cellphone_number_ending, cellphoneNumber))
                .positiveButton(getString(R.string.ok))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _, _ -> requestCall() }
                .build())
    }

    override fun navigateToCallMeBackFragment(description: String?, uniqueReferenceNumber: String?) {
        callMeBackUniqueReferenceNumber = uniqueReferenceNumber
        setHeadingStyle(false, false)
        startFragment(EnterSecurityCodeFragment.newInstance(description, secretCode))
    }

    override fun navigateToCallMeBackFailureScreen() {
        val onClickListener = View.OnClickListener { navigateToReportFraudFragment() }
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_failure.json")
                .setTitle(getString(R.string.claim_error_text))
                .setSecondaryButtonLabel(getString(R.string.try_again))
                .build(false)

        startFragment(GenericResultScreenFragment.newInstance(resultScreenProperties,
                false, null, onClickListener))
    }

    override fun navigateToGenericFailureScreen(errorMessage: String?) {
        val onClickListener = View.OnClickListener { finish() }
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_failure.json")
                .setTitle(getString(R.string.claim_error_text))
                .setDescription(errorMessage)
                .setSecondaryButtonLabel(getString(R.string.ok))
                .build(false)

        startFragment(GenericResultScreenFragment.newInstance(resultScreenProperties, false, null, onClickListener))
    }

    override fun navigateToCallMeBackSuccessScreen() {
        val onClickListener = View.OnClickListener { onBackPressed() }
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_success.json")
                .setDescription(getString(R.string.your_call_is_secure))
                .setTitle(getString(R.string.call_verified))
                .setPrimaryButtonLabel(getString(R.string.done_action))
                .build(true)

        startFragment(GenericResultScreenFragment.newInstance(resultScreenProperties, false, onClickListener, null))
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount <= 1 -> finish()
            EnterSecurityCodeFragment::class.java.name == currentFragmentName -> finish()
            else -> {
                removeFragments(1)
                setHeadingStyle(false, false)
            }
        }
    }

    override fun isCallMeBackSessionAvailable(): Boolean = secretCode != null

    private fun setHeadingStyle(isContactUsFragment: Boolean, isDark: Boolean) {
        if (isContactUsFragment) {
            headingFadeType = HeadingFadeType.FadeToContactUs
        } else if (headingFadeType == HeadingFadeType.FadeToContactUs) {
            headingFadeType = HeadingFadeType.FadeFromContactUs
        }

        when (headingFadeType) {
            HeadingFadeType.None -> toolbar.setBackgroundColor(Color.TRANSPARENT)
            HeadingFadeType.FadeFromContactUs -> toolbar.setBackgroundResource(R.drawable.home_container_tranparent_warm_purple_transition)
            HeadingFadeType.FadeToContactUs -> toolbar.setBackgroundResource(R.drawable.home_container_warm_purple_transparent_transition)
        }

        if (isDark) {
            toolbar.setTitleTextAppearance(this, za.co.absa.presentation.uilib.R.style.ToolbarLiteTheme_TitleTextStyle)
            toolBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
        } else {
            toolbar.setTitleTextAppearance(this, za.co.absa.presentation.uilib.R.style.ToolbarDarkTheme_TitleTextStyle)
            toolBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_dark)
        }
    }

    override fun emailAppSupportOrGeneralEnquiry(emailAddress: String) = EmailUtil.email(this, emailAddress)

    override fun call(phoneNumber: String) = TelephoneUtil.call(this, phoneNumber)

    override fun initView() = startFragment(ContactUsFragment(), R.id.fragmentContainer,
            true, AnimationType.NONE, false, ContactUsActivity::class.java.simpleName)
}
