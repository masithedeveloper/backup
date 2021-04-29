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

package com.barclays.absa.banking.funeralCover.ui;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.policy.PolicyComponent;
import com.barclays.absa.banking.databinding.InsurancePolicyComponentBinding;
import com.barclays.absa.utils.TextFormatUtils;

import java.util.List;

import styleguide.content.SecondaryContentAndLabelView;

public class InsurancePolicyComponentAdapter extends RecyclerView.Adapter<InsurancePolicyComponentAdapter.InsurancePolicyComponentViewHolder> {
    private List<PolicyComponent> policyComponents;
    private Context context;

    InsurancePolicyComponentAdapter(List<PolicyComponent> policyComponents) {
        this.policyComponents = policyComponents;
    }

    @NonNull
    @Override
    public InsurancePolicyComponentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        InsurancePolicyComponentBinding insurancePolicyComponentBinding = DataBindingUtil.inflate(LayoutInflater.from(context)
                , R.layout.insurance_policy_component, parent, false);
        return new InsurancePolicyComponentViewHolder(insurancePolicyComponentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InsurancePolicyComponentViewHolder holder, int position) {
        PolicyComponent policyComponent = policyComponents.get(position);
        if (policyComponent != null) {
            String componentName = TextUtils.isEmpty(policyComponent.getComponentName()) ? policyComponent.getDescription() : policyComponent.getComponentName();
            holder.policyComponentDetail.setContentText(componentName);
            if (policyComponent.getCoverAmount() != null) {
                if (context.getString(R.string.main_member_accidental_cover).equalsIgnoreCase(componentName)) {
                    holder.policyComponentDetail.setLabelText(String.format("%s", TextFormatUtils.formatBasicAmount(policyComponent.getCoverAmount())));
                } else {
                    String coverAmount;
                    if (policyComponent.getPremiumAmount() != null) {
                        coverAmount = String.format("%s %s %s", TextFormatUtils.formatBasicAmount(policyComponent.getCoverAmount()), context.getString(R.string.cover_at),
                                TextFormatUtils.formatBasicAmount(policyComponent.getPremiumAmount()));
                    } else {
                        coverAmount = String.format("%s", TextFormatUtils.formatBasicAmount(policyComponent.getCoverAmount()));
                    }
                    holder.policyComponentDetail.setLabelText(coverAmount);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return policyComponents == null ? 0 : policyComponents.size();
    }

    static class InsurancePolicyComponentViewHolder extends RecyclerView.ViewHolder {
        SecondaryContentAndLabelView policyComponentDetail;

        InsurancePolicyComponentViewHolder(InsurancePolicyComponentBinding insurancePolicyComponentBinding) {
            super(insurancePolicyComponentBinding.getRoot());
            policyComponentDetail = insurancePolicyComponentBinding.policyComponentDetail;
        }
    }
}
