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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryItemHelper;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;

import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;

public class RecentTransactionAdapter extends BaseAdapter {

    private final Context mContext;

    private List<BeneficiaryObject> mLastTransactionDataObject;

    public RecentTransactionAdapter(Context mContext, List<BeneficiaryObject> mLastTransactionDataObject) {
        this.mContext = mContext;
        this.mLastTransactionDataObject = mLastTransactionDataObject;
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

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.sectioned_beneficiary_list_item, viewGroup, false);

            viewHolder = new ViewHolderItem();
            viewHolder.beneficiaryView = convertView.findViewById(R.id.beneficiaryView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        BeneficiaryObject beneficiary = mLastTransactionDataObject.get(position);
        String placeholderText = mContext.getString(R.string.paid);
        String lastPaymentDetail = null;
        Amount lastTransactionAmount = beneficiary.getLastTransactionAmount();
        if (lastTransactionAmount != null) {
            lastPaymentDetail = mContext.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), placeholderText, beneficiary.getLastTransactionDate());
        }

        BeneficiaryListItem displayItem = new BeneficiaryListItem(beneficiary.getBeneficiaryName(), BeneficiaryItemHelper.getAccountNumberDetails(beneficiary), lastPaymentDetail);
        viewHolder.beneficiaryView.setBeneficiary(displayItem);
        return viewHolder.beneficiaryView;
    }

    class ViewHolderItem {
        BeneficiaryView beneficiaryView;
    }
}