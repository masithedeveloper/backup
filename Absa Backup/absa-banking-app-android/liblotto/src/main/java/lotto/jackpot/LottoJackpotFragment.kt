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
package lotto.jackpot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import liblotto.lotto.R

class LottoJackpotFragment : Fragment() {

    companion object {
        const val JACKPOT_INFO = "jackpotAmount"

        fun newInstance(jackpotInfo: JackpotInfo): LottoJackpotFragment {
            val arguments = Bundle()
            arguments.putParcelable(JACKPOT_INFO, jackpotInfo)
            val fragment = LottoJackpotFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LottoJackpotView(inflater.context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            bundle.getParcelable<JackpotInfo>(JACKPOT_INFO)?.let {
                val jackpotInfo = it
                val lottoJackpotView = view as LottoJackpotView
                lottoJackpotView.setJackpotAmount(jackpotInfo.jackpot)
                val drawableToUse = when (jackpotInfo.drawType) {
                    DRAW_TYPE_LOTTO -> R.drawable.lotto
                    DRAW_TYPE_LOTTO_PLUS1 -> R.drawable.lotto_plus_1
                    DRAW_TYPE_LOTTO_PLUS2 -> R.drawable.lotto_plus_2
                    DRAW_TYPE_POWERBALL -> R.drawable.power_ball
                    DRAW_TYPE_POWERBALL_PLUS -> R.drawable.lotto_power_ball_plus
                    else -> R.drawable.lotto
                }
                lottoJackpotView.setLottoBadgeImageView(drawableToUse)
            }
        }
    }
}