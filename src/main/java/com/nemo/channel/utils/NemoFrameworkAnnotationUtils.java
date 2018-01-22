/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/23 19:02
 */
package com.nemo.channel.utils;

import com.nemo.channel.annotation.Controller;
import com.nemo.channel.annotation.UrlMapping;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * 注解操作工具类
 * Created by Nemo on 2017/11/23.
 */
public class NemoFrameworkAnnotationUtils {

    /**
     * 得到controller上注解的地址
     * @param controller
     * @return
     */
    public static String getControllersUrl(Object controller){
        Controller anno = controller.getClass().getAnnotation(Controller.class);
        return anno.value();
    }

    /**
     * 得到method上注解的地址
     * @param method
     * @return
     */
    public static String getMethodsUrl(Method method){
        UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
        return urlMapping.value();
    }

    /**
     * 得到所有的controller对象
     * @param packageName 需要扫描的包名
     * @return
     */
    public static List<Object> getAllController(String packageName) {
        //第一个class类的集合
        List<Class<?>> classes = new ArrayList<Class<?>>();
        //是否循环迭代
        boolean recursive = true;
        //获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的文件
        Enumeration<URL> dirs;
        try {
            //读取指定package下的所有class
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //判断是否以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
         }

        List<Object> stringList = new ArrayList<Object>();
        for (Class<?> clazz : classes) {
            //循环获取所有的类，判断类是否注解了controller
            Controller controllerAnno = clazz.getAnnotation(Controller.class);
            if(controllerAnno!=null){
                try {
                    Object o = clazz.newInstance();
                    stringList.add(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringList;
    }

    /**
     * 得到类下面所有带Mapping注解的方法
     * @param clss
     * @return
     */
    public static List<Method> getMapMethodsByClass(Class clss){
        List<Method> mappings = new ArrayList<>();
        //获取类的所有方法
        Method[] methods = clss.getMethods();
        for (Method method : methods) {
            //获取RequestMapping注解
            UrlMapping annotation = method.getAnnotation(UrlMapping.class);
            if (annotation != null) {
                mappings.add(method);
            }
        }
        return mappings;
    }

    /**
     * 根据包名，取得其下的所有类文件
     * @param packageName 包名
     * @param packagePath 包的实际路径
     * @param recursive  是否需要迭代下去查询
     * @param classes 存放类对象的集合
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes){
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            }
            else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //添加到集合中去
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}