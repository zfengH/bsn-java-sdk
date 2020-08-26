package com.bsnbase.sdk.util;

import org.apache.commons.logging.LogFactory;

public class Log {
    static org.apache.commons.logging.Log log = LogFactory.getLog("[mLog:]");

    public static void d(Object s){
        log.debug(s);
    }

    public static void e(Object s){
        log.error(s);
    }

    public static void i(Object s){
        log.info(s);
    }
}
