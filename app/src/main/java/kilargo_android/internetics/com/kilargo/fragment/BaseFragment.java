package kilargo_android.internetics.com.kilargo.fragment;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.view.TouchDelegate;
import android.view.View;
import android.view.animation.Animation;

import kilargo_android.internetics.com.kilargo.util.FragmentUtils;
import kilargo_android.internetics.com.kilargo.util.UIHelper;

/**
 * Created by BourneWang on 26/07/2016.
 */
public class BaseFragment extends Fragment {

    public void increaseControlTouchArea(final View widget,final int byDP) {
        final View parent = (View) widget.getParent();
        parent.post( new Runnable() {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before we call getHitRect()
            public void run() {
                int byPixel = (int) UIHelper.convertDpToPixel(byDP);
                final Rect r = new Rect();
                widget.getHitRect(r);
                r.top -= byPixel;
                r.bottom += byPixel;
                r.left -= byPixel;
                r.right += byPixel;
                parent.setTouchDelegate( new TouchDelegate( r , widget));
            }
        });
    }

}


