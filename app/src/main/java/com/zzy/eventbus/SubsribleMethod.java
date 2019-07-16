package com.zzy.eventbus;

import java.lang.reflect.Method;

public class SubsribleMethod {
    /**
     * 线程模式
     */
    private ThreadMode mode;
    /**
     * 方法体本身
     */
    private Method method;
    private Class<?> type;

    public ThreadMode getMode() {
        return mode;
    }

    public void setMode(ThreadMode mode) {
        this.mode = mode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public SubsribleMethod(ThreadMode mode, Method method, Class<?> type) {
        this.mode = mode;
        this.method = method;
        this.type = type;
    }
}
