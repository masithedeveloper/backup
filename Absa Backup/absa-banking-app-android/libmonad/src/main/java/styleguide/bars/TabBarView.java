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

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

import za.co.absa.presentation.uilib.R;

public class TabBarView extends TabLayout {

    private final int scrollableTabWidthConstraint = 200;
    private final int maxFixedTabCount = 3;

    public TabBarView(Context context) {
        super(context);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addTabs(SparseArray<FragmentPagerItem> fragmentPagerItems) {
        int fragmentItemsCount = fragmentPagerItems.size();
        for (int index = 0; index < fragmentItemsCount; index++) {
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
            TextView tabDescriptionTextView = tabView.findViewById(R.id.tab_description);
            FragmentPagerItem fragmentPagerItem = fragmentPagerItems.get(index);
            if (fragmentPagerItem != null) {
                tabDescriptionTextView.setText(fragmentPagerItem.getTabDescription());
                Tab tab = newTab();
                tab.setCustomView(tabView).setContentDescription(fragmentPagerItem.getTabDescription());
                addTab(tab);
            }
        }

        int tabCount = getTabCount();
        if (tabCount == 1) {
            setFixedTabWidthForSingleTab(tabCount);
            setTabMode(TabLayout.MODE_FIXED);
            setTabGravity(TabLayout.GRAVITY_FILL);
        } else if (tabCount <= maxFixedTabCount) {
            setFixedTabWidth(tabCount);
            setTabMode(TabLayout.MODE_FIXED);
            setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            setScrollableTabWidth(maxFixedTabCount);
            setTabMode(TabLayout.MODE_SCROLLABLE);
            setTabGravity(TabLayout.GRAVITY_FILL);
        }
    }

    private DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    private void setScrollableTabWidth(int tabCount) {
        Field field;
        DisplayMetrics displayMetrics = getDisplayMetrics();
        int width = displayMetrics.widthPixels - scrollableTabWidthConstraint;
        if (tabCount == 0) {
            tabCount = 1;
        }
        int newWidth = ((width - (tabCount)) / tabCount);
        try {
            field = TabLayout.class.getDeclaredField("scrollableTabMinWidth");
            field.setAccessible(true);
            field.set(this, newWidth);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFixedTabWidth(int tabCount) {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        if (tabCount == 0) {
            tabCount = 1;
        }
        int newWidth = ((displayMetrics.widthPixels - (tabCount)) / tabCount);

        try {
            Field field = TabLayout.class.getDeclaredField("scrollableTabMinWidth");

            field.setAccessible(true);
            field.set(this, newWidth);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFixedTabWidthForSingleTab(int tabCount) {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        if (tabCount == 0) {
            tabCount = 1;
        }
        int newWidth = ((displayMetrics.widthPixels - (tabCount * 10)) / tabCount);

        try {
            Field field = TabLayout.class.getDeclaredField("requestedTabMinWidth");
            field.setAccessible(true);
            field.set(this, newWidth);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}