package com.xd.aselab.chinabank_shop.Vos;

/**
 * Created by Dorise on 2017/10/9.
 */

public class Personal_Loan {
    private String personalAccount;
    private String personalWorkerName;
    private String personalWorkerTel;
    private String personalType;
    private String personalCompany;
    private String personalManager_account;
    private String personalManager_name;

    private String personalManager_tele;
    private String personalHead_image;

//public void CardDivInfo(){
//    head_image="http:// 192.168.0.129/ChinaBank/images/head.jpg";
//}
    public String getPersonalAccount(){return personalAccount;}
    public void setPersonalAccount(String account){personalAccount=account;}

    public String getPersonalWorkerName(){return personalWorkerName;}
    public void setPersonalWorkerName(String name){
        this.personalWorkerName=name;
    }

    public String getPersonalWorkerTel(){
        return personalWorkerTel;
    }
    public void setPersonalWorkerTel(String tel){personalWorkerTel=tel;}

    public String getPersonalType(){
        return personalType;
    }
    public void setPersonalType(String type){personalType=type;}

    public String getPersonalCompany(){
        return personalCompany;
    }
    public void setPersonalCompany(String company){personalCompany=company;}

    public String getPersonalManager_account(){
        return personalManager_account;
    }
    public void setPersonalManager_account(String account){this.personalManager_account=account;}

    public String getPersonalManager_name(){
        return personalManager_name;
    }
    public void setPersonalManager_name(String name){personalManager_name=name;}

    public String getPersonalManager_tele(){
        return personalManager_tele;
    }
    public void setPersonalManager_tele(String tel){personalManager_tele=tel;}

    public String getPersonalHead_image(){
        return personalHead_image;
    }
    public void setPersonalHead_image(String head){personalHead_image=head;}


}
