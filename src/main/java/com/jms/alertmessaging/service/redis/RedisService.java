package com.jms.alertmessaging.service.redis;

import java.util.Date;

public interface RedisService {

    //분 단위 캐싱
    public void putStringKeyStringValue(String key, String value, Date expiredAt);

    //없으면 null 반환
    public String getStringValue(String key);
}
