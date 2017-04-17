package com.verizon.vnf.repository;


import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RepositoryImplClass implements RepositoryClass {		
	
	private RedisTemplate<String, Object> redisTemplate;		
	private ValueOperations<String, Object> TokenOps;  
	private HashOperations<String, String, Object> hashOps;
	
    
    @Autowired
   public RepositoryImplClass(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;        
    } 
  
    @PostConstruct
    private void init() {     
        TokenOps = redisTemplate.opsForValue();
        hashOps = redisTemplate.opsForHash();
    }

	@Override
	public void save(String key, String id, Object object) {
		// TODO Auto-generated method stub
		hashOps.put(key, id, object);
	}

	@Override
	public Object get(String key, String id) {
		// TODO Auto-generated method stub
		
		return hashOps.get(key, id);
	}

	@Override
	public void save(String key, Object object) {
		TokenOps.set(key, object);
		
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return TokenOps.get(key);
	}

	@Override
	public List<Object> getAll(String key) {
		// TODO Auto-generated method stub
		return hashOps.values(key);
	}
	
	public Set<String> getAllKeys(String key) {
		// TODO Auto-generated method stub
		return hashOps.keys(key);
	}

	@Override
	public Long delete(String key, String id) {
		// TODO Auto-generated method stub
		return hashOps.delete(key, id);
	}
	
	
}
