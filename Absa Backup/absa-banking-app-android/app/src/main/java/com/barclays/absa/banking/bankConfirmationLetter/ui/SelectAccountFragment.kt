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
 *
 */

package com.barclays.absa.banking.bankConfirmationLetter.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.bankConfirmationLetter.ui.BankConfirmationLetterActivity.Companion.BANK_CONFIRMATION_LETTER_ANALYTIC_TAG
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.fragment_select_account.*
import styleguide.forms.GenericAdapter
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorList
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class SelectAccountFragment : BaseFragment(R.layout.fragment_select_account), ItemSelectionInterface {

    private lateinit var bankConfirmationLetterViewModel: BankConfirmationLetterViewModel
    private var selectedIndex = -1
    private val accountTypesList = SelectorList<AccountTypesObjectWrapper>()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var pdfByteArray: ByteArray

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bankConfirmationLetterViewModel = viewModel()

        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                handler.postDelayed({
                    accountTypesList[selectedIndex].displayValueLine2?.let {
                        bankConfirmationLetterViewModel.fetchBankConfirmationLetter(it)
                    }
                }, 250)
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()
                showErrorResultScreen(true)
            }

            override fun onSureCheckRejected() {
                showErrorResultScreen(false)
            }

            override fun onSureCheckCancelled() {
                super.onSureCheckCancelled()
                baseActivity.finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.bank_confirmation_letter_select_account)
        setupObserver()

        if (isBusinessAccount) {
            AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_SelectAccount_ScreenDisplayed")
        } else {
            AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_SelectAccount_ScreenDisplayed")
        }

        val accountList = AbsaCacheManager.getInstance().accountsList
        accountList.accountsList.filter { it.accountType == AccountTypesBMG.savingsAccount.name || it.accountType == AccountTypesBMG.currentAccount.name }.forEach { accountObject ->
            accountTypesList.add(AccountTypesObjectWrapper(accountObject.description, accountObject.accountNumber))
        }

        accountsRecyclerView.apply {
            layoutManager = LinearLayoutManager(baseActivity)
            adapter = GenericAdapter(accountTypesList, selectedIndex, this@SelectAccountFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PdfUtil.PDF_FILE_DOWNLOAD) {
            val uri = data?.data ?: Uri.EMPTY

            if (::pdfByteArray.isInitialized) {
                PdfUtil.savePdfFile(baseActivity, uri, pdfByteArray).observe(baseActivity, { pdfUri ->
                    pdfUri?.let { PdfUtil.launchPdfViewer(baseActivity, it) }
                    baseActivity.finish()
                })
            }
        }
    }

    private fun setupObserver() {
        bankConfirmationLetterViewModel.apply {

            bankConfirmationLetterResponse.removeObservers(viewLifecycleOwner)
            bankConfirmationLetterResponse.observe(viewLifecycleOwner, { successResponse ->
                if (BMBConstants.FAILURE.equals(successResponse.transactionStatus, true)) {
                    if (isBusinessAccount) {
                        AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_Unsuccessful_ScreenDisplayed")
                    } else {
                        AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_Unsuccessful_ScreenDisplayed")
                    }
                    navigateToUnsuccessfulScreen()
                } else {
                    val bclDocument = successResponse.bclDocument
                    sureCheckDelegate.processSureCheck(baseActivity, successResponse) {
                        if (bclDocument.isNullOrEmpty()) {
                            if (isBusinessAccount) {
                                AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_SomethingWentWrong_ScreenDisplayed")
                            } else {
                                AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_SomethingWentWrong_ScreenDisplayed")
                            }
                            navigateToSomethingWentWrongScreen()
                        } else {
                            if (isBusinessAccount) {
                                AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_BankConfirmationLetterPDF_ScreenDisplayed")
                            } else {
                                AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_BankConfirmationLetterPDF_ScreenDisplayed")
                            }
                            pdfByteArray = Base64.decode(bclDocument, Base64.DEFAULT)
                            PdfUtil.openPdfFileIntent(this@SelectAccountFragment)
                        }
                    }
                }
                dismissProgressDialog()
            })
        }
    }

    private fun navigateToUnsuccessfulScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setDescription(getString(R.string.bank_confirmation_letter_unsuccessful_description))
            setPrimaryButtonLabel(getString(R.string.close))
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.bank_confirmation_letter_unsuccessful_title))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                showToolBar()
                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_Unsuccessful_CloseTapped")
                } else {
                    AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_Unsuccessful_CloseTapped")
                }
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(SelectAccountFragmentDirections.actionSelectAccountFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToSomethingWentWrongScreen() {
        baseActivity.finish()
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setDescription(getString(R.string.bank_confirmation_letter_something_went_wrong_description))
            setPrimaryButtonLabel(getString(R.string.ok))
            setResultScreenAnimation(ResultAnimations.generalError)
            setTitle(getString(R.string.bank_confirmation_letter_something_went_wrong_title))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                showToolBar()
                if (isBusinessAccount) {
                    AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_SomethingWentWrong_OkTapped")
                } else {
                    AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_SomethingWentWrong_OkTapped")
                }
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(SelectAccountFragmentDirections.actionSelectAccountFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun showErrorResultScreen(isFailureResult: Boolean) {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { navigateToHomeScreenWithoutReloadingAccounts() }
        val intent = Intent(baseActivity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
            if (isFailureResult) {
                putExtra(GenericResultActivity.IS_FAILURE, true)
                putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed)
            } else {
                putExtra(GenericResultActivity.IS_GENERAL_ALERT, true)
                putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected)
            }
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home)
        }
        baseActivity.startActivity(intent)
    }

    override fun onItemClicked(index: Int) {
        if (isBusinessAccount) {
            AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_SelectAccount_AccountSelected")
        } else {
            AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_SelectAccount_AccountSelected")
        }

        PermissionHelper.requestExternalStorageWritePermission(baseActivity) {
            accountTypesList[index].displayValueLine2?.let {
                bankConfirmationLetterViewModel.fetchBankConfirmationLetter(it)
            }
        }
        selectedIndex = index
    }
}