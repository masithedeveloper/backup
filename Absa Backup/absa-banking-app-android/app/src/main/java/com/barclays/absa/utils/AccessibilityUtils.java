package com.barclays.absa.utils;

import android.os.Handler;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.shared.widget.CountDownCircularView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import styleguide.forms.NormalInputView;
import styleguide.forms.StringItem;

public class AccessibilityUtils {

    public static boolean isExploreByTouchEnabled() {
        return BaseActivity.isAccessibilityEnabled();
    }

    public static void setupViewpagerAccessibility(final ArrayList<String> accessibilityDescriptions, ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.requestFocus();
                viewPager.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                viewPager.announceForAccessibility(accessibilityDescriptions.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public static void announceTextFromGenericView(@NonNull View view, String textToAnnounce) {
        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        view.announceForAccessibility(textToAnnounce);
    }

    public static void announceRandValueTextFromView(@NonNull TextView textView) {
        textView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        textView.announceForAccessibility(getTalkBackRandValueFromTextView(textView));
    }

    public static void announceGenericResult(@NonNull TextView primaryNotice, @NonNull TextView secondaryNotice) {
        primaryNotice.announceForAccessibility(primaryNotice.getText().toString());
        Runnable runnable = () -> {
            secondaryNotice.requestFocus();
            secondaryNotice.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            secondaryNotice.announceForAccessibility(getTalkBackRandValueFromString(secondaryNotice.getText().toString()));
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);
    }

    public static void announceTimerInfo(@NonNull CountDownCircularView countDownCircularView, int secondsLeft) {
        countDownCircularView.requestFocus();
        countDownCircularView.announceForAccessibility(String.valueOf(secondsLeft));
        if (secondsLeft == 0) {
            countDownCircularView.clearFocus();
        }
    }

    public static String getTalkBackRandValueFromTextView(@NonNull TextView textWidget) {
        String collectedText = textWidget.getText().toString();
        if (collectedText.length() > 0) {
            return getTalkBackRandValueFromString(collectedText);
        }
        return textWidget.getText().toString();
    }

    public static String[] splitAccountNumberFromName(String accountInfo) {

        if (accountInfo.contains("(")) {
            return accountInfo.split(Pattern.quote("("));
        }
        return new String[0];
    }

    public static String getTalkBackRandValueFromString(String randValue) {

        if (randValue.length() > 0) {
            if (randValue.contains(".") && randValue.contains("R")) {
                String[] amountText = randValue.split(Pattern.quote("."));
                return amountText.length > 0 ? amountText[0].replace("R", "").concat(" Rand and") : "";

            } else if (!randValue.contains(".") && randValue.contains("R")) {
                return randValue.replace("R", "").concat(" Rand");

            } else if (randValue.contains("e.g")) {
                return randValue.replace("e.g", ", for example,");
            }
        }
        return randValue;
    }

    public static String getRandFromStringItem(StringItem item) {

        String contentDescription = "";
        if (item.getDisplayValue() != null) {
            contentDescription = getTalkBackAccountNumberFromString(item.getDisplayValue());
        }
        return contentDescription;
    }

    public static String getTalkbackPinNumberFromString(String preformattedPin) {
        if (preformattedPin.length() > 0 && !preformattedPin.contains("R") && !preformattedPin.contains(".")) {
            return preformattedPin.replace("", ",");
        }
        return preformattedPin;
    }

    public static String getTalkBackAccountNumberFromString(String accountNumber) {
        if (accountNumber != null && accountNumber.length() > 0) {
            return accountNumber.replace("", ",");
        }
        return accountNumber;
    }

    public static String[] getSplitAccountInfo(String accountInformation) {
        if (accountInformation.length() > 0) {
            if (accountInformation.contains(" | ")) {
                String[] info = accountInformation.split(Pattern.quote(" | "));
                String name = info.length > 0 ? info[0] : "";
                String number = info.length > 1 ? getTalkBackAccountNumberFromString(info[1]) : "";
                return new String[]{name, number};
            } else {
                return new String[]{accountInformation};
            }
        } else {
            return new String[]{accountInformation};
        }
    }

    public static void announceErrorFromTextWidget(@Nullable TextView textView) {
        if (textView != null && textView.getText().toString().length() > 0) {
            textView.requestFocus();
            textView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            textView.announceForAccessibility(getTalkBackRandValueFromString(textView.getText().toString()));
        }
    }

    public static void setPostEditedViewContentDescription(@NonNull NormalInputView normalInputView, String description) {
        normalInputView.setContentDescription(description);
        normalInputView.setEditTextContentDescription(description);
    }

}