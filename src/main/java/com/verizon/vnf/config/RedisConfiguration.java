package com.verizon.vnf.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
@EnableAutoConfiguration 
public class RedisConfiguration {
	
	@Value("${rms.datasource.redisHostName}")
	private String hostName;
	
	@Value("${rms.datasource.redisPortNo}")
	private Integer port;
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		System.out.println(hostName+"***********************Configuration Start**************************"+port);		
		JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
	    jedisConFactory.setHostName(hostName);
	    jedisConFactory.setPort(port);
	    //jedisConFactory.setPassword("");	   
	    return jedisConFactory;
	}
	 
	@Bean
	public RedisTemplate<String, Object> listRedisTemplate() {
	    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
	    template.setConnectionFactory(jedisConnectionFactory());
	    //template.setHashValueSerializer(new StringRedisSerializer());
	    template.setKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(new StringRedisSerializer());
	    //template.setHashKeySerializer(new StringRedisSerializer());
	    template.afterPropertiesSet();	    
	    return template;
	}

}
