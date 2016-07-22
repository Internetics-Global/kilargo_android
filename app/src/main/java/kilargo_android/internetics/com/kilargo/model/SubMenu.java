package kilargo_android.internetics.com.kilargo.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by BourneWang on 27/04/2016.
 */
@Deprecated
public class SubMenu {

    public @Json(name = "subcategory_id")         int           mMenuID = 0;
    public @Json(name = "name_of_subcategory")    String        mName   = "submenu";
    public @Json(name = "parent_category")        String        mParent = "";
    public @Json(name = "products")               List<Product> mProducts;
}
