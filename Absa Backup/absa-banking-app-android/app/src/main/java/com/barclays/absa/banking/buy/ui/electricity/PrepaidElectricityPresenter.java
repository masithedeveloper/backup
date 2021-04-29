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
package com.barclays.absa.banking.buy.ui.electricity;

import android.content.Context;
import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.AddPrepaidElectricityBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityInteractor;
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthElectricityPendingActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AlertBox;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static com.barclays.absa.banking.framework.app.BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION;
import static com.barclays.absa.banking.framework.app.BMBConstants.DUPLICATE_BENEFICIARY_ERRORCODE;
import static com.barclays.absa.banking.framework.app.BMBConstants.FAILURE;
import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;

class PrepaidElectricityPresenter {

    private boolean isRepeatPurchase;
    private long startTimeMillis;
    private WeakReference<PrepaidElectricityView> prepaidElectricityViewWeakReference;

    private final PrepaidElectricityInteractor prepaidElectricityInteractor;
    private final BeneficiariesInteractor beneficiariesInteractor;

    private FlowType flowType;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    enum FlowType {
        HISTORY,
        NEW_BENEFICIARY,
        EXISTING_BENEFICIARY,
        NEW_BENEFICIARY_MANAGE_BENEFICIARY_FLOW
    }

    PrepaidElectricityPresenter(PrepaidElectricityView prepaidElectricityView) {
        prepaidElectricityViewWeakReference = new WeakReference<>(prepaidElectricityView);

        prepaidElectricityInteractor = new PrepaidElectricityInteractor();
        beneficiariesInteractor = new BeneficiariesInteractor();

        beneficiaryDetailExtendedResponseListener.setView(prepaidElectricityView);
        validateMeterNumberExtendedResponseListener.setView(prepaidElectricityView);
        validateMeterForHistoryExtendedResponseListener.setView(prepaidElectricityView);
        listElectricityBeneficiaryResponseListener.setView(prepaidElectricityView);
        validateRecipientMeterNumberExtendedResponseListener.setView(prepaidElectricityView);
        addPrepaidBeneficiaryExtendedResponseListener.setView(prepaidElectricityView);
        buyElectricityResponseListener.setView(prepaidElectricityView);
    }

