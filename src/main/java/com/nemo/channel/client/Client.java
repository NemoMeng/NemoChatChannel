/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 10:26
 */
package com.nemo.channel.client;

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.AuthBean;
import com.nemo.channel.bean.RequestBean;
import com.nemo.channel.bean.ResponseBean;
import com.nemo.channel.enums.ResponseCode;
import com.nemo.channel.utils.CharsetUtils;
import com.nemo.channel.utils.Helper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * 客户端实现
 * Created by Nemo on 2018/1/19.
 */
public class Client{

    private AsynchronousSocketChannel channel;
    private Helper helper;
    private CountDownLatch latch;
    private final Queue<ByteBuffer> queue = new LinkedList<>();
    private boolean writing = false;
    final ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);

    private ClientUI ui;

    private Client(AsynchronousChannelGroup channelGroup, CountDownLatch latch) throws IOException, InterruptedException{
        this.latch = latch;
        helper = new Helper();
        initChannel(channelGroup);
    }

    /**
     * 初始化一个绘画
     * @param channelGroup
     * @throws IOException
     */
    private void initChannel(AsynchronousChannelGroup channelGroup) throws IOException {
        //在默认channel group下创建一个socket channel
        channel = AsynchronousSocketChannel.open(channelGroup);
        //设置Socket选项
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client c = getClient();
    }

    /**
     * 得到一个客户端
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Client getClient() throws IOException, InterruptedException {
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
        CountDownLatch latch = new CountDownLatch(2);
        Client c = new Client(channelGroup, latch);
        c.dealConnect();
        return c;
    }

    public void setUI(ClientUI ui){
        this.ui = ui;
    }

    /**
     * 连接服务器
     */
    private void dealConnect(){
        channel.connect(new InetSocketAddress("localhost", 8080), null, new CompletionHandler<Void, Void>() {

            /**
             * 连接成功处理
             * @param result
             * @param attachment
             */
            @Override
            public void completed(Void result, Void attachment) {
                //异步调用OS读取服务器发送的消息
                dealRead(attachment);
            }

            /**
             * 连接失败处理
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("client connect to server failed: " + exc);

                try {
                    shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        });
    }

    /**
     * 读取服务端的响应数据
     * @param attachment
     */
    private void dealRead(Object attachment){
        readBuffer.clear();
        channel.read(readBuffer, attachment, new CompletionHandler<Integer, Object>(){

            @Override
            public void completed(Integer result, Object attachment) {
                dealCallBack(result,attachment);
            }

            /**
             * 回调
             * @param result
             */
            private void dealCallBack(Integer result,Object attachment) {
                //异步读取完成后处理
                if(result > 0){
                    readBuffer.flip();
                    try {
                        CharBuffer charBuffer = CharsetUtils.decode(readBuffer);
                        String answer = charBuffer.toString();
                        System.out.println(Thread.currentThread().getName() + "---" + answer);
                        readBuffer.clear();

                        ResponseBean responseBean = JSONObject.parseObject(answer,ResponseBean.class);
                        if(!responseBean.getCode().equals(ResponseCode.SUCCESS.name())) {
                            if(responseBean.getCode().equals(ResponseCode.MSG_TYPE.name())){
                                ui.addShow(responseBean.getData().toString());
                            }else {
                                ui.addShow(answer);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dealRead(attachment);
                }
                else{
                    //对方已经关闭channel，自己被动关闭，避免空循环
                    try {
                        shutdown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /**
             * 读取失败处理
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("client read failed: " + exc);
                try {
                    shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 客户端关闭
     * @throws IOException
     */
    public void shutdown() throws IOException {
        if(channel != null){
            channel.close();
        }

        latch.countDown();
    }

    /**
     * Enqueues a write of the buffer to the channel.
     * The call is asynchronous so the buffer is not safe to modify after
     * passing the buffer here.
     *
     * @param buffer the buffer to send to the channel
     */
    private void writeMessage(final ByteBuffer buffer) {
        boolean threadShouldWrite = false;

        synchronized(queue) {
            queue.add(buffer);
            // Currently no thread writing, make this thread dispatch a write
            if (!writing) {
                writing = true;
                threadShouldWrite = true;
            }
        }

        if (threadShouldWrite) {
            writeFromQueue();
        }
    }

    /**
     * 从队列里读取消息发送到服务器
     */
    private void writeFromQueue() {
        ByteBuffer buffer;
        synchronized (queue) {
            //从队列取数据
            buffer = queue.poll();
            if (buffer == null) {
                writing = false;
            }
        }

        //如果有新的数据，则推送
        if (writing) {
            writeBuffer(buffer);
        }
    }

    /**
     * 向服务器推送一个缓冲流
     * @param buffer
     */
    private void writeBuffer(ByteBuffer buffer) {
        channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    channel.write(buffer, buffer, this);
                } else {
                    // Go back and check if there is new data to write
                    writeFromQueue();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }

    /**
     * 给服务端发送一个消息
     * @param msg
     * @throws CharacterCodingException
     */
    public void writeStringMessage(String msg) throws CharacterCodingException {
        writeMessage(CharsetUtils.encode(msg));
    }

    /**
     * 给服务端发一个消息
     * @param method        请求的路径
     * @param parameter     请求参数
     * @throws CharacterCodingException
     */
    public void writeMsg(String method,Object parameter) throws CharacterCodingException {
        RequestBean requestBean = new RequestBean();
        requestBean.setMethod(method);
        requestBean.setParams(parameter);

        writeStringMessage(JSONObject.toJSONString(requestBean));
    }

}