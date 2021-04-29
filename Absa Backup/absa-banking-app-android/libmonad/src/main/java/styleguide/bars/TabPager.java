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

package styleguide.bars;

import android.os.Parcelable;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

public class TabPager extends FragmentStatePagerAdapter {

    private int tabsCount;
    private SparseArray<FragmentPagerItem> tabs;

    public TabPager(FragmentManager fragmentManager, SparseArray<FragmentPagerItem> tabs) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabs = tabs;
        tabsCount = tabs.size();
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabsCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTabDescription();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
