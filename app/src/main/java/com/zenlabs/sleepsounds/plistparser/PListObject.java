package com.zenlabs.sleepsounds.plistparser;

import java.io.Serializable;

/**
 * Created by fedoro on 5/12/16.
 */
public class PListObject extends Object implements Cloneable, Serializable {

    private static final long serialVersionUID = -5258056855425643835L;

    private PListObjectType type;

    /**
     * @return the type
     */
    public PListObjectType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(PListObjectType type) {
        this.type = type;
    }
}
