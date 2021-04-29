/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.expressCashSend.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.CashSendToNewBeneficiaryFragmentBinding
import com.barclays.absa.banking.express.cashSend.addCashSendBeneficiaryDetails.AddCashSendBeneficiaryViewModel
import com.barclays.absa.banking.express.cashSend.addCashSendBeneficiaryDetails.dto.AddCashSendBeneficiaryDetailsRequest
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiary
import com.barclays.absa.banking.framework.BaseActivity.Companion.mScreenName
import com.barclays.absa.banking.framework.BaseActivity.Companion.mSiteSection
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AccessibilityUtils.isExploreByTouchEnabled
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.CommonUtils.setInputFilter
import com.barclays.absa.utils.ValidationUtils
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.imageHelpers.BeneficiaryImageHelper
import com.barclays.absa.utils.imageHelpers.ImageHelper
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toTenDigitPhoneNumber

class CashSendToNewBeneficaryFragment : CashSendBaseFragment(R.layout.cash_send_to_new_beneficiary_fragment), ImageHelper.OnImageActionListener {
    private val binding by viewBinding(CashSendToNewBeneficiaryFragmentBinding::bind)
    private val addCashSendBeneficiaryViewModel by viewModels<AddCashSendBeneficiaryViewModel>()

    private var addToFavourites: Boolean = false
    private var hasImage: Boolean = false
    private val contactUri: Uri? = null
    private var beneficiaryImageHelper: BeneficiaryImageHelper? = null
    private var beneficiaryDetails: CashSendBeneficiary = CashSendBeneficiary()

    companion object {
        const val BENEFICIARY_DETAIL = "beneficiaryDetail"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cashsend)
        isCashSendPlus = cashSendViewModel.isCashSendPlus.value ?: false
        mScreenName = BMBConstants.CASHSEND_TO_SOMEONE_NEW_CONST
        mSiteSection = BMBConstants.CASHSEND_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_TO_SOMEONE_NEW_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST)
        initViews()
        setupTalkBack()
        getDeviceProfilingInteractor().notifyAddBeneficiary()
        setUpComponentListeners()
    }

    //TODO: To add image in future
//    private val addCashSendBeneficiaryExtendedResponseListener: ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> = object : ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject>() {
//        override fun onRequestStarted() {}
//        override fun onSuccess(successResponse: AddBeneficiaryCashSendConfirmationObject) {
//            addBeneficiarySuccessObject = successResponse
//            if (BMBConstants.SUCCESS == addBeneficiarySuccessObject.status) {
//                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, BMBConstants.PASS_CASHSEND)
//                if (beneficiaryImageHelper?.getBitmap() != null) {
//                    try {
//                        // pass parameters
//                        val bundle = Bundle()
//                        bundle.putString(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND)
//                        if (TextUtils.isEmpty(addBeneficiarySuccessObject.beneficiaryId)) {
//                            navigateToNextScreen()
//                        }
//                        else {
//                            beneficiariesService.uploadBeneficiaryImage(addBeneficiarySuccessObject.beneficiaryId, BMBConstants.PASS_CASHSEND,
//                                    ImageUtils.convertToBase64(beneficiaryImageHelper?.getBitmap()), BMBConstants.MIME_TYPE_JPG,
//                                    BMBConstants.SERVICE_ACTIONTYPE_ADD, addBeneficiaryImageExtendedResponseListener)
//                        }
//                    } catch (e: IOException) {
//                        if (BuildConfig.DEBUG) e.printStackTrace()
//                        navigateToNextScreen()
//                    } catch (e: Exception) {
//                        e("Image size is too big" + e.message)
//                        addBeneficiarySuccessObject.msg = getString(R.string.image_size_limit)
//                        navigateToNextScreen()
//                    }
//                } else {
//                    navigateToNextScreen()
//                }
//            }
//        }
//    }

    //TODO: To add image in future
