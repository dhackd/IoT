package keti.sgs.config;

import keti.sgs.repository.DeviceRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(basePackageClasses = {DeviceRedisRepository.class})
public class RedisConfig {
  @Autowired
  private RedisConfigProperties clusterProperties;

  /**
   * redis connection factory.
   * 
   * @return factory
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
    return lettuceConnectionFactory;
  }

  /**
   * redis cluster connection factory.
   * 
   * @return factory
   */
  @Bean
  public RedisConnectionFactory redisClusterConnectionFactory() {
    return new LettuceConnectionFactory(
        new RedisClusterConfiguration(clusterProperties.getNodes()));
  }

  /**
   * get redis template.
   * 
   * @return redis template
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    if (clusterProperties.getStatus()) {
      redisTemplate.setConnectionFactory(redisClusterConnectionFactory());
      return redisTemplate;
    }
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }
}