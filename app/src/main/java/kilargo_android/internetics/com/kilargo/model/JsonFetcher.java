package kilargo_android.internetics.com.kilargo.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.orhanobut.logger.Logger;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Task;
import kilargo_android.internetics.com.kilargo.util.AppContext;
import kilargo_android.internetics.com.kilargo.util.Global;

/**
 * Created by BourneWang on 27/04/2016.
 */






public class JsonFetcher {

    private static int     mSemphore = 0;
    private static boolean mIsError = false;
    private static String  mErrorMessage = "";

    private List<Product>      mProducts = new ArrayList<>();
    private List<Category>     mCategories = new ArrayList<>();
    private List<SubCategory>  mSubCategories = new ArrayList<>();

    private OnCompletionHandler onCompletionHandler;

    private volatile static JsonFetcher instance;


    public static JsonFetcher sharedFetcher() {
        if (instance == null) {
            synchronized (JsonFetcher.class) {
                if (instance == null) {
                    instance = new JsonFetcher();
                }
            }
        }
        return instance;
    }

    protected JsonFetcher() {
    }


    public JsonFetcher fetchAllFeed() {

        mSemphore = 0;
        mIsError = false;
        mErrorMessage = "";

        RequestQueue requestQueue = Volley.newRequestQueue(AppContext.getAppContext());

        //products feed
        {
            JsonArrayRequest jsonArrayRequestForProducts = new JsonArrayRequest(Global.productFeedURL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    String jsonStr = response.toString();
                    Logger.json(jsonStr);
                    boolean result = parseProductsJson(jsonStr);

                    if (result == false) {
                        mIsError = true;
                    }

                    mSemphore +=1;

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JsonFetcher", error.getMessage(), error);

                    mIsError = true;
                    mErrorMessage = error.getMessage();

                    mSemphore +=1;

                }
            });
            requestQueue.add(jsonArrayRequestForProducts);
        }

        //categories feed
        {
            JsonArrayRequest jsonArrayRequestForCategories = new JsonArrayRequest(Global.categoryFeedURL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    String jsonStr = response.toString();
                    Logger.json(jsonStr);
                    boolean result = parseCategoriesJson(jsonStr);

                    if (result == false) {
                        mIsError = true;
                    }

                    mSemphore +=1;

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JsonFetcher", error.getMessage(), error);

                    mIsError = true;
                    mErrorMessage = error.getMessage();

                    mSemphore +=1;

                }
            });
            requestQueue.add(jsonArrayRequestForCategories);
        }

        //subcategories feed
        {
            JsonArrayRequest jsonArrayRequestForSubCategories = new JsonArrayRequest(Global.subCategoryFeedURL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    String jsonStr = response.toString();
                    Logger.json(jsonStr);
                    boolean result = parseSubCategoriesJson(jsonStr);

                    if (result == false) {
                        mIsError = true;
                    }

                    mSemphore +=1;

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JsonFetcher", error.getMessage(), error);

                    mIsError = true;
                    mErrorMessage = error.getMessage();

                    mSemphore +=1;

                }
            });
            requestQueue.add(jsonArrayRequestForSubCategories);
        }

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                while (mSemphore != 3) {
                    Thread.sleep(10);
                }

                mSemphore = 0;

                if (onCompletionHandler != null) {
                    if (mIsError == false) {
                        onCompletionHandler.responseJSON(true,"Parse succeed");
                    } else {
                        onCompletionHandler.responseJSON(false,"Parse failed");
                    }
                }

                return null;
            }
        });

        return this;
    }

    private boolean parseSubCategoriesJson(String jsonStr) {

        Moshi moshi = new Moshi.Builder()
                .add(new String2IntAdapter())
                .build();

        Type listOfCardsType = Types.newParameterizedType(List.class, SubCategory.class);
        JsonAdapter<List<SubCategory>> jsonAdapter = moshi.adapter(listOfCardsType);

        try {
            mSubCategories = jsonAdapter.fromJson(jsonStr);
            System.out.println(mSubCategories);

            return true;

        } catch (IOException e) {
            mSubCategories = new ArrayList<>();
            e.printStackTrace();
            return false;
        }
    }

    private boolean parseCategoriesJson(String jsonStr) {

        Moshi moshi = new Moshi.Builder()
                .add(new String2IntAdapter())
                .build();
        Type listOfCardsType = Types.newParameterizedType(List.class, Category.class);
        JsonAdapter<List<Category>> jsonAdapter = moshi.adapter(listOfCardsType);


        try {
            mCategories = jsonAdapter.fromJson(jsonStr);
            System.out.println(mCategories);

            return true;

        } catch (IOException e) {
            mCategories = new ArrayList<>();
            e.printStackTrace();
            return false;
        }
    }


    private boolean parseProductsJson(String jsonStr) {

        Moshi moshi = new Moshi.Builder()
                .add(new String2ArrayAdapter())
                .add(new String2IntAdapter())
                .build();

        Type listOfCardsType = Types.newParameterizedType(List.class, Product.class);
        JsonAdapter<List<Product>> jsonAdapter = moshi.adapter(listOfCardsType);

        try {
            mProducts = jsonAdapter.fromJson(jsonStr);
            System.out.println(mProducts);

            return true;

        } catch (IOException e) {
            mProducts = new ArrayList<>();
            e.printStackTrace();
            return false;
        }
    }




    public void setOnCompletionHandler(OnCompletionHandler onCompletionHandler) {
        this.onCompletionHandler = onCompletionHandler;
    }


    public interface OnCompletionHandler {
        void responseJSON(boolean result, String errorMessage);
    }

    public List<Category> getAllCategories() {

        return mCategories;


    }

    public List<SubCategory> getAllSubCategories() {

        return mSubCategories;


    }

    public List<Product> getAllProducts() {

        return mProducts;


    }


    public List<SubCategory> getSubCategoryWithParent(final int categoryID) {

        ArrayList<SubCategory> filtedSubCategories = new ArrayList<>();
        for (SubCategory item: mSubCategories) {
            if (item.masterCategoryID == (categoryID)) {
                filtedSubCategories.add(item);
            }
        }

        return filtedSubCategories;

    }

    public List<Product> getProducts(@NonNull int categoryId, @NonNull int subCategoryId) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product item : mProducts) {

            List categoryList = Arrays.asList(item.categoryIDList);
            List subCategoryList = Arrays.asList(item.subcategoryIDList);

            if ((categoryList.contains(categoryId)) && (subCategoryList.contains(subCategoryId))) {
                filteredProducts.add(item);
            }
        }

        return filteredProducts;
    }

    /*
     * support product name, category, subcategory name
     */
    public List<Product> getProductsWithAnyKeyword(@NonNull  final String name) {

        ArrayList<Product> products = new ArrayList();

        if (Strings.isNullOrEmpty(name)) {
            return products;
        }

        Iterable<Product> iterableProducts = Iterables.filter(mProducts, new Predicate<Product>() {
            @Override
            public boolean apply(Product input) {
                if (input.mProductName.toLowerCase().contains( name.toLowerCase())){
                    return true;
                } else {
                    return false;
                }
            }
        });

        products = Lists.newArrayList(iterableProducts);

        return products;



    }

    public String getCategoryName(@NonNull int categoryID) {

        for (Category item : mCategories) {
            if (item.categoryID == categoryID) {
                return item.categoryName;
            }
        }

        return "";

    }

    public String getSubCategoryName(@NonNull  int subCategoryID) {

        for (SubCategory item : mSubCategories) {
            if (item.subcategoryID == (subCategoryID)) {
                return item.subcategoryName;
            }
        }

        return "";

    }

    public String getMasterCategoryNameFromSubCategoryID(@NonNull int subCategoryID) {

        for (SubCategory item : mSubCategories) {
            if (item.subcategoryID == (subCategoryID)) {
                return getCategoryName(item.masterCategoryID);
            }
        }

        return "";

    }


}
