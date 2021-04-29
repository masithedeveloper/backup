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
import com.barclays.absa.banking.databinding.AddFamilyMemberItemBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.utils.TextFormatUtils;

import java.math.BigDecimal;
import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;

public class FuneralCoverAddFamilyMemberAdapter extends RecyclerView.Adapter<FuneralCoverAddFamilyMemberAdapter.AddFamilyMemberViewHolder> {

    private List<FamilyMemberCoverDetails> familyMemberCoverDetailsList;
    private AdditionalFamilyMemberClickListener familyMemberClickListener;

    FuneralCoverAddFamilyMemberAdapter(List<FamilyMemberCoverDetails> familyMemberCoverDetailsList, AdditionalFamilyMemberClickListener familyMemberClickListener) {
        this.familyMemberCoverDetailsList = familyMemberCoverDetailsList;
        this.familyMemberClickListener = familyMemberClickListener;
    }

    @NonNull
    @Override
    public AddFamilyMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddFamilyMemberItemBinding memberItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.add_family_member_item, parent, false);
        return new AddFamilyMemberViewHolder(memberItemBinding, familyMemberClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFamilyMemberViewHolder holder, int position) {
        FamilyMemberCoverDetails rolePlayerDetails = familyMemberCoverDetailsList.get(position);
        Context context = holder.beneficiaryView.getContext();
        String premiumAmount = String.format(BMBApplication.getApplicationLocale(), "R %s p/m", TextFormatUtils.formatBasicAmount(rolePlayerDetails.getPremiumAmount()));
        String memberName = String.format(BMBApplication.getApplicationLocale(), "%s %s", rolePlayerDetails.getInitials(),
                rolePlayerDetails.getSurname());
        String memberQuote = String.format(BMBApplication.getApplicationLocale(), "R %s %s %s ", TextFormatUtils.formatBasicAmount(rolePlayerDetails.getCoverAmount()), context.getString(R.string.at), premiumAmount);
        String memberRelationship = String.format(BMBApplication.getApplicationLocale(), " (%s)", rolePlayerDetails.getRelationship());
        holder.setAdditionalFamilyMember(position, rolePlayerDetails);
        holder.beneficiaryView.setFuneralCoverItem(new BeneficiaryListItem(memberName, memberQuote, memberRelationship));
    }

    @Override
    public int getItemCount() {
        return familyMemberCoverDetailsList != null ? familyMemberCoverDetailsList.size() : 0;
    }

    public void updateFamilyMember(FamilyMemberCoverDetails familyMemberCoverDetails, int position) {
        familyMemberCoverDetailsList.get(position).setInitials(familyMemberCoverDetails.getInitials());
        familyMemberCoverDetailsList.get(position).setSurname(familyMemberCoverDetails.getSurname());
        familyMemberCoverDetailsList.get(position).setBenefitCode(familyMemberCoverDetails.getBenefitCode());
        familyMemberCoverDetailsList.get(position).setCategory(familyMemberCoverDetails.getCategory());
        familyMemberCoverDetailsList.get(position).setCoverAmount(familyMemberCoverDetails.getCoverAmount());
        familyMemberCoverDetailsList.get(position).setPremiumAmount(familyMemberCoverDetails.getPremiumAmount());
        familyMemberCoverDetailsList.get(position).setRelationship(familyMemberCoverDetails.getRelationship());
        familyMemberCoverDetailsList.get(position).setRelationshipCode(familyMemberCoverDetails.getRelationshipCode());
        familyMemberCoverDetailsList.get(position).setDateOfBirth(familyMemberCoverDetails.getDateOfBirth());
        familyMemberCoverDetailsList.get(position).setGender(familyMemberCoverDetails.getGender());
        notifyItemChanged(position);
    }

    public BigDecimal getTotalMonthlyPremiumAmount() {
        BigDecimal memberPremium = new BigDecimal(0.00);
        for (FamilyMemberCoverDetails familyMemberCoverDetails : familyMemberCoverDetailsList) {
            memberPremium = memberPremium.add(new BigDecimal(familyMemberCoverDetails.getPremiumAmount()));
        }
        return memberPremium;
    }

    public BigDecimal getTotalCoverAmount() {
        BigDecimal memberCoverAmount = new BigDecimal(0.00);
        for (FamilyMemberCoverDetails familyMemberCoverDetails : familyMemberCoverDetailsList) {
            memberCoverAmount = memberCoverAmount.add(new BigDecimal(familyMemberCoverDetails.getCoverAmount()));
        }
        return memberCoverAmount;
    }

    public void removeFamilyMember(int position) {
        familyMemberCoverDetailsList.remove(position);
        notifyDataSetChanged();
    }

    public interface AdditionalFamilyMemberClickListener {
        void onAdditionalFamilyMemberClicked(int position, FamilyMemberCoverDetails rolePlayerDetails);
    }

    static class AddFamilyMemberViewHolder extends RecyclerView.ViewHolder {
        BeneficiaryView beneficiaryView;
        FamilyMemberCoverDetails rolePlayerDetails;
        int position;

        AddFamilyMemberViewHolder(AddFamilyMemberItemBinding itemBinding, AdditionalFamilyMemberClickListener familyMemberClickListener) {
            super(itemBinding.getRoot());
            beneficiaryView = itemBinding.addFamilyMemberBeneficiaryView;
            beneficiaryView.setOnClickListener(v -> familyMemberClickListener.onAdditionalFamilyMemberClicked(position, rolePlayerDetails));
        }

        void setAdditionalFamilyMember(int position, FamilyMemberCoverDetails rolePlayerDetails) {
            this.rolePlayerDetails = rolePlayerDetails;
            this.position = position;
        }

    }
}
