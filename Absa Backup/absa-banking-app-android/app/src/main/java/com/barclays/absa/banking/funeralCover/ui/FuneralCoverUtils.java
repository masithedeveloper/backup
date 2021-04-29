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
package com.barclays.absa.banking.funeralCover.ui;

import android.content.Context;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.app.BMBApplication;

import java.util.HashMap;

public class FuneralCoverUtils {

    public static HashMap<String, String> getRelationshipDescriptionFromRelationshipCodeMap() {
        Context context = BMBApplication.getInstance();
        HashMap<String, String> relationshipMap = new HashMap<>();
        relationshipMap.put("EBRO", context.getString(R.string.brother));
        relationshipMap.put("DAUG", context.getString(R.string.daughter));
        relationshipMap.put("FAIL", context.getString(R.string.father_in_law));
        relationshipMap.put("MOIL", context.getString(R.string.mother_in_law));
        relationshipMap.put("MOTH", context.getString(R.string.mother_in_law));
        relationshipMap.put("OTHE", context.getString(R.string.other));
        relationshipMap.put("SFAI", context.getString(R.string.step_father));
        relationshipMap.put("ESIS", context.getString(R.string.sister));
        relationshipMap.put("SMOT", context.getString(R.string.step_mother));
        relationshipMap.put("SON", context.getString(R.string.son));
        relationshipMap.put("SPEC", context.getString(R.string.special_child));
        relationshipMap.put("STUD", context.getString(R.string.student));
        relationshipMap.put("UNC", context.getString(R.string.uncle));
        relationshipMap.put("NIEC", context.getString(R.string.niece));
        relationshipMap.put("NEPH", context.getString(R.string.nephew));
        relationshipMap.put("AUNT", context.getString(R.string.aunt));
        relationshipMap.put("COUS", context.getString(R.string.cousin));
        relationshipMap.put("FATH", context.getString(R.string.father));
        return relationshipMap;
    }

    public static HashMap<String, String> getMemberCategoryFromRelationshipCode() {
        Context context = BMBApplication.getInstance();
        HashMap<String, String> relationshipMap = new HashMap<>();
        relationshipMap.put("EBRO", context.getString(R.string.extended_family));
        relationshipMap.put("DAUG", context.getString(R.string.children));
        relationshipMap.put("FAIL", context.getString(R.string.parents_in_laws));
        relationshipMap.put("MOIL", context.getString(R.string.parents_in_laws));
        relationshipMap.put("MOTH", context.getString(R.string.parents_in_laws));
        relationshipMap.put("SFAI", context.getString(R.string.parents_in_laws));
        relationshipMap.put("ESIS", context.getString(R.string.extended_family));
        relationshipMap.put("SMOT", context.getString(R.string.parents_in_laws));
        relationshipMap.put("SON", context.getString(R.string.children));
        relationshipMap.put("SPEC", context.getString(R.string.children));
        relationshipMap.put("STUD", context.getString(R.string.children));
        relationshipMap.put("UNC", context.getString(R.string.extended_family));
        relationshipMap.put("NIEC", context.getString(R.string.extended_family));
        relationshipMap.put("NEPH", context.getString(R.string.extended_family));
        relationshipMap.put("AUNT", context.getString(R.string.extended_family));
        relationshipMap.put("COUS", context.getString(R.string.extended_family));
        relationshipMap.put("FATH", context.getString(R.string.parents_in_laws));
        return relationshipMap;
    }
}
