package com.lb.three_phases_bottom_sheet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewCompat;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {
    public enum ViewPadding {START_OR_LEFT, BOTTOM, END_OR_RIGHT, TOP}

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

    public static Bitmap blur(Context context, Bitmap srcBitmap, @FloatRange(from = 0.0f, to = 25.0f) float radius) {
        if (srcBitmap == null)
            return null;
        Bitmap outputBitmap = null;
        RenderScript rs = null;
        try {
            rs = RenderScript.create(context);
            outputBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outputBitmap);
            canvas.drawBitmap(srcBitmap, 0, 0, null);
            Allocation overlayAlloc = Allocation.createFromBitmap(rs, outputBitmap);
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blur.setInput(overlayAlloc);
            blur.setRadius(radius);
            blur.forEach(overlayAlloc);
            overlayAlloc.copyTo(outputBitmap);
            return outputBitmap;
        } catch (Exception ex) {
            if (outputBitmap != null)
                outputBitmap.recycle();
            return srcBitmap;
        } finally {
            if (rs != null)
                rs.destroy();
        }
    }

    /**
     * sets a single padding on a view, without changing the rest of the padding values of the view
     */
    public static void setPaddingOrRelativePadding(View v, ViewPadding paddingType, int paddingValueToSetInPixels) {
        switch (paddingType) {
            case BOTTOM:
                ViewCompat.setPaddingRelative(v, ViewCompat.getPaddingStart(v), v.getPaddingTop(), ViewCompat.getPaddingEnd(v), paddingValueToSetInPixels);
                break;
            case END_OR_RIGHT:
                ViewCompat.setPaddingRelative(v, ViewCompat.getPaddingStart(v), v.getPaddingTop(), paddingValueToSetInPixels, v.getPaddingBottom());
                break;
            case START_OR_LEFT:
                ViewCompat.setPaddingRelative(v, paddingValueToSetInPixels, v.getPaddingTop(), ViewCompat.getPaddingEnd(v), v.getPaddingBottom());
                break;
            case TOP:
                ViewCompat.setPaddingRelative(v, ViewCompat.getPaddingStart(v), paddingValueToSetInPixels, ViewCompat.getPaddingEnd(v), v.getPaddingBottom());
                break;
        }
    }
}
