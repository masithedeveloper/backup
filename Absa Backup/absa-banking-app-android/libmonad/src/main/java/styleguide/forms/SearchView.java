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
package styleguide.forms;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import za.co.absa.presentation.uilib.R;

public class SearchView extends ConstraintLayout {
    private EditText searchEditText;
    private ConstraintLayout moreFiltersConstraintLayout;

    public SearchView(Context context) {
        super(context);
        init(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        LayoutInflater.from(context).inflate(R.layout.search_view, this);
        wireUpViews();
        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.SearchView);
        int filterOptionVisibility = typedArray.getInteger(R.styleable.SearchView_attribute_filter_button, View.VISIBLE);
        setFiltersOptionsVisibility(filterOptionVisibility);
        typedArray.recycle();
    }

    private void wireUpViews() {
        searchEditText = findViewById(R.id.searchEditText);
        moreFiltersConstraintLayout = findViewById(R.id.moreFiltersConstraintLayout);
        ImageView clearImageView = findViewById(R.id.clearImageView);
        clearImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText(null);
            }
        });
    }

    public void setOnFiltersClickListener(OnClickListener onClickListener) {
        moreFiltersConstraintLayout.setOnClickListener(onClickListener);
    }

    public void setFiltersOptionsVisibility(int visibility) {
        moreFiltersConstraintLayout.setVisibility(visibility);
    }

    public String getSearchedString() {
        return searchEditText.getText().toString();
    }

    public void setSearchedString(String keyword) {
        searchEditText.setText(keyword);
    }

    public void addSearchTextWatcher(TextWatcher textWatcher) {
        searchEditText.addTextChangedListener(textWatcher);
    }

    public void removeSearchTextWatcher(TextWatcher textWatcher) {
        searchEditText.removeTextChangedListener(textWatcher);
    }

    public void hideCalander() {
        moreFiltersConstraintLayout.setVisibility(GONE);
    }
}
