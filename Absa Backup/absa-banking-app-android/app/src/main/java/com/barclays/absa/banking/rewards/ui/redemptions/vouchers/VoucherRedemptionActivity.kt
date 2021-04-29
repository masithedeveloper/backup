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
package com.barclays.absa.banking.rewards.ui.redemptions.vouchers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedCellphoneNumber

class VoucherRedemptionActivity : BaseActivity(), VoucherRedemptionFragment.OnProceedCallback, VoucherRedemptionConfirmationView {

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()
    private val voucherRedemptionFragment = VoucherRedemptionFragment.newInstance()
    private val voucherRedemptionConfirmationFragment = VoucherRedemptionConfirmationFragment.newInstance()

    companion object {
        private const val REQUEST_SELECT_PHONE_NUMBER = 1
        private const val COUNTRY_CODE_PREFIX = "27"
        private const val COUNTRY_CODE_PREFIX_WITH_PLUS = "+27"
        private const val REPLACEMENT_DIGIT = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voucher_redemption_activity)
        startFragment(voucherRedemptionFragment)
    }

    override fun onNext() {
        startFragment(voucherRedemptionConfirmationFragment)
    }

    override fun showFailureScreen() {
        hideToolBar()
        val onClickListener = View.OnClickListener { finish() }
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.redeem_voucher_error_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)

        val genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, true, onClickListener, null)
        startFragment(genericResultScreenFragment)
    }

    override fun showSuccessScreen() {
        hideToolBar()
        val onClickListener = View.OnClickListener { finish() }
        val voucherRedemption = rewardsCacheService.getRedeemVoucherInfo() ?: VoucherRedemptionInfo()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setDescription(String.format(getString(R.string.redeem_voucher_success_message, voucherRedemption.cellNumber.toFormattedCellphoneNumber())))
                .setTitle(getString(R.string.redeem_voucher_success_status))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        val genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, true, onClickListener, null)
        startFragment(genericResultScreenFragment)
    }

    fun startFragment(fragment: Fragment) {
        startFragment(fragment, R.id.rewardsVouchersContentView, true, AnimationType.FADE, true, fragment.javaClass.name)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1 || supportFragmentManager.backStackEntryCount == 3) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                val contactUri: Uri = data.data!!
                val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        var cellNumber: String = cursor.getString(numberIndex)
                        if (cellNumber.startsWith(COUNTRY_CODE_PREFIX)) {
                            cellNumber = cellNumber.replaceFirst(COUNTRY_CODE_PREFIX, REPLACEMENT_DIGIT)
                        } else {
                            if (cellNumber.startsWith(COUNTRY_CODE_PREFIX_WITH_PLUS)) {
                                cellNumber = cellNumber.replaceFirst(COUNTRY_CODE_PREFIX_WITH_PLUS, REPLACEMENT_DIGIT)
                            }
                        }
                        if (cellNumber.contains(" ")) {
                            cellNumber = cellNumber.replace(" ", "")
                        }
                        voucherRedemptionFragment.onCellNumberResult(cellNumber)
                    }
                }
            }
        }
    }
}