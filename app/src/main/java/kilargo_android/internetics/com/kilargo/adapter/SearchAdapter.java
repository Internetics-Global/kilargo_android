package kilargo_android.internetics.com.kilargo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.model.Product;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class SearchAdapter extends BaseAdapter {

    private Context     mContext;

    private List<Product> mDataArrayList;

    public SearchAdapter(Context c)
    {
        mContext = c;
    }

    @Override
    public int getCount() {
        if (mDataArrayList == null) {
            return 0;
        } else {
            return mDataArrayList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Product product = mDataArrayList.get(i);

        holder.categoryNameTextView.setText(product.mCategory + " -> " + product.mSubcategory);
        holder.productNameTextView.setText(product.mProductName);


        return view;
    }


    static class ViewHolder {
        @Bind(R.id.category_textview) TextView categoryNameTextView;
        @Bind(R.id.product_name_textview) TextView productNameTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public void setDataArrayList(List dataArrayList) {
        mDataArrayList = dataArrayList;
    }
}
