package com.zenlabs.sleepsounds.model;

/**
 * Created by fedoro on 5/13/16.
 */
public class MSound {

    String name, background, sound,uniqueId;
    public boolean isUsable;
    public float volume;
    public int type;
    public boolean isPlaying;

    public String getName() {

        return this.name;
    }

    public void setName(String name)   {
        this.name = name;
    }

    public String getBackground()   {
        return this.background;
    }

    public void setBackground(String background) {

        this.background = background;
    }

    public String getSound()    {

        return this.sound;
    }

    public void setSound(String sound)  {

        this.sound = sound;
    }

    public String getUniqueId()    {

        return this.uniqueId;
    }

    public void setUniqueId(String uniqueId)    {

        this.uniqueId = uniqueId;
    }

}
