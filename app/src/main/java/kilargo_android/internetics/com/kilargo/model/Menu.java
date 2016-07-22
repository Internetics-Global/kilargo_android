package kilargo_android.internetics.com.kilargo.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by BourneWang on 27/04/2016.
 */
@Deprecated
public class Menu {

    public @Json(name = "category_id")        int           mMenuID = 0;
    public @Json(name = "name_of_category")   String        mName = "menu";
    public @Json(name = "subcategories")      List<SubMenu> mSubMenus;
}
