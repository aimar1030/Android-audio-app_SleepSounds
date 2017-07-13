package com.zenlabs.sleepsounds.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.adapter.SoundSelectAdapter;
import com.zenlabs.sleepsounds.utils.Common;

/**
 * Created by fedoro on 5/25/16.
 */
public class SoundSelectFragment extends Fragment {

    ImageView noneSelected;
    ListView soundListView;
    SoundSelectAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_sound, container, false);
        noneSelected = (ImageView) rootView.findViewById(R.id.select_none);
        soundListView = (ListView) rootView.findViewById(R.id.sound_list);

        TextView title = (TextView) rootView.findViewById(R.id.select_sound_title);
        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MyriadPro-Light.otf");
        title.setTypeface(custom_font);

        if (Common.currentSelectedSound == -1)   {
            noneSelected.setVisibility(View.VISIBLE);
        } else
            noneSelected.setVisibility(View.GONE);

        ImageView backSound = (ImageView) rootView.findViewById(R.id.back_sound);
        backSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).backAction();
            }
        });

        if (Common.favorits != null && Common.favorits.size()>0) {
            adapter = new SoundSelectAdapter(getContext(), Common.favorits);
            soundListView.setAdapter(adapter);

            soundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Common.currentSelectedSound = position;
                    adapter.notifyDataSetChanged();
                    noneSelected.setVisibility(View.GONE);
                }
            });
        }
        return rootView;
    }
}
