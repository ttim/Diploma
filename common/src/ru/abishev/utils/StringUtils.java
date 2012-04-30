package ru.abishev.utils;

public class StringUtils {
    public static String[] splitOnPunctuation(String word) {
        return word.split("[ ,.!?:\"\\(\\)\\|/=\\[\\]#{}';<>]");
    }
}
