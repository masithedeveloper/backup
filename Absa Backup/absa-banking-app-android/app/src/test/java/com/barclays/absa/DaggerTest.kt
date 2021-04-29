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
 */

package com.barclays.absa

import android.app.Application
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.ApplicationComponent
import com.barclays.absa.banking.framework.dagger.DaggerApplicationComponent
import com.barclays.absa.banking.framework.dagger.ServiceModule
import org.junit.After
import org.mockito.Mockito.mock

abstract class DaggerTest {

    private val applicationComponent: ApplicationComponent = getApplicationComponent()

    private fun getApplicationComponent(): ApplicationComponent {
        val application = mock(Application::class.java)
        BMBApplication.applicationComponent = DaggerApplicationComponent.builder().serviceModule(ServiceModule(application)).build()
        return BMBApplication.applicationComponent
    }

    @After
    open fun closeReference() {
        BMBApplication.applicationComponent = null
    }
}