package kilargo_android.internetics.com.kilargo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.adapter.SearchAdapter;
import kilargo_android.internetics.com.kilargo.model.JsonFetcher;
import kilargo_android.internetics.com.kilargo.model.Product;
import kilargo_android.internetics.com.kilargo.util.FragmentUtils;

/**
 * Created by BourneWang on 28/04/2016.
 */
public class SearchResultFragment extends BaseFragment {

    @Bind(R.id.searchView)       SearchView mSearchView;
    @Bind(R.id.cancel_button)    Button     mCancelButton;
    @Bind(R.id.listview)         ListView   mListView;

    private SearchAdapter        mAdapter;

    private List<Product> mSearchResults = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_search_result, container,false);
        ButterKnife.bind(this,view);

        setupView(view);

        return  view;

    }

    private void setupView(View view) {

        mAdapter = new SearchAdapter(getActivity());
        mAdapter.setDataArrayList(mSearchResults);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonClicked();
            }
        });

        mSearchView.setFocusable(true);
        mSearchView.requestFocusFromTouch();
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                mSearchResults = JsonFetcher.sharedFetcher().getProductsWithProductName(s);

                mAdapter.setDataArrayList(mSearchResults);
                mAdapter.notifyDataSetInvalidated();



                return false;
            }
        });


    }

    private void cancelButtonClicked() {

        mSearchView.clearFocus();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        FragmentUtils.sDisableFragmentAnimations = true;  //don't allow animation
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        FragmentUtils.sDisableFragmentAnimations = false;
    }

    private void listItemClicked(int i) {

        mSearchView.clearFocus();

        Product selectedProduct = mSearchResults.get(i);

        ProductFragment newFragment = new ProductFragment();
        newFragment.setProductList(Arrays.asList(selectedProduct));
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.disallowAddToBackStack();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                0,
                0);
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();

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

}
