package com.zenlabs.sleepsounds.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.ForumActivity;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.activity.MoreActivity;
import com.zenlabs.sleepsounds.adapter.MenuListAdapter;
import com.zenlabs.sleepsounds.utils.ShareResponseManager;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

public class MenuFragment extends Fragment {

    private ShareResponseManager shareDialogResponseManager = new ShareResponseManager() {
        @Override
        public void shareResponse(Intent intent, int resultCode) {
            startActivityForResult(intent, resultCode);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.menu_list);
        String[] titles = getContext().getResources().getStringArray(R.array.menu_cell_title);
        MenuListAdapter adapter = new MenuListAdapter(getContext(), titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position)   {
                    case 0:
                        showPrefieldPopUp();
                        break;
                    case 1:
                        followUsOnTwitter();
                        break;
                    case 2:
                        followUsOnInstagram();
                        break;
                    case 4:
                        sendFeedback();
                        break;
                    case 5:
                        share();
                        break;
                    case 6:
                        ((MainActivity)getActivity()).showInfoScreen();
                        break;
                    case 7:
                        if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false))
                            ((MainActivity)getActivity()).showSubscribe();
                        break;
                    case 8:
                        if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false))
                            ((MainActivity)getActivity()).restorePurchase();
                        break;
                    case 9:
                        startActivity(new Intent(getActivity(), ForumActivity.class));
                        break;
                    case 10:
                        if (UtilsMethods.hasInternetConnection(getContext())) {
                            startActivity(new Intent(getActivity(), MoreActivity.class));
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getResources().getString(R.string.no_intertnet_connections))
                                    .setMessage(getResources().getString(R.string.pls_check_intertnet_connections))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        return rootView;
    }

    private void followUsOnInstagram()  {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (getContext().getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                intent.setData(Uri.parse("http://instagram.com/_u/" + UtilsValues.kInstagramId));
                intent.setPackage("com.instagram.android");
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            intent.setData(Uri.parse(UtilsValues.kInstagramLink));
        }

        startActivity(intent);
    }

    private void followUsOnTwitter()    {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=" + UtilsValues.kTwitterId));
            startActivity(intent);

        }catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(UtilsValues.kTwitterLink)));
        }
    }

    private void showPrefieldPopUp() {

        Intent intent;

        try {
            getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/"+UtilsValues.kFacebookId));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UtilsValues.kFacebookLink));
        }

        getActivity().startActivity(intent);
    }

    private void sendFeedback() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email_app)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_feedback));

        PackageInfo pInfo = null;
        String versionName = "";
        int versionCode = 0;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo("com.zenlabs.sleepsounds", 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String text = getResources().getString(R.string.feedback_app_name) + "\n" + getResources().getString(R.string.app_version) + " " + versionCode + "/" + versionName + "\n" + getResources().getString(R.string.device_model) + " " + Build.MANUFACTURER.toUpperCase() + ", " + Build.MODEL + "\n" + getResources().getString(R.string.os_version) + " " + android.os.Build.VERSION.RELEASE;

        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_email)));
    }

    private void share()    {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//                    sharingIntent.setType("message/rfc822");
//                    sharingIntent.setType("text/html");
        String shareBody = getResources().getString(R.string.share_text);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
