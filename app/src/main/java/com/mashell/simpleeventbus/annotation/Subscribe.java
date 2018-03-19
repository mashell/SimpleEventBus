package com.mashell.simpleeventbus.annotation;

import com.mashell.simpleeventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mashell on 18/3/19.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    int threadMode () default ThreadMode.POST_THREAD;
}
