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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountType;

import java.util.List;

public class AccountTypeAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<AccountType> itemsArrayList;

    public AccountTypeAdapter(Context context, List<AccountType> itemsArrayList) {
        super(context, R.layout.custom_account_type_row);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_account_type_row, parent, false);
        }
        TextView labelView = convertView.findViewById(R.id.account_type);
        labelView.setText(itemsArrayList.get(position).getName());
        return convertView;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
    }
}