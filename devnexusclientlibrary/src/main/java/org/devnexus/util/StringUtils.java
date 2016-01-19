package org.devnexus.util;

/**
 * Boring class for one off string checks
 */
public final class StringUtils {
    private StringUtils() {
    }

    public static int compare(String a, String b) {
        if (a == null) {
            a = "";
        }
        if (b == null) {
            b = "";
        }
        return a.compareTo(b);
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