//    private val addBeneficiaryImageExtendedResponseListener: ExtendedResponseListener<AddBeneficiaryObject> = object : ExtendedResponseListener<AddBeneficiaryObject>() {
//        override fun onRequestStarted() {}
//        override fun onSuccess(addBeneficiaryObject: AddBeneficiaryObject) {
//            if (BMBConstants.SUCCESS.equals(addBeneficiaryObject.status, ignoreCase = true)) {
//                // Save beneficiary image on successful image upload
//                val addBeneficiaryDAO = AddBeneficiaryDAO(activity)
//                addBeneficiaryObject.imageData = ImageUtils.convertToByteArray(beneficiaryImageHelper?.getBitmap())
//                addBeneficiaryDAO.saveBeneficiary(addBeneficiaryObject)
//            } else {
//                addBeneficiarySuccessObject.msg = addBeneficiaryObject.msg
//            }
//            navigateToNextScreen()
//        }
//    }

    private fun navigateToNextScreen() {
        dismissProgressDialog()
        if (!isCashSendPlus) {
            beneficiaryDetails = CashSendBeneficiary().apply {
                targetAccountNumber = binding.numberInputView.selectedValue
                beneficiaryDetails.apply {
                    beneficiaryName = "${binding.nameEditBeneficiaryInputView.selectedValue} ${binding.surnameInputView.selectedValue}"
                    statementReference = binding.myReferenceInputView.selectedValue
                }
            }
            launchSuccessScreen()
        }
    }

    fun setupTalkBack() {
        if (isExploreByTouchEnabled()) {
            binding.numberInputView.setIconImageViewDescription(getString(R.string.talkback_cashsend_choose_contact_from_phone))
            binding.nameEditBeneficiaryInputView.beneficiaryImageView.contentDescription = getString(R.string.talkback_cashsend_sn_beneficiary_profile_picture)
        }
    }

    private fun initViews() {
        beneficiaryImageHelper = BeneficiaryImageHelper(activity, binding.nameEditBeneficiaryInputView.beneficiaryImageView)
        beneficiaryImageHelper?.setOnImageActionListener(this)
        beneficiaryImageHelper?.setDefaultPlaceHolderImageId(R.drawable.ic_image_upload)

        setInputFilter(binding.nameEditBeneficiaryInputView.editText)
        setInputFilter(binding.surnameInputView.editText)
        context?.let { ContactDialogOptionListener(binding.numberInputView.editText, R.string.selFrmPhoneBookMsg, it, BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS, null) }?.let { binding.numberInputView.setImageViewOnTouchListener(it) }
    }

    private fun setUpComponentListeners() {
        binding.nameEditBeneficiaryInputView.addRequiredValidationHidingTextWatcher()
        binding.surnameInputView.addRequiredValidationHidingTextWatcher()
        binding.myReferenceInputView.addRequiredValidationHidingTextWatcher()
        binding.numberInputView.addRequiredValidationHidingTextWatcher()
        binding.nextButton.setOnClickListener {
            requestAddCashSendBeneficiary()
        }
    }

    private fun requestAddCashSendBeneficiary() {
        if (validateAndPopulateConfirmation()) {
            val addCashSendBeneficiaryDetailsRequest = AddCashSendBeneficiaryDetailsRequest().apply {
                beneficiaryName = binding.nameEditBeneficiaryInputView.selectedValue
                beneficiaryShortName = binding.nameEditBeneficiaryInputView.selectedValue
                beneficiarySurname = binding.surnameInputView.selectedValue
                recipientCellphoneNumber = binding.numberInputView.selectedValue.toTenDigitPhoneNumber()
                statementReference = binding.myReferenceInputView.selectedValue
            }
            addCashSendBeneficiaryViewModel.addCashSendBeneficiaryDetails(addCashSendBeneficiaryDetailsRequest)

            addCashSendBeneficiaryViewModel.addCashSendBeneficiaryDetailsResponse.observe(viewLifecycleOwner, {
                navigateToNextScreen()
                dismissProgressDialog()
            })
        }
    }

    private fun launchSuccessScreen() {
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.beneficiary_added_successfully))
                .setPrimaryButtonLabel(getString(R.string.home))
                .setSecondaryButtonLabel(getString(R.string.send_cash))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            loadAccountsAndGoHome()
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick {
            with(Bundle()) {
                putParcelable(BENEFICIARY_DETAIL, beneficiaryDetails)
                findNavController().navigate(R.id.action_cashSendGenericResultScreenFragment_to_cashSendDetailFragment, this)
            }
        }
        navigate(CashSendToNewBeneficaryFragmentDirections.actionCashSendToNewBeneficiaryFragmentToCashSendGenericResultScreenFragment(resultScreenProperties))
    }

    //TODO: Will have to be updated
    private fun readContact() {
        val contact = CommonUtils.getContact(activity, contactUri)
        val nameAndSurname = CommonUtils.getNameDetails(contact)
        nameAndSurname?.let {
            val name = nameAndSurname.first
            val surname = nameAndSurname.second
            name?.let { binding.nameEditBeneficiaryInputView.selectedValue = name }
            surname?.let { binding.surnameInputView.text = surname }
        }
        CommonUtils.updateMobileNumberOnSelection(activity, binding.numberInputView, contact)
    }

    override fun onProfileImageLoad() {
        hasImage = true
    }

    private fun validateAndPopulateConfirmation(): Boolean {
        if (!(ValidationUtils.validateInput(binding.nameEditBeneficiaryInputView, resources.getString(R.string.firstName)) && ValidationUtils.validateInput(binding.surnameInputView, resources.getString(R.string.surname)) && ValidationUtils.validatePhoneNumberInput(binding.numberInputView.text) && ValidationUtils.validateInput(binding.myReferenceInputView, resources.getString(R.string.my_reference)))) {
            return false
        }
        addToFavourites = false
        return true
    }
}