/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package styleguide.bars

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.light_collapsing_app_bar_view.view.*
import za.co.absa.presentation.uilib.R

private const val MAX_TAB_COUNT = 3

class LightCollapsingAppBarView : CoordinatorLayout {
    private var appBarStateExpanded: Boolean = false

    private lateinit var fragmentManager: FragmentManager

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.light_collapsing_app_bar_view, this)
        val appCompatActivity = context as AppCompatActivity
        fragmentManager = appCompatActivity.supportFragmentManager
        if (appbarLayout.layoutParams != null) {
            val layoutParams = appbarLayout.layoutParams as LayoutParams
            val appBarLayoutBehaviour = AppBarLayout.Behavior()
            appBarLayoutBehaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(@NonNull appBarLayout: AppBarLayout): Boolean {
                    return false
                }
            })
            layoutParams.behavior = appBarLayoutBehaviour
        }

        appbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                appbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                appbarLayout.setExpanded(appBarStateExpanded)
            }
        })
    }

    fun addHeaderView(appBarHeaderFragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.appbarContentView, appBarHeaderFragment)
        fragmentTransaction.commit()
    }

    fun addToolBarAndFragments(fragmentActivity: FragmentActivity, tabs: List<TabBarFragment>, stateChangeListener: StateChangedListener) {
        val viewPagerAdapter = CollapsingTabBarViewPagerAdapter(tabs, fragmentActivity)
        viewPager.adapter = viewPagerAdapter
        setupTabMode(tabLayout, tabs.size)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position].title
        }.attach()

        appbarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(isExpanded: Boolean) {
                stateChangeListener.onStateChanged(isExpanded)
            }
        })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                stateChangeListener.onTabChanged(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun setAppBarState(appBarStateExpanded: Boolean) {
        this.appBarStateExpanded = appBarStateExpanded
    }

    fun collapseToolbar() {
        appbarLayout.setExpanded(false)
    }

    private fun setupTabMode(tabLayout: TabLayout, tabCount: Int) {
        tabLayout.tabMode = when (tabCount) {
            in 1..MAX_TAB_COUNT -> TabLayout.MODE_FIXED
            else -> TabLayout.MODE_SCROLLABLE
        }
    }
}

data class TabBarFragment(var fragment: Fragment, var title: String)

interface StateChangedListener {
    fun onStateChanged(isExpanded: Boolean)
    fun onTabChanged(position: Int)
}

abstract class AppBarStateChangeListener : OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED
    }

    private var currentState = State.EXPANDED

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        currentState = if (i == 0) {
            if (currentState != State.COLLAPSED) {
                onStateChanged(true)
            }
            State.COLLAPSED
        } else {
            if (currentState != State.EXPANDED) {
                onStateChanged(false)
            }
            State.EXPANDED
        }
    }

    abstract fun onStateChanged(isExpanded: Boolean)
}