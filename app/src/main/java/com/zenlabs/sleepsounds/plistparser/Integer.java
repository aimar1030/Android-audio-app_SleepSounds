package com.zenlabs.sleepsounds.plistparser;

/**
 * Created by fedoro on 5/12/16.
 */
public class Integer extends PListObject implements
        IPListSimpleObject<java.lang.Integer> {

    protected java.lang.Integer intgr;


    private static final long serialVersionUID = -5952071046933925529L;

    public Integer() {
        setType(PListObjectType.INTEGER);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#getValue()
     */
    @Override
    public java.lang.Integer getValue() {
        return intgr;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
     * (java.lang.Object)
     */
    @Override
    public void setValue(java.lang.Integer val) {
        this.intgr = val;
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
        this.intgr = java.lang.Integer.valueOf(java.lang.Integer.parseInt(val
                .trim()));
    }

}
