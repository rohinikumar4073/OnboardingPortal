package com.verizon.vnf.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,Object> formData = new LinkedHashMap<String,Object>();

	public Map<String,Object> getFormData() {
		return formData;
	}

	public void setFormData(Map<String,Object> formData) {
		this.formData = formData;
	}

	
	
	
	
	

}
