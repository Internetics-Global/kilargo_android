package kilargo_android.internetics.com.kilargo.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.adapter.KKListAdapter;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class SubFragment extends BaseFragment {

    @Bind(R.id.search_textview)       TextView mSearchView;
    @Bind(R.id.listview)   ListView   mListView;
    @Bind(R.id.back_textview)TextView mBackTextView;

    private KKListAdapter mAdapter;

    private String mParentCategoryName;

    private List<String> mCategories;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_sub, container,false);
        ButterKnife.bind(this,view);

        setupView(view);

        return  view;

    }

    @Override
    public void onPause() {
        super.onPause();

        mSearchView.clearFocus();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

    }

    private void setupView(View baseView) {

        mAdapter = new KKListAdapter(getActivity());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });

        mAdapter.setDataArrayList(mCategories);
        mAdapter.notifyDataSetInvalidated();


        mSearchView.setFocusable(false);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResultFragment newFragment = new SearchResultFragment();

                android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack("SearchResultFragment" + System.currentTimeMillis());
                transaction.commit();
            }
        });


        mBackTextView.setVisibility(View.VISIBLE);
        mBackTextView.setText("Back");
        Drawable img = getResources().getDrawable(R.drawable.left_arrow );
        img.setBounds( 0, 0, 24, 24 );
        mBackTextView.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        mBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    private void listItemClicked(int i) {

        showProductDetails(i);
    }

    private void showProductDetails(int i) {

        ProductFragment newFragment = new ProductFragment();
        List<Product> products = JsonFetcher.sharedFetcher().getProductsWithSubcategoryName(mCategories.get(i));
        newFragment.setProductList(products);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_pop_enter,
                R.anim.fragment_slide_pop_exit);
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("ProductFragment" + System.currentTimeMillis());
        transaction.commit();

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logger.d("onDestroy");
        super.onDestroy();

    }

    public void setParentCategoryName(String parentCategoryName) {
        mParentCategoryName = parentCategoryName;
        mCategories = JsonFetcher.sharedFetcher().getSubcategoryWithParenent(mParentCategoryName);

    }



}
