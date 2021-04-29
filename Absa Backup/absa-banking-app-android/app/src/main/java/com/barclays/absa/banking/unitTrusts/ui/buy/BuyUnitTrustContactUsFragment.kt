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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.utils.EmailUtil
import kotlinx.android.synthetic.main.buy_unit_contact_us_fragment.*
import styleguide.content.Contact
import styleguide.utils.extensions.toTitleCase

class BuyUnitTrustContactUsFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_contact_us_fragment) {

    private lateinit var callContact: Contact
    private lateinit var financialAdviserContact: Contact
    private lateinit var emailContact: Contact

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.buy_unit_trust_contact_details).toTitleCase())
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Learn_Contact")
        emailContact = Contact()
        callContact = Contact()
        financialAdviserContact = Contact()

        callContact.apply {
            contactName = getString(R.string.call_centre)
            contactNumber = getString(R.string.unit_trust_call_centre_number)
        }

        financialAdviserContact.apply {
            contactName = getString(R.string.buy_unit_trust_contact_financial_advisor)
            contactNumber = getString(R.string.buy_unit_trust_financial_advisor_number)
        }

        emailContact.apply {
            contactName = getString(R.string.unit_trust_email_csi)
            contactNumber = getString(R.string.unit_trust_email)
        }

        unitTrustContactCentreContactView.setContact(callContact)
        finicialAdvicerCentreContactView.setContact(financialAdviserContact)
        unitTrustEmailCentreContactView.apply {
            setContact(emailContact)
            setIcon(R.drawable.ic_email_new)
            setOnClickListener { EmailUtil.email(activity, emailContact.contactName) }
        }
    }
}