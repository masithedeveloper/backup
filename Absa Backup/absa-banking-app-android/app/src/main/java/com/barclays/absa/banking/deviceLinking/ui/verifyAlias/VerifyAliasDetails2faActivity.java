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
package com.barclays.absa.banking.deviceLinking.ui.verifyAlias;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.databinding.Activity2faVerifyAliasDetailsBinding;
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity;
import com.barclays.absa.banking.deviceLinking.ui.LinkingPasswordValidationActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.passcode.createPasscode.CreatePasscodeActivity;
import com.barclays.absa.banking.presentation.adapters.TabLayoutFragmentAdapter;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.banking.registration.services.dto.AliasUpdate2faRequest;
import com.barclays.absa.banking.registration.services.dto.AliasUpdate2faResponse;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.CardViewErrorUtil;
import com.barclays.absa.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class VerifyAliasDetails2faActivity extends BaseActivity implements View.OnClickListener, VerifyAliasDetailsView {

    public static final int CREATE_NICKAME_SCREEN = 0;
    public static final int ENTER_PASSWORD_SCREEN = 2;
    public static final int ACCOUNT_LOGIN_SCREEN = 3;
    private final int ID_TAB_POSITION = 0;
    private final int PASSPORT_TAB_POSITION = 1;
    public static final String VERIFY_ALIAS_FROM_SCREEN = "verify_alias_from_screen";

    private Activity2faVerifyAliasDetailsBinding binding;
    private TabLayoutFragmentAdapter adapter;
    private VerifyAliasDetailsPresenter presenter;
    private int sourceScreen = -1;
    private TransaktHandler transaktHandler;

    private final ExtendedResponseListener<AliasUpdate2faResponse> updateUserIdResponseListener = new ExtendedResponseListener<AliasUpdate2faResponse>(this) {

        @Override
        public void onSuccess(final AliasUpdate2faResponse aliasIdUserUpdateResponse) {
            SymmetricCryptoHelper.getInstance();

            dismissProgressDialog();
            Intent continueIntent;
            String aliasId = aliasIdUserUpdateResponse.getAliasID();
            if (aliasId != null) {
                CustomerProfileObject.getInstance().setAlias(aliasId);
                getAppCacheService().setCreate2faAliasResponse(aliasIdUserUpdateResponse);
                getAppCacheService().setEnrollingUserAliasID(aliasId);
                showCreatePasscodeScreen();
            } else if (sourceScreen == CREATE_NICKAME_SCREEN) {
                continueIntent = new Intent(VerifyAliasDetails2faActivity.this, CreateNicknameActivity.class);
                startActivity(continueIntent);
            }
            if (sourceScreen == ACCOUNT_LOGIN_SCREEN) {
                continueIntent = new Intent(VerifyAliasDetails2faActivity.this, LinkingPasswordValidationActivity.class);
                startActivity(continueIntent);
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            Intent linkIntent = new Intent(VerifyAliasDetails2faActivity.this, LinkingPasswordValidationActivity.class);
            BMBLogger.d("Failure", failureResponse.toString());
            startActivity(linkIntent);
        }
    };

    public void showCreatePasscodeScreen() {
        Intent passcodeIntent = new Intent(this, CreatePasscodeActivity.class);
        startActivity(passcodeIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_verify_alias_details, null, false);
        setContentView(binding.getRoot());

        RebuildUtils.setupToolBar(this, null, 0, false, null);

        binding.btnConfirmDetails.setOnClickListener(this);
        presenter = new VerifyAliasDetailsPresenter(this);

        List<Fragment> tabFragments = new ArrayList<>();
        tabFragments.add(VerificationNumber2faFragment.newInstance(ID_TAB_POSITION));
        tabFragments.add(VerificationNumber2faFragment.newInstance(PASSPORT_TAB_POSITION));

        adapter = new TabLayoutFragmentAdapter(getSupportFragmentManager(), tabFragments);
        binding.vpVerificationNumber.setAdapter(adapter);
        binding.tlVerificationDetailsType.setupWithViewPager(binding.vpVerificationNumber);

        binding.tlVerificationDetailsType.removeAllTabs();
        binding.tlVerificationDetailsType.addTab(binding.tlVerificationDetailsType.newTab().setText(getString(R.string.id)));
        binding.tlVerificationDetailsType.addTab(binding.tlVerificationDetailsType.newTab().setText(getString(R.string.passport)));

        sourceScreen = getIntent().getIntExtra(VERIFY_ALIAS_FROM_SCREEN, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirmDetails) {
            TextInputLayout etVerificationType = adapter.getItem(binding.tlVerificationDetailsType.getSelectedTabPosition()).getView().findViewById(R.id.til_verificationType);
            TextInputEditText etVerificationNumber = adapter.getItem(binding.tlVerificationDetailsType.getSelectedTabPosition()).getView().findViewById(R.id.et_verificationNumber);
            String number = etVerificationNumber.getText().toString().trim();
            if (binding.tlVerificationDetailsType.getSelectedTabPosition() == ID_TAB_POSITION) {
                if (ValidationUtils.isValidSouthAfricanIdNumber(number)) {
                    presenter.updateIdInvoked(number, "SA_ID");
                } else {
                    CardViewErrorUtil.showError(getString(R.string.id_number_errormessage), etVerificationType, binding.tvDescription, binding.cvAliasInformation, null, getString(R.string.select_type_of_verification), R.color.color_FFFFFFFF);
                }
            } else if (binding.tlVerificationDetailsType.getSelectedTabPosition() == PASSPORT_TAB_POSITION) {
                presenter.updateIdInvoked(number, "PASSPORT");
            }
        }
    }

    @Override
    public void callUpdateUserIdService(final String idNumber, final String idType) {
        TransaktDelegate transaktDelegate = new TransaktDelegate() {
            @Override
            protected void onGenerateTrustTokenSuccess(String trustToken) {
                super.onGenerateTrustTokenSuccess(trustToken);
                AliasUpdate2faRequest<AliasUpdate2faResponse> aliasIdUserUpdateRequest = new AliasUpdate2faRequest<>(idNumber, idType, updateUserIdResponseListener);
                aliasIdUserUpdateRequest.setMockResponseFile("registration/op0821_create_2fa_alias.json");
                ServiceClient serviceClient = new ServiceClient(aliasIdUserUpdateRequest);
                serviceClient.submitRequest();
            }

            @Override
            public void onConnected() {
                super.onConnected();
                transaktHandler.generateTrustToken();
            }
        };

        transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        transaktHandler.setConnectCallbackTriggeredFlag(false);
        transaktHandler.setTransaktDelegate(transaktDelegate);
        transaktHandler.start();
    }
}