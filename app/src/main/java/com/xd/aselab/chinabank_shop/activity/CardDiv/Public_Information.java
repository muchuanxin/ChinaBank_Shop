package com.xd.aselab.chinabank_shop.activity.CardDiv;

/**
 * Created by Administrator on 2017/10/8.
 */

public class Public_Information {

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }


    private String title;
    private String time;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    private String sender;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

}
