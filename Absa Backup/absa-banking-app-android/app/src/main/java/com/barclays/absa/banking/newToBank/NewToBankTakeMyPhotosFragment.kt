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

package com.barclays.absa.banking.newToBank

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.NewToBankTakeMyPhotosFragmentBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.PermissionHelper
import kotlinx.android.synthetic.main.new_to_bank_take_my_photos_fragment.*
import styleguide.buttons.OptionActionButtonView
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class NewToBankTakeMyPhotosFragment : ExtendedFragment<NewToBankTakeMyPhotosFragmentBinding>() {

    private var justReturnedFromSettings: Boolean = false
    private var newToBankView: NewToBankView? = null

    override fun getLayoutResourceId() = R.layout.new_to_bank_take_my_photos_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        newToBankView = activity as NewToBankView?
        newToBankView?.toolbarTitle = toolbarTitle
        newToBankView?.showToolbar()
        if (newToBankView?.isStudentFlow == true) {
            newToBankView?.trackStudentAccount("StudentAccount_TakeMyPhotos_ScreenDisplayed")
        } else {
            newToBankView?.trackCurrentFragment(NewToBankConstants.SELFIE_INSTRUCTION_SCREEN)
        }

        adjustPadding()
        setUpComponentListeners()
    }

    override fun onResume() {
        super.onResume()

        if (justReturnedFromSettings) {
            justReturnedFromSettings = false
            requestCameraPermission()
        }
    }

    private fun adjustPadding() {
        setMargins(binding.instructionsOptionActionButtonView)
        setMargins(binding.faceFitsOptionActionButtonView)
        setMargins(binding.sunglassesOptionActionButtonView)
        setMargins(binding.wellLitAreaOptionActionButtonView)
        setMargins(binding.dontMoveAroundOptionActionButtonView)
    }

    private fun setMargins(optionActionButtonView: OptionActionButtonView) {
        optionActionButtonView.removeTopAndBottomMargins()
        optionActionButtonView.setPadding(0, resources.getDimensionPixelSize(R.dimen.small_space), 0, 0)
    }

    private fun setUpComponentListeners() {
        if (BuildConfig.TOGGLE_DEF_SELFIE_PRIVACY_CONSENT_ENABLED || FeatureSwitchingCache.featureSwitchingToggles.newToBankSelfiePrivacyIndicator == FeatureSwitchingStates.GONE.key) {
            newToBankSelfiePrivacyConsentCheckBox.visibility = View.VISIBLE
            complianceDescriptionView.visibility = View.GONE
        } else {
            complianceDescriptionView.visibility = View.VISIBLE
            newToBankSelfiePrivacyConsentCheckBox.visibility = View.GONE
        }

        binding.takeMyPhotosButton.setOnClickListener { requestCameraPermission() }
        newToBankSelfiePrivacyConsentCheckBox.setOnCheckedListener { newToBankSelfiePrivacyConsentCheckBox.clearError() }
    }

    private fun requestCameraPermission() {
        newToBankView?.let {
            if (newToBankSelfiePrivacyConsentCheckBox.isChecked) {
                it.newToBankTempData.useSelfie = true
                activity?.let { fragmentActivity ->
                    PermissionHelper.requestCameraAccessPermission(fragmentActivity, R.string.new_to_bank_permissions_camera_rationale) { newToBankView?.navigateToSelfieFragment() }
                }
            } else {
                newToBankSelfiePrivacyConsentCheckBox.setErrorMessage(R.string.new_to_bank_error_message)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && newToBankSelfiePrivacyConsentCheckBox.isChecked) {
                newToBankView?.navigateToSelfieFragment()
            } else {
                showPermissionResultScreen()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPermissionResultScreen() {
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()

        resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.branch_help_camera_permission_failed_title))
                .setDescription(getString(R.string.new_to_bank_camera_access_error))
                .setPrimaryButtonLabel(getString(R.string.ok))
                .setSecondaryButtonLabel(getString(R.string.branch_help_camera_open_settings))

        val primaryOnClick = View.OnClickListener { parentFragmentManager.popBackStack() }

        val secondaryOnClick = View.OnClickListener {
            justReturnedFromSettings = true
            val uri = Uri.parse("package:" + baseActivity.packageName)
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
        }

        val genericResultsScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties.build(false), false, primaryOnClick, secondaryOnClick)
        (activity as BaseActivity).startFragment(genericResultsScreenFragment, false, BaseActivity.AnimationType.FADE)
        (activity as BaseActivity).hideToolBar()
    }

    override fun getToolbarTitle() = getString(R.string.new_to_bank_who_you_are)

    companion object {
        fun newInstance(): NewToBankTakeMyPhotosFragment {
            return NewToBankTakeMyPhotosFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cancel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel_menu_item) {
            BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                    .message(getString(R.string.new_to_bank_cancel_process))
                    .positiveDismissListener { _, _ ->
                        baseActivity.finish()
                        startActivity(Intent(baseActivity, WelcomeActivity::class.java))
                    })
        }
        return super.onOptionsItemSelected(item)
    }
}