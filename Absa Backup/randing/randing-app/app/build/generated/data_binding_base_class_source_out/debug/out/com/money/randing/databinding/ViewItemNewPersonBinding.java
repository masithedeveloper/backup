// Generated by view binder compiler. Do not edit!
package com.money.randing.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.card.MaterialCardView;
import com.money.randing.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ViewItemNewPersonBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout container;

  @NonNull
  public final ImageView image;

  @NonNull
  public final MaterialCardView imageCard;

  @NonNull
  public final TextView tvName;

  private ViewItemNewPersonBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout container, @NonNull ImageView image,
      @NonNull MaterialCardView imageCard, @NonNull TextView tvName) {
    this.rootView = rootView;
    this.container = container;
    this.image = image;
    this.imageCard = imageCard;
    this.tvName = tvName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ViewItemNewPersonBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ViewItemNewPersonBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.view_item_new_person, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ViewItemNewPersonBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout container = (ConstraintLayout) rootView;

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

      id = R.id.tv_name;
      TextView tvName = rootView.findViewById(id);
      if (tvName == null) {
        break missingId;
      }

      return new ViewItemNewPersonBinding((ConstraintLayout) rootView, container, image, imageCard,
          tvName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
