package kilargo_android.internetics.com.kilargo.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
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
import kilargo_android.internetics.com.kilargo.fragment.HomeFragment;
import kilargo_android.internetics.com.kilargo.fragment.MainFragment;
import kilargo_android.internetics.com.kilargo.fragment.MoreFragment;
import kilargo_android.internetics.com.kilargo.fragment.SettingFragment;
import kilargo_android.internetics.com.kilargo.util.AppContext;

public class MainActivity extends BaseActivity implements android.support.v4.app.FragmentManager.OnBackStackChangedListener{

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
            final android.support.v4.app.Fragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().disallowAddToBackStack()
                    .add(R.id.fragment_container, mainFragment,"MainFragment").commit();

            final MoreFragment moreFragment = new MoreFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.left_drawer, moreFragment).commit();
        }


        getSupportFragmentManager().addOnBackStackChangedListener(this);

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
        if(getSupportFragmentManager().getBackStackEntryCount() >= 0) {
            getSupportFragmentManager().popBackStack();
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

        getSupportFragmentManager().popBackStack("MainFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newFragment = new SettingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("SettingFragment" + System.currentTimeMillis());
        transaction.commit();


    }


    public void aboutButtonClicked() {

        closeDrawer();

        getSupportFragmentManager().popBackStack("MainFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newFragment = new AboutFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("AboutFragment" + System.currentTimeMillis());
        transaction.commit();

    }

    public void aboutAppButtonClicked() {

        closeDrawer();

        getSupportFragmentManager().popBackStack("HomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("HomeFragment" + System.currentTimeMillis());
        transaction.commit();

    }


    public void productsButtonClicked() {

        closeDrawer();
    }

    /*
     * 发现实际中,back stack会有问题,不知道是否是系统的bug,所以需要如下额外的逻辑进行处理.
     */
    static int     LAST_STACK_ENTRY_COUNT = -1;
    static String  LAST_TOP_STACK_FRAGMENT_NAME = "";
    @Override
    public void onBackStackChanged() {

        Logger.d("onBackStackChanged");

        FragmentManager fm = getSupportFragmentManager();

        if (LAST_STACK_ENTRY_COUNT <= fm.getBackStackEntryCount()) {
            LAST_STACK_ENTRY_COUNT =  fm.getBackStackEntryCount();

            if (fm.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry backEntry=fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1);
                String str=backEntry.getName();
                LAST_TOP_STACK_FRAGMENT_NAME = str;
            }

            return;

        } else {

            //由于onBackStackChanged 是在stack内容变化时被回调,而我们只是希望在back时才执行.

            LAST_STACK_ENTRY_COUNT =  fm.getBackStackEntryCount();


            if (fm.getBackStackEntryCount() == 0) {
                final Fragment mainFragment = new MainFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (LAST_TOP_STACK_FRAGMENT_NAME.contains("SearchResultFragment")) {
                    //in this case, we don't want the animation of slide out, since we try to simulate modal effect
                    transaction.setCustomAnimations(0,
                            0,
                            0,
                            0);
                } else {
                }
                transaction.replace(R.id.fragment_container, mainFragment);
                transaction.commit();



            } else {

                FragmentManager.BackStackEntry backEntry=fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1);
                String str=backEntry.getName();

                if (str.contains("SearchResultFragment")) {  //实际中,我们发现虽然我们已经disable add to stack,SearchResultFragment仍旧会出现
                    fm.popBackStack();
                }
            }

        }


    }


}
