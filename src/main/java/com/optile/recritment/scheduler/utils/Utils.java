package com.optile.recritment.scheduler.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Utils Class, has common Utils.
 */
public class Utils {

    /**
     * Method to convert Read Input Stream and return a String.
     *
     * @param inputStream
     * @return String
     * @throws Exception
     */
    public static String getStringFromStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int inputByte;

        while ((inputByte = inputStream.read()) != -1) {
            stream.write(inputByte);
        }

        return stream.toString();
    }
}
