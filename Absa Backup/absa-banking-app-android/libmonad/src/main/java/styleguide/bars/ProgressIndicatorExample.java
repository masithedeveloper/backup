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

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import za.co.absa.presentation.uilib.R;

public class ProgressIndicatorExample extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_indicator_example);
        Button button = findViewById(R.id.animate_button);


        final LimitsIndicator limitProgressIndicator = findViewById(R.id.progress_indicator_1);
        limitProgressIndicator.setupLimits(100, 6000);
        limitProgressIndicator.setStepSize(1000);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limitProgressIndicator.nextStep();

                ProgressIndicatorView progressIndicator2 = findViewById(R.id.progress_indicator_2);
                progressIndicator2.animateNextStep();

                ProgressIndicatorView progressIndicator3 = findViewById(R.id.progress_indicator_3);
                progressIndicator3.animateNextStep();

                int nextStep = 6;
                ProgressIndicatorView progressIndicator4 = findViewById(R.id.progress_indicator_4);
                progressIndicator4.setNextStep(nextStep);

                ProgressIndicatorView progressIndicator5 = findViewById(R.id.progress_indicator_5);
                progressIndicator5.goBackStep();

                ProgressIndicatorView progressIndicator6 = findViewById(R.id.progress_indicator_6);
                progressIndicator6.animateNextStep();

                ProgressIndicatorView progressIndicator7 = findViewById(R.id.progress_indicator_7);
                progressIndicator7.animateNextStep();
            }
        });
    }
}