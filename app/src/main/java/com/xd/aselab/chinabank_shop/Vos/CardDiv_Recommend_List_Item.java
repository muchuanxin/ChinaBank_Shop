
package com.xd.aselab.chinabank_shop.Vos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dorise on 2017/10/5.
 */

public class CardDiv_Recommend_List_Item{
    private String id="";
    private String name="";
    private String time="";
    private String Tel="";
    private String Div_Money="";
    private String Div_num="";
    private String Purchase="";
    private String evaluation="";
    private String confirm="";


    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id= id;
    }
    public void setTime(String time){
        this.time=time;
    }
    public String getTime(){
        return time;
    }

    public void setName(String name){
        this.name=name;
    }


    public String getName(){
        return name;
    }

    public void setTel(String Tel){
        this.Tel=Tel;
    }
    public String getTel(){
        return Tel;
    }

    public void setDiv_Money(String money){
        this.Div_Money=money;
    }
    public String getDiv_Money(){
        return Div_Money;
    }

    public void setDiv_num(String num){
        this.Div_num=num;
    }
    public String getDiv_num(){
        return Div_num;
    }

    public void setPurchase(String purchase){
        this.Purchase=purchase;
    }
    public String getPurchase(){
        return Purchase;
    }

    public void setevaluation(String evaluation){
        this.evaluation=evaluation;
    }
    public String getevaluation(){
        return evaluation;
    }

    public void setconfirm(String confirm){
        this.confirm=confirm;
    }
    public String getconfirm(){
        return confirm;
    }


}
