package com.brilliance.netsign.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2020/9/16 0016 11:14
 */
@Data
@ApiModel("上传证书请求实体")
public class RequestUploadCertModel {
    private String keyLabel;
    private String cert;
}
