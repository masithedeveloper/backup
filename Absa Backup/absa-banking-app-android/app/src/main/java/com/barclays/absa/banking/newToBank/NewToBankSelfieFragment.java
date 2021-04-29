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

package com.barclays.absa.banking.newToBank;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankTakeSelfieFragmentBinding;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.google.android.gms.vision.face.Face;
import com.thisisme.sdk.fragments.PhotoVerificationFragment;
import com.thisisme.sdk.fragments.PhotoVerificationFragmentEventListener;
import com.thisisme.sdk.gestures.Gesture;
import com.thisisme.sdk.gestures.NeutralExpression;
import com.thisisme.sdk.gestures.Smile;
import com.thisisme.sdk.gestures.TiltLeft;
import com.thisisme.sdk.gestures.TiltRight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NewToBankSelfieFragment extends Fragment {

    private static final int ERROR_SIGNIFICANT_MOVEMENT = 4;
    private static final int ERROR_FACE_OUT_SCREEN = 3;

    private static final String TILT_LEFT = "TiltLeft";
    private static final String TILT_RIGHT = "TiltRight";
    private static final String LEFT_EYE_CLOSED = "LeftEyeClosed";
    private static final String RIGHT_EYE_CLOSED = "RightEyeClosed";
    private static final String SMILE = "Smile";

    private PhotoVerificationFragmentEventListener photoVerificationFragmentEventListener;
    private NewToBankTakeSelfieFragmentBinding binding;
    private NewToBankView newToBankView;
    private PhotoVerificationFragment mPhotoVerificationFragment;
    private List<Gesture> mGestures;
    private List<String> mGesturesText;
    private int mCurrentGesture = 0;
    private Bitmap neutralPhoto;
    private Context context;

    private int errorCount = 0;
    private int captureCount = 0;
    private boolean isScanningDisabled;
    private boolean isScanComplete;

    public NewToBankSelfieFragment() {
        // Default - Empty constructor
    }

    public static NewToBankSelfieFragment newInstance() {
        return new NewToBankSelfieFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.new_to_bank_take_selfie_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        if (newToBankView != null) {
            newToBankView.hideToolbar();
        }
        wireUpComponents();
        generateGestures();
        setUpComponentListeners();
        delayedGesture(4000);
    }

    private boolean isFemale(String idNumber) {
        String substring = idNumber.substring(6, 10);
        int genderValue = Integer.parseInt(substring);
        return genderValue < 5000;
    }

    private void wireUpComponents() {
        mPhotoVerificationFragment = new PhotoVerificationFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.capture_view, mPhotoVerificationFragment);
        ft.addToBackStack(null);
        ft.commit();
        setFaceType();
    }

    private void setFaceType() {
        context = getContext();
        String idNumber = "";

        if (context == null || newToBankView.getNewToBankTempData().getCustomerDetails() == null) {
            showGenericErrorAndRestartProcess();
        } else {
            idNumber = newToBankView.getNewToBankTempData().getCustomerDetails().getIdNumber();
        }

        if (isFemale(idNumber)) {
            binding.faceImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_face_female));

            ConstraintSet set = new ConstraintSet();
            set.clone(binding.faceConstraintLayout);

            constraintToParent(set, binding.leftEyeImageView.getId());
            constraintToParent(set, binding.rightEyeImageView.getId());
            constraintToParent(set, binding.smileImageView.getId());

            set.setHorizontalBias(binding.leftEyeImageView.getId(), 0.31f);
            set.setHorizontalBias(binding.rightEyeImageView.getId(), 0.65f);
            set.setVerticalBias(binding.leftEyeImageView.getId(), 0.55f);
            set.setVerticalBias(binding.rightEyeImageView.getId(), 0.55f);
            set.setVerticalBias(binding.smileImageView.getId(), 0.87f);
            set.applyTo(binding.faceConstraintLayout);
        }
    }

    private void showGenericErrorAndRestartProcess() {
        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_SelfieScreen_ScreenDisplayed");
        } else {
            newToBankView.trackFragmentAction(NewToBankConstants.SELFIE_SCREEN, NewToBankConstants.SELFIE_FAILED);
        }

        newToBankView.showMessageError(AppConstants.GENERIC_ERROR_MSG);
        newToBankView.navigateToWelcomeActivity();
    }

    private void constraintToParent(ConstraintSet set, int viewID) {
        set.connect(viewID, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(viewID, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.connect(viewID, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(viewID, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
    }

    private void setUpComponentListeners() {

        photoVerificationFragmentEventListener = new PhotoVerificationFragmentEventListener() {
            @Override
            public void onReady() {
                super.onReady();
            }

            @Override
            public void onTimeoutProgress(int seconds) {
                if (binding.hintTextView.getText().toString().equalsIgnoreCase(getString(R.string.new_to_bank_tim_dont_smile)) && seconds > 5) {
                    binding.hintTextView.setText(getString(R.string.new_to_bank_tim_lift_chin));
                    final Animation anim = AnimationUtils.loadAnimation(getContext(), za.co.absa.presentation.uilib.R.anim.scale_in_out);
                    binding.hintTextView.startAnimation(anim);
                }
            }

            @Override
            public void onCaptureComplete(Gesture gesture, Bitmap photo) {
                if (isScanningDisabled) {
                    return;
                }

                animatePhotoTaken();

                if (gesture instanceof NeutralExpression) {
                    neutralPhoto = photo;
                }
                if (mCurrentGesture < mGestures.size() - 1) {
                    mCurrentGesture++;

                    if (mCurrentGesture == mGestures.size() - 1) {
                        binding.hintTextView.setText(mGesturesText.get(mCurrentGesture));
                        delayedGesture(2000);
                    } else {
                        demandGesture(mCurrentGesture);
                    }
                } else {
                    binding.faceConstraintLayout.setVisibility(View.GONE);
                    binding.filterView.setVisibility(View.GONE);
                    binding.fragmentContainerLinearLayout.setVisibility(View.GONE);
                    binding.hintTextView.setVisibility(View.GONE);
                    NewToBankView newToBankView = (NewToBankView) getActivity();
                    if (newToBankView != null) {
                        isScanComplete = true;
                        newToBankView.navigateToProcessingFragment();
                        newToBankView.uploadSelfiePhoto(neutralPhoto);
                        releaseFragment();
                    }
                }
            }

            @Override
            public void onCameraCapture(Bitmap photo) {
                if (isScanningDisabled) {
                    return;
                }

                captureCount++;
                setAnalyticsData(captureCount);
                super.onCameraCapture(photo);
                clearAllAnimations();
                mPhotoVerificationFragment.disableCaptureTimeout();
            }

            @Override
            public void onNeedsPermission() {

            }

            @Override
            public void onVerificationFailed(int reason) {
                if (isScanComplete || isScanningDisabled) {
                    return;
                }

                if (errorCount > 2) {
                    releaseFragment();
                    if (newToBankView.isStudentFlow()) {
                        newToBankView.trackStudentAccount("StudentAccount_SelfieScreen_SelfiesFailedError");
                    } else {
                        newToBankView.trackCurrentFragment(NewToBankConstants.SELFIE_NOT_CLEAR_FAILED_ERROR);
                    }
                    showGenericError();
                    return;
                }
                restartProcess(reason);
                BMBLogger.d("FAILURE", String.valueOf(reason));
                if (newToBankView.isStudentFlow()) {
                    newToBankView.trackStudentAccount("StudentAccount_SelfieScreen_SelfiesFailedRetryError");
                } else {
                    newToBankView.trackCurrentFragment(NewToBankConstants.SELFIE_NOT_CLEAR_RETRY_ERROR);
                }
                super.onVerificationFailed(reason);
            }
        };

        mPhotoVerificationFragment.setEventListener(photoVerificationFragmentEventListener);
    }

    private void animatePhotoTaken() {
        binding.filterView.animate().alpha(1).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.filterView.animate().alpha(0.35f).setDuration(100).setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void setAnalyticsData(int captureCount) {
        newToBankView.trackFragmentAction(NewToBankConstants.SELFIE_SCREEN, NewToBankConstants.SELFIE_PHOTO.concat(String.valueOf(captureCount)));
    }

    private void clearAllAnimations() {
        clearAnimations(binding.faceConstraintLayout);
        clearAnimations(binding.leftEyeImageView);
        clearAnimations(binding.rightEyeImageView);
        clearAnimations(binding.smileImageView);
    }

    private void releaseFragment() {
        if (getActivity() != null && mPhotoVerificationFragment != null) {
            getChildFragmentManager().popBackStack();
            mPhotoVerificationFragment.setEventListener(null);
            mPhotoVerificationFragment = null;
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void showGenericError() {
        newToBankView.navigateToGenericResultFragment(getString(R.string.new_to_bank_selfie_process_restart),
                ResultAnimations.generalFailure, getString(R.string.new_to_bank_generic_header_unable_to_continue), true, getString(R.string.close), v -> {
                    newToBankView.navigateBack();
                }, false);
    }

    private void restartProcess(int reason) {
        captureCount = 0;
        errorCount++;
        isScanningDisabled = true;
        animationHintDown();
        binding.hintTextView.setText(R.string.new_to_bank_lets_try_that_again);
        binding.faceConstraintLayout.animate().alpha(0).setDuration(400).setListener(null);
        clearAllAnimations();
        generateGestures();
        setFaceType();

        String errorToDisplay = "";

        if (reason == ERROR_SIGNIFICANT_MOVEMENT) {
            errorToDisplay = getString(R.string.new_to_bank_movement_failure);
        } else if (reason == ERROR_FACE_OUT_SCREEN) {
            errorToDisplay = getString(R.string.new_to_bank_selfie_failure);
            if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_SelfieScreen_HeadOutOfFrameError");
            } else {
                newToBankView.trackFragmentAction(NewToBankConstants.SELFIE_SCREEN, NewToBankConstants.SELFIE_HEAD_OUT_OF_FRAME_ERROR);
            }
        }

        newToBankView.showMessage(getString(R.string.new_to_bank_please_try_again), errorToDisplay, (dialog, which) -> delayedGesture(1000));
    }

    private void clearAnimations(View view) {
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
        }
    }

    private void animationHintUp() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.parentConstraintLayout);
        constraintSet.clear(binding.hintTextView.getId(), ConstraintSet.BOTTOM);

        applyHintAnimation(constraintSet);
    }

    private void animationHintDown() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.parentConstraintLayout);
        constraintSet.connect(binding.hintTextView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        applyHintAnimation(constraintSet);
    }

    private void applyHintAnimation(ConstraintSet constraintSet) {
        Transition transition = new ChangeBounds();
        transition.setDuration(800);
        TransitionManager.beginDelayedTransition(binding.parentConstraintLayout, transition);

        constraintSet.applyTo(binding.parentConstraintLayout);
    }

    private void delayedGesture(int milliseconds) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            animationHintUp();
            binding.faceConstraintLayout.animate().alpha(1).setDuration(800).setListener(null);

            demandGesture(mCurrentGesture);
            mPhotoVerificationFragment.enableSignificantMotionDetection();
            mPhotoVerificationFragment.enableTrackingTimeout(2);
            isScanningDisabled = false;
        }, milliseconds);
    }

    private void generateGestures() {
        mGestures = new ArrayList<>();
        mGesturesText = new ArrayList<>();
        mCurrentGesture = 0;

        mGestures.add(new RightEyeClosed());
        mGestures.add(new LeftEyeClosed());
        TiltLeft tiltLeft = new TiltLeft();
        TiltRight tiltRight = new TiltRight();
        tiltLeft.setIdentifier(TILT_LEFT);
        tiltRight.setIdentifier(TILT_RIGHT);
        mGestures.add(tiltLeft);
        mGestures.add(tiltRight);
        mGestures.add(new Smile());

        mGesturesText = new ArrayList<>();
        mGesturesText.add(getString(R.string.new_to_bank_tim_right_eye_closed));
        mGesturesText.add(getString(R.string.new_to_bank_tim_left_eye_closed));
        mGesturesText.add(getString(R.string.new_to_bank_tim_tilt_head_left));
        mGesturesText.add(getString(R.string.new_to_bank_tim_tilt_head_right));
        mGesturesText.add(getString(R.string.new_to_bank_tim_smile));

        shuffleGestures();

        mGestures.remove(0);
        mGesturesText.remove(0);
        mGestures.remove(0);
        mGesturesText.remove(0);

        mGestures.add(new NeutralExpression());
        mGesturesText.add(getString(R.string.new_to_bank_tim_dont_smile));

        shuffleGestures();
    }

    private void shuffleGestures() {
        long seed = System.nanoTime();
        Collections.shuffle(mGestures, new Random(seed));
        Collections.shuffle(mGesturesText, new Random(seed));
    }

    private void demandGesture(int i) {
        binding.hintTextView.setText(mGesturesText.get(i));
        context = getContext();
        if (mPhotoVerificationFragment == null || context == null) {
            showGenericErrorAndRestartProcess();
            return;
        }
        mPhotoVerificationFragment.demandGesture(mGestures.get(i));
        binding.rightEyeImageView.setVisibility(View.VISIBLE);
        binding.leftEyeImageView.setVisibility(View.VISIBLE);
        binding.faceConstraintLayout.setVisibility(View.VISIBLE);

        binding.smileImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_neutral));

        switch (mGestures.get(i).getIdentifier()) {
            case TILT_LEFT:
                binding.faceConstraintLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.head_tilt_left));
                break;
            case TILT_RIGHT:
                binding.faceConstraintLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.head_tilt_right));
                break;
            case LEFT_EYE_CLOSED:
                binding.leftEyeImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.eye_close));
                break;
            case RIGHT_EYE_CLOSED:
                binding.rightEyeImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.eye_close));
                break;
            case SMILE:
                binding.smileImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_smile));
                binding.smileImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mouth_open));
                break;
        }
    }

    public class LeftEyeClosed extends Gesture {
        LeftEyeClosed() {
            this.mIdentifier = LEFT_EYE_CLOSED;
        }

        public boolean verify(Face face) {
            return (double) face.getIsLeftEyeOpenProbability() != -1 && face.getIsLeftEyeOpenProbability() < 0.3D;
        }
    }

    public class RightEyeClosed extends Gesture {
        RightEyeClosed() {
            this.mIdentifier = RIGHT_EYE_CLOSED;
        }

        public boolean verify(Face face) {
            return (double) face.getIsRightEyeOpenProbability() != -1 && face.getIsRightEyeOpenProbability() < 0.3D;
        }
    }
}