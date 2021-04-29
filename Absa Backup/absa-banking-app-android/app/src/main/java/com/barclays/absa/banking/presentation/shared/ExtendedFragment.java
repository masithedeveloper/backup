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
package com.barclays.absa.banking.presentation.shared;

import androidx.databinding.ViewDataBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.utils.BMBLogger;

public abstract class ExtendedFragment<T extends ViewDataBinding> extends AbsaBaseFragment<T> {
    public abstract String getToolbarTitle();

    @Override
    public void onResume() {
        super.onResume();
        BMBLogger.d("x-class:", getClass().getSimpleName());
    }
}