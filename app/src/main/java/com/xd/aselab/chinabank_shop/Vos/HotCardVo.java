package com.xd.aselab.chinabank_shop.Vos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liuhaoxian on 2016/6/15.
 */
public class HotCardVo implements Serializable{
    private String cardName;
    private String imageUrl;
    private String desc;
    private String content;
    private ArrayList<String> items=new ArrayList<String>();

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}
