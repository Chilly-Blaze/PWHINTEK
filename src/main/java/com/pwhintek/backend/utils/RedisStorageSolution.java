package com.pwhintek.backend.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pwhintek.backend.dto.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pwhintek.backend.constant.RedisConstants.CACHE_NULL_TTL;
import static com.pwhintek.backend.constant.RedisConstants.LOCK_TTL;

/**
 * 缓存问题解决
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 20:38
 */
@Component
public class RedisStorageSolution {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 超时存储
     *
     * @author ChillyBlaze
     * @since 22/04/2022 21:05
     */
    public void saveByTTL(String key, Object value, Long time, TimeUnit unit) {
        String jsonValue = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, jsonValue, time, unit);
    }

    /**
     * 逻辑存储，无超时时间
     *
     * @author ChillyBlaze
     * @since 22/04/2022 21:05
     */
    public void saveByLogical(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        String jsonRedisData = JSONUtil.toJsonStr(redisData);
        stringRedisTemplate.opsForValue().set(key, jsonRedisData);
    }

    /**
     * 空值存储
     *
     * @author ChillyBlaze
     * @since 22/04/2022 21:03
     */
    public void saveByNull(String key) {
        stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
    }

    /**
     * 删除键值
     *
     * @author ChillyBlaze
     * @since 22/04/2022 21:11
     */
    public void deleteByKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 键值自增
     */
    public void increaseKey(String key) {
        // 判断值是否为整型
        if (NumberUtil.isNumber(stringRedisTemplate.opsForValue().get(key))) {
            stringRedisTemplate.opsForValue().increment(key);
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * 键值自减
     */
    public void decreaseKey(String key) {
        // 判断值是否为整型
        if (NumberUtil.isNumber(stringRedisTemplate.opsForValue().get(key))) {
            stringRedisTemplate.opsForValue().decrement(key);
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * 缓存穿透预防，防止无效请求过度打到数据库
     *
     * @param prefix         Redis内前缀
     * @param id             数据库查找id
     * @param type           返回类型
     * @param databaseMethod 通过id获得type的方法
     * @param timeY          RedisTTL
     * @param unitY          TimeUnit
     * @param <O>            返回对象类型
     * @param <ID>           id类型
     * @return type对象
     * @author ChillyBlaze
     * @since 22/04/2022 21:02
     */
    public <O, ID> O queryWithPassThrough(String prefix,
                                          ID id,
                                          Class<O> type,
                                          Function<ID, O> databaseMethod,
                                          Long timeY,
                                          TimeUnit unitY) {
        // 尝试从Redis查询缓存
        String key = prefix + id;
        String objectJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(objectJson)) {
            return JSONUtil.toBean(objectJson, type);
        }
        // 判断命中的是否为空值
        if (objectJson != null) {
            return null;
        }

        // 不存在，查询数据库
        O object = databaseMethod.apply(id);  // 调用apply方法
        // 数据库也不存在，返回错误
        if (ObjectUtil.isNull(object)) {
            // 将空值写入Redis
            saveByNull(key);
            return null;
        }
        // 数据库存在，写入Redis
        saveByTTL(key, object, timeY, unitY);
        // 返回
        return object;
    }

    /**
     * 获取记录数的重写方法
     *
     * @return 指定内容的记录数
     */
    public <ID> Long queryWithPassThrough(String prefix,
                                          ID id,
                                          Function<ID, Long> databaseMethod,
                                          Long timeY,
                                          TimeUnit unitY) {
        // 尝试从Redis查询缓存
        String key = prefix + id;
        String numS = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(numS)) {
            assert numS != null;
            return Long.valueOf(numS);
        }

        // 不存在，查询数据库
        Long num = databaseMethod.apply(id);  // 调用apply方法
        // 写入Redis
        stringRedisTemplate.opsForValue().set(key, num.toString(), timeY, unitY);
        // 返回
        return num;
    }

    // 开启一个线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(100);

    // 缓存击穿预防，防止热点数据缓存超时段时间大量请求打到数据库，逻辑过期
    public <O, ID> O queryWithLogicalExpire(String prefixID,
                                            String prefixLock,
                                            ID id,
                                            Class<O> type,
                                            Function<ID, O> queryById,
                                            Long overDueTime,
                                            TimeUnit unit) {
        // 1.尝试从Redis查询缓存
        String key = prefixID + id;
        String objectJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(objectJson)) {
            // 返回为空，说明不存在，存入第一次数据
            if (objectJson != null) {
                return null;
            }
            O object = queryById.apply(id);
            saveByLogical(key, object, overDueTime, unit);
            return object;
        }
        // 返回不为空
        // 先将json反序列化为对象
        RedisData redisData = JSONUtil.toBean(objectJson, RedisData.class);
        O object = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 检查逻辑过期时间，判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期，直接返回对象
            return object;
        }
        // 过期，需要缓存重建
        // 获取互斥锁
        String lockKey = prefixLock + id;
        boolean isLock = tryLock(lockKey);
        // 判断是否获取锁成功
        if (isLock) {
            // 成功，开启独立线程，实现缓存重建，开启线程池做
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    O databaseObject = queryById.apply(id);
                    saveByLogical(key, databaseObject, overDueTime, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 一定要记得释放锁，同时保证释放的过程不被某些Runtime异常给错过
                    unlock(lockKey);
                }
            });
        }
        // 返回过期的商铺信息
        return object;
    }

    public boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", LOCK_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);  // 注意此处最好不要直接返回，Java会自动拆箱，可能会返回空指针（比如连接不上redis，此时flag就为空）
    }

    public void unlock(String key) {
        deleteByKey(key);
    }

}

