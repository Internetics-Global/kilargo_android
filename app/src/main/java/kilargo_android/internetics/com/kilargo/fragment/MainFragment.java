package kilargo_android.internetics.com.kilargo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
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
import java.util.concurrent.Callable;

import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.activity.MainActivity;
import kilargo_android.internetics.com.kilargo.adapter.KKCategoryListAdapter;
import kilargo_android.internetics.com.kilargo.model.Category;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.AppContext;
import kilargo_android.internetics.com.kilargo.util.Global;
import kilargo_android.internetics.com.kilargo.util.UIHelper;
import kilargo_android.internetics.com.kilargo.widget.AVLoadingIndicatorDialog;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.search_view)         SearchView mSearchView;
    @Bind(R.id.listview)            ListView   mListView;
    @Bind(R.id.refresh_button)      ImageButton mRefreshButton;
    @Bind(R.id.swipe_refresh_layout)SwipeRefreshLayout  mSwipeRefreshLayout;

    @Bind(R.id.back_textview)    TextView mBackTextView;

    private AVLoadingIndicatorDialog mAVLoadingIndicatorDialog;


    private KKCategoryListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container,false);
        ButterKnife.bind(this,view);

        setupView(view);

        return  view;

    }



    private void setupView(View baseView) {
        mAdapter = new KKCategoryListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });
        refreshList();

        setupSearch();

        mRefreshButton.setVisibility(View.VISIBLE);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButtonClicked();
            }
        });
//        increaseControlTouchArea(mRefreshButton,40);

        mBackTextView.setVisibility(View.VISIBLE);
        mBackTextView.setText("");
        Drawable img = getResources().getDrawable(R.drawable.left_setting );
        img.setBounds( 0, 0, 24, 24 );
        mBackTextView.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        mBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerButtonClicked();
            }
        });
        increaseControlTouchArea(mBackTextView,40);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });


    }


    private List<Product> mSearchResult;
    private PopupWindow   mSearchResultPopupWindow;
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

                mSearchResult = JsonFetcher.sharedFetcher().getProductsWithAnyKeyword(s);

                if (mSearchResult.size() ==0) {
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

                    String text = String.format("%s(System number = %s)",item.mProductName,item.mSystemNumber);
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

        Product selectedProduct = mSearchResult.get(index);


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

    private void refreshButtonClicked() {
        fetchData();
    }


    private void listItemClicked(int i) {

        List<Category> categories = JsonFetcher.sharedFetcher().getAllCategories();

        SubFragment newFragment = new SubFragment();
        newFragment.setParentCategoryID(categories.get(i).categoryID);

        FragmentTransaction transaction =  getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_pop_enter,
                R.anim.fragment_slide_pop_exit);
        transaction.replace(R.id.fragment_container, newFragment);

        transaction.addToBackStack("SubFragment" +System.currentTimeMillis());
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

        AppContext myApp = (AppContext)getActivity().getApplication();
        if (myApp.onceToken == false)
        {
            fetchData();
            drawerButtonClicked();
            myApp.onceToken = true;
        }



        Logger.d("onResume");

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy");
    }

    private void refreshList() {

        List<Category> categories = JsonFetcher.sharedFetcher().getAllCategories();

        mAdapter.setDataArrayList(categories);
        mAdapter.notifyDataSetInvalidated();

    }

    public void fetchData() {

        if (mSwipeRefreshLayout.isRefreshing() == false) {

            if (mAVLoadingIndicatorDialog == null) {
                mAVLoadingIndicatorDialog = new AVLoadingIndicatorDialog(getActivity());
            }

            mAVLoadingIndicatorDialog.setMessage("Loading...");
            mAVLoadingIndicatorDialog.show();


        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {


                String url = Global.productFeedURL;
                JsonFetcher.sharedFetcher().fetchAllFeed().setOnCompletionHandler(new JsonFetcher.OnCompletionHandler() {
                    @Override
                    public void responseJSON(final boolean result, String errorMessage) {

                        Task.call(new Callable<Object>() {
                            @Override
                            public String call() throws Exception {

                                //mProgressDialog.dismiss();

                                if (mAVLoadingIndicatorDialog != null) {
                                    mAVLoadingIndicatorDialog.cancel();
                                }

                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                                if (result == false) {

                                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Alert")
                                            .setContentText("Failed to fetch data from server, please try again")
                                            .show();
                                }

                                refreshList();


                                return null;
                            }
                        },Task.UI_THREAD_EXECUTOR);

                    }
                });

            }

        }, 1000); // 5000ms delay
    }




    private void drawerButtonClicked() {
        ((MainActivity)getActivity()).switchDrawer();
    }
}
