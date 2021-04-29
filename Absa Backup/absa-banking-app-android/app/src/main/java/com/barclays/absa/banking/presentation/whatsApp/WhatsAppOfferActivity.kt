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
package com.barclays.absa.banking.presentation.whatsApp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants.AFRIKAANS_LANGUAGE
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.PdfUtil
import com.barclays.absa.banking.shared.AlertDialogProperties.Builder
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.activity_whats_app_offer.*

class WhatsAppOfferActivity : BaseActivity(R.layout.activity_whats_app_offer) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.whats_chat_heading)

        val activeUserProfileLanguage = ProfileManager.getInstance().activeUserProfile?.languageCode
        whatsAppDisclaimerTextView.visibility = if (AFRIKAANS_LANGUAGE.toString().equals(activeUserProfileLanguage, ignoreCase = true)) View.VISIBLE else View.GONE

        addChatBankingButton.setOnClickListener {
            if (isWhatsAppInstalled()) {
                navigateOnWhatsAppInstalled()
            } else {
                BaseAlertDialog.showAlertDialog(Builder().message(getString(R.string.please_install_whatsapp)).build())
            }
        }
        moreInformationView.setOnClickListener { openFindOutMorePdf() }
    }

    private fun openFindOutMorePdf() {
        val chatBankingUri = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/Offers/terms-and-conditions/FAQs-ChatBanking.pdf"
        PdfUtil.showPDFInApp(this, chatBankingUri)
    }

    private fun isWhatsAppInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun navigateOnWhatsAppInstalled() {
        val contactName = getString(R.string.absa_whats_app_contact)
        val contactNumber = getString(R.string.support_contact_number)
        startActivity(Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.PHONE, contactNumber)
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
            putExtra(ContactsContract.Intents.Insert.NAME, contactName)
        })
    }
}