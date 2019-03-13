package com.base512.ordo.util;

/**
 * Created by Thomas on 2/22/2017.
 */

public class StringUtils {

    public static String leftZeroPad(int input, int length) {
        return String.format("%0"+length+"d", input);
    }
}
