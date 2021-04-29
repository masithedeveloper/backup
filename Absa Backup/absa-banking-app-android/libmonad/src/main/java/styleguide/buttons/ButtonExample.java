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

package styleguide.buttons;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import za.co.absa.presentation.uilib.R;

public class ButtonExample extends AppCompatActivity {

    int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.buttons_example);

        FloatingActionButtonView floatingActionButtonView = findViewById(R.id.dark_fab);
        FloatingActionButtonView floatingActionButtonView2 = findViewById(R.id.light_fab2);

        floatingActionButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ButtonExample.this, String.valueOf(count++), Toast.LENGTH_SHORT).show();
            }
        });

        floatingActionButtonView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ButtonExample.this, String.valueOf(count--), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
