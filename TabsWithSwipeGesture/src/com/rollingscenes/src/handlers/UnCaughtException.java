package com.rollingscenes.src.handlers;

import java.lang.Thread.UncaughtExceptionHandler;

import com.rollingscenes.src.exceptions.BackgroundException;

import android.os.Handler;
import android.os.Process;

public class UnCaughtException implements UncaughtExceptionHandler {

    private Handler mHandler;

    public UnCaughtException(Handler handler) {
        mHandler = handler;
    }
 
    public void uncaughtException(Thread t,final Throwable e) {
        
        final int tid = Process.myTid();
        final String threadName = t.getName();
        mHandler.post(new Runnable() {
            public void run() {
                throw new BackgroundException(e, tid, threadName);
            }
        });
    }
}
