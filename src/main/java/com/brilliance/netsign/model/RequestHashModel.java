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
@ApiModel("哈希请求实体")
public class RequestHashModel {
    String msgBytes;
    String digestAlg;

    public String getMsgBytes() {
        return msgBytes;
    }

    public void setMsgBytes(String msgBytes) {
        this.msgBytes = msgBytes;
    }

    public String getDigestAlg() {
        return digestAlg;
    }

    public void setDigestAlg(String digestAlg) {
        this.digestAlg = digestAlg;
    }
}
