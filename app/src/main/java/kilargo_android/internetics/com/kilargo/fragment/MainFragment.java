package kilargo_android.internetics.com.kilargo.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.activity.MainActivity;
import kilargo_android.internetics.com.kilargo.adapter.KKListAdapter;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.Global;
import kilargo_android.internetics.com.kilargo.widget.AVLoadingIndicatorDialog;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.search_view)         SearchView mSearchView;
    @Bind(R.id.listview)            ListView   mListView;
    @Bind(R.id.refresh_button)      Button     mRefreshButton;
    @Bind(R.id.swipe_refresh_layout)SwipeRefreshLayout  mSwipeRefreshLayout;

    @Bind(R.id.back_textview)    TextView mBackTextView;

    private AVLoadingIndicatorDialog mAVLoadingIndicatorDialog;


    private KKListAdapter mAdapter;

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
        mAdapter = new KKListAdapter(getActivity());
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

        mBackTextView.setVisibility(View.VISIBLE);
        mBackTextView.setText("");
        Drawable img = getResources().getDrawable(R.drawable.left_setting );
        img.setBounds( 0, 0, 24, 24 );
        mBackTextView.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        mBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerButtonClicked(view);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });


    }


    private List<Product> searchResult;
    private void setupSearch() {

        final DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(getActivity(), mSearchView);

        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {

                mSearchView.clearFocus();

                Product selectedProduct = searchResult.get(id);

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
        });

        final DroppyMenuPopup droppyMenu = droppyBuilder.build();

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                droppyMenu.dismiss(false);
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

                searchResult = JsonFetcher.sharedFetcher().getProductsWithAnyKeyword(s);
                if (searchResult.size() >0) {


                    int index = 0;
                    for (Product item : searchResult) {

                        if (index == searchResult.size() - 1) {
                            droppyBuilder.addMenuItem(new DroppyMenuItem(item.mProductName));
                        } else {
                            droppyBuilder.addMenuItem(new DroppyMenuItem(item.mProductName))
                                    .addSeparator();
                        }

                    }

                    droppyMenu.show();
                } else {
//                    droppyMenu.hideAnimationCompleted(false);
                }

                return false;
            }
        });
    }

    private void refreshButtonClicked() {
        fetchData();
    }


    private void showProductDetails(int i) {

        ProductFragment newFragment = new ProductFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_pop_enter,
                R.anim.fragment_slide_pop_exit);
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("ProductFragment" + System.currentTimeMillis());
        transaction.commit();


    }

    private void listItemClicked(int i) {

        List<String> categories = JsonFetcher.sharedFetcher().getCategory();

        SubFragment newFragment = new SubFragment();
        newFragment.setParentCategoryName(categories.get(i));

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

    }

    @Override
    public void onPause() {
        super.onPause();

//        mSearchView.clearFocus();
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

        List<String> categores = JsonFetcher.sharedFetcher().getCategory();

        mAdapter.setDataArrayList(categores);
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


                        String url = Global.feedURL;
                        JsonFetcher.sharedFetcher().fetchMenu(url).setOnCompletionHandler(new JsonFetcher.OnCompletionHandler() {
                            @Override
                            public void responseJSON(boolean result, String errorMessage) {

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

                            }
                        });

                    }

                }, 1000); // 5000ms delay
    }




    private void drawerButtonClicked(View view) {
        ((MainActivity)getActivity()).switchDrawer();
    }
}
