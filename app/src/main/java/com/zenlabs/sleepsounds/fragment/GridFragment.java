package com.zenlabs.sleepsounds.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.adapter.FramesAdapter;
import com.zenlabs.sleepsounds.model.App;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/12/16.
 */
public class GridFragment extends Fragment {

    private int pageNr;
    private int firstImagePos;
    private int imageCount;
    private ArrayList<App> items;
    private GridView gridView;
    private FramesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        pageNr = args.getInt("number");
        firstImagePos = args.getInt("firstImage");
        imageCount = args.getInt("imageCount");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.layout_grid, container, false);

        gridView = (GridView) view.findViewById(R.id.grid);

        adapter = new FramesAdapter(getActivity(), getActivity(), imageCount, firstImagePos,
                items);

        gridView.setAdapter(adapter);
        gridView.setTag(pageNr);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final int framePos = arg2 + firstImagePos;
                final App item = items.get(framePos);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item
                        .getUrl())));
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public ArrayList<App> getItems() {
        return items;
    }

    public void setItems(ArrayList<App> items) {
        this.items = items;
    }
}
