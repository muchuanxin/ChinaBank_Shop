package com.xd.aselab.chinabank_shop.Vos;

/**
 * Created by Administrator on 2016/6/13 0013.
 */
public class Shopkeeper {

    private String shopkeeperAccount;
    private String shopkeeperPassword;
    private String shopkeeperTel;
    private String sshopName;
    private String sserviceDescribe;
    private String slocDescribe;
    private String sOwnerName;
    private String sprovince;
    private String scity;
    private String scounty;
    private String sstreet;
    private String sindustryType;
    private String smanagerAccount;//银行卡客户经理工号
    private String smanagerTel;
    private String stype;
    private String scookie;

    public String getShopkeeperAccount(){return shopkeeperAccount;}
    public void setShopkeeperAccount(String shopkeeperAccount ){
        this.shopkeeperAccount=shopkeeperAccount;
    }

    public String getShopkeeperPassword(){return shopkeeperPassword;}
    public void setShopkeeperPassword(String shopkeeperPassword){
        this.shopkeeperPassword=shopkeeperPassword;
    }

    public String getShopkeeperTel(){return shopkeeperTel;}
    public void setShopkeeperTel(String shopkeeperTel){
        this.shopkeeperTel=shopkeeperTel;
    }

    public String getSshopName(){return  sshopName;}
    public void setSshopName(String sshopName){
        this.sshopName=sshopName;
    }

    public String getSserviceDescribe(){return  sserviceDescribe;}
    public void setSserviceDescribe(String sserviceDescribe){
        this.sserviceDescribe=sserviceDescribe;
    }

    public String getSlocDescribe(){return slocDescribe;}
    public void setSlocDescribe(String slocDescribe){
        this.slocDescribe=slocDescribe;
    }

    public String getsOwnerName(){return sOwnerName;}
    public void setsOwnerName(String sOwnerName){
        this.sOwnerName=sOwnerName;
    }

    public String getSprovince(){return sprovince;}
    public void setSprovince(String sprovince){
        this.sprovince=sprovince;
    }

    public String getScity(){return scity;}
    public void setScity(String scity){
        this.scity=scity;
    }

    public String getScounty(){return scounty;}
    public void setScounty(String scounty){
        this.scounty=scounty;
    }

    public String getSstreet(){return sstreet;}
    public void setSstreet(String sstreet)
    {
        this.sstreet=sstreet;
    }

    public String getSindustryType(){return  sindustryType;}
    public void setSindustryType(String sindustryType)
    {
        this.sindustryType=sindustryType;
    }

    public String getSmanagerAccount() {
        return smanagerAccount;
    }
    public void setSmanagerAccount(String smanagerID) {
        this.smanagerAccount = smanagerID;
    }

    public String getSmanagerTel(){return smanagerTel;}
    public void setSmanagerTel(String smanagerTel)
    {
        this.smanagerTel=smanagerTel;
    }

    public String getStype(){return stype;}
    public void setStype(String stype){
        this.stype=stype;
    }

    public String getScookie(){return scookie;}
    public void setScookie(String scookie){
        this.scookie=scookie;
    }

}

