package com.verizon.vnf.model;
import java.io.Serializable;
public class WorkFlowView  implements Serializable{
	private static final long serialVersionUID = 1L;
	public WorkFlowViewObject upload;
	public WorkFlowViewObject package_validation;
	public WorkFlowViewObject security;
	public WorkFlowViewObject instantiation;
	public WorkFlowViewObject  test_updates;
	public WorkFlowViewObject  certification;
	public WorkFlowViewObject  artifactory_version_update;
	
	public WorkFlowViewObject getUpload() {
		return upload;
	}
	public void setUpload(WorkFlowViewObject upload) {
		this.upload = upload;
	}
	public WorkFlowViewObject getPackage_validation() {
		return package_validation;
	}
	public void setPackage_validation(WorkFlowViewObject package_validation) {
		this.package_validation = package_validation;
	}
	public WorkFlowViewObject getSecurity() {
		return security;
	}
	public void setSecurity(WorkFlowViewObject security) {
		this.security = security;
	}
	public WorkFlowViewObject getInstantiation() {
		return instantiation;
	}
	public void setInstantiation(WorkFlowViewObject instantiation) {
		this.instantiation = instantiation;
	}
	public WorkFlowViewObject getTest_updates() {
		return test_updates;
	}
	public void setTest_updates(WorkFlowViewObject test_updates) {
		this.test_updates = test_updates;
	}
	public WorkFlowViewObject getCertification() {
		return certification;
	}
	public void setCertification(WorkFlowViewObject certification) {
		this.certification = certification;
	}
	public WorkFlowViewObject getArtifactory_version_update() {
		return artifactory_version_update;
	}
	public void setArtifactory_version_update(WorkFlowViewObject artifactory_version_update) {
		this.artifactory_version_update = artifactory_version_update;
	}
	
	
}
