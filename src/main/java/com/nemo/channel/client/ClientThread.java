/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 11:05
 */
package com.nemo.channel.client;

import java.io.IOException;

/**
 * Created by Nemo on 2018/1/23.
 */
public class ClientThread implements Runnable {

    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (true){
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
