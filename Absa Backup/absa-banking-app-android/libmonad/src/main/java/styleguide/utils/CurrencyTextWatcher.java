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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyTextWatcher implements TextWatcher {

    private final EditText editText;
    private final DecimalFormat formatter;
    private final String currency;
    private boolean isSelfChange = false;

    public CurrencyTextWatcher(EditText editText, String currency) {
        this.editText = editText;
        this.currency = currency;

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        decimalFormatSymbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("###,###,###,###,###.##", decimalFormatSymbols);
        formatter.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (currency.equals(s.toString())) {
            isSelfChange = true;
            s.replace(0, currency.length(), String.format("%s ", currency));
            return;
        }

        if (isSelfChange) {
            isSelfChange = false;
            return;
        }

        if (containsTwoDecimals(s.toString())) {
            isSelfChange = true;
            editText.setText(editText.getText().toString().substring(0, editText.length() - 1));
        } else {
            editText.setText(priceWithDecimal(s.toString().replace("-", "")));
        }

        editText.setSelection(editText.getText().length());
    }

    private String priceWithDecimal(String priceString) {
        if (priceString.contains(".") && priceString.substring(priceString.indexOf(".")).length() > 3) {
            return priceString.substring(0, priceString.length() - 1);
        }

        priceString = priceString.replace(currency, "").replaceAll(" ", "").replaceAll("\u00A0", "");
        boolean containsDecimal = priceString.contains(".");

        //if there is a comma acting as a decimal separator, replace it with a period
        if (priceString.length() > 3 && priceString.charAt(priceString.length() - 3) == ',') {
            StringBuilder stringBuilder = new StringBuilder(priceString);
            int position = priceString.lastIndexOf(',');
            priceString = stringBuilder.replace(position, position + 1, ".").toString();
        }

        //if there are still any commas left, these are probably thousands separator
        if (priceString.contains(",")) {
            priceString = priceString.replace(",", "");
        }

        isSelfChange = true;
        if (containsDecimal && priceString.substring(priceString.indexOf(".")).length() > 3) {
            return String.format("%s %s", currency, priceString.substring(0, priceString.length() - 1));
        }

        if ("".equals(priceString) || ".".equals(priceString)) {
            return String.format("%s ", currency);
        }

        String decimal = "";

        if (priceString.lastIndexOf(".") == priceString.length() - 1) {
            decimal = ".";
        } else if (priceString.lastIndexOf(".0") > 0 && priceString.lastIndexOf(".0") == priceString.length() - 2) {
            decimal = ".0";
        } else if (containsDecimal) {
            if (priceString.contains(".00")) {
                decimal = ".00";
            } else if (priceString.charAt(priceString.length() - 1) == '0') {
                decimal = "0";
            }
        }
        BigDecimal price = new BigDecimal(priceString);

        return String.format("%s %s%s", currency, formatter.format(price), decimal);
    }

    private boolean containsTwoDecimals(String text) {
        int counter = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '.') {
                counter++;
            }
            if (counter == 2) {
                return true;
            }
        }
        return false;
    }
}
