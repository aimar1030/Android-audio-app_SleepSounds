package com.zenlabs.sleepsounds.plistparser;

/**
 * Created by fedoro on 5/12/16.
 */
public class False extends PListObject implements IPListSimpleObject<Boolean> {

    private static final long serialVersionUID = -8533886020773567552L;

    java.lang.Boolean aBoolean;

    public False() {
        setType(PListObjectType.FALSE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#getValue()
     */
    @Override
    public Boolean getValue() {
        return aBoolean;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
     * (java.lang.Object)
     */
    @Override
    public void setValue(Boolean val) {
        // noop
        aBoolean = val;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
     * (java.lang.String)
     */
    @Override
    public void setValue(String val) {
        // noop
        this.aBoolean= java.lang.Boolean.valueOf(java.lang.Boolean.parseBoolean(val
                .trim()));
    }

}
