package com.brilliance.netsign.config.redis;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2020/5/11 0011 14:27
 */
public class SM2Key extends BasePrefix {

    public SM2Key(String prefix) {
        super(prefix);
    }

    public static SM2Key getKey = new SM2Key("");
}
