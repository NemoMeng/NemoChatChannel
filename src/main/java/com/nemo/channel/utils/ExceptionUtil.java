/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/27 10:59
 */
package com.nemo.channel.utils;

import java.util.Arrays;

/**
 * Created by Nemo on 2017/11/27.
 */
public class ExceptionUtil {

    /**
     *
     * @param flag
     * @param message
     * @param args
     */
    public static void makeRunTimeWhen(boolean flag, String message,
                                       Object... args) {
        if (flag) {
            message = String.format(message, args);
            RuntimeException e = new RuntimeException(message);
            throw correctStackTrace(e);
        }
    }

    /**
     *
     * @param cause
     */
    public static void makeRuntime(Throwable cause) {
        RuntimeException e = new RuntimeException(cause);
        throw correctStackTrace(e);
    }

    /**
     * 移除 Lang层堆栈信息
     * @param e
     * @return
     */
    private static RuntimeException correctStackTrace(RuntimeException e) {
        StackTraceElement[] s = e.getStackTrace();
        if(null != s && s.length > 0){
            e.setStackTrace(Arrays.copyOfRange(s, 1, s.length));
        }
        return e;
    }

}