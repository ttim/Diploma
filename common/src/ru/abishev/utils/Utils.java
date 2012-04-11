package ru.abishev.utils;

import java.util.Arrays;
import java.util.Iterator;

public class Utils {
    public static <T> Iterable<T> unsafeUp(final Iterator<T> iterator) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }
    
    public static String join(Iterable<String> elements, String separator) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;

        for (String e : elements) {
            if (!isFirst) {
                builder.append(separator);
            } else {
                isFirst = false;
            }
            builder.append(e);
        }

        return builder.toString();
    }

    public static String join(String[] elements, String separator) {
        return join(Arrays.asList(elements), separator);
    }

    public static int indexOfAccordingEscaping(String s, int from, int ch, int strCh) {
        int pos = from;
        boolean isInString = false, isInEscape = false;

        while (pos < s.length() && !(s.charAt(pos) == ch && !isInString)) {
            char current = s.charAt(pos);

            if (isInString) {
                if (isInEscape) {
                    // go next
                    isInEscape = false;
                } else {
                    if (current == '\\') {
                        isInEscape = true;
                    }
                    if (current == strCh) {
                        isInString = false;
                    }
                }
            } else {
                if (current == strCh) {
                    isInString = true;
                }
            }

            pos++;
        }

        return pos == s.length() ? -1 : pos;
    }
}
