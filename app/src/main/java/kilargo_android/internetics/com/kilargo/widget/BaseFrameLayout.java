package kilargo_android.internetics.com.kilargo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class BaseFrameLayout extends FrameLayout {
    public BaseFrameLayout(Context context) {
        super(context);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
         * used in fragment_slide_left_enter.xml, etc
         */
    @SuppressWarnings("unused")
    public float getXFraction() {
        final int width = getWidth();
        if (width != 0) return getX() / getWidth();
        else return getX();
    }

    /*
     * used in fragment_slide_left_enter.xml, etc
     */
    @SuppressWarnings("unused")
    public void setXFraction(float xFraction) {
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : -9999);
    }
}
