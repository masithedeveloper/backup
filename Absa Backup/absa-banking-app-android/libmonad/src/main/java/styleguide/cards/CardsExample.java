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

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import za.co.absa.presentation.uilib.R;

public class CardsExample extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cards_example);

        AlertView alertView1 = findViewById(R.id.alert_view_example1);
        AlertView alertView2 = findViewById(R.id.alert_view_example2);
        OfferView offerView1 = findViewById(R.id.offer_view_example1);
        OfferView offerView2 = findViewById(R.id.offer_view_example2);

        Offer offer = new Offer("This is a test", "This is a label", "R 500.50", "Per month", false);
        Offer offer2 = new Offer("This is a test 2", "This is a label 2", "R 1500.50", "Per day", true);

        offerView1.setOffer(offer);
        offerView2.setOffer(offer2);

        alertView1.setAlert(new Alert("This is a test", "This is a label"));
        alertView2.setAlert(new Alert("This is a test 2", "This is a label 2"));

        TransactionView transactionCard = findViewById(R.id.transaction_card);

        transactionCard.setTransaction(new Transaction("This is a test", "19 Feb 2018, 2:30pm", "R 2500.43", true));

        AccountView accountCard = findViewById(R.id.account_card_example);
        accountCard.setAccount(new Account("R500.00", "4901 2345 567", "Current Account", "Current balance"));

        offerView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CardsExample.this, "Offer 1", Toast.LENGTH_SHORT).show();
            }
        });

        offerView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CardsExample.this, "Offer 2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
