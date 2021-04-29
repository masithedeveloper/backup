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
package com.barclays.absa.banking.presentation.contactDetail;

import android.util.Pair;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.Contact.ContactDetail;
import com.barclays.absa.banking.boundary.model.androidContact.EmailAddresses;
import com.barclays.absa.banking.boundary.model.androidContact.FaxNumbers;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.framework.app.BMBApplication;

import java.util.List;

public class ContactDetailViewModel {

    private Contact contact;
    private ContactDetail contactDetail;
    private List<Pair<String, String>> contactDetailList;

    public ContactDetailViewModel(Contact contact, ContactDetail contactDetail) {
        this.contact = contact;
        this.contactDetail = contactDetail;
        initializeContactList();
    }

    public boolean hasContactDetailList() {
        return ((contactDetailList != null) && (contactDetailList.size() > 0));
    }

    public String getContactTypeTitle() {
        switch (contactDetail) {
            case PHONE_NUMBER:
                return BMBApplication.getInstance().getString(R.string.select_mobile_number);
            case FAX_NUMBER:
                return BMBApplication.getInstance().getString(R.string.select_fax_number);
            case EMAIL:
                return BMBApplication.getInstance().getString(R.string.select_email_address);
            default:
                return "";
        }
    }

    public int getContactTypeImage() {
        switch (contactDetail) {
            case PHONE_NUMBER:
                return R.drawable.ic_phone;
            case FAX_NUMBER:
                return R.drawable.ic_fax;
            case EMAIL:
                return R.drawable.ic_email;
            default:
                return 0;
        }
    }

    public List<Pair<String, String>> getContactDetailList() {
        return contactDetailList;
    }

    void initializeContactList() {
        switch (contactDetail) {
            case PHONE_NUMBER:
                PhoneNumbers phoneNumbers = contact.getPhoneNumbers();
                contactDetailList = phoneNumbers == null ? null : phoneNumbers.getPhoneNumberPairList();
                break;
            case EMAIL:
                EmailAddresses emailAddresses = contact.getEmailAddresses();
                contactDetailList = emailAddresses == null ? null : emailAddresses.getEmailAddressPairList();
                break;
            case FAX_NUMBER:
                FaxNumbers faxNumbers = contact.getFaxNumbers();
                contactDetailList = faxNumbers == null ? null : faxNumbers.getFaxNumberPairList();
                break;
            default:
                break;
        }
    }
}
