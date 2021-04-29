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
package com.barclays.absa.banking.presentation.adapters;

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
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;

public class BeneficiaryCustomSectionListAdapterPrepaid extends BaseAdapter implements BMBConstants {

    private final AddBeneficiaryDAO addBeneficiaryDAO;
    private final Context context;
    private HashMap<String, Bitmap> imageNameViewMap;
    private final BeneficiariesInteractor interactor = new BeneficiariesInteractor();
    private final Object locker = new Object();

    private final ExtendedResponseListener<AddBeneficiaryObject> requestImageExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryObject>() {

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(final AddBeneficiaryObject successResponse) {
            byte[] imageData;
            Bitmap imgBitmap;

            synchronized (locker) {
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

        @Override
        public void onFailure(ResponseObject failureResponse) {
            BaseView baseView = getBaseView();
            if (baseView != null)
                baseView.dismissProgressDialog();
        }
    };

    private SectionListItem[] items;
    private List<BeneficiaryObject> beneficiaryListData;

    public BeneficiaryCustomSectionListAdapterPrepaid(Context context, SectionListItem[] items, List<BeneficiaryObject> beneficiaryListData) {
        super();
        this.items = items;
        this.context = context;
        this.beneficiaryListData = beneficiaryListData;
        this.addBeneficiaryDAO = new AddBeneficiaryDAO(context);
        requestImageExtendedResponseListener.setView((BaseView) context);
    }

    public SectionListItem[] getItems() {
        return items;
    }

    public void setItems(SectionListItem[] items) {
        this.items = items;
    }

    public List<BeneficiaryObject> getBeneficiaryListData() {
        return beneficiaryListData;
    }

    public void setBeneficiaryListData(List<BeneficiaryObject> beneficiaryListData) {
        this.beneficiaryListData = beneficiaryListData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.sectioned_beneficiary_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.beneficiaryView = convertView.findViewById(R.id.beneficiaryView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final BeneficiaryObject beneficiary = beneficiaryListData.get(position);
        String name = beneficiary.getBeneficiaryName();
        Amount lastTransactionAmount = beneficiary.getLastTransactionAmount();
        String lastPaymentDetail = null;
        if (lastTransactionAmount != null) {
            lastPaymentDetail = context.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), context.getString(R.string.purchased), beneficiary.getLastTransactionDate());
        }

        String number = fomatCellPhoneNumber(beneficiary.getBeneficiaryAccountNumber());
        if (context instanceof BuyPrepaidActivity) {
            String network = beneficiary.getMyReference();
            number = network + " - " + number;
        } else if (context instanceof CashSendActivity) {
            if (lastTransactionAmount != null) {
                lastPaymentDetail = context.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), "CashSend", beneficiary.getLastTransactionDate());
            }
        }

        BeneficiaryListItem beneficiaryListItem = new BeneficiaryListItem(name, number, lastPaymentDetail);
        viewHolder.beneficiaryView.setBeneficiary(beneficiaryListItem);

        if (BMBApplication.FEATURE_BENEFICIARY_IMAGE) {
            final ImageView roundedImageView = viewHolder.beneficiaryView.getRoundedImageView();
            if (null != roundedImageView) {
                if (beneficiary.isImageAvailable()) {
                    if (null == imageNameViewMap) {
                        imageNameViewMap = new HashMap<>();
                    }
                    Bitmap iconBitmap;
                    // check if image already loaded
                    if (null != (iconBitmap = imageNameViewMap.get(beneficiary.getImageName()))) {
                        BMBLogger.d("BEN-IMAGE IMAGE FOUND IN HOLDER! " + beneficiary.getBeneficiaryName() + ", " + position);
                        boolean hasImage = ImageUtils.setImageFromBitmap(roundedImageView, iconBitmap);
                        viewHolder.beneficiaryView.setHasImage(hasImage);
                    }
                    // first time view initialization, then download image
                    else {
                        // retrieve image from cache and set
                        AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary(beneficiary.getImageName());
                        if (null != addBeneficiaryObject) {
                            byte[] imageData = addBeneficiaryObject.getImageData();
                            if (null != imageData) {
                                BMBLogger.d("BEN-IMAGE IMAGE AVAILABLE FOR --> " + beneficiary.getBeneficiaryName());
                                Bitmap imgBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                                roundedImageView.setImageBitmap(imgBitmap);
                                viewHolder.beneficiaryView.setImage(imgBitmap);

                                ImageUtils.setImageFromBitmap(roundedImageView, imgBitmap);
                                roundedImageView.setTag(R.id.TAG_BENEFICIARY_IMAGE, imgBitmap);
                            }
                        }
                        // if beneficiary does not have an image
                        handleSetBeneficiaryIcon(roundedImageView, beneficiary, addBeneficiaryObject);
                    }
                } else if (!beneficiary.isImageAvailable()) {
                    addBeneficiaryDAO.deleteBeneficiaryFromId(beneficiary.getBeneficiaryID(), beneficiary.getBeneficiaryType());
                    roundedImageView.setImageResource(R.drawable.ic_private);
                }
            }
        }
        return convertView;
    }

    private String fomatCellPhoneNumber(String cellNo) {
        if (cellNo != null && !String.valueOf(cellNo.charAt(0)).equalsIgnoreCase("0"))
            return "0".concat(cellNo);
        return cellNo;
    }

    @Override
    public int getCount() {
        return this.items.length;
    }

    @Override
    public SectionListItem getItem(int position) {
        return this.items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
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
                    SimpleDateFormat df = new SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, Locale.ENGLISH);
                    timestamp = df.format(new Date());
                }
                // send available timestamp
                else {
                    timestamp = addBeneficiaryObject.getTimestamp();
                }

                imageNameViewMap.put(beneficiary.getImageName(),
                        BitmapFactory.decodeByteArray(new byte[1], 0, 1));
                BMBLogger.d("BEN-IMAGE IMAGE REQUEST MADE FOR " + beneficiary.getBeneficiaryName());
                if (context instanceof BuyPrepaidActivity) {
                    interactor.downloadBeneficiaryImage(beneficiary.getBeneficiaryID(), PASS_PREPAID, timestamp, requestImageExtendedResponseListener);
                } else if (context instanceof CashSendActivity) {
                    interactor.downloadBeneficiaryImage(beneficiary.getBeneficiaryID(), PASS_CASHSEND, timestamp, requestImageExtendedResponseListener);
                }
            }
        }
    }

    static class ViewHolder {
        public BeneficiaryView beneficiaryView;
    }
}