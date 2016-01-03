package com.lb.three_phases_bottom_sheet;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by user on 14/12/2015.
 */
public class Utils {

    /**
     * hides the soft keyboard for the activity , based on the currently focused view
     */
    public static void hideSoftKeyboardFromFocusedView(final Activity activity) {
        if (activity == null)
            return;
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        final View focusedView = activity.getWindow().getCurrentFocus();
        if (focusedView != null)
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
    }

    /**
     * returns the height of the action bar
     */
    public static int getActionBarHeight(final Context context) {
        // based on http://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height
        final TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources()
                    .getDisplayMetrics());
        return actionBarHeight;
    }

}
