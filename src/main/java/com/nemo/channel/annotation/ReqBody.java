/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/12/15 11:11
 */
package com.nemo.channel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求体参数的定义注解，该注解只解析json类型参数
 * Created by Nemo on 2017/12/15.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReqBody {
}