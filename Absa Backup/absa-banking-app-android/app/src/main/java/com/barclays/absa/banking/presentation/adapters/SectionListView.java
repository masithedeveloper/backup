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
package com.barclays.absa.banking.presentation.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.barclays.absa.banking.BuildConfig;


/**
 * View displaying the list with sectioned header.
 */
public class SectionListView extends ListView implements OnScrollListener {

    private View transparentView;

    public SectionListView(final Context context, final AttributeSet attrs,
                           final int defStyle) {
        super(context, attrs, defStyle);
        commonInitialisation();
    }

    public SectionListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        commonInitialisation();
    }

    public SectionListView(final Context context) {
        super(context);
        commonInitialisation();
    }

    private void commonInitialisation() {
        setDivider(null);
        setDividerHeight(0);

        setOnScrollListener(this);
        setVerticalFadingEdgeEnabled(false);
        setFadingEdgeLength(0);
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (!(adapter instanceof SectionListAdapter)) {
            throw new IllegalArgumentException(
                    "The adapter needds to be of type "
                            + SectionListAdapter.class + " and is "
                            + adapter.getClass());
        }
        super.setAdapter(adapter);
        final ViewParent parent = getParent();
        if (!(parent instanceof FrameLayout)) {
            throw new IllegalStateException(
                    "Section List should have FrameLayout as parent!");
        }
        if (transparentView != null) {
            ((FrameLayout) parent).removeView(transparentView);
        }
        transparentView = ((SectionListAdapter) adapter)
                .getTransparentSectionView();
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ((FrameLayout) parent).addView(transparentView, lp);
        if (adapter.isEmpty()) {
            transparentView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
                         final int visibleItemCount, final int totalItemCount) {
        try {

//			final SectionListAdapter adapter = (SectionListAdapter) getAdapter();
            SectionListAdapter adapter;
            if (getAdapter().getClass().equals(HeaderViewListAdapter.class)) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) getAdapter();
                adapter = (SectionListAdapter) wrapperAdapter.getWrappedAdapter();
            } else {
                adapter = (SectionListAdapter) getAdapter();
            }
            if (adapter != null) {
                adapter.makeSectionInvisibleIfFirstInList(-1/*firstVisibleItem*/);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(final AbsListView view,
                                     final int scrollState) {
        // do nothing
    }

}
