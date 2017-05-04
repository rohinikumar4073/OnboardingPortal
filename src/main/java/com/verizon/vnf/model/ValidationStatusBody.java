package com.verizon.vnf.model;

import java.io.Serializable;

public class ValidationStatusBody implements Serializable {
	private String status;
	private	String phase;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	

}
