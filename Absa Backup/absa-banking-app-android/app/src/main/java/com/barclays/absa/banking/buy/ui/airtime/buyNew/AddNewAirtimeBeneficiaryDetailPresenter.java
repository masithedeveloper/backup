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

package com.barclays.absa.banking.buy.ui.airtime.buyNew;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult;
import com.barclays.absa.banking.boundary.model.airtime.AddedBeneficiary;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.buy.services.airtime.AddBeneficiaryConfirmResponseListener;
import com.barclays.absa.banking.buy.services.airtime.AirtimeBeneficiaryDetailsExtendedResponseListener;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.buy.services.airtime.UploadBeneficiaryImageExtendedResponseListener;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.ImageUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.framework.app.BMBConstants.MIME_TYPE_JPG;
import static com.barclays.absa.banking.framework.app.BMBConstants.SERVICE_ACTIONTYPE_ADD;

public class AddNewAirtimeBeneficiaryDetailPresenter extends AbstractPresenter {

    private final AddBeneficiaryDAO addBeneficiaryDAO;
    private Bitmap benImage;
    private final PrepaidInteractor prepaidInteractor = new PrepaidInteractor();
    private AirtimeBeneficiaryDetailsExtendedResponseListener requestAirtimeBeneficiaryDetailsExtendedResponseListener;
    private UploadBeneficiaryImageExtendedResponseListener uploadBeneficiaryImageResponseListener;
    private AddBeneficiaryConfirmResponseListener addBeneficiaryConfirmResponseListener;

    AddNewAirtimeBeneficiaryDetailPresenter(WeakReference<AddNewAirtimeBeneficiaryDetailView> view, AddBeneficiaryDAO addBeneficiaryDAO, Bitmap benImage) {
        super(view);
        this.addBeneficiaryDAO = addBeneficiaryDAO;
        this.benImage = benImage;
        requestAirtimeBeneficiaryDetailsExtendedResponseListener = new AirtimeBeneficiaryDetailsExtendedResponseListener(this);
        uploadBeneficiaryImageResponseListener = new UploadBeneficiaryImageExtendedResponseListener(this);
        addBeneficiaryConfirmResponseListener = new AddBeneficiaryConfirmResponseListener(this);
    }

    public void beneficiaryDetailsResponseReceived(AirtimeBuyBeneficiary airtimeBuyBeneficiary) {
        AddNewAirtimeBeneficiaryDetailView view = (AddNewAirtimeBeneficiaryDetailView) viewWeakReference.get();
        if (view != null) {
            if (airtimeBuyBeneficiary != null) {
                view.navigateToPrepaidPurchaseDetailsScreen(airtimeBuyBeneficiary);
            }
            dismissProgressIndicator();
        }
    }

    public void imageUploadResponseReceived(AddBeneficiaryObject addBeneficiaryObject) {
        AddNewAirtimeBeneficiaryDetailView view = (AddNewAirtimeBeneficiaryDetailView) viewWeakReference.get();
        if (view != null) {
            if (addBeneficiaryObject != null) {
                if (BMBConstants.SUCCESS.equalsIgnoreCase(addBeneficiaryObject.getStatus())) {
                    addBeneficiaryObject.setImageData(ImageUtils.convertToByteArray(benImage));
                    addBeneficiaryDAO.saveBeneficiary(addBeneficiaryObject);
                    view.showResultScreen(addBeneficiaryObject.getStatus(), addBeneficiaryObject.getBeneficiaryId(), addBeneficiaryObject.getMsg());
                } else {
                    view.showErrorScreen(addBeneficiaryObject.getMsg());
                }
            }
            dismissProgressIndicator();
        }
    }

    public void beneficiaryAddedResponseConfirmationReceived(AddBeneficiaryResult addBeneficiaryResult) {
        if (addBeneficiaryResult != null) {
            processResponse(addBeneficiaryResult);
        }
    }

