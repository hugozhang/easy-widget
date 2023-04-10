package me.about.widget.trace.util;

/**
 * 字符串工具
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:44
 * @description:
 */
public class StringUtils {

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

}
