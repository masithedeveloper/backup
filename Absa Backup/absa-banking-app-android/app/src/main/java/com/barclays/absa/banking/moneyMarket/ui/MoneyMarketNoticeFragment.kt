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
package com.barclays.absa.banking.moneyMarket.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MoneyMarketNoticeFragmentBinding
import com.barclays.absa.banking.express.configurationsRetrieval.ConfigurationCategory
import com.barclays.absa.banking.express.configurationsRetrieval.ConfigurationsRetrievalViewModel
import com.barclays.absa.utils.extensions.viewBinding

class MoneyMarketNoticeFragment : MoneyMarketBaseFragment(R.layout.money_market_notice_fragment) {
    private val binding by viewBinding(MoneyMarketNoticeFragmentBinding::bind)
    private val configurationsRetrievalViewModel by viewModels<ConfigurationsRetrievalViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.money_market_fund_closure)
        binding.noticeTextView.text = getString(R.string.money_market_notice_content, getInvestmentAccountType())
        binding.acknowledgeNoticeCheckBoxView.setOnCheckedListener {
            if (it) {
                ObjectAnimator.ofInt(binding.scrollView, "scrollY", binding.scrollView.bottom).setDuration(250).start()
            }
            binding.acknowledgeNoticeCheckBoxView.clearError()
        }
        binding.nextButton.setOnClickListener {
            if (binding.acknowledgeNoticeCheckBoxView.isChecked) {
                navigate(MoneyMarketNoticeFragmentDirections.actionMoneyMarketNoticeFragmentToMoneyMarketChooseProductFragment())
            } else {
                binding.acknowledgeNoticeCheckBoxView.setErrorMessage(getString(R.string.money_market_withdraw_funds_closure_error))
            }
        }
        binding.moreInfoActionButtonView.setOnClickListener {
            configurationsRetrievalViewModel.fetchSingleConfigValue(ConfigurationCategory.XOB_ORBIT.name, "MORE_INFORMATION_URL")
            configurationsRetrievalViewModel.configSingleLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(it.configDetailValue.configValue))
                startActivity(intent)
            })
        }
        setUpObserver()
    }

    private fun setUpObserver() {
        moneyMarketViewModel.moneyMarketLogActionLiveData.observe(viewLifecycleOwner, {
            navigate(MoneyMarketNoticeFragmentDirections.actionMoneyMarketNoticeFragmentToMoneyMarketChooseProductFragment())
            moneyMarketViewModel.moneyMarketFlowModel.termsAndConditionsCompleted = true
        })
    }
}