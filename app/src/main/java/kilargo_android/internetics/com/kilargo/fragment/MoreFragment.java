package kilargo_android.internetics.com.kilargo.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.activity.MainActivity;

public class MoreFragment extends BaseFragment {

    @Bind(R.id.button_setting_products)                  Button         mProductsButton;
    @Bind(R.id.button_setting_setting)                   Button         mSettingButton;
    @Bind(R.id.button_setting_about)                     Button         mAboutButton;
    @Bind(R.id.version_info)                             TextView       mVersionTextView;
    @Bind(R.id.back_textview)                            TextView       mNaviBarBackTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Logger.d("onCreateView");

        View view = inflater.inflate(R.layout.fragment_more, null);
        ButterKnife.bind(this,view);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).settingButtonClicked();

            }
        });


        mProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).productsButtonClicked();

            }
        });


        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).aboutButtonClicked();

            }
        });

        mNaviBarBackTextView.setVisibility(View.INVISIBLE);


        refreshContentViews();

        setupVersionBuildInfo();



        return view;
    }

    private void setupVersionBuildInfo() {
        PackageInfo info = null;
        try {
            info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            mVersionTextView.setText("Rev："+info.versionName + "  Build: " + info.versionCode + "\n\nAndroid Version:" + android.os.Build.VERSION.SDK_INT);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Logger.d("onResume");

    }


    public void refreshContentViews() {


    }




}
