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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import za.co.absa.presentation.uilib.R;

public class ExtendedCarouselViewPager extends ConstraintLayout {

    private ViewPager2 viewPager;
    private ViewPagerIndicator viewPagerIndicator;

    public ExtendedCarouselViewPager(Context context) {
        super(context);
        init(context, null);
    }

    public ExtendedCarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExtendedCarouselViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.extended_image_carousel, this);
        wireUpExtendedViewPager();
    }

    private void wireUpExtendedViewPager() {
        viewPager = findViewById(R.id.viewPagerExtended);
        viewPagerIndicator = findViewById(R.id.viewPagerIndicator);
    }

    public void populateExtendedCarouselPager(List<CarouselPage> carouselPageList) {
        if (carouselPageList != null && carouselPageList.size() > 0) {
            List<Fragment> pagerFragments = createExtendedCarouselFragment(carouselPageList);
            viewPagerIndicator.setVisibility(pagerFragments.size() == 1 ? GONE : VISIBLE);
            PagerAdapter pagerAdapter = new PagerAdapter((AppCompatActivity) getContext(), pagerFragments);
            viewPager.setOffscreenPageLimit(4);
            viewPager.setClipToPadding(false);
            viewPager.setAdapter(pagerAdapter);

            viewPagerIndicator.setupWithViewPager(viewPager);
        }
    }

    private List<Fragment> createExtendedCarouselFragment(List<CarouselPage> carouselPageList) {
        List<Fragment> cardFragments = new ArrayList<>();
        for (CarouselPage page : carouselPageList) {
            cardFragments.add(ExtendedCarouselFragment.newInstance(page.getTitle(), page.getDescription(), page.getImage()));
        }
        return cardFragments;
    }

    private static class PagerAdapter extends FragmentStateAdapter {
        private final List<? extends Fragment> fragments;

        PagerAdapter(FragmentActivity fragmentActivity, List<? extends Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}