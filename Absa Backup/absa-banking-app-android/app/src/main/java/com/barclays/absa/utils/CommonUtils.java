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
package com.barclays.absa.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.avaf.ui.AvafConstants;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.PurchaseHistoryElectricityTokens;
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.boundary.model.UniversalBank;
import com.barclays.absa.banking.boundary.model.UniversalBanks;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.Contact.ContactDetail;
import com.barclays.absa.banking.boundary.model.androidContact.EmailAddresses;
import com.barclays.absa.banking.boundary.model.androidContact.FaxNumbers;
import com.barclays.absa.banking.boundary.model.androidContact.NameDetails;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricityBeneficiaryTokens;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricityReceiptObject;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricityToken;
import com.barclays.absa.banking.databinding.DialogContactDetailsBinding;
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG;
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.MultipleBeneficiarySectionListItem;
import com.barclays.absa.banking.presentation.adapters.ContactDetailListAdapter;
import com.barclays.absa.banking.presentation.adapters.ContactDetailListAdapter.ContactDetailSelection;
import com.barclays.absa.banking.presentation.adapters.SectionListItem;
import com.barclays.absa.banking.presentation.contactDetail.ContactDetailViewModel;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.fileUtils.FileReaderUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import styleguide.forms.NormalInputView;
import styleguide.utils.extensions.StringExtensions;

public class CommonUtils implements BMBConstants {

