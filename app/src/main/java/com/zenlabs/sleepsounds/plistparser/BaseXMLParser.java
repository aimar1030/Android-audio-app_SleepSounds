package com.zenlabs.sleepsounds.plistparser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by fedoro on 5/12/16.
 */
public abstract class BaseXMLParser {

    /**
     * {@link Stringer} for this class.
     */
    protected Stringer stringer;

    /**
     * The handler used to parse the classifications xml.
     */
    private DefaultHandler handler;

    /**
     * Re-usable factory for gettings {@link SAXParser}s.
     */
    protected SAXParserFactory spf;

    /**
     * Re-usable {@link SAXParser} for parsing XML.
     */
    protected SAXParser sp;

    /**
     * Public c'tor.
     */
    public BaseXMLParser() {
        stringer = new Stringer();
    }

    /**
     * @return the handler
     */
    public DefaultHandler getHandler() {
        return (DefaultHandler) handler;
    }

    /**
     * @param handler
     *            the handler to set
     */
    public void setHandler(DefaultHandler handler) {
        this.handler = handler;
    }

    /**
     * Creates a new {@link SAXParserFactory} and gets a new {@link SAXParser}.
     *
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void initParser() {
        // get a factory
        if (null == spf) {
            spf = SAXParserFactory.newInstance();
        }
        // get a new parser instance
        try {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse an XML document.
     *
     * @param xml
     */
    public void parse(String xml) throws IllegalStateException {
        try {

            InputSource inSrc = new InputSource(new StringReader(xml));

            sp.parse(inSrc, getHandler());
        } catch (SAXException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}

