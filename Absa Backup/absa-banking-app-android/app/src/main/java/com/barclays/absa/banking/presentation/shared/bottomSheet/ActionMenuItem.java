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
package com.barclays.absa.banking.presentation.shared.bottomSheet;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenuItem;

import org.jetbrains.annotations.NotNull;

class ActionMenuItem implements SupportMenuItem {

    private final int mId;
    private final int mGroup;
    private final int mOrdering;
    private int mFlags = ENABLED;

    private CharSequence mTitle;
    private CharSequence mTitleCondensed;

    private Intent mIntent;
    private Drawable mIconDrawable;
    private final Context context;

    private char mShortcutNumericChar;
    private char mShortcutAlphabeticChar;

    private OnMenuItemClickListener mClickListener;

    private static final int CHECKABLE = 0x00000001;
    private static final int CHECKED = 0x00000002;
    private static final int EXCLUSIVE = 0x00000004;
    private static final int HIDDEN = 0x00000008;
    private static final int ENABLED = 0x00000010;

    ActionMenuItem(Context context, int group, int id, int ordering,
                   CharSequence title) {
        this.context = context;
        mId = id;
        mGroup = group;
        mOrdering = ordering;
        mTitle = title;
    }

    public char getAlphabeticShortcut() {
        return mShortcutAlphabeticChar;
    }

    public int getGroupId() {
        return mGroup;
    }

    public Drawable getIcon() {
        return mIconDrawable;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public int getItemId() {
        return mId;
    }

    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    public char getNumericShortcut() {
        return mShortcutNumericChar;
    }

    public int getOrder() {
        return mOrdering;
    }

    public SubMenu getSubMenu() {
        return null;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public CharSequence getTitleCondensed() {
        return mTitleCondensed != null ? mTitleCondensed : mTitle;
    }

    public boolean hasSubMenu() {
        return false;
    }

    public boolean isCheckable() {
        return (mFlags & CHECKABLE) != 0;
    }

    public boolean isChecked() {
        return (mFlags & CHECKED) != 0;
    }

    public boolean isEnabled() {
        return (mFlags & ENABLED) != 0;
    }

    public boolean isVisible() {
        return (mFlags & HIDDEN) == 0;
    }

    public MenuItem setAlphabeticShortcut(char alphaChar) {
        mShortcutAlphabeticChar = alphaChar;
        return this;
    }

    public MenuItem setCheckable(boolean checkable) {
        mFlags = (mFlags & ~CHECKABLE) | (checkable ? CHECKABLE : 0);
        return this;
    }

    ActionMenuItem setExclusiveCheckable(boolean exclusive) {
        mFlags = (mFlags & ~EXCLUSIVE) | (exclusive ? EXCLUSIVE : 0);
        return this;
    }

    public MenuItem setChecked(boolean checked) {
        mFlags = (mFlags & ~CHECKED) | (checked ? CHECKED : 0);
        return this;
    }

    public MenuItem setEnabled(boolean enabled) {
        mFlags = (mFlags & ~ENABLED) | (enabled ? ENABLED : 0);
        return this;
    }

    public MenuItem setIcon(Drawable icon) {
        mIconDrawable = icon;
        return this;
    }

    public MenuItem setIcon(@DrawableRes int iconRes) {
        if (iconRes != 0) {
            mIconDrawable = ContextCompat.getDrawable(context, iconRes);
        }
        return this;
    }

    public MenuItem setIntent(Intent intent) {
        mIntent = intent;
        return this;
    }

    public MenuItem setNumericShortcut(char numericChar) {
        mShortcutNumericChar = numericChar;
        return this;
    }

    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        mClickListener = menuItemClickListener;
        return this;
    }

    public MenuItem setShortcut(char numericChar, char alphaChar) {
        mShortcutNumericChar = numericChar;
        mShortcutAlphabeticChar = alphaChar;
        return this;
    }

    public MenuItem setTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    public MenuItem setTitle(int title) {
        mTitle = context.getString(title);
        return this;
    }

    public MenuItem setTitleCondensed(CharSequence title) {
        mTitleCondensed = title;
        return this;
    }

    public MenuItem setVisible(boolean visible) {
        mFlags = (mFlags & HIDDEN) | (visible ? 0 : HIDDEN);
        return this;
    }

    boolean invoke() {
        if (mClickListener != null && mClickListener.onMenuItemClick(this)) {
            return true;
        }

        if (mIntent != null) {
            context.startActivity(mIntent);
            return true;
        }

        return false;
    }

    public void setShowAsAction(int show) {
        //ActionMenuItems always show as action buttons.
    }

    public SupportMenuItem setActionView(View actionView) {
        throw new UnsupportedOperationException();
    }

    public View getActionView() {
        return null;
    }

    @Override
    public MenuItem setActionProvider(android.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public android.view.ActionProvider getActionProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SupportMenuItem setActionView(int resId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public androidx.core.view.ActionProvider getSupportActionProvider() {
        return null;
    }

    @Override
    public SupportMenuItem setSupportActionProvider(androidx.core.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SupportMenuItem setShowAsActionFlags(int actionEnum) {
        setShowAsAction(actionEnum);
        return this;
    }

    @Override
    public boolean expandActionView() {
        return false;
    }

    @Override
    public boolean collapseActionView() {
        return false;
    }

    @Override
    public boolean isActionViewExpanded() {
        return false;
    }

    @Override
    public SupportMenuItem setContentDescription(CharSequence contentDescription) {
        return this;
    }

    @Override
    public CharSequence getContentDescription() {
        return null;
    }

    @Override
    public SupportMenuItem setTooltipText(CharSequence tooltipText) {
        return this;
    }

    @Override
    public CharSequence getTooltipText() {
        return null;
    }

    @Override
    public MenuItem setShortcut(char numericChar, char alphaChar, int numericModifiers, int alphaModifiers) {
        return this;
    }

    @Override
    public MenuItem setNumericShortcut(char numericChar, int numericModifiers) {
        mShortcutNumericChar = numericChar;
        return this;
    }

    @Override
    public int getNumericModifiers() {
        return 0;
    }

    @Override
    public MenuItem setAlphabeticShortcut(char alphaChar, int alphaModifiers) {
        return this;
    }

    @Override
    public int getAlphabeticModifiers() {
        return 0;
    }

    @Override
    public MenuItem setIconTintList(ColorStateList tint) {
        return this;
    }

    @Override
    public ColorStateList getIconTintList() {
        return null;
    }

    @NotNull
    @Override
    public MenuItem setIconTintMode(PorterDuff.Mode tintMode) {
        return this;
    }

    @Override
    public PorterDuff.Mode getIconTintMode() {
        return null;
    }

    @Override
    public boolean requiresActionButton() {
        return false;
    }

    @Override
    public boolean requiresOverflow() {
        return false;
    }

    @Override
    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        return this;
    }

   /* @Override
    public SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener listener) {
        // No need to save the listener; ActionMenuItem does not support collapsing items.
        return this;
    }*/
}
