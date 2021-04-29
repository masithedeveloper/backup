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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import styleguide.utils.CustomFlingBehavior;
import za.co.absa.presentation.uilib.R;

public class CollapsingAppBarView extends CoordinatorLayout implements LifecycleObserver {
    private AppBarLayout appBarLayout;
    private TabBarView tabBarView;
    private SearchView searchView;
    private TabPager tabPager;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private FrameLayout contentFrameLayout;
    private FragmentManager fragmentManager;
    private float scrollRange;
    private OnViewPropertiesChangeListener onViewPropertiesChangeListener;

    private State lastState;
    private String appBarTitle;
    private OnPageSelectionListener onPageSelectionListener;
    private CollapsingToolbarLayout.LayoutParams layoutParams;
    private int tabHeight;
    private int lastVerticalOffset;

    private boolean selfAnimate;

    public CollapsingAppBarView(Context context) {
        super(context);
        init(context, null);
    }

    public CollapsingAppBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CollapsingAppBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void wireUpComponents() {
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.toolbar);
        tabBarView = findViewById(R.id.appbar_tab_layout);
        searchView = findViewById(R.id.searchView);
        viewPager = findViewById(R.id.view_pager);
        contentFrameLayout = findViewById(R.id.appbar_content_view);
        viewPager = findViewById(R.id.view_pager);
        tabBarView.setupWithViewPager(viewPager);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init(Context context, AttributeSet attrs) {
        setFitsSystemWindows(true);
        LayoutInflater.from(context).inflate(R.layout.collapsing_app_bar_view, this);
        final AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        wireUpComponents();

        tabHeight = getResources().getDimensionPixelSize(R.dimen.tabHeight);
        layoutParams = new CollapsingToolbarLayout.LayoutParams(LayoutParams.MATCH_PARENT, tabHeight);
        layoutParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN);
        layoutParams.gravity = Gravity.TOP;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapsingAppBarView);
        appBarTitle = typedArray.getString(R.styleable.CollapsingAppBarView_attribute_app_bar_title);

        toolbar.setTitle(appBarTitle);
        appCompatActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getViewTreeObserver().addOnGlobalLayoutListener(() -> scrollRange = (float) appBarLayout.getTotalScrollRange());

        appBarLayout.addOnOffsetChangedListener((AppBarLayout appBarLayout, int verticalOffset) -> {

            if (!selfAnimate) {
                if (verticalOffset == 0 && lastState != State.COLLAPSED) {
                    if (onViewPropertiesChangeListener != null && lastState != State.EXPANDED) {
                        onViewPropertiesChangeListener.onViewPropertiesChanged(State.EXPANDED);
                    }
                    lastState = State.EXPANDED;
                } else if (Math.abs(verticalOffset) >= scrollRange) {
                    if (onViewPropertiesChangeListener != null && lastState != State.COLLAPSED) {
                        onViewPropertiesChangeListener.onViewPropertiesChanged(State.COLLAPSED);
                    }
                    lastState = State.COLLAPSED;
                } else if (verticalOffset < lastVerticalOffset) {
                    if (onViewPropertiesChangeListener != null && lastState != State.MOVE_CLOSING) {
                        onViewPropertiesChangeListener.onViewPropertiesChanged(State.MOVE_CLOSING);
                    }
                    lastState = State.MOVE_CLOSING;
                } else if (verticalOffset > lastVerticalOffset) {
                    if (onViewPropertiesChangeListener != null && lastState != State.MOVE_OPENING) {
                        onViewPropertiesChangeListener.onViewPropertiesChanged(State.MOVE_OPENING);
                    }
                    lastState = State.MOVE_OPENING;
                } else {
                    if (onViewPropertiesChangeListener != null) {
                        onViewPropertiesChangeListener.onViewPropertiesChanged(State.MOVING);
                    }
                    lastState = State.MOVING;
                }
            }

            if (verticalOffset == 0) {
                setContentFrameAlphaAndScale(1, 1);
            } else {
                float invertedVerticalOffset = scrollRange + verticalOffset;
                float alpha = (float) Math.pow((invertedVerticalOffset / scrollRange), 3);
                float scale = (float) Math.cbrt(invertedVerticalOffset / scrollRange);
                setContentFrameAlphaAndScale(scale, alpha);
            }

            lastVerticalOffset = verticalOffset;
        });

        fragmentManager = appCompatActivity.getSupportFragmentManager();
        typedArray.recycle();
    }

    public void setOnViewPropertiesChangeListener(OnViewPropertiesChangeListener onViewPropertiesChangeListener) {
        this.onViewPropertiesChangeListener = onViewPropertiesChangeListener;
    }

    private void setContentFrameAlphaAndScale(float scale, float alpha) {
        double minScaleValue = -3.4028235E38;
        contentFrameLayout.setAlpha(alpha);
        if (scale < minScaleValue || scale == Float.NEGATIVE_INFINITY) {
            contentFrameLayout.setScaleX((float) minScaleValue);
            contentFrameLayout.setScaleY((float) minScaleValue);
        } else {
            contentFrameLayout.setScaleX(scale);
            contentFrameLayout.setScaleY(scale);
        }
    }

    public void addHeaderView(Fragment appBarHeaderFragment) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.appbar_content_view, appBarHeaderFragment);
        fragmentTransaction.commit();
    }

    //Always call this method if you want to disable the tabs to use a different layout with no tabs
    public void disableTabs() {
        if (tabBarView.getVisibility() == View.VISIBLE && viewPager.getVisibility() == View.VISIBLE) {
            tabBarView.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        }
    }

    public void setUpTabs(LifecycleOwner lifecycleOwner, FragmentPagerItem... tabs) {
        SparseArray<FragmentPagerItem> array = new SparseArray<>();
        for (int i = 0; i < tabs.length; i++) {
            array.append(i, tabs[i]);
        }
        setUpTabs(lifecycleOwner, array);
    }

    //This method enables a tabview that has its content in a view pager
    public void setUpTabs(LifecycleOwner lifecycleOwner, SparseArray<FragmentPagerItem> tabs) {
        lifecycleOwner.getLifecycle().addObserver(this);

        if (tabBarView.getVisibility() == View.GONE && viewPager.getVisibility() == View.GONE) {
            tabBarView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }

        tabBarView.addTabs(tabs);
        tabPager = new TabPager(fragmentManager, tabs);
        viewPager.setAdapter(tabPager);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void enableCallbacks() {
        if (tabBarView != null && viewPager != null) {
            tabBarView.addOnTabSelectedListener(new CollapsingAppBarOnPageChangeListener(viewPager));
            viewPager.addOnPageChangeListener(new TabBarView.TabLayoutOnPageChangeListener(tabBarView));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void disableCallbacks() {
        if (tabBarView != null && viewPager != null) {
            tabBarView.removeOnTabSelectedListener(new TabBarView.ViewPagerOnTabSelectedListener(viewPager));
            viewPager.removeOnPageChangeListener(new TabBarView.TabLayoutOnPageChangeListener(tabBarView));
        }
    }

    public void setOnPageSelectionListener(OnPageSelectionListener onPageSelectionListener) {
        this.onPageSelectionListener = onPageSelectionListener;
    }

    private class CollapsingAppBarOnPageChangeListener extends TabBarView.ViewPagerOnTabSelectedListener {

        CollapsingAppBarOnPageChangeListener(ViewPager viewPager) {
            super(viewPager);
        }

        @Override
        public void onTabSelected(@NotNull TabLayout.Tab tab) {
            super.onTabSelected(tab);
            if (onPageSelectionListener != null) {
                String description = tab.getText() != null ? tab.getText().toString() : "";
                onPageSelectionListener.onPageSelected(description, tab.getPosition());
            }
        }
    }

    public void setBackground(int background) {
        appBarLayout.setBackgroundResource(background);
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setSubTitle(String subTitle) {
        toolbar.setSubtitle(subTitle);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TabBarView getTabBarView() {
        return tabBarView;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void collapseAppBar() {
        selfAnimate = true;
        lastState = State.COLLAPSED;
        CustomFlingBehavior.disableScroll = true;

        appBarLayout.setExpanded(false);
        Handler handler = new Handler();
        handler.postDelayed(() -> selfAnimate = false, 500);
    }

    public void expandAppBar() {
        CustomFlingBehavior.disableScroll = false;
        appBarLayout.setExpanded(true);
    }

    public void hideSearchView() {
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.setVisibility(View.GONE);
        }
    }

    public void showSearchView() {
        if (searchView.getVisibility() == View.GONE) {
            searchView.setVisibility(View.VISIBLE);
            searchView.requestFocus();

            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public void setOnSearchQueryListener(SearchView.OnQueryTextListener onQueryTextListener) {
        searchView.setOnQueryTextListener(onQueryTextListener);
    }

    public enum State {
        COLLAPSED,
        EXPANDED,
        MOVE_CLOSING,
        MOVE_OPENING,
        MOVING
    }

    public interface OnPageSelectionListener {
        void onPageSelected(String description, int position);
    }

    public interface OnViewPropertiesChangeListener {
        void onViewPropertiesChanged(State state);
    }

    public State getLastState() {
        return lastState;
    }

    public SearchView getSearchView() {
        return searchView;
    }

}