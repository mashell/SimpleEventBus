package com.mashell.simpleeventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by mashell on 18/3/19.
 */

public class MainThreadHandler extends Handler {
    private Object event;
    private Subscription mSubscription;

    public MainThreadHandler(Looper looper) {
        super(looper);
    }

    public void post(Subscription subscription, Object event){
        this.mSubscription = subscription;
        this.event = event;
        sendMessage(Message.obtain());
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        try {
            mSubscription.method.invoke(mSubscription.subscriber,event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
