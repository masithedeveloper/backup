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
package com.barclays.absa.banking.funeralCover.services;

import com.barclays.absa.banking.boundary.model.FuneralCoverQuotes;
import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.RolePlayerDetails;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse;
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper;

public interface FuneralCoverQuoteService {

    String OP0858_FUNERAL_QUOTES = "OP0858";
    String OP0859_COVER_RELATION_APPLY_PLAN = "OP0859";
    String OP0860_GET_RETAIL_ACCOUNTS = "OP0860";
    String OP0862_APPLY_FOR_FUNERAL_PLAN = "OP0862";
    String OP2057_GET_UNIT_TRUST_ACCOUNTS = "OP2057";

    String ACCOUNT_NUMBER = "accountNumber";
    String DAY_OF_DEBIT = "dayOfDebit";
    String FAMILY_SELECTED = "familySelected";
    String SELF_PREMIUM = "selfPremium";
    String SOURCE_OF_FUNDS = "sourceOfFund";
    String SPOUSE_COVER_AMOUNT = "spouseCoverAmount";
    String SPOUSE_PLAN_CODE = "spousePlanCode";
    String SPOUSE_PREMIUM = "spousePremium";
    String TOTAL_COVER_AMOUNT = "totalCoverAmount";
    String TOTAL_PREMIUM = "totalPremium";
    String ACC_TYPE = "accountType";
    String ANNUAL_ESCALATION_OF_PREMIUM = "annualEscalationOfPremium";
    String PLAN_CODE = "planCode";
    String SELF_COVER_AMOUNT = "selfCoverAmount";
    String FAMILY_INITIALS = "familyInitials";
    String FAMILY_SURNAME = "familySurname";
    String FAMILY_GENDER = "familyGender";
    String FAMILY_DATE_OF_BIRTH = "familyDateOfBirth";
    String FAMILY_COVER_AMOUNT = "familyCoverAmount";
    String FAMILY_PREMIUM = "familyPremium";
    String FAMILY_RELATIONSHIP_CODE = "familyRelationShip";
    String FAMILY_BENEFIT_CODE = "familyBenefitCode";
    String GROUP_CODE_KEY = "groupCode";
    String LANGUAGE_CODE_KEY = "language";
    String ACC_TYPE_FILTER = "accountTypeFilter";
    String PAY_TO_TYPE = "payToType";
    String BENEFICIARY_TITLE = "beneficiaryTitle";
    String BENEFICIARY_FIRST_NAME = "beneficiaryFirstName";
    String BENEFICIARY_SURNAME = "beneficiarySurname";
    String BENEFICIARY_DATE_OF_BIRTH = "beneficiaryDateOfBirth";
    String BENEFICIARY_RELATIONSHIP = "beneficiaryRelationship";
    String BENEFICIARY_INITIALS = "beneficiaryInitials";

    void pullFuneralCoverQuotes(ExtendedResponseListener<FuneralCoverQuotes> responseListener);
    void pullFuneralPlanApplication(ExtendedResponseListener<FuneralCoverDetails> responseListener, FuneralCoverDetails funeralCoverDetails);
    void pullCoverAmountForRelationApplyFuneralPlan(ExtendedResponseListener<RolePlayerDetails> responseListener, FuneralCoverDetails funeralCoverDetails, FamilyMemberCoverDetails rolePlayerDetails);
    void fetchRetailAccounts(ExtendedResponseListener<RetailAccountsResponse> extendedResponseListener);
    void fetchUnitTrustAccounts(ExtendedResponseListener<UnitTrustAccountsWrapper> unitTrustAccountExtendedResponseListener);
}
