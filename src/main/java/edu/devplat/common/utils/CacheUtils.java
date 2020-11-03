package edu.devplat.common.utils;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * Cache 工具类
 */

public class CacheUtils {

    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);
    private static CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);

    public static final String SYS_CACHE = "sysCache";

    /**
     * 获取 SYS_CACHE 缓存
     * @param key
     * @return
     */
    public static Object get(String key){
        return get(SYS_CACHE, key);
    }

    /**
     * 获取 SYS_CACHE 缓存
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object get(String key, Object defaultValue){
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 写入 SYS_CACHE 缓存
     * @param key
     * @param value
     */
    public static void put(String key, Object value){
        put(SYS_CACHE, key, value);
    }

    /**
     * 从 SYS_CACHE 缓存中删除
     * @param key
     * @param value
     */
    public static void remove(String key, Object value){
        remove(SYS_CACHE, key);
    }

    /**
     * 写入该缓存
     * @param cacheName
     * @param key
     * @return
     */
    public static Object get(String cacheName, String key){
        return getCache(cacheName).get(key);
    }

    /**
     * 写入该缓存
     * @param cacheName
     * @param key
     * @param value
     */
    public static void put(String cacheName, String key, Object value){
        getCache(cacheName).put(key, value);
    }

    /**
     * 从缓存中移除
     * @param cacheName
     * @param key
     */
    public static void remove (String cacheName, String key){
        getCache(cacheName).remove(key);
    }

    /**
     * 从该缓存中移除所有 key
     * @param cacheName
     */
    public static void removeAll(String cacheName){
        Cache<String, Object> cache = getCache(cacheName);
        Set<String> keys = cache.keys();
        for(Iterator<String> it = keys.iterator(); it.hasNext(); ){
            cache.remove(it.next());
        }
        logger.info("Remove cache: {} => {}", cacheName, keys);

    }

    /**
     * 获取一个 Cache, 没有则显示日志
     * @param cacheName
     * @return
     */
    public static Cache<String, Object> getCache(String cacheName){
        Cache<String, Object> cache = cacheManager.getCache(cacheName);
        if(cache == null)
            throw new RuntimeException("No such " + cacheName + " in system");
        return cache;
    }
}
