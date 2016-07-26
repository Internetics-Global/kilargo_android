package kilargo_android.internetics.com.kilargo.activity;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;

import kilargo_android.internetics.com.kilargo.util.AppContext;

/**
 * Created by BourneWang on 5/05/2016.
 */
public class BaseActivity extends FragmentActivity {

    @Override
    protected void onResume()
    {
        super.onResume();

        AppContext myApp = (AppContext)this.getApplication();
        if (myApp.wasInBackground)
        {
            //Do specific came-here-from-background code
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ((AppContext)this.getApplication()).startActivityTransitionTimer();
    }


}
