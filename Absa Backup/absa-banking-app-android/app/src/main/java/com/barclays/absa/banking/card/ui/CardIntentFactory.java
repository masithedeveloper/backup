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

package com.barclays.absa.banking.card.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel;
import com.barclays.absa.banking.card.ui.creditCard.hub.CardListActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment;
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.genericResult.DialogGenericResultActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.settings.ui.SettingsCardLimitsActivity;
import com.barclays.absa.utils.AnalyticsUtil;

import static com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.TRAVEL_DATA;

public class CardIntentFactory extends IntentFactory {

    public static String IS_SUCCESS = "isSuccess";

    private CardIntentFactory() {
        throw new AssertionError("CardIntentFactory does not support initialization...");
    }

    public static Intent cardInformationIntent(Activity activity, ManageCardResponseObject.ManageCardItem manageCardItem, GetSecondaryCardMandateResponse secondaryCards) {
        Intent cardInformationIntent = new Intent(activity, CardActivity.class);
        cardInformationIntent.putExtra("CARD_MANAGE_ITEM", (Parcelable) manageCardItem);
        cardInformationIntent.putExtra("SECONDARY_CARDS", secondaryCards);
        cardInformationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return cardInformationIntent;
    }

    public static Intent getPauseCardResultScreen(Activity activity, int header, int message, boolean isFromCardHub, ManageCardResponseObject.ManageCardItem manageCardItem) {
        return getPauseCardResultScreen(activity, header, message, isFromCardHub, manageCardItem, false);
    }

    public static Intent getPauseCardFailureScreen(Activity activity, int header, int message, boolean isFromCardHub, ManageCardResponseObject.ManageCardItem manageCardItem) {
        return getPauseCardResultScreen(activity, header, message, isFromCardHub, manageCardItem, true);
    }

    public static Intent getPauseCardResultScreen(Activity activity, int header, int message, boolean isFromCardHub, ManageCardResponseObject.ManageCardItem manageCardItem, boolean isFailed) {
        IntentBuilder intentBuilder = new IntentBuilder().setClass(activity, GenericResultActivity.class);

        if (isFailed) {
            intentBuilder.setGenericResultIconToFailure();
        } else {
            intentBuilder.setGenericResultIconToSuccessful();
        }

        intentBuilder.setGenericResultHeaderMessage(header)
                .setGenericResultHeaderMessageFontSize()
                .setGenericResultsSubMessageFontSize()
                .setGenericResultSubMessage(message)
                .setIsFromCardHub(isFromCardHub)
                .setGenericResultDoneTopButton(view -> {
                    if (isFromCardHub) {
                        if (manageCardItem != null && manageCardItem.getPauseStates() != null && manageCardItem.getPauseStates().getCardNumber() != null) {
                            Intent cardIntent = new Intent(activity, CreditCardHubActivity.class);
                            cardIntent.putExtra(ACCOUNT_NUMBER, manageCardItem.getPauseStates().getCardNumber());
                            cardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(cardIntent);
                        } else {
                            ((BaseActivity) activity).loadAccountsAndGoHome();
                        }
                    } else {
                        Intent manageCardIntent = new Intent(activity, CardListActivity.class);
                        manageCardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(manageCardIntent);
                    }
                });
        return intentBuilder.build();
    }

