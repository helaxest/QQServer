package com.hyy.qqserver.service;

import com.hyy.qqcommon.Message;
import com.hyy.qqcommon.MessageType;
import com.hyy.qqcommon.User;
import com.sun.org.apache.xml.internal.security.Init;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description
 *
 * @author helaxest
 * @date 2021/04/20  8:33
 * @since
 */
public class QQServer {
    private ServerSocket ss = null;
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineDB = new ConcurrentHashMap<>();//离线数据

    static {//静态代码块 初始化 validUsers
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("101", new User("101", "123456"));
        validUsers.put("102", new User("102", "123456"));
        validUsers.put("103", new User("103", "123456"));
        validUsers.put("104", new User("104", "123456"));
    }
    private boolean checkUser(String userId,String pwd){
       if(validUsers.containsKey(userId)) {
           if(validUsers.get(userId).getPassword().equals(pwd)){
               return true;
           }
           System.out.println("userId为"+userId+"的用户输入密码错误");
         return false ;
       }
        System.out.println("userId为"+userId+"的用户不存在");
       return false;
    }

    public QQServer() {

        try {
            ss = new ServerSocket(9999);
            System.out.println("QQServer在9999监听");
            new Thread(new SendNewsAllService()).start();
            Message message = new Message();
            while (true) {//当与某个客户端建立连接
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();
                //验证user
                if (checkUser(u.getUserId(), u.getPassword())) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    //创建一个线程 保持通信
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket,u.getUserId());
                    serverConnectClientThread.start();

                    ServerConnectClientThread.setOfflineDB(offlineDB);//共享离线用户
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);
                } else {
                    System.out.println("用户" + u.getUserId() + "登陆失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
