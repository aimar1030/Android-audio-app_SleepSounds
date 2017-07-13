package com.zenlabs.sleepsounds.plistparser;

/**
 * Created by fedoro on 5/12/16.
 */
public class MyString extends PListObject implements
        IPListSimpleObject<String> {

    protected Stringer str;

    private static final long serialVersionUID = -8134261357175236382L;

    public MyString() {
        setType(PListObjectType.STRING);
        str = new Stringer();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#getValue()
     */
    @Override
    public String getValue() {
        return this.str.getBuilder().toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
     * (java.lang.Object)
     */
    @Override
    public void setValue(String val) {
        str.newBuilder().append(val);
    }

}