package kilargo_android.internetics.com.kilargo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.common.base.Strings;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

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
public class CarouseActivity extends BaseActivity {

    @Bind(R.id.close_button)
    ImageButton mCloseButton;
    @Bind(R.id.view_pager)  ViewPager mViewPager;

    private ArrayList<String> mValidImages = new ArrayList<>();

    private static final String EXTRA_PARCEL = "product";

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
        if (Strings.isNullOrEmpty(product.productImage) == false) {
            mValidImages.add(product.productImage);
        }

        mViewPager.setAdapter(new SamplePagerAdapter());

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void dismiss() {

        this.finish();
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
