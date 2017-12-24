package com.xd.aselab.chinabank_shop.Vos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class MyWorkersInfoVo implements Serializable {

    private String workerName;
    private String cardsNumber;
    private String tel;
    private ArrayList<String> items=new ArrayList<String>();

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getCardsNumber() {
        return cardsNumber;
    }

    public void setCardsNumber(String cadsNumber) {
        this.cardsNumber = cadsNumber;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}
