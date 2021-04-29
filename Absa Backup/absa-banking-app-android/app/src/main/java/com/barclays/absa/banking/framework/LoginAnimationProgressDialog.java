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
package com.barclays.absa.banking.framework;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.CompatibilityUtils;
import com.barclays.absa.utils.UserSettingsManager;

import java.io.IOException;

public class LoginAnimationProgressDialog extends LottieAnimationProgressDialog {

    private LottieAnimationView animationView;
    private static MediaPlayer mediaPlayer;
    private static boolean isReleased = false;

    public static LoginAnimationProgressDialog newInstance() {
        return new LoginAnimationProgressDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        animationView = (LottieAnimationView) inflater.inflate(R.layout.login_progress_indicator_dialog, container, false);
        return animationView;
    }

    public void onLoginCompleted() {
        if (animationView == null) return;
        final Context context = getContext();
        if (context != null && UserSettingsManager.INSTANCE.isSoundEnabled()) {
            try {
                if (mediaPlayer != null && !isReleased) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                mediaPlayer = new MediaPlayer();
                isReleased = false;

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    stopNotificationSound();
                    return true;
                });

                Uri lockSoundEffectUri = Uri.parse("android.resource://" + BMBApplication.getInstance().getPackageName() + "/" + R.raw.lock_sound_effect);
                mediaPlayer.setDataSource(BMBApplication.getInstance().getApplicationContext(), lockSoundEffectUri);
                if (CompatibilityUtils.isVersionGreaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP)) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
                    mediaPlayer.setAudioAttributes(audioAttributes);
                } else {
                    //noinspection Convert2Lambda
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.release();
                    isReleased = true;
                });
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                //this is timed to sync with the animation*/

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    try {
                        mediaPlayer.prepare();
                    } catch (Exception e) {
                        new MonitoringInteractor().logCaughtExceptionEvent(e);
                    }
                }, 1060);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception es) {
                BMBLogger.e(es.getMessage());
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::stopNotificationSound, 3000);
        }

        animationView.setAnimation("lock_loader_for_login.json");
        animationView.setRepeatMode(LottieDrawable.RESTART);
        animationView.setRepeatCount(0);
        animationView.playAnimation();
    }

    private void stopNotificationSound() {
        try {
            if (mediaPlayer != null && !isReleased) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            new MonitoringInteractor().logCaughtExceptionEvent(e);
        }
    }

}