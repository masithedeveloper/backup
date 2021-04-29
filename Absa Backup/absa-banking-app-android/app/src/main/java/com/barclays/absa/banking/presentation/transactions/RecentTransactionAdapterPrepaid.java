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
package com.barclays.absa.banking.presentation.transactions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.cashSend.ui.CashSendActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;

import static com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0401_BENEFICIARY_IMAGE_DOWNLOAD;

public class RecentTransactionAdapterPrepaid extends BaseAdapter {

    private final Context mContext;
    private final Object lockObject = new Object();
    private List<BeneficiaryObject> mLastTransactionDataObject;
    private final AddBeneficiaryDAO addBeneficiaryDAO;
    private HashMap<String, Bitmap> imageNameViewMap;

    public RecentTransactionAdapterPrepaid(Context mContext, List<BeneficiaryObject> mLastTransactionDataObject) {
        this.mContext = mContext;
        this.mLastTransactionDataObject = mLastTransactionDataObject;
        this.addBeneficiaryDAO = new AddBeneficiaryDAO(mContext);
    }

    @Override
    public int getCount() {
        return mLastTransactionDataObject.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolderItem viewHolder;
        Bitmap iconBitmap;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.sectioned_beneficiary_list_item, viewGroup, false);

            viewHolder = new ViewHolderItem();
            viewHolder.beneficiaryView = convertView.findViewById(R.id.beneficiaryView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        final BeneficiaryObject beneficiary = mLastTransactionDataObject.get(position);
        String name = beneficiary.getBeneficiaryName().trim();
        String number = beneficiary.getBeneficiaryAccountNumber();
        String placeholderText = mContext.getString(R.string.purchased);
        if (mContext instanceof CashSendActivity) {
            placeholderText = "CashSend";
        } else if (mContext instanceof BuyPrepaidActivity) {
            String network = beneficiary.getMyReference();
            number = network + " - " + number;
        } else {
            number = beneficiary.getMyReference();
        }
        Amount lastTransactionAmount = beneficiary.getLastTransactionAmount();
        String lastPaymentDetail = null;
        if (lastTransactionAmount != null) {
            lastPaymentDetail = mContext.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), placeholderText, beneficiary.getLastTransactionDate());
        }
        viewHolder.beneficiaryView.setBeneficiary(new BeneficiaryListItem(name, number, lastPaymentDetail));
        String recentBeneficiaryName = viewHolder.beneficiaryView.getNameTextView().getText().toString();
        String accountNumber = AccessibilityUtils.getTalkBackAccountNumberFromString(viewHolder.beneficiaryView.getAccountNumberTextView().getText().toString());
        viewHolder.beneficiaryView.setContentDescription(BMBApplication.getInstance().getString(R.string.talkback_cashsend_recent_sends, recentBeneficiaryName, accountNumber));
        viewHolder.beneficiaryView.getNameTextView().setContentDescription(BMBApplication.getInstance().getString(R.string.talkback_cashsend_recent_sends, recentBeneficiaryName, accountNumber));
        viewHolder.beneficiaryView.getAccountNumberTextView().setContentDescription(BMBApplication.getInstance().getString(R.string.talkback_cashsend_recent_sends, recentBeneficiaryName, accountNumber));

        if (BMBApplication.FEATURE_BENEFICIARY_IMAGE) {
            final ImageView roundedImageView = viewHolder.beneficiaryView.getRoundedImageView();
            if (null != roundedImageView) {
                if (beneficiary.isImageAvailable()) {
                    if (null == imageNameViewMap) {
                        imageNameViewMap = new HashMap<>();
                    }

                    // check if image already loaded
                    if (null != (iconBitmap = imageNameViewMap.get(beneficiary.getImageName()))) {
                        BMBLogger.d("BEN-IMAGE IMAGE FOUND IN HOLDER! "
                                + beneficiary.getBeneficiaryName() + ", " + position);
                        boolean hasImage = ImageUtils.setImageFromBitmap(roundedImageView, iconBitmap);
                        viewHolder.beneficiaryView.setHasImage(hasImage);
                    }
                    // first time view initialization, then download image
                    else {
                        // retrieve image from cache and set
                        AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO
                                .getBeneficiary(beneficiary.getImageName());

                        if (null != addBeneficiaryObject) {
                            byte[] oldImage = addBeneficiaryObject.getImageData();
                            if (null != oldImage) {
                                BMBLogger.d("BEN-IMAGE IMAGE AVAILABLE FOR --> "
                                        + beneficiary.getBeneficiaryName());
                                Bitmap imgBitmap = BitmapFactory.decodeByteArray(oldImage, 0,
                                        oldImage.length);
                                roundedImageView.setImageBitmap(imgBitmap);
                                ImageUtils.setImageFromBitmap(roundedImageView, imgBitmap);
                                roundedImageView.setTag(R.id.TAG_BENEFICIARY_IMAGE, imgBitmap);
                                viewHolder.beneficiaryView.setHasImage(true);
                            }
                        }

                        handleSetBeneficiaryIcon(roundedImageView, beneficiary, addBeneficiaryObject);
                    }
                } else if (!beneficiary.isImageAvailable()) {

                    // Remove Image from Database, if exist
                    addBeneficiaryDAO.deleteBeneficiaryFromId(beneficiary.getBeneficiaryID(), beneficiary.getBeneficiaryType());

                }
            }
        }
        return convertView;
    }

    private void handleSetBeneficiaryIcon(ImageView iconView, BeneficiaryObject beneficiary,
                                          AddBeneficiaryObject addBeneficiaryObject) {
        if (null != iconView) {
            // if request is not already made
            if (!imageNameViewMap.containsKey(beneficiary.getImageName())) {
                String timestamp;

                // send current timestamp if image not found in cache
                if (null == addBeneficiaryObject) {
                    // example date format 27/FEB/13 16:28:55.145000000
                    SimpleDateFormat df = new SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, BMBApplication.getApplicationLocale());
                    timestamp = df.format(new Date());
                }
                // send available timestamp
                else {
                    timestamp = addBeneficiaryObject.getTimestamp();
                }

                imageNameViewMap.put(beneficiary.getImageName(),
                        BitmapFactory.decodeByteArray(new byte[1], 0, 1));
                BMBLogger.d("BEN-IMAGE IMAGE REQUEST MADE FOR " + beneficiary.getBeneficiaryName());
                if (mContext instanceof CashSendActivity) {
                    new BeneficiariesInteractor().downloadBeneficiaryImage(beneficiary.getBeneficiaryID(), BMBConstants.PASS_CASHSEND, timestamp, beneficiaryImageResponseListener);
                }
            }
        }
    }

    private ExtendedResponseListener<AddBeneficiaryObject> beneficiaryImageResponseListener = new ExtendedResponseListener<AddBeneficiaryObject>() {

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(AddBeneficiaryObject successResponse) {
            byte[] imageData;
            Bitmap imgBitmap;

            // download image response
            if (OP0401_BENEFICIARY_IMAGE_DOWNLOAD.equalsIgnoreCase(successResponse.getOpCode())) {
                synchronized (lockObject) {
                    imageData = successResponse.getImageData();

                    if (null != imageData) {
                        imgBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        imageNameViewMap.put(successResponse.getImageName(), imgBitmap);
                        BMBLogger.d("BEN-IMAGE IMAGE DOWNLOADED FOR : " + successResponse);
                        // Save image to cache
                        addBeneficiaryDAO.saveBeneficiary(successResponse);
                        notifyDataSetChanged();
                    }
                }
            }
        }
    };

    class ViewHolderItem {
        BeneficiaryView beneficiaryView;
    }
}