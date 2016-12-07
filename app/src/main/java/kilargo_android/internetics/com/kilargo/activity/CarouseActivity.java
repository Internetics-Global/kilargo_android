package kilargo_android.internetics.com.kilargo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.common.base.Strings;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.Global;
import kilargo_android.internetics.com.kilargo.widget.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by BourneWang on 5/05/2016.
 */
public class CarouseActivity extends BaseActivity implements ViewPager.OnPageChangeListener, SensorEventListener{

    @Bind(R.id.close_button)
    ImageButton mCloseButton;
    @Bind(R.id.view_pager)  ViewPager mViewPager;

    @Bind(R.id.pager_left_arrow)               ImageView    mLeftArrowImageView;
    @Bind(R.id.pager_right_arrow)              ImageView    mRightArrowImageView;
    @Bind(R.id.rotation_instruction_imageView) ImageView    mRotationInstructionImageView;

    private ArrayList<String> mValidImages = new ArrayList<>();

    private static final String EXTRA_PARCEL = "product";

    private SensorManager mSensorManager;

    public static Intent buildIntent(Context context, Product product) {
        Intent intent = new Intent(context, CarouseActivity.class);
        intent.putExtra(EXTRA_PARCEL, Parcels.wrap(product));
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carouse);
        ButterKnife.bind(this);

        Product product = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_PARCEL));
        if (Strings.isNullOrEmpty(product.mImage1) == false) {
            mValidImages.add(product.mImage1);
        }
        if (Strings.isNullOrEmpty(product.mImage2) == false) {
            mValidImages.add(product.mImage2);
        }
        if (Strings.isNullOrEmpty(product.mImage3) == false) {
            mValidImages.add(product.mImage3);
        }
        if (Strings.isNullOrEmpty(product.mImage4) == false) {
            mValidImages.add(product.mImage4);
        }
        if (Strings.isNullOrEmpty(product.mImage5) == false) {
            mValidImages.add(product.mImage5);
        }

        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(this);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



    }

    private void setupSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this,gravitySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void cleanSensor() {
        mSensorManager.unregisterListener(this);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float X = sensorEvent.values[0];
        float Y = sensorEvent.values[1];
        float Z = sensorEvent.values[2];
        //Log.d("ccaa","X="+X + " Y=" + Y + " Z=" + Z);

        updateContextHelp(Y);

    }

    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    boolean mIsUsingRotatePleaseRightImage = true;
    private void updateContextHelp(double gravityY) {

        int deviceOrientation = getScreenOrientation();

        Log.d("ccaa","getScreenOrientation (LANDSCAPE = 0,REVERSE_LANDSCAPE=8) is " + deviceOrientation + " gravityY=" + gravityY);

        if (Math.abs(gravityY) < 1.8) {

            if (mRotationInstructionImageView.getVisibility() == View.VISIBLE) {
                mRotationInstructionImageView.setVisibility(View.INVISIBLE);

            }

        } else if (gravityY > 2.3)  {

            if (mRotationInstructionImageView.getVisibility() == View.INVISIBLE) {
                mRotationInstructionImageView.setVisibility(View.VISIBLE);


            }

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRotationInstructionImageView.getLayoutParams();

            if (deviceOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                if (mIsUsingRotatePleaseRightImage == false) {
                    mRotationInstructionImageView.setImageDrawable(getResources().getDrawable(R.drawable.rotate_please_right));
                    mIsUsingRotatePleaseRightImage = true;
                }
            } else {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                if (mIsUsingRotatePleaseRightImage) {
                    mRotationInstructionImageView.setImageDrawable(getResources().getDrawable(R.drawable.rotate_please_left));
                    mIsUsingRotatePleaseRightImage = false;
                }
            }

            mRotationInstructionImageView.setLayoutParams(params);



        } else if (gravityY < -2.3)  {

            if (mRotationInstructionImageView.getVisibility() == View.INVISIBLE) {
                mRotationInstructionImageView.setVisibility(View.VISIBLE);


            }

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRotationInstructionImageView.getLayoutParams();

            if (deviceOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                if (mIsUsingRotatePleaseRightImage) {
                    mRotationInstructionImageView.setImageDrawable(getResources().getDrawable(R.drawable.rotate_please_left));
                    mIsUsingRotatePleaseRightImage = false;
                }
            } else {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                if (mIsUsingRotatePleaseRightImage == false) {
                    mRotationInstructionImageView.setImageDrawable(getResources().getDrawable(R.drawable.rotate_please_right));
                    mIsUsingRotatePleaseRightImage = true;
                }
            }
            mRotationInstructionImageView.setLayoutParams(params);



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void dismiss() {

        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();

        cleanSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private void updatePagerArrowsVisibility(int position) {

        if (mValidImages == null) {
            return;
        }

        if (position > 0) {
            mLeftArrowImageView.setVisibility(View.VISIBLE);
        } else {
            mLeftArrowImageView.setVisibility(View.INVISIBLE);
        }

        if (position < mValidImages.size() - 1) {
            mRightArrowImageView.setVisibility(View.VISIBLE);
        } else {
            mRightArrowImageView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        updatePagerArrowsVisibility(position);

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mValidImages.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            View view = LayoutInflater.from(CarouseActivity.this).inflate(R.layout.carouse_item,container, false);
            container.addView(view);

            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.spinner_indicator);
            final PhotoView   photoView =   (PhotoView) view.findViewById(R.id.photo_view);

            final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
            String url = Global.imageBaseURL + mValidImages.get(position);
            if (url.contains(".png") || url.contains(".jpg") || url.contains(".jpeg")) {

            } else {
                url = url + ".png";
            }

            Picasso.with(CarouseActivity.this)
                    .load(url)
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);

                            photoView.setImageDrawable(getResources().getDrawable(R.drawable.loading_error_white));
                            photoView.setScaleType(ImageView.ScaleType.CENTER);
                        }
                    });

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
