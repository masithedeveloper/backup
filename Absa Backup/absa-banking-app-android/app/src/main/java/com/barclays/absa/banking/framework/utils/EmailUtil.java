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
package com.barclays.absa.banking.framework.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class EmailUtil {

    public static void email(Activity activity, String emailAddress) {
        Intent mailer = new Intent(Intent.ACTION_SENDTO);
        mailer.setType("message/rfc822");
        mailer.setData(Uri.parse("mailto:"));
        mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        activity.startActivity(Intent.createChooser(mailer, "Send email..."));
    }
}
