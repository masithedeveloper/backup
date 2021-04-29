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
package com.barclays.absa.banking.presentation.multipleUsers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.databinding.ActivityMultipleUsersListBinding;
import com.barclays.absa.banking.databinding.ListItemMultipleUsersBinding;
import com.barclays.absa.banking.databinding.ListItemMultipleUsersRemoveBinding;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.AlertBox;
import com.barclays.absa.banking.passcode.passcodeLogin.RemoveAccountConfirmationActivity;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.utils.ToolBarUtils;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.ImageUtils;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.UserSettingsManager;
import com.barclays.absa.utils.imageHelpers.BeneficiaryImageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import styleguide.widgets.RoundedImageView;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;

public class MultipleUsersListActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private ActivityMultipleUsersListBinding binding;
    private final int SEARCH_THRESHOLD = 5;
    private int numberOfProfiles;
    private List<UserProfile> originalUserProfileList;
    private List<UserProfile> backingUserProfileList;
    private Filter filter;
    private CharSequence searchText = "";
    private final int REMOVE_USER_PROFILE_REQUEST_CODE = 101;
    private boolean lastResultWasEmptyResult;
    private final UserProfileComparator userProfileSortComparator = new UserProfileComparator();
    private boolean enableAnimation = true;
    private MenuItem menuItemRemove, menuItemCancel;
    private ActionBar actionBar;
    private boolean userRemoved;
    public static final int RESULT_FAILED = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_multiple_users_list, null, false);
        setContentView(binding.getRoot());

        actionBar = ToolBarUtils.setToolBarBack(this, "Users");
        originalUserProfileList = (List<UserProfile>) getIntent().getSerializableExtra("userProfiles");

        loadUserProfileList();
        binding.clAddUser.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (userRemoved) {
            Intent intent = new Intent(this, SimplifiedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.cl_add_user) {
            animate(binding.clAddUser, R.anim.expand_horizontal);
            addUser();
        }
    }

    private void loadUserProfileList() {
        if (originalUserProfileList != null) {
            initUserProfileList();
        } else
            ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
                @Override
                public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                    originalUserProfileList = userProfiles;
                    initUserProfileList();
                }

                @Override
                public void onProfilesLoadFailed() {
                    BaseAlertDialog.INSTANCE.showRetryErrorDialog(getString(R.string.generic_error),
                            new AlertBox.AlertRetryListener() {
                                @Override
                                public void retry() {
                                    loadUserProfileList();
                                }
                            });
                }
            });
    }

    public void initUserProfileList() {
        Collections.sort(originalUserProfileList, userProfileSortComparator);
        backingUserProfileList = originalUserProfileList;
        numberOfProfiles = backingUserProfileList.size();
        setNumberOfProfilesText();
        createMultipleUsersAdapter(false);
    }

    static class UserProfileComparator implements Comparator<UserProfile> {
        @Override
        public int compare(UserProfile o1, UserProfile o2) {
            int result;
            if (o1 == null || o1.getCustomerName() == null || o2 == null || o2.getCustomerName() == null) {
                result = 0;
            } else {
                result = o1.getCustomerName().toLowerCase().compareTo(o2.getCustomerName().toLowerCase());
            }
            return result;
        }
    }

    private void loadUserProfileList(final ProfileManager.OnProfileLoadListener loadListener) {
        ProfileManager.getInstance().loadAllUserProfiles(loadListener);
    }

    private void createMultipleUsersAdapter(boolean isRemovalList) {
        if (isRemovalList) {
            actionBar.setTitle("Remove user");
            if (menuItemCancel != null) {
                menuItemCancel.setVisible(true);
            }
        } else {
            actionBar.setTitle("Users");
            if (menuItemRemove != null) {
                menuItemRemove.setVisible(true);
            }
            binding.clAddUser.setVisibility(View.VISIBLE);
        }
        if (enableAnimation) {
            enableAnimation = false;
        }
        MultipleUsersListAdapter multipleUsersAdapter = new MultipleUsersListAdapter(isRemovalList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvUsersList.setLayoutManager(layoutManager);
        binding.rvUsersList.setAdapter(multipleUsersAdapter);
        filter = multipleUsersAdapter.getFilter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.multi_users_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        menuItemCancel = menu.findItem(R.id.action_cancel);
        menuItemRemove = menu.findItem(R.id.action_remove);

        if (searchItem != null) {
            searchItem.setVisible(false);
        }
        if (ProfileManager.getInstance().getUserProfiles().size() > SEARCH_THRESHOLD) { //better implementation would determine how many profiles would fit on one screen
            if (searchItem != null) {
                searchItem.setVisible(true);
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView = (SearchView) searchItem.getActionView();
                setupSearchView(searchView);

                if (searchView != null && searchManager != null) {
                    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_remove) {
            actionBar.setTitle("Remove user");
            binding.clAddUser.setVisibility(View.GONE);
            item.setVisible(false);
            createMultipleUsersAdapter(true);
            if (menuItemCancel != null) {
                menuItemCancel.setVisible(true);
            }
        } else if (item.getItemId() == R.id.action_cancel) {
            actionBar.setTitle("Users");
            onActionCancel();
        }
        return true;
    }

    public void onActionCancel() {
        createMultipleUsersAdapter(false);
        binding.clAddUser.setVisibility(View.VISIBLE);
        if (menuItemRemove != null) {
            menuItemRemove.setVisible(true);
        }
        if (menuItemCancel != null)
            menuItemCancel.setVisible(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REMOVE_USER_PROFILE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    new Handler(Looper.getMainLooper()).postDelayed(this::showUserRemovedDialog, 200);
                    break;
                case RESULT_FAILED:
                    showGenericErrorMessage();
                    break;
            }
        }
    }

    private void showUserRemovedDialog() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.user_successfully_removed))
                .positiveButton(getString(R.string.continue_title))
                .positiveDismissListener((dialog, which) -> {
                    loadUserProfileList(new ProfileManager.OnProfileLoadListener() {
                        @Override
                        public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                            originalUserProfileList = userProfiles;
                            Collections.sort(originalUserProfileList, userProfileSortComparator);
                            backingUserProfileList = originalUserProfileList;
                            numberOfProfiles = backingUserProfileList.size();
                            setNumberOfProfilesText();
                            createMultipleUsersAdapter(false);
                            userRemoved = true;
                            if (userProfiles.size() == 0) {
                                goToLaunchScreen(MultipleUsersListActivity.this);
                                finish();
                            }
                        }

                        @Override
                        public void onProfilesLoadFailed() {
                            goToLaunchScreen(MultipleUsersListActivity.this);
                            finish();
                        }
                    });
                    if (menuItemRemove != null && menuItemCancel != null) {
                        menuItemRemove.setVisible(true);
                        menuItemCancel.setVisible(false);
                    }
                })
                .build());
    }

    private void setNumberOfProfilesText() {
        String profilesLinkedText = "";
        if (numberOfProfiles == 1) {
            profilesLinkedText = getString(R.string.profiles_linked_singular, numberOfProfiles, Build.MANUFACTURER + " " + Build.MODEL);
        } else if (numberOfProfiles > 1) {
            profilesLinkedText = getString(R.string.profiles_linked_plural, numberOfProfiles, Build.MANUFACTURER + " " + Build.MODEL);
        }
        binding.tvNumberOfProfilesLinked.setText(profilesLinkedText);
    }

    private void setupSearchView(SearchView mSearchView) {
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryRefinementEnabled(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (numberOfProfiles > SEARCH_THRESHOLD) {
            if (query != null) {
                performSearch(query.trim());
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (numberOfProfiles > SEARCH_THRESHOLD) {
            if (newText != null) {
                performSearch(newText.trim());
            }
        }
        return false;
    }

    private void performSearch(String query) {
        if (filter != null) {
            filter.filter(query);
        }
    }

    class MultipleUsersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
        private final boolean shouldShowRemoveButton;
        private BeneficiaryImageHelper imageHelper;

        MultipleUsersListAdapter(boolean isRemovalList) {
            shouldShowRemoveButton = isRemovalList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            imageHelper = new BeneficiaryImageHelper(MultipleUsersListActivity.this);
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            @LayoutRes int layoutToUse = shouldShowRemoveButton ? R.layout.list_item_multiple_users_remove : R.layout.list_item_multiple_users;
            ViewDataBinding multipleUsersBinding = DataBindingUtil.inflate(inflater, layoutToUse, parent, false);
            return new MultipleUsersViewHolder(multipleUsersBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MultipleUsersViewHolder) {
                MultipleUsersViewHolder holder = (MultipleUsersViewHolder) viewHolder;
                UserProfile userProfile = getUserProfile(position);

                if (userProfile != null) {
                    String customerName = userProfile.getCustomerName();
                    int clientType = R.string.personal_banking_account;
                    if (userProfile.getClientType() != null && ClientTypeGroupKt.isBusiness(userProfile.getClientType())) {
                        clientType = R.string.business_banking_account;
                    }

                    byte[] imageData = null;
                    if (userProfile.getImageName() != null && !userProfile.getImageName().isEmpty()) {
                        imageData = Base64.decode(userProfile.getImageName(), Base64.URL_SAFE);
                    }

                    if (shouldShowRemoveButton) {
                        holder.multipleUsersRemoveBinding.itemLayout.accountTypeTextView.setText(clientType);
                        holder.multipleUsersRemoveBinding.itemLayout.userImageView.setImageResource(R.drawable.ic_no_profile);
                        holder.multipleUsersRemoveBinding.itemLayout.usernameTextView.setText(customerName);
                        setProfileBadge(holder.multipleUsersRemoveBinding.itemLayout.userImageView, imageData);

                        imageHelper.setBeneficiaryImage(userProfile.getImageName(), holder.multipleUsersRemoveBinding.itemLayout.userImageView);
                        if (customerName != null && searchText != null) {
                            CommonUtils.highlightSearchString(searchText.toString(), customerName, holder.multipleUsersRemoveBinding.itemLayout.usernameTextView);
                        }
                    } else {
                        holder.multipleUsersListItemBinding.accountTypeTextView.setText(clientType);
                        holder.multipleUsersListItemBinding.usernameTextView.setText(customerName);
                        setProfileBadge(holder.multipleUsersListItemBinding.userImageView, imageData);

                        imageHelper.setBeneficiaryImage(userProfile.getImageName(), holder.multipleUsersListItemBinding.userImageView);
                        if (customerName != null && searchText != null) {
                            CommonUtils.highlightSearchString(searchText.toString(), customerName, holder.multipleUsersListItemBinding.usernameTextView);
                        }
                    }
                }
            }
        }

        void setProfileBadge(RoundedImageView profilePicture, byte[] imageBytes) {
            // This will null the image when incorrect information is written to imageBytes which is not an actual image
            if (imageBytes != null && imageBytes.length < 100) {
                imageBytes = null;
            }

            if (imageBytes == null) {
                profilePicture.setImageResource(R.drawable.ic_no_profile);
            } else {
                Bitmap profileBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ImageUtils.setImageFromBitmap(profilePicture, profileBitmap);
            }
        }

        @Override
        public int getItemCount() {
            return backingUserProfileList == null ? 0 : backingUserProfileList.size();
        }

        @Override
        public Filter getFilter() {
            return new UserFilter();
        }

        class MultipleUsersViewHolder extends RecyclerView.ViewHolder {
            ListItemMultipleUsersBinding multipleUsersListItemBinding;
            ListItemMultipleUsersRemoveBinding multipleUsersRemoveBinding;

            MultipleUsersViewHolder(ViewDataBinding binding) {
                super(binding.getRoot());
                View.OnClickListener clickListener;
                if (shouldShowRemoveButton) {
                    multipleUsersRemoveBinding = (ListItemMultipleUsersRemoveBinding) binding;
                    clickListener = v -> {
                        final UserProfile profileToDelete = getUserProfile(getAdapterPosition());
                        if (profileToDelete != null) {
                            Intent removeUserIntent = new Intent(MultipleUsersListActivity.this, RemoveAccountConfirmationActivity.class);
                            removeUserIntent.putExtra(SimplifiedLoginActivity.USER_PROFILE, profileToDelete);
                            startActivityForResult(removeUserIntent, REMOVE_USER_PROFILE_REQUEST_CODE);
                        }
                    };
                    multipleUsersRemoveBinding.btnRemove.setOnClickListener(clickListener);
                } else {
                    multipleUsersListItemBinding = (ListItemMultipleUsersBinding) binding;
                    clickListener = v -> {
                        animate(v, R.anim.contract_horizontal);
                        UserProfile userProfile = getUserProfile(getAdapterPosition());
                        if (userProfile != null) {
                            Intent intent = IntentFactory.getSimplifiedLogonActivity(MultipleUsersListActivity.this, userProfile);
                            startActivity(intent);
                        }
                    };
                    binding.getRoot().setOnClickListener(clickListener);
                }
            }
        }

        UserProfile getUserProfile(int position) {
            return 0 <= position && position < backingUserProfileList.size() ? backingUserProfileList.get(position) : null;
        }

        void updateList(List<UserProfile> userProfileList) {
            backingUserProfileList = userProfileList;
            notifyDataSetChanged();
        }

        class UserFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                ArrayList<UserProfile> results = new ArrayList<>();
                if (lastResultWasEmptyResult) {
                    backingUserProfileList = originalUserProfileList;
                    lastResultWasEmptyResult = false;
                }
                if (backingUserProfileList != null && constraint != null && !TextUtils.isEmpty(constraint.toString())) {
                    for (UserProfile profile : backingUserProfileList) {
                        if (!TextUtils.isEmpty(profile.getCustomerName()) && profile.getCustomerName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            searchText = constraint;
                            results.add(profile);
                        }
                    }
                    filterResults.values = results;
                    if (results.isEmpty()) {
                        lastResultWasEmptyResult = true;
                    }
                } else {
                    filterResults.values = originalUserProfileList;
                    searchText = null;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updateList((List<UserProfile>) results.values);
            }
        }
    }

    public void addUser() {
        AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_NewUserScreen_AddUserButtonClicked");
        int profileCount = ProfileManager.getInstance().getProfileCount();
        if (profileCount == 1 && UserSettingsManager.INSTANCE.isFingerprintActive()) {
            Intent launcherActivityIntent = new Intent(MultipleUsersListActivity.this, MultipleUsersFingerprintWarningActivity.class);
            startActivity(launcherActivityIntent);
        } else {
            getDeviceProfilingInteractor().notifyLogin();
            Intent launcherActivityIntent = new Intent(MultipleUsersListActivity.this, WelcomeActivity.class);
            startActivity(launcherActivityIntent);
        }
    }
}

