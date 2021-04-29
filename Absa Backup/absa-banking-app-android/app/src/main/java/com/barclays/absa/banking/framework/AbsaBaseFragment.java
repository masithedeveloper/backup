/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.barclays.absa.integration.DeviceProfilingInteractor;

import org.jetbrains.annotations.NotNull;

public abstract class AbsaBaseFragment<T extends ViewDataBinding> extends BaseFragment {

    protected abstract int getLayoutResourceId();

    protected T binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), container, false);
        return binding.getRoot();
    }

    @NotNull
    public DeviceProfilingInteractor getDeviceProfilingInteractor() {
        return getBaseActivity().getDeviceProfilingInteractor();
    }

    @Override
    public void showMessage(String title, String message, DialogInterface.OnClickListener onDismissListener) {
        getBaseActivity().showMessage(title, message, onDismissListener);
    }

}