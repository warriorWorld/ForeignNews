package com.warrior.hangsu.administrator.foreignnews.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/2/20.
 */

public class StringUtils {
    public static boolean isWord(String word) {
        if (word.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern
                .compile("[\\w]+");
        Matcher matcher = pattern.matcher(word);
        return matcher.matches();
    }
}
