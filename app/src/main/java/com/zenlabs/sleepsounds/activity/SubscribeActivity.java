package com.zenlabs.sleepsounds.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.iab.IabBroadcastReceiver;
import com.zenlabs.sleepsounds.iab.IabHelper;
import com.zenlabs.sleepsounds.iab.IabResult;
import com.zenlabs.sleepsounds.iab.Inventory;
import com.zenlabs.sleepsounds.iab.Purchase;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubscribeActivity extends Activity implements IabBroadcastReceiver.IabBroadcastListener {

    IabHelper mHelper;
    boolean mIsPremium = false;

    static final String SKU_PREMIUM = "unlock_forever_iap_item";

    final String TAG = "com.zenlabs.sleepsounds";

    static final String SKU_INFINITE_Sound_MONTHLY = "unlock_1month_iap_item";
    static final String SKU_INFINITE_Sound_3MONTHLY = "unlock_3months_iap_item";

    boolean mSubscribedToInfiniteSound = false;

    static final int RC_REQUEST = 10001;

    IabBroadcastReceiver mBroadcastReceiver;

    boolean isAvailable;

    @Override
    public void onCreate(Bundle savedInstanceState)    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subscription);

        isAvailable = false;
        
        TextView subscribe_title = (TextView) findViewById(R.id.subscribe_title);
        TextView subscribe_detail1 = (TextView) findViewById(R.id.subscribe_detail_1);
        TextView subscribe_detail2 = (TextView) findViewById(R.id.subscribe_detail_2);
        TextView subscribe_detail3 = (TextView) findViewById(R.id.subscribe_detail_3);
        TextView subscribe_detail4 = (TextView) findViewById(R.id.subscribe_detail_4);

        TextView unlock_all_price = (TextView) findViewById(R.id.all_unlock_price);
        TextView unlock_3month_price = (TextView) findViewById(R.id.month3_unlock_price);
        TextView unlock_1month_price = (TextView) findViewById(R.id.month1_unlock_price);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-BoldCond.otf");
        subscribe_title.setTypeface(custom_font);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Light.otf");
        subscribe_detail1.setTypeface(custom_font1);
        subscribe_detail2.setTypeface(custom_font1);
        subscribe_detail3.setTypeface(custom_font1);
        subscribe_detail4.setTypeface(custom_font1);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf");
        unlock_all_price.setTypeface(custom_font2);
        unlock_1month_price.setTypeface(custom_font2);
        unlock_3month_price.setTypeface(custom_font2);

        ImageView close_bt = (ImageView) findViewById(R.id.close_subscribe);

        ImageView buy_full_bt = (ImageView) findViewById(R.id.buy_full_bt);
        RelativeLayout buy_3months_bt = (RelativeLayout) findViewById(R.id.buy_3months_bt);
        RelativeLayout buy_1month_bt = (RelativeLayout) findViewById(R.id.buy_1month_bt);

        buy_full_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAvailable) {
                    buyAction(SKU_PREMIUM);
                }
            }
        });

        buy_1month_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAvailable)
                    buyAction(SKU_INFINITE_Sound_MONTHLY);
            }
        });

        buy_3months_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAvailable)
                    buyAction(SKU_INFINITE_Sound_3MONTHLY);
            }
        });

        close_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faildPaid();
            }
        });

        mHelper = new IabHelper(SubscribeActivity.this, UtilsValues.base64EncodedPublicKey_google);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    isAvailable = false;
                    return;
                }

                if (mHelper == null) return;

                Log.d(TAG, "Setup successful. Querying inventory.");
                mBroadcastReceiver = new IabBroadcastReceiver(SubscribeActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));

            if (mIsPremium) {
                try {
                    Common.currentIAPStatus = UtilsValues.USABLE_FOREVER;
                    mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUM), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                }
            }

            // Do we have the infinite gas plan?
            Purchase infinite3MonthPurchase = inventory.getPurchase(SKU_INFINITE_Sound_3MONTHLY);
            mSubscribedToInfiniteSound = (infinite3MonthPurchase != null &&
                    verifyDeveloperPayload(infinite3MonthPurchase));

            if (mSubscribedToInfiniteSound) {
                Common.currentIAPStatus = UtilsValues.USABLE_3MONTH;
                loadUnlockSounds();
            }

            Purchase infiniteMonthPurchase = inventory.getPurchase(SKU_INFINITE_Sound_MONTHLY);
            mSubscribedToInfiniteSound = (infinite3MonthPurchase != null &&
                    verifyDeveloperPayload(infiniteMonthPurchase));

            if (mSubscribedToInfiniteSound) {
                Common.currentIAPStatus = UtilsValues.USABLE_MONTH;
                loadUnlockSounds();
            }

            isAvailable = true;

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            if (mHelper == null) return;
            if (result.isSuccess()) {
                loadUnlockSounds();
            }
            else {
                Log.d(TAG, "Error while consuming: " + result);
                faildPaid();
            }
        }
    };

    private void faildPaid()    {

        UtilsMethods.saveBooleanInSharedPreferences(SubscribeActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false);
        Common.currentIAPStatus = UtilsValues.NON_PAID;
        Intent intent = new Intent(SubscribeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadUnlockSounds() {

        switch (Common.currentIAPStatus)    {

            case UtilsValues.USABLE_MONTH:
                UtilsMethods.saveStringInSharedPreferences(SubscribeActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, getDelay(1));
                break;
            case UtilsValues.USABLE_3MONTH:
                UtilsMethods.saveStringInSharedPreferences(SubscribeActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, getDelay(3));
                break;
            case UtilsValues.USABLE_FOREVER:
                UtilsMethods.saveStringInSharedPreferences(SubscribeActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, "forever");
                break;
        }

        UtilsMethods.saveBooleanInSharedPreferences(SubscribeActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_STATUS, true);

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

        Intent intent = new Intent(SubscribeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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

    private void buyAction(String sku)    {
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(SubscribeActivity.this, sku, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }

    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                complain("Error purchasing: " + result);
                faildPaid();
                return;
            }

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                faildPaid();
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                faildPaid();
                return;
            }

            Log.d(TAG, "Purchase successful.");

            purchase.getPurchaseState();
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                Common.currentIAPStatus = UtilsValues.USABLE_FOREVER;
                loadUnlockSounds();
            } else if (purchase.getSku().equals(SKU_INFINITE_Sound_MONTHLY))    {
                Common.currentIAPStatus = UtilsValues.USABLE_MONTH;
                loadUnlockSounds();
            }
            else if (purchase.getSku().equals(SKU_INFINITE_Sound_3MONTHLY)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Infinite gas subscription purchased.");
                alert("Thank you for subscribing to infinite gas!");
                mSubscribedToInfiniteSound = true;
                Common.currentIAPStatus = UtilsValues.USABLE_3MONTH;
                loadUnlockSounds();
            }
        }
    };

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(SubscribeActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    public void onBackPressed() {

    }
}