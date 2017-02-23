package com.base512.ordo.util;

/**
 * Created by Thomas on 2/23/2017.
 */

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Utilities for Activities
 */
public class ActivityUtils {
    public static void openActivity(@NonNull Activity activity, @NonNull Class<?> activityToOpen) {
        openActivity(activity, activityToOpen, null, null);
    }

    public static void openActivityWithTransition(@NonNull Activity activity, @NonNull Class<?> activityToOpen, Bundle bundle, @NonNull View view, @NonNull String transitionName) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, transitionName);

        openActivity(activity, activityToOpen, bundle, options);
    }

    public static void openActivity(@NonNull Activity activity, @NonNull Class<?> activityToOpen, @Nullable  Bundle bundle, @Nullable  ActivityOptionsCompat activityOptions) {
        Intent intent = new Intent(activity, activityToOpen);

        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if(activityOptions != null) {
            activity.startActivity(intent, activityOptions.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }
}
