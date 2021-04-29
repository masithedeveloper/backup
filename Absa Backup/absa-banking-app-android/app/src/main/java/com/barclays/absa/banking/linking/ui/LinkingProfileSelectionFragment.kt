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
package com.barclays.absa.banking.linking.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LinkingProfileSelectionFragmentBinding
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.ItemSelectionInterface
import styleguide.screens.GenericResultScreenFragment

class LinkingProfileSelectionFragment : LinkingBaseFragment(R.layout.linking_profile_selection_fragment), ItemSelectionInterface, OnBackPressedInterface {
    private lateinit var linkingSureCheckHandler: LinkingSureCheckHandler
    private val binding by viewBinding(LinkingProfileSelectionFragmentBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        linkingSureCheckHandler = LinkingSureCheckHandler(this, linkingViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarNoBack(R.string.linking_choose_a_profile)
        val genericResultScreenProperties = LinkingResultFactory(this).showFailureForNoActiveAccounts()
        when {
            linkingViewModel.linkedProfilesList.isEmpty() && (linkingViewModel.hasDigitalProfile || linkingViewModel.hasCIFProfile) -> {
                navigateToAccountLogin()
            }
            linkingViewModel.linkedProfilesList.isEmpty() -> {
                findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
            }
            else -> {
                binding.chooseDeviceRecyclerView.adapter = linkingViewModel.linkedProfiles.value?.let { LinkingChooseAccountAdapter(it, this) }
            }
        }

        binding.iDontSeeMyAccountButton.setOnClickListener {
            appCacheService.clearAllIdentificationAndVerificationCacheValues()
            navigateToAccountLogin()
        }
    }

    override fun onItemClicked(index: Int) {
        linkingSureCheckHandler.requestSureCheckForLinking(index)
    }

    override fun onBackPressed(): Boolean {
        cancelLinkingDialog()
        return true
    }
}