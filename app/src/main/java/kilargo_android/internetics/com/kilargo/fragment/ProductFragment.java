package kilargo_android.internetics.com.kilargo.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.activity.CarouseActivity;
import kilargo_android.internetics.com.kilargo.adapter.KKImageScrollAdapter;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.UIHelper;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class ProductFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @Bind(R.id.search_view)                   SearchView mSearchView;
    @Bind(R.id.viewpager)                     ViewPager mViewPager;
    @Bind(R.id.refresh_button)                Button mRefreshButton;
    @Bind(R.id.product_info_textview)         TextView     mProductInfoTextView;
    @Bind(R.id.product_installation_textview) TextView     mProductInstallationTextView;

    @Bind(R.id.product_name_textview)         TextView    mProductNameTextView;

    @Bind(R.id.product_info_scrollview)       ScrollView  mProductInfoScrollView;
    @Bind(R.id.product_info_bg_mask)          ViewGroup  mProductInfoBgMask;

    @Bind(R.id.back_textview)                   TextView mBackTextView;
    @Bind(R.id.pager_left_arrow)              ImageView    mLeftArrowImageView;
    @Bind(R.id.pager_right_arrow)             ImageView    mRightArrowImageView;

    private KKImageScrollAdapter mAdapter;

    private List<Product> mProductList = new ArrayList<>();

    private int  mCurrentPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_product, container,false);
        ButterKnife.bind(this,view);

        setupView(view);

        return  view;

    }



    private void setupView(View baseView) {

        mRefreshButton.setVisibility(View.INVISIBLE);

        mAdapter = new KKImageScrollAdapter(getActivity());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mAdapter.setOnItemClickListener(new KKImageScrollAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                viewPagerItemClicked(position);
            }
        });
        refresh();

        setupSearch();


        mProductInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchProductContentVisibility();
                updateProductInfoContent();
            }
        });

        mProductInstallationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchProductContentVisibility();
                updateProductInstallationContent();
            }
        });


        mProductInfoBgMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideProductContentView();
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
                backButtonClicked();
            }
        });

        if (mProductList != null && mProductList.size()>0) {
            mProductNameTextView.setText(mProductList.get(0).mProductName);
        }

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

    private void backButtonClicked() {

        getActivity().getSupportFragmentManager().popBackStack();

    }

    private void viewPagerItemClicked(int position) {

        Intent intent = CarouseActivity.buildIntent(getActivity(),mProductList.get(position));

        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Logger.d("onResume");


    }

    @Override
    public void onPause() {
        super.onPause();

        Logger.d("onPause");

//        mSearchView.clearFocus();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public void onStart() {
        super.onStart();

        Logger.d("onStart");
    }


    @Override
    public void onStop() {
        super.onStop();

        Logger.d("onStop");

    }

    @Override
    public void onDestroy() {
        Logger.d("onDestroy");
        super.onDestroy();

    }

    private void refresh() {
        mAdapter.setProductList(mProductList);
        mAdapter.notifyDataSetChanged();

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        mCurrentPage = position;

        this.mProductNameTextView.setText(mProductList.get(position).mProductName);

        updatePagerArrowsVisiblity(position);


    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updatePagerArrowsVisiblity(int position) {

        if (mProductList == null) {
            return;
        }

        if (position > 0) {
            mLeftArrowImageView.setVisibility(View.VISIBLE);
        } else {
            mLeftArrowImageView.setVisibility(View.INVISIBLE);
        }

        if (position < mProductList.size() - 1) {
            mRightArrowImageView.setVisibility(View.VISIBLE);
        } else {
            mRightArrowImageView.setVisibility(View.INVISIBLE);
        }

    }


    private void switchProductContentVisibility() {

        final float alphaVal = 0.95f;

        if (mProductInfoScrollView.getAlpha() > 0) {
            mProductInfoBgMask.setVisibility(View.INVISIBLE);
            mProductInfoScrollView.animate()
                    .alpha(0)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProductInfoScrollView.setAlpha(0);
                        }
                    });
        } else {
            mProductInfoBgMask.setVisibility(View.VISIBLE);
            mProductInfoScrollView.animate()
                    .alpha(alphaVal)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProductInfoScrollView.setAlpha(alphaVal);
                        }
                    });
        }

    }

    private void hideProductContentView() {
        if (mProductInfoScrollView.getAlpha() > 0) {
            mProductInfoBgMask.setVisibility(View.INVISIBLE);
            mProductInfoScrollView.animate()
                    .alpha(0)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProductInfoScrollView.setAlpha(0);
                        }
                    });
        }
    }

    private void updateProductInstallationContent() {

        Product product = mProductList.get(mCurrentPage);

        mProductInfoScrollView.removeAllViews();

        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(layoutParams);
        ll.setOrientation(LinearLayout.VERTICAL);
        mProductInfoScrollView.addView(ll);

        View item = LayoutInflater.from(getActivity()).inflate(R.layout.product_info_dialog_item, null);
        TextView summaryTextView = (TextView) item.findViewById(R.id.summary_textview);
        TextView detailTextView = (TextView) item.findViewById(R.id.detail_textview);

        summaryTextView.setText(product.installationInstructionTitle);
        detailTextView.setText(product.installationInstructionBody);

        ll.addView(item);

    }

    private void updateProductInfoContent() {

        Product product = mProductList.get(mCurrentPage);

        mProductInfoScrollView.removeAllViews();

        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(layoutParams);
        ll.setOrientation(LinearLayout.VERTICAL);
        mProductInfoScrollView.addView(ll);

        for(int i = 0; i < 3; i++)
        {
            View item = LayoutInflater.from(getActivity()).inflate(R.layout.product_info_dialog_item, null);
            TextView summaryTextView = (TextView) item.findViewById(R.id.summary_textview);
            TextView detailTextView = (TextView) item.findViewById(R.id.detail_textview);

            switch (i) {
                case 0: {
                    summaryTextView.setText("Building Element");
                    detailTextView.setText(product.mBuildingElement);
                    break;
                }
                case 1: {
                    summaryTextView.setText("Application");
                    detailTextView.setText(product.mApplication);
                    break;
                }
                case 2: {
                    summaryTextView.setText("Maximum size shitttttttttterwerwerwerwerwerewrwerewrewrwerewrweerew");
                    detailTextView.setText(product.mMaxSize);
                    break;
                }
            }

            ll.addView(item);

            if (i < 2) {
                int dp5 = (int) UIHelper.convertDpToPixel(5);
                View separator = new View(getActivity());
                layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(dp5 *2,dp5,dp5,dp5 *2);
                separator.setBackgroundColor(Color.DKGRAY);
                separator.setLayoutParams(layoutParams);
                ll.addView(separator);
            }
        }


    }


    public void setProductList(List<Product> productList) {
        mProductList = productList;
    }
}
