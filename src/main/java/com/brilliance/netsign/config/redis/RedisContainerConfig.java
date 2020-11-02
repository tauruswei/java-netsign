package com.brilliance.netsign.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2020/4/19 0019 16:57
 */
@Configuration
public class RedisContainerConfig {
    @Autowired
    private RedisKeyExpirationListener redisExpiredListener;

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory redisConnection){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 设置Redis的连接工厂
        container.setConnectionFactory(redisConnection);
        // 设置监听使用的线程池
        // 设置监听器
        container.addMessageListener(redisExpiredListener, new PatternTopic("__keyevent@0__:expired"));
        return container;
    }
}
