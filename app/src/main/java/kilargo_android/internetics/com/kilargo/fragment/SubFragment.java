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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.adapter.KKSubCategoryListAdapter;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.model.SubCategory;
import kilargo_android.internetics.com.kilargo.util.UIHelper;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class SubFragment extends BaseFragment {

    @Bind(R.id.search_view)  SearchView mSearchView;
    @Bind(R.id.listview)     ListView   mListView;
    @Bind(R.id.refresh_button) ImageButton mRefreshButton;
    @Bind(R.id.back_textview)TextView mBackTextView;

    private KKSubCategoryListAdapter mAdapter;

    private Integer mParentCategoryID;

    private List<SubCategory> mSubCategories;

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

        mRefreshButton.setVisibility(View.INVISIBLE);

        mAdapter = new KKSubCategoryListAdapter(getActivity());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });

        mAdapter.setDataArrayList(mSubCategories);
        mAdapter.notifyDataSetInvalidated();

        setupSearch();


        mBackTextView.setVisibility(View.VISIBLE);
        mBackTextView.setText("Back");
        Drawable img = getResources().getDrawable(R.drawable.back_arrow );
        img.setBounds( 0, 0, 24, 24 );
        mBackTextView.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        mBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    private ArrayList<HashMap<String,Object>> mSearchResult;
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

                if (mSearchResultPopupWindow != null) {
                    mSearchResultPopupWindow.dismiss();
                    mSearchResultPopupWindow = null;
                }

                List<Product> rawSearchResult = JsonFetcher.sharedFetcher().getProductsWithAnyKeyword(s);

                if (rawSearchResult.size() ==0) {
                    return false;
                }

                LayoutInflater layoutInflater = (LayoutInflater)getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.search_result_popup, null);

                LinearLayout scrollViewContentLL = (LinearLayout) popupView.findViewById(R.id.search_result_scrollview_content_ll);

                scrollViewContentLL.removeAllViews();

                mSearchResult = new ArrayList<>();
                for (Product product : rawSearchResult) {

                    for (Integer subCategoryID : product.subcategoryIDList) {

                        String subCategoryName = JsonFetcher.sharedFetcher().getSubCategoryName(subCategoryID);

                        for (Integer categoryID: product.categoryIDList) {

                            boolean isParentChildRelationship = JsonFetcher.sharedFetcher().isParentChildRelationship(categoryID,subCategoryID);

                            if (isParentChildRelationship) {

                                String categoryName = JsonFetcher.sharedFetcher().getCategoryName(categoryID);

                                if (subCategoryName.length() > 0 && categoryName.length() > 0) {

                                    HashMap<String,Object> dict = new HashMap<String, Object>();
                                    dict.put("subCategoryName",subCategoryName);
                                    dict.put("categoryName",categoryName);
                                    dict.put("product",product);
                                    mSearchResult.add(dict);
                                }
                            }
                        }
                    }
                }

                int i = 0;
                for (HashMap<String,Object> item:mSearchResult) {

                    final View searchResultItem = LayoutInflater.from(getActivity()).inflate(R.layout.search_result_item, null);
                    searchResultItem.setTag(String.format("%d",i));
                    searchResultItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchResultItemClicked(view);
                        }
                    });

                    TextView summaryTextView = (TextView) searchResultItem.findViewById(R.id.summary_textview);

                    String text = String.format("%s->%s",item.get("categoryName"),item.get("subCategoryName"));
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

                if (mSearchResultPopupWindow != null && mSearchResultPopupWindow.isShowing() == false) {
                    mSearchResultPopupWindow.dismiss();

                }
                mSearchResultPopupWindow = null;

                mSearchResultPopupWindow = new PopupWindow(popupView,mSearchView.getWidth(), (int) UIHelper.convertDpToPixel(150),true);
                mSearchResultPopupWindow.setFocusable(false);
                mSearchResultPopupWindow.setOutsideTouchable(true);

                if (mSearchResultPopupWindow.isShowing() == false) {
                    mSearchResultPopupWindow.showAsDropDown(mSearchView,0,10);
                }


                return false;
            }
        });
    }

    private void searchResultItemClicked(View view) {

        int index = Integer.parseInt((String) view.getTag());

        Product selectedProduct = (Product) mSearchResult.get(index).get("product");

        mSearchView.setQuery("",false);
        mSearchView.clearFocus();
        if (mSearchResultPopupWindow != null) {
            mSearchResultPopupWindow.dismiss();
        }

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



    }

    private void listItemClicked(int i) {

        showProductDetails(i);
    }

    private void showProductDetails(int i) {

        SubCategory selectedSubCategory = mSubCategories.get(i);

        ProductFragment newFragment = new ProductFragment();
        List<Product> products = JsonFetcher.sharedFetcher().getProducts(mParentCategoryID,selectedSubCategory.subcategoryID);
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

    public void setParentCategoryID(int parentCategoryID) {
        mParentCategoryID = parentCategoryID;
        mSubCategories = JsonFetcher.sharedFetcher().getSubCategoryWithParentID(parentCategoryID);

    }



}
