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

package com.barclays.absa.banking.presentation.genericResult;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;

import com.airbnb.lottie.LottieAnimationView;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.Activity2faGenericResultBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.MediaPlayerHelper;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.presentation.launch.SplashActivity;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import styleguide.utils.TextFormattingUtils;

public class GenericResultActivity extends BaseActivity {

    public static final String IMAGE_RESOURCE_ID = "image_resource_id";
    public static final String NOTICE_MESSAGE = "notice_message";
    public static final String NOTICE_MESSAGE_STRING = "notice_message_string";
    public static final String SUB_MESSAGE = "instruction_message";
    public static final String SUB_MESSAGE_STRING = "string_response_message";
    public static final String IS_NOMINATE_PRIMARY = "is_nominate_primary";
    public static final String TOP_BUTTON_MESSAGE = "top_button_message";
    public static final String BOTTOM_BUTTON_MESSAGE = "bottom_button_message";
    public static final String BOTTOM_BUTTON_TYPE_SECONDARY = "bottomButtonTypeSecondary";
    public static final String CALL_US_CONTACT_NUMBER = "call_us_contact_number";
    public static final String SHOULD_QUIT = "should_quit";
    public static final String DEVICE_NICKNAME = "deviceNickname";
    public static final String IS_CALL_SUPPORT_GONE = "is_call_support_gone";
    public static final String BOTTOM_BUTTON_MESSAGE_WHITE = "bottom_button_message_white";
    public static final String HEADER_MESSAGE_FONT_SIZE = "font_size";
    public static final String SUB_MESSAGE_FONT_SIZE = "sub_message_font_size";
    public static final String SHOULD_MAKE_CONTACT_CLICKABLE = "make_contact_clickable";
    public static final String IS_SUCCESS = "isSuccess";
    public static final String IS_FAILURE = "isFailure";
    public static final String IS_GENERAL_ALERT = "isGeneralAlert";
    public static final String IS_PAYMENT_SUCCESS = "isPaymentSuccess";
    public static final String IS_ERROR = "isError";
    public static final String IS_WAITING_STATE = "isWaiting";
    public static final String TOP_BUTTON_SHOULD_RETURN_TO_PREVIOUS_SCREEN = "topButtonShouldReturnToPreviousScreen";
    public static final String CUSTOM_FAILURE_ANIMATION = "customFailureScreen";
    public static final String CLICKABLE_TEXT = "clickableText";
    public static final String CLICKABLE_TYPE = "clickableType";
    public static View.OnClickListener topOnClickListener;
    public static View.OnClickListener bottomOnClickListener;
    Activity2faGenericResultBinding binding;
    private int secondsLeft = 3;

