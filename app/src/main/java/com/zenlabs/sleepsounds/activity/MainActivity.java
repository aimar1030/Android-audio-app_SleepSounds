package com.zenlabs.sleepsounds.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vungle.publisher.VunglePub;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.adapter.NavDrawerListAdapter;
import com.zenlabs.sleepsounds.customview.TipsDialog;
import com.zenlabs.sleepsounds.fragment.AddFavoriteFragment;
import com.zenlabs.sleepsounds.fragment.AlarmTimerFragment;
import com.zenlabs.sleepsounds.fragment.AlarmViewFragment;
import com.zenlabs.sleepsounds.fragment.ClockFragment;
import com.zenlabs.sleepsounds.fragment.FadeInFragment;
import com.zenlabs.sleepsounds.fragment.FavoritFragment;
import com.zenlabs.sleepsounds.fragment.HomeFragment;
import com.zenlabs.sleepsounds.fragment.MenuFragment;
import com.zenlabs.sleepsounds.fragment.SoundSelectFragment;
import com.zenlabs.sleepsounds.model.App;
import com.zenlabs.sleepsounds.model.NavDrawerItem;
import com.zenlabs.sleepsounds.model.ProductModel;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.LogService;
import com.zenlabs.sleepsounds.utils.Utils;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by fedoro on 5/12/16.
 */
public class MainActivity extends FragmentActivity {

    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    public MainActivity mActivity;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    public static com.nostra13.universalimageloader.core.ImageLoader imageLoader;

    public HomeFragment homeFragment = new HomeFragment();

    AlarmTimerFragment alarmTimerFragment;

    Fragment currentPlayedFragment;

    final VunglePub vunglePub = VunglePub.getInstance();

    Bundle mSavedInstanceState;

    public static final int REQUEST_CODE = 1;

    BillingProcessor bp;

    static final String SKU_PREMIUM = "unlock_forever_iap_item";

    final String TAG = "com.zenlabs.sleepsounds";

    static final String SKU_INFINITE_Sound_MONTHLY = "unlock_1month_iap_item";
    static final String SKU_INFINITE_Sound_3MONTHLY = "unlock_3months_iap_item";

    @Override
    public void onCreate(Bundle savedInstanceState)    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSavedInstanceState = savedInstanceState;

        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        bp = new BillingProcessor(this, UtilsValues.base64EncodedPublicKey_google, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
            }

            @Override
            public void onPurchaseHistoryRestored() {

                ArrayList<ProductModel> products = new ArrayList<>();

                for(String sku : bp.listOwnedProducts()) {
                    Log.d(TAG, "Owned Managed Product: " + sku);

                    if (sku != null) {
                        ProductModel model = new ProductModel();
                        TransactionDetails details = bp.getPurchaseTransactionDetails(sku);
                        model.product_time = details.purchaseTime;
                        model.sku = sku;
                        products.add(model);
                    }
                }
                for(String sku : bp.listOwnedSubscriptions()) {
                    Log.d(TAG, "Owned Subscription: " + sku);

                    if (sku != null) {
                        ProductModel model = new ProductModel();
                        TransactionDetails details = bp.getPurchaseTransactionDetails(sku);
                        model.product_time = details.purchaseTime;
                        model.sku = sku;
                        products.add(model);
                    }
                }

                ProductModel model = products.get(0);

                if (products.size() > 2) {
                    for (int i = 1; i < products.size(); i++) {
                        ProductModel temp = products.get(i);
                        if (UtilsMethods.compareDate(model.product_time, temp.product_time))
                            model = temp;
                    }
                }

                if (model.sku.equals(SKU_PREMIUM))  {
                    Common.currentIAPStatus = UtilsValues.USABLE_FOREVER;
                } else if (model.sku.equals(SKU_INFINITE_Sound_3MONTHLY))   {
                    Common.currentIAPStatus = UtilsValues.USABLE_3MONTH;
                } else if (model.sku.equals(SKU_INFINITE_Sound_MONTHLY))    {
                    Common.currentIAPStatus = UtilsValues.USABLE_MONTH;
                }

                loadUnlockSounds();
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                if (currentPlayedFragment instanceof HomeFragment)  {
                    ((HomeFragment) currentPlayedFragment).playVideoView();
                }
            }

