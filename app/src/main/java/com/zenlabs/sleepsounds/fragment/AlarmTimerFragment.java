package com.zenlabs.sleepsounds.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.adapter.AlarmControlAdapter;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.utils.Common;

/**
 * Created by fedoro on 5/24/16.
 */
public class AlarmTimerFragment extends Fragment implements AlarmControlAdapter.AlarmControlHandler {

    ImageView close_bt, edit_bt, plus_bt;
    ListView listView;
    RelativeLayout alertView;
    ImageView selectAlarm, selectTimer;
    RelativeLayout selectCancel;
    RelativeLayout selectView;
    AlarmControlAdapter adapter;

    boolean isEditing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Common.isGoHomeView = false;
        View rootView = inflater.inflate(R.layout.fragment_alarm_timer, container, false);
        close_bt = (ImageView) rootView.findViewById(R.id.close_alarm_timer_view_bt);
        edit_bt = (ImageView) rootView.findViewById(R.id.edit_bt);
        plus_bt = (ImageView) rootView.findViewById(R.id.add_alarm_timer_bt);

        listView = (ListView) rootView.findViewById(R.id.alerm_timer_list);

        alertView = (RelativeLayout) rootView.findViewById(R.id.alarm_timer_alert_view);

        selectAlarm = (ImageView) rootView.findViewById(R.id.select_alarm);
        selectTimer = (ImageView) rootView.findViewById(R.id.select_timer);
        selectCancel = (RelativeLayout) rootView.findViewById(R.id.cancel_bt);

        selectView = (RelativeLayout) rootView.findViewById(R.id.alarm_select_view);

        edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEditing)  {
                    edit_bt.setImageResource(R.drawable.pen);
                    if (Common.alarms != null && Common.alarms.size() > 0) {
                        for (int i = 0; i < Common.alarms.size(); i++) {
                            Common.alarms.get(i).isEditing = false;
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    edit_bt.setImageResource(R.drawable.check);
                    if (Common.alarms != null && Common.alarms.size() > 0) {
                        for (int i = 0; i < Common.alarms.size(); i++) {
                            Common.alarms.get(i).isEditing = true;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                isEditing = !isEditing;
            }
        });

        close_bt.setColorFilter(Color.WHITE);
        edit_bt.setColorFilter(Color.WHITE);
        plus_bt.setColorFilter(Color.WHITE);

        close_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.isGoHomeView = true;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                ((MainActivity)getActivity()).backHomeView();
            }
        });

        plus_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing)
                addView();
            }
        });

        selectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideView();
            }
        });

        selectTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentSelection = Common.SELECT_TIMER;
                ((MainActivity)getActivity()).showSetTimerview();
            }
        });

        selectAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentSelection = Common.SELECT_ALARM;
                alertView.setVisibility(View.GONE);
                ((MainActivity)getActivity()).showSetTimerview();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isEditing)
                {
                    Common.currentSelectedAlarm = position;
                    MAlarm alarm = Common.alarms.get(position);

                    if (alarm.isTimer)
                        Common.currentSelection = Common.SELECT_TIMER;
                    else
                        Common.currentSelection = Common.SELECT_ALARM;

                    ((MainActivity)getActivity()).showSetTimerview();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume()  {
        super.onResume();

        if (Common.alarms != null && Common.alarms.size() > 0) {
            for (int i = 0; i < Common.alarms.size(); i++) {
                Common.alarms.get(i).isEditing = false;
            }

            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        edit_bt.setImageResource(R.drawable.pen);
        isEditing = false;

        if (Common.alarms != null && Common.alarms.size() > 0) {
            adapter = new AlarmControlAdapter(getContext(), this);
            if (listView.getAdapter() != null)
                listView.setAdapter(null);
            listView.setAdapter(adapter);
        }

        alertView.setVisibility(View.GONE);

    }

    private void addView()  {

        alertView.setVisibility(View.VISIBLE);
        selectView.setScaleX(0.1f);
        selectView.setScaleY(0.1f);

        ObjectAnimator anim = ObjectAnimator.ofFloat(selectView,"scaleX",1.0f);
        anim.setDuration(100);
        anim.start();

        // Make the object height 50%
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(selectView,"scaleY",1.0f);
        anim2.setDuration(100);
        anim2.start();
    }

    private void hideView() {

        alertView.setVisibility(View.GONE);
    }

    @Override
    public void dataChanged() {
        adapter.notifyDataSetChanged();
    }
}
