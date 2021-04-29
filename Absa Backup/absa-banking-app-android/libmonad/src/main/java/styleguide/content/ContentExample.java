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

package styleguide.content;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.SelectorInterface;
import styleguide.forms.SelectorList;
import styleguide.utils.ImageUtils;
import za.co.absa.presentation.uilib.R;

public class ContentExample extends AppCompatActivity {

    private SimpleCarouselViewPager simpleCarouselViewPager;
    private ExtendedCarouselViewPager extendedViewPagerExample;
    private CardView cardView;
    private ContactView contactContent;
    private ProfileView profileContent;
    private Bitmap profileImage;
    private BeneficiaryView beneficiaryContentCell;
    private BeneficiaryView beneficiaryContentHeader;
    private TitleAndDescriptionView title2TitleAndDescriptionView;
    private HeadingView headingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_example);
        initViews();
    }

    private void initViews() {
        headingView = findViewById(R.id.heading_view);

        title2TitleAndDescriptionView = findViewById(R.id.title2_text_view);

        cardView = findViewById(R.id.card_view);
        cardView.setCard(new Card("Credit Card", "4901 3445 667"));

        contactContent = findViewById(R.id.contact_view);
        contactContent.setContact(new Contact("Call Centre", "082 134 8123"));

        profileContent = findViewById(R.id.profile_view);
        profileImage = ImageUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_call_dark);
        profileContent.setProfile(new Profile("Idrice Sop", "Personal Bank Account", profileImage));

        beneficiaryContentCell = findViewById(R.id.beneficiary_view_cell);
        beneficiaryContentCell.setBeneficiary(new BeneficiaryListItem("Jonathan Richard Gears Of War", "082 134 8123", null));

        beneficiaryContentHeader = findViewById(R.id.beneficiary_view_header);
        beneficiaryContentHeader.setBeneficiary(new BeneficiaryListItem("Jonathan Richard Gears Of War", "082 134 8123", "R500.00 on 21 August 2018"));

        simpleCarouselViewPager = findViewById(R.id.image_description_view_pager);
        extendedViewPagerExample = findViewById(R.id.extendedCarouselViewPager);
        populateImageViewPager();

        AccountInformation accountOne = new AccountInformation();
        accountOne.accountName = "Current Account";
        accountOne.accountNumber  = "1234 568 89";

        AccountInformation accountTwo = new AccountInformation();
        accountTwo.accountName = "Credit Card Account";
        accountTwo.accountNumber  = "1234 568 89";

        AccountInformation accountThree = new AccountInformation();
        accountThree.accountName = "Home Loan Account";
        accountThree.accountNumber  = "1234 568 89";

        AccountInformation accountFour = new AccountInformation();
        accountFour.accountName = "Savings Account";
        accountFour.accountNumber  = "1234 568 89";

        AccountInformation accountFive = new AccountInformation();
        accountFive.accountName = "Cheque Account Account";
        accountFive.accountNumber  = "1234 568 89";

        SelectorList<AccountInformation> bankAccounts = new SelectorList<>();
        bankAccounts.add(accountOne);
        bankAccounts.add(accountTwo);
        bankAccounts.add(accountThree);
        bankAccounts.add(accountFour);
        bankAccounts.add(accountFive);
    }

    private void populateImageViewPager() {
        List<CarouselPage> pages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            pages.add(new CarouselPage("hello " + String.valueOf(i), "I'm secondary content created!", R.drawable.gradient_orange_pink));
        }
        extendedViewPagerExample.populateExtendedCarouselPager(pages);
    }

    public class AccountInformation implements SelectorInterface {

        String accountName;
        String accountNumber;

        @Override
        public String getDisplayValue() {
            return accountName;
        }

        @Override
        public String getDisplayValueLine2() {
            return accountNumber;
        }
    }
}
