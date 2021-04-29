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

package styleguide.bars;

import android.content.Context;

import android.util.AttributeSet;


public class LimitsIndicator extends ProgressIndicatorView {

    public LimitsIndicator(Context context) {
        super(context);
    }

    public LimitsIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getMaxLimit() {
        return getSteps();
    }

    public void setCurrentAmount(int currentProgress) {
        setNextStep(currentProgress);
    }

    public int getCurrentAmount() {
        return getPreviousStep();
    }

    public void setupLimits(int currentProgress, int maxLimit) {
        setSteps(maxLimit);
        setPreviousStep(currentProgress);
        setNextStep(currentProgress);
    }
}