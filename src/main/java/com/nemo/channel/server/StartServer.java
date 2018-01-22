/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:25
 */
package com.nemo.channel.server;

import java.io.IOException;

/**
 * 服务启动入口
 * Created by Nemo on 2018/1/19.
 */
public class StartServer {

    public static void main(String args[]) throws Exception {
        ServerCore core = ServerCore.core();
        if(!core.isInited()) {
            //初始化容器
            NemoFrameworkCorePackageScaner.scan("com.nemo.channel.controller");
            //服务开启
            Server.open();
            //初始化完成
            core.setInited(true);
        }

    }

}
