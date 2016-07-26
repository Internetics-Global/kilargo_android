package kilargo_android.internetics.com.kilargo.fragment;

import android.support.v4.app.Fragment;
import android.view.animation.Animation;

import kilargo_android.internetics.com.kilargo.util.FragmentUtils;

/**
 * Created by BourneWang on 26/07/2016.
 */
public class BaseFragment extends Fragment {

    @Override
        public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (FragmentUtils.sDisableFragmentAnimations) {
            Animation a = new Animation() {};
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
}


