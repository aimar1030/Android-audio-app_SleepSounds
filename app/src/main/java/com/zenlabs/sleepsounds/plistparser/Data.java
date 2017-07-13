package com.zenlabs.sleepsounds.plistparser;

/**
 * Created by fedoro on 5/12/16.
 */
public class Data extends PListObject implements
        IPListSimpleObject<String> {

    protected Stringer dataStringer;
    protected byte[] rawData;

    private static final long serialVersionUID = -3101592260075687323L;

    public Data() {
        setType(PListObjectType.DATA);
        dataStringer = new Stringer();
    }

    /**
     * Get the raw Base64 encoded data value on this object. Assumes the data
     * has already been encoded.
     *
     *      IPListSimpleObject#getValue()
     */
    @Override
    public String getValue() {
        return getValue(true);
    }

    /**
     * Get the raw Base64 encoded data value on this object. Assumes the data
     * has already been encoded.
     *
     * @param decode
     *            - if true, the result is Base64 decoded before being returned.
     *            NOTE: this calls {@link Base64#decodeFast(byte[])} which
     *            expects the raw encoded data to be on one line (no line
     *            separators). To change this behavior, subclass and override.
     *
     *      IPListSimpleObject#getValue()
     */
    public String getValue(boolean decode) {
        dataStringer.newBuilder();
        if (decode) {
            return dataStringer.getBuilder()
                    .append(new String(Base64.decodeFast(rawData)))
                    .toString();
        } else {
            return dataStringer.getBuilder().append(rawData).toString();
        }
    }

    /**
     * Sets the raw Base64 data value on this object. Assumes the data is
     * properly encoded.
     *
     * @param val
     *            - Base64 encoded data to set
     *
     *      IPListSimpleObject#setValue(java.lang.Object)
     */
    @Override
    public void setValue(String val) {
        setValue(val, true);
    }

    /**
     * Sets the data value on this object.
     *
     * @param val
     *            - unencoded data to set
     * @param encoded
     *            - flag true if val is Base64 encoded already. If false, val is
     *            encoded (without line seperators) before being stored.
     *
     *      IPListSimpleObject#setValue(java.lang.Object)
     */
    public void setValue(String val, boolean encoded) {
        if (!encoded) {
            rawData = Base64.encodeToByte(val.getBytes(), false);
        } else {
            rawData = val.getBytes();
        }
    }

    /**
     * Sets the data value on this object.
     *
     * @param val
     *            - unencoded data to set
     * @param encoded
     *            - flag true if val is Base64 encoded already. If false, val is
     *            encoded (without line seperators) before being stored.
     *
     *      IPListSimpleObject#setValue(java.lang.Object)
     */
    public void setValue(byte[] val, boolean encoded) {
        if (!encoded) {
            rawData = Base64.encodeToByte(val, false);
        } else {
            rawData = val;
        }
    }

}
