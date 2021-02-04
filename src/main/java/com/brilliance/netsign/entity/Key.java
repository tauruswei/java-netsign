package com.brilliance.netsign.entity;

import java.io.Serializable;

/**
 * 密钥(Key)实体类
 *
 * @author makejava
 * @since 2020-09-07 11:23:17
 */
public class Key implements Serializable {
    private static final long serialVersionUID = 886731440220291033L;
    
    private String keyLabel;
    
    private String keyMaterial;
    private String keyUser;
    private int signCount;
    private int verifyCount;

    public String getKeyUser() {
        return keyUser;
    }

    public void setKeyUser(String keyUser) {
        this.keyUser = keyUser;
    }

    private int keyEnable;

    public int getKeyEnable() {
        return keyEnable;
    }

    public void setKeyEnable(int keyEnable) {
        this.keyEnable = keyEnable;
    }



    public String getKeyLabel() {
        return keyLabel;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }

    public String getKeyMaterial() {
        return keyMaterial;
    }

    public void setKeyMaterial(String keyMaterial) {
        this.keyMaterial = keyMaterial;
    }

}