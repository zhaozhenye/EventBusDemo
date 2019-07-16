package com.zzy.eventbus;

import android.os.Handler;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管理类 负责将activity的方法添加到其中，其他activity可以在里面寻找方法并调用
 */
//将MainActivity的某个方法加入到Eventbus中，需要个容器来包装这些方法
public class EventBus {

    //定义存放方法的容器
    private Map<Object, List<SubsribleMethod>> cacheMap;

    private static volatile EventBus eventBus;

    private Handler handler;
    ExecutorService executorService;

    private EventBus() {
        cacheMap = new HashMap<>();
        handler = new Handler();
        executorService = Executors.newFixedThreadPool(1);
    }

    public static EventBus getInstance() {
        if (eventBus == null) {
            synchronized (EventBus.class) {
                if (eventBus == null) {
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }

    /**
     * @param obj
     */
    public void register(Object obj) {
        //  寻找到所有object 类，带有subsrible注解的方法
        List<SubsribleMethod> list = cacheMap.get(obj);
        if (list == null) {
            list = findSubscribleMethods(obj);
            cacheMap.put(obj, list);
        }
    }

    private List<SubsribleMethod> findSubscribleMethods(Object obj) {
        List<SubsribleMethod> list = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        while (clazz != null) {
            //系统的肯定不带有自定义注解，这里需要优化
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            //得到类中所有的方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                //只找带有subsrible注解的方法
                Annotation subscrible = method.getAnnotation(Subscrible.class);
                if (subscrible == null) {
                    continue;
                }
                //判断带有subscrible方法的参数类型
                Class<?>[] types = method.getParameterTypes();
                if (types.length != 1) {
                    Log.e("error-->", "不支持多个参数传递");
                }
                ThreadMode mode = ((Subscrible) subscrible).threadMode();

                SubsribleMethod sb = new SubsribleMethod(mode, method, types[0]);
                list.add(sb);
            }
            clazz = clazz.getSuperclass();


        }

        return list;
    }


    /**
     * 根据参数obj类型判断来回调哪个方法
     * 如果参数类型相同，那么这2个方法会被同时调用（即使好处也是坏处）
     *
     * @param type
     */
    public void post(final Object type) {
        //直接循环cachemap中所有的方法
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            List<SubsribleMethod> list = cacheMap.get(obj);
            for (final SubsribleMethod subsribleMethod : list) {
                //得到方法的参数 isAssignableFrom 前面对象对应的类是不是后面类的父类或者接口
                if (subsribleMethod.getType().isAssignableFrom(type.getClass())) {
                    ThreadMode mode = subsribleMethod.getMode();
                    switch (mode) {
                        case MAIN:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subsribleMethod, obj, type);
                                }
                            });
                            break;
                        case BACKGROUND:
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subsribleMethod, obj, type);
                                }
                            });

                            break;
                        default:
                            break;
                    }


                }
            }
        }
    }

    private void invoke(SubsribleMethod subsribleMethod, Object obj, Object type) {
        Method method = subsribleMethod.getMethod();
        try {
            method.invoke(obj, type);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //留一个问题：加入跳转过去的页面还没有实例化，怎么办? 现在可以直接调用是因为已经初始化过了
}
