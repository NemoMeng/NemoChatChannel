/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/27 10:58
 */
package com.nemo.channel.utils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 反射相关操作工具
 * Created by Nemo on 2017/11/27.
 */
public class ReflectUtils  {

    /**
     * 得到实例
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Object newInstance(String className) throws ClassNotFoundException{
        Object object = Class.forName(className);
        if(null != object){
            return object;
        }
        return null;
    }

    /**
     * 执行方法
     * @param bean
     * @param method
     * @param args
     * @return
     */
    public static Object invokeMehod(Object bean, Method method,Object[] args) {
        try {
            Class<?>[] types = method.getParameterTypes();
            int argCount = args == null ? 0 : args.length;
            // 参数个数对不上
            ExceptionUtil.makeRunTimeWhen(argCount != types.length, "%s in %s", method.getName(), bean);
            // 转参数类型
            for (int i = 0; i < argCount; i++) {
                args[i] = cast(args[i], types[i]);
            }
            return method.invoke(bean, args);
        } catch (Exception e) {
            ExceptionUtil.makeRuntime(e);
        }
        return null;
    }

    /**
     * 类型转换
     * @param value
     * @param type
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object value, Class<T> type) {
        if (value != null && !type.isAssignableFrom(value.getClass())) {
            if (is(type, int.class, Integer.class)) {
                value = Integer.parseInt(String.valueOf(value));
            } else if (is(type, long.class, Long.class)) {
                value = Long.parseLong(String.valueOf(value));
            } else if (is(type, float.class, Float.class)) {
                value = Float.parseFloat(String.valueOf(value));
            } else if (is(type, double.class, Double.class)) {
                value = Double.parseDouble(String.valueOf(value));
            } else if (is(type, boolean.class, Boolean.class)) {
                value = Boolean.parseBoolean(String.valueOf(value));
            } else if (is(type, String.class)) {
                value = String.valueOf(value);
            }
        }
        return (T) value;
    }

    /**
     * 对象是否其中一个
     * @param obj
     * @param mybe
     * @return
     */
    public static boolean is(Object obj, Object... mybe) {
        if (obj != null && mybe != null) {
            for (Object mb : mybe)
                if (obj.equals(mb))
                    return true;
        }
        return false;
    }

}