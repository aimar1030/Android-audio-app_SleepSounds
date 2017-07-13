package com.zenlabs.sleepsounds.plistparser;

/**
 * Created by fedoro on 5/12/16.
 */
public class True extends PListObject implements IPListSimpleObject<Boolean> {

    private static final long serialVersionUID = -3560354198720649001L;

    public True() {
        setType(PListObjectType.TRUE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#getValue()
     */
    @Override
    public Boolean getValue() {
        return Boolean.valueOf(true);
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
    }

}
