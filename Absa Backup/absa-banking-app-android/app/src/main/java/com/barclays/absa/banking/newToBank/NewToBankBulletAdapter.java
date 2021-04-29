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

package com.barclays.absa.banking.newToBank;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.BulletListItemBinding;
import com.barclays.absa.banking.newToBank.dto.NewToBankBulletItem;
import com.barclays.absa.utils.CommonUtils;

import java.util.List;

public class NewToBankBulletAdapter extends RecyclerView.Adapter<NewToBankBulletAdapter.ViewHolder> {

    private List<NewToBankBulletItem> bulletItems;
    private BulletListItemBinding binding;
    private int dotMarginTop;
    private int headingMarginTop;
    private int headingMarginBottom;
    private NewToBankView newToBankBusinessAccountView;

    public NewToBankBulletAdapter() {
    }

    public NewToBankBulletAdapter(NewToBankView newToBankBusinessAccountView) {
        this.newToBankBusinessAccountView = newToBankBusinessAccountView;
    }

    public NewToBankBulletAdapter(List<NewToBankBulletItem> bulletItems) {
        this.bulletItems = bulletItems;
    }

    @NonNull
    @Override
    public NewToBankBulletAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bullet_list_item, parent, false);
        dotMarginTop = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.dimen_16dp);
        headingMarginTop = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.large_space);
        headingMarginBottom = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.medium_space);
        return new NewToBankBulletAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final NewToBankBulletAdapter.ViewHolder holder, int position) {
        NewToBankBulletItem bulletItem = bulletItems.get(position);
        setViewMargins(holder.viewItemContainer, 0, dotMarginTop, headingMarginBottom);
        holder.dotView.setImageResource(R.drawable.ic_tick_black_24);

        switch (bulletItem.getType()) {
            case HEADING:
                appendBulletText(holder.descriptionTextView, bulletItem.getText(), true);
                setViewMargins(holder.viewItemContainer, 0, headingMarginTop, headingMarginBottom);
                holder.dotView.setVisibility(View.GONE);
                break;
            case INCLUDE:
                appendBulletText(holder.descriptionTextView, bulletItem.getText(), false);
                holder.dotView.setVisibility(View.INVISIBLE);
                setViewMargins(holder.viewItemContainer, holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.dimen_16dp), 0, headingMarginBottom);
                break;
            case EXTRA_BULLET:
                appendBulletText(holder.descriptionTextView, bulletItem.getText(), false);
                holder.dotView.setVisibility(View.VISIBLE);
                holder.dotView.setImageResource(R.drawable.line);
                setViewMargins(holder.viewItemContainer, holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.large_space), 0, headingMarginBottom);
                break;
            case DOT:
                appendBulletText(holder.descriptionTextView, bulletItem.getText(), false);
                setViewMargins(holder.viewItemContainer, holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.small_space), 0, headingMarginBottom);
                setViewSize(holder.dotView, holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.dimen_6dp));
                holder.dotView.setImageResource(R.drawable.dot_grey);
                break;
            default:
                appendBulletText(holder.descriptionTextView, bulletItem.getText(), false);
                setViewMargins(holder.viewItemContainer, holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.small_space), 0, headingMarginBottom);
                break;
        }
    }

    public void refreshItems(List<NewToBankBulletItem> bulletItems) {
        this.bulletItems = bulletItems;
        notifyDataSetChanged();
    }

    private void appendBulletText(TextView textView, String text, boolean isBold) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        StyleSpan styleSpan = isBold ? new StyleSpan(Typeface.BOLD) : new StyleSpan(Typeface.NORMAL);
        spannableStringBuilder.setSpan(styleSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (text.equalsIgnoreCase(textView.getContext().getString(R.string.new_to_bank_visit_student_benefits))) {
            ClickableSpan navigateToStudentBenefits = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (newToBankBusinessAccountView != null) {
                        newToBankBusinessAccountView.navigateToStudentBenefits();
                    }
                }
            };

            CommonUtils.makeTextClickable(textView.getContext(), spannableStringBuilder.toString(),
                    textView.getContext().getString(R.string.new_to_bank_student_benefits),
                    navigateToStudentBenefits, textView, R.color.dark_grey);
        } else {
            textView.setText(spannableStringBuilder);
        }
    }

    private void setViewMargins(View bullet, int marginLeft, int marginTop, int marginBottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bullet.getLayoutParams();
        params.setMargins(marginLeft, marginTop, 0, marginBottom);
        bullet.requestLayout();
    }

    private void setViewSize(View view, int dimension) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.width = dimension;
        params.height = dimension;
        params.topMargin = view.getContext().getResources().getDimensionPixelOffset(R.dimen.dimen_8dp);
        view.requestLayout();
    }

    @Override
    public int getItemCount() {
        return bulletItems != null ? bulletItems.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout viewItemContainer;
        TextView descriptionTextView;
        ImageView dotView;

        public ViewHolder(View itemView) {
            super(itemView);
            viewItemContainer = binding.viewItemConstraintLayout;
            dotView = binding.dotView;
            descriptionTextView = binding.descriptionTextView;
        }
    }
}