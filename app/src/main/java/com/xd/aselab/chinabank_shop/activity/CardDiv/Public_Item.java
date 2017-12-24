package com.xd.aselab.chinabank_shop.activity.CardDiv;



import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dorise on 2017/9/26.
 */

public class Public_Item {
    private String public_head="";
    private String public_body="";
    private String public_time="";

    public Public_Item(String head){
        public_head=head;
//        public_body=body;
    }

    public String get_public_time(){
        return public_time;
    }
    public void set_public_time(String time){
         public_time=time;
    }
    public String get_public_head(){
        return public_head;
    }
    public void set_public_head(String head){
        this.public_head=head;
    }
    public String get_public_body(){
        return public_body;
    }

    public void set_public_body(String body){
        this.public_body=body;
    }
}
