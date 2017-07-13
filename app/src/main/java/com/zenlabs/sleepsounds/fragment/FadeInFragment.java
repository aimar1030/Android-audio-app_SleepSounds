package com.zenlabs.sleepsounds.fragment;

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
import com.zenlabs.sleepsounds.adapter.FadeInAdapter;
import com.zenlabs.sleepsounds.utils.Common;

/**
 * Created by fedoro on 5/25/16.
 */
public class FadeInFragment extends Fragment {

    FadeInAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_time, container, false);
        ImageView back_bt = (ImageView) rootView.findViewById(R.id.back_fade_in);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_fadein);
        TextView title = (TextView) rootView.findViewById(R.id.alarm_view_title);

        adapter = new FadeInAdapter(Common.data, getContext());
        listView.setAdapter(adapter);

        if (Common.currentSelection == Common.SELECT_ALARM) {
            title.setText("Fade In");
        } else
            title.setText("Fade Out");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.currentSelectedFadeIn = position;
                adapter.notifyDataSetChanged();
            }
        });

        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).backAction();
            }
        });

        return rootView;
    }
}
