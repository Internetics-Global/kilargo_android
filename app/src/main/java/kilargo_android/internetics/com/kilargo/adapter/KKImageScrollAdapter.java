package kilargo_android.internetics.com.kilargo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.Global;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class KKImageScrollAdapter extends PagerAdapter {

    private Context mContext;

    private List<Product> mProductList = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;  //ViewPager does not provide item click function, so we have to design ourself

    public KKImageScrollAdapter(Context c)
    {
        mContext = c;
    }

    @Override
    public int getCount() {
        if (mProductList == null) {
            return 0;
        } else {
            return mProductList.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_item,container, false);
        container.addView(view);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.spinner_indicator);

        Product product = mProductList.get(position);

        String url = Global.imageBaseURL + product.mImage1;
        if (url.contains(".png") || url.contains(".jpg") || url.contains(".jpeg")) {

        } else {
            url = url + ".png";
        }
        final ImageView productImageView = (ImageView) view.findViewById(R.id.product_imageview);
        Picasso.with(mContext)
                .load(url)
                .fit().centerInside()
                .into(productImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                        progressBar.setVisibility(View.GONE);

                        productImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.loading_error));
                        productImageView.setScaleType(ImageView.ScaleType.CENTER);

                    }
                });

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });



        return view;
    }



    public void setProductList(List productList) {
        mProductList = productList;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
