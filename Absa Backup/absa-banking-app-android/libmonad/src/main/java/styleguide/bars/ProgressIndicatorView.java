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

package styleguide.bars;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class ProgressIndicatorView extends ConstraintLayout {

    private int stepSize = 1;
    private int currentStep = 0;
    private int previousStep = 0;
    private int nextStep = 0;
    private int steps = -1;
    private String stepText = "";
    private final int PROGRESS_INDICATOR_DURATION_MILLISECONDS = 1400;

    private ConstraintSet constraintSet;
    private TextView stepCountTextView;

    private boolean isAnimationDisabled = false;

    public ProgressIndicatorView(Context context) {
        super(context);
        initView(context);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setSteps(context, attrs);
        setUpConstraints();
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.progress_indicator_view, this);
        stepCountTextView = findViewById(R.id.stepCountTextView);
    }

    private void setUpConstraints() {
        constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        constraintSet.setGuidelinePercent(R.id.guideline, (float) previousStep / steps);
        constraintSet.applyTo(this);
    }

    public int getPreviousStep() {
        return previousStep;
    }

    public void setPreviousStep(int previousStep) {
        this.previousStep = previousStep;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void animateNextStep() {
        setNextStep(nextStep);
    }

    public void goBackStep() {
        nextStep--;
        setNextStep(nextStep);
    }

    public void setNextStepWithIncrement() {
        nextStep++;
        setNextStep(nextStep);
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void nextStep() {
        currentStep = currentStep + stepSize;
        setNextStep(currentStep);
    }

    public void setNextStepNoAnimation(int nextStep) {
        isAnimationDisabled = true;
        setNextStep(nextStep);
    }

    public void setNextStep(int nextStep) {
        if (nextStep < 0) {
            return; // beyond (begin or last step)
        } else if (nextStep > steps) {
            nextStep = steps;
        }

        this.nextStep = nextStep;
        currentStep = nextStep;

        constraintSet.setGuidelinePercent(R.id.guideline, (float) this.nextStep / steps);
        if (!isAnimationDisabled) {
            Transition transition = new ChangeBounds();
            transition.setDuration(PROGRESS_INDICATOR_DURATION_MILLISECONDS);
            TransitionManager.beginDelayedTransition(this, transition);
        }
        constraintSet.applyTo(this);
        if (stepCountTextView != null) {
            stepCountTextView.setText(String.format(stepText, nextStep, steps));
        }
        isAnimationDisabled = false;
    }

    private void setSteps(Context context, AttributeSet attrs) {
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressIndicatorView);
        previousStep = styledAttributes.getInteger(R.styleable.ProgressIndicatorView_attribute_previous_step, previousStep);
        steps = styledAttributes.getInteger(R.styleable.ProgressIndicatorView_attribute_steps, steps);
        nextStep = styledAttributes.getInteger(R.styleable.ProgressIndicatorView_attribute_next_step, nextStep);
        String alternativeStepText = styledAttributes.getString(R.styleable.ProgressIndicatorView_attribute_alternative_step_text);
        stepText = (alternativeStepText != null) ? alternativeStepText : context.getString(R.string.step_x_of_y);
        currentStep = nextStep;
        if (styledAttributes.getBoolean(R.styleable.ProgressIndicatorView_attribute_show_step_count, false)) {
            if (stepCountTextView != null) {
                stepCountTextView.setVisibility(VISIBLE);
                stepCountTextView.setText(String.format(getContext().getString(R.string.step_x_of_y), nextStep, steps));
            }
        }

        styledAttributes.recycle();
    }
}