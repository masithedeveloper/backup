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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankStartBankingFragmentBinding;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class NewToBankStartBankingFragment extends Fragment {
    private String nextLottieAnimationFile;
    private final String ANIMATION_FILE_PREFIX = "new_to_bank_animation_";
    private final String ANIMATION_FILE_EXTENSION = ".json";
    private NewToBankView newToBankView;
    private NewToBankStartBankingFragmentBinding binding;

    public NewToBankStartBankingFragment() {
    }

    public static NewToBankStartBankingFragment newInstance() {
        return new NewToBankStartBankingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.new_to_bank_start_banking_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();

        if (newToBankView != null) {
            newToBankView.setToolbarBackTitle("");
        }
        initViews();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        populateAnimatedPages();
    }

    private void initViews() {
        binding.letsDoThisButton.setOnClickListener(v -> newToBankView.navigateToChooseAccountScreen());
        binding.absaWebsiteDescriptionView.setOnClickListener(v -> newToBankView.navigateToAbsaWebsite());

        String absaLink = getString(R.string.new_to_bank_absa_website_lower);
        SpannableString absaLinkUnderlined = new SpannableString(absaLink);
        absaLinkUnderlined.setSpan(new UnderlineSpan(), 0, absaLink.length(), 0);

        binding.absaWebsiteDescriptionView.setDescription(absaLinkUnderlined);
        populateAnimatedPages();

        binding.titleCenteredTitleView.setMaxLines(2);
    }

    @SuppressWarnings("unchecked")
    private void populateAnimatedPages() {
        LinkedHashMap<String, Map.Entry<String, Boolean>> viewPagerContent = new LinkedHashMap<>();

        String[] welcomeContentDescriptions = getResources().getStringArray(R.array.new_to_bank_welcome_screen_items);
        String content;
        for (int i = 0; i < welcomeContentDescriptions.length; i++) {
            nextLottieAnimationFile = ANIMATION_FILE_PREFIX + (i + 1) + ANIMATION_FILE_EXTENSION;
            content = welcomeContentDescriptions[i];
            viewPagerContent.put(content, getAnimationValues(nextLottieAnimationFile, false));
        }
        binding.welcomeViewPager.setLottieScale(0.85f);
        binding.welcomeViewPager.populateAnimatedSimpleCarouselPage(viewPagerContent, R.style.LargeTextRegularDark);
    }

    @SuppressWarnings("unchecked")
    private AbstractMap.SimpleEntry getAnimationValues(String animation, boolean loop) {
        return new AbstractMap.SimpleEntry(animation, loop);
    }
}