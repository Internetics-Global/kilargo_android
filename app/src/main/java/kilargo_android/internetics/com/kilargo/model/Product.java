package kilargo_android.internetics.com.kilargo.model;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.ToJson;

import org.parceler.Parcel;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by BourneWang on 27/04/2016.
 */
@Parcel
public class Product {

    public @Json(name = "product_id")                @String2Int  int    mProductID = 0;
    public @Json(name = "product_name")              String mProductName = "";

    public @Json(name = "category_name")             @String2Array Integer[] categoryIDList = {};
    public @Json(name = "subcategory_name")          @String2Array Integer[] subcategoryIDList = {};

    public  @Json(name = "system_number")            String mSystemNumber = "";
    public @Json(name = "building_element")          String mBuildingElement = "";
    public @Json(name = "application")               String mApplication = "";
    public @Json(name = "maximum_size")              String mMaxSize = "";
    public @Json(name = "FRL")                       String mFRL = "";
    public @Json(name = "test_reference_no")         String mTestReferenceNumber = "";

    public @Json(name = "product_image")             String productImage = "";
    public @Json(name = "image_1")                   String mImage1 = "";
    public @Json(name = "image_2")                   String mImage2 = "";
    public @Json(name = "image_3")                   String mImage3 = "";
    public @Json(name = "image_4")                   String mImage4 = "";
    public @Json(name = "image_5")                   String mImage5 = "";

    public @Json(name = "installation_instructions_title") String installationInstructionTitle = "";
    public @Json(name = "installation_instructions_body")  String installationInstructionBody = "";

    public @Json(name = "notes")                     String mNotes = "";
}

@Retention(RUNTIME)
@JsonQualifier
@interface String2Array {
}

class String2ArrayAdapter {
    @FromJson
    @String2Array Integer[] fromJson(String str) {

        List<String> list = Arrays.asList(str.split(","));
        Collection<String> collection = Collections2.transform(list, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.replace(" ","");
            }
        });

        Integer[] array = new Integer[collection.size()];
        int i = 0;
        for (String item:collection) {
            array[i] = Integer.parseInt(item);
            i += 1;
        }

        return array;
    }

    @ToJson
    String toJson(@String2Array Integer[] value) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <value.length; i++) {
            builder.append(value[i]);
            if (i != value.length - 1) {
                builder.append(",");
            }
        }

        return builder.toString();

    }
}
