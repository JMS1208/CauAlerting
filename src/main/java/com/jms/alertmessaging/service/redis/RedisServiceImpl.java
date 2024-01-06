package com.jms.alertmessaging.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{

    private final RedisTemplate<String, String> redisTemplate;

    //키 있으면 값 덮어씌움
    @Override
    public void putStringKeyStringValue(String key, String value, Date expiredAt) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expireAt(key, expiredAt);
    }

    //없으면 Null 반환
    @Override
    public String getStringValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
