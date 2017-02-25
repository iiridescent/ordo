package com.base512.ordo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.base512.ordo.R;

public class CounterView extends TextView {
    private int mNumerator;
    private int mDenominator;

    private String mSeparator;
    private boolean mPadNumber;

    public CounterView(Context context) {
        this(context, null);
    }

    public CounterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNumerator = 0;
        mDenominator = 0;

        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CounterView,
                0, 0);

        try {
            String separator = attributes.getString(R.styleable.CounterView_separator);
            mSeparator = separator == null ? "/" : separator;
            mPadNumber = attributes.getBoolean(R.styleable.CounterView_padNumber, false);
        } finally {
            attributes.recycle();
        }

        updateCounterText();
    }

    public void setNumerator(int numerator) {
        mNumerator = numerator;
        updateCounterText();
    }

    public void setDenominator(int denominator) {
        mDenominator = denominator;
        updateCounterText();
    }

    private void updateCounterText() {
        String numeratorText = mPadNumber ? String.format("%02d", mNumerator) : String.valueOf(mNumerator);
        String denominatorText = mPadNumber ? String.format("%02d", mDenominator) : String.valueOf(mDenominator);
        setText(numeratorText+mSeparator+denominatorText);
    }
}
