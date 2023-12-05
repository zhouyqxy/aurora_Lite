package com.aurora.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegrexUtil {

    public static String match(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        Pattern pattern = Pattern.compile("([\\u4e00-\\u9fa5]+)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            stringBuilder.append(matcher.group(0));
        }
        return stringBuilder.toString();
    }
}