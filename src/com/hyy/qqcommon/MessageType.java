package com.hyy.qqcommon;

/**
 * Description
 * 消息类型
 *
 * @author helaxest
 * @date 2021/04/19  13:47
 * @since
 */
public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMM_MES = "3";//普通信息
    String MESSAGE_GET_ONLINE_FRIEND = "4";//要求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_TO_ALL_MES="7";//群发
    String MESSAGE_FILE_MES="8";//文件

}
