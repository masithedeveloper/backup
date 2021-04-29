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

package styleguide.forms.maskedEditText;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.lang.ref.WeakReference;

public class MaskedWatcher implements TextWatcher {
    private final WeakReference<MaskedFormatter> maskFormatter;
    private final WeakReference<MaskedEditText> editText;
    private String oldFormattedValue = "";
    private int oldCursorPosition;
    private boolean isDeletingChar = false;

    MaskedWatcher(MaskedFormatter maskedFormatter, MaskedEditText editText) {
        maskFormatter = new WeakReference<>(maskedFormatter);
        this.editText = new WeakReference<>(editText);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == null) {
            return;
        }

        String value = s.toString();

        if (value.length() > oldFormattedValue.length() && maskFormatter.get().getMaskLength() < value.length()) {
            value = oldFormattedValue;
        }

        IFormattedString formattedString = maskFormatter.get().formatString(value);

        if (!TextUtils.equals(s, formattedString)) {
            if (oldCursorPosition > 0) {
                if (oldCursorPosition < formattedString.length()) {
                    String lastChar = formattedString.toString().substring(oldCursorPosition - 1, oldCursorPosition);
                    if (isDeletingChar && lastChar.equals(" ")) {
                        oldCursorPosition--;
                        s.replace(oldCursorPosition - 1, oldCursorPosition, "");
                    } else {
                        s.replace(0, s.length(), formattedString);
                    }
                } else {
                    s.replace(0, s.length(), formattedString);
                }
            } else {
                s.replace(0, s.length(), formattedString);
            }
            isDeletingChar = false;
        }

        int currentPosition = editText.get().getSelectionStart();
        if (oldCursorPosition - currentPosition > 0) {
            int newPosition;
            if (oldFormattedValue.length() - formattedString.length() > 1)
                newPosition = Math.max(0, Math.min(formattedString.length(), oldCursorPosition));
            else
                newPosition = Math.max(0, Math.min(formattedString.length(), currentPosition));
            if (s.length() > 1 && s.charAt(s.length() - 1) == ' ') {
                s.replace(s.length() - 1, s.length(), "");
                return;
            }
            editText.get().setSelection(newPosition);
        }
        oldFormattedValue = formattedString.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        this.oldCursorPosition = editText.get().getSelectionStart();
        isDeletingChar = after == 0;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}