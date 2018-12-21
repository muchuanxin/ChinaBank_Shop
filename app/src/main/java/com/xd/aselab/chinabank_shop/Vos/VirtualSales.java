package com.xd.aselab.chinabank_shop.Vos;

public class VirtualSales {
    private String account;
    private String password;
    private String name;
    private String tel;
    private String type;
    private String headImage;

    // 登录账号的存取
    public String getAccount() {
        return account;
    }

    public void setAccount(String VirtualSalesAccount) {
        this.account = VirtualSalesAccount;
    }

    // 密码的存取
    public String getPassword() {
        return password;
    }

    public void setPassword(String VirtualSalesPassword) {
        this.password = VirtualSalesPassword;
    }

    // 姓名的存取
    public String getName() {
        return name;
    }

    public void setName(String VirtualSalesName) {
        this.name = VirtualSalesName;
    }

    // 手机号的存取
    public String getTel() {
        return tel;
    }

    public void setTel(String VirtualSalesTel) {
        this.tel = VirtualSalesTel;
    }

    // 用户类型的存取
    public String getType() {
        return type;
    }

    public void setType(String workerType) {
        this.type = workerType;
    }

    // 用户头像的存取
    public String getHead_image() {
        return headImage;
    }

    public void setHead_image(String head_image) {
        this.headImage = head_image;
    }
}
