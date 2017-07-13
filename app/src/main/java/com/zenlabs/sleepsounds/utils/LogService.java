package com.zenlabs.sleepsounds.utils;

import android.util.Log;

/**
 * Created by fedoro on 5/12/16.
 */
public class LogService {

    public static final boolean IS_DEBUG = false;

    public static void Log(String tag, String msg) {


        if (IS_DEBUG) {

            if (msg == null) {
                msg = "MSG is null";
            }

            StackTraceElement[] stackTraceElement = Thread.currentThread()
                    .getStackTrace();
            int currentIndex = -1;
            for (int i = 0; i < stackTraceElement.length; i++) {
                if (stackTraceElement[i].getMethodName().compareTo("Log") == 0) {

                    currentIndex = i + 1;

                    break;
                }
            }

            String fullClassName = stackTraceElement[currentIndex]
                    .getClassName();
            String className = fullClassName.substring(fullClassName
                    .lastIndexOf(".") + 1);
            String methodName = stackTraceElement[currentIndex].getMethodName();
            String lineNumber = String.valueOf(stackTraceElement[currentIndex]
                    .getLineNumber());

            Log.d(tag, msg);
            Log.d(tag + " position", "at " + fullClassName + "." + methodName
                    + "(" + className + ".java:" + lineNumber + ")");

        }

    }
}
