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
package styleguide.cards;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import za.co.absa.presentation.uilib.R;

public class WebNavigationView extends ConstraintLayout {

    private ImageView previousButtonImageView;
    private ImageView nextButtonImageView;

    public WebNavigationView(Context context) {
        super(context);
        init(context);
    }

    public WebNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.web_navigation_view, this);
        previousButtonImageView = findViewById(R.id.backImageView);
        nextButtonImageView = findViewById(R.id.forwardImageView);
    }

    public void deactivateForwardNavigationButton(boolean canGoBack) {
        if (canGoBack) {
            nextButtonImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_arrow_right_dark));
        } else {
            nextButtonImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_arrow_right_grey));
        }
    }

    public void deactivateBackwardNavigationButton(boolean canGoBack) {
        if (canGoBack) {
            previousButtonImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_arrow_left_dark));
        } else {
            previousButtonImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_arrow_left_grey));
        }
    }

    public void setOnPreviousClick(OnClickListener onPreviousClick) {
        previousButtonImageView.setOnClickListener(onPreviousClick);
    }

    public void setOnNextClick(OnClickListener onNextClick) {
        nextButtonImageView.setOnClickListener(onNextClick);
    }
}
