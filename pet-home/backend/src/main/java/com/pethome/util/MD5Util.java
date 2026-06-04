package com.pethome.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String md5(String input) {
        return DigestUtils.md5Hex(input);
    }

    public static boolean verify(String input, String md5) {
        return md5(input).equals(md5);
    }
}
