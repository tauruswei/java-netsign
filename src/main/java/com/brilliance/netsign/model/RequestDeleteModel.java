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
@ApiModel("删除请求实体")
public class RequestDeleteModel {
    String keyLabel;

    public String getKeyLabel() {
        return keyLabel;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }
}
