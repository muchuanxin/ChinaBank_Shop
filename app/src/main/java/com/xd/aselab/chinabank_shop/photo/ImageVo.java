package com.xd.aselab.chinabank_shop.photo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/8.
 */

public class ImageVo implements Serializable {
    private String imagePath;
    private String imageName;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
