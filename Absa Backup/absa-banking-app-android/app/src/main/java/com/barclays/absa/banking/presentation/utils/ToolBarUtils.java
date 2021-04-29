/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.presentation.utils;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.BaseActivity;

import java.lang.reflect.Field;

import styleguide.utils.extensions.StringExtensions;

public class ToolBarUtils {

    public static ActionBar setToolBarBack(final BaseActivity baseActivity, Toolbar toolbar, String title, String subTitle) {
        return setToolBarBack(baseActivity, toolbar, title, subTitle, v -> baseActivity.onBackPressed(), false);
    }

    public static ActionBar setToolBarBack(final BaseActivity baseActivity, String title) {
        Toolbar toolbar = getToolbar(baseActivity);
        return setToolBarBack(baseActivity, toolbar, title, null);
    }

    public static ActionBar setToolBarBack(final BaseActivity baseActivity, String title, View.OnClickListener onClickListener, boolean overrideTitleCase) {
        Toolbar toolbar = getToolbar(baseActivity);
        return setToolBarBack(baseActivity, toolbar, title, null, onClickListener, overrideTitleCase);
    }

    public static void setToolBarIconToClose(final BaseActivity baseActivity) {
        getToolbar(baseActivity).setNavigationIcon(R.drawable.ic_cross_dark);
    }

    private static Toolbar getToolbar(BaseActivity baseActivity) {
        Toolbar toolbar = baseActivity.findViewById(R.id.toolBar);
        if (toolbar == null) {
            toolbar = baseActivity.findViewById(R.id.toolbar);
        }
        return toolbar;
    }

    public static ActionBar setToolBarBack(final BaseActivity baseActivity, int title, int subTitle) {
        return setToolBarBack(baseActivity, baseActivity.getString(title), baseActivity.getString(subTitle));
    }

    public static ActionBar setToolBarBack(final BaseActivity baseActivity, String title, String subTitle) {
        Toolbar toolbar = getToolbar(baseActivity);
        return setToolBarBack(baseActivity, toolbar, title, subTitle);
    }

    public static ActionBar setToolBarNoBack(final BaseActivity baseActivity, String title) {
        return setToolBarNoBack(baseActivity, title, null);
    }

    private static ActionBar setToolBarNoBack(final BaseActivity baseActivity, String title, String subTitle) {
        Toolbar toolbar = getToolbar(baseActivity);
        return setToolBarNoBack(baseActivity, toolbar, title, subTitle);
    }

    public static ActionBar setToolBarBack(final BaseActivity baseActivity,
                                           Toolbar toolbar, String title, String subTitle,
                                           View.OnClickListener onClickListener, boolean overrideTitleCase) {
        return setToolBar(baseActivity, true, toolbar, title, subTitle, onClickListener, overrideTitleCase);
    }

    private static ActionBar setToolBarNoBack(final BaseActivity baseActivity, Toolbar toolbar, String title, String subTitle) {
        return setToolBar(baseActivity, false, toolbar, title, subTitle, null, false);
    }

    private static ActionBar setToolBar(final BaseActivity baseActivity, boolean hasBackIcon,
                                        Toolbar toolbar, String title, String subTitle,
                                        View.OnClickListener onClickListener, boolean overrideTitleCase) {
        if (toolbar != null) {
            if (overrideTitleCase) {
                toolbar.setTitle(title);
            } else {
                toolbar.setTitle(StringExtensions.toTitleCase(title));
            }
            toolbar.setSubtitle(subTitle);
            baseActivity.setSupportActionBar(toolbar);
            ActionBar actionBar = baseActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(hasBackIcon);
            }
            if (!hasBackIcon) {
                int dpAsPixels = baseActivity.getResources().getDimensionPixelOffset(R.dimen.small_space);
                toolbar.setPadding(dpAsPixels, 0, 0, 0);
            } else {
                toolbar.setPadding(0, 0, 0, 0);
            }
            if (onClickListener != null) {
                toolbar.setNavigationOnClickListener(onClickListener);
            }

            if (getToolbarTitleTextColor(toolbar) == Color.WHITE) {
                toolbar.setNavigationIcon(R.drawable.ic_left_arrow_light);
                if (title.length() > 22) {
                    toolbar.setTitleTextAppearance(baseActivity, R.style.ToolbarRegularTextLight);
                } else {
                    toolbar.setTitleTextAppearance(baseActivity, R.style.ToolbarLargeTextLight);
                }
            } else {
                if (title.length() > 22) {
                    toolbar.setTitleTextAppearance(baseActivity, R.style.ToolbarRegularText);
                } else {
                    toolbar.setTitleTextAppearance(baseActivity, R.style.ToolbarLargeText);
                }
            }
        }

        return baseActivity.getSupportActionBar();
    }

    private static int getToolbarTitleTextColor(Toolbar toolbar) {
        try {
            Field field = toolbar.getClass().getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            TextView mTitleTextView = (TextView) field.get(toolbar);
            if (mTitleTextView != null) {
                return mTitleTextView.getCurrentTextColor();
            }
        } catch (Exception e) {
            //ignore
        }
        return 0;
    }
}