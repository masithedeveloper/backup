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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;

import androidx.core.internal.view.SupportMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ActionMenu implements SupportMenu {
    private static final int[] sCategoryToOrder = new int[]{
            1, /* No category */
            4, /* CONTAINER */
            5, /* SYSTEM */
            3, /* SECONDARY */
            2, /* ALTERNATIVE */
            0, /* SELECTED_ALTERNATIVE */
    };
    private final Context mContext;
    private boolean mIsQwerty;
    private ArrayList<ActionMenuItem> mItems;

    ActionMenu(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    private static int findInsertIndex(ArrayList<ActionMenuItem> items, int ordering) {
        for (int i = items.size() - 1; i >= 0; i--) {
            ActionMenuItem item = items.get(i);
            if (item.getOrder() <= ordering) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Returns the ordering across all items. This will grab the category from
     * the upper bits, find out how to order the category with respect to other
     * categories, and combine it with the lower bits.
     *
     * @param categoryOrder The category order for a particular item (if it has
     *                      not been or/add with a category, the default category is
     *                      assumed).
     * @return An ordering integer that can be used to order this item across
     * all the items (even from other categories).
     */
    private static int getOrdering(int categoryOrder) {
        final int index = (categoryOrder & CATEGORY_MASK) >> CATEGORY_SHIFT;

        if (index < 0 || index >= sCategoryToOrder.length) {
            throw new IllegalArgumentException("order does not contain a valid category.");
        }

        return (sCategoryToOrder[index] << CATEGORY_SHIFT) | (categoryOrder & USER_MASK);
    }

    private Context getContext() {
        return mContext;
    }

    public MenuItem add(CharSequence title) {
        return add(0, 0, 0, title);
    }

    public MenuItem add(int titleRes) {
        return add(0, 0, 0, titleRes);
    }

    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return add(groupId, itemId, order, mContext.getString(titleRes));
    }

    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        ActionMenuItem item = new ActionMenuItem(getContext(),
                groupId, itemId, order, title);
        mItems.add(findInsertIndex(mItems, getOrdering(order)), item);
        return item;
    }

    void add(ActionMenuItem item) {
        mItems.add(findInsertIndex(mItems, getOrdering(item.getOrder())), item);
    }

    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller,
                                Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {

        PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivityOptions(caller, specifics, intent, 0);

        if ((flags & FLAG_APPEND_TO_GROUP) == 0) {
            removeGroup(groupId);
        }

        for (ResolveInfo resolveInfo : resolveInfoList) {
            Intent resolveInfoIntent = new Intent(resolveInfo.specificIndex < 0 ?
                    intent : specifics[resolveInfo.specificIndex]);

            resolveInfoIntent.setComponent(
                    new ComponentName(
                            resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name)
            );

            MenuItem item = add(groupId, itemId, order, resolveInfo.loadLabel(packageManager))
                    .setIcon(resolveInfo.loadIcon(packageManager))
                    .setIntent(resolveInfoIntent);

            if (outSpecificItems != null && resolveInfo.specificIndex >= 0) {
                outSpecificItems[resolveInfo.specificIndex] = item;
            }
        }
        return resolveInfoList.size();
    }

    public SubMenu addSubMenu(CharSequence title) {
        return null;
    }

    public SubMenu addSubMenu(int titleRes) {
        return null;
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return null;
    }

    public void clear() {
        mItems.clear();
    }

    public void close() {
    }

    private int findItemIndex(int id) {
        final ArrayList<ActionMenuItem> items = mItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            if (items.get(i).getItemId() == id) {
                return i;
            }
        }
        return -1;
    }

    public MenuItem findItem(int id) {
        final int index = findItemIndex(id);
        if (index < 0) {
            return null;
        }

        return mItems.get(index);
    }

    public MenuItem getItem(int index) {
        return mItems.get(index);
    }

    public boolean hasVisibleItems() {
        final ArrayList<ActionMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            if (items.get(i).isVisible()) {
                return true;
            }
        }

        return false;
    }

    private ActionMenuItem findItemWithShortcut(int keyCode) {
        final boolean qwerty = mIsQwerty;
        final ArrayList<ActionMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            final char shortcut = qwerty ? item.getAlphabeticShortcut() :
                    item.getNumericShortcut();
            if (keyCode == shortcut) {
                return item;
            }
        }
        return null;
    }

    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return findItemWithShortcut(keyCode) != null;
    }

    public boolean performIdentifierAction(int id, int flags) {
        final int index = findItemIndex(id);
        return index >= 0 && mItems.get(index).invoke();
    }

    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        ActionMenuItem item = findItemWithShortcut(keyCode);
        return item != null && item.invoke();

    }

    public void removeGroup(int groupId) {
        final ArrayList<ActionMenuItem> items = mItems;
        int itemCount = items.size();
        int i = 0;
        while (i < itemCount) {
            if (items.get(i).getGroupId() == groupId) {
                items.remove(i);
                itemCount--;
            } else {
                i++;
            }
        }
    }

    public void removeItem(int id) {
        final int index = findItemIndex(id);
        if (index < 0) {
            return;
        }

        mItems.remove(index);
    }

    public void setGroupCheckable(int group, boolean checkable,
                                  boolean exclusive) {
        final ArrayList<ActionMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setCheckable(checkable);
                item.setExclusiveCheckable(exclusive);
            }
        }
    }

    public void setGroupEnabled(int group, boolean enabled) {
        final ArrayList<ActionMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setEnabled(enabled);
            }
        }
    }

    public void setGroupVisible(int group, boolean visible) {
        final ArrayList<ActionMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setVisible(visible);
            }
        }
    }

    public void setQwertyMode(boolean isQwerty) {
        mIsQwerty = isQwerty;
    }

    public int size() {
        return mItems.size();
    }

    ActionMenu clone(int size) {
        ActionMenu out = new ActionMenu(getContext());
        out.mItems = new ArrayList<>(this.mItems.subList(0, size));
        return out;
    }

    void removeInvisible() {
        Iterator<ActionMenuItem> actionMenuItemIterator = mItems.iterator();
        while (actionMenuItemIterator.hasNext()) {
            ActionMenuItem item = actionMenuItemIterator.next();
            if (!item.isVisible()) actionMenuItemIterator.remove();
        }
    }

    @Override
    public void setGroupDividerEnabled(boolean b) {

    }
}
