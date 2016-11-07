package kilargo_android.internetics.com.kilargo.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.ToJson;

import org.parceler.Parcel;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by BourneWang on 27/04/2016.
 */
@Parcel
public class Category {

    public @Json(name = "category_id")          @String2Int int    categoryID = 0;
    public @Json(name = "category_name")                    String    categoryName = "";

}

@Retention(RUNTIME)
@JsonQualifier
@interface String2Int {
}

class String2IntAdapter {
    @FromJson @String2Int int fromJson(String str) {
        return Integer.parseInt(str);
    }

    @ToJson String toJson(@String2Int int value) {
        return String.format("%d", value);
    }
}