            @Override
            public void onBillingInitialized() {

            }
        });

        initView(mSavedInstanceState);

        if (!Common.isStart)
            shoStartPopUp();
    }

    public void restorePurchase()   {

        if (currentPlayedFragment instanceof HomeFragment)  {
            ((HomeFragment) currentPlayedFragment).stopVideoView();
        }

//        Common.currentIAPStatus = UtilsValues.USABLE_MONTH;
//        loadUnlockSounds();

        bp.loadOwnedPurchasesFromGoogle();
    }

    private void loadUnlockSounds() {

        switch (Common.currentIAPStatus)    {

            case UtilsValues.USABLE_MONTH:
                UtilsMethods.saveStringInSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, getDelay(1));
                break;
            case UtilsValues.USABLE_3MONTH:
                UtilsMethods.saveStringInSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, getDelay(3));
                break;
            case UtilsValues.USABLE_FOREVER:
                UtilsMethods.saveStringInSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, "forever");
                break;
        }

        UtilsMethods.saveBooleanInSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_STATUS, true);

        for (int i = 0; i < Common.lockSounds.size(); i ++) {
            Common.lockSounds.get(i).isUsable = true;
            Common.unlockSounds.add(Common.lockSounds.get(i));
        }

        if (Common.videoSounds.size() > 0)
            for (int i = 0; i < Common.videoSounds.size(); i ++) {
                Common.videoSounds.get(i).isUsable = true;
                Common.unlockSounds.add(Common.videoSounds.get(i));
            }

        Common.videoSounds = new ArrayList<>();
        Common.lockSounds = new ArrayList<>();

        if (currentPlayedFragment instanceof HomeFragment)  {
            ((HomeFragment) currentPlayedFragment).reloadAllViews();
        }
    }

    private String getDelay(int delay)  {

        int currentMonth = UtilsMethods.getMonth();
        Date expiredDate = UtilsMethods.getDateWithMonth(currentMonth + delay);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expiredDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "," + month + "," + day;
    }

    private void shoStartPopUp() {

        if (UtilsMethods.getBooleanFromSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_INTRO_SCREEN, true)) {
            UtilsMethods.saveBooleanInSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_INTRO_SCREEN, false);
            UtilsMethods.showTipScreenDialog(MainActivity.this);
        } else {
            checkForShowTips();
        }
    }

    private void checkForShowTips() {

        if (UtilsMethods.getBooleanFromSharedPreferences(MainActivity.this, UtilsValues.SHARED_PREFERENCES_TIPS_SCREEN)) {

            String quote = "";
            ArrayList<App> apps;

            final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFS), 1);
            quote = preferences.getString("quote", "");
            Log.d("QUOTE", " original::" + quote);
            Gson tmp = new Gson();
            Type arrayListType = new TypeToken<ArrayList<App>>() {
            }.getType();
            apps = tmp.fromJson(preferences.getString("apps", ""), arrayListType);

            LogService.Log("checkForShowTips", "apps: " + apps);

            FragmentManager fm = getSupportFragmentManager();
            TipsDialog tipsDialog = new TipsDialog();
            tipsDialog.setQuote(quote);
            tipsDialog.setApps(apps);
            tipsDialog.setStyle(DialogFragment.STYLE_NORMAL,
                    R.style.Dialog_TipScreenDialog);
            tipsDialog.show(fm, "fragment_tips");
        }
    }

    private void initView(Bundle savedInstanceState) {

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], true, "50+"));

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);

        // enabling action bar app icon and behaving it as toggle button


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null)
            displayView(0);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = homeFragment;
                currentPlayedFragment = fragment;
                break;
            case 1:
                fragment = new AlarmTimerFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {

            final FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (fragmentManager != null)    {
                        Fragment currentFragment = (Fragment)fragmentManager.findFragmentById(R.id.frame_container);
                        currentFragment.onResume();

                        currentPlayedFragment = currentFragment;
                    }
                }
            });

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            FrameLayout favorit_frame = (FrameLayout) findViewById(R.id.frame_favorit);

            if (UtilsMethods.isTablet(this))
            {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams)favorit_frame.getLayoutParams();
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels - 200;

                params.width = width;

                favorit_frame.setLayoutParams(params);
            }
            fragmentManager.beginTransaction().replace(R.id.frame_favorit, new FavoritFragment()).commit();
            fragmentManager.beginTransaction().replace(R.id.frame_menu, new MenuFragment()).commit();
            // update selected item and title, then close the drawer
            setTitle(navMenuTitles[position]);
//            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void openFavorit()   {
        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    public void openMenu()  {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void showAlertTimerView()    {

        alarmTimerFragment = new AlarmTimerFragment();
        mDrawerLayout.closeDrawers();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, alarmTimerFragment);
        ft.addToBackStack("Home");
        ft.commit();
    }

    public void backAction()    {
        getSupportFragmentManager().popBackStack();
    }

    public void showSetTimerview()  {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, new AlarmViewFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void backHomeView()  {

        getSupportFragmentManager().popBackStack("Home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void backAlarmTimerView()    {

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.frame_container, new AlarmTimerFragment());
//        ft.commit();
        backAction();
    }

    public void backSetTimerView()  {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.frame_container, new AlarmViewFragment());
//        ft.commit();
        backAction();
    }

    public void showFadeInView()    {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, new FadeInFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void showwSoundView()    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, new SoundSelectFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void showAddFavoriteView()   {

        mDrawerLayout.closeDrawers();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, new AddFavoriteFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void backFromAddFavoriteView()   {

        backAction();
        Fragment currentFragment = (Fragment)getSupportFragmentManager().findFragmentById(R.id.frame_favorit);
        currentFragment.onResume();
        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    public void showSubscribe() {
        mDrawerLayout.closeDrawers();

        Intent intent = new Intent(MainActivity.this,
                SubscribeActivity.class);
        startActivity(intent);

        finish();
    }

    public void showClockView() {

        mDrawerLayout.closeDrawers();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, new ClockFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void showInfoScreen()    {
        mDrawerLayout.closeDrawers();

        if (currentPlayedFragment instanceof HomeFragment)  {
            ((HomeFragment) currentPlayedFragment).showTipScreen();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPlayedFragment instanceof HomeFragment)  {
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentPlayedFragment instanceof HomeFragment)
            vunglePub.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentPlayedFragment instanceof HomeFragment)
            vunglePub.onResume();
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }
}
