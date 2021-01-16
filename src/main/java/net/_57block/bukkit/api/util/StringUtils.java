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
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                builder.append(separator);
            }
            if (array[i] != null) {
                builder.append(array[i]);
            }
        }
        return builder.toString();
    }

    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(Iterator<?> iterator, String separator) {
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

        StringBuilder builder = new StringBuilder(256);
        if (first != null) {
            builder.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                builder.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                builder.append(obj);
            }
        }
        return builder.toString();
    }

}
