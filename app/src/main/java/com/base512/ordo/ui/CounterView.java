package com.base512.ordo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CounterView extends TextView {
    private int mNumerator;
    private int mDenominator;
    public CounterView(Context context) {
        this(context, null);
    }

    public CounterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mNumerator = 0;
        mDenominator = 0;
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
        setText(mNumerator +"/"+ mDenominator);
    }
}
