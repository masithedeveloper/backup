/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.recognition.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity

class BranchRecognitionActivity : BaseActivity(R.layout.branch_rating_activity), BranchRecognitionView {

    private lateinit var appbarConfig: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragmentController()
    }

    private fun setupFragmentController() {
        navController = Navigation.findNavController(this, R.id.branchFeedbackNavHostFragment)
        appbarConfig = AppBarConfiguration(navController.graph)
    }

    override fun setToolbarTitle(@StringRes toolbarTitle: Int) = setToolBarBack(toolbarTitle)

    override fun navigateToAccountTypeOpenedFragment() =
            findNavController(R.id.navigationHostFragment).navigate(R.id.branchAccountOpenedFragment)

    override fun hideAppToolbar() {
        supportActionBar?.hide()
    }

    override fun showAppToolbar() {
        supportActionBar?.show()
    }

    override fun onResume() {
        super.onResume()
        if (appCacheService.getSecureHomePageObject() != null) {
            if (navController.currentDestination?.id == R.id.branchRecognitionResultScreenFragment) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    supportFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun launchSettingsActivity() {
        val uri = Uri.parse("package:$packageName")
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        startActivity(settingsIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        currentFragment?.childFragmentManager?.fragments?.firstOrNull()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun dismissDialog() = dismissProgressDialog()
}
