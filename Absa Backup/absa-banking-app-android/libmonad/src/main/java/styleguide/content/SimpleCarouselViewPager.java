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

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import za.co.absa.presentation.uilib.R;

public class SimpleCarouselViewPager extends ConstraintLayout {
    private TabLayout dotsTabLayout;
    private ViewPager viewPager;
    private float lottieScale = 1f;

    public SimpleCarouselViewPager(Context context) {
        super(context);
        init(context);
    }

    public SimpleCarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleCarouselViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.image_description_view_pager, this);
        viewPager = findViewById(R.id.viewPagerSimple);
        dotsTabLayout = findViewById(R.id.tab_layout_dots);
        setUpComponentListeners();
    }

    private void setUpComponentListeners() {
        dotsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void populateAnimatedSimpleCarouselPage(LinkedHashMap<String, Map.Entry<String, Boolean>> pagerContent, int pagerStyle) {
        if (pagerContent != null && pagerContent.size() > 0) {
            List<Fragment> pagerFragments = createAnimatedSimpleCarouselFragment(pagerContent, pagerStyle);
            dotsTabLayout.setupWithViewPager(viewPager, true);
            dotsTabLayout.setVisibility(pagerContent.size() == 1 ? GONE : VISIBLE);
            PagerAdapter pagerAdapter = new PagerAdapter(((AppCompatActivity) getContext()).getSupportFragmentManager(), pagerFragments);
            viewPager.setOffscreenPageLimit(4);
            viewPager.setClipToPadding(false);
            viewPager.setAdapter(pagerAdapter);
        }
    }

    private List<Fragment> createAnimatedSimpleCarouselFragment(LinkedHashMap<String, Map.Entry<String, Boolean>> pagerContent, int pagerStyle) {
        List<Fragment> animatedFragments = new ArrayList<>();
        for (String description : pagerContent.keySet()) {
            Map.Entry<String, Boolean> animationValues = pagerContent.get(description);
            String animationName = animationValues.getKey();
            boolean shouldLoop = animationValues.getValue();
            animatedFragments.add(AnimationDescriptionFragment.newInstance(description, animationName, shouldLoop, pagerStyle, lottieScale));
        }
        return animatedFragments;
    }

    public void setLottieScale(float scale) {
        lottieScale = scale;
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {
        private final List<? extends Fragment> fragments;

        PagerAdapter(FragmentManager fm, List<? extends Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}