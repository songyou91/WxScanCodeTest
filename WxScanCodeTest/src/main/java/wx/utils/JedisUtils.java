/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/">JeeSite</a> All rights reserved.
 */
package wx.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import wx.service.SpringContextHolder;

/**
 * Jedis Cache 工具类
 * 
 * @author ThinkGem
 * @version 2014-6-29
 */
public class JedisUtils {

	private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);

	private static JedisPool jedisPool = SpringContextHolder.getBean(JedisPool.class);

	public static final String KEY_PREFIX = Global.getConfig("redis.keyPrefix");

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				logger.debug("get {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("get {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	public static Long hset(String key,String field,String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hset(key,field,value);
		} catch (Exception e) {
			logger.warn("hset {} {} = {}", key, field,value);
		} finally {
			returnResource(jedis);
		}
		return null;
	}
	
	public static String hget(String key,String field) {
		String value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			value=jedis.hget(key,field);
			logger.debug("hget {} {} = {}", key, field, value);
		} catch (Exception e) {
			logger.warn("hget {} {} = {}", key, field,value);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	public static void hdel(String key,String field) {
		String value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.hdel(key, field);
			logger.debug("hdel {} {} = {}", key, field, value);
		} catch (Exception e) {
			logger.warn("hdel {} {} = {}", key, field,value);
		} finally {
			returnResource(jedis);
		}
	}
	
	public static Map hgetAll(String key) {
		Map map=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			map=jedis.hgetAll(key);
		} catch (Exception e) {
			logger.warn("hgetAll {} {}", key);
		} finally {
			returnResource(jedis);
		}
		return map;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Object getObject(String key) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = toObject(jedis.get(getBytesKey(key)));
				logger.debug("getObject {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("getObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String set(String key, String value, long cacheMillSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(key,value);
			if(cacheMillSeconds!=0){
				jedis.pexpire(key, cacheMillSeconds);
			}
			logger.debug("set {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("set {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String set(String key, String value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(key,value);
			logger.debug("set {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("set {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String set(String key, String value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("set {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("set {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setObject(String key, Object value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObject {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static List<String> getList(String key) {
		List<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.lrange(key, 0, -1);
				logger.debug("getList {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("getList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static List getObjectList(String key) {
		List value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
				value = Lists.newArrayList();
				for (byte[] bs : list) {
					value.add(toObject(bs));
				}
				logger.debug("getObjectList {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("getObjectList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setList(String key, List<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.rpush(key, (String[]) value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setList {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setObjectList(String key, List value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
//			List<byte[]> list = Lists.newArrayList();
			for (Object o : value) {
//				list.add(toBytes(o));
				//windows redis 不支持放入多个元素
				jedis.rpush(getBytesKey(key), toBytes(o));
			}
//			byte[][] array =new byte[list.size()][];
//			list.toArray(array);
//			result = jedis.rpush(getBytesKey(key), array);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObjectList {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setObjectList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long listAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.rpush(key, value);
			logger.debug("listAdd {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("listAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long listObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			List<byte[]> list = Lists.newArrayList();
			for (Object o : value) {
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			logger.debug("listObjectAdd {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("listObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Set<String> getSet(String key) {
		Set<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.smembers(key);
				logger.debug("getSet {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("getSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Set<Object> getObjectSet(String key) {
		Set<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Sets.newHashSet();
				Set<byte[]> set = jedis.smembers(getBytesKey(key));
				for (byte[] bs : set) {
					value.add(toObject(bs));
				}
				logger.debug("getObjectSet {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("getObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setSet(String key, Set<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.sadd(key, (String[]) value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setSet {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value) {
				set.add(toBytes(o));
			}
			result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObjectSet {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setSetAdd(String setName, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.sadd(setName, value);
			logger.debug("setSetAdd {} = {}", setName, value);
		} catch (Exception e) {
			logger.warn("setSetAdd {} = {}", setName, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	public static long setSetRem(String setName, String value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if(jedis.exists(setName)){
				result = jedis.srem(setName, value);
			}
			logger.debug("setSetRem {}  {}", setName, value);
		} catch (Exception e) {
			logger.warn("setSetRem {}  {}", setName, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 增加incrementBy
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long incrementBy(String key, long value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.incrBy(key, value);
			logger.debug("incrementBy {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("incrementBy {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	public static String setExpire(String key, int seconds, String value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.setex(key, seconds, value);
			logger.debug("incrementBy {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("incrementBy {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * setNotExist
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setNotExist(String key, String value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.setnx(key, value);
			logger.debug("setnx {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setnx {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 尝试获取锁并设置有效时间
	 * 
	 * @param lock
	 * @param expired
	 * @return
	 */
//	public static boolean acquireLock(String lock, long expired) {
//		boolean isSuccess = false;
//		Jedis jedis = null;
//		try {
//			jedis = getResource();
//			long value = System.currentTimeMillis() + expired + 1;
//			long acquired = jedis.setnx(lock, String.valueOf(value));
//			if (acquired == 1) {
//				jedis.expire(lock, Long.valueOf(value).intValue());  
//				isSuccess = true;
//			} else {
//				long oldValue = Long.valueOf(jedis.get(lock));
//				// 如果其他资源之前获得锁已经超时
//				if (oldValue < System.currentTimeMillis()) {
//					String getValue = jedis.getSet(lock, String.valueOf(value));
//					if (Long.valueOf(getValue) == oldValue)
//						isSuccess = true;
//					else
//						isSuccess = false;
//				} else
//					isSuccess = false;
//			}
//		} catch (Exception e) {
//			logger.warn("acquireLock {} ", lock, e);
//		} finally {
//			returnResource(jedis);
//		}
//		return isSuccess;
//	}
	
	/**
	 * 尝试获取锁并设置有效时间
	 * 
	 * @param lock
	 * @param expired
	 * @return
	 */
	public static boolean acquireLock(String lock, String value,long expired) {
		boolean isSuccess = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			String acquired = jedis.set(lock, String.valueOf(value), "NX", "PX", expired);
			if (acquired!=null) {
				isSuccess = true;
			} else {
				isSuccess = false;
			}
		} catch (Exception e) {
			logger.warn("acquireLock {} ", lock, e);
		} finally {
			returnResource(jedis);
		}
		return isSuccess;
	}
	
	
	/**
	 * 尝试获取锁并设置有效时间
	 * 
	 * @param lock
	 * @param expired
	 * @return
	 */
	public static boolean acquireLockByExpireTime(String lock, long expired) {
		boolean isSuccess = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			long value = expired;
			long acquired = jedis.setnx(lock, String.valueOf(value));
			if (acquired == 1) {
				isSuccess = true;
			} else {
				long oldValue = Long.valueOf(jedis.get(lock));
				// 如果其他资源之前获得锁已经超时
				if (oldValue < System.currentTimeMillis()) {
					String getValue = jedis.getSet(lock, String.valueOf(value));
					if (Long.valueOf(getValue) == oldValue)
						isSuccess = true;
					else
						isSuccess = false;
				} else
					isSuccess = false;
			}
		} catch (Exception e) {
			logger.warn("acquireLock {} ", lock, e);
		} finally {
			returnResource(jedis);
		}
		return isSuccess;
	}
	
	/**
	 * 释放锁资源
	 * 
	 * @param lock
	 */
	public static boolean releaseLock(String lock) {
		Jedis jedis = null;
		long currentTime = System.currentTimeMillis();
		try {
			jedis = getResource();
			if (currentTime < Long.valueOf(jedis.get(lock))) {
				jedis.del(lock);
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.warn("releaseLock {} = {}", lock);
		} finally {
			returnResource(jedis);
		}
		return false;
	}
	/**
	 * 释放锁资源
	 * 
	 * @param lock
	 */
	public static boolean releaseLockByOldExpireTime(String lock,long oldTime) {
		Jedis jedis = null;
		long currentTime = System.currentTimeMillis();
		try {
			jedis = getResource();
			long lockTime=Long.valueOf(jedis.get(lock));
			if (currentTime <lockTime && oldTime==lockTime) {
				jedis.del(lock);
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.warn("releaseLock {} = {}", lock);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * 过期
	 * 
	 * @param key
	 *            键
	 * @param seconds
	 *            值
	 * @return
	 */
	public static long expire(String key, int seconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.expire(key, seconds);
			logger.debug("expire {} = {}", key, seconds);
		} catch (Exception e) {
			logger.warn("expire {} = {}", key, seconds, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	/**
	 * 过期
	 * 
	 * @param key
	 *            键
	 * @param seconds
	 *            值
	 * @return
	 */
	public static long pexpire(String key, long millSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.pexpire(key, millSeconds);
			logger.debug("expire {} = {}", key, millSeconds);
		} catch (Exception e) {
			logger.warn("expire {} = {}", key, millSeconds, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setSetObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value) {
				set.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
			logger.debug("setSetObjectAdd {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setSetObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Map<String, String> getMap(String key) {
		Map<String, String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.hgetAll(key);
				logger.debug("getMap {} = {}", key, value);
			}
		} catch (Exception e) {
			logger.warn("getMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.hmset(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setMap {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObjectMap {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("setObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static String mapPut(String key, Map<String, String> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hmset(key, value);
			logger.debug("mapPut {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("mapPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static String mapObjectPut(String key, Map<String, Object> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			logger.debug("mapObjectPut {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("mapObjectPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long mapRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(key, mapKey);
			logger.debug("mapRemove {}  {}", key, mapKey);
		} catch (Exception e) {
			logger.warn("mapRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long mapObjectRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
			logger.debug("mapObjectRemove {}  {}", key, mapKey);
		} catch (Exception e) {
			logger.warn("mapObjectRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean mapExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(key, mapKey);
			logger.debug("mapExists {}  {}", key, mapKey);
		} catch (Exception e) {
			logger.warn("mapExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean mapObjectExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
			logger.debug("mapObjectExists {}  {}", key, mapKey);
		} catch (Exception e) {
			logger.warn("mapObjectExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long del(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				result = jedis.del(key);
				logger.debug("del {}", key);
			} else {
				logger.debug("del {} not exists", key);
			}
		} catch (Exception e) {
			logger.warn("del {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long delObject(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				result = jedis.del(getBytesKey(key));
				logger.debug("delObject {}", key);
			} else {
				logger.debug("delObject {} not exists", key);
			}
		} catch (Exception e) {
			logger.warn("delObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long delObjectStr(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				result = jedis.del(key);
				logger.debug("delObject {}", key);
			} else {
				logger.debug("delObject {} not exists", key);
			}
		} catch (Exception e) {
			logger.warn("delObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean exists(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(key);
			logger.debug("exists {}", key);
		} catch (Exception e) {
			logger.warn("exists {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean existsObject(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			logger.debug("existsObject {}", key);
		} catch (Exception e) {
			logger.warn("existsObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取资源
	 * 
	 * @return
	 * @throws JedisException
	 */
	public static Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// logger.debug("getResource.", jedis);
		} catch (JedisException e) {
			logger.warn("getResource.", e);
			returnBrokenResource(jedis);
			throw e;
		}
		return jedis;
	}

	/**
	 * 归还资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public static void returnBrokenResource(Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnBrokenResource(jedis);
		}
	}

	/**
	 * 释放资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public static void returnResource(Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 获取byte[]类型Key
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] getBytesKey(Object object) {
		if (object instanceof String) {
			//return StringUtils.getBytes((String) object);
			String str = object.toString();
			if (str != null){
	    		try {
					return str.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					return null;
				}
	    	}else{
	    		return null;
	    	}
		} else {
			//return ObjectUtils.serialize(object);
			ObjectOutputStream oos = null;
			ByteArrayOutputStream baos = null;
			try {
				if (object != null){
					baos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(baos);
					oos.writeObject(object);
					return baos.toByteArray();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * 获取byte[]类型Key
	 * 
	 * @param key
	 * @return
	 */
	public static Object getObjectKey(byte[] key) {
		try {
			try {
				return new String(key, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} catch (UnsupportedOperationException uoe) {
			try {
				return JedisUtils.toObject(key);
			} catch (UnsupportedOperationException uoe2) {
				uoe2.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Object转换byte[]类型
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] toBytes(Object object) {
		//return ObjectUtils.serialize(object);
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			if (object != null){
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				return baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * byte[]型转换Object
	 * 
	 * @param key
	 * @return
	 */
	public static Object toObject(byte[] bytes) {
		//return ObjectUtils.unserialize(bytes);
		ByteArrayInputStream bais = null;
		try {
			if (bytes != null && bytes.length > 0){
				bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais);
				return ois.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
