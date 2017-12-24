
package com.xd.aselab.chinabank_shop.util;
/**
 * 发起HTTP请求中的参数
 */
public class PostParameter {

    private String key;
    private String value;

	public PostParameter(){
    }

    public PostParameter(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
