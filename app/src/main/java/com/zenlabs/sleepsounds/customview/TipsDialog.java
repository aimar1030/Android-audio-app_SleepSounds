package com.zenlabs.sleepsounds.customview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.adapter.ViewPagerAdapter;
import com.zenlabs.sleepsounds.model.App;
import com.zenlabs.sleepsounds.utils.GetResponse;
import com.zenlabs.sleepsounds.utils.LogService;

import java.util.ArrayList;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by fedoro on 5/12/16.
 */
public class TipsDialog extends DialogFragment {

    private SharedPreferences preferences;
    private LinearLayout registerLayout, appsLayout;

    private String quote;
    private ArrayList<App> apps = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        apps = new ArrayList<>();
        preferences = getActivity().getSharedPreferences("Preferences",
                Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.tips_dialog, container,
                false);

        TextView quoteTv = (TextView) view.findViewById(R.id.quote);

        Spannable wordtoSpan = new SpannableString("\"" + quote + "\"");
        wordtoSpan.setSpan(
                new ForegroundColorSpan(Color.parseColor("#78a608")), 0, 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(
                new ForegroundColorSpan(Color.parseColor("#78a608")),
                wordtoSpan.length() - 1, wordtoSpan.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new RelativeSizeSpan(1.5f), 0, 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new RelativeSizeSpan(1.5f), wordtoSpan.length() - 1,
                wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Log.d("QUOTE", quote + " set:" + wordtoSpan.toString());
        quoteTv.setText(wordtoSpan);

        view.findViewById(R.id.close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            dismiss();
                        } catch (Throwable e) {
                        }
                    }
                });

        registerLayout = (LinearLayout) view.findViewById(R.id.registerLayout);
        appsLayout = (LinearLayout) view.findViewById(R.id.appsLayout);

        if (preferences.contains("REGISTERED")) {
            appsLayout.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.GONE);
        }

        final ViewPager appsPager = (ViewPager) view.findViewById(R.id.apps);

        if(apps==null){
            apps = new ArrayList<>();
        }
        LogService.Log("TipsDialog", "apps: "+apps.toString());
        if (apps.size() > 0) {
            int imagesPerPage = 3;
            int framesCount = apps.size();

            int pageNr = framesCount / imagesPerPage;
            if (framesCount % imagesPerPage != 0) {
                pageNr++;
            }

            appsPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), pageNr, imagesPerPage,
                    framesCount, apps));

            final RadioGroup pages = (RadioGroup) view.findViewById(R.id.pages);

            pages.removeAllViews();
            for (int i = 0; i < pageNr; ++i) {
                RadioButton radioBtn = new RadioButton(getActivity()
                        .getApplicationContext());
                radioBtn.setHeight(30);
                radioBtn.setWidth(30);
                radioBtn.setPadding(4, 4, 4, 4);
                radioBtn.setBackgroundResource(0);
                radioBtn.setButtonDrawable(R.drawable.selector_pager_item);
                radioBtn.setChecked(false);
                final int current = i;
                radioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked == true) {
                            appsPager.setCurrentItem(current);
                        }
                    }
                });
                pages.addView(radioBtn);
            }
            RadioButton btn = (RadioButton) pages.getChildAt(0);
            btn.setChecked(true);

            appsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    RadioButton btn = (RadioButton) pages.getChildAt(arg0);
                    btn.setChecked(true);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
        }

        final EditText emailEditText = (EditText) view.findViewById(R.id.email);

        Button register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = ProgressDialog.show(
                        getActivity(), "Uploading to GetResponse",
                        "Adding e-mail to GetResponse", true, false);

                Runnable run = new Runnable() {
                    String message = "Signup successful!";
                    String email = emailEditText.getText().toString();

                    public void run() {

                        try {

                            if (isValidEmailAddress(email)) {
                                GetResponse gr = new GetResponse();
                                gr.SendEmail(email);
                            } else
                                message = "Invalid email!";
                        } catch (AndroidException e) {
                            message = e.getMessage();
                        } catch (Throwable e) {
                            LogService.Log("register", "Signup failed, e-m: " + e.getMessage().toString() + " e-lm: "+e.getLocalizedMessage().toString());
                            e.printStackTrace();
                            message = "Signup failed!";

                        } finally {
                            progressDialog.dismiss();
                            showResult(message, getActivity());
                        }
                    }
                };
                (new Thread(run)).start();
            }

        });

        return view;
    }

    private void showResult(final String message, final Context context) {

        Runnable run = new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (message.equals("Signup successful!")
                                        || message
                                        .equals("Contact already added to target campaign")) {
                                    preferences.edit()
                                            .putBoolean("REGISTERED", true)
                                            .commit();
                                    if (appsLayout != null) {
                                        appsLayout.setVisibility(View.VISIBLE);
                                        registerLayout.setVisibility(View.GONE);
                                    }
                                }
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        };
        ((Activity) context).runOnUiThread(run);
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public ArrayList<App> getApps() {
        return apps;
    }

    public void setApps(ArrayList<App> apps) {
        this.apps = apps;
    }
}
