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
package com.barclays.absa.banking.lotto.ui

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.BaseView
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.lotto_ticket_confirmation_fragment.*
import kotlinx.android.synthetic.main.step_indicator_button.*
import lotto.LottoBoard
import styleguide.forms.SelectorList
import styleguide.forms.SelectorType
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTenDigitPhoneNumber

class LottoTicketConfirmationFragment : BaseFragment(R.layout.lotto_ticket_confirmation_fragment) {

    private lateinit var lottoSourceAccounts: SelectorList<AccountObjectWrapper>
    private lateinit var selectedAccount: AccountObject
    private lateinit var lottoViewModel: LottoViewModel
    private var hasChangedTerms = false
    private var contactNumber: String = ""
    private var lottoFlowTag = "ConfirmPurchaseTicket"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (lottoViewModel.purchaseData.quickPickInd) {
            if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_LOTTO) {
                setToolBar(R.string.lotto_lotto_quick_pick)
            } else {
                setToolBar(R.string.lotto_powerball_quick_pick, true)
            }
            stepIndicatorTextView.text = getString(R.string.lotto_step_indicator_text, 2, 2, getString(R.string.lotto_confirm_ticket))
        } else {
            if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_LOTTO) {
                setToolBar(R.string.lotto_play_the_lotto)
            } else {
                setToolBar(R.string.lotto_play_powerball, true)
            }
            stepIndicatorTextView.text = getString(R.string.lotto_step_indicator_text, 3, 3, getString(R.string.lotto_confirm_ticket))
        }
        lottoViewModel.purchaseData.lottoGameType = lottoViewModel.currentGameRules.gameType

        (activity as LottoActivity).tagLottoAndPowerBallEvent("ConfirmPurchaseTicket_QuickPickIndicator${lottoViewModel.purchaseData.quickPickInd.toString().toSentenceCase()}")

        setUpTermsView()
        setupListeners()
        setupSourceAccounts()
        setUpViews()
        setUpBoard()
    }

    private fun setUpViews() {
        val ticketHistoryList = lottoViewModel.lottoTicketHistoryLiveData.value
        if (!ticketHistoryList.isNullOrEmpty() && ticketHistoryList[0].lottoCustomerDetails.cellPhoneNumber.isNotEmpty()) {
            contactNumberNormalInputView.selectedValue = ticketHistoryList[0].lottoCustomerDetails.cellPhoneNumber.toTenDigitPhoneNumber()
        } else {
            contactNumberNormalInputView.selectedValue = appCacheService.getCellphoneNumber().toTenDigitPhoneNumber()
        }
        contactNumber = contactNumberNormalInputView.selectedValue

        totalTextView.text = Amount(lottoViewModel.purchaseData.purchaseAmount.toString()).toString()
    }

    private fun setUpTermsView() {
        lottoViewModel.termsAcceptanceStateLiveData.value?.let { isChecked ->
            if (isChecked) {
                termsAndConditionsCheckBox.setCheckBoxVisibility(false)
                termsAndConditionsCheckBox.setDescription(getString(R.string.client_agreement_have_accepted, getString(R.string.lotto_terms_and_conditions)))

                CommonUtils.makeTextClickable(baseActivity, R.string.lotto_client_agreement_have_accepted, getString(R.string.lotto_terms_and_conditions), object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        openTermsAndConditionsPdf()
                    }
                }, termsAndConditionsCheckBox.checkBoxTextView, android.R.color.black)
            } else {
                termsAndConditionsCheckBox.setDescription(getString(R.string.lotto_i_agree_terms_and_conditions))

                CommonUtils.makeTextClickable(baseActivity, R.string.lotto_i_agree_terms_and_conditions, getString(R.string.lotto_terms_and_conditions), object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        openTermsAndConditionsPdf()
                    }
                }, termsAndConditionsCheckBox.checkBoxTextView, android.R.color.black)
            }
            termsAndConditionsCheckBox.isChecked = isChecked
        }
    }

    private fun setUpBoard() {
        val purchaseData = lottoViewModel.purchaseData
        val ticketTitle = if (lottoViewModel.purchaseData.lottoGameType == LottoViewModel.GAME_TYPE_LOTTO) {
            if (lottoViewModel.purchaseData.playPlus2) getString(R.string.lotto_plus_two) else if (lottoViewModel.purchaseData.playPlus1) getString(R.string.lotto_plus_one) else getString(R.string.lotto)
        } else {
            if (lottoViewModel.purchaseData.playPlus1) getString(R.string.lotto_powerball_plus) else getString(R.string.lotto_powerball)
        }

        ticketView.setTicketTitle("$ticketTitle ${getString(R.string.lotto_ticket)}")
        ticketView.setTicketBoardsDescription(LottoUtils.getNoAmountTicketDescription(requireContext(), purchaseData.numberOfBoards, purchaseData.numberOfDraws, purchaseData.date))

        val boardText = if (purchaseData.numberOfBoards > 1) getString(R.string.lotto_boards) else getString(R.string.lotto_board)
        if (purchaseData.quickPickInd) {
            ticketView.setQuickPickBoard("${purchaseData.numberOfBoards} $boardText", getString(R.string.lotto_quick_pick))
        } else {
            val boardList = arrayListOf<LottoBoard>()
            lottoViewModel.purchaseData.lottoBoardNumbers.forEachIndexed { index, it ->
                val board = LottoBoard()
                board.ballList.addAll(it.lottoBoardNumbers)
                board.powerBall = it.powerBall
                board.boardLetter = LottoUtils.getCharForNumber(index)
                boardList.add(board)
            }

            ticketView.setSelectedLottoBoards(boardList)
        }
    }

    private fun setupSourceAccounts() {
        lottoSourceAccounts = lottoViewModel.getLottoSourceAccounts()

        if (lottoSourceAccounts.size > 5) {
            selectAccountNormalInputView.setSelectorViewType(SelectorType.SEARCHABLE_LONG_LIST)
        }

        selectAccountNormalInputView.setList(lottoSourceAccounts, getString(R.string.select_account_toolbar_title))

        selectAccountNormalInputView.setItemSelectionInterface {
            selectedAccount = lottoSourceAccounts[it].accountObject
            selectAccountNormalInputView.setDescription(String.format("%s %s", getString(R.string.available_balance), selectedAccount.availableBalanceFormated))
            validateAmount()
        }

        if (lottoViewModel.purchaseData.sourceAccount.isNotEmpty()) {
            selectAccountNormalInputView.selectedIndex = lottoSourceAccounts.indexOfFirst { it.accountObject.accountNumber == lottoViewModel.purchaseData.sourceAccount }
        } else if (lottoSourceAccounts.size > 0) {
            selectAccountNormalInputView.selectedIndex = 0
        }

        if (selectAccountNormalInputView.selectedIndex > -1) {
            selectedAccount = lottoSourceAccounts[selectAccountNormalInputView.selectedIndex].accountObject
            lottoViewModel.purchaseData.sourceAccount = selectedAccount.accountNumber
            selectAccountNormalInputView.setDescription(String.format("%s %s", getString(R.string.available_balance), selectedAccount.availableBalanceFormated))
            validateAmount()
        }
    }

    private fun validateAmount() {
        if (selectedAccount.availableBalance.amountValue < lottoViewModel.purchaseData.purchaseAmount) {
            selectAccountNormalInputView.setError(R.string.insufficient_balance)
        } else {
            lottoViewModel.purchaseData.sourceAccount = selectedAccount.accountNumber
            selectAccountNormalInputView.clearError()
        }
    }

    private fun setupListeners() {
        continueButton.setOnClickListener {
            preventDoubleClick(it)
            if (isValidInput()) {
                if (hasChangedTerms) {
                    lottoViewModel.termsAcceptedLiveData.observe(viewLifecycleOwner, { isTermsAccepted ->
                        if (isTermsAccepted) {
                            setUpAndPurchaseTicket()
                        } else {
                            dismissProgressDialog()
                            showGenericErrorMessage()
                        }
                        lottoViewModel.termsAcceptedLiveData.removeObservers(this)
                    })

                    lottoViewModel.acceptLottoTerms()
                } else {
                    setUpAndPurchaseTicket()
                }

                if (contactNumberNormalInputView.selectedValue != contactNumber) {
                    lottoFlowTag = if (lottoViewModel.purchaseData.quickPickInd) "QuickPickConfirmTicketScreen" else "ConfirmPurchaseTicket"
                    (activity as LottoActivity).tagLottoAndPowerBallEvent("${lottoFlowTag}_MobileNumberEdited")
                }
            }
        }
        contactNumberNormalInputView.addRequiredValidationHidingTextWatcher()

        termsAndConditionsCheckBox.setOnCheckedListener {
            contactNumberNormalInputView.hideError()
            if (termsAndConditionsCheckBox.checkBox.visibility == View.VISIBLE) {
                hasChangedTerms = true
            }
        }
    }

    private fun setUpAndPurchaseTicket() {
        val gameType = lottoViewModel.currentGameRules.gameType
        lottoViewModel.lottoPurchaseResponseLiveData = MutableLiveData()
        lottoViewModel.lottoPurchaseResponseLiveData.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(activity as BaseView, it) {
                dismissProgressDialog()
                if (BMBConstants.SUCCESS.equals(it.transactionStatus, ignoreCase = true)) {
                    navigate(LottoTicketConfirmationFragmentDirections.actionLottoTicketConfirmationFragmentToLottoPurchaseSuccessFragment())
                } else if (it.transactionMessage == "Lotto game is closed") {
                    val genericResultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                            .setTitle(getString(R.string.lotto_unable_complete_purchase, gameType))
                            .setDescription(getString(R.string.lotto_closed, gameType, lottoViewModel.serviceAvailabilityLiveData.value?.nextStartDate))
                            .setResultScreenAnimation("general_failure.json")
                            .setPrimaryButtonLabel(getString(R.string.home))
                            .setPrimaryButtonContentDescription(getString(R.string.home))
                            .build(false)
                    GenericResultScreenFragment.setPrimaryButtonOnClick {
                        navigateToHomeScreenWithoutReloadingAccounts()
                        (activity as LottoActivity).tagLottoAndPowerBallEvent("FailureScreen_DoneButtonClicked")
                    }
                    val bundle = Bundle()
                    bundle.putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties)
                    findNavController().navigate(R.id.action_lottoTicketConfirmationFragment_to_lottoGenericResultFragment, bundle)
                } else if (it.lottoLimitErrorType.equals("ONLINE", true)) {
                    performDigitalLimitUpdate()
                } else if (it.lottoLimitErrorType != "" && !it.lottoLimitErrorType.equals("ok", true)) {
                    showMessageError(getString(R.string.lotto_purchase_limit_exceeded))
                } else {
                    showGenericErrorMessage()
                }
            }
        })

        lottoViewModel.failureResponse = MutableLiveData()
        lottoViewModel.failureResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            showGenericErrorMessage()
        })

        lottoViewModel.purchaseData.cellphoneNumber = contactNumberNormalInputView.selectedValue.removeSpaces()
        lottoViewModel.purchaseData.lottoGameType = gameType
        lottoViewModel.purchaseLottoTicket()
    }

    private fun isValidInput(): Boolean {
        when {
            selectAccountNormalInputView.selectedValue.isEmpty() -> selectAccountNormalInputView.setError(getString(R.string.lotto_select_account))
            contactNumberNormalInputView.selectedValue.isEmpty() || !ValidationUtils.validatePhoneNumberInput(contactNumberNormalInputView.selectedValue) -> contactNumberNormalInputView.setError(R.string.lotto_enter_valid_number)
            !termsAndConditionsCheckBox.isChecked -> termsAndConditionsCheckBox.setErrorMessage(R.string.lotto_conditions_error_message)
            selectAccountNormalInputView.hasError() -> selectAccountNormalInputView.setError(R.string.insufficient_balance)
            else -> return true
        }
        return false
    }

    private fun performDigitalLimitUpdate() {
        DigitalLimitsHelper.checkPaymentAmount(activity as BaseActivity, Amount(lottoViewModel.purchaseData.purchaseAmount.toString()), false)

        DigitalLimitsHelper.digitalLimitState.observe(viewLifecycleOwner, { digitalLimitState ->
            if (digitalLimitState === DigitalLimitState.CHANGED || digitalLimitState === DigitalLimitState.UNCHANGED) {
                lottoViewModel.purchaseLottoTicket()
            } else {
                dismissProgressDialog()
            }
        })
    }

    private val sureCheckDelegate: SureCheckDelegate = object : SureCheckDelegate(activity) {
        override fun onSureCheckProcessed() {
            lottoViewModel.purchaseLottoTicket()
        }
    }

    private fun openTermsAndConditionsPdf() {
        BMBLogger.d(LottoTicketConfirmationFragment::class.java.canonicalName)
        val activeUserProfileLanguage = ProfileManager.getInstance().activeUserProfile?.languageCode
        if (BMBConstants.ENGLISH_LANGUAGE.toString().equals(activeUserProfileLanguage, ignoreCase = true)) {
            PdfUtil.showPDFInApp(baseActivity, LottoViewModel.LOTTO_TERMS_AND_CONDITIONS_PDF_URL_ENGLISH)
        } else {
            PdfUtil.showPDFInApp(baseActivity, LottoViewModel.LOTTO_TERMS_AND_CONDITIONS_PDF_URL_AFRIKAANS)
        }
    }
}