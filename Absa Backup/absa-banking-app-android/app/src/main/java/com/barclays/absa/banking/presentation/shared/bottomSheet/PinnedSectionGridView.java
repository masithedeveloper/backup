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
package com.barclays.absa.banking.presentation.shared.bottomSheet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * ListView capable to pin views at its top while the rest is still scrolled.
 */
public class PinnedSectionGridView extends GridView {


    // -- class fields

    private int mNumColumns;
    private int mHorizontalSpacing;
    private int mColumnWidth;
    private int mAvailableWidth;

    public PinnedSectionGridView(Context context) {
        super(context);
    }

    public PinnedSectionGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedSectionGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    @Override
    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        super.setNumColumns(numColumns);
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
        super.setHorizontalSpacing(horizontalSpacing);
    }

    public int getColumnWidth() {
        return mColumnWidth;
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        mColumnWidth = columnWidth;
        super.setColumnWidth(columnWidth);
    }

    public int getAvailableWidth() {
        return mAvailableWidth != 0 ? mAvailableWidth : getWidth();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (mNumColumns == GridView.AUTO_FIT) {
//            mAvailableWidth = MeasureSpec.getSize(widthMeasureSpec);
//            if (mColumnWidth > 0) {
//                int availableSpace = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
//                // Client told us to pick the number of columns
//                mNumColumns = (availableSpace + mHorizontalSpacing) /
//                        (mColumnWidth + mHorizontalSpacing);
//            } else {
//                // Just make up a number if we don't have enough info
//                mNumColumns = 2;
//            }
//            if(null != getAdapter()){
//                if(getAdapter() instanceof SimpleSectionedGridAdapter){
//                    ((SimpleSectionedGridAdapter)getAdapter()).setSections();
//                }
//            }
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
}