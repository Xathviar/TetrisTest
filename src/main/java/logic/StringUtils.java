package logic;

import nakama.com.google.common.base.Strings;

public class StringUtils {

    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        sb.append(Strings.repeat(String.valueOf(pad), (size - s.length() / 2)));
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }
}