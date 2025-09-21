package com.notepad.global.utils;

public class FullText {

    public static String toFTS(String q) {
        if (q == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (String t : q.trim().split("\\s+")) {
            if (t.length() >= 2) {
                if (!sb.isEmpty()) {
                    sb.append(' ');
                }
                sb.append('+').append(t).append('*');
            }
        }

        return sb.isEmpty() ? null : sb.toString();
    }
}
