package com.zenlabs.sleepsounds.plistparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by fedoro on 5/12/16.
 */
public class Stringer {

    private StringBuilder builder;

    /**
     *
     */
    public Stringer() {
        builder = new StringBuilder();
    }

    public Stringer(String val) {
        builder = new StringBuilder(val);
    }

    /**
     * Clear the class-global stringbuilder.
     *
     * @return fluent interface to {@link builder}
     */
    public StringBuilder newBuilder() {
        builder.setLength(0);
        return builder;
    }

    /**
     * Get the class-global stringbuilder.
     *
     * @return fluent interface to {@link builder}
     */
    public StringBuilder getBuilder() {
        return builder;
    }

    /**
     * Converts an {@link InputStream} to a stringer.
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static Stringer convert(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the Stringer class to
		 * produce the string.
		 */
        Stringer ret = new Stringer();
        if (is != null) {
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    ret.getBuilder().append(buffer, 0, n);
                }
            } finally {
                is.close();
            }
        }
        return ret;
    }

}
