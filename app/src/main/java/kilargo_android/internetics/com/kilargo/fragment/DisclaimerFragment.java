package kilargo_android.internetics.com.kilargo.fragment;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.activity.MainActivity;

/**
 * Created by BourneWang on 6/07/2016.
 */
public class DisclaimerFragment extends BaseFragment {

    @Bind(R.id.back_textview)    TextView mBackTextView;
    @Bind(R.id.refresh_button)   ImageButton mRefreshButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_disclaimer, null);
        ButterKnife.bind(this,view);

        setupView(view);

        return view;
    }


    private void setupView(View baseView) {

        mBackTextView.setVisibility(View.VISIBLE);
        mBackTextView.setText("Back");
        Drawable img = getResources().getDrawable(R.drawable.left_setting );
        img.setBounds( 0, 0, 24, 24 );
        mBackTextView.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        mBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).switchDrawer();
            }
        });

        mRefreshButton.setVisibility(View.INVISIBLE);

    }

}
