package com.zqh.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author：zhengqh
 * @date 2020/2/13 19:47
 **/
public class NioServerDemo {
    private int port;
    //轮询器 selector
    private Selector selector;
    //缓存区 buffer  指定容量
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    //初始化
    public  NioServerDemo(int port){
        try {
            this.port = port;
            //开放通道
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            //绑定ip 端口
            socketChannel.bind(new InetSocketAddress(this.port));
            // bio 升级到 nio  兼容bio  参数设置是否使用阻塞模式
            socketChannel.configureBlocking(false);
            //初始化selector
            selector = Selector.open();
            // 注册 表示现在是可以处理请求状态
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   public void listen(){
       System.out.println("listen on :"+this.port);
       //一直获取请求
       while(true){
           try {
               //轮询器获取请求
               selector.select();
               Set<SelectionKey> keys= selector.selectedKeys();
               //不断地迭代，就叫轮询
               //同步体现在这里，因为每次只能拿一个key，每次只能处理一种状态
               Iterator<SelectionKey> iters = keys.iterator();
               while(iters.hasNext()){
                   SelectionKey selectionKey = iters.next();
                   iters.remove();//处理完了  删除掉
                   //处理请求
                   process(selectionKey);
               }

           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }


    //具体办业务的方法、
    //每一次轮询就是调用一次process方法，而每一次调用，只能干一件事
    //在同一时间点，只能干一件事
    private void process(SelectionKey key) {
        //针对于每一种状态给一个反应
        try {
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            //这个方法体现非阻塞，不管你数据有没有准备好
            //你给我一个状态和反馈
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            //当数据准备就绪的时候，将状态改为可读
            key= channel.register(selector,SelectionKey.OP_READ);
        }else if(key.isReadable()){
            //key.channel 从多路复用器中拿到客户端的引用
            SocketChannel server = (SocketChannel)key.channel();
            int len = server.read(buffer);
            if(len>0){
               /* public final Buffer flip() {
                    limit = position;
                    position = 0;
                    mark = -1;
                    return this;
                }*/
                buffer.flip();
                String content = new String(buffer.array(),0,len);
                key = server.register(selector,SelectionKey.OP_WRITE);
                //在key上携带一个附件，一会再写出去
                key.attach(content);
                System.out.println("读取数据："+content);

            }else if(key.isWritable()){
                SocketChannel channel = (SocketChannel) key.channel();
                String content = (String) key.attachment();
                channel.write(ByteBuffer.wrap(("输出："+content).getBytes()));
                channel.close();
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NioServerDemo(8080).listen();
    }
}


