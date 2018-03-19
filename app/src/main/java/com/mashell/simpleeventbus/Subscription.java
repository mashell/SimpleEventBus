package com.mashell.simpleeventbus;

import java.lang.reflect.Method;

/**
 * Created by mashell on 18/3/19.
 */

public class Subscription {
    //订阅对应的方法
    public Method method;
    //订阅者对应的类
    public Object subscriber;
    //对应线程
    public int threadMode;

    public Subscription(Method method, Object subscriber, int threadMode) {
        this.method = method;
        this.subscriber = subscriber;
        this.threadMode = threadMode;
    }
}
