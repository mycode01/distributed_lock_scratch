package com.example.scratchspring;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class RedissonLockProvider {
  
  private final RedissonClient redissonClient;

  public RedissonLockProvider(RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
  }

  public RLock getLock(final String key){
    return redissonClient.getLock(key);
  }
}
