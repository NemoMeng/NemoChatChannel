/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/23 19:07
 */
package com.nemo.channel.server;

import com.nemo.channel.utils.CollectionUtils;
import com.nemo.channel.utils.MapUtils;
import com.nemo.channel.utils.NemoFrameworkAnnotationUtils;
import com.nemo.channel.utils.NemoFrameworkUrlUtils;
import com.nemo.channel.annotation.UrlMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 包扫描器，扫描报下的指定注解，存放到核心框架内存中
 * Created by Nemo on 2017/11/23.
 */
public class NemoFrameworkCorePackageScaner {

    /**
     * 开始扫描指定包下面的所有注解为Controller的类及其下面全部注解为UrlMapping的方法
     * @param packageName
     */
    public static void scan(String packageName) throws Exception {
        ServerCore core = ServerCore.core();
        if(!core.isInited()) {
            //得到所有的controller
            List<Object> classList = NemoFrameworkAnnotationUtils.getAllController(packageName);
            if (CollectionUtils.isNotEmpty(classList)) {
                for (Object clss : classList) {
                    //所有controller下的mapping
                    List<Method> methods = NemoFrameworkAnnotationUtils.getMapMethodsByClass(clss.getClass());
                    if (CollectionUtils.isNotEmpty(methods)) {
                        for (Method method : methods) {

                            //根据controller + method上的注解得到完整的访问路径
                            String key = NemoFrameworkUrlUtils.getFullUrlByControllerAndMethodUrl(clss,method);
                            //key = key + NemoFrameworkUrlUtils.getfilterSubffix();


                            if(core.getRoute(key) != null){
                                throw new Exception("抱歉，有重复的controller+mapping定义。");
                            }

                            //添加到核心控制器中
                            core.addRoute(key,clss,method);
                        }
                    }
                }
            }
        }
    }

}