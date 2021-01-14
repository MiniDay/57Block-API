package net._57block.bukkit.api.util;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * 字符串工具
 */
@SuppressWarnings("unused")
public class StringUtils {
    public static final String EMPTY = "";

    private StringUtils() {
    }

    public static boolean startsWithIgnoreCase(String string, String start) {
        return string.toLowerCase().startsWith(start.toLowerCase());
    }

    public static boolean endsWithIgnoreCase(String string, String end) {
        return string.toLowerCase().endsWith(end.toLowerCase());
    }

    public static ArrayList<String> startsWith(Iterable<String> strings, String start) {
        ArrayList<String> list = new ArrayList<>();
        for (String string : strings) {
            if (string.startsWith(start)) {
                list.add(string);
            }
        }
        return list;
    }

    public static ArrayList<String> endsWith(Iterable<String> strings, String end) {
        ArrayList<String> list = new ArrayList<>();
        for (String string : strings) {
            if (string.endsWith(end)) {
                list.add(string);
            }
        }
        return list;
    }

    public static ArrayList<String> startsWithIgnoreCase(Iterable<String> strings, String start) {
        ArrayList<String> list = new ArrayList<>();
        for (String string : strings) {
            if (startsWithIgnoreCase(string, start)) {
                list.add(string);
            }
        }
        return list;
    }

    public static ArrayList<String> endsWithIgnoreCase(Iterable<String> strings, String end) {
        ArrayList<String> list = new ArrayList<>();
        for (String string : strings) {
            if (endsWithIgnoreCase(string, end)) {
                list.add(string);
            }
        }
        return list;
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                + separator.length());

        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return String.valueOf(first);
        }

        // two or more elements
        StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

}
