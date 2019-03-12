package com.base512.ordo.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Thomas on 2/22/2017.
 */

public class StringUtils {

    public static String leftZeroPad(double input, int length) {
        return String.format("%0"+length+"d", input);
    }
}
