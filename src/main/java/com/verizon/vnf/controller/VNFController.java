package com.verizon.vnf.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.verizon.vnf.model.FormData;
import com.verizon.vnf.model.PopulateNsd;
import com.verizon.vnf.model.ValidationStatusBody;
import com.verizon.vnf.model.Vnfd;
import com.verizon.vnf.model.WorkFlowView;
import com.verizon.vnf.model.WorkFlowViewObject;
import com.verizon.vnf.repository.RepositoryImplClass;
import com.verizon.vnf.util.Util;

import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/vnf")
//@RequestMapping("/vnfOnb")
public class VNFController {
	
	private static final String VNF = "VNF";
	private static final String WORKFLOW = "WORKFLOW";
	@Autowired
	RepositoryImplClass repositoryImplClass;
	
	@Autowired
	Util util;

/*	@RequestMapping(value = "{title}/get", method = RequestMethod.GET)	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Vnf get(@PathVariable String title){
		Vnf vnf = new Vnf();
		vnf.setCompanyname("");
		vnf.setHighleveldes("");
		vnf.setNetworkservice("");
		vnf.setVnfproductname("");
		CompanyTechnicalContact companyTechnicalContact = new CompanyTechnicalContact();
		companyTechnicalContact.setEmail("");
		companyTechnicalContact.setPhone("");
		vnf.setCompanytechnicalcontact(companyTechnicalContact);
		repositoryImplClass.save(VNF, title, vnf);
		return vnf;
	}
	*/
	@RequestMapping(value = "{id}/saveFormData", method = RequestMethod.POST, consumes = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> save(@PathVariable String id, @RequestBody FormData formData){
		
		Map<String,String> message = new HashMap<String,String>();		
		/*Object object = repositoryImplClass.get(VNF, id);
		if(object !=null){
			message.put("type", "failure");
			message.put("message", "Already available!");
			return message;
		}*/
		repositoryImplClass.save(VNF, id, formData);
		message.put("type", "sucess");
		message.put("message", "Data save sucessfully!");
		return message;
	/*	message.put("testing", "add");
		message.put("testing1", "add1");
		message.put("testing2", "add2");
		message.put("testing3", "add3");
		message.put("testing4", "add4");
		
		Map<String,String> message1 = new HashMap<String,String>();
		message1.put("testing2", "add2");
		message1.put("testing3", "add3");
		message1.put("testing4", "add4");
		
		Map<String,String> message2 = new HashMap<String,String>();
		message2.put("testing2", "add2");
		message2.put("testing3", "add3");
		message2.put("testing4", "add4");
		
		Map<String,Map<String,String>> finalc = new HashMap<String,Map<String,String>>();
		finalc.put("1", message);
		finalc.put("2", message1);
		finalc.put("3", message2);
		
		formData.setFormData(finalc);	*/ 
		
		
	}
	
	@RequestMapping(value = "{id}/getFormData", method = RequestMethod.GET, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Object get(@PathVariable String id){		
		System.out.println("Id: "+id);
		Object object = repositoryImplClass.get(VNF, id);		
		return object;	
		
	}	
	
	@RequestMapping(value = "/getAllPackage", method = RequestMethod.GET, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,Object> getAll(){		
		Map<String,Object> listMap = new LinkedHashMap<String,Object>();
		
		List<Object> object = repositoryImplClass.getAll(VNF);
		Set<String> keys = repositoryImplClass.getAllKeys(VNF);
		Iterator<Object> obj = object.iterator();
		Iterator<String> key = keys.iterator();
		while(obj.hasNext() && key.hasNext()){
			listMap.put(key.next(), obj.next());
		}
		return listMap;	
		
	}
	@RequestMapping(value = "{id}/deleteForm", method = RequestMethod.DELETE, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> deleteForm(@PathVariable String id){		
		Map<String,String> message = new HashMap<String,String>();		
		Long status = repositoryImplClass.delete(VNF, id);
		System.out.println("Status: "+status);
		message.put("type", "sucess");
		message.put("message", "Form deleted sucessfully!");
		return message;
	}
	
	@RequestMapping(value = "/createVnfd", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> CreateVnfd(@RequestBody Vnfd vnfd){	
		Map<String,String> message = new HashMap<String,String>();
		
		util.createVnfd("C:\\Users\\TEST\\Desktop\\vnfOnboarding\\openimscore-packages-master", vnfd.getVnfdName(), vnfd.getVendor(), vnfd.getVersion(), vnfd.getType(), vnfd.getEndpoint(), vnfd.getVmImage(), vnfd.getVim(), vnfd.getScaleInOut(), vnfd.getFloatingIp(), vnfd.getFlavor());
		
		return message;
		
	}
	
	@RequestMapping(value = "{vnfName}/uploadVnfd", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> UploadVnfd(@PathVariable String vnfName,@RequestPart MultipartFile uploadFile){	
		Map<String,String> message = new HashMap<String,String>();
		//"C:\\Users\\TEST\\Desktop\\vnfOnboarding\\openimscore-packages-master\\scscf"
		try {
			String response = util.uploadVnfdPackage(util.convert(uploadFile), vnfName);
			message.put("type", "sucess");
			message.put("message", "sucessfully uploaded");
			message.put("id", response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;		
	}
	
	@RequestMapping(value = "{vnfName}/getVnfStatus", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> getVnfStatus(@PathVariable String vnfName){	
		Map<String,String> message = new HashMap<String,String>();
		//"C:\\Users\\TEST\\Desktop\\vnfOnboarding\\openimscore-packages-master\\scscf"
		String status = util.getVnfStatus(vnfName);
		message.put("type", "sucess");
		message.put("message", "sucessfully get Status");
		message.put("status", status);
		return message;		
	}
	
	@RequestMapping(value = "/createNsd", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> createNsd(@RequestBody PopulateNsd populateNsd){
		
		Map<String,String> message = new HashMap<String,String>();		
		message = util.populateNsd(populateNsd.getVnfdList(),"C:\\Users\\TEST\\Desktop\\vnfOnboarding\\openimscore-packages-master\\descriptors\\tutorial-ims-NSR");		
		return message;		
	}
	
	@RequestMapping(value = "/uploadNsd", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> uploadNsd(@RequestPart MultipartFile uploadFile) throws ParseException{
		
		Map<String,String> message = new HashMap<String,String>();	
		//util.uploadNsd(uploadFile);
		message.put("type", "sucess");
		message.put("message", "sucessfully upload NSD");		
		return message;		
	}	
	
	@RequestMapping(value = "{nsdName}/activateVnf", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf information", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> activateVnf(@PathVariable String nsdName){
		Map<String,String> message = new HashMap<String,String>();		
		util.activateVNF(nsdName);
		message.put("type", "sucess");
		message.put("message", "sucessfully activate Vnf");
		return message;
		
	}
	

	//initialize
	@RequestMapping(value = "{id}/initialize", method = RequestMethod.PUT, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf initialize", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> initialize(@PathVariable String id){
		//public Map<String,String> initialize(@PathVariable String id){
	
		WorkFlowView workFlowView = new WorkFlowView();
		WorkFlowViewObject workFlowViewObject = new WorkFlowViewObject();
		workFlowViewObject.setStatus("not-started");
		
		workFlowView.setUpload(workFlowViewObject);
		workFlowView.setPackage_validation(workFlowViewObject);
		workFlowView.setSecurity(workFlowViewObject);
		workFlowView.setInstantiation(workFlowViewObject);
		workFlowView.setTest_updates(workFlowViewObject);
		workFlowView.setCertification(workFlowViewObject);
		workFlowView.setArtifactory_version_update(workFlowViewObject);
	
		Map<String,String> message = new HashMap<String,String>();
		//repositoryImplClass.save(id,workFlowView);
		repositoryImplClass.save(WORKFLOW, id,workFlowView);
		message.put("type", "sucess");
		message.put("message", "Data save sucessfully!");
		return message;
	}

	
	//update date
	//@RequestMapping(value = "{id}/update", method = RequestMethod.POST, produces = "application/json")	
	//@RequestMapping(value = "{id}/{name}/{status}/update", method = RequestMethod.POST, produces = "application/json")
	@RequestMapping(value = "initialValidationStatus/{id}/update", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "This API used to provide the vnf update", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> update(@PathVariable String id,@RequestBody ValidationStatusBody valStatusbody){
		WorkFlowView workFlowView = new WorkFlowView();
		WorkFlowViewObject workFlowViewObject = new WorkFlowViewObject();
		workFlowViewObject.setStatus(valStatusbody.getStatus());
		Map<String,String> message = new HashMap<String,String>();
		workFlowView=retriveID(id);
			
			 switch (valStatusbody.getPhase()) {
	         case "upload":
	        	 workFlowView.setUpload(workFlowViewObject);
	             break;
	         case "package_validation":
	        	 workFlowView.setPackage_validation(workFlowViewObject);
	             break;
	         case "security":
	        	 workFlowView.setSecurity(workFlowViewObject);
	             break;
	         case "instantiation":
	        	 workFlowView.setInstantiation(workFlowViewObject);
	             break;
	         case "test_updates":
	        	 workFlowView.setTest_updates(workFlowViewObject);
	             break;
	         case "certification":
	        	 workFlowView.setCertification(workFlowViewObject);
	             break;
	         case "artifactory_version_update":
	        	 workFlowView.setArtifactory_version_update(workFlowViewObject);
	             break;
		     }
		repositoryImplClass.save(WORKFLOW, id, workFlowView);
		message.put("type", "sucess");
		message.put("message", "Data Updated sucessfully!");
		return message;
		
	}
	
	//retrieve date
	@RequestMapping(value = "{id}/retrieve", method = RequestMethod.GET, produces = "application/json")	
	@ApiOperation(value = "This API used to provide the vnf retrive", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Object retrieve(@PathVariable String id){		
		System.out.println("Id: "+id);
		Object object = repositoryImplClass.get(WORKFLOW, id);		
		return object;	
	}	
	public WorkFlowView retriveID(String id){
		System.out.println("Id: "+id);
		WorkFlowView object = (WorkFlowView) repositoryImplClass.get(WORKFLOW, id);		
		return object;	
	}
	
	//Upload File 
	@RequestMapping(value = "{id}/UploadFile", method = RequestMethod.POST, produces = "application/json")	
	@ApiOperation(value = "This API is used to Upload a given File", notes = "Returns success or failure SLA:500")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful get all the data"),
			@ApiResponse(code = 400, message = "Invalid input provided"),
			@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
	public Map<String,String> UploadFile(@RequestPart MultipartFile uploadFile,@PathVariable String id){	
		Map<String,String> message = new HashMap<String,String>();
		try {
			String response = util.uploadFile(util.convert(uploadFile));
			message.put("type", "sucess");
			message.put("message", "sucessfully uploaded");
			message.put("id", response);
			
			WorkFlowView workFlowViewUpload = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectUpload = new WorkFlowViewObject();
			workFlowViewObjectUpload.setStatus("completed");
			workFlowViewUpload=retriveID(id);
			workFlowViewUpload.setUpload(workFlowViewObjectUpload);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewUpload);
			
			//Trigger Jenkins job
			util.triggerJenkinsValidation(id);
			
			/*Thread.sleep(15000);
			
			WorkFlowView workFlowViewPV = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectPV = new WorkFlowViewObject();
			workFlowViewObjectPV.setStatus("completed");
			workFlowViewPV=retriveID(id);
			workFlowViewPV.setPackage_validation(workFlowViewObjectPV);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewPV);
			
			Thread.sleep(10000);
			
			WorkFlowView workFlowViewSecurity = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectSecurity = new WorkFlowViewObject();
			workFlowViewObjectSecurity.setStatus("completed");
			workFlowViewSecurity=retriveID(id);
			workFlowViewSecurity.setSecurity(workFlowViewObjectSecurity);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewSecurity);
			
			Thread.sleep(12000);
			
			WorkFlowView workFlowViewInstantiation = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectInstantiation = new WorkFlowViewObject();
			workFlowViewObjectInstantiation.setStatus("completed");
			workFlowViewInstantiation=retriveID(id);
			workFlowViewInstantiation.setInstantiation(workFlowViewObjectInstantiation);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewInstantiation);
			
			Thread.sleep(20000);
			
			WorkFlowView workFlowViewTU = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectTU = new WorkFlowViewObject();
			workFlowViewObjectTU.setStatus("completed");
			workFlowViewTU=retriveID(id);
			workFlowViewTU.setTest_updates(workFlowViewObjectTU);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewTU);
			
			Thread.sleep(15000);
			
			WorkFlowView workFlowViewCert = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectCert = new WorkFlowViewObject();
			workFlowViewObjectCert.setStatus("completed");
			workFlowViewCert=retriveID(id);
			workFlowViewCert.setCertification(workFlowViewObjectCert);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewCert);
			
			Thread.sleep(5000);
			
			WorkFlowView workFlowViewAVU = new WorkFlowView();
			WorkFlowViewObject workFlowViewObjectAVU = new WorkFlowViewObject();
			workFlowViewObjectAVU.setStatus("completed");
			workFlowViewAVU=retriveID(id);
			workFlowViewAVU.setArtifactory_version_update(workFlowViewObjectAVU);
			repositoryImplClass.save(WORKFLOW, id, workFlowViewAVU);*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;		
	}
	
	
	//Test VNF
		@RequestMapping(value = "{id}/testVnf", method = RequestMethod.POST, produces = "application/json")	
		@ApiOperation(value = "This API is used to test a VNF", notes = "Returns success or failure SLA:500")
		@ApiResponses(value = {
				@ApiResponse(code = 200, message = "Successful get all the data"),
				@ApiResponse(code = 400, message = "Invalid input provided"),
				@ApiResponse(code = 404, message = "given Transaction ID does not exist"), })
		public Map<String,String> TriggerJenkins(@PathVariable String id){	
			Map<String,String> message = new HashMap<String,String>();
			util.testVNF(id);
			message.put("type", "sucess");
			message.put("message", "sucessfully triggered");
			return message;		
		}
	
	
}
		
		