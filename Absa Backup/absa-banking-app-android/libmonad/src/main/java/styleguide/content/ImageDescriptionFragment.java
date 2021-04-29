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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import za.co.absa.presentation.uilib.R;
import za.co.absa.presentation.uilib.databinding.ImageDescriptionFragmentBinding;

public class ImageDescriptionFragment extends Fragment {

    private static final String PAGER_IMAGE_DESCRIPTION = "pagerImageDescription";
    private static final String PAGER_IMAGE_RESOURCE_ID = "pagerImageResource";
    private static final String PAGER_STYLE = "pagerStyle";

    private String imageDescription;
    private int resourceDrawableID;
    private int pagerStyle;
    private ImageDescriptionFragmentBinding binding;

    public ImageDescriptionFragment() {
    }

    public static ImageDescriptionFragment newInstance(@NonNull String imageDescription, int resourceDrawableID, int  style) {
        Bundle bundle = new Bundle();
        ImageDescriptionFragment imageDescriptionFragment = new ImageDescriptionFragment();
        bundle.putString(PAGER_IMAGE_DESCRIPTION, imageDescription);
        bundle.putInt(PAGER_IMAGE_RESOURCE_ID, resourceDrawableID);
        bundle.putInt(PAGER_STYLE, style);
        imageDescriptionFragment.setArguments(bundle);
        return imageDescriptionFragment;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.image_description_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageDescription = getArguments().getString(PAGER_IMAGE_DESCRIPTION);
        resourceDrawableID = getArguments().getInt(PAGER_IMAGE_RESOURCE_ID);
        pagerStyle = getArguments().getInt(PAGER_STYLE);
        populateComponents();
    }

    private void populateComponents() {
        binding.descriptionTextView.setText(imageDescription);
        binding.descriptionTextView.setTextAppearance(getContext(), pagerStyle);
        binding.imageViewImage.setImageResource(resourceDrawableID);
    }
}