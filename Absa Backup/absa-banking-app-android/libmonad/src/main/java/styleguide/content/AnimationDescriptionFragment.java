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

import android.animation.ValueAnimator;
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
import za.co.absa.presentation.uilib.databinding.AnimationDescriptionFragmentBinding;

public class AnimationDescriptionFragment extends Fragment {

    private static final String PAGER_ANIMATION_DESCRIPTION = "pagerImageDescription";
    private static final String PAGER_ANIMATION_RESOURCE_ID = "pagerAnimationResource";
    private static final String PAGER_STYLE = "pagerStyle";
    private static final String PAGER_LOOP_ANIMATION = "pagerLoopAnimation";
    private static final String LOTTIE_SCALE = "lottieScale";

    private String imageDescription;
    private String animationResource;
    private boolean loopAnimation;
    private int pagerStyle;
    private AnimationDescriptionFragmentBinding binding;

    public AnimationDescriptionFragment() {
    }

    public static AnimationDescriptionFragment newInstance(@NonNull String animationDescription, String animationResource, boolean loopAnimation, int style, float lottieScale) {
        Bundle bundle = new Bundle();
        AnimationDescriptionFragment animationDescriptionFragment = new AnimationDescriptionFragment();
        bundle.putString(PAGER_ANIMATION_DESCRIPTION, animationDescription);
        bundle.putString(PAGER_ANIMATION_RESOURCE_ID, animationResource);
        bundle.putBoolean(PAGER_LOOP_ANIMATION, loopAnimation);
        bundle.putInt(PAGER_STYLE, style);
        bundle.putFloat(LOTTIE_SCALE, lottieScale);
        animationDescriptionFragment.setArguments(bundle);
        return animationDescriptionFragment;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.animation_description_fragment, container, false);
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            imageDescription = getArguments().getString(PAGER_ANIMATION_DESCRIPTION);
            animationResource = getArguments().getString(PAGER_ANIMATION_RESOURCE_ID);
            pagerStyle = getArguments().getInt(PAGER_STYLE);
            loopAnimation = getArguments().getBoolean(PAGER_LOOP_ANIMATION);
            binding.welcomeAnimation.setScale(getArguments().getFloat(LOTTIE_SCALE, 1f));
        }

        populateComponents();
    }

    private void populateComponents() {
        binding.descriptionTextView.setText(imageDescription);
        binding.descriptionTextView.setTextAppearance(getContext(), pagerStyle);
        binding.welcomeAnimation.enableMergePathsForKitKatAndAbove(true);
        binding.welcomeAnimation.setAnimation(animationResource);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && binding != null) {
            if (loopAnimation) {
                binding.welcomeAnimation.setRepeatCount(ValueAnimator.INFINITE);
            }
            binding.welcomeAnimation.playAnimation();
        }
    }
}
