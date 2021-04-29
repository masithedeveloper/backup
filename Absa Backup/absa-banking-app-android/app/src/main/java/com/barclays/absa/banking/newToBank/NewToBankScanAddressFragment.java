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

package com.barclays.absa.banking.newToBank;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankScanAddressFragmentBinding;
import com.thisisme.sdk.fragments.GenericCaptureFragment;
import com.thisisme.sdk.fragments.GenericCaptureFragmentEventListener;

public class NewToBankScanAddressFragment extends Fragment {

    private NewToBankView newToBankView;
    private GenericCaptureFragment mCaptureFragment = null;
    private NewToBankScanAddressFragmentBinding binding;

    public NewToBankScanAddressFragment() {
        // Default - Empty constructor
    }

    public static NewToBankScanAddressFragment newInstance() {
        return new NewToBankScanAddressFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.new_to_bank_scan_address_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.hideToolbar();

        mCaptureFragment = (GenericCaptureFragment) getChildFragmentManager().findFragmentById(R.id.viewAddressFragment);

        GenericCaptureFragmentEventListener listener = new GenericCaptureFragmentEventListener() {
            @Override
            public void onCameraCapture(Bitmap photo) {
                newToBankView.uploadProofOfAddress(photo);
            }

            @Override
            public void onNeedsPermission() {

            }
        };
        mCaptureFragment.setEventListener(listener);

        binding.takePictureImageView.setOnClickListener(view1 -> {
            binding.takePictureImageView.setVisibility(View.GONE);
            mCaptureFragment.snap();
        });
    }
}
