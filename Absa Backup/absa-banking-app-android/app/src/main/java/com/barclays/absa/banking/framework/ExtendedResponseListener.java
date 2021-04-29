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

package com.barclays.absa.banking.framework;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;

import java.lang.ref.WeakReference;

import za.co.absa.networking.error.ApplicationError;
import za.co.absa.networking.error.ApplicationErrorType;

public abstract class ExtendedResponseListener<T> extends AbstractResponseListener<T> {

    protected WeakReference<BaseView> viewWeakReference;

    public ExtendedResponseListener() {
    }

    public ExtendedResponseListener(BaseView view) {
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + " instantiated with view: " + view.getClass());
        }
        viewWeakReference = new WeakReference<>(view);
    }

    public void setView(BaseView view) {
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "View " + view.getClass().getSimpleName() + " injected via setter in ExtendedResponseListener: " + getClass());
        }
        viewWeakReference = new WeakReference<>(view);
    }

    public void onSuccess() {
        BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + "#onSuccess(): dismissing progressDialog in view: " + view.getClass());
            view.dismissProgressDialog();
        }
    }

    public void onRequestStarted() {
        BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener: " + getClass() + " showing progressDialog in view: " + view.getClass());
            view.showProgressDialog();
        }
    }

    public void onPolling() {

    }

    public void onFailure(final ResponseObject failureResponse) {
        BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + "#onFailure(): dismissing progressDialog in view: " + view.getClass());
            view.dismissProgressDialog();
            if (failureResponse != null && AppConstants.SSLPinningError.equalsIgnoreCase(failureResponse.getErrorMessage())) {
                view.showMessageError(new ApplicationError(ApplicationErrorType.CERTIFICATE_PINNING, ExtendedResponseListener.class.getSimpleName(), ""));
            } else {
                view.showMessageError(ResponseObject.extractErrorMessage(failureResponse));
            }
        }
    }

    public void onFailureDefault(final ResponseObject failureResponse) {
        BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + "#onFailure(): dismissing progressDialog in view: " + view.getClass());
            view.dismissProgressDialog();
            view.showMessageError(ResponseObject.extractErrorMessage(failureResponse));
        }
    }

    protected void onNetworkUnreachable(final ResponseObject failureResponse) {
        BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + "#onNetworkUnreachable(): dismissing progressDialog in view: " + view.getClass());
            view.dismissProgressDialog();
            view.showMessageError(ResponseObject.extractErrorMessage(failureResponse));
        }
    }

    void forceLogout() {
        BaseView view = getBaseView();
        if (view != null && !(view instanceof SimplifiedLoginActivity || view instanceof WelcomeActivity)) {
            view.logoutAndGoToStartScreen();
        }
    }

    public void onMaintenanceError() {
        BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + "#onMaintenanceError(): dismissing progressDialog in view: " + view.getClass());
            view.dismissProgressDialog();
            view.checkDeviceState();
        }
    }

    @Override
    public void onInvalidAuthLevel() {
        final BaseView view = getBaseView();
        if (view != null) {
            BMBLogger.d(ExtendedResponseListener.class.getSimpleName(), "ExtendedResponseListener " + getClass() + "#onInvalidAuthLevel(): dismissing progressDialog in view: " + view.getClass());
            view.dismissProgressDialog();
            view.logoutAndGoToStartScreen();
        }
    }

    public BaseView getBaseView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        } else if (BMBApplication.getInstance() != null && BMBApplication.getInstance().getTopMostActivity() instanceof BaseActivity) {
            return ((BaseView) BMBApplication.getInstance().getTopMostActivity());
        }
        return null;
    }

}