    private static final String[] alphabets = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "#"};
    public static boolean isOddActivityTransition;
    private static List<UniversalBank> UNIVERSAL_BANKS = getUniversalBanks();
    private static boolean manageBeneficiaryPage;

    public static boolean isManageBeneficiaryPage() {
        return manageBeneficiaryPage;
    }

    public static void setManageBeneficiaryPage(boolean manageBeneficiaryPage) {
        CommonUtils.manageBeneficiaryPage = manageBeneficiaryPage;
    }

    private static List<UniversalBank> getUniversalBanks() {
        try {
            String universalBanksResponse = FileReaderUtils.getFileContent("universal_banks.json");
            UniversalBanks universalBanks = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(universalBanksResponse, UniversalBanks.class);
            return universalBanks.getList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UniversalBank getUniversalBank(String bankName) {
        for (UniversalBank universalBank : UNIVERSAL_BANKS) {
            if (universalBank.getBankName() != null && universalBank.getBankName().equalsIgnoreCase(bankName)) {
                return universalBank;
            }
        }
        return null;
    }

    /**
     * Checks if is account supported.
     *
     * @param account the account
     * @return true, if is account supported
     */

    public static boolean isStandaloneAccountSupported(AccountObject account) {
        return account.getAccountType().equalsIgnoreCase(AccountTypesBMG.absaVehicleAndAssetFinance.name())
                || account.getAccountType().equalsIgnoreCase(AccountTypesBMG.homeLoan.name())
                || account.getAccountType().equalsIgnoreCase(AccountTypesBMG.noticeDeposit.name());
    }

    public static boolean isAccountSupported(AccountObject account) {
        // Cheque Account : currentAccount
        // Savings Account : savingsAccount
        // Credit Account : creditCard
        // Personal Loan Account : personalLoan
        // Home Loan Account : homeLoan
        // Money Market ( Investment Account ) : savingsAccount
        // Notice Deposit ( Investment Account ) : noticeDeposit
        // Fixed Deposit ( Investment Account ) : termDeposit
        // AVAF Account : absaVehicleAndAssetFinance
        // CIA Account : cia

        return (
                account.getAccountType().equalsIgnoreCase("currentAccount")
                        || account.getAccountType().equalsIgnoreCase("savingsAccount")
                        || account.getAccountType().equalsIgnoreCase("creditCard")
                        || account.getAccountType().equalsIgnoreCase("personalLoan")
                        || account.getAccountType().equalsIgnoreCase("homeLoan")
                        || account.getAccountType().equalsIgnoreCase("noticeDeposit")
                        || account.getAccountType().equalsIgnoreCase("termDeposit")
                        || account.getAccountType().equalsIgnoreCase("absaVehicleAndAssetFinance")
                        || account.getAccountType().equalsIgnoreCase("onlineShareTrading")
                        || account.getAccountType().equalsIgnoreCase("absaReward")
                        || account.getAccountType().equalsIgnoreCase("cia"))
                && account.isAccountDetailAllowed();
    }

    /**
     * Checks if is account supported.
     *
     * @param account the account
     * @return true, if is account supported
     */
    public static boolean isAccountSupportedRebuild(AccountObject account) {
        // Cheque Account : currentAccount
        // Savings Account : savingsAccount
        // Credit Account : creditCard
        // Personal Loan Account : personalLoan
        // Home Loan Account : homeLoan
        // Absa Rewards Account : absaReward
        // Money Market ( Investment Account ) : savingsAccount
        // Notice Deposit ( Investment Account ) : noticeDeposit
        // Fixed Deposit ( Investment Account ) : termDeposit
        // AVAF Account : absaVehicleAndAssetFinance
        // CIA Account : cia

        List<String> accountSupported = new ArrayList<>();
        accountSupported.add("currentAccount".toLowerCase());
        accountSupported.add("savingsAccount".toLowerCase());
        accountSupported.add("creditCard".toLowerCase());
        accountSupported.add("homeLoan".toLowerCase());
        accountSupported.add("absaReward".toLowerCase());
        accountSupported.add("termDeposit".toLowerCase());
        accountSupported.add("noticeDeposit".toLowerCase());
        accountSupported.add("cia".toLowerCase());
        accountSupported.add("advantage".toLowerCase());
        accountSupported.add("insuranceCluster".toLowerCase());
        accountSupported.add("investmentCluster".toLowerCase());

        if (BuildConfig.TOGGLE_DEF_UNIT_TRUST) {
            accountSupported.add("unitTrustAccount".toLowerCase());
        }

        FeatureSwitching featureSwitching = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (BuildConfig.TOGGLE_DEF_PERSONAL_LOANS_HUB_ENABLED && featureSwitching != null && featureSwitching.getPersonalLoanHub() == FeatureSwitchingStates.ACTIVE.getKey()) {
            accountSupported.add("personalLoan".toLowerCase());
        }

        if (featureSwitching != null && featureSwitching.getVehicleFinanceHub() != FeatureSwitchingStates.GONE.getKey() && !AvafConstants.BALANCE_NOT_AVAILABLE.equalsIgnoreCase(account.getCurrentBalance().getAmount()) && !AvafConstants.BALANCE_FAILED.equalsIgnoreCase(account.getCurrentBalance().getAmount())) {
            accountSupported.add("absaVehicleAndAssetFinance".toLowerCase());
        }

        String accountType = account.getAccountType().toLowerCase();
        return accountSupported.contains(accountType);
    }

    /**
     * Gets the device specific services.
     *
     * @param typeOfData the type of services
     * @return the device specific services
     */

    public static String getDeviceSpecificData(String typeOfData) {
        Context context = BMBApplication.getInstance();
        if (IMEI_KEY.equalsIgnoreCase(typeOfData)) {
            final String hardwareDeviceId = DeviceUtils.getHardwareDeviceId();
            return hardwareDeviceId == null ? "" : hardwareDeviceId;
        } else if (SERIAL_NUMBER_KEY.equalsIgnoreCase(typeOfData)) {

            String serialnum = "";
            try {
                final Class<?> c = Class.forName("android.os.SystemProperties");
                final Method get = c.getMethod("get", String.class, String.class);
                serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
            } catch (final Exception ignored) {
            }
            return serialnum == null ? "" : serialnum;
        } else if (UDID_KEY.equalsIgnoreCase(typeOfData)) {
            final String udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return udid == null ? "" : udid;
        } else if (MAC_ID_KEY.equalsIgnoreCase(typeOfData)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String macID = "";
                if (wifiInfo != null) {
                    macID = wifiInfo.getMacAddress();
                }
                return macID == null ? "" : macID;
            } else {
                return "";
            }
        } else
            return "";
    }

    /**
     * Gets the current application locale.
     *
     * @return the current application locale
     */
    public static Locale getCurrentApplicationLocale() {
        return BMBApplication.getApplicationLocale();
    }

    public static void setInputFilter(EditText field) {
        setInputFilter(field, 20);
    }

    public static void setInputFilter(EditText field, int length) {
        if (field == null)
            return;

        InputFilter[] Textfilters = new InputFilter[2];
        Textfilters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {

                char[] acceptedChars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '.', ' ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

                for (int index = start; index < end; index++) {
                    if (!new String(acceptedChars).contains(String.valueOf(source.charAt(index)))) {
                        return "";
                    }
                }
            }
            return null;
        };

        Textfilters[1] = new InputFilter.LengthFilter(length);
        field.setFilters(Textfilters);
    }

    public static boolean isPrivateBanker() {
        String customerType = "";
        try {
            customerType = CustomerProfileObject.getInstance().getSbuSegment();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return customerType.equalsIgnoreCase(PRIVATE_CUSTOMER_CODE);
    }

    /**
     * Get all the contact details for the specified contactId
     *
     * @param context     The required context
     * @param contactsUri The selected contact's URI
     * @return The contact at the specified contactId
     */
    public static Contact getContact(Context context, Uri contactsUri) {
        Contact contact = new Contact();
        if (contactsUri != null) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor contactsCursor = contentResolver.query(contactsUri, null, null, null, null);
            if (contactsCursor != null && contactsCursor.moveToFirst()) {
                // Get contactId required for selection criteria
                int index = contactsCursor.getColumnIndex(BaseColumns._ID);
                if (index > -1) {
                    String contactId = contactsCursor.getString(index);
                    // Get the contact details
                    contact.setCompanyName(getCompanyName(contentResolver, contactId));
                    contact.setNameDetails(getNameDetails(contentResolver, contactId));
                    contact.setPhoneNumbers(getPhoneNumbers(contentResolver, contactId));
                    contact.setFaxNumbers(getFaxNumbers(contentResolver, contactId));
                    contact.setEmailAddresses(getEmailAddresses(contentResolver, contactId));
                }
            }
            if (contactsCursor != null) {
                contactsCursor.close();
            }
        }
        return contact;
    }

    /**
     * Pick phone number from phone book
     *
     * @param editText    An NormalInputView that is going to be populated with phone number
     * @param requestCode The intent request code
     */
    public static void pickPhoneNumber(@Nullable EditText editText, int requestCode) {
        if (editText != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            ((Activity) editText.getContext()).startActivityForResult(intent, requestCode);
            if (inputMethodManager != null) {
                editText.requestFocus();
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    /**
     * Get the contact's name details using the specified contactId
     *
     * @param contentResolver The content resolver required to do the query
     * @param contactId       The id of the contact we are querying
     * @return The contact's name details for the specified contact
     */
    private static NameDetails getNameDetails(ContentResolver contentResolver, String contactId) {
        String[] nameDetailsProjection = {StructuredName.DISPLAY_NAME, StructuredName.PREFIX,
                StructuredName.GIVEN_NAME, StructuredName.MIDDLE_NAME, StructuredName.FAMILY_NAME};
        Cursor nameDetailsCursor = contentResolver.query(
                Data.CONTENT_URI,
                nameDetailsProjection,
                Data.CONTACT_ID + " = ? AND " + StructuredName.MIMETYPE + " = ?",
                new String[]{contactId, StructuredName.CONTENT_ITEM_TYPE}, null
        );

        NameDetails nameDetails = new NameDetails();
        if (nameDetailsCursor != null) {
            try {
                if (nameDetailsCursor.moveToFirst()) {
                    nameDetails.setPrefix(nameDetailsCursor.getString(nameDetailsCursor.getColumnIndex(StructuredName.PREFIX)));
                    nameDetails.setDisplayName(nameDetailsCursor.getString(nameDetailsCursor.getColumnIndex(StructuredName.DISPLAY_NAME)));
                    nameDetails.setFirstName(nameDetailsCursor.getString(nameDetailsCursor.getColumnIndex(StructuredName.GIVEN_NAME)));
                    nameDetails.setMiddleName(nameDetailsCursor.getString(nameDetailsCursor.getColumnIndex(StructuredName.MIDDLE_NAME)));
                    nameDetails.setFamilyName(nameDetailsCursor.getString(nameDetailsCursor.getColumnIndex(StructuredName.FAMILY_NAME)));
                }
                nameDetailsCursor.close();
            } catch (android.database.CursorIndexOutOfBoundsException e) {
                BMBLogger.e(e.getMessage());
            }
        }

        return nameDetails;
    }

    /**
     * Get the contact's company name using the specified contactId
     *
     * @param contentResolver The content resolver required to do the query
     * @param contactId       The id of the contact we are querying
     * @return The company name for the specified contact
     */
    private static String getCompanyName(ContentResolver contentResolver, String contactId) {
        String[] companyNameProjection = {Organization.COMPANY};
        Cursor companyCursor = contentResolver.query(
                Data.CONTENT_URI,
                companyNameProjection,
                Organization.CONTACT_ID + " = ? AND " + StructuredName.MIMETYPE + " = ?",
                new String[]{contactId, Organization.CONTENT_ITEM_TYPE},
                null
        );
        if (companyCursor != null) {
            if (companyCursor.moveToFirst()) {
                String companyName = companyCursor.getCount() == 0 ?
                        "" : companyCursor.getString(companyCursor.getColumnIndex(Organization.COMPANY));
                companyCursor.close();
                return companyName;
            } else {
                companyCursor.close();
            }
        }
        return "";
    }

    /**
     * Get the contact's email addresses using the specified contactId
     *
     * @param contentResolver The content resolver required to do the query
     * @param contactId       The id of the contact we are querying
     * @return The email addresses for the specified contact
     */
    private static EmailAddresses getEmailAddresses(ContentResolver contentResolver, String contactId) {
        String[] emailAddressProjection = {Email.TYPE, Email.ADDRESS};
        Cursor emailAddressCursor = contentResolver.query(
                Email.CONTENT_URI,
                emailAddressProjection,
                Email.CONTACT_ID + " = ? AND " + StructuredName.MIMETYPE + " = ?",
                new String[]{contactId, Email.CONTENT_ITEM_TYPE},
                null
        );

        EmailAddresses emailAddresses = new EmailAddresses();
        if (emailAddressCursor != null) {
            while (emailAddressCursor.moveToNext()) {
                int emailType = emailAddressCursor.getInt(emailAddressCursor.getColumnIndex(Email.TYPE));
                String email = emailAddressCursor.getString(emailAddressCursor.getColumnIndex(Email.DATA));
                if (!TextUtils.isEmpty(email)) {
                    switch (emailType) {
                        case Email.TYPE_HOME:
                            emailAddresses.setHome(email);
                            break;
                        case Email.TYPE_MOBILE:
                            emailAddresses.setMobile(email);
                            break;
                        case Email.TYPE_WORK:
                            emailAddresses.setWork(email);
                            break;
                        case Email.TYPE_OTHER:
                            emailAddresses.setOther(email);
                            break;
                        case Email.TYPE_CUSTOM:
                            emailAddresses.setCustom(email);
                            break;
                        default:
                            break;
                    }
                }
            }
            emailAddressCursor.close();
        }
        return emailAddresses;
    }

    /**
     * Get the contact's phone numbers using the specified contactId
     *
     * @param contentResolver The content resolver required to do the query
     * @param contactId       The id of the contact we are querying
     * @return The numbers for the specified contact
     */
    private static PhoneNumbers getPhoneNumbers(ContentResolver contentResolver, String contactId) {
        String[] phoneNumberProjection = {Phone.TYPE, Phone.NUMBER};
        Cursor phoneNumberCursor = contentResolver.query(
                Phone.CONTENT_URI,
                phoneNumberProjection,
                Phone.CONTACT_ID + " = ? AND " + StructuredName.MIMETYPE + " = ?",
                new String[]{contactId, Phone.CONTENT_ITEM_TYPE},
                null);

        PhoneNumbers phoneNumbers = new PhoneNumbers();
        if (phoneNumberCursor != null) {
            while (phoneNumberCursor.moveToNext()) {
                int phoneNumberType = phoneNumberCursor.getInt(phoneNumberCursor.getColumnIndex(Phone.TYPE));
                String number = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(Phone.NUMBER));
                if (!TextUtils.isEmpty(number)) {
                    String nonFormattedPhoneNumber = StringExtensions.getUnFormattedPhoneNumber(number);

                    switch (phoneNumberType) {
                        case Phone.TYPE_MAIN:
                            phoneNumbers.setMain(nonFormattedPhoneNumber);
                            break;
                        case Phone.TYPE_MOBILE:
                            phoneNumbers.setMobile(nonFormattedPhoneNumber);
                            break;
                        case Phone.TYPE_HOME:
                            phoneNumbers.setHome(nonFormattedPhoneNumber);
                            break;
                        case Phone.TYPE_WORK:
                            phoneNumbers.setWork(nonFormattedPhoneNumber);
                            break;
                        case Phone.TYPE_OTHER:
                            phoneNumbers.setOther(nonFormattedPhoneNumber);
                            break;
                        default:
                            break;
                    }
                }
            }
            phoneNumberCursor.close();
        }

        return phoneNumbers;
    }

    /**
     * Get the contact's fax numbers using the specified contactId
     *
     * @param contentResolver The content resolver required to do the query
     * @param contactId       The id of the contact we are querying
     * @return The fax numbers for the specified contact
     */
    private static FaxNumbers getFaxNumbers(ContentResolver contentResolver, String contactId) {
        String[] faxNumberProjection = {Phone.TYPE, Phone.NUMBER};
        Cursor faxNumberCursor = contentResolver.query(
                Phone.CONTENT_URI,
                faxNumberProjection,
                Phone.CONTACT_ID + " = ? AND " + StructuredName.MIMETYPE + " = ?",
                new String[]{contactId, Phone.CONTENT_ITEM_TYPE},
                null);

        FaxNumbers faxNumbers = new FaxNumbers();
        if (faxNumberCursor != null) {
            while (faxNumberCursor.moveToNext()) {
                int faxNumberType = faxNumberCursor.getInt(faxNumberCursor.getColumnIndex(Phone.TYPE));
                String number = faxNumberCursor.getString(faxNumberCursor.getColumnIndex(Phone.NUMBER));
                if (!TextUtils.isEmpty(number)) {
                    String nonFormattedFaxNumber = StringExtensions.toUnFormattedFaxNumber(number);

                    switch (faxNumberType) {
                        case Phone.TYPE_FAX_HOME:
                            faxNumbers.setHomeFax(nonFormattedFaxNumber);
                            break;
                        case Phone.TYPE_FAX_WORK:
                            faxNumbers.setWorkFax(nonFormattedFaxNumber);
                            break;
                        default:
                            break;
                    }
                }
            }
            faxNumberCursor.close();
        }

        return faxNumbers;
    }

    public static Pair<String, String> getNameDetails(Contact contact) {
        if (contact.getNameDetails() != null) {
            final String firstName = contact.getNameDetails().getFirstName();
            final String familyName = contact.getNameDetails().getFamilyName();
            final String companyName = contact.getCompanyName();

            // Populate what we received for a contact name and surname (contact might also be a company)
            if (!TextUtils.isEmpty(firstName)) {
                return new Pair<>(firstName, familyName);
            } else if (!TextUtils.isEmpty(companyName)) {
                return new Pair<>(companyName, null);
            }
        }
        return null;
    }

    private static void setErrorMessage(EditText editText, String errorMessage) {
        editText.setText("");
        editText.setError(errorMessage);
        editText.requestFocus();
        editText.setVisibility(View.VISIBLE);
    }

    private static void setErrorMessage(NormalInputView normalInputView, String errorMessage) {
        normalInputView.setError(errorMessage);
        normalInputView.requestFocus();
    }

    private static void updateMobileNumberOnValidation(EditText etMobileNumber, String mobileNumber, String errorMessage) {
        if (ValidationUtils.isValidMobileNumber(mobileNumber)) {
            etMobileNumber.setText(mobileNumber);
        } else {
            setErrorMessage(etMobileNumber, errorMessage);
        }
    }

    public static void updateMobileNumberOnValidation(@NonNull NormalInputView normalInputView, String mobileNumber, String errorMessage) {
        if (ValidationUtils.isValidMobileNumber(mobileNumber)) {
            normalInputView.setText(mobileNumber);
            normalInputView.hideError();
        } else {
            normalInputView.setError(errorMessage);
            normalInputView.requestFocus();
        }
    }

    public static void updateMobileNumberOnSelection(Context context, final EditText etMobileNumber, Contact contact) {
        final String INVALID_MOBILE_NUMBER = context.getString(R.string.invalid_mobile_number);
        final ContactDetailViewModel contactDetailViewModel = new ContactDetailViewModel(contact, ContactDetail.PHONE_NUMBER);
        if (contactDetailViewModel.hasContactDetailList()) {
            List<Pair<String, String>> contactDetailList = contactDetailViewModel.getContactDetailList();
            final String mobileNumber = contactDetailList.get(0).second;
            if (contactDetailList.size() == 1) {
                updateMobileNumberOnValidation(etMobileNumber, mobileNumber, INVALID_MOBILE_NUMBER);
            } else {
                showContactDetailsDialog(context, contactDetailViewModel, (alertDialog, contactDetail) -> {
                    updateMobileNumberOnValidation(etMobileNumber, contactDetail, INVALID_MOBILE_NUMBER);
                    alertDialog.cancel();
                });
            }
        } else {
            setErrorMessage(etMobileNumber, INVALID_MOBILE_NUMBER);
        }
    }

    public static void updateMobileNumberOnSelection(Context context, final NormalInputView numberInputView, Contact contact) {
        final String INVALID_MOBILE_NUMBER = context.getString(R.string.invalid_mobile_number);
        final ContactDetailViewModel contactDetailViewModel = new ContactDetailViewModel(contact, ContactDetail.PHONE_NUMBER);
        if (contactDetailViewModel.hasContactDetailList()) {
            List<Pair<String, String>> contactDetailList = contactDetailViewModel.getContactDetailList();
            final String mobileNumber = contactDetailList.get(0).second;
            if (contactDetailList.size() == 1) {
                updateMobileNumberOnValidation(numberInputView, mobileNumber, INVALID_MOBILE_NUMBER);
            } else {
                showContactDetailsDialog(context, contactDetailViewModel, (alertDialog, contactDetail) -> {
                    updateMobileNumberOnValidation(numberInputView, contactDetail, INVALID_MOBILE_NUMBER);
                    alertDialog.cancel();
                });
            }
        } else {
            setErrorMessage(numberInputView, INVALID_MOBILE_NUMBER);
        }
    }

    private static void showContactDetailsDialog(Context context, ContactDetailViewModel contactDetailViewModel, ContactDetailSelection contactDetailSelection) {
        List<Pair<String, String>> contactDetails = contactDetailViewModel.getContactDetailList();
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_contact_details, null);

        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.MyDialogTheme).setView(dialogView).create();
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
        }

        DialogContactDetailsBinding view = DataBindingUtil.bind(dialogView);
        view.setContactDetailViewModel(contactDetailViewModel);

        view.contactDetailsRecyclerView.setHasFixedSize(true);
        view.contactDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        view.contactDetailsRecyclerView.setAdapter(new ContactDetailListAdapter(alertDialog, contactDetails, contactDetailSelection));
        alertDialog.show();
    }

    public static void callWelcomeActivity(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void showAlertDialogWelcomeScreen(final Context context) {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(context.getString(R.string.register_cancel_popup_title))
                .message(context.getString(R.string.register_cancel_popup_msg))
                .positiveDismissListener((dialog, which) -> {
                    Intent intent;
                    Class classToGoTo = WelcomeActivity.class;
                    if (appCacheService.getUserLoggedInStatus()) {
                        classToGoTo = SimplifiedLoginActivity.class;
                    }
                    intent = new Intent(context, classToGoTo);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }));
    }

    public static void detailNeedsToBeUpdated(final Context ctx) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(ctx.getString(R.string.register_contact_details))
                .message(ctx.getString(R.string.register_devicelink_require_popup_msg))
                .build());
    }

    private static String getOriginalPath(String path) {
        String uri;
        if (path.contains("/ORIGINAL")) {
            String[] url = path.split("/ORIGINAL");
            uri = url[0];
        } else {
            uri = path;
        }

        return uri;
    }

    /**
     * Perform crop.
     */
    public static void performCrop(Activity context, Uri picUri, Uri tempCropImageUri) {
        // take care of exceptions
        try {
            Uri uri;
            if (picUri != null && picUri.toString().contains("%3A")) {
                String[] photo_split = picUri.toString().split("%3A");
                String imageUrl = "content:" + photo_split[1].replace("%2F", "/");
                uri = Uri.parse(CommonUtils.getOriginalPath(imageUrl));
            } else {
                uri = picUri;
            }
            // call the standard crop action intent (the user device may not
            // support it)
            final Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 150);
            cropIntent.putExtra("outputY", 150);
            cropIntent.putExtra("scale", true);
            // retrieve services on return
            cropIntent.putExtra("return-services", false);
            // because return-services is fale, we'll use tempCropImageUri, to retrieve the image in onActivityResult
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCropImageUri);

            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(cropIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo info : infos) {
                    context.grantUriPermission(info.activityInfo.packageName, tempCropImageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            // start the activity - we handle returning in onActivityResult
            context.startActivityForResult(cropIntent, REQUESTCODE_CUSTOMER_PHOTO_AFTER_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (final ActivityNotFoundException anfe) {
            BMBLogger.e(anfe.getMessage());
        }
    }

    /**
     * Set height base on number of records in list view.
     */
    public static void setListViewHeightBasedOnChildren(ListView listView, BaseAdapter baseAdapter) {
        if (baseAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < baseAdapter.getCount(); i++) {
                View listItem = baseAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + ((listView.getDividerHeight() * (baseAdapter.getCount() - 1)));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    public static SectionListItem[] createSectionedList(List<BeneficiaryObject> lstBeneficiaryData) {
        SectionListItem[] tempArray = new SectionListItem[lstBeneficiaryData.size()];
        for (int i = 0; i < tempArray.length; i++) {
            String beneficiaryName = Character.toString(lstBeneficiaryData.get(i).getBeneficiaryName().charAt(0));

            if (beneficiaryName != null && beneficiaryName.matches("[^A-Za-z0-9 ]")) {
                tempArray[i] = new SectionListItem(lstBeneficiaryData.get(i), alphabets[alphabets.length - 1]);
            } else {
                for (String alphabetLetter : alphabets) {
                    if (alphabetLetter.equalsIgnoreCase(Character.toString(lstBeneficiaryData.get(i).getBeneficiaryName().charAt(0)))) {
                        tempArray[i] = new SectionListItem(lstBeneficiaryData.get(i), alphabetLetter);
                    }
                }
            }
        }
        return tempArray;
    }

    // TODO Probably temoporary
    public static MultipleBeneficiarySectionListItem[] createCashSendPlusSectionedList(List<BeneficiaryObject> lstBeneficiaryData) {
        MultipleBeneficiarySectionListItem[] tempArray = new MultipleBeneficiarySectionListItem[lstBeneficiaryData.size()];
        for (int i = 0; i < tempArray.length; i++) {
            String beneficiaryName1 = lstBeneficiaryData.get(i).getBeneficiaryName();
            String beneficiaryName = (beneficiaryName1 != null && beneficiaryName1.length() > 0) ? Character.toString(beneficiaryName1.charAt(0)) : " ";

            if (beneficiaryName.matches("[^A-Za-z0-9 ]")) {
                tempArray[i] = new MultipleBeneficiarySectionListItem(lstBeneficiaryData.get(i), alphabets[alphabets.length - 1]);
            } else {
                for (String alphabetLetter : alphabets) {
                    if (alphabetLetter.equalsIgnoreCase(beneficiaryName)) {
                        tempArray[i] = new MultipleBeneficiarySectionListItem(lstBeneficiaryData.get(i), alphabetLetter);
                    }
                }
            }
        }
        return tempArray;
    }

    public static MultipleBeneficiarySectionListItem[] createMultiplePaymentsSectionedList(List<RegularBeneficiary> lstBeneficiaryData) {
        MultipleBeneficiarySectionListItem[] tempArray = new MultipleBeneficiarySectionListItem[lstBeneficiaryData.size()];
        for (int i = 0; i < tempArray.length; i++) {
            String beneficiaryName1 = lstBeneficiaryData.get(i).getBeneficiaryName();
            String beneficiaryName = (beneficiaryName1 != null && beneficiaryName1.length() > 0) ? Character.toString(beneficiaryName1.charAt(0)) : " ";

            if (beneficiaryName.matches("[^A-Za-z0-9 ]")) {
                tempArray[i] = new MultipleBeneficiarySectionListItem(lstBeneficiaryData.get(i), alphabets[alphabets.length - 1]);
            } else {
                for (String alphabetLetter : alphabets) {
                    if (alphabetLetter.equalsIgnoreCase(beneficiaryName)) {
                        tempArray[i] = new MultipleBeneficiarySectionListItem(lstBeneficiaryData.get(i), alphabetLetter);
                    }
                }
            }
        }
        return tempArray;
    }

    /**
     * Sort list of beneficiary according to alphabet.
     */
    public static List<BeneficiaryObject> sortBeneficiaryData(List<BeneficiaryObject> mBeneficiaryDataObjects) {
        // Sort currency services alphabetically based on currency name
        Collections.sort(mBeneficiaryDataObjects, (beneficiaryObject1, beneficiaryObject2) -> {
            if (beneficiaryObject1.getBeneficiaryName() != null && beneficiaryObject2 != null) {
                return beneficiaryObject1.getBeneficiaryName().compareToIgnoreCase(beneficiaryObject2.getBeneficiaryName());
            }
            return 0;
        });
        return mBeneficiaryDataObjects;
    }

    public static void setInputFilterForRestrictingSpecialCharacter(EditText field, int length) {
        if (field == null) {
            return;
        }
        InputFilter[] Textfilters = new InputFilter[2];
        Textfilters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                char[] acceptedChars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '-', '_', ' ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
                for (int index = start; index < end; index++) {
                    if (!new String(acceptedChars).contains(String.valueOf(source.charAt(index)))) {
                        return "";
                    }
                }
            }
            return null;
        };
        Textfilters[1] = new InputFilter.LengthFilter(length);
        field.setFilters(Textfilters);
    }

    public static void setInputFilterForRestrictingSpecialCharacter(EditText field) {
        setInputFilterForRestrictingSpecialCharacter(field, 20);
    }

    public static PrepaidElectricityReceiptObject getReceiptData(PrepaidElectricity prepaidElectricity, PurchasePrepaidElectricityResultObject resultObject) {
        PrepaidElectricityReceiptObject prepaidElectricityReceiptObject = new PrepaidElectricityReceiptObject();
        PrepaidElectricityBeneficiaryTokens beneficiaryTokens = prepaidElectricity.getBeneficiaryTokens();
        double vat = 0.0;
        double costOfElectricity = 0.0;
        double totalUnits = 0.0;
        if (beneficiaryTokens != null) {
            List<PrepaidElectricityToken> beneficiaryNormalTokens = beneficiaryTokens.getNormalTokens();
            List<PrepaidElectricityToken> beneficiaryDebtRecoveryTokens = beneficiaryTokens.getDebtRecoveryTokens();

            if (beneficiaryNormalTokens != null && !beneficiaryNormalTokens.isEmpty()) {
                for (PrepaidElectricityToken beneficiaryNormalToken : beneficiaryNormalTokens) {
                    if ((int) Double.parseDouble(beneficiaryNormalToken.getTokenUnits()) > 0) {
                        prepaidElectricityReceiptObject.setPurchasedUnit(beneficiaryNormalToken.getTokenUnits());
                        prepaidElectricityReceiptObject.setTransactionDateTimeStamp(beneficiaryNormalToken.getTokenDateStamp());
                        prepaidElectricityReceiptObject.setTokenTax(beneficiaryNormalToken.getTokenTax());
                        totalUnits = totalUnits + Double.parseDouble(beneficiaryNormalToken.getTokenUnits());
                    }

                    vat = vat + Double.parseDouble(beneficiaryNormalToken.getTokenTax());
                    costOfElectricity = costOfElectricity + Double.parseDouble(beneficiaryNormalToken.getTokenValue());
                }
            }

            if (beneficiaryDebtRecoveryTokens != null && !beneficiaryDebtRecoveryTokens.isEmpty()) {
                for (PrepaidElectricityToken beneficiaryDebtRecoveryToken : beneficiaryDebtRecoveryTokens) {
                    if (beneficiaryDebtRecoveryToken.getDebtAmount() != null) {
                        prepaidElectricityReceiptObject.setArrearsAmount(beneficiaryDebtRecoveryToken.getDebtAmount());
                    }
                }
            }

            if (beneficiaryTokens.getFixedCostTokens() != null && !beneficiaryTokens.getFixedCostTokens().isEmpty()) {
                prepaidElectricityReceiptObject.setChargesReason(beneficiaryTokens.getFixedCostTokens().get(beneficiaryTokens.getFixedCostTokens().size() - 1).getReason());
                prepaidElectricityReceiptObject.setChargesAmount(beneficiaryTokens.getFixedCostTokens().get(beneficiaryTokens.getFixedCostTokens().size() - 1).getAmount());
            }

            if (beneficiaryTokens.getMessageInfoToken() != null) {
                prepaidElectricityReceiptObject.setTi(beneficiaryTokens.getMessageInfoToken().getTarifIndex());
                prepaidElectricityReceiptObject.setScg(beneficiaryTokens.getMessageInfoToken().getSupplierCode());
                prepaidElectricityReceiptObject.setKrn(beneficiaryTokens.getMessageInfoToken().getKeyRevisionCode());
            }
        }
        prepaidElectricityReceiptObject.setVat(String.valueOf(vat));
        prepaidElectricityReceiptObject.setCostOfElectricity(String.valueOf(costOfElectricity));
        prepaidElectricityReceiptObject.setTotalUnits(String.valueOf(totalUnits));
        prepaidElectricityReceiptObject.setHasImage(resultObject.isHasImage());
        prepaidElectricityReceiptObject.setImageName(resultObject.getImageName());
        return prepaidElectricityReceiptObject;
    }

    public static PrepaidElectricityReceiptObject getPurchaseHistoryReceiptData(TransactionObject transactionObject) {
        PrepaidElectricityReceiptObject prepaidElectricityReceiptObject = new PrepaidElectricityReceiptObject();
        double vat = 0.0;
        double costOfElectricity = 0.0;
        double totalUnits = 0.0;
        if (transactionObject.getPurchaseHistoryElectricityTokens() != null && !transactionObject.getPurchaseHistoryElectricityTokens().isEmpty()) {
            for (PurchaseHistoryElectricityTokens electricityToken : transactionObject.getPurchaseHistoryElectricityTokens()) {
                totalUnits = totalUnits + Double.parseDouble(electricityToken.getTokenUnit());
                vat = vat + Double.parseDouble(electricityToken.getTokenTax());
                costOfElectricity = costOfElectricity + Double.parseDouble(electricityToken.getTokenValue());
            }
        }

        if (transactionObject.getPurchaseHistoryElectricityFixedCosts() != null && !transactionObject.getPurchaseHistoryElectricityFixedCosts().isEmpty()) {
            prepaidElectricityReceiptObject.setChargesReason(transactionObject.getPurchaseHistoryElectricityFixedCosts().
                    get(transactionObject.getPurchaseHistoryElectricityFixedCosts().size() - 1).getFixedCostReason());
            prepaidElectricityReceiptObject.setChargesAmount(transactionObject.getPurchaseHistoryElectricityFixedCosts().
                    get(transactionObject.getPurchaseHistoryElectricityFixedCosts().size() - 1).getFixedCostAmount());
        }

        if (transactionObject.getPurchaseHistoryElectricityDebts() != null && !transactionObject.getPurchaseHistoryElectricityDebts().isEmpty()) {
            prepaidElectricityReceiptObject.setArrearsAmount(transactionObject.getPurchaseHistoryElectricityDebts().
                    get(transactionObject.getPurchaseHistoryElectricityDebts().size() - 1).getDebtAmount());
        }

        prepaidElectricityReceiptObject.setTi(transactionObject.getTariffIndex());
        prepaidElectricityReceiptObject.setScg(transactionObject.getSupplierGroupCode());

        prepaidElectricityReceiptObject.setVat(String.valueOf(vat));
        prepaidElectricityReceiptObject.setCostOfElectricity(String.valueOf(costOfElectricity));
        prepaidElectricityReceiptObject.setTotalUnits(String.valueOf(totalUnits));
        prepaidElectricityReceiptObject.setTransactionDateTimeStamp(transactionObject.getDate());
        return prepaidElectricityReceiptObject;
    }

    public static void makeTextClickable(
            Context context, @StringRes int sentenceStringId, String textToMakeClickable,
            ClickableSpan actionToPerformWhenTextIsClicked, TextView linkTextView) {
        makeTextClickable(context, sentenceStringId, textToMakeClickable, actionToPerformWhenTextIsClicked, linkTextView, R.color.color_FF666666);
    }

    public static void makeTextClickable(
            Context context, String sentenceString, String textToMakeClickable,
            ClickableSpan actionToPerformWhenTextIsClicked, TextView linkTextView, int colorId) {

        if (isDarkMode(context)) {
            colorId = R.color.white;
        }

        int indexOfString = sentenceString.indexOf(textToMakeClickable);
        if (indexOfString > -1) {
            SpannableString spannableString = new SpannableString(sentenceString);
            spannableString.setSpan(actionToPerformWhenTextIsClicked, indexOfString, (indexOfString + textToMakeClickable.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), indexOfString, (indexOfString + textToMakeClickable.length()), 0);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorId)), indexOfString, (indexOfString + textToMakeClickable.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linkTextView.setText(spannableString);
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static void makeTextClickable(
            Context context, @StringRes int sentenceStringId, String textToMakeClickable,
            ClickableSpan actionToPerformWhenTextIsClicked, TextView linkTextView, int colorId) {

        if (isDarkMode(context)) {
            colorId = R.color.white;
        }

        int indexOfString = context.getString(sentenceStringId, textToMakeClickable).indexOf(textToMakeClickable);
        if (indexOfString > -1) {
            SpannableString spannableString = new SpannableString(context.getString(sentenceStringId, textToMakeClickable));
            spannableString.setSpan(actionToPerformWhenTextIsClicked, indexOfString, (indexOfString + textToMakeClickable.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), indexOfString, (indexOfString + textToMakeClickable.length()), 0);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorId)), indexOfString, (indexOfString + textToMakeClickable.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linkTextView.setText(spannableString);
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void makeMultipleTextClickable(
            Context context, @StringRes int sentenceStringId, String[] textsToMakeClickable,
            ClickableSpan[] actionsToPerformWhenTextIsClicked, TextView linkTextView, int colorId) {

        SpannableString spannableString = new SpannableString(context.getString(sentenceStringId));

        if (isDarkMode(context)) {
            colorId = R.color.white;
        }

        for (int i = 0; i < textsToMakeClickable.length; i++) {
            if (i < actionsToPerformWhenTextIsClicked.length) {
                String textToMakeClickable = textsToMakeClickable[i];
                int indexOfString = context.getString(sentenceStringId, textToMakeClickable).indexOf(textToMakeClickable);
                if (indexOfString > -1) {
                    spannableString.setSpan(actionsToPerformWhenTextIsClicked[i], indexOfString, (indexOfString + textToMakeClickable.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new UnderlineSpan(), indexOfString, (indexOfString + textToMakeClickable.length()), 0);
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorId)), indexOfString, (indexOfString + textToMakeClickable.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                return;
            }
        }

        linkTextView.setText(spannableString);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void highlightSearchString(String searchString, String inString, TextView textView) {
        int indexOfString = inString.toLowerCase().indexOf(searchString.toLowerCase());
        if (indexOfString != -1) {
            SpannableString spannableString = new SpannableString(inString);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfString, (indexOfString + searchString.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        }
    }


    public static void makeTextClickable(
            Context context, @StringRes int sentenceStringId, String textToMakeClickable,
            TextView linkTextView, int colorId, ClickableSpan actionToPerformWhenTextIsClicked) {
        makeTextClickable(context, sentenceStringId, textToMakeClickable, actionToPerformWhenTextIsClicked, linkTextView, colorId);
    }

    public static void makeTextClickable(
            Context context, @StringRes int sentenceStringId, @StringRes int textToMakeClickable,
            TextView linkTextView, int colorId, ClickableSpan actionToPerformWhenTextIsClicked) {
        makeTextClickable(context, sentenceStringId, context.getString(textToMakeClickable), actionToPerformWhenTextIsClicked, linkTextView, colorId);
    }

}