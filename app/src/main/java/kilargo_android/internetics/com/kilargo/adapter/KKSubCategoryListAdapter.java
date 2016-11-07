package kilargo_android.internetics.com.kilargo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.model.SubCategory;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class KKSubCategoryListAdapter extends BaseAdapter {

    private Context     mContext;

    private List<SubCategory> mDataArrayList = new ArrayList<>();

    public KKSubCategoryListAdapter(Context c)
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
            view = LayoutInflater.from(mContext).inflate(R.layout.menu_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.titleTextView.setText(mDataArrayList.get(i).subcategoryName);

        return view;
    }


    static class ViewHolder {
        @Bind(R.id.title_textview) TextView titleTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public void setDataArrayList(List dataArrayList) {
        mDataArrayList = dataArrayList;
    }
}
