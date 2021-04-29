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
 */

package com.barclays.absa.banking.linking.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.express.identificationAndVerification.GetBiometricStatusViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.manage.devices.DeviceListActivity
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import styleguide.screens.GenericResultScreenFragment

abstract class LinkingBaseFragment(@LayoutRes layout: Int) : BaseFragment(layout) {
    lateinit var linkingActivity: LinkingActivity
    protected val linkingViewModel by activityViewModels<LinkingViewModel>()
    protected val getBiometricStatusViewModel by activityViewModels<GetBiometricStatusViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        linkingActivity = context as LinkingActivity
    }

    fun cancelLinkingDialog() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.alert_dialog_header))
                .message(getString(R.string.alert_dialog_text))
                .positiveDismissListener { _: DialogInterface?, _: Int ->
                    dismissAlertDialog()
                    val welcomeIntent = Intent(activity, WelcomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(welcomeIntent)
                    linkingActivity.finish()
                })
    }

    protected fun navigateToAccountLogin() {
        Intent(baseActivity, AccountLoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
    }

    protected fun navigateToSureCheckResultScreen(@StringRes title: Int, @StringRes message: Int? = -1) {
        val genericResultScreenProperties = LinkingResultFactory(this).showFailureScreenFragment(title, message ?: -1, R.string.done) {
            if (appCacheService.isFromManageDevicesFlow()) {
                startActivity(Intent(linkingActivity, DeviceListActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            } else {
                baseActivity.finish()
            }
        }
        findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
    }
}