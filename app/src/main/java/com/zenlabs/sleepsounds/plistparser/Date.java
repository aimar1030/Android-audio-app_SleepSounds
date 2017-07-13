package com.zenlabs.sleepsounds.plistparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by fedoro on 5/12/16.
 */
public class Date extends PListObject implements
        IPListSimpleObject<java.util.Date> {

    private static final long serialVersionUID = 3846688440069431376L;

    protected java.util.Date date;

    private SimpleDateFormat iso8601Format;

    public Date() {
        setType(PListObjectType.DATE);
        iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#getValue()
     */
    @Override
    public java.util.Date getValue() {
        return (java.util.Date) date;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
     * (java.lang.Object)
     */
    @Override
    public void setValue(java.util.Date val) {
        this.date = val;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
     * (java.lang.String)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setValue(String val) {
        // sniff date
        if (null == val || val.length() < 1) {
            this.date = null;
            return;
        }
        Scanner scanner = new Scanner(val).useDelimiter("-");
        if (scanner.hasNextInt()) {
            try {
                this.date = iso8601Format.parse(val);
            } catch (ParseException e) {

                e.printStackTrace();
            }
        } else {

            this.date = new java.util.Date(java.util.Date.parse(val.trim()));
        }
    }
}
