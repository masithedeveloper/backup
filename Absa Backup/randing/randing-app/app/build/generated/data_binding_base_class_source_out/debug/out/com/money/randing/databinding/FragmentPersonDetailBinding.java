// Generated by view binder compiler. Do not edit!
package com.money.randing.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.card.MaterialCardView;
import com.money.randing.R;
import com.money.randing.ui.common.IconLabelButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentPersonDetailBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final Button btnNewMovement;

  @NonNull
  public final ConstraintLayout contentContainer;

  @NonNull
  public final CoordinatorLayout coordinatorLayout;

  @NonNull
  public final IconLabelButton iconButtonDelete;

  @NonNull
  public final IconLabelButton iconButtonEdit;

  @NonNull
  public final IconLabelButton iconButtonSettle;

  @NonNull
  public final IconLabelButton iconButtonShare;

  @NonNull
  public final ImageView image;

  @NonNull
  public final MaterialCardView imageCard;

  @NonNull
  public final RecyclerView rvMovements;

  @NonNull
  public final NestedScrollView scrollView;

  @NonNull
  public final TextView tvEmpty;

  @NonNull
  public final TextView tvHistory;

  @NonNull
  public final TextView tvName;

  @NonNull
  public final TextView tvTotal;

  @NonNull
  public final TextView tvTotalLabel;

  private FragmentPersonDetailBinding(@NonNull CoordinatorLayout rootView,
      @NonNull Button btnNewMovement, @NonNull ConstraintLayout contentContainer,
      @NonNull CoordinatorLayout coordinatorLayout, @NonNull IconLabelButton iconButtonDelete,
      @NonNull IconLabelButton iconButtonEdit, @NonNull IconLabelButton iconButtonSettle,
      @NonNull IconLabelButton iconButtonShare, @NonNull ImageView image,
      @NonNull MaterialCardView imageCard, @NonNull RecyclerView rvMovements,
      @NonNull NestedScrollView scrollView, @NonNull TextView tvEmpty, @NonNull TextView tvHistory,
      @NonNull TextView tvName, @NonNull TextView tvTotal, @NonNull TextView tvTotalLabel) {
    this.rootView = rootView;
    this.btnNewMovement = btnNewMovement;
    this.contentContainer = contentContainer;
    this.coordinatorLayout = coordinatorLayout;
    this.iconButtonDelete = iconButtonDelete;
    this.iconButtonEdit = iconButtonEdit;
    this.iconButtonSettle = iconButtonSettle;
    this.iconButtonShare = iconButtonShare;
    this.image = image;
    this.imageCard = imageCard;
    this.rvMovements = rvMovements;
    this.scrollView = scrollView;
    this.tvEmpty = tvEmpty;
    this.tvHistory = tvHistory;
    this.tvName = tvName;
    this.tvTotal = tvTotal;
    this.tvTotalLabel = tvTotalLabel;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentPersonDetailBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentPersonDetailBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_person_detail, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentPersonDetailBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_new_movement;
      Button btnNewMovement = rootView.findViewById(id);
      if (btnNewMovement == null) {
        break missingId;
      }

      id = R.id.content_container;
      ConstraintLayout contentContainer = rootView.findViewById(id);
      if (contentContainer == null) {
        break missingId;
      }

      CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;

      id = R.id.icon_button_delete;
      IconLabelButton iconButtonDelete = rootView.findViewById(id);
      if (iconButtonDelete == null) {
        break missingId;
      }

      id = R.id.icon_button_edit;
      IconLabelButton iconButtonEdit = rootView.findViewById(id);
      if (iconButtonEdit == null) {
        break missingId;
      }

      id = R.id.icon_button_settle;
      IconLabelButton iconButtonSettle = rootView.findViewById(id);
      if (iconButtonSettle == null) {
        break missingId;
      }

      id = R.id.icon_button_share;
      IconLabelButton iconButtonShare = rootView.findViewById(id);
      if (iconButtonShare == null) {
        break missingId;
      }

      id = R.id.image;
      ImageView image = rootView.findViewById(id);
      if (image == null) {
        break missingId;
      }

      id = R.id.image_card;
      MaterialCardView imageCard = rootView.findViewById(id);
      if (imageCard == null) {
        break missingId;
      }

      id = R.id.rv_movements;
      RecyclerView rvMovements = rootView.findViewById(id);
      if (rvMovements == null) {
        break missingId;
      }

      id = R.id.scroll_view;
      NestedScrollView scrollView = rootView.findViewById(id);
      if (scrollView == null) {
        break missingId;
      }

      id = R.id.tv_empty;
      TextView tvEmpty = rootView.findViewById(id);
      if (tvEmpty == null) {
        break missingId;
      }

      id = R.id.tv_history;
      TextView tvHistory = rootView.findViewById(id);
      if (tvHistory == null) {
        break missingId;
      }

      id = R.id.tv_name;
      TextView tvName = rootView.findViewById(id);
      if (tvName == null) {
        break missingId;
      }

      id = R.id.tv_total;
      TextView tvTotal = rootView.findViewById(id);
      if (tvTotal == null) {
        break missingId;
      }

      id = R.id.tv_total_label;
      TextView tvTotalLabel = rootView.findViewById(id);
      if (tvTotalLabel == null) {
        break missingId;
      }

      return new FragmentPersonDetailBinding((CoordinatorLayout) rootView, btnNewMovement,
          contentContainer, coordinatorLayout, iconButtonDelete, iconButtonEdit, iconButtonSettle,
          iconButtonShare, image, imageCard, rvMovements, scrollView, tvEmpty, tvHistory, tvName,
          tvTotal, tvTotalLabel);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
