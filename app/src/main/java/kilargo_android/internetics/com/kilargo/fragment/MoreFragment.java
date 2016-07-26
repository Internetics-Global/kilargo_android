package kilargo_android.internetics.com.kilargo.fragment;

import android.app.Fragment;
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

    @Bind(R.id.avatar_image)                   ImageView       mClerkAvatarImageView;
    @Bind(R.id.button_setting_setting)                   Button       mSettingButton;
    @Bind(R.id.button_setting_about)                   Button       mAboutButton;
    @Bind(R.id.version_info)                   TextView       mVersionTextView;

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


        mClerkAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clerkButtonClicked(v);

            }
        });

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).settingButtonClicked();

            }
        });


        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).aboutButtonClicked();

            }
        });


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

    private void clerkButtonClicked(View v) {

    }

    public void refreshContentViews() {


    }




}
