package com.verizon.vnf.model;

import java.io.Serializable;

public class Vnfd implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String folderPath;
	private String vnfdName;
	private String vendor;
	private String version;
	private String type;
	private String endpoint;
	private String vmImage;
	private String vim;
	private String ScaleInOut;
	private String floatingIp;
	private String flavor;
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getVnfdName() {
		return vnfdName;
	}
	public void setVnfdName(String vnfdName) {
		this.vnfdName = vnfdName;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getVmImage() {
		return vmImage;
	}
	public void setVmImage(String vmImage) {
		this.vmImage = vmImage;
	}
	public String getVim() {
		return vim;
	}
	public void setVim(String vim) {
		this.vim = vim;
	}
	public String getScaleInOut() {
		return ScaleInOut;
	}
	public void setScaleInOut(String scaleInOut) {
		ScaleInOut = scaleInOut;
	}
	public String getFloatingIp() {
		return floatingIp;
	}
	public void setFloatingIp(String floatingIp) {
		this.floatingIp = floatingIp;
	}
	public String getFlavor() {
		return flavor;
	}
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
	
	

}
