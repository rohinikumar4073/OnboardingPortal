package com.verizon.vnf.repository;

import java.util.List;

public interface RepositoryClass {
	
	public void save(String key,String id, Object object);
	
	public Object get(String key, String id);	
	
	public void save(String key,Object object);
	
	public Object get(String key);
	
	public List<Object> getAll(String key);	
	
	public Long delete(String key, String id);

}
