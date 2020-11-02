package com.brilliance.netsign.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="spring.redis")
@Data
public class RedisConfig {
	private String host;
	private int port;
	private int timeout;//秒
	private String password;
	private int poolMaxTotal;
	private int poolMaxIdle;
	private int poolMinIdle;
	private int poolMaxWait;//秒
}
