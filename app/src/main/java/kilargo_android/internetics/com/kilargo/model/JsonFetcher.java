package kilargo_android.internetics.com.kilargo.model;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kilargo_android.internetics.com.kilargo.util.AppContext;

/**
 * Created by BourneWang on 27/04/2016.
 */






public class JsonFetcher {

    private List<Product>  mProducts = new ArrayList<>();
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

    public JsonFetcher fetchMenu(String urlStr) {

        RequestQueue requestQueue = Volley.newRequestQueue(AppContext.getAppContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlStr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String jsonStr = response.toString();
                Logger.json(jsonStr);
                boolean result = parseJson(jsonStr);
                if (onCompletionHandler != null) {
                    if (result) {
                        onCompletionHandler.responseJSON(true,"Parse succeed");
                    } else {
                        onCompletionHandler.responseJSON(false,"Parse failed");
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JsonFetcher", error.getMessage(), error);
                if (onCompletionHandler != null) {
                    onCompletionHandler.responseJSON(false,error.getMessage());
                }

            }
        });
        requestQueue.add(jsonArrayRequest);

        return this;
    }

    private boolean parseJson(String jsonStr) {

        Moshi moshi = new Moshi.Builder().build();

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

    public List<Product> getAllProducts() {

        return mProducts;


    }

    public List<String> getCategory() {

        ArrayList<String> categories = new ArrayList<>();
        for (Product item : mProducts) {
            categories.add(item.mCategory);
        }

        Set<String> mySet = new HashSet<>(categories);  //remove duplicated

        return new ArrayList<>(mySet);

    }

    public List<String> getSubcategoryWithParenent(final String categoryName) {

        ArrayList<String> subcategories = new ArrayList<>();
        for (Product item: mProducts) {
            if (item.mCategory.toLowerCase().equals(categoryName.toLowerCase())) {
                subcategories.add(item.mSubcategory);
            }
        }

        Set<String> mySet = new HashSet<>(subcategories);  //remove duplicated

        return new ArrayList<>(mySet);

    }


    public List<Product> getProductsWithProductName(final String productName) {

        ArrayList<Product> products = new ArrayList();

        if (Strings.isNullOrEmpty(productName)) {
            return products;
        }

        Iterable<Product> iterableProducts = Iterables.filter(mProducts, new Predicate<Product>() {
            @Override
            public boolean apply(Product input) {
                if (input.mProductName.toLowerCase().contains( productName.toLowerCase())) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        products = Lists.newArrayList(iterableProducts);

        return products;



    }


    public List<Product> getProductsWithSubcategoryName(final String subcategoryName) {

        ArrayList<Product> products = new ArrayList();

        if (Strings.isNullOrEmpty(subcategoryName)) {
            return products;
        }

        Iterable<Product> iterableProducts = Iterables.filter(mProducts, new Predicate<Product>() {
            @Override
            public boolean apply(Product input) {
                if (input.mSubcategory.toLowerCase().equals(subcategoryName.toLowerCase())) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        products = Lists.newArrayList(iterableProducts);

        return products;


    }


}
