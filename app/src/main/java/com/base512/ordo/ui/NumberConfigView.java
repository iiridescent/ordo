package com.base512.ordo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.ordo.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * An editable number field with '+' and '-' buttons for manually adjusting
 */
public class NumberConfigView extends ConstraintLayout implements TextWatcher, View.OnClickListener {

    @BindView(R.id.numberConfigAddButton)
    ImageView mAddButton;

    @BindView(R.id.numberConfigSubtractButton)
    ImageView mSubtractButton;

    @BindView(R.id.numberConfigValueField)
    EditText mValueField;

    @BindView(R.id.numberConfigLabel)
    TextView mLabel;

    private int mMinValue;

    private int mMaxValue;

    private boolean mWasEdited = false;

    public NumberConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NumberConfigView, 0, 0);
        String labelText = a.getString(R.styleable.NumberConfigView_labelText);
        int value = a.getInt(R.styleable.NumberConfigView_defaultValue, 0);
        mMaxValue = a.getInt(R.styleable.NumberConfigView_maxValue, Integer.MAX_VALUE);
        mMinValue = a.getInt(R.styleable.NumberConfigView_minValue, Integer.MIN_VALUE);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_number_config, this, true);

        ButterKnife.bind(this, this);

        mLabel.setText(labelText);

        mValueField.setText(String.valueOf(value));
        mValueField.addTextChangedListener(this);

        mAddButton.setOnClickListener(this);
        mSubtractButton.setOnClickListener(this);
    }

    private void addToValue(int valueToAdd) {
        int newValue = Integer.valueOf(mValueField.getText().toString()) + valueToAdd;
        mValueField.setText(String.valueOf(newValue));
    }

    public int getValue() {
        return Integer.valueOf(mValueField.getText().toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.numberConfigAddButton:
                addToValue(1);
                break;
            case R.id.numberConfigSubtractButton:
                addToValue(-1);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (mWasEdited){
            mWasEdited = false;
            return;
        }

        int inputValue = editable.toString().isEmpty() ? 0 : Integer.valueOf(editable.toString());
        int numberValue = Math.min(mMaxValue, Math.max(mMinValue, inputValue));

        mWasEdited = true;

        mValueField.setText(String.valueOf(numberValue));
        mValueField.setSelection(mValueField.getText().length());
    }
}