package com.lb.three_phases_bottom_sheet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build.VERSION_CODES;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
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

    public static Bitmap blur(Context context, Bitmap bkg) {
        return blur(context, bkg, 4);
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap srcBitmap, float radius) {
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

    public static Bitmap fastBlur(Bitmap bitmap) {
        return fastBlur(bitmap, 2, 16);
    }

    //http://stackoverflow.com/questions/2067955/fast-bitmap-blur-for-android-sdk
    public static Bitmap fastBlur(Bitmap inputBitmap, float scale, int radius) {
        if (inputBitmap == null)
            return null;
        int width = Math.round(inputBitmap.getWidth() * scale);
        int height = Math.round(inputBitmap.getHeight() * scale);
        inputBitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, false);

        Bitmap resultBitmap = inputBitmap.copy(inputBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = resultBitmap.getWidth();
        int h = resultBitmap.getHeight();

        int[] pix = new int[w * h];
        resultBitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        resultBitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (resultBitmap);
    }

}
