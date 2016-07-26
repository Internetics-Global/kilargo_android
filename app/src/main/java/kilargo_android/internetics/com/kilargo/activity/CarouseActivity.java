package kilargo_android.internetics.com.kilargo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.Global;
import kilargo_android.internetics.com.kilargo.util.UIHelper;

/**
 * Created by BourneWang on 5/05/2016.
 */
public class CarouseActivity extends BaseActivity {

    @Bind(R.id.carousel_layout)  ViewGroup            mBaseLayout;
    @Bind(R.id.scrollview)       HorizontalScrollView mHorizontalScrollViewl;
    @Bind(R.id.ll)               LinearLayout         mScrollViewContentView;

    private ArrayList<String> mValidImages = new ArrayList<>();


    private static final String EXTRA_PARCEL = "product";

    public static Intent buildIntent(Context context, Product product) {
        Intent intent = new Intent(context, CarouseActivity.class);
        intent.putExtra(EXTRA_PARCEL, Parcels.wrap(product));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        mBaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setupHorizontalScrollView();


    }

    private void setupHorizontalScrollView() {

        for (String item : mValidImages) {

            int margin = (int) UIHelper.convertDpToPixel(10);
            int width = UIHelper.getScreenWidth(CarouseActivity.this) - (int) UIHelper.convertDpToPixel(40);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            params.leftMargin = margin;
            params.rightMargin = margin;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            String url = Global.imageBaseURL + item;
            Picasso.with(this)
                    .load(url)
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            mScrollViewContentView.addView(imageView);

        }


    }

    private void dismiss() {
        this.finish();
    }
}
