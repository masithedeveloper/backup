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

package styleguide.content;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.absa.presentation.uilib.R;
import za.co.absa.presentation.uilib.databinding.ImageTextCarouselFragmentBinding;

public class ExtendedCarouselFragment extends Fragment {

    private static final String CAROUSEL_IMAGE = "carouselImage";
    private static final String CAROUSEL_TEXT_HEADER = "carouselTextHeader";
    private static final String CAROUSEL_TEXT_CONTENT = "carouselTextContent";

    private ImageTextCarouselFragmentBinding binding;

    public ExtendedCarouselFragment() {
    }

    public static ExtendedCarouselFragment newInstance(String textHeader, String textContent, int imageResourceId) {
        Bundle args = new Bundle();
        ExtendedCarouselFragment extendedCarouselFragment = new ExtendedCarouselFragment();
        args.putInt(CAROUSEL_IMAGE, imageResourceId);
        args.putString(CAROUSEL_TEXT_HEADER, textHeader);
        args.putString(CAROUSEL_TEXT_CONTENT, textContent);
        extendedCarouselFragment.setArguments(args);
        return extendedCarouselFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.image_text_carousel_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int carouselImage = getArguments().getInt(CAROUSEL_IMAGE);
        String carouselTextHeader = getArguments().getString(CAROUSEL_TEXT_HEADER);
        String carouselTextContent = getArguments().getString(CAROUSEL_TEXT_CONTENT);
        populateComponents(carouselImage, carouselTextHeader, carouselTextContent);

    }

    private void populateComponents(int carouselImage, String carouselTextHeader, String carouselTextContent) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float ratio = ((float) metrics.heightPixels / (float) metrics.widthPixels);

        final double REGULAR_DISPLAY_RATIO = 1.6;
        int headerStyle = ratio > REGULAR_DISPLAY_RATIO ? R.style.TitleTextBoldDark : R.style.HeadingTextMediumBoldDark;
        int contentStyle = ratio > REGULAR_DISPLAY_RATIO ? R.style.LargeTextRegularDark : R.style.NormalTextRegularDark;

        binding.imageViewHeader.setImageResource(carouselImage);

        binding.descriptionHeader.setText(carouselTextHeader);
        binding.descriptionHeader.setTextAppearance(getContext(), headerStyle);

        binding.descriptionContent.setText(carouselTextContent);
        binding.descriptionContent.setTextAppearance(getContext(), contentStyle);
    }
}