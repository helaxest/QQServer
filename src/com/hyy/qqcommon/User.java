package com.hyy.qqcommon;

import java.io.Serializable;

/**
 * Description
 * 用户信息
 * @author helaxest
 * @date 2021/04/19  13:39
 * @since
 */
public class User implements Serializable {
    private String userId;//用户名
    private String password;//密码
    private static final long serialVersionUID=1L;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
