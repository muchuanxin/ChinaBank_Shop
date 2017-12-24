package com.xd.aselab.chinabank_shop.chat.entity;

import java.io.Serializable;

/**
 * Created by xinye on 2017/11/4.
 */

public class ChatMsgEntity implements Serializable {

    private boolean isLeft;
    private String account;
    private String name;
    private String head;
    private String time;
    private String content;
    private ChatMsgType type;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatMsgType getType() {
        return type;
    }

    public void setType(ChatMsgType type) {
        this.type = type;
    }
}
