package com.zqh.io.bio;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * @Author：zhengqh
 * @date 2020/2/13 19:39
 **/
public class BioClient {
    public static void main(String[] args) {
        try {
           /* ServerSocket serverSocket = new ServerSocket();
            OutputStream os = new OutputStream();*/
            Socket socket = new Socket("localhost",8080);
            OutputStream os =  socket.getOutputStream();
            String msg  = UUID.randomUUID().toString();
            System.out.println("发送数据："+msg);
            os.write(msg.getBytes());
            os.flush();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