    private static Intent genericResultScreen(final Activity activity, int message) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultHeaderMessage(message)
                .setGenericResultBottomButton(R.string.home_button_description, v -> ((BaseActivity) activity).loadAccountsAndGoHome())
                .build();
    }

    public static Intent showCardLimitSuccessResultScreen(Activity activity, String subMessage, ManageCardResponseObject.ManageCardItem cardItem, boolean isCreditCardFlow) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToSuccessful()
                .setGenericResultHeaderMessage(activity.getString(R.string.success_string))
                .setGenericResultSubMessage(subMessage)
                .setGenericResultHomeButtonTop(activity)
                .setGenericResultDoneButton(activity, v -> {
                    if (isCreditCardFlow) {
                        Intent intent = new Intent(activity, CreditCardHubActivity.class);
                        intent.putExtra(ACCOUNT_NUMBER, cardItem.getCardLimit().getCardNumber());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    } else {
                        Intent manageCardIntent = new Intent(activity, CardListActivity.class);
                        manageCardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(manageCardIntent);
                    }
                    AnalyticsUtil.INSTANCE.trackAction("Update card limits", (isCreditCardFlow ? "CreditCardLimit" : "DebitCardLimit") + "_UpdateLimitsSuccessScreen_DoneButtonClicked");
                }, true).build();
    }

    public static Intent showCardLimitFailedResultScreen(Activity activity, String subMessage, ManageCardResponseObject.ManageCardItem cardItem) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(activity.getString(R.string.failureMsg))
                .setGenericResultSubMessage(subMessage)
                .setGenericResultHomeButtonTop(activity)
                .setGenericResultDoneButton(activity, v -> {
                    Intent intent = new Intent(activity, SettingsCardLimitsActivity.class);
                    intent.putExtra(ManageCardFragment.MANAGE_CARD_ITEM, (Parcelable) cardItem);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                }).build();
    }

    public static Intent showTravelDateUpdateFailedResultScreen(Activity activity, String subMessage, TravelUpdateModel cardItem, boolean isCreditCardFlow) {
        return new IntentBuilder()
                .setClass(activity, GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(activity.getString(R.string.travel_abroad_failure_result_message))
                .setGenericResultSubMessage(subMessage)
                .setGenericResultHomeButtonTop(activity)
                .setGenericResultDoneButton(activity, v -> {
                    if (isCreditCardFlow) {
                        Intent intent = new Intent(activity, CreditCardHubActivity.class);
                        intent.putExtra(ACCOUNT_NUMBER, cardItem.getCardNumber());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, CardListActivity.class);
                        intent.putExtra(TRAVEL_DATA, cardItem);
                        intent.putExtra(CreditCardHubActivity.IS_FROM_CARD_HUB, isCreditCardFlow);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    }
                }).build();

    }

    public static void showSurecheckRejectedResultScreen(Activity activity) {
        activity.startActivity(genericResultScreen(activity, R.string.transaction_rejected));
    }

    public static void showSurecheckFailedResultScreen(Activity activity) {
        activity.startActivity(genericResultScreen(activity, R.string.surecheck_failed));
    }

    public static Intent showStopAndReplaceFailureScreen(final Activity activity) {
        Intent failureResultScreen = new Intent(activity, DialogGenericResultActivity.class);
        failureResultScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        failureResultScreen.putExtra("isFailure", true);
        failureResultScreen.putExtra(DialogGenericResultActivity.NOTICE_MESSAGE, R.string.credit_card_replacement_failed);
        failureResultScreen.putExtra(DialogGenericResultActivity.SUB_MESSAGE, R.string.credit_card_replacement_assistance_message);
        failureResultScreen.putExtra(DialogGenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        failureResultScreen.putExtra(DialogGenericResultActivity.TOP_BUTTON_MESSAGE, R.string.call_helpline);
        return failureResultScreen;
    }

    private static Intent buildTravelAbroadResultIntent(final Activity activity, final TravelUpdateModel travelUpdateModel, boolean isFromCreditCardHub, int title, int subMessage) {
        GenericResultActivity.bottomOnClickListener = v -> {
            if (isFromCreditCardHub) {
                Intent intent = new Intent(activity, CreditCardHubActivity.class);
                intent.putExtra(ACCOUNT_NUMBER, travelUpdateModel.getCardNumber());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            } else {
                Intent manageCardIntent = new Intent(activity, CardListActivity.class);
                manageCardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(manageCardIntent);
            }
        };

        Intent intent = new Intent(activity, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_success);
        intent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, title);
        if (subMessage != -1) {
            intent.putExtra(GenericResultActivity.SUB_MESSAGE, subMessage);
        }
        return intent;
    }


    public static Intent buildAcceptResultIntent(final Activity activity, final TravelUpdateModel travelUpdateModel, boolean isFromCreditCardHub) {
        return buildTravelAbroadResultIntent(activity, travelUpdateModel, isFromCreditCardHub, R.string.travel_abroad_success_result_title, R.string.travel_abroad_success_result_message);
    }

    public static Intent buildTravelAbroadCancelResultIntent(final Activity activity, final TravelUpdateModel travelUpdateModel, boolean isFromCreditCardHub) {
        return buildTravelAbroadResultIntent(activity, travelUpdateModel, isFromCreditCardHub, R.string.travel_abroad_success_cancel_result_message, -1);
    }

    public static Intent showStopAndReplaceSuccessScreen(final Activity activity, boolean isCollection) {
        Intent successResultScreen = new Intent(activity, DialogGenericResultActivity.class);
        successResultScreen.putExtra(IS_SUCCESS, true);

        if (isCollection) {
            successResultScreen.putExtra(DialogGenericResultActivity.SUB_MESSAGE, R.string.credit_card_replacement_branch_collection_message);
        } else {
            successResultScreen.putExtra(DialogGenericResultActivity.SUB_MESSAGE, R.string.credit_card_replacement_face_to_face_collection_message);
        }
        successResultScreen.putExtra(DialogGenericResultActivity.NOTICE_MESSAGE, R.string.stop_and_replace_success_message_heading);
        successResultScreen.putExtra(DialogGenericResultActivity.SUB_MESSAGE, R.string.stop_and_replace_success_message_subheading);
        successResultScreen.putExtra(DialogGenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        successResultScreen.putExtra(DialogGenericResultActivity.TOP_BUTTON_MESSAGE, R.string.call_helpline);
        return successResultScreen;
    }

}