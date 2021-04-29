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
package com.barclays.absa.banking.card.services.card.dto;

import com.barclays.absa.banking.boundary.model.ManageCardConfirmLimit;
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails;
import com.barclays.absa.banking.boundary.model.OverdraftSnooze;
import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtectionQuote;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementConfirmation;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReasonsList;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardBureauData;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationResponse;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardOverdraft;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditLimitApplicationResult;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface CreditCardService {
    String OP0124_RETRIEVE_PIN = "OP0124";
    String OP0125_CARD_LIST = "OP0125";
    String OP0842_CREDIT_CARD_INFO = "OP0842";
    String OP0846_DISPLAY_OVERDRAFT_SNOOZE = "OP0846";
    String OP0863_VIEW_POLICY_DETAILS = "OP0863";
    String OP0864_FETCH_CREDIT_CARD_PROTECTION_INSURANCE_QUOTE = "OP0864";
    String OP0865_APPLY_CREDIT_CARD_PROTECTION_INSURANCE = "OP0865";
    String OP0869_CREDIT_CARD_REPLACEMENT_VALIDATION = "OP0869";
    String OP0871_CREDIT_CARD_REPLACEMENT_CONFIRMATION = "OP0871";
    String OP0893_REPLACEMENT_CREDIT_CARD_NUMBERS = "OP0893";
    String OP0905_BUREAU_DATA_FOR_VCL_CLI_PULL = "OP0905";
    String OP0906_CREDIT_LIMIT_INCREASE_APPLICATION = "OP0906";
    String OP2052_CREDIT_CARD_HUB = "OP2052";
    String OP2135_APPLY_FOR_CREDIT_CARD = "OP2135";

    void fetchCreditCardInformation(String creditCardNumber, ExtendedResponseListener<CreditCardInformation> creditCardInfoResponseListener);
    void fetchCreditCardDetails(ManageCardLimitDetails manageCardLimitDetails, String posLimitValue, String atmLimitValue, ExtendedResponseListener<ManageCardConfirmLimit> responseListener);
    void fetchStopAndReplaceCardReasons(String cardNumber, ExtendedResponseListener<CreditCardReplacementReasonsList> responseListener);
    void validateStopAndReplaceCreditCard(CreditCardReplacementConfirmation replacementConfirmation, ExtendedResponseListener<CreditCardReplacementConfirmation> cardReplacementValidationListener);
    void confirmStopAndReplaceCreditCard(CreditCardReplacementConfirmation replacementConfirmation, ExtendedResponseListener<CreditCardReplacementConfirmation> cardReplacementConfirmationListener);
    void overdraftOfferRequest(ExtendedResponseListener<CreditCardOverdraft> responseListener);
    void fetchCreditCardProtectionPolicyQuote(String creditCardNumber, ExtendedResponseListener<CreditProtectionQuote> responseListener);
    void submitCreditCardProtectionApplication(String creditCardNumber, ExtendedResponseListener<CreditProtectionQuote> fetchPolicyQuoteResponseListener);
    void fetchClientBureauData(VCLParcelableModel dataModel, ExtendedResponseListener<CreditCardBureauData> responseListener);
    void requestCreditLimitIncrease(VCLParcelableModel requestModel, ExtendedResponseListener<CreditLimitApplicationResult> responseListener);
    void requestVCLOverdraftSnooze(String snoozeOptionValue, boolean isCreditCardVCLSnooze, ExtendedResponseListener<OverdraftSnooze> snoozeResponseListener);
    void setFirstCall(boolean firstCall);
    void requestCreditCardHub(String cardNumber, String fromDate, String toDate, ExtendedResponseListener<CreditCardResponseObject> responseListener);
    void applyForCreditCardHotLead(String cellphoneNumber, ExtendedResponseListener<CreditCardHotLeadApplicationResponse> responseListener);
}