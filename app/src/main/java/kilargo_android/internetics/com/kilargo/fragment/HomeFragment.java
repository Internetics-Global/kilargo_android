package kilargo_android.internetics.com.kilargo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;

/**
 * Created by internetics on 7/10/2016.
 */

public class HomeFragment extends BaseFragment  {

    @Bind(R.id.button_get_started) ImageButton mGetStartedButton;

    @Bind(R.id.back_textview)      TextView    mBackTextView;

    @Bind(R.id.refresh_button)     ImageButton     mRefreshButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home, container,false);
        ButterKnife.bind(this,view);

        setupView(view);

        return  view;

    }

    private void setupView(View view) {

        mBackTextView.setVisibility(View.INVISIBLE);
        mRefreshButton.setVisibility(View.INVISIBLE);

        mGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainFragment newFragment = new MainFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        0,
                        0);
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack("MainFragment" +System.currentTimeMillis());
                transaction.commit();
            }
        });

    }
}
