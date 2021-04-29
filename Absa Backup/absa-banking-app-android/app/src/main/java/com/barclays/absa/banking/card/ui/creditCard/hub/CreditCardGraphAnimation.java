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
package com.barclays.absa.banking.card.ui.creditCard.hub;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CreditCardGraphAnimation extends Animation {

    private CreditCardGraph graph;

    private float oldAngle;
    private float newAngle;

    CreditCardGraphAnimation(CreditCardGraph graph, int newAngle) {
        this.oldAngle = graph.getAngleIndicator();
        this.newAngle = newAngle;
        this.graph = graph;
    }

    CreditCardGraphAnimation(CreditCardGraph graph, int oldAngle, int newAngle) {
        this.oldAngle = oldAngle;
        this.newAngle = newAngle;
        this.graph = graph;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);
        graph.setAngleIndicator(angle);
        graph.requestLayout();
    }
}
