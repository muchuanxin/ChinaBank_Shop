package com.xd.aselab.chinabank_shop.Vos;

/**
 * Created by Dorise on 2017/10/5.
 */

public class CardDivInfo {
    private String carddivAccount;
    private String carddivWorkerName;
    private String carddivWorkerTel;
    private String carddivType;
    private String carddivShopName;
    private String carddivShopTel;
    private String carddivShopAccunt;
    private String carddivCompany;
    private String carddivManagerAccount;
private String manager_head_image;
    private String carddivManegerName;
    private String carddivManagerTel;
    private String head_image;

//public void CardDivInfo(){
//    head_image="http:// 192.168.0.129/ChinaBank/images/head.jpg";
//}

    public String getHead_image(){return head_image;}
    public void setHead_image(String head_image){
        this.head_image=head_image;
    }

    public String getCarddivCompany(){
        return carddivCompany;
    }
    public void setCarddivCompany(String company){carddivCompany=company;}
    public String getCarddivManagerAccount(){
        return carddivManagerAccount;
    }
    public void setCarddivManagerAccount(String account){carddivManagerAccount=account;}

    public String getCarddivAccount(){
        return carddivAccount;
    }
    public void setCarddivAccount(String account){carddivAccount=account;}

    public String getCarddivWorkerName(){
        return carddivWorkerName;
    }
    public void setCarddivWorkerName(String carddivWorkerName){this.carddivWorkerName=carddivWorkerName;}

    public String getCarddivWorkerTel(){
        return carddivWorkerTel;
    }
    public void setCarddivWorkerTel(String Tel){carddivWorkerTel=Tel;}

    public String getCarddivType(){
        return carddivType;
    }
    public void setCarddivType(String Type){carddivType=Type;}

    public String getCarddivShopName(){
        return carddivShopName;
    }
    public void setCarddivShopName(String name){carddivShopName=name;}

    public String getCarddivShopTel(){
        return carddivShopTel;
    }
    public void setCarddivShopTel(String tel){carddivShopTel=tel;}

    public String getCarddivManegerName(){
        return carddivManegerName;
    }
    public void setCarddivManegerName(String name){carddivManegerName=name;}

    public String getCarddivManagerTel(){
        return carddivManagerTel;
    }
    public void setCarddivManagerTel(String tel){carddivManagerTel=tel;}

    public void setCarddivManagerHeadImage(String carddivManagerHeadImage) {
        this.manager_head_image = carddivManagerHeadImage;
    }
    public String getCarddivManagerHeadImage() {
        return manager_head_image;
    }
}

