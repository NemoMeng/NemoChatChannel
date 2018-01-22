/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 10:25
 */
package com.nemo.channel.server;

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.RequestBean;
import com.nemo.channel.bean.ResponseBean;
import com.nemo.channel.bean.RouteBean;
import com.nemo.channel.controller.ContextController;
import com.nemo.channel.enums.ResponseCode;
import com.nemo.channel.exception.ChannelException;
import com.nemo.channel.utils.CharsetUtils;
import com.nemo.channel.utils.Helper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;

/**
 * 服务提供者
 * Created by Nemo on 2018/1/19.
 */
public class Server {
    private final AsynchronousServerSocketChannel server;
    //写队列，因为当前一个异步写调用还没完成之前，调用异步写会抛WritePendingException
    //所以需要一个写队列来缓存要写入的数据，这是AIO比较坑的地方
    private final Queue<ByteBuffer> queue = new LinkedList<ByteBuffer>();
    private boolean writing = false;

    public static void main(String[] args) throws IOException{
      open();
    }

    private Server() throws IOException{
        //设置线程数为CPU核数
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
        server = AsynchronousServerSocketChannel.open(channelGroup);
        //重用端口
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        //绑定端口并设置连接请求队列长度
        server.bind(new InetSocketAddress(8080), 80);
    }

    /**
     * 开启服务
     * @throws IOException
     */
    public static void open() throws IOException {
        Server server = new Server();
        server.dealConnect();
    }

    /**
     * 处理每个请求
     */
    private void dealConnect() {
        System.out.println(Thread.currentThread().getName() + ": run in listen method" );
        //开始接受第一个连接请求
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>(){
            @Override
            public void completed(AsynchronousSocketChannel channel,
                                  Object attachment) {
                System.out.println(Thread.currentThread().getName() + ": run in accept completed method" );

                server.accept(attachment, this);
                //处理连接读写
                dealRead(channel,attachment);
            }

            /**
             * 服务器接受连接失败处理
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("server accept failed: " + exc);
            }

        });
    }

    /**
     * 处理读取请求
     * @param channel
     * @param attachment
     */
    private void dealRead(final AsynchronousSocketChannel channel,Object attachment) {
        //每个AsynchronousSocketChannel，分配一个缓冲区
        final ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
        readBuffer.clear();
        channel.read(readBuffer, attachment, new CompletionHandler<Integer, Object>(){

            @Override
            public void completed(Integer count, Object attachment) {
                if(count > 0){
                    try{
                        readBuffer.flip();
                        CharBuffer charBuffer = CharsetUtils.decode(readBuffer);
                        String question = charBuffer.toString();

                        System.out.println(Thread.currentThread().getName() +"收到消息："+question);

                        ResponseBean responseBean = new ResponseBean();;
                        RequestBean requestBean = JSONObject.parseObject(question,RequestBean.class);
                        if(requestBean.getMethod() == null){    //没有说明任何请求方法，异常连接，强行中断
                            shutdownChannel(channel);
                        }else {
                            try {
                                RouteBean routeBean = ServerCore.core().getRoute(requestBean.getMethod());
                                if(routeBean == null){
                                    throw new ChannelException(ResponseCode.METHOD_NOT_FOUND.name(),ResponseCode.METHOD_NOT_FOUND.getRemark());
                                }

                                Object invoke = routeBean.getMethod().invoke(routeBean.getController(), requestBean.getParams());
                                if(invoke != null){
                                    responseBean = new ResponseBean();
                                    responseBean.setData(invoke);
                                }
                            } catch (Throwable e) {
                                if(e instanceof InvocationTargetException){
                                    InvocationTargetException exception = (InvocationTargetException)e;
                                    e = exception.getTargetException();
                                }
                                if(e instanceof ChannelException){
                                    ChannelException channelException = (ChannelException) e;
                                    responseBean.setCode(channelException.getCode());
                                    responseBean.setMsg(channelException.getMsg());
                                }else {
                                    responseBean.setCode(ResponseCode.COMMON_ERROR.name());
                                    responseBean.setMsg(ResponseCode.COMMON_ERROR.getRemark());
                                }
                            }
                        }
                        writeStringMessage(channel,JSONObject.toJSONString(responseBean));
                        readBuffer.clear();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }finally {
                        //继续读取
                        dealRead(channel,attachment);
                    }
                }
                else{
                    //如果客户端关闭socket，那么服务器也需要关闭，否则浪费CPU
                    System.out.println(Thread.currentThread().getName() +"客户端请求关闭");
                    shutdownChannel(channel);
                }
            }

            /**
             * 服务器读失败处理
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("server read failed: " + exc);
                exc.printStackTrace();
                if(channel != null){
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    /**
     * 关闭连接
     * @param channel
     */
    private void shutdownChannel(AsynchronousSocketChannel channel){
        if(channel!=null){
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Enqueues a write of the buffer to the channel.
     * The call is asynchronous so the buffer is not safe to modify after
     * passing the buffer here.
     *
     * @param buffer the buffer to send to the channel
     */
    private void writeMessage(final AsynchronousSocketChannel channel, final ByteBuffer buffer) {
        boolean threadShouldWrite = false;

        synchronized(queue) {
            queue.add(buffer);
            // Currently no thread writing, make this thread dispatch a write
            if (!writing) {
                writing = true;;
                threadShouldWrite = true;
            }
        }

        if (threadShouldWrite) {
            writeFromQueue(channel);
        }
    }

    private void writeFromQueue(final AsynchronousSocketChannel channel) {
        ByteBuffer buffer;

        synchronized (queue) {
            buffer = queue.poll();
            if (buffer == null) {
                writing = false;
            }
        }

        // No new data in buffer to write
        if (writing) {
            writeBuffer(channel, buffer);
        }
    }

    private void writeBuffer(final AsynchronousSocketChannel channel, ByteBuffer buffer) {
        channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    channel.write(buffer, buffer, this);
                } else {
                    // Go back and check if there is new data to write
                    writeFromQueue(channel);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("server write failed: " + exc);
                exc.printStackTrace();
            }
        });
    }

    /**
     * Sends a message
     * @param msg
     * @throws CharacterCodingException
     */
    private void writeStringMessage(final AsynchronousSocketChannel channel, String msg) throws CharacterCodingException {
        writeMessage(channel, Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(msg)));
    }
}
