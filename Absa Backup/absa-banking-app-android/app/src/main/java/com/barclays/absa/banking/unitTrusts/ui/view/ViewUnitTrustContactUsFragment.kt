/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustContactUsDetailFragmentBinding
import com.barclays.absa.banking.framework.utils.EmailUtil
import com.barclays.absa.banking.shared.ItemPagerFragment
import styleguide.content.Contact

class ViewUnitTrustContactUsFragment : ItemPagerFragment() {
    private lateinit var binding: ViewUnitTrustContactUsDetailFragmentBinding
    private lateinit var callContact: Contact
    private lateinit var emailContact: Contact

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    companion object {
        fun newInstance(description: String): ViewUnitTrustContactUsFragment {
            val viewUnitTrustContactUsFragment = ViewUnitTrustContactUsFragment()
            val arguments = Bundle()
            arguments.putString(TAB_DESCRIPTION_KEY, description)
            viewUnitTrustContactUsFragment.arguments = arguments
            return viewUnitTrustContactUsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_unit_trust_contact_us_detail_fragment, container, false)
        return binding.root
    }

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailContact = Contact()
        callContact = Contact()

        callContact.contactName = getString(R.string.call_centre)
        callContact.contactNumber = getString(R.string.unit_trust_call_centre_number)
        emailContact.contactName = getString(R.string.unit_trust_email)
        emailContact.contactNumber = getString(R.string.unit_trust_email_csi)

        binding.unitTrustContactCentreContactView.setContact(callContact)
        binding.unitTrustEmailCentreContactView.setContact(emailContact)
        binding.unitTrustEmailCentreContactView.setIcon(R.drawable.ic_email_new)
        binding.unitTrustEmailCentreContactView.setOnClickListener {
            EmailUtil.email(activity, emailContact.contactName)
        }
    }
}