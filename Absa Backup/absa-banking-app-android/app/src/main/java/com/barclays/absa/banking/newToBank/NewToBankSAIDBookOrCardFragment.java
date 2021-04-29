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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankSaIdBookOrCardFragmentBinding;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

public class NewToBankSAIDBookOrCardFragment extends Fragment {

    private NewToBankSaIdBookOrCardFragmentBinding binding;
    private NewToBankView newToBankView;

    public NewToBankSAIDBookOrCardFragment() {
    }

    public static NewToBankSAIDBookOrCardFragment newInstance() {
        return new NewToBankSAIDBookOrCardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.new_to_bank_sa_id_book_or_card_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();

        if (newToBankView != null) {
            newToBankView.showToolbar();
            newToBankView.showProgressIndicator();
            newToBankView.setToolbarTitle(getString(R.string.new_to_bank_who_you_are));
            newToBankView.trackCurrentFragment(NewToBankConstants.ID_PHOTO_INSTRUCTIONS);
            if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_IDBookOrCardFragment_ScreenDisplayed");
            }
        }

        adjustPadding();
        populateRadioView();
        setUpComponentListeners();
    }

    private void adjustPadding() {
        binding.holdIDOptionActionButtonView.removeTopAndBottomMargins();
        binding.placeIDOptionActionButtonView.removeTopAndBottomMargins();

        binding.holdIDOptionActionButtonView.setClickable(false);
        binding.placeIDOptionActionButtonView.setClickable(false);

        binding.holdIDOptionActionButtonView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.small_space), 0, 0);
        binding.placeIDOptionActionButtonView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.small_space), 0, 0);
    }

    @SuppressWarnings("unchecked")
    private void populateRadioView() {
        SelectorList<StringItem> selectorList = new SelectorList<>();
        selectorList.add(new StringItem(getString(R.string.new_to_bank_sa_id_book)));
        selectorList.add(new StringItem(getString(R.string.new_to_bank_sa_id_card)));
        binding.idOptionsRadioButtonView.setDataSource(selectorList);
        binding.idOptionsRadioButtonView.setItemCheckedInterface(index -> binding.nextButton.setEnabled(true));
    }

    private void setUpComponentListeners() {
        binding.nextButton.setOnClickListener(v -> {
            if (binding.idOptionsRadioButtonView.getSelectedIndex() == 0) {
                newToBankView.navigateToScanIdBookFragment();
            } else {
                newToBankView.navigateToScanIdCardActivity();
            }
        });
    }
}