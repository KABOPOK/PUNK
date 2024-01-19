package ru.kabopok.punk_jv.classes;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.Layout;
import android.text.StaticLayout;
import android.widget.EditText;

import ru.kabopok.punk_jv.current.Online;

public class LineLimitFilter implements InputFilter {

    private final int maxLines;

    public LineLimitFilter(int maxLines) {
        this.maxLines = maxLines;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int currentLines = Online.lineCount;
        if (currentLines >= maxLines) {
            // Reject input if it would exceed the maximum lines
            return "";
        } else {
            // Allow input if within line limit
            return null;
        }
    }
}

