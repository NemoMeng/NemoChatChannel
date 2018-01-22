/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/24 19:25
 */
package com.nemo.channel.utils;

import com.nemo.channel.utils.StringUtils;
import com.nemo.channel.enums.NemoFramworkMvcPropertiesNameEnums;

import java.lang.reflect.Method;

/**
 * Created by Nemo on 2017/11/24.
 */
public class NemoFrameworkUrlUtils {

    /**
     * 得到一个全的访问路径
     * eg：
     *      controller  index/test
     *      method  user/update
     *      fullUrl /index/test/user/update/
     * @param controller
     * @param method
     * @return
     */
    public static String getFullUrlByControllerAndMethodUrl(Object controller,Method method){
        //controller上注解的url
        String controllerUrl = NemoFrameworkAnnotationUtils.getControllersUrl(controller);
        //方法上注解的url
        String methodUrl = NemoFrameworkAnnotationUtils.getMethodsUrl(method);

        if(controllerUrl == null){
            controllerUrl = "/";
        }

        if(!controllerUrl.startsWith("/")){
            controllerUrl = "/" + controllerUrl;
        }

        if(controllerUrl.length()>1 && controllerUrl.endsWith("/")){
            controllerUrl = controllerUrl.substring(0,controllerUrl.length()-1);
        }

        if(methodUrl == null){
            methodUrl = "";
        }


        if(!methodUrl.startsWith("/")){
            methodUrl = "/" + methodUrl;
        }

        if(!methodUrl.endsWith("/")){
            methodUrl = methodUrl + "/";
        }

        String url = controllerUrl + methodUrl;

        if(url.equals("//")){
            return "/";
        }

        if(url.length()>1){
            url = url.substring(0,url.length()-1);
            url = url + "/";
        }

        url = url.replaceAll("//","/");

        return url;
    }

    /**
     * 得到完整的访问路径
     * @param url
     * @return
     */
    public static String getFullRequestUrl(String url){

        if(url == null){
            return "/";
        }

        if(!url.startsWith("/")){
            url = "/"+url;
        }

        if(!url.endsWith("/")){
            url = url + "/";
        }
        return url;
    }
}