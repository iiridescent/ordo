package com.base512.ordo.ui;

import android.view.animation.Interpolator;

/**
 * Created by Thomas on 2/23/2017.
 */

public class SineInterpolator implements Interpolator {

    private EasingType.Type type;

    public SineInterpolator(EasingType.Type type) {
        this.type = type;
    }

    public float getInterpolation(float t) {
        if (type == EasingType.Type.IN) {
            return in(t);
        } else
        if (type == EasingType.Type.OUT) {
            return out(t);
        } else
        if (type == EasingType.Type.INOUT) {
            return inout(t);
        }
        return 0;
    }

    private float in(float t) {
        return (float) (-Math.cos(t * (Math.PI/2)) + 1);
    }
    private float out(float t) {
        return (float) Math.sin(t * (Math.PI/2));
    }
    private float inout(float t) {
        return (float) (-0.5f * (Math.cos(Math.PI*t) - 1));
    }
}