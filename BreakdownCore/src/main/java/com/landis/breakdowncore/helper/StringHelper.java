package com.landis.breakdowncore.helper;

public class StringHelper {
    public static String convertToTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean convertNext = true;

        for (char ch : input.toCharArray()) {
            if (ch == '_') {
                convertNext = true;
                titleCase.append(' ');
            } else if (convertNext) {
                titleCase.append(Character.toUpperCase(ch));
                convertNext = false;
            } else {
                titleCase.append(ch);
            }
        }

        return titleCase.toString();
    }
}
