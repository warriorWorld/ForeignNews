package com.warrior.hangsu.administrator.foreignnews.okhttp.interceptor;


import java.util.logging.Level;

import static okhttp3.internal.platform.Platform.INFO;


/**
 * @author ihsan on 10/02/2017.
 */
class I {

    protected I() {
        throw new UnsupportedOperationException();
    }

    static void log(int type, String tag, String msg) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(tag);
        switch (type) {
            case INFO:
                logger.log(Level.INFO, msg);
                break;
            default:
                logger.log(Level.WARNING, msg);
                break;
        }
    }
}
