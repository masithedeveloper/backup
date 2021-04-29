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

import android.os.Bundle;
import android.util.SparseArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import za.co.absa.presentation.uilib.R;

public class TopTabExample extends AppCompatActivity {

    private TabBarView tabBar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_example);

        setToolbar();

        tabBar = findViewById(R.id.tabLayout);

        SparseArray<FragmentPagerItem> tabs = new SparseArray<>();
        tabs.append(0, new TabBar1Example());
        tabs.append(1, new TabBar2Example());
        tabs.append(2, new TabBar3Example());
        tabs.append(3, new TabBar4Example());
        tabs.append(4, new TabBar2Example());

        tabBar.addTabs(tabs);

        viewPager = findViewById(R.id.viewPager);
        TabPager adapter = new TabPager(getSupportFragmentManager(), tabs);
        viewPager.setAdapter(adapter);

        tabBar.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBar));
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tabBar.removeOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.removeOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBar));
    }
}