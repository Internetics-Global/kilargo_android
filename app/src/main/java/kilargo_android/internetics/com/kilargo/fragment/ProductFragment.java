package kilargo_android.internetics.com.kilargo.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
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
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.activity.CarouseActivity;
import kilargo_android.internetics.com.kilargo.adapter.KKImageScrollAdapter;
import kilargo_android.internetics.com.kilargo.model.Product;

/**
 * Created by BourneWang on 22/04/2016.
 */
public class ProductFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @Bind(R.id.search_textview)               TextView mSearchView;
    @Bind(R.id.viewpager)                     ViewPager mViewPager;
    @Bind(R.id.product_info_button)           Button    mProductInfoButton;

    @Bind(R.id.product_info_board_textview)   TextView  mProductInfoBoardTextView;
    @Bind(R.id.product_info_board_title_textview)   TextView  mProductInfoBoardTitleTextView;
    @Bind(R.id.product_info_board_appendix_textview)   TextView  mProductInfoBoardAppendixTextView;
    @Bind(R.id.product_info_board)            ViewGroup  mProductInfoBoard;
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

        mSearchView.setFocusable(false);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResultFragment newFragment = new SearchResultFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);

                transaction.addToBackStack("SearchResultFragment" + System.currentTimeMillis());
                transaction.commit();
            }
        });


        mProductInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotesViewVisibility();
                updateProductBoardContent();
            }
        });
        mProductInfoBgMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotesViewVisibility();
                updateProductBoardContent();
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


    }

    private void backButtonClicked() {

        getActivity().getSupportFragmentManager().popBackStack();

    }

    private void viewPagerItemClicked(int position) {

//        switchNotesViewVisibility();
//        updateProductBoardContent();

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

        mSearchView.clearFocus();
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

    private void switchNotesViewVisibility() {

        final float alphaVal = 0.9f;

        if (mProductInfoBoard.getAlpha() > 0) {
            mProductInfoBgMask.setVisibility(View.INVISIBLE);
            mProductInfoBoard.animate()
                    .alpha(0)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProductInfoBoard.setAlpha(0);
                        }
                    });
        } else {
            mProductInfoBgMask.setVisibility(View.VISIBLE);
            mProductInfoBoard.animate()
                    .alpha(alphaVal)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProductInfoBoard.setAlpha(alphaVal);
                        }
                    });
        }

    }

    private void updateProductBoardContent() {
        mProductInfoBoardTextView.setText(mProductList.get(mCurrentPage).mNotes);
        mProductInfoBoardTitleTextView.setText(mProductList.get(mCurrentPage).mProductName);
        mProductInfoBoardAppendixTextView.setText(mProductList.get(mCurrentPage).mBuildingElement);
    }


    public void setProductList(List<Product> productList) {
        mProductList = productList;
    }
}
