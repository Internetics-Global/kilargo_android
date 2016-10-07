package kilargo_android.internetics.com.kilargo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.adapter.KKListAdapter;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.UIHelper;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class SubFragment extends BaseFragment {

    @Bind(R.id.search_view)  SearchView mSearchView;
    @Bind(R.id.listview)     ListView   mListView;
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

//        mSearchView.clearFocus();
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

        setupSearch();


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

    private List<Product> mSearchResult;
    private PopupWindow mSearchResultPopupWindow;
    private void setupSearch() {

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                mSearchResult = JsonFetcher.sharedFetcher().getProductsWithAnyKeyword(s);

                if (mSearchResult.size() ==0 && mSearchResultPopupWindow != null) {
                    mSearchResultPopupWindow.dismiss();
                    mSearchResultPopupWindow = null;
                    return false;
                }

                LayoutInflater layoutInflater = (LayoutInflater)getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.search_result_popup, null);

                LinearLayout scrollViewContentLL = (LinearLayout) popupView.findViewById(R.id.search_result_scrollview_content_ll);

                scrollViewContentLL.removeAllViews();

                int i = 0;
                for (Product item:mSearchResult) {

                    final View searchResultItem = LayoutInflater.from(getActivity()).inflate(R.layout.search_result_item, null);
                    searchResultItem.setTag(String.format("%d",i));
                    searchResultItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchResultItemClicked(view);
                        }
                    });

                    TextView summaryTextView = (TextView) searchResultItem.findViewById(R.id.summary_textview);

                    String text = String.format("%s->%s",item.mCategory,item.mSubcategory);
                    summaryTextView.setText(text);

                    scrollViewContentLL.addView(searchResultItem);

                    if (i < mSearchResult.size() -1) {
                        int dp5 = (int) UIHelper.convertDpToPixel(5);
                        View separator = new View(getActivity());
                        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        layoutParams.setMargins(dp5 *2,dp5,dp5,dp5 *2);
                        separator.setBackgroundColor(Color.DKGRAY);
                        separator.setLayoutParams(layoutParams);
                        scrollViewContentLL.addView(separator);
                    }

                    i++;
                }

                if (mSearchResultPopupWindow == null) {
                    mSearchResultPopupWindow = new PopupWindow(popupView,mSearchView.getWidth(), (int) UIHelper.convertDpToPixel(150),true);
                    mSearchResultPopupWindow.setFocusable(false);
                    mSearchResultPopupWindow.setOutsideTouchable(true);

                }

                if (mSearchResultPopupWindow.isShowing() == false) {
                    mSearchResultPopupWindow.showAsDropDown(mSearchView,0,10);
                }


                return false;
            }
        });
    }

    private void searchResultItemClicked(View view) {

        int index = Integer.parseInt((String) view.getTag());

        Product selectedProduct = mSearchResult.get(index);

        ProductFragment newFragment = new ProductFragment();
        newFragment.setProductList(Arrays.asList(selectedProduct));
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                0,
                0);
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("ProductFragment" +System.currentTimeMillis());
        transaction.commit();

        mSearchView.setQuery("", false);
        mSearchView.clearFocus();


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
