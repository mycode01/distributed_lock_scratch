package com.example.scratchspring;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

  private final String redisAddress;

  public RedissonConfig(
      @Value("${spring.redis.host}")
          String redisHost,
      @Value("${spring.redis.port}")
          String redisPort) {
    this.redisAddress = getRedissonAddress(redisHost + ":" + redisPort);
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config().setCodec(StringCodec.INSTANCE);
    config.useSingleServer().setAddress(redisAddress);
    return Redisson.create(config);
  }

  private static String getRedissonAddress(String addressAndPort) {
    return "redis://" + addressAndPort;
    // use "rediss://" if using secure
  }
}
