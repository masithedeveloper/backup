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

package com.barclays.absa.banking.beneficiaries.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.buy.ui.airtime.buyNew.AddBeneficiaryAirtimeActivity;
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityActivity;
import com.barclays.absa.banking.cashSend.ui.CashSendToNewBeneficaryActivity;
import com.barclays.absa.banking.databinding.BeneficiaryLandingActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.RequestCompletionCallback;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.payments.NewPaymentBeneficiaryDetailsActivity;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DrawerOptions;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;

import styleguide.bars.FragmentPagerItem;
import styleguide.bars.TabBarView;
import styleguide.bars.TabPager;

public class BeneficiaryLandingActivity extends BaseActivity implements BeneficiaryLandingView {

    public static final String TAB_TO_SELECT_EXTRA = "tabToSelect";
    public static final String BENEFICIARY_TYPE = "beneficiaryType";
    public static final String ADD_PREPAID_ELECTRICITY_BENEFICIARY = "addPrepaidElectricityBeneficiaryManageBeneficiaryFlow";
    private BeneficiaryLandingActivityBinding binding;
    private BeneficiaryListFragment currentListFragment;
    private SearchView searchView;
    private final IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.beneficiary_landing_activity, null, false);
        setContentView(binding.getRoot());

        setToolbar();
        beneficiaryCacheService.setTabPositionToReturnTo("");
        beneficiaryCacheService.setBeneficiaryAdded(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String tabPositionToReturnTo = beneficiaryCacheService.getTabPositionToReturnTo();
        Boolean isBeneficiaryAdded = beneficiaryCacheService.isBeneficiaryAdded();

        if (isBeneficiaryAdded != null && isBeneficiaryAdded) {
            DrawerOptions.callPaymentManageBeneficiary(this);
        }

        if (tabPositionToReturnTo == null || tabPositionToReturnTo.isEmpty()) {
            requestBeneficiaryData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu_light, menu);

        menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (currentListFragment != null && currentListFragment.isResumed()) {
                    currentListFragment.hideBeneficiaryButton();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (currentListFragment != null && currentListFragment.isResumed()) {
                    currentListFragment.showBeneficiaryButton();
                }
                handleSearchQuery(null);
                return true;
            }
        });

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                handleSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            handleSearchQuery(null);
            onBackPressed();
            return true;
        });

        return true;
    }

    private void requestBeneficiaryData() {
        showProgressDialog();
        BeneficiaryLandingRequestCompletionCallback requestCompletionCallback = new BeneficiaryLandingRequestCompletionCallback(this);
        BeneficiariesInteractor beneficiariesInteractor = new BeneficiariesInteractor();
        beneficiariesInteractor.fetchAllBeneficiaryLists(requestCompletionCallback);
    }

    @Override
    public void loadTabData() {
        TabBarView tabBar = binding.tabLayout;
        tabBar.removeAllTabs();

        SparseArray<FragmentPagerItem> tabs = new SparseArray<>();
        String tabDescription = getString(R.string.beneficiary_management_payments_title);
        final BeneficiaryListFragment paymentBeneficiaryListFragment = BeneficiaryListFragment.newInstance(tabDescription, BeneficiaryType.BENEFICIARY_TYPE_PAYMENT);
        tabs.append(BeneficiaryType.BENEFICIARY_TYPE_PAYMENT, paymentBeneficiaryListFragment);
        tabDescription = getString(R.string.beneficiary_management_prepaid_title);
        final BeneficiaryListFragment prepaidBeneficiaryListFragment = BeneficiaryListFragment.newInstance(tabDescription, BeneficiaryType.BENEFICIARY_TYPE_PREPAID, false);
        tabs.append(BeneficiaryType.BENEFICIARY_TYPE_PREPAID, prepaidBeneficiaryListFragment);
        tabDescription = getString(R.string.beneficiary_management_cashsend_title);
        final BeneficiaryListFragment cashSendBeneficiaryListFragment = BeneficiaryListFragment.newInstance(tabDescription, BeneficiaryType.BENEFICIARY_TYPE_CASHSEND);
        tabs.append(BeneficiaryType.BENEFICIARY_TYPE_CASHSEND, cashSendBeneficiaryListFragment);
        tabDescription = getString(R.string.electricity);
        final BeneficiaryListFragment electricityBeneficiaryListFragment = BeneficiaryListFragment.newInstance(tabDescription, BeneficiaryType.BENEFICIARY_TYPE_ELECTRICITY);
        if (!AccessPrivileges.getInstance().isOperator()) {
            tabs.append(BeneficiaryType.BENEFICIARY_TYPE_ELECTRICITY, electricityBeneficiaryListFragment);
        }

        tabBar.addTabs(tabs);

        ViewPager viewPager = binding.viewPager;
        TabPager adapter = new TabPager(getSupportFragmentManager(), tabs);
        viewPager.setAdapter(adapter);

        tabBar.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBar));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                setToolbar();
                switch (position) {
                    case 0:
                        setCurrentFragment(paymentBeneficiaryListFragment);
                        break;
                    case 1:
                        setCurrentFragment(prepaidBeneficiaryListFragment);
                        break;
                    case 2:
                        setCurrentFragment(cashSendBeneficiaryListFragment);
                        break;
                    case 3:
                        setCurrentFragment(electricityBeneficiaryListFragment);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int tabToSelect = getIntent().getIntExtra(TAB_TO_SELECT_EXTRA, 0);
        if (tabToSelect == 0) {
            setCurrentFragment(paymentBeneficiaryListFragment);
        }
        viewPager.setCurrentItem(tabToSelect);
    }

    private void setToolbar() {
        setToolBarBack(getString(R.string.payment_details_beneficiary_title));
    }

    @Override
    public void addBeneficiaryClicked(String beneficiaryType) {
        beneficiaryCacheService.setTabPositionToReturnTo(beneficiaryType);
        switch (beneficiaryType) {
            case PASS_AIRTIME:
                startActivity(new Intent(this, AddBeneficiaryAirtimeActivity.class));
                break;
            case PASS_CASHSEND:
                AnalyticsUtil.INSTANCE.tagCashSend("BeneficiaryCashSendPlusTab_AddBeneficiaryClicked");
                startActivity(new Intent(this, CashSendToNewBeneficaryActivity.class));
                break;
            case PASS_PAYMENT:
                startActivity(new Intent(this, NewPaymentBeneficiaryDetailsActivity.class));
                break;
            case PASS_PREPAID_ELECTRICITY:
                Intent intent = new Intent(this, PrepaidElectricityActivity.class);
                intent.putExtra(ADD_PREPAID_ELECTRICITY_BENEFICIARY, true);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void setCurrentFragment(BeneficiaryListFragment currentFragment) {
        currentListFragment = currentFragment;
        handleSearchQuery(null);
    }

    class BeneficiaryLandingRequestCompletionCallback extends RequestCompletionCallback<Void> {
        private final WeakReference<BeneficiaryLandingView> weakReference;

        public BeneficiaryLandingRequestCompletionCallback(BeneficiaryLandingView beneficiaryLandingView) {
            weakReference = new WeakReference<>(beneficiaryLandingView);
        }

        @Override
        public void onSuccess() {
            dismissProgressDialog();
            beneficiaryCacheService.setTabPositionToReturnTo("");
            BeneficiaryLandingView beneficiaryLandingView = weakReference.get();
            if (beneficiaryLandingView != null) {
                beneficiaryLandingView.loadTabData();
            }
        }

        @Override
        public void onFailure() {
            dismissProgressDialog();
            if (weakReference.get() != null) {
                showGenericErrorMessageThenFinish();
            }
            beneficiaryCacheService.setTabPositionToReturnTo("");
        }
    }

    private void handleSearchQuery(String query) {
        if (currentListFragment != null && currentListFragment.isResumed() && currentListFragment.isVisible()) {
            if (query == null) {
                currentListFragment.loadData(false);
            } else if (query.isEmpty()) {
                currentListFragment.loadData(true);
            } else {
                currentListFragment.loadData(query);
            }
        }
    }
}