package org.bawaberkah.app;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.StringTokenizer;

public class NumberTextWatcherForThousand implements TextWatcher {
    EditText editText;

    public NumberTextWatcherForThousand(EditText editText) {
        this.editText = editText;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
        try {
            this.editText.removeTextChangedListener(this);
            String value = this.editText.getText().toString();
            if (value != null && !value.equals("")) {
                if (value.startsWith(".")) {
                    this.editText.setText("0");
                }

                if (value.startsWith("0")) {
                    this.editText.setText("0");
                }

                String str = this.editText.getText().toString().replaceAll(",", "");
                if (!value.equals("")) {
                    this.editText.setText(getDecimalFormattedString(str));
                }

                this.editText.setSelection(this.editText.getText().toString().length());
            }

            this.editText.addTextChangedListener(this);
        } catch (Exception var4) {
            var4.printStackTrace();
            this.editText.addTextChangedListener(this);
        }
    }

    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }

        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            --j;
            str3 = ".";
        }

        for(int k = j; k >= 0; --k) {
            if (i == 3) {
                str3 = "," + str3;
                i = 0;
            }

            str3 = str1.charAt(k) + str3;
            ++i;
        }

        if (str2.length() > 0) {
            str3 = str3 + "." + str2;
        }

        return str3;
    }

    public static String trimCommaOfString(String string) {
        return string.contains(",") ? string.replace(",", "") : string;
    }
}
