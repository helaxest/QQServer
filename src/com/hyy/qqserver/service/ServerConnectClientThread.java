package com.hyy.qqserver.service;

import com.hyy.qqcommon.Message;
import com.hyy.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description
 *
 * @author helaxest
 * @date 2021/04/20  8:50
 * @since
 */
@SuppressWarnings({"all"})
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineDB = null;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;

    }

    public Socket getSocket() {
        return socket;
    }

    public static void setOfflineDB(ConcurrentHashMap<String, ArrayList<Message>> offlineDB) {
        ServerConnectClientThread.offlineDB = offlineDB;
    }

    @Override
    public void run() {
        while (true) {
            //如果offlineDB有关于该用户的离线消息,则发消息
            if (offlineDB.containsKey(userId)) {
                ArrayList<Message> ms = offlineDB.get(userId);
                for (int i = 0; i < offlineDB.get(userId).size(); i++) {
                    Message message = ms.get(i);
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        oos.writeObject(message);
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                offlineDB.remove(userId);
                continue;
            }
            System.out.println("服务端与客户端" + userId + "保持通信,读取数据");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端拉取有牛股列表
                    System.out.println(message.getSender() + " 请求在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //构建message返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    //干两件事，停掉线程，关闭流，把在线用户从hm中去掉
                    System.out.println(message.getSender() + " 请求退出出系统");

                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();
                    break;
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println(message.getSender() + "给" + message.getGetter() + "发消息");                  //判断message.getGetter()在线与否
                    if (ManageClientThreads.getServerConnectClientThread(message.getGetter()) == null) {
                        if (!offlineDB.containsKey(userId)) {
                            ArrayList<Message> messages = new ArrayList<>();
                            messages.add(message);
                            offlineDB.put(message.getGetter(), messages);
                        } else {
                            offlineDB.get(message.getGetter()).add(message);//获得离线用户的消息数据库,并添加
                        }
                    } else {
                        //给特定用户发消息
                        ObjectOutputStream oos =
                                new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(message.getGetter()).getSocket().getOutputStream());
                        oos.writeObject(message);
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    Set<String> userIds = ManageClientThreads.getOnlineUser(message.getSender());
                    if (userIds != null) {
                        for (String onlineUserId : userIds) {
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    if (ManageClientThreads.getServerConnectClientThread(message.getGetter()) == null) {
                        if (!offlineDB.containsKey(userId)) {
                            ArrayList<Message> messages = new ArrayList<>();
                            messages.add(message);
                            offlineDB.put(message.getGetter(), messages);
                        } else {
                            offlineDB.get(message.getGetter()).add(message);//获得离线用户的消息数据库,并添加
                        }
                    } else {
                        ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(message.getGetter()).getSocket().getOutputStream());
                        oos.writeObject(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
