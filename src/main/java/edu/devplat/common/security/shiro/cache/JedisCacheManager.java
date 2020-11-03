package edu.devplat.common.security.shiro.cache;

import com.google.common.collect.Sets;
import edu.devplat.common.utils.JedisUtils;
import edu.devplat.common.web.Servlets;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 自定义的 Shiro CacheManager，用的 Jedis
 */
public class JedisCacheManager implements CacheManager {


    // 定义前缀，这个也会通过配置文件注入
    private String cacheKeyPrefix = "shiro_cache_";

    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new JedisCache<K, V>(cacheKeyPrefix + name);
    }

    public String getCacheKeyPrefix() {
        return cacheKeyPrefix;
    }

    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    /**
     * 使用 Redis 作为 shiro 的缓存就需要去实现 shiro 的 Cache
     */
    public class JedisCache<K, V> implements Cache<K, V>{
        // 打印日志
        private Logger logger = LoggerFactory.getLogger(getClass());

        private String cacheKeyName = null;

        public JedisCache(String cacheKeyName){
            this.cacheKeyName = cacheKeyName;
        }

        /**
         * 通过该 cache 的 cacheKeyName 和传入的 key 在 Redis 中查找 -- hget
         * @param key hashkey
         * @return
         * @throws CacheException
         */
        @SuppressWarnings("unchecked")
        public V get(K key) throws CacheException {
            if(key == null)
                return null;

            // 从请求中获取 value
            V v = null;
            HttpServletRequest request = Servlets.getRequest();
            if(request != null){
                v = (V)request.getAttribute(cacheKeyName);
                if(v != null)
                    return null;
            }

            V value = null;
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                // 通过 hget(cacheKeyName, key) 来获取 value
                value = (V)JedisUtils.toObject(jedis.hget(JedisUtils.getBytesKey(cacheKeyName),
                        JedisUtils.getBytesKey(key)));
                logger.debug("get {} {} {}", cacheKeyName, key, request != null ? request.getRequestURI() : "");
            }catch (Exception e){
                logger.error("get {} {} {}", cacheKeyName, key, request != null ? request.getRequestURI() : "", e);

            }finally {
                // 释放资源
                JedisUtils.returnResource(jedis);
            }
            // 结果保存在请求中一份
            if(request != null && value != null)
                request.setAttribute(cacheKeyName, value);

            return value;
        }

        /**
         * value 放入缓存中 -- hset
         * @param key
         * @param value
         * @return
         * @throws CacheException
         */
        public V put(K key, V value) throws CacheException {
            if(key == null)
                return null;
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                jedis.hset(JedisUtils.getBytesKey(cacheKeyName),
                        JedisUtils.getBytesKey(key),
                        JedisUtils.toBytes(value));
                logger.debug("put {} {} = {}", cacheKeyName, key, value);
            } catch (Exception e){
                logger.error("put {} {}", cacheKeyName, key, e);

            }finally {
                JedisUtils.returnResource(jedis);
            }
            return value;
        }

        /**
         * 移除 key -- hdel
         * @param key
         * @return
         * @throws CacheException
         */
        @SuppressWarnings("unchecked")
        public V remove(K key) throws CacheException {
            if(key == null)
                return null;
            V value = null;
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                value = (V)JedisUtils.toObject(jedis.hget(JedisUtils.getBytesKey(cacheKeyName),
                        JedisUtils.getBytesKey(key)));
                logger.debug("remove {} {}", cacheKeyName, key);
            }catch (Exception e){
                logger.warn("remove {} {}", cacheKeyName, key, e);
            }finally {
                JedisUtils.returnResource(jedis);
            }
            return value;
        }

        /**
         * 清空该缓存
         * @throws CacheException
         */
        public void clear() throws CacheException {
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                jedis.hdel(JedisUtils.getBytesKey(cacheKeyName));
                logger.debug("clear {}", cacheKeyName);
            }catch (Exception e){
                logger.error("clear {}", cacheKeyName, e);
            } finally {
                JedisUtils.returnResource(jedis);
            }
        }

        public int size() {
            int size = 0;
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                size = jedis.hlen(JedisUtils.getBytesKey(cacheKeyName)).intValue();
                logger.debug("size {} {} ", cacheKeyName, size);
            } catch (Exception e){
                logger.error("clear {}",  cacheKeyName, e);
            } finally {
                JedisUtils.returnResource(jedis);
            }
            return size;
        }


        @SuppressWarnings("unchecked")
        public Set<K> keys() {
            Set<K> keys = Sets.newHashSet();
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                Set<byte[]> keySet = jedis.hkeys(JedisUtils.getBytesKey(cacheKeyName));
                for(byte[] keyBytes : keySet){
                    Object obj = JedisUtils.getObjectKey(keyBytes);
                    if(obj != null)
                        keys.add((K)obj);
                }
                logger.debug("keys {} {} ", cacheKeyName, keys);
            } catch (Exception e){
                logger.error("keys {}", cacheKeyName, e);
            } finally {
                JedisUtils.returnResource(jedis);
            }
            return keys;
        }

        @SuppressWarnings("unchecked")
        public Collection<V> values() {
            Collection<V> vals = Collections.emptyList();
            Jedis jedis = null;
            try{
                jedis = JedisUtils.getResource();
                Collection<byte[]>  values = jedis.hvals(JedisUtils.getBytesKey(cacheKeyName));
                for(byte[] val : values){
                    Object obj = JedisUtils.getObjectKey(val);
                    if(obj != null)
                        vals.add((V)obj);
                }
                logger.debug("values {} {} ", cacheKeyName, vals);
            } catch (Exception e){
                logger.error("values {}", cacheKeyName, e);
            } finally {
                JedisUtils.returnResource(jedis);
            }
            return vals;
        }
    }

}
