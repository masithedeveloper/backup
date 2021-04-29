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
package com.barclays.absa.banking.passcode.passcodeLogin;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.databinding.Activity2faRemoveProfileBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;

import java.util.List;

import static com.barclays.absa.banking.presentation.multipleUsers.MultipleUsersListActivity.RESULT_FAILED;

public class RemoveAccountConfirmationActivity extends BaseActivity implements View.OnClickListener {
    private UserProfile userProfile;
    private Activity2faRemoveProfileBinding binding;
    private ExtendedResponseListener<TransactionVerificationValidateCodeResponse> deleteAliasResponseListerner = new ExtendedResponseListener<TransactionVerificationValidateCodeResponse>() {
        @Override
        public void onSuccess(final TransactionVerificationValidateCodeResponse response) {
            if (BMBConstants.SUCCESS.equalsIgnoreCase(response.getTxnStatus()) || response.getTxnMessage().contains("StatusCodeAliasNotFound")) {
                deleteAliasLocally();
            } else {
                setResult(RESULT_FAILED);
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_remove_profile, null, false);
        setContentView(binding.getRoot());

        deleteAliasResponseListerner.setView(this);
        binding.btnYes.setOnClickListener(this);
        binding.btnNo.setOnClickListener(this);
        userProfile = (UserProfile) getIntent().getSerializableExtra(SimplifiedLoginActivity.USER_PROFILE);
        if (userProfile != null) {
            String customerName = userProfile.getCustomerName();
            if (customerName != null) {
                binding.tvMessage.setText(getString(R.string.remove_user_profile, customerName));
            }
        }
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        switch (v.getId()) {
            case R.id.btn_yes:
                loadUserProfilesThenExecuteDeleteRequest();
                break;
            case R.id.btn_no:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private void deleteAliasLocally() {
        ProfileManager.getInstance().deleteProfile(userProfile, new ProfileManager.SimpleCallback() {

            @Override
            public void onSuccess() {
                ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
                    @Override
                    public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                        setResult(RESULT_OK);
                        dismissProgressDialog();
                        finish();
                    }

                    @Override
                    public void onProfilesLoadFailed() {
                        setResult(RESULT_OK);
                        dismissProgressDialog();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                showGenericErrorMessage();
                dismissProgressDialog();
            }
        });
    }

    private void loadUserProfilesThenExecuteDeleteRequest() {
        showProgressDialog();
        if (userProfile != null) {
            String aliasID = extractAlias(userProfile);
            if (aliasID.isEmpty()) {
                deleteAliasLocally();
            } else {
                String deviceID = SecureUtils.INSTANCE.getDeviceID();
                deleteAliasRemotelyThenLocally(aliasID, deviceID);
            }
        } else {
            dismissProgressDialog();
            showGenericErrorMessage();
        }
    }

    private String extractAlias(UserProfile profile) {
        if (profile != null) {
            SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
            try {
                String EXISTING_USER_ALIAS_KEY = "alias_key";
                byte[] aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(EXISTING_USER_ALIAS_KEY);
                if (aliasEncryptedWithZeroKey != null) { // Legacy code: no multiple users feature
                    aliasEncryptedWithZeroKey = symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey);
                } else {// new App with multiple users feature
                    aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(profile.getUserId());
                }
                if (aliasEncryptedWithZeroKey != null) {
                    byte[] aliasBytes = symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey);
                    if (aliasBytes != null) {
                        return new String(aliasBytes);
                    }
                }
            } catch (SymmetricCryptoHelper.DecryptionFailureException | SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void deleteAliasRemotelyThenLocally(String aliasID, String deviceID) {
        new TransactionVerificationInteractor().deleteAlias(aliasID, deviceID, deleteAliasResponseListerner);
    }
}