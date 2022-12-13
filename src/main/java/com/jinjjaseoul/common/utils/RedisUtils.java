package com.jinjjaseoul.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, String data) {
        ValueOperations<String, Object> value = redisTemplate.opsForValue();
        value.set(key, data);
    }

    public void setValue(String key, String data, Duration duration) {
        ValueOperations<String, Object> value = redisTemplate.opsForValue();
        value.set(key, data, duration);
    }

    public Optional<String> getValue(String key) {
        ValueOperations<String, Object> value = redisTemplate.opsForValue();
        return Optional.ofNullable((String) value.get(key));
    }

    public void setCollection(String key, Object... data) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPushAll(key, data);
    }

    public void setCollection(String key, Duration duration, Object... data) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPushAll(key, data, duration);
    }

    public Object getDataFromCollection(String key, long index) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.index(key, index);
    }

    public List<Object> getDataListFromCollection(String key, long start, long end) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(key, start, end);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}