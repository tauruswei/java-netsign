package com.brilliance.netsign.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2020/9/4 0004 22:34
 */
@Data
@ApiModel("验签请求实体")
public class RequestVerifyModel {
    String origBytes;
    String signature;
    String keyLabel;
    String digestAlg;

    public String getOrigBytes() {
        return origBytes;
    }

    public void setOrigBytes(String origBytes) {
        this.origBytes = origBytes;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }

    public String getDigestAlg() {
        return digestAlg;
    }

    public void setDigestAlg(String digestAlg) {
        this.digestAlg = digestAlg;
    }
}