    private void processResponse(ResponseObject response) {
        AddNewAirtimeBeneficiaryDetailView view = (AddNewAirtimeBeneficiaryDetailView) viewWeakReference.get();
        if (view != null) {
            if (response instanceof AddBeneficiaryResult) {
                AddBeneficiaryResult addBeneficiaryResult = (AddBeneficiaryResult) response;
                AddedBeneficiary addedBeneficiary = addBeneficiaryResult.getAddedBeneficiary();
                if (addedBeneficiary != null) {
                    if (BMBConstants.SUCCESS.equalsIgnoreCase(addBeneficiaryResult.getTransactionStatus())) {
                        if (hasImage()) {
                            try {
                                if (TextUtils.isEmpty(addedBeneficiary.getBeneficiaryId())) {
                                    if (addBeneficiaryResult.getTransactionMessage() != null) {
                                        dismissProgressIndicator();
                                        view.showResultScreen(addBeneficiaryResult.getTransactionStatus(), addedBeneficiary.getBeneficiaryId(), addBeneficiaryResult.getTransactionMessage());
                                    }
                                } else {
                                    addedBeneficiary.setBeneficiaryType(BMBConstants.PASS_AIRTIME);
                                    addedBeneficiary.setBeneficiaryImage(ImageUtils.convertToBase64(benImage));
                                    requestAddBenImage(addedBeneficiary);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                dismissProgressIndicator();
                                view.showResultScreen(addBeneficiaryResult.getTransactionStatus(), addedBeneficiary.getBeneficiaryId(), addBeneficiaryResult.getTransactionMessage());
                            } catch (Exception e) {
                                BMBLogger.e("Image size is too big" + e.getMessage());
                                dismissProgressIndicator();
                                view.showResultScreenWithMessage(addBeneficiaryResult.getTransactionStatus(), addedBeneficiary.getBeneficiaryId(), addBeneficiaryResult.getTransactionMessage());
                            }
                        } else {
                            view.showResultScreen(addBeneficiaryResult.getTransactionStatus(), addedBeneficiary.getBeneficiaryId(), addBeneficiaryResult.getTransactionMessage());
                        }
                    } else {
                        if (addBeneficiaryResult.getTransactionMessage() != null) {
                            dismissProgressIndicator();
                            view.showResultScreen(addBeneficiaryResult.getTransactionStatus(), addedBeneficiary.getBeneficiaryId(), addBeneficiaryResult.getTransactionMessage());
                        }
                    }
                }
            } else if (response instanceof AddBeneficiaryObject) {
                AddBeneficiaryObject addBeneficiaryObject = (AddBeneficiaryObject) response;
                if (BMBConstants.SUCCESS.equalsIgnoreCase(addBeneficiaryObject.getStatus())) {
                    addBeneficiaryObject.setImageData(ImageUtils.convertToByteArray(benImage));
                    addBeneficiaryDAO.saveBeneficiary(addBeneficiaryObject);
                    dismissProgressIndicator();
                    view.showResultScreen(addBeneficiaryObject.getStatus(), addBeneficiaryObject.getBeneficiaryId(), addBeneficiaryObject.getMsg());
                }
            }
        }
    }

    private void requestAddBenImage(AddedBeneficiary addedBeneficiary) {
        new BeneficiariesInteractor().uploadBeneficiaryImage(addedBeneficiary, MIME_TYPE_JPG, SERVICE_ACTIONTYPE_ADD, uploadBeneficiaryImageResponseListener);
    }

    void requestAddAirtimeBenConfirmation(String beneficiaryReference) {
        String hasImage = (this.hasImage() ? "Y" : "N");
        prepaidInteractor.addAirtimeBeneficiaryResult(beneficiaryReference, hasImage, addBeneficiaryConfirmResponseListener);
    }


    void requestAirtimeBeneficiaryDetails(String beneficiaryId) {
        prepaidInteractor.fetchAirtimeBeneficiaryDetails(beneficiaryId, requestAirtimeBeneficiaryDetailsExtendedResponseListener);
    }

    public boolean hasImage() {
        return benImage != null;
    }
}
