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
import com.barclays.absa.banking.lotto.ui.LottoUtils.getDrawDates
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.lotto_play_the_lotto_fragment.*
import kotlinx.android.synthetic.main.step_indicator_button.*
import kotlinx.android.synthetic.main.step_indicator_button.view.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class LottoPlayTheLottoFragment : BaseFragment(R.layout.lotto_play_the_lotto_fragment) {

    private lateinit var drawList: SelectorList<StringItem>
    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var currentRules: LottoGameRules

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
        currentRules = lottoViewModel.currentGameRules
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepIndicatorView.stepIndicatorTextView.text = getString(R.string.lotto_step_indicator_text, "2", "3", getString(R.string.lotto_choose_additional))

        if (currentRules.gameType == LottoViewModel.GAME_TYPE_LOTTO) {
            setToolBar(getString(R.string.lotto_play_the_lotto))
        } else {
            setToolBar(getString(R.string.lotto_play_powerball))
        }

        setUpCheckBoxes()
        setUpDrawInputView()

        totalTextView.text = calculateTotalAmount()

        nextButton.setOnClickListener {
            if (isValidInput()) {
                lottoViewModel.purchaseData.quickPickInd = false
                lottoViewModel.purchaseData.date = drawList[howManyDrawsNormalInputView.selectedIndex].item2.toString()
                checkTermsAndNavigate()
                eventTagging()
            }
        }
    }

    private fun eventTagging() {
        val hostActivity = activity as LottoActivity
        when {
            currentRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL && lottoPlusOneCheckBoxView.isChecked -> hostActivity.tagLottoAndPowerBallEvent("NewTicketAdditionalGamesScreen_PowerBallPlusBoxChecked")
            lottoPlusTwoCheckBoxView.isChecked -> hostActivity.tagLottoAndPowerBallEvent("NewTicketAdditionalGamesScreen_LottoPlus2CheckboxChecked")
            lottoPlusOneCheckBoxView.isChecked -> hostActivity.tagLottoAndPowerBallEvent("NewTicketAdditionalGamesScreen_LottoPlus1CheckboxChecked")
        }
    }

    private fun checkTermsAndNavigate() {
        if (lottoViewModel.termsAcceptanceStateLiveData.value == null) {
            lottoViewModel.termsAcceptanceStateLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                navigate(LottoPlayTheLottoFragmentDirections.actionLottoPlayTheLottoFragmentToLottoTicketConfirmationFragment())
            })

            lottoViewModel.loadTermsAcceptanceData()
        } else {
            navigate(LottoPlayTheLottoFragmentDirections.actionLottoPlayTheLottoFragmentToLottoTicketConfirmationFragment())
        }
    }

    private fun setUpDrawInputView() {
        drawList = SelectorList()
        drawList.add(StringItem("1 " + getString(R.string.lotto_draw), DateUtils.formatDate(currentRules.nextDrawDate, DateUtils.DASHED_DATE_PATTERN, LottoUtils.DATE_DISPLAY_PATTERN)))
        for (i in 2..currentRules.maximumNoOfDraws) {
            drawList.add(StringItem("$i ${getString(R.string.lotto_draws)}", getDrawDates(currentRules, i)))
        }

        howManyDrawsNormalInputView.setList(drawList, getString(R.string.lotto_number_of_entries))
        howManyDrawsNormalInputView.selectedIndex = drawList.indexOfFirst { it.item?.substringBefore(" ", "1") == lottoViewModel.purchaseData.numberOfDraws.toString() }
        howManyDrawsNormalInputView.selectedValue = LottoUtils.formatDrawText(drawList, howManyDrawsNormalInputView.selectedIndex)
        lottoViewModel.purchaseData.date = getDrawDates(currentRules, howManyDrawsNormalInputView.selectedIndex)

        howManyDrawsNormalInputView.setItemSelectionInterface { index ->
            lottoViewModel.purchaseData.date = getDrawDates(currentRules, index)
            drawList[index].item?.let { lottoViewModel.purchaseData.numberOfDraws = it.substringBefore(" ").toInt() }
            howManyDrawsNormalInputView.selectedValue = LottoUtils.formatDrawText(drawList, index)
            totalTextView?.text = calculateTotalAmount()
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

    private fun isValidInput(): Boolean {
        if (howManyDrawsNormalInputView.selectedValue.isEmpty()) {
            howManyDrawsNormalInputView.setError(getString(R.string.please_select))
            return false
        }
        return true
    }

    private fun calculateTotalAmount(): String {
        val totalAmount = LottoUtils.calculateTotalAmount(lottoViewModel.currentGameRules, lottoViewModel.purchaseData)
        lottoViewModel.purchaseData.purchaseAmount = totalAmount.amountValue
        return totalAmount.toString()
    }
}