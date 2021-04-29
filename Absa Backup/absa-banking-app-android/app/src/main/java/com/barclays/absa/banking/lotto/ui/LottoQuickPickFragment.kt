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
import android.view.View
import androidx.core.content.ContextCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.banking.lotto.ui.LottoUtils.DATE_DISPLAY_PATTERN
import com.barclays.absa.banking.lotto.ui.LottoUtils.getDrawDates
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.lotto_quick_pick_fragment.*
import kotlinx.android.synthetic.main.step_indicator_button.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.removeCurrencyDefaultZero
import java.math.BigDecimal

class LottoQuickPickFragment : BaseFragment(R.layout.lotto_quick_pick_fragment) {

    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var currentRules: LottoGameRules

    private lateinit var boardList: SelectorList<StringItem>
    private lateinit var drawList: SelectorList<StringItem>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
        currentRules = lottoViewModel.currentGameRules
        setPurchaseData()
        loadBoardList()
    }

    private fun setPurchaseData() {
        lottoViewModel.purchaseData.lottoGameType = currentRules.gameType
        lottoViewModel.purchaseData.numberOfBoards = 2
        lottoViewModel.purchaseData.numberOfDraws = 1
        lottoViewModel.purchaseData.playPlus2 = false
        lottoViewModel.purchaseData.playPlus1 = false
    }

    private fun loadBoardList() {
        boardList = SelectorList()
        for (boardNumber in currentRules.minimumBoards..currentRules.maximumBoards) {
            val boardText = if (boardNumber == 1) "${getString(R.string.lotto_board)} ${getString(R.string.lotto_per_draw)}" else "${getString(R.string.lotto_boards)} ${getString(R.string.lotto_per_draw)}"
            boardList.add(StringItem("$boardNumber $boardText", calculateTotalAmountForBoardNumber(boardNumber)))
        }
    }

    private fun setUpCheckBoxes() {
        lottoCheckBoxView.setSubText(getString(R.string.lotto_per_board, Amount(currentRules.basePrice.toString()).toString()))
        val lottoPlus = "+${Amount(currentRules.multiplierPrice.toString())}"

        lottoPlusOneCheckBoxView.setSubText(getString(R.string.lotto_per_board, lottoPlus))

        if (currentRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            lottoCheckBoxView.setImage(ContextCompat.getDrawable(requireContext(), R.drawable.power_ball))
            lottoPlusOneCheckBoxView.setImage(ContextCompat.getDrawable(requireContext(), R.drawable.lotto_power_ball_plus))
            lottoPlusTwoCheckBoxView.visibility = View.GONE
        } else {
            lottoPlusTwoCheckBoxView.setOnCheckedListener { isChecked ->
                if (isChecked) {
                    lottoPlusOneCheckBoxView.isChecked = true
                }

                lottoViewModel.purchaseData.playPlus2 = isChecked
                totalTextView.text = calculateTotalAmount()
            }
            lottoPlusTwoCheckBoxView.setSubText(getString(R.string.lotto_per_board, lottoPlus))
            lottoPlusTwoCheckBoxView.isChecked = lottoViewModel.purchaseData.playPlus2
        }

        lottoCheckBoxView.isChecked = true
        lottoCheckBoxView.isEnabled = false

        lottoPlusOneCheckBoxView.setOnCheckedListener { isChecked ->
            if (!isChecked) {
                lottoPlusTwoCheckBoxView.isChecked = false
            }

            lottoViewModel.purchaseData.playPlus1 = isChecked
            totalTextView.text = calculateTotalAmount()
        }

        lottoPlusOneCheckBoxView.isChecked = lottoViewModel.purchaseData.playPlus1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            setToolBar(R.string.lotto_powerball_quick_pick, true)
        } else {
            setToolBar(R.string.lotto_lotto_quick_pick)
        }

        setUpCheckBoxes()
        setUpNormalInputViews()
        setUpClickListeners()
        initialiseViews()
    }

    private fun initialiseViews() {
        howManyBoardsNormalInputView.setList(boardList, getString(R.string.lotto_number_of_entries))

        howManyBoardsNormalInputView.selectedIndex = boardList.indexOfFirst { it.item?.substringBefore(" ", "1") == lottoViewModel.purchaseData.numberOfBoards.toString() }
        howManyDrawsNormalInputView.selectedIndex = drawList.indexOfFirst { it.item?.substringBefore(" ", "1") == lottoViewModel.purchaseData.numberOfDraws.toString() }

        howManyDrawsNormalInputView.selectedValue = LottoUtils.formatDrawText(drawList, howManyDrawsNormalInputView.selectedIndex)

        totalTextView.text = calculateTotalAmount()
        stepIndicatorTextView.text = getString(R.string.lotto_step_indicator_text, "1", "2", getString(R.string.lotto_place_bet))
    }

    private fun setUpClickListeners() {
        nextButton.setOnClickListener { view ->
            preventDoubleClick(view)
            if (isValidInput()) {
                boardList[howManyBoardsNormalInputView.selectedIndex].item?.let { lottoViewModel.purchaseData.numberOfBoards = it.substringBefore(" ").toInt() }
                drawList[howManyDrawsNormalInputView.selectedIndex].item?.let { lottoViewModel.purchaseData.numberOfDraws = it.substringBefore(" ").toInt() }
                lottoViewModel.purchaseData.apply {
                    playPlus1 = lottoPlusOneCheckBoxView.isChecked
                    playPlus2 = lottoPlusTwoCheckBoxView.isChecked
                    purchaseAmount = BigDecimal(totalTextView.text.toString().removeCurrencyDefaultZero())
                    quickPickInd = true
                    date = drawList[howManyDrawsNormalInputView.selectedIndex].item2.toString()
                }
                checkTermsAndNavigate()
                eventTagging()
            }
        }
    }

    private fun eventTagging() {
        val hostActivity = activity as LottoActivity
        when {
            currentRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL && lottoPlusOneCheckBoxView.isChecked -> hostActivity.tagLottoAndPowerBallEvent("PlayQuickPickScreen_PowerBallPlusBoxChecked")
            lottoPlusTwoCheckBoxView.isChecked -> hostActivity.tagLottoAndPowerBallEvent("PlayQuickPickScreen_LottoPlus2CheckboxChecked")
            lottoPlusOneCheckBoxView.isChecked -> hostActivity.tagLottoAndPowerBallEvent("PlayQuickPickScreen_LottoPlus1CheckboxChecked")
        }
    }

    private fun checkTermsAndNavigate() {
        if (lottoViewModel.termsAcceptanceStateLiveData.value == null) {
            lottoViewModel.termsAcceptanceStateLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                navigate(LottoQuickPickFragmentDirections.actionLottoQuickPickFragmentToLottoTicketConfirmationFragment())
            })

            lottoViewModel.loadTermsAcceptanceData()
        } else {
            navigate(LottoQuickPickFragmentDirections.actionLottoQuickPickFragmentToLottoTicketConfirmationFragment())
        }
    }

    private fun isValidInput(): Boolean {
        when {
            howManyBoardsNormalInputView.selectedValue.isEmpty() -> howManyBoardsNormalInputView.setError(getString(R.string.please_select))
            howManyDrawsNormalInputView.selectedValue.isEmpty() -> howManyDrawsNormalInputView.setError(getString(R.string.please_select))
            else -> return true
        }
        return false
    }

    private fun setUpNormalInputViews() {
        howManyBoardsNormalInputView.setCustomOnClickListener {
            loadBoardList()
            howManyBoardsNormalInputView.setList(boardList, getString(R.string.lotto_number_of_entries))
            howManyBoardsNormalInputView.triggerListActivity()
        }

        drawList = SelectorList()
        drawList.add(StringItem("1 " + getString(R.string.lotto_draw), DateUtils.formatDate(currentRules.nextDrawDate, DateUtils.DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN)))
        for (i in 2..currentRules.maximumNoOfDraws) {
            drawList.add(StringItem("$i ${getString(R.string.lotto_draws)}", getDrawDates(currentRules, i)))
        }

        howManyDrawsNormalInputView.setList(drawList, getString(R.string.lotto_number_of_entries))

        howManyBoardsNormalInputView.setItemSelectionInterface { index ->
            lottoViewModel.purchaseData.numberOfBoards = boardList[index].item!!.substringBefore(" ").toInt()
            totalTextView.text = calculateTotalAmount()
        }

        howManyDrawsNormalInputView.setItemSelectionInterface { index ->
            howManyDrawsNormalInputView.selectedValue = LottoUtils.formatDrawText(drawList, index)
            lottoViewModel.purchaseData.numberOfDraws = drawList[index].item!!.substringBefore(" ").toInt()
            totalTextView.text = calculateTotalAmount()
        }
    }

    private fun calculateTotalAmountForBoardNumber(numberOfBoards: Int): String {
        val totalAmount = LottoUtils.calculateTotalAmount(lottoViewModel.currentGameRules, lottoViewModel.purchaseData, numberOfBoards)
        return totalAmount.toString()
    }

    private fun calculateTotalAmount(): String {
        val totalAmount = LottoUtils.calculateTotalAmount(lottoViewModel.currentGameRules, lottoViewModel.purchaseData)
        lottoViewModel.purchaseData.purchaseAmount = totalAmount.amountValue
        return totalAmount.toString()
    }
}