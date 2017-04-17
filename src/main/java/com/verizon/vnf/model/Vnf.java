package com.verizon.vnf.model;

import java.io.Serializable;

public class Vnf implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String companyname;
	private String vnfproductname;
	private String highleveldes;
	private String networkservice;
	private CompanyTechnicalContact companytechnicalcontact;
	
	public String getCompanyname() {
		return companyname;
	}
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}
	public String getVnfproductname() {
		return vnfproductname;
	}
	public void setVnfproductname(String vnfproductname) {
		this.vnfproductname = vnfproductname;
	}
	public String getHighleveldes() {
		return highleveldes;
	}
	public void setHighleveldes(String highleveldes) {
		this.highleveldes = highleveldes;
	}
	public String getNetworkservice() {
		return networkservice;
	}
	public void setNetworkservice(String networkservice) {
		this.networkservice = networkservice;
	}
	public CompanyTechnicalContact getCompanytechnicalcontact() {
		return companytechnicalcontact;
	}
	public void setCompanytechnicalcontact(CompanyTechnicalContact companytechnicalcontact) {
		this.companytechnicalcontact = companytechnicalcontact;
	}
	

	

}
