package com.zenlabs.sleepsounds.utils;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.customview.MSoundView;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.model.MSound;

import java.util.ArrayList;

public class Common {
    public static ArrayList<MSound> sounds;
    public static ArrayList<MSound> unlockSounds;
    public static ArrayList<MSound> lockSounds;
    public static ArrayList<MSound> videoSounds;
    public static ArrayList<MFavorit> favorits;
    public static ArrayList<MAlarm> alarms;
    public static ArrayList<MSoundView> unlockViews;
    public static ArrayList<MSound> currentPlayingSounds;
    public static MAlarm currentAlarm;
    public static MFavorit currentFavorit;

    public static final int SELECT_TIMER = 0;
    public static final int SELECT_ALARM = SELECT_TIMER + 1;

    public static int currentSelection;

    public static int currentSelectedFadeIn = 2;

    public static int currentSelectedSound = -1;

    public static int currentSelectedAlarm = -1;

    public static boolean isStart = false;

    public static boolean isGoHomeView;

    public static String data[] = {"None", "15 seconds", "30 seconds", "1 minutes", "2 minutes", "5 minutes", "10 minutes"};
    public static int data_value[] = {0, 15, 30, 60, 120, 300, 600};

    public static int currentIAPStatus = 0;
}
