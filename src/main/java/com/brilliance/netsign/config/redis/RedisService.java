package com.brilliance.netsign.config.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;

@Service
public class RedisService {

	@Autowired
    JedisPool jedisPool;

	/**
	 * 获取当个对象
	 * */
	public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			String  str = jedis.get(realKey);
			T t =  stringToBean(str, clazz);
			return t;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 设置对象
	 * */
	public <T> boolean set(KeyPrefix prefix, String key,  T value) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			String str = beanToString(value);
			if(str == null || str.length() <= 0) {
				return false;
			}
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			int seconds =  prefix.expireSeconds();
			if(seconds <= 0) {
				jedis.set(realKey, str);
			}else {
				jedis.setex(realKey, seconds, str);
			}
			return true;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 设置key过期时间
	 * @param key
	 * @param seconds
	 * @return
	 */
	public boolean expire(String key,int seconds) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			jedis.expire(key, seconds);
			return true;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 判断key是否存在
	 * */
	public <T> boolean exists(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			return  jedis.exists(realKey);
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 增加值
	 * */
	public <T> Long incr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			return  jedis.incr(realKey);
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 减少值
	 * */
	public <T> Long decr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			return  jedis.decr(realKey);
		}finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 获取list中指定范围的数据
	 * @param prefix
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	public List<String> lrange(KeyPrefix prefix,String key,long start,long stop){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			List<String> lrange = jedis.lrange(realKey, -stop, -start);
			Collections.reverse(lrange);
			return lrange;
		}finally {
			returnToPool(jedis);
		}
	}
	public Set<String> keys(KeyPrefix prefix){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix()+"*";
			return jedis.keys(realKey);
		}finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 向list中添加数据
	 * @param prefix
	 * @param key
	 * @retur
	 */
	public Long  lpush(KeyPrefix prefix,String key, String value){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
//			String value = beanToString(prefix1.getPrefix()+key1);
			Long lpush = jedis.lpush(realKey, value);
			return lpush;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 获取list中属性个数
	 * @param prefix
	 * @param key
	 * @return
	 */
	public long llen(KeyPrefix prefix,String key){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			return jedis.llen(realKey);
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 删除指定key
	 * @param key
	 * @return
	 */
	public Long del(String key){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			return jedis.del(key);
		}finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 删除list中的集合中的指定value
	 * @param prefix
	 * @param key
	 * @param value
	 * @return
	 */
	public Long lrem(KeyPrefix prefix,String key,String value){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			return jedis.lrem(realKey,0,value);
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 模糊查询所有得key，不会阻塞进程
	 * @param prefix
	 * @param scanLimitSize
	 * @return
	 */
	public Set<String> scan(KeyPrefix prefix,int scanLimitSize){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String pattern  = prefix.getPrefix()+"*";
			//存储返回的结果
			Set<String> result=new HashSet<String>();
			//设置查询的参数
			ScanParams scanParams=new ScanParams().count(scanLimitSize).match(pattern);
			//游标的开始
			String cur= ScanParams.SCAN_POINTER_START;
			do{
				//遍历每一批数据
				ScanResult<String> scan=jedis.scan(cur, scanParams);
				//得到结果返回
				List<String> scanResult =scan.getResult();
				result.addAll(scanResult);
				//获取新的游标
				cur=scan.getStringCursor();
				//判断游标迭代是否结束
			}while (!cur.equals(ScanParams.SCAN_POINTER_START));
			//返回结果
			return result;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 获取指定field的value
	 * @param prefix
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(KeyPrefix prefix,String key,String field) {
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			return jedis.hget(realKey, field);
		}finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 获取指定field的value
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key,String field) {
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			return jedis.hget(key, field);
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 通过索引获取列表中的元素
	 * @param prefix
	 * @param key
	 * @param index
	 * @return
	 */
	public Boolean lindex(KeyPrefix prefix,String key,Long index){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			jedis.lindex(realKey, index);
			return true;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 向哈希中添加field-value属性
	 * @param prefix
	 * @param key
	 * @param field
	 * @param value
	 */
	public boolean hset(KeyPrefix prefix,String key,String field,String value){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			jedis.hset(realKey, field, value);
			return true;
		}finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 向哈希中添加field-value属性
	 * @param key
	 * @param field
	 * @param value
	 */
	public boolean hset(String key,String field,String value){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			jedis.hset(key, field, value);
			return true;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 向哈希中添加多个field-value属性
	 * @param prefix
	 * @param key
	 * @param map
	 */
	public boolean hmset(KeyPrefix prefix,String key,Map map){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			String realKey  = prefix.getPrefix() + key;
			jedis.hmset(realKey, map);
			if(prefix.expireSeconds()!=0){
				jedis.expire(realKey, prefix.expireSeconds());
			}
			return true;
		}finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 向哈希中添加多个field-value属性
	 * @param key
	 * @param map
	 */
	public boolean hmset(String key,Map map){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			jedis.hmset(key, map);
			return true;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 获取指定键的所有属性（哈希）
	 * @param key
	 */
	public Map hgetall(String key){
		Jedis jedis = null;
		try{
			jedis =  jedisPool.getResource();
			return jedis.hgetAll(key);
		}finally {
			returnToPool(jedis);
		}
	}

	private <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			return ""+value;
		}else if(clazz == String.class) {
			return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			System.out.println(JSON.parseObject(str,clazz));
			return JSON.parseObject(str, clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		if(jedis != null) {
			jedis.close();
		}
	}

}
