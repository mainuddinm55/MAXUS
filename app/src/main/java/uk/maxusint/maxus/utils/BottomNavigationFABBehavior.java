package uk.maxusint.maxus.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;



public final class BottomNavigationFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    public BottomNavigationFABBehavior(@Nullable Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean layoutDependsOn(@Nullable CoordinatorLayout parent, @NonNull FloatingActionButton child, @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton child, @NonNull View dependency) {
        child.setTranslationY(0.0f);
    }

    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton child, @NonNull View dependency) {
        return this.updateButton(child, dependency);
    }

    private boolean updateButton(View child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float oldTranslation = child.getTranslationY();
            float height = (float) dependency.getHeight();
            float newTranslation = dependency.getTranslationY() - height;
            child.setTranslationY(newTranslation);
            return oldTranslation != newTranslation;
        } else {
            return false;
        }
    }
}