    private ClickableSpan performActionUponTextClicked = new ClickableSpan() {
        @Override
        public void onClick(@NotNull View view) {
            Intent openDialerIntent = new Intent(Intent.ACTION_DIAL);
            openDialerIntent.setData(Uri.parse("tel:".concat(getString(R.string.credit_protection_tel_number))));
            startActivity(openDialerIntent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    };

    public static View.OnClickListener getTopOnClickListener() {
        return topOnClickListener;
    }

    public static View.OnClickListener getBottomOnClickListener() {
        return bottomOnClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BMBLogger.d("x-class:", "onCreate.GenericResultActivity");
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_generic_result, null, false);
        setContentView(binding.getRoot());
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setBackground();
        Bundle bundleExtras = getIntent().getExtras();

        if (bundleExtras != null) {
            final boolean isSuccess = bundleExtras.getBoolean(IS_SUCCESS, false);
            final boolean isFailure = bundleExtras.getBoolean(IS_FAILURE, false);
            final boolean isError = bundleExtras.getBoolean(IS_ERROR, false);
            final boolean isWaiting = bundleExtras.getBoolean(IS_WAITING_STATE, false);
            final boolean isGeneralAlert = bundleExtras.getBoolean(IS_GENERAL_ALERT, false);
            final boolean isPaymentSuccess = bundleExtras.getBoolean(IS_PAYMENT_SUCCESS, false);
            final boolean isBottomButtonStyleSecondary = bundleExtras.getBoolean(BOTTOM_BUTTON_TYPE_SECONDARY, false);
            int imageResourceId = bundleExtras.getInt(IMAGE_RESOURCE_ID, -1);
            final int noticeMessage = bundleExtras.getInt(NOTICE_MESSAGE, -1);
            final int headerMessageFontSize = bundleExtras.getInt(HEADER_MESSAGE_FONT_SIZE, 16);
            final int subMessageFontSize = bundleExtras.getInt(SUB_MESSAGE_FONT_SIZE, 14);
            final int instructionMessage = bundleExtras.getInt(SUB_MESSAGE, -1);
            final int buttonTopMessage = bundleExtras.getInt(TOP_BUTTON_MESSAGE, -1);
            final int buttonBottomMessage = bundleExtras.getInt(BOTTOM_BUTTON_MESSAGE, -1);
            final ClickableType clickableSpanType = (ClickableType) bundleExtras.getSerializable(CLICKABLE_TYPE);
            final String deviceNickName = bundleExtras.getString(DEVICE_NICKNAME, "");
            final String clickableText = bundleExtras.getString(CLICKABLE_TEXT, "");
            final boolean bottomButtonColorSetToWhite = bundleExtras.getBoolean(BOTTOM_BUTTON_MESSAGE_WHITE, false);
            final boolean shouldMakeContactNumberClickable = bundleExtras.getBoolean(SHOULD_MAKE_CONTACT_CLICKABLE, false);
            final String customLottieAnimation = bundleExtras.getString(CUSTOM_FAILURE_ANIMATION);
            ImageView resultImageView = findViewById(R.id.resultImageView);

            if (resultImageView instanceof LottieAnimationView) {
                LottieAnimationView lottieAnimationView = (LottieAnimationView) resultImageView;
                if (isPaymentSuccess) {
                    lottieAnimationView.setAnimation(ResultAnimations.paymentSuccess);
                } else if (isSuccess) {
                    lottieAnimationView.setAnimation(ResultAnimations.generalSuccess);
                } else if (isFailure) {
                    lottieAnimationView.setAnimation(ResultAnimations.generalFailure);
                } else if (isError) {
                    lottieAnimationView.setAnimation(ResultAnimations.generalError);
                } else if (isGeneralAlert) {
                    lottieAnimationView.setAnimation(ResultAnimations.generalAlert);
                } else if (isWaiting) {
                    lottieAnimationView.setAnimation(ResultAnimations.blankState);
                }

                lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        MediaPlayerHelper.INSTANCE.playNotificationSound();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(MediaPlayerHelper.INSTANCE::stopNotificationSound, 3000);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        MediaPlayerHelper.INSTANCE.stopNotificationSound();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        MediaPlayerHelper.INSTANCE.stopNotificationSound();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                lottieAnimationView.playAnimation();

            } else {
                if (imageResourceId != -1) {
                    Drawable icon = AppCompatResources.getDrawable(this, imageResourceId);
                    if (icon != null) {
                        resultImageView.setImageDrawable(icon);
                    } else {
                        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), imageResourceId);
                        Drawable imageDrawable = new BitmapDrawable(getResources(), iconBitmap);
                        resultImageView.setImageDrawable(imageDrawable);
                        iconBitmap.recycle();
                    }
                }
                if (isSuccess) {
                    resultImageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_success));
                } else if (isFailure) {
                    resultImageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_failure));
                } else if (isError) {
                    resultImageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_error));
                } else if (isGeneralAlert) {
                    resultImageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_alert));
                } else if (isWaiting) {
                    resultImageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_blank_state));
                }
            }

            if (customLottieAnimation != null && !customLottieAnimation.isEmpty()) {
                if (resultImageView instanceof LottieAnimationView) {
                    LottieAnimationView lottieAnimationView = (LottieAnimationView) resultImageView;
                    lottieAnimationView.setAnimation(customLottieAnimation);
                }
            }
            if (getIntent().getBooleanExtra(SHOULD_QUIT, false)) {
                binding.bottomActionButton.setOnClickListener(v -> {
                    Intent quitIntent = new Intent(GenericResultActivity.this, SplashActivity.class);
                    quitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    quitIntent.putExtra(SHOULD_QUIT, true);
                    startActivity(quitIntent);
                });
            } else {
                if (getBottomOnClickListener() != null)
                    binding.bottomActionButton.setOnClickListener(getBottomOnClickListener());
            }

            if (deviceNickName != null && !deviceNickName.isEmpty()) {
                binding.noticeMessageTextView.setText(getString(R.string.this_is_now_your_surecheck_2_0_device_nominated, deviceNickName));
            } else {
                String header = bundleExtras.getString(NOTICE_MESSAGE_STRING, null);
                if (header != null) {
                    binding.noticeMessageTextView.setText(header);
                    binding.noticeMessageTextView.setVisibility(View.VISIBLE);
                } else if (noticeMessage != -1) {
                    binding.noticeMessageTextView.setText(noticeMessage);
                    binding.noticeMessageTextView.setVisibility(View.VISIBLE);
                }
            }

            String message_string = bundleExtras.getString(SUB_MESSAGE_STRING, "");
            if (!message_string.isEmpty()) {
                binding.subMessageTextView.setText(message_string);
                binding.subMessageTextView.setVisibility(View.VISIBLE);
                binding.subMessageTextView.setTextSize(subMessageFontSize);
            } else if (instructionMessage != -1) {
                binding.subMessageTextView.setText(instructionMessage);
                binding.subMessageTextView.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(clickableText)) {
                TextFormattingUtils.INSTANCE.makeTextClickable(binding.subMessageTextView, R.color.grey, message_string, clickableText, new ClickableSpan() {
                    @Override
                    public void onClick(@NotNull View widget) {
                        if (clickableSpanType == ClickableType.URL) {
                            String url;
                            if (!clickableText.startsWith("http://") && !clickableText.startsWith("https://")) {
                                url = "http://" + clickableText;
                                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(openUrlIntent);
                            }
                        } else if (clickableSpanType == ClickableType.CONTACT_NUMBER) {
                            Intent openDialerIntent = new Intent(Intent.ACTION_DIAL);
                            openDialerIntent.setData(Uri.parse("tel:".concat(clickableText)));
                            startActivity(openDialerIntent);
                        }
                    }
                });
            }

            if (shouldMakeContactNumberClickable) {
                CommonUtils.makeTextClickable(this, R.string.credit_protection_unsuccessful_sub_message,
                        getString(R.string.credit_protection_tel_number), performActionUponTextClicked, binding.subMessageTextView);
            }

            // fix null pointer crash
            if (isOperator() && getAppCacheService().isLinkingFlow()) {
                binding.bottomMessageTextView.setVisibility(View.VISIBLE);
            }

            // 1st Bottom Button
            if (buttonTopMessage != -1) {
                if (getTopOnClickListener() != null) {
                    binding.topActionButton.setOnClickListener(getTopOnClickListener());
                } else if (bundleExtras.getBoolean(TOP_BUTTON_SHOULD_RETURN_TO_PREVIOUS_SCREEN)) {
                    binding.topActionButton.setOnClickListener(v -> finish());
                }
                binding.topActionButton.setText(buttonTopMessage);
                binding.topActionButton.setContentDescription(getResources().getString(buttonTopMessage));
                binding.topActionButton.setVisibility(View.VISIBLE);
            } else
                binding.topActionButton.setVisibility(View.GONE);

            // 2nd Bottom Button
            if (buttonBottomMessage != -1) {
                if (getBottomOnClickListener() != null)
                    binding.bottomActionButton.setOnClickListener(getBottomOnClickListener());
                binding.bottomActionButton.setText(buttonBottomMessage);
                binding.bottomActionButton.setContentDescription(getResources().getString(buttonBottomMessage));
                binding.bottomActionButton.setVisibility(View.VISIBLE);
            } else {
                binding.bottomActionButton.setVisibility(View.GONE);
            }

            if (bottomButtonColorSetToWhite) {
                binding.bottomActionButton.setBackgroundResource(R.drawable.button_rounded_white);
                binding.bottomActionButton.setTextColor(R.color.color_FF000000);
            }
            if (isBottomButtonStyleSecondary && buttonBottomMessage != -1) {
                if (getBottomOnClickListener() != null) {
                    binding.bottomActionButtonSecondary.setOnClickListener(getBottomOnClickListener());
                    binding.bottomActionButtonSecondary.setText(buttonBottomMessage);
                    binding.bottomActionButtonSecondary.setContentDescription(getResources().getString(buttonBottomMessage));
                    binding.bottomActionButton.setVisibility(View.GONE);
                    binding.bottomActionButtonSecondary.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomActionButtonSecondary.setVisibility(View.GONE);
                }
            }

            if (isAccessibilityEnabled()) {
                if (binding.noticeMessageTextView.getText().length() > 0) {
                    AccessibilityUtils.announceRandValueTextFromView(binding.noticeMessageTextView);
                } else if (binding.subMessageTextView.getText().length() > 0) {
                    AccessibilityUtils.announceRandValueTextFromView(binding.subMessageTextView);
                } else {
                    AccessibilityUtils.announceGenericResult(binding.noticeMessageTextView, binding.subMessageTextView);
                }
            }
        }

        if (isOperator() && getAppCacheService().isLinkingFlow()) {
            Timer loginTimer = new Timer();
            TimerTask loginTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if (secondsLeft != 0) {
                            --secondsLeft;
                        } else {
                            cancel();
                            navigateToHomeScreenWithoutReloadingAccounts();
                        }
                    });
                }
            };
            loginTimer.scheduleAtFixedRate(loginTask, 0, 2000);
        }

    }

    public enum ClickableType {
        URL,
        CONTACT_NUMBER
    }

    @Override
    public void navigateToHomeScreenWithoutReloadingAccounts() {
        Intent homePageIntent = new Intent(GenericResultActivity.this, HomeContainerActivity.class);
        homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePageIntent);
        finish();
    }

    private void setBackground() {
        binding.contentConstraintLayout
                .animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start();
    }

    @Override
    public void onBackPressed() {
        if (getAppCacheService().isChangePrimaryDeviceFromNoPrimaryDeviceScreen()) {
            super.onBackPressed();
        }
    }
}