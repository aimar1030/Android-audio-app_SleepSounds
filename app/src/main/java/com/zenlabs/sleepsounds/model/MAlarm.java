package com.zenlabs.sleepsounds.model;

/**
 * Created by fedoro on 5/25/16.
 */
public class MAlarm {

    public String category_id;
    public int fade_in_time;
    public boolean isSnooze;
    public int minutes_fire;
    public int hours_fire;
    public int status;
    public boolean isTimer;
    public boolean isPower;
    public boolean isCounter;
    public long fire_seconds;
    public boolean isEditing;
    public String unique_id;

    public MAlarm() {

        category_id = "";
        fade_in_time = 0;
        isSnooze = false;
        minutes_fire = 0;
        hours_fire = 0;
        status = 0;
        isTimer = false;
        isPower = false;
        isCounter = false;
        fire_seconds = 0;
        isEditing = false;
        unique_id = "";
    }
}
