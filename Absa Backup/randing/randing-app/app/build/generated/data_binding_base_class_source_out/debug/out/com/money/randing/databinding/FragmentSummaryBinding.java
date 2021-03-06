// Generated by view binder compiler. Do not edit!
package com.money.randing.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.money.randing.R;
import com.money.randing.ui.common.TypingTextView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentSummaryBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final RecyclerView rvPeople;

  @NonNull
  public final TypingTextView tvBalanceAmount;

  @NonNull
  public final TextView tvBalanceLabel;

  @NonNull
  public final TextView tvEmpty;

  @NonNull
  public final TextView tvNegativeAmount;

  @NonNull
  public final TextView tvPositiveAmount;

  private FragmentSummaryBinding(@NonNull ConstraintLayout rootView, @NonNull RecyclerView rvPeople,
      @NonNull TypingTextView tvBalanceAmount, @NonNull TextView tvBalanceLabel,
      @NonNull TextView tvEmpty, @NonNull TextView tvNegativeAmount,
      @NonNull TextView tvPositiveAmount) {
    this.rootView = rootView;
    this.rvPeople = rvPeople;
    this.tvBalanceAmount = tvBalanceAmount;
    this.tvBalanceLabel = tvBalanceLabel;
    this.tvEmpty = tvEmpty;
    this.tvNegativeAmount = tvNegativeAmount;
    this.tvPositiveAmount = tvPositiveAmount;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentSummaryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentSummaryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_summary, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentSummaryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.rv_people;
      RecyclerView rvPeople = rootView.findViewById(id);
      if (rvPeople == null) {
        break missingId;
      }

      id = R.id.tv_balance_amount;
      TypingTextView tvBalanceAmount = rootView.findViewById(id);
      if (tvBalanceAmount == null) {
        break missingId;
      }

      id = R.id.tv_balance_label;
      TextView tvBalanceLabel = rootView.findViewById(id);
      if (tvBalanceLabel == null) {
        break missingId;
      }

      id = R.id.tv_empty;
      TextView tvEmpty = rootView.findViewById(id);
      if (tvEmpty == null) {
        break missingId;
      }

      id = R.id.tv_negative_amount;
      TextView tvNegativeAmount = rootView.findViewById(id);
      if (tvNegativeAmount == null) {
        break missingId;
      }

      id = R.id.tv_positive_amount;
      TextView tvPositiveAmount = rootView.findViewById(id);
      if (tvPositiveAmount == null) {
        break missingId;
      }

      return new FragmentSummaryBinding((ConstraintLayout) rootView, rvPeople, tvBalanceAmount,
          tvBalanceLabel, tvEmpty, tvNegativeAmount, tvPositiveAmount);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
