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

package styleguide.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

public final class ViewUtils {

    public static float getDisplayDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static int generateViewId() {
        return View.generateViewId();
    }

    public static void setTextAppearance(TextView view, Context context, int resourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextAppearance(resourceId);
        } else {
            view.setTextAppearance(context, resourceId);
        }
    }

}