package com.hyy.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Description
 *
 * @author helaxest
 * @date 2021/04/20  9:04
 * @since
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }

    public static String getOnlineUser() {
        //集合遍历
        Set<String> keySet = hm.keySet();
        String onlineUsers = "";
        for (String onlineUser : keySet) {
            onlineUsers += onlineUser + " ";
        }
        return onlineUsers;
    }

    public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
    }

    public static Set<String> getOnlineUser(String userId){
        if(userId==null){
           return hm.keySet();
        }
        Set<String> keySet = hm.keySet();
        keySet.remove(userId);
        return keySet;
    }

}
