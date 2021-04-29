/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.taxCertificates.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.ui.StatementUtils
import com.barclays.absa.banking.account.ui.StatementUtils.TAX_CERTIFICATES
import com.barclays.absa.banking.databinding.FragmentSelectTaxYearAndTypeBinding
import com.barclays.absa.banking.express.taxCertificate.TaxCertificateViewModel
import com.barclays.absa.banking.express.taxCertificate.TaxCertificatesTypesViewModel
import com.barclays.absa.banking.express.taxCertificate.dto.TaxCertificateResponse
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.fileUtils.GenericStatementsViewModel
import styleguide.forms.SelectorList
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties.PropertiesBuilder

class SelectTaxYearAndTypeFragment : BaseFragment(R.layout.fragment_select_tax_year_and_type) {

    private val taxCertificateTypesViewModel by activityViewModels<TaxCertificatesTypesViewModel>()
    private val taxCertificateViewModel by activityViewModels<TaxCertificateViewModel>()
    private val statementsViewModel by activityViewModels<GenericStatementsViewModel>()
    private val binding by viewBinding(FragmentSelectTaxYearAndTypeBinding::bind)

    private lateinit var pdfByteArray: ByteArray

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.tax_certificates_menu_item, true)

        if (isBusinessAccount) {
            AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_TaxCertificateSelection_ScreenDisplayed")
        } else {
            AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_TaxCertificateSelection_ScreenDisplayed")
        }

        setUpComponentListeners()
        setClickListeners()
        setObservers()
    }

    private fun setClickListeners() {
        binding.continueButton.setOnClickListener {
            if (validateFields()) {
                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_TaxCertificateSelection_ContinueTapped")
                } else {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_TaxCertificateSelection_ContinueTapped")
                }

                PermissionHelper.requestExternalStorageWritePermission(baseActivity) {
                    val selectedCertificateType = taxCertificateTypesViewModel.taxCertificateTypesResponseLiveData.value?.taxCertificateTypes?.getValue(binding.selectCertificateTypeNormalInputView.selectedValue) ?: ""
                    with(taxCertificateViewModel) {
                        fetchTaxCertificate(binding.selectTaxYearNormalInputView.selectedValue, selectedCertificateType)
                        taxCertificateResponseLiveData.observe(viewLifecycleOwner) {
                            handleTaxCertificate(it)
                        }
                    }
                }
            }
        }

        binding.selectCertificateTypeNormalInputView.setOnClickListener {
            binding.selectCertificateTypeNormalInputView.triggerListActivity()
        }
    }

    private fun setObservers() {
        with(taxCertificateTypesViewModel) {
            fetchTaxYearAndTypes()
            taxCertificateTypesResponseLiveData.observe(viewLifecycleOwner) { response ->
                val selectTaxTypeList = SelectorList<TaxTypesObjectWrapper>()
                val taxYears = mutableListOf<String>()

                response.taxCertificateTypes.forEach {
                    selectTaxTypeList.add(TaxTypesObjectWrapper(it.key))
                }

                response.taxYears.forEach {
                    taxYears.add(it.key)
                }

                binding.selectTaxYearNormalInputView.setOnClickListener {
                    taxYearSelector(taxYears)
                }

                binding.selectCertificateTypeNormalInputView.apply {
                    setList(selectTaxTypeList, getString(R.string.tax_certificates_menu_item))
                    setItemSelectionInterface { index ->
                        selectTaxTypeList[index].displayValueLine2?.let { value ->
                            binding.selectCertificateTypeNormalInputView.selectedValue = value
                        }
                    }
                }
                dismissProgressDialog()
            }
        }

        taxCertificateViewModel.failureLiveData.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            navigateToCertificateDoesNotExistScreen()

            if (isBusinessAccount) {
                AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_Error_ScreenDisplayed")
            } else {
                AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_Error_ScreenDisplayed")
            }
        }
    }

    private fun handleTaxCertificate(response: TaxCertificateResponse) {
        dismissProgressDialog()
        with(response) {
            if (fileContent.isEmpty()) {
                navigateToErrorScreen()

                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_Error_ScreenDisplayed")
                } else {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_Error_ScreenDisplayed")
                }
            } else {
                pdfByteArray = Base64.decode(fileContent, Base64.DEFAULT)
                startActivityForResult(StatementUtils.getPDFIntent(fileName, baseActivity), TAX_CERTIFICATES)

                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_TaxCertificatePDF_ScreenDisplayed")
                } else {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_TaxCertificatePDF_ScreenDisplayed")
                }
            }
        }
    }

    private fun setUpComponentListeners() {
        binding.selectTaxYearNormalInputView.addRequiredValidationHidingTextWatcher()
        binding.selectCertificateTypeNormalInputView.addRequiredValidationHidingTextWatcher()
    }

    fun validateFields(): Boolean {
        with(binding) {
            when {
                selectTaxYearNormalInputView.selectedValue.isEmpty() -> selectTaxYearNormalInputView.setError(getString(R.string.tax_certificates_please_select_tax_year))
                selectCertificateTypeNormalInputView.selectedValue.isEmpty() -> selectCertificateTypeNormalInputView.setError(getString(R.string.tax_certificates_please_select_certificate_type))
                else -> return true
            }
        }
        return false
    }

    private fun navigateToCertificateDoesNotExistScreen() {
        hideToolBar()
        PropertiesBuilder().apply {
            setDescription(getString(R.string.tax_certificates_no_data_exists_for_the_selected_criteria))
            setPrimaryButtonLabel(getString(R.string.ok))
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setTitle(getString(R.string.tax_certificates_unable_to_locate_certificate))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_Error_OKTapped")
                } else {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_Error_OKTapped")
                }
                findNavController().navigate(R.id.action_genericResultFragment_to_selectTaxYearAndTypeFragment)
                showToolBar()
            }
            navigate(SelectTaxYearAndTypeFragmentDirections.actionSelectTaxYearAndTypeFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToErrorScreen() {
        hideToolBar()
        PropertiesBuilder().apply {
            setDescription(getString(R.string.tax_certificates_having_trouble_opening_document))
            setPrimaryButtonLabel(getString(R.string.ok))
            setResultScreenAnimation(ResultAnimations.generalError)
            setTitle(getString(R.string.something_went_wrong))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_Error_OKTapped")
                } else {
                    AnalyticsUtil.trackAction(TaxCertificatesActivity.TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_Error_OKTapped")
                }
                findNavController().navigate(R.id.action_genericResultFragment_to_selectTaxYearAndTypeFragment)
                showToolBar()
            }
            navigate(SelectTaxYearAndTypeFragmentDirections.actionSelectTaxYearAndTypeFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun taxYearSelector(taxYear: List<String>) {
        val dialogView: View = layoutInflater.inflate(R.layout.number_picker_view, null)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.selectYearNumberPicker)

        numberPicker.apply {
            minValue = 0
            maxValue = taxYear.size - 1
            displayedValues = taxYear.toTypedArray()
            wrapSelectorWheel = false
        }
        AlertDialog.Builder(baseActivity, R.style.MyDialogTheme).apply {
            setCancelable(true)
                    .setView(dialogView).setNegativeButton(getText(R.string.cancel)) { dialog, _ ->
                        dialog?.dismiss()
                    }.setPositiveButton(getText(R.string.ok)) { dialog, _ ->
                        binding.selectTaxYearNormalInputView.text = taxYear[numberPicker.value]
                        dialog?.dismiss()
                    }
                    .create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == TAX_CERTIFICATES) {
            val uri = data?.data ?: Uri.EMPTY

            statementsViewModel.writePdfFileContent(uri, pdfByteArray).observe(this, {
                with(Intent(Intent.ACTION_VIEW)) {
                    this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    this.setDataAndType(it, "application/pdf")
                    startActivityIfAvailable(this)
                }
            })
        }
    }
}