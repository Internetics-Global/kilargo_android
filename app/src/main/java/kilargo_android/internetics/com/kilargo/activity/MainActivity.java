package kilargo_android.internetics.com.kilargo.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kilargo_android.internetics.com.kilargo.R;
import kilargo_android.internetics.com.kilargo.fragment.AboutFragment;
import kilargo_android.internetics.com.kilargo.fragment.MainFragment;
import kilargo_android.internetics.com.kilargo.fragment.MoreFragment;
import kilargo_android.internetics.com.kilargo.fragment.SettingFragment;
import kilargo_android.internetics.com.kilargo.util.AppContext;

public class MainActivity extends BaseActivity {

    /*
     * Network monitor (reachability)
     */
    private BusWrapper    busWrapper;
    private NetworkEvents  networkEvents;

    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.left_drawer)   FrameLayout mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.d("onCreate");

        final EventBus bus = new EventBus();
        busWrapper = getGreenRobotBusWrapper(bus);
        networkEvents = new NetworkEvents(getApplicationContext(), busWrapper);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            final Fragment mainFragment = new MainFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mainFragment,"MainFragment").commit();

            final MoreFragment moreFragment = new MoreFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.left_drawer, moreFragment).commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Logger.d("onStart");

        busWrapper.register(this);
        networkEvents.register();

    }

    @Override
    protected void onStop() {

        Logger.d("onStop");

        busWrapper.unregister(this);
        networkEvents.unregister();

        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();

        AppContext myApp = (AppContext)this.getApplication();
        if (myApp.wasInBackground == false)
        {
            FragmentManager fragmentManager = getFragmentManager();
            String targetFragmentTag = "MainFragment";
            MainFragment myFragment = (MainFragment) fragmentManager.findFragmentByTag(targetFragmentTag);
            if (myFragment != null) {

                int count = fragmentManager.getBackStackEntryCount();

                if (count == 0) {  // no back,
                    myFragment.fetchData();
                } else {
                    //ideally, there's no this case. However, if there's a memory leak which could not free MainFragment, it does exist
                    String fragmentTag = fragmentManager.getBackStackEntryAt(count - 1).getName();
                    if (myFragment != null && targetFragmentTag.equals(fragmentTag)) {
                        myFragment.fetchData();
                    }
                }

            }


        }

        Logger.d("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Logger.d("onPause");
    }

    @SuppressWarnings("unused")
    @Subscribe  public void onEvent(ConnectivityChanged event) {

        Logger.d("ConnectivityChanged:" + event.getConnectivityStatus());

        if (event.getConnectivityStatus() == ConnectivityStatus.OFFLINE ||
                event.getConnectivityStatus() == ConnectivityStatus.UNKNOWN) {

            new SweetAlertDialog(this)
                .setTitleText("Alert")
                .setContentText("No internet connection")
                .show();
        }


    }


    //http://stackoverflow.com/questions/23728216/back-button-closing-app-even-when-using-fragmenttransaction-addtobackstack
    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() >= 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @NonNull private BusWrapper getGreenRobotBusWrapper(final EventBus bus) {
        return new BusWrapper() {
            @Override public void register(Object object) {
                bus.register(object);
            }

            @Override public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override public void post(Object event) {
                bus.post(event);
            }
        };
    }


    public void switchDrawer() {

        if(isDrawerOpen()) {
            closeDrawer();
        }else {
            openDrawer();
        }

    }

    public void closeDrawer() {
        if (isDrawerOpen()) {
            mDrawerLayout.closeDrawer(mDrawerList);

        }

    }


    public void openDrawer() {
        if (isDrawerOpen() == false) {
            mDrawerLayout.openDrawer(mDrawerList);


        }

    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawerList);
    }


    public void settingButtonClicked() {

        closeDrawer();

        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newFragment = new SettingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("AboutFragment");
        transaction.commit();


    }


    public void aboutButtonClicked() {

        closeDrawer();

        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newFragment = new AboutFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("AboutFragment");
        transaction.commit();

    }

}
