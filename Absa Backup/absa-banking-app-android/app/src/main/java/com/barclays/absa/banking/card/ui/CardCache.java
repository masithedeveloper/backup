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
package com.barclays.absa.banking.card.ui;

import android.util.LruCache;

import com.barclays.absa.banking.boundary.model.CardAccountList;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;


public class CardCache {
    private static CardCache cache;
    private final int MAXIMUM_CACHE_SIZE = 32;
    private LruCache<String, Object> BACKING_STORE = new LruCache<>(MAXIMUM_CACHE_SIZE);

    private final String VCL_PARCELABLE_MODEL = "vcl_parcelable_model";
    private final String CARD_INDEX_LIST = "card_index_list";

    private CardCache() {
    }

    public static synchronized CardCache getInstance() {
        if (cache == null) {
            cache = new CardCache();
        }
        return cache;
    }

    public VCLParcelableModel retrieveVCLParcelableModel() {
        return (VCLParcelableModel) BACKING_STORE.get(VCL_PARCELABLE_MODEL);
    }

    public void storeVCLParcelableModel(VCLParcelableModel vclDataModel) {
        BACKING_STORE.put(VCL_PARCELABLE_MODEL, vclDataModel);
    }

    public CardAccountList retrieveCardIndex() {
        if (BACKING_STORE.get(CARD_INDEX_LIST) == null) {
            return new CardAccountList();
        }
        return (CardAccountList) BACKING_STORE.get(CARD_INDEX_LIST);
    }

    public void storeRetrieveCardIndex(CardAccountList vclDataModel) {
        BACKING_STORE.put(CARD_INDEX_LIST, vclDataModel);
    }

    public void clear() {
        if (BACKING_STORE.size() > 0) {
            BACKING_STORE.evictAll();
        }
    }
}
