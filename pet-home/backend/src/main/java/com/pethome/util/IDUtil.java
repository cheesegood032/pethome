package com.pethome.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class IDUtil {
    public static String generateOrderNo() {
        return "ORD" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
               + (int)(Math.random() * 10000);
    }

    public static String generateFosterOrderNo() {
        return "FOS" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
               + (int)(Math.random() * 10000);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
