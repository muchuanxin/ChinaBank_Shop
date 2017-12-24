package com.xd.aselab.chinabank_shop.Vos;

import java.io.Serializable;

/**
 * Created by liuhaoxian on 2016/6/14.
 */
public class AccountTypeVo implements Serializable {
       String new_account;
       String account;
       String type;
       String worker_type;

    public String getWorker_type() {
        return worker_type;
    }

    public void setWorker_type(String worker_type) {
        this.worker_type = worker_type;
    }

    public String getNew_account() {
        return new_account;
    }

    public void setNew_account(String new_account) {
        this.new_account = new_account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
