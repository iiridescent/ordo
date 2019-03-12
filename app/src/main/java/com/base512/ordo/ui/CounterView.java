package com.base512.ordo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.base512.ordo.R;
import com.base512.ordo.util.StringUtils;


/**
 * View for displaying progress as "{quantity} / {total}"
 */
public class CounterView extends TextView {
    private int mCount;
    private int mTotal;

    private String mSeparator;
    private boolean mPadNumber;

    private static final String DEFAULT_SEPARATOR = "/";

    public CounterView(Context context) {
        this(context, null);
    }

    public CounterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCount = 0;
        mTotal = 0;

        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CounterView,
                0, 0);

        try {
            String separator = attributes.getString(R.styleable.CounterView_separator);
            mSeparator = separator == null ? DEFAULT_SEPARATOR : separator;
            mPadNumber = attributes.getBoolean(R.styleable.CounterView_padNumber, false);
        } finally {
            attributes.recycle();
        }

        updateCounterText();
    }

    public void setCount(int numerator) {
        mCount = numerator;
        updateCounterText();
    }

    public void setTotal(int denominator) {
        mTotal = denominator;
        updateCounterText();
    }

    private void updateCounterText() {
        String numeratorText = mPadNumber ? StringUtils.leftZeroPad(mCount, 2) : String.valueOf(mCount);
        String denominatorText = mPadNumber ? StringUtils.leftZeroPad(mTotal, 2) : String.valueOf(mTotal);
        setText(numeratorText+mSeparator+denominatorText);
    }
}
