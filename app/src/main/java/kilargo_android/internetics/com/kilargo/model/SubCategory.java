package kilargo_android.internetics.com.kilargo.model;

import com.squareup.moshi.Json;

import org.parceler.Parcel;


/**
 * Created by BourneWang on 27/04/2016.
 */
@Parcel
public class SubCategory {

    public @Json(name = "subcategory_id")                @String2Int    int       subcategoryID    = 0;
    public @Json(name = "subcategory_name")                             String    subcategoryName  = "";
    public @Json(name = "master_category")               @String2Int    int       masterCategoryID = 0;
}


