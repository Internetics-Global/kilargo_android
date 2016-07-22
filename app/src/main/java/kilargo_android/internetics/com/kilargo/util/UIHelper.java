package kilargo_android.internetics.com.kilargo.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class UIHelper {

    public static float convertDpToPixel(float dp){
        Resources resources = AppContext.getAppContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px){
        Resources resources = AppContext.getAppContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public static int getScreenWidth (Context context) {
        WindowManager wm = (WindowManager) AppContext.getAppContext().getSystemService(context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;  // 宽度（PX）

        return width;
    }

    public static int getScreenHeight (Context context) {
        WindowManager wm = (WindowManager) AppContext.getAppContext().getSystemService(context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;

        return height;
    }
}
