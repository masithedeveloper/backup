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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryItemHelper;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;

public class BeneficiaryCustomSectionListAdapter extends BaseAdapter implements BMBConstants {

    private final Context context;
    private SectionListItem[] items;
    private List<BeneficiaryObject> beneficiaryListData;

    public BeneficiaryCustomSectionListAdapter(Context context, SectionListItem[] items, List<BeneficiaryObject> beneficiaryListData) {
        super();
        this.items = items;
        this.context = context;
        this.beneficiaryListData = beneficiaryListData;
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
            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.beneficiaryView = convertView.findViewById(R.id.beneficiaryView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BeneficiaryObject beneficiary = beneficiaryListData.get(position);

        Amount lastTransactionAmount = beneficiary.getLastTransactionAmount();
        String lastPaymentDetail = null;
        if (lastTransactionAmount != null) {
            lastPaymentDetail = context.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), context.getString(R.string.paid), beneficiary.getLastTransactionDate());
        }
        BeneficiaryListItem displayItem = new BeneficiaryListItem(beneficiary.getBeneficiaryName(), BeneficiaryItemHelper.getAccountNumberDetails(beneficiary), lastPaymentDetail);
        viewHolder.beneficiaryView.setBeneficiary(displayItem);

        return convertView;
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

    static class ViewHolder {
        BeneficiaryView beneficiaryView;
    }
}
