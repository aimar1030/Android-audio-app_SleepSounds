package com.zenlabs.sleepsounds.plistparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Created by fedoro on 5/12/16.
 */
public class PListXMLHandler extends DefaultHandler2 {

    public static final String TAG = "PListXMLHandler";

    public enum ParseMode {
        START_TAG, END_TAG
    };

    public static interface PListParserListener {
        public void onPListParseDone(PList pList, ParseMode mode);
    }

    /**
     * Listener for this parser.
     */
    private PListParserListener parseListener;

    /**
     * The value of parsed characters from elements and attributes.
     */
    private Stringer tempVal;

    /**
     * The parsed {@link PList}.
     */
    private PList pList;

    // Registers to hold state of parsing the workflow as Dict
    protected String key;

    /**
     *
     */
    public PListXMLHandler() {
        super();
    }

    /**
     * @return the pList
     */
    public PList getPlist() {
        return pList;
    }

    /**
     *            the pList to set
     */
    public void setPlist(PList plist) {
        this.pList = plist;
    }

    /**
     * @return the parseListener
     */
    public PListParserListener getParseListener() {
        return parseListener;
    }

    /**
     * @param parseListener
     *            the parseListener to set
     */
    public void setParseListener(PListParserListener parseListener) {
        this.parseListener = parseListener;
    }

    /**
     * @return the tempVal
     */
    public Stringer getTempVal() {
        return tempVal;
    }

    /**
     * @param tempVal
     *            the tempVal to set
     */
    public void setTempVal(Stringer tempVal) {
        this.tempVal = tempVal;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        tempVal = new Stringer();
        pList = null;
        key = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {

        tempVal.newBuilder();
        if (localName.equalsIgnoreCase(Constants.TAG_PLIST)) {
            if (null != pList) {
                // there should only be one PList element in the root
                throw new SAXException(
                        "there should only be one PList element in PList XML");
            }
            pList = new PList();
        } else {
            if (null == pList) {
                throw new SAXException(
                        "invalid PList - please see http://www.apple.com/DTDs/PropertyList-1.0.dtd");
            }
            if (localName.equalsIgnoreCase(Constants.TAG_DICT)
                    || localName.equalsIgnoreCase(Constants.TAG_PLIST_ARRAY)) {
                try {
                    PListObject objToAdd = pList.buildObject(localName, tempVal
                            .getBuilder().toString());
                    pList.stackObject(objToAdd, key);
                } catch (Exception e) {
                    throw new SAXException(e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        tempVal.getBuilder().append(new String(ch, start, length));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (localName.equalsIgnoreCase(Constants.TAG_KEY)) {
            key = tempVal.getBuilder().toString().trim();
        } else if (localName.equalsIgnoreCase(Constants.TAG_DICT)
                || localName.equalsIgnoreCase(Constants.TAG_PLIST_ARRAY)) {
            pList.popStack();
        } else if (!localName.equalsIgnoreCase(Constants.TAG_PLIST)) {
            try {
                PListObject objToAdd = pList.buildObject(localName, tempVal
                        .getBuilder().toString());
                pList.stackObject(objToAdd, key);
            } catch (Exception e) {
                throw new SAXException(e);
            }
            key = null;
        } else if (localName.equalsIgnoreCase(Constants.TAG_PLIST)) {
            if (null != parseListener) {
                parseListener.onPListParseDone(pList, ParseMode.END_TAG);
            }
        }
        tempVal.newBuilder();

    }
}
