/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/27 10:58
 */
package com.nemo.channel.utils;

import com.nemo.channel.server.ServerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

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
     * 参数处理
     * @param method
     * @param params
     * @return
     */
    public static Object[] dealMethodParams(Method method, Map<String,Object> params){
        Parameter[] parameters = method.getParameters();
        if (parameters!=null) {
            Object args[] = new Object[parameters.length];
            for(int i=0;i<parameters.length;i++){
                Parameter parameter = parameters[i];
                String name = parameter.getName();
                args[i] = params.get(name);
            }
            return args;
        }
        return new Object[0];
    }

    /**
     * 执行方法
     * @param bean
     * @param method
     * @param params
     * @return
     */
    public static Object invokeMehod(Object bean, Method method, Object params) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes == null || parameterTypes.length<=0){
            return invokeMehod(bean,method,new Object[0]);
        }

        ServerContext context = new ServerContext();
        context.setParameter(params);

        Object attrs[] = new Object[parameterTypes.length];
        for(int i=0;i<parameterTypes.length;i++){
            Class cls = parameterTypes[i];
            if(cls.getName().equals("com.nemo.channel.server.ServerContext")){
                attrs[i] = context;
            }else{
                attrs[i] = new Object();
            }
        }

        try {

            return invokeMehod(bean,method,attrs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
    public static Object invokeMehod(Object bean, Method method,Object[] args) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] types = method.getParameterTypes();
        int argCount = args == null ? 0 : args.length;
        // 参数个数对不上
        ExceptionUtil.makeRunTimeWhen(argCount != types.length, "%s in %s", method.getName(), bean);
        // 转参数类型
        for (int i = 0; i < argCount; i++) {
            args[i] = cast(args[i], types[i]);
        }
        return method.invoke(bean, args);
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