package com.zenlabs.sleepsounds.model;

import java.util.ArrayList;

public class MFavorit {

    public ArrayList<MSound> sounds;
    public String title;
    public String uniqueID;
    public boolean isSelected;
    public boolean isEditing;

    public MFavorit()   {

        sounds = new ArrayList<>();
        title = "";
        uniqueID = "";
        isSelected = false;
        isEditing = false;
    }
}
