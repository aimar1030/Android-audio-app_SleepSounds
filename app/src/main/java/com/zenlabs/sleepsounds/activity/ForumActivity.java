package com.zenlabs.sleepsounds.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.utils.LogService;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import me.kiip.sdk.Kiip;
import me.kiip.sdk.Poptart;

/**
 * Created by fedoro on 5/31/16.
 */
public class ForumActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forum);

        final WebView forumWeb = (WebView) findViewById(R.id.forumWeb);
        forumWeb.getSettings().setJavaScriptEnabled(true);
        forumWeb.loadUrl("http://forums.zenlabsfitness.com");
        forumWeb.setWebViewClient(new WebViewClient());

        ImageView home, back, forward, refresh;
        home = (ImageView) findViewById(R.id.home);
//        home.setImageResource(SkinsManager.getHomeDrawable());
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                forumWeb.loadUrl("http://forums.zenlabsfitness.com");
            }
        });

        back = (ImageView) findViewById(R.id.back);
//        back.setImageResource(SkinsManager.getBackwardDrawable());
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (forumWeb.canGoBack()) {
                    forumWeb.goBack();
                }
            }
        });

        forward = (ImageView) findViewById(R.id.forward);
//        forward.setImageResource(SkinsManager.getForwardDrawable());
        forward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (forumWeb.canGoForward()) {
                    forumWeb.goForward();
                }
            }
        });

        refresh = (ImageView) findViewById(R.id.refresh);
//        refresh.setImageResource(SkinsManager.getReloadDrawable());
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                forumWeb.reload();
            }
        });

        ImageView backToSettings = (ImageView) findViewById(R.id.imageview_back_to_settings);
        backToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showKiip();
    }

    private void showKiip() {

        if(UtilsMethods.getBooleanFromSharedPreferences(ForumActivity.this, UtilsValues.SHARED_PREFERENCES_KIIP_REWARDS)){
            Kiip.getInstance().saveMoment(UtilsValues.KIIP_MOMENT_WORKOUT_COMPLETED, new Kiip.Callback() {
                @Override
                public void onFinished(Kiip kiip, Poptart reward) {
                    if (reward == null) {
                        LogService.Log("kiip_fragment_tag", "Successful moment but no reward to give.");
//                        Toast.makeText(ForumActivity.this, "Kiip: Successful moment but no reward to give.", Toast.LENGTH_LONG).show();
                    } else {
                        onPoptart(reward);
                    }
                }

                @Override
                public void onFailed(Kiip kiip, Exception exception) {
                    LogService.Log("kiip_fragment_tag", "onFailed ex: " + exception.toString());
                }
            });
        }
    }
}
