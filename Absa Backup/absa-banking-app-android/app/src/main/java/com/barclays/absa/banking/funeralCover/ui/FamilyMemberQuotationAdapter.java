/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.funeralCover.ui;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails;
import com.barclays.absa.banking.databinding.FamilyMemberQuotationItemBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.utils.TextFormatUtils;

import java.util.List;

import styleguide.content.SecondaryContentAndLabelView;

public class FamilyMemberQuotationAdapter extends RecyclerView.Adapter<FamilyMemberQuotationAdapter.FamilyMemberQuotationViewHolder> {
    private List<FamilyMemberCoverDetails> rolePlayerDetailsArrayList;
    private Context context;

    FamilyMemberQuotationAdapter(List<FamilyMemberCoverDetails> rolePlayerDetailsArrayList) {
        this.rolePlayerDetailsArrayList = rolePlayerDetailsArrayList;
    }

    @NonNull
    @Override
    public FamilyMemberQuotationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        FamilyMemberQuotationItemBinding quotationItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.family_member_quotation_item, parent, false);
        return new FamilyMemberQuotationViewHolder(quotationItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyMemberQuotationViewHolder holder, int position) {
        FamilyMemberCoverDetails rolePlayerDetails = rolePlayerDetailsArrayList.get(position);
        String premiumAmount = String.format(BMBApplication.getApplicationLocale(), "R %s p/m", rolePlayerDetails.getPremiumAmount());
        String memberNameAndInitials = String.format(BMBApplication.getApplicationLocale(),
                "%s %s (%s)", rolePlayerDetails.getInitials(), rolePlayerDetails.getSurname(), rolePlayerDetails.getRelationship());
        String memberQuote = String.format(BMBApplication.getApplicationLocale(), "R %s %s %s "
                , TextFormatUtils.formatBasicAmount(rolePlayerDetails.getCoverAmount()), context.getString(R.string.at), premiumAmount);
        holder.familyMemberView.setContentText(memberNameAndInitials);
        holder.familyMemberView.setLabelText(memberQuote);
    }

    @Override
    public int getItemCount() {
        return rolePlayerDetailsArrayList != null ? rolePlayerDetailsArrayList.size() : 0;
    }

    static class FamilyMemberQuotationViewHolder extends RecyclerView.ViewHolder {
        SecondaryContentAndLabelView familyMemberView;

        FamilyMemberQuotationViewHolder(FamilyMemberQuotationItemBinding quotationItemBinding) {
            super(quotationItemBinding.getRoot());
            familyMemberView = quotationItemBinding.familyMemberQuotationItemView;
        }
    }
}
