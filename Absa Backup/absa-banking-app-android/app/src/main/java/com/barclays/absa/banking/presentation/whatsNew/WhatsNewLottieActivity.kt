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
package com.barclays.absa.banking.presentation.whatsNew

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.WhatsNewLottieActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.SessionManager
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.home.ui.HomeContainerActivity
import com.barclays.absa.banking.presentation.shared.LottieCarouselViewPager
import com.barclays.absa.banking.presentation.utils.ToolBarUtils.setToolBarIconToClose
import com.barclays.absa.utils.SharedPreferenceService
import java.util.*

class WhatsNewLottieActivity : BaseActivity() {

    private lateinit var viewPager: LottieCarouselViewPager
    private lateinit var binding: WhatsNewLottieActivityBinding
    private lateinit var whatsNewScreens: List<WhatsNewPage>
    private val whatsNewInfo = LinkedHashMap<String, String>()

    companion object {
        const val IS_FROM_LOGIN = "isFromLogin"
    }

    private val carouselCallback: LottieCarouselViewPager.CarouselCallback = object : LottieCarouselViewPager.CarouselCallback() {
        override fun onPageChange(outPage: Int, inPage: Int) {
            binding.lottieAnimationView.apply {
                if (whatsNewScreens.isNotEmpty()) {
                    speed = 1f
                    setAnimation(whatsNewScreens[inPage - 1].animation)
                    playAnimation()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whatsNewScreens = WhatsNewHelper.getEnabledWhatsScreens()
        startSession()

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.whats_new_lottie_activity, null, false)
        setContentView(binding.root)

        binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        binding.lottieAnimationView.enableMergePathsForKitKatAndAbove(true)

        viewPager = binding.whatsNewViewPager

        mScreenName = BMBConstants.WHATS_NEW_CONST
        mSiteSection = BMBConstants.HOME_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.WHATS_NEW_CONST, BMBConstants.HOME_CONST, BMBConstants.TRUE_CONST)

        setLottieScale()
    }

    private fun setLottieScale() {
        var scale = resources.displayMetrics.density / 3f
        if (scale > 0.9f) {
            scale = 0.9f
        }
        binding.lottieAnimationView.scale = scale
    }

    private fun startSession() {
        val app = BMBApplication.getInstance()
        app.userLoggedInStatus = true
        if (!SessionManager.isSessionStarted) {
            SessionManager.startSession()
        }
        app.setCurrentTime(System.currentTimeMillis())
    }

    private fun updateSharedPreferences() {
        SharedPreferenceService.setLastLoginVersion()
        SharedPreferenceService.setFirstLoginStatus(false)
    }

    override fun onStart() {
        super.onStart()
        setToolBarBack("") { navigateToHomeContainer() }
        setToolBarIconToClose(this)
        binding.okGotItButton.setOnClickListener { navigateToHomeContainer() }
        displayWhatsNew()
    }

    override fun onBackPressed() {
        navigateToHomeContainer()
    }

    private fun goToHomeScreen() {
        preventDoubleClick(binding.okGotItButton)
        updateSharedPreferences()
        showProgressDialog()
        val currentShortcutId = intent.getStringExtra("shortcut")
        if (currentShortcutId == null) {
            loadAccountsAndGoHome()
        } else {
            loadAccountsAndGoHomeWithShortcut(currentShortcutId)
        }
    }

    private fun navigateToHomeContainer() {
        if (intent.getBooleanExtra(IS_FROM_LOGIN, false)) {
            goToHomeScreen()
        } else {
            updateSharedPreferences()
            val secureHomeObject = appCacheService.getSecureHomePageObject()
            startActivity(Intent(this, HomeContainerActivity::class.java).apply {
                putExtra(AppConstants.RESULT, secureHomeObject)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
    }

    private fun displayWhatsNew() {
        whatsNewScreens.forEach { whatsNewPage ->
            binding.lottieAnimationView.setAnimation(whatsNewPage.animation)
            val heading: String = getString(whatsNewPage.title)
            val content: String = getString(whatsNewPage.content)
            whatsNewInfo[heading] = content
        }

        if (whatsNewScreens.isNotEmpty()) {
            binding.lottieAnimationView.setAnimation(whatsNewScreens[0].animation)
            binding.lottieAnimationView.playAnimation()
        }

        viewPager.populateLottieCarouselPager(whatsNewInfo, true)
        viewPager.addCarouselCallback(carouselCallback)
    }
}