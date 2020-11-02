package com.brilliance.netsign.config.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2020/4/19 0019 23:13
 */
@Component
public class RedisKeyExpirationListener implements MessageListener {
    @Resource
    private RedisService redisService;

    /**
     * 客户端监听订阅的topic，当有消息的时候，会触发该方法;
     * 并不能得到value, 只能得到key。
     * 姑且理解为: redis服务在key失效时(或失效后)通知到java服务某个key失效了, 那么在java中不可能得到这个redis-key对应的redis-value。
     *      * 解决方案:
     *  创建copy/shadow key, 例如 set vkey "vergilyn"; 对应copykey: set copykey:vkey "" ex 10;
     *  真正的key是"vkey"(业务中使用), 失效触发key是"copykey:vkey"(其value为空字符为了减少内存空间消耗)。
     *  当"copykey:vkey"触发失效时, 从"vkey"得到失效时的值, 并在逻辑处理完后"del vkey"
     *
     * 缺陷:
     *  1: 存在多余的key; (copykey/shadowkey)
     *  2: 不严谨, 假设copykey在 12:00:00失效, 通知在12:10:00收到, 这间隔的10min内程序修改了key, 得到的并不是 失效时的value.
     *  (第1点影响不大; 第2点貌似redis本身的Pub/Sub就不是严谨的, 失效后还存在value的修改, 应该在设计/逻辑上杜绝)
     *  当"copykey:vkey"触发失效时, 从"vkey"得到失效时的值, 并在逻辑处理完后"del vkey"
     *
     */
    @Override
    public void onMessage(Message message, byte[] pattern){
        String expiredKey = message.toString();
//        System.out.println(expiredKey);
        if(expiredKey.startsWith("SessionKey:sessionId")){
//            如果是SessionKey:sessionId开头的key，进行处理
//            System.out.println("嘿嘿，失效了呀");
//            new Thread(() -> redisService.lrem(SessionKey.getByList(), "sessions", expiredKey)).start();
        }
    }
}
