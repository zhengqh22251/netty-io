package com.zqh.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author：zhengqh
 * @date 2020/2/13 19:31
 **/
public class BioServer {
    private ServerSocket serverSocket;

    public BioServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务启动，监听端口:"+port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        //循环读取数据
       while (true){
           try {
               Socket socket = serverSocket.accept();//阻塞
               //读取客户端发送的数据
               InputStream is =  socket.getInputStream();
                byte[] bytes = new byte[1024];
                int length = is.read(bytes);
                if(length>0){
                    String msg= new  String(bytes,0,length);
                    System.out.println("收到数据："+msg);
                }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public static void main(String[] args) {
        new BioServer(8080).listen();
    }
}
