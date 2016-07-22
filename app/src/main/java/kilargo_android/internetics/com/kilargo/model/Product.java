package kilargo_android.internetics.com.kilargo.model;

import com.squareup.moshi.Json;

import org.parceler.Parcel;


/**
 * Created by BourneWang on 27/04/2016.
 */
@Parcel
public class Product {

    public @Json(name = "product_id")                int    mProductID = 0;
    public @Json(name = "product_name")              String mProductName = "";

    public @Json(name = "category_name")             String mCategory = "";
    public @Json(name = "subcategory_name")          String mSubcategory = "";

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
