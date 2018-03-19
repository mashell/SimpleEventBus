package com.mashell.simpleeventbus;

import android.os.Looper;
import android.util.Log;

import com.mashell.simpleeventbus.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mashell on 18/3/18.
 * 首先 EventBus 是个单例，按照自己的理解实现一个单例模式即可
 */

public class EventBus {

    public static final String TAG = EventBus.class.getSimpleName();

    private volatile static EventBus instance;
    //维护一个 map , Key -> 事件， Value -> 所有的订阅此事件的方法
    private Map<Class<?>, List<Subscription>> map;
    private MainThreadHandler handler;

    private EventBus() {
        map = new HashMap<>();
        handler = new MainThreadHandler(Looper.getMainLooper());
    }

    /**
     * 构造函数 DoubleCheck
     */
    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册方法
     */
    public void register(Object subscriber) {
        //拿到订阅者的class
        Class<?> clazz = subscriber.getClass();
        //通过反射获取所有声明的方法
        Method[] methods = clazz.getDeclaredMethods();
        //遍历所有方法
        for (Method m : methods) {
            //当发现有 Subscribe 的注解
            if (m.isAnnotationPresent(Subscribe.class)) {
                Subscribe s = m.getAnnotation(Subscribe.class);
                //拿到参数列表,这里简易处理一个参数的情况
                Class<?> c = m.getParameterTypes()[0];
                //通过参数，从 Map 中取出订阅方法集合
                List<Subscription> list = map.get(c);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(c, list);
                }
                list.add(new Subscription(m, subscriber,
                        s.threadMode() == ThreadMode.POST_THREAD ? ThreadMode.POST_THREAD : ThreadMode.MAIN_THREAD));
            }
        }
    }

    /**
     * 取消注册的方法
     * 该方法整体与 register() 相反
     */
    public void unregister(Object subscriber) {
        //拿到订阅者的class
        Class<?> clazz = subscriber.getClass();
        //通过反射拿到所有声明的方法
        Method[] methods = clazz.getDeclaredMethods();
        //遍历所有方法
        for (Method m : methods) {
            //当发现有 Subscribe 的注解
            if (m.isAnnotationPresent(Subscribe.class)) {
                //拿到参数列表
                Class<?> c = m.getParameterTypes()[0];
                //通过参数拿到对应的集合
                List<Subscription> list = map.get(c);
                if (list != null) {
                    //通过集合遍历
                    for (Subscription subscription : list) {
                        //找到集合对应的订阅者，就移除
                        if (subscription.subscriber == subscriber) {
                            list.remove(subscription);
                        }
                    }
                }
            }
        }
    }

    /**
     * 发送事件方法
     * @param event
     */
    public void post(Object event) {
        Class<?> clazz = event.getClass();
        List<Subscription> list = map.get(clazz);

        //没有注册事件，不处理
        if (list == null) {
            Log.e(TAG, "No class subscribes to this event");
            return;
        }

        //根据订阅事件，从 map 中取出订阅方法集合, 遍历反射调用
        for (Subscription s : list) {
            switch (s.threadMode){
                case ThreadMode.POST_THREAD:
                    try {
                        s.method.invoke(s.subscriber,event);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;

                case ThreadMode.MAIN_THREAD:
                    handler.post(s,event);
                    break;

                default:
                    break;
            }
        }
    }
}
