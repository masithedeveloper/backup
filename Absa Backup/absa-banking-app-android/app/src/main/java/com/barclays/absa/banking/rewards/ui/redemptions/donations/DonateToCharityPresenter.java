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

package com.barclays.absa.banking.rewards.ui.redemptions.donations;

import android.text.TextUtils;

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedemptionCharity;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.rewards.ui.redemptions.RedeemRewardsContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DonateToCharityPresenter implements RedeemRewardsContract.DonateToCharityPresenter {
    private RedeemRewardsContract.DonateToCharityView view;
    private WeakReference<RedeemRewardsContract.DonateToCharityView> weakReferenceView;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    DonateToCharityPresenter(RedeemRewardsContract.DonateToCharityView view) {
        weakReferenceView = new WeakReference<>(view);
    }

    @Override
    public void fetchListOfCharities() {
        view = weakReferenceView.get();
        RedeemRewards redeemRewards = rewardsCacheService.getRewardsRedemption();
        if (redeemRewards != null) {
            updateRewardsData(redeemRewards);
        }
    }

    private void updateRewardsData(RedeemRewards redeemRewards) {
        List<CharityDataModel> charities = new ArrayList<>();
        List<RewardsRedemptionCharity> rewardsCharities = redeemRewards.getCharityList();
        if (redeemRewards.getCharityList() != null) {
            for (RewardsRedemptionCharity rewardsItem : rewardsCharities) {
                CharityDataModel charityModel = new CharityDataModel();
                if (rewardsItem != null && !TextUtils.isEmpty(rewardsItem.getCharityName())) {
                    charityModel.setCharity(rewardsItem);
                    charityModel.setCharityName(rewardsItem.getCharityName());
                    charityModel.setCharityId(rewardsItem.getCharityId());
                    charities.add(charityModel);
                }
            }
        }
        view.updateListOfCharities(charities);
    }
}
