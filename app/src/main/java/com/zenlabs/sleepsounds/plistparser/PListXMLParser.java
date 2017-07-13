package com.zenlabs.sleepsounds.plistparser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fedoro on 5/12/16.
 */
public class PListXMLParser extends BaseXMLParser {

    public PListXMLParser() {
        super();
    }

    /**
     * Parse a PList XML document.
     *
     * @param xml
     */
    public void parse(String xml) throws IllegalStateException {
        PListXMLHandler pListHandler = (PListXMLHandler) getHandler();
        if (null == pListHandler) {
            throw new IllegalStateException(
                    "handler is null, must set a document handler before calling parse");
        }
        if (null == xml) {
            pListHandler.setPlist(null);
            return;
        }
        initParser();
        super.parse(xml);
    }

    /**
     * Parse a PList XML document from an {@link InputStream}.
     *
     * @throws IOException
     */
    public void parse(InputStream is) throws IllegalStateException, IOException {
        PListXMLHandler pListHandler = (PListXMLHandler) getHandler();
        if (null == pListHandler) {
            throw new IllegalStateException(
                    "handler is null, must set a document handler before calling parse");
        }
        if (null == is) {
            pListHandler.setPlist(null);
            return;
        }
        Stringer xml = null;
        try {
            xml = Stringer.convert(is);
        } catch (IOException e) {
            throw new IOException(
                    "error reading from input string - is it encoded as UTF-8?");
        }
        initParser();
        super.parse(xml.getBuilder().toString());
    }
}