    private ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailExtendedResponseListener = new ExtendedResponseListener<BeneficiaryDetailsResponse>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final BeneficiaryDetailsResponse beneficiaryDetailObject) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null) {
                prepaidElectricityView.dismissProgressDialog();
                prepaidElectricityView.saveBeneficiaryDetailsObject(beneficiaryDetailObject.getBeneficiaryDetails());
                if (flowType == FlowType.HISTORY) {
                    prepaidElectricityView.navigateToPurchaseHistorySelectedFragment();
                } else if ("duplicatePayment".equalsIgnoreCase(beneficiaryDetailObject.getTransactionMessage()) && !isRepeatPurchase) {
                    prepaidElectricityView.dismissProgressDialog();
                    prepaidElectricityView.showDuplicatePurchaseErrorMessage();
                } else if (isRepeatPurchase) {
                    prepaidElectricityView.closeResultScreen();
                } else if (flowType == FlowType.EXISTING_BENEFICIARY) {
                    prepaidElectricityView.navigateToPurchaseDetailsFragment();
                } else if (flowType == FlowType.NEW_BENEFICIARY) {
                    prepaidElectricityView.navigateToPurchaseDetailsFragment();
                } else if (flowType == FlowType.NEW_BENEFICIARY_MANAGE_BENEFICIARY_FLOW) {
                    prepaidElectricityView.navigateToBeneficiaryAddedResultScreen();
                }
            }
        }
    };

    private ExtendedResponseListener<MeterNumberObject> validateMeterNumberExtendedResponseListener = new ExtendedResponseListener<MeterNumberObject>() {

        @Override
        public void onSuccess(MeterNumberObject meterNumberObject) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null) {
                prepaidElectricityView.saveMeterNumberObject(meterNumberObject);
                if (SUCCESS.equalsIgnoreCase(meterNumberObject.getTransactionStatus())) {
                    fetchBeneficiaryDetails(prepaidElectricityView.getSelectedBeneficiaryObject().getBeneficiaryID(), false);
                } else {
                    prepaidElectricityView.dismissProgressDialog();
                    if ("Payments/PurchasePrepaid/Validation/TandemTimeOut_RC91".equalsIgnoreCase(meterNumberObject.getTransactionMessage())) {
                        prepaidElectricityView.showTandemTimeoutErrorMessage();
                    } else {
                        prepaidElectricityView.navigateToProblemWithMeterNumberActivity();
                    }
                }
            }
        }
    };

    public void fetchBeneficiaryDetails(String beneficiaryID, boolean isRepeatBuy) {
        isRepeatPurchase = isRepeatBuy;
        beneficiariesInteractor.fetchBeneficiaryDetails(beneficiaryID, BMBConstants.PASS_PREPAID_ELECTRICITY, beneficiaryDetailExtendedResponseListener);
    }

    private ExtendedResponseListener<MeterNumberObject> validateMeterForHistoryExtendedResponseListener = new ExtendedResponseListener<MeterNumberObject>() {

        @Override
        public void onSuccess(MeterNumberObject meterNumberObject) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null && meterNumberObject != null && SUCCESS.equalsIgnoreCase(meterNumberObject.getTransactionStatus())) {
                prepaidElectricityView.saveMeterNumberObject(meterNumberObject);
                fetchBeneficiaryDetails(prepaidElectricityView.getSelectedBeneficiaryObject().getBeneficiaryID(), false);
            } else if (getBaseView() != null) {
                getBaseView().showGenericErrorMessage();
                getBaseView().dismissProgressDialog();
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> listElectricityBeneficiaryResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(final BeneficiaryListObject beneficiaryListObject) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null) {
                prepaidElectricityView.dismissProgressDialog();
                prepaidElectricityView.setBeneficiariesFragment(beneficiaryListObject);
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null && failureResponse != null) {
                prepaidElectricityView.dismissProgressDialog();
                prepaidElectricityView.setBeneficiariesFragment(new BeneficiaryListObject());
                //noinspection StatementWithEmptyBody
                if (AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equalsIgnoreCase(failureResponse.getResponseCode())) {
                    // NO beneficiaries found maybe do something extra?
                } else {
                    BaseAlertDialog.INSTANCE.showRetryErrorDialog(ResponseObject.extractErrorMessage(failureResponse), new AlertBox.AlertRetryListener() {
                        @Override
                        public void retry() {
                            prepaidElectricityView.requestBeneficiaries();
                        }
                    });
                }
            }
        }
    };

    private ExtendedResponseListener<MeterNumberObject> validateRecipientMeterNumberExtendedResponseListener = new ExtendedResponseListener<MeterNumberObject>() {
        @Override
        public void onSuccess(final MeterNumberObject meterNumberObject) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null && meterNumberObject != null) {
                prepaidElectricityView.dismissProgressDialog();
                prepaidElectricityView.saveMeterNumberObject(meterNumberObject);

                if (SUCCESS.equalsIgnoreCase(meterNumberObject.getTransactionStatus())) {
                    prepaidElectricityView.navigateToSomeoneNewBeneficiaryDetailsFragment();
                } else {
                    prepaidElectricityView.showRecipientMeterError(meterNumberObject.getTransactionMessage());
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null && failureResponse != null) {
                prepaidElectricityView.dismissProgressDialog();
                prepaidElectricityView.showRecipientMeterError(failureResponse.getErrorMessage());
            }
        }
    };

    private ExtendedResponseListener<AddPrepaidElectricityBeneficiaryObject> addPrepaidBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AddPrepaidElectricityBeneficiaryObject>() {
        @Override
        public void onSuccess(AddPrepaidElectricityBeneficiaryObject addPrepaidElectricityBeneficiaryObject) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null) {
                if (addPrepaidElectricityBeneficiaryObject != null && SUCCESS.equalsIgnoreCase(addPrepaidElectricityBeneficiaryObject.getResponseMessage())) {
                    try {
                        if (addPrepaidElectricityBeneficiaryObject.getErrorCode() != null &&
                                DUPLICATE_BENEFICIARY_ERRORCODE.equalsIgnoreCase(addPrepaidElectricityBeneficiaryObject.getErrorCode()) &&
                                FAILURE.equalsIgnoreCase(addPrepaidElectricityBeneficiaryObject.getStatus())) {
                            prepaidElectricityView.dismissProgressDialog();
                            prepaidElectricityView.showBeneficiaryExistDialog();
                        } else {
                            fetchBeneficiaryDetails(addPrepaidElectricityBeneficiaryObject.getBeneficiaryID(), false);
                            beneficiaryCacheService.setTabPositionToReturnTo("PPE");
                            beneficiaryCacheService.setBeneficiaryAdded(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getBaseView().showGenericErrorMessage();
                    getBaseView().dismissProgressDialog();
                }
            }
        }
    };

    private ExtendedResponseListener<PrepaidElectricity> buyElectricityResponseListener = new ExtendedResponseListener<PrepaidElectricity>() {
        @Override
        public void onSuccess(final PrepaidElectricity prepaidElectricityResponse) {
            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null) {
                buyElectricitySureCheckDelegate.processSureCheck(prepaidElectricityView, prepaidElectricityResponse, () -> {
                    prepaidElectricityView.dismissProgressDialog();
                    if (SUCCESS.equalsIgnoreCase(prepaidElectricityResponse.getTransactionStatus()) && AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(prepaidElectricityResponse.getTransactionMessage())) {
                        Intent authorisationIntent = new Intent((Context) prepaidElectricityView, DualAuthElectricityPendingActivity.class);
                        authorisationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ((Context) prepaidElectricityView).startActivity(authorisationIntent);
                    } else if (SUCCESS.equalsIgnoreCase(prepaidElectricityResponse.getTransactionStatus())) {
                        prepaidElectricityView.navigateToPurchaseSuccessfulFragment(prepaidElectricityResponse);
                    } else {
                        prepaidElectricityView.navigateToPurchaseUnsuccessfulFragment(prepaidElectricityResponse);
                    }
                });
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            recordMonitoringEvent(failureResponse);

            PrepaidElectricityView prepaidElectricityView = prepaidElectricityViewWeakReference.get();

            if (prepaidElectricityView != null) {
                prepaidElectricityView.dismissProgressDialog();

                String errorMessage = ResponseObject.extractErrorMessage(failureResponse);

                GenericResultActivity.bottomOnClickListener = v -> prepaidElectricityView.loadAccountsAndShowHomeScreenWithAccountsList();
                Intent intent = new Intent((Context) prepaidElectricityView, GenericResultActivity.class);
                intent.putExtra(GenericResultActivity.IS_FAILURE, true);
                intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
                intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, errorMessage);
                intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
                ((Context) prepaidElectricityView).startActivity(intent);
            }
        }
    };

    private SureCheckDelegate buyElectricitySureCheckDelegate;

    private void recordMonitoringEvent(ResponseObject response) {
        long endTimeMillis = System.currentTimeMillis();
        long elapsedTime = endTimeMillis - startTimeMillis;
        Map<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME, elapsedTime);
        new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_ELECTRICITY_PURCHASE, response, eventData);
    }

    void validateMeterNumber(String meterNumber) {
        flowType = FlowType.EXISTING_BENEFICIARY;
        prepaidElectricityInteractor.validateMeterNumber(meterNumber, validateMeterNumberExtendedResponseListener);
    }

    void validateRecipientMeterNumber(String recipientMeterNumber, boolean manageBeneficiaryFlow) {
        if (manageBeneficiaryFlow) {
            flowType = FlowType.NEW_BENEFICIARY_MANAGE_BENEFICIARY_FLOW;
        } else {
            flowType = FlowType.NEW_BENEFICIARY;
        }
        prepaidElectricityInteractor.validateMeterNumber(recipientMeterNumber, validateRecipientMeterNumberExtendedResponseListener);
    }

    void requestBeneficiaryList() {
        beneficiariesInteractor.fetchBeneficiaryList(BMBConstants.PASS_PREPAID_ELECTRICITY, listElectricityBeneficiaryResponseListener);
    }

    void addPrepaidElectricityBeneficiary(String beneficiaryName, String meterNumber, String utility) {
        prepaidElectricityInteractor.addPrepaidElectricityBeneficiary(beneficiaryName, meterNumber, utility, addPrepaidBeneficiaryExtendedResponseListener);
    }

    void confirmPurchase(PurchasePrepaidElectricityResultObject purchasePrepaidElectricityResultObject) {
        prepaidElectricityInteractor.purchasePrepaidElectricity(purchasePrepaidElectricityResultObject, buyElectricityResponseListener);
    }

    void fetchHistoryForMeterNumber(String recipientMeterNumber) {
        flowType = FlowType.HISTORY;
        prepaidElectricityInteractor.validateMeterNumber(recipientMeterNumber, validateMeterForHistoryExtendedResponseListener);
    }

    void setUpSureCheckDelegate(SureCheckDelegate sureCheckDelegate) {
        this.buyElectricitySureCheckDelegate = sureCheckDelegate;
    }
}