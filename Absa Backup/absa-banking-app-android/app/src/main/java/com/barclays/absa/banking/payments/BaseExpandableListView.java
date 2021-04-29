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
package com.barclays.absa.banking.payments;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.SearchSelectActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public abstract class BaseExpandableListView<T> extends BaseActivity implements SearchView.OnQueryTextListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    private SearchSelectActivityBinding searchSelectActivityBinding;
    private HashMap<String, List<T>> childItemSource;
    private List<String> headerItemSource;
    private final String HEADER_NOT_REQUIRED = "header_not_required";

    public abstract void getIntentExtraData(BaseExpandableListView baseExpandableListView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentExtraData(this);
        searchSelectActivityBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.search_select_activity, null, false);
        setContentView(searchSelectActivityBinding.getRoot());

        setToolBarBack(getNavigationTitle(), expression -> onBackPressed());
        searchSelectActivityBinding.baseExpandableListView.setOnChildClickListener(this);
        searchSelectActivityBinding.baseExpandableListView.setOnGroupClickListener(this);
        prepareData(getChildItemSource(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isSearchRequired()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.search_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (searchItem != null) {
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setIconifiedByDefault(true);
                searchView.setOnQueryTextListener(this);
                searchView.setSubmitButtonEnabled(false);
                searchView.setQueryRefinementEnabled(false);
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (isSearchRequired()) {
            if (item.getItemId() == R.id.action_search) {
                AnalyticsUtils.getInstance().trackSearchEvent(mScreenName);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        startSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        startSearch(query);
        return false;
    }

    private void startSearch(String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            prepareData(getChildItemSource(), false);
        } else {
            prepareData(onListItemSearch(searchString), true);
        }
        refreshList();
    }

    private void prepareData(List<T> childData, boolean isSearchOn) {
        AsyncTask.execute(() -> {
            List<T> universalData = getUniversalListItemSource();
            String previousLetter = null;
            List<T> alphabetSortedList = new ArrayList<>();
            List<String> tempHeaderItemSource = new ArrayList<>();
            HashMap<String, List<T>> tempChildItemSource = new HashMap<>();
            Pattern numberPattern = Pattern.compile("[0-9]");
            if (!isSearchOn && isUniversalListRequired() && universalData != null && universalData.size() > 0) {
                tempHeaderItemSource.add(HEADER_NOT_REQUIRED);
                tempChildItemSource.put(tempHeaderItemSource.get(0), universalData);
            }
            if (childData != null) {
                // create alphabetically sorted services
                for (T value : childData) {
                    String firstLetter = getFirstLetter(value);

                    // Group numbers together in the scroller
                    if (numberPattern.matcher(firstLetter).matches()) {
                        firstLetter = "#";
                    }
                    // check if section is changing
                    if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                        tempChildItemSource.put(tempHeaderItemSource.get(tempHeaderItemSource.indexOf(previousLetter)), new ArrayList<>(alphabetSortedList));
                        alphabetSortedList.clear();
                    }
                    // Add unique header services
                    if (!tempHeaderItemSource.contains(firstLetter)) {
                        tempHeaderItemSource.add(firstLetter);
                        previousLetter = firstLetter;
                    }
                    alphabetSortedList.add(value);

                    // add last item on iteration
                    if (childData.size() - 1 == childData.indexOf(value)) {
                        tempChildItemSource.put(tempHeaderItemSource.get(tempHeaderItemSource.indexOf(previousLetter)), new ArrayList<>(alphabetSortedList));
                        alphabetSortedList.clear();
                    }
                }
            }
            headerItemSource = tempHeaderItemSource;
            childItemSource = tempChildItemSource;
            new Handler(getMainLooper()).post(this::refreshList);
        });
    }

    private void refreshList() {
        BaseExpandableListAdapter baseExpandableListAdapter = new BaseExpandableListAdapter(this, getChildTemplateId(), getHeaderTemplateId(), childItemSource, headerItemSource);
        searchSelectActivityBinding.baseExpandableListView.setAdapter(baseExpandableListAdapter);
        baseExpandableListAdapter.expandGroup(searchSelectActivityBinding.baseExpandableListView);
        searchSelectActivityBinding.baseExpandableListView.setFastScrollEnabled(false);
    }

    public void onItemClick(T item) {
    }

    public List<T> onListItemSearch(String searchString) {
        return null;
    }

    public List<T> getUniversalListItemSource() {
        return null;
    }

    public List<T> getChildItemSource() {
        return null;
    }

    public List<String> getHeaderItemSource() {
        return null;
    }

    public int getChildTemplateId() {
        return 0;
    }

    public int getHeaderTemplateId() {
        return 0;
    }

    public String getNavigationTitle() {
        return null;
    }

    public boolean isUniversalListRequired() {
        return false;
    }

    public boolean isSearchRequired() {
        return true;
    }

    public String getFirstLetter(T item) {
        return null;
    }

    public String getPrimaryData(T item) {
        return null;
    }

    public String getSecondaryData(T item) {
        return null;
    }

    public boolean isSecondaryLabelRequired() {
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }
}
