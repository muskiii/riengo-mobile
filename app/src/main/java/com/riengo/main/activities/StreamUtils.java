package com.riengo.main.activities;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by rakuraku on 07/10/17.
 */

public class StreamUtils {
    public static String readStream(InputStream stream)
            throws IOException, UnsupportedEncodingException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer, "UTF-8");
        String theString = writer.toString();
        return theString;
    }

}
