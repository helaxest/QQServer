package com.hyy.qqserver.service;

import com.hyy.qqcommon.Message;
import com.hyy.qqcommon.MessageType;
import com.hyy.qqserver.service.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import java.util.Scanner;
import java.util.Set;


/**
 * Description
 *
 * @author helaxest
 * @date 2021/04/20  16:18
 * @since
 */
public class SendNewsAllService implements Runnable{
    private Scanner scanner=new Scanner(System.in);
    @Override
    public void run() {
        while (true) {
            System.out.println("请输入服务器要推送的新闻/[输入exit退出推送线程线程]");
            String news = Utility.readString(100);
            if(news.equals("exit")){
                break;
            }
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送给所有人说" + news);

            Set<String> onlineUserIds = ManageClientThreads.getOnlineUser(null);//null表示为服务器Id
            for (String userId : onlineUserIds) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(userId).getSocket().getOutputStream());
                    oos.writeObject(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
