/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.recognition.ui

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.HelpTypeReceivedFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.AliasEncrypter
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.viewModel
import com.google.zxing.integration.android.IntentIntegrator
import styleguide.forms.ItemCheckedInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class BranchRecognitionHelpTypeReceivedFragment : AbsaBaseFragment<HelpTypeReceivedFragmentBinding>(), ItemCheckedInterface, ActivityCompat.OnRequestPermissionsResultCallback, DialogInterface.OnClickListener {

    private var branchRecognitionView: BranchRecognitionView? = null
    private var functionCodePosition: Int = 0
    private lateinit var branchRecognitionViewModel: BranchRecognitionActivityViewModel
    private var branchRecognitionResponse: BranchRecognitionResponse? = null

    private companion object {
        private const val APP_NTB = "APPNTB"
    }

    override fun getLayoutResourceId(): Int = R.layout.help_type_received_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        branchRecognitionViewModel = baseActivity.viewModel()

        loadData()
        branchRecognitionView = activity as BranchRecognitionView
        branchRecognitionView?.showAppToolbar()
        binding.scanQrCodeTextView.visibility = View.GONE
    }

    private fun loadData() {
        branchRecognitionViewModel.provideInitialData()?.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            if (it != null && branchRecognitionViewModel.validateResponse(it)) {
                branchRecognitionResponse = it
                setupListeners()
                initView()
            } else {
                showResultScreen(isSuccess = false, isPermissionError = false)
            }
        })
    }

    private fun setupListeners() {
        binding.scanQrCodeButton.setOnClickListener {
            preventDoubleClick(it)
            prepareButton()
        }
    }

    private fun initView() {
        val helpOptions = SelectorList<StringItem>()
        branchRecognitionView?.setToolbarTitle(R.string.branch_help_someone_helped_me)
        if (!branchRecognitionResponse?.functionList.isNullOrEmpty()) {
            branchRecognitionResponse?.functionList?.let {
                for (functionList in branchRecognitionResponse?.functionList!!) {
                    helpOptions.add(StringItem(functionList.functionDescription))
                }
                binding.helpTypeRadioButtonView.setDataSource(helpOptions)
                binding.helpTypeRadioButtonView.setItemCheckedInterface(this)
                binding.helpTypeRadioButtonView.selectedIndex = branchRecognitionViewModel.selectedIndex
            }
        } else {
            showResultScreen(isSuccess = false, isPermissionError = false)
        }
    }

    private fun prepareButton() {
        if (getString(R.string.next).equals(binding.scanQrCodeButton.text.toString(), ignoreCase = true)) {
            findNavController().navigate(R.id.branchAccountOpenedFragment)
        } else {
            requestCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()

        if (branchRecognitionViewModel.justReturnedFromSettings) {
            branchRecognitionViewModel.justReturnedFromSettings = false
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (activity != null) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.permissions_camera_rationale))
                        .positiveButton(getString(R.string.ok))
                        .negativeButton(getString(R.string.cancel))
                        .positiveDismissListener { _, _ ->
                            PermissionHelper.requestCameraAccessPermission(baseActivity) {
                                launchQrScanner()
                            }
                        }
                        .negativeDismissListener { _, _ -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PermissionHelper.PermissionCode.ACCESS_CAMERA.value) }
                        .build())
            } else if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                PermissionHelper.requestCameraAccessPermission(baseActivity) {
                    launchQrScanner()
                }
            } else {
                launchQrScanner()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            showResultScreen(isSuccess = false, isPermissionError = true)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onChecked(index: Int) {
        branchRecognitionViewModel.selectedIndex = index
        binding.scanQrCodeTextView.visibility = View.VISIBLE
        functionCodePosition = index
        if (index != -1) {
            binding.scanQrCodeButton.isEnabled = true
        }
        if (APP_NTB == branchRecognitionResponse?.functionList?.get(index)?.functionCode) {
            binding.scanQrCodeButton.text = getString(R.string.next)
        } else {
            binding.scanQrCodeButton.text = getString(R.string.branch_help_scan_qr)
        }
    }

    private fun launchQrScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setOrientationLocked(true)
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.captureActivity = BranchRecognitionBarcodeScanActivity::class.java
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (result != null && !result.contents.isNullOrEmpty()) {
                val handler = Handler(BMBApplication.getInstance().backgroundHandlerThread.looper)
                handler.post {
                    uploadData(result.contents)
                }
            } else {
                showResultScreen(isSuccess = true, isPermissionError = false)
            }
        }
    }

    private fun uploadData(qrCode: String) {
        var digitalIdentifier = "StubAlias"
        if (!BuildConfigHelper.STUB && ProfileManager.getInstance().activeUserProfile != null) {
            digitalIdentifier = AliasEncrypter.encryptAlias(ProfileManager.getInstance().activeUserProfile.userId, false)
        }
        val functionCode = branchRecognitionResponse?.functionList?.get(functionCodePosition)?.functionCode
        val recognitionConfirmation = BranchRecognitionConfirmation(qrCode, functionCode, digitalIdentifier, null, null, null, null)

        baseActivity.runOnUiThread {
            showResultScreen(isSuccess = true, isPermissionError = false)
            branchRecognitionViewModel.uploadBranchData(recognitionConfirmation)
        }
    }

    private fun showResultScreen(isSuccess: Boolean, isPermissionError: Boolean) {

        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
        val resultsBundle = Bundle()

        when {
            isPermissionError ->
                resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalFailure)
                        .setTitle(getString(R.string.branch_help_camera_permission_failed_title))
                        .setDescription(getString(R.string.branch_help_camera_permission_failed_description))
                        .setPrimaryButtonLabel(getString(R.string.ok))
                        .setSecondaryButtonLabel(getString(R.string.branch_help_camera_open_settings))
            isSuccess ->
                resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalSuccess)
                        .setTitle(getString(R.string.branch_help_result_success))
                        .setPrimaryButtonLabel(getString(R.string.done))
            else ->
                resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalFailure)
                        .setTitle(getString(R.string.something_went_wrong))
                        .setDescription(getString(R.string.something_went_wrong_message))
                        .setPrimaryButtonLabel(getString(R.string.ok))
        }

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        if (isPermissionError) {
            GenericResultScreenFragment.setSecondaryButtonOnClick {
                branchRecognitionViewModel.justReturnedFromSettings = true
                branchRecognitionView?.launchSettingsActivity()
            }
        }

        resultsBundle.putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, resultScreenProperties.build(isSuccess))
        branchRecognitionView?.hideAppToolbar()
        dismissProgressDialog()
        findNavController().navigate(R.id.branchRecognitionResultScreenFragment, resultsBundle)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        dialog?.dismiss()
    }
}
