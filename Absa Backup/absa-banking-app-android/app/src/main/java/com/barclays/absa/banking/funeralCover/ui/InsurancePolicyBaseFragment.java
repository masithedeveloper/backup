/*
 * Copyright (c) 2019 Barclays Bank Plc, All Rights Reserved.
 *
 * This code is confidential to Barclays Bank Plc and shall not be disclosed
 * outside the Bank without the prior written permission of the Director of
 * CIO
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Barclays Bank
 * Plc.
 */

package com.barclays.absa.banking.funeralCover.ui;

import androidx.databinding.ViewDataBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.app.BMBApplication;

import org.jetbrains.annotations.NotNull;

public abstract class InsurancePolicyBaseFragment<T extends ViewDataBinding> extends AbsaBaseFragment<T> {
    @NotNull
    public InsuranceManagePaymentDetailsActivity getBaseActivity() {
        InsuranceManagePaymentDetailsActivity baseActivity = (InsuranceManagePaymentDetailsActivity) getActivity();
        return baseActivity != null ? baseActivity : (InsuranceManagePaymentDetailsActivity) BMBApplication.getInstance().getTopMostActivity();
    }
}
