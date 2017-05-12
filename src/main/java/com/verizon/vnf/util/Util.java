package com.verizon.vnf.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Service
public class Util {
	
	
	
	public void createVnfd(String folderPath, String vnfdName, String vendor, String version, String type, 
			String endpoint, String vmImage, String vim, String ScaleInOut, String floatingIp, String flavor){
//		String vnfName = "pcscf";
//		final File folder = new File("/home/sdnuser/Downloads/openimscore-packages-master");
		final File folder = new File(folderPath);
		String filePath = listFilesForFolder(folder, vnfdName);

		populateVnfdData(filePath, vnfdName, vendor, version, type, endpoint, vmImage, vim, ScaleInOut, floatingIp, flavor);
//		populateVnfdData(filePath, vnfName, "fokus", "3.2.0", vnfName, "generic", "openims", "VIM",  "1", "random", "m1.small");

		createMetadata(filePath, vnfdName, vendor, version);

		createVnfPackage(filePath, vnfdName);
	}
	
	public static void populateVnfdData(String filePath, String vnfdName, String vendor, String version, String type, 
			String endpoint, String vmImage, String vim, String ScaleInOut, String floatingIp, String flavor){
		try
		{
			JSONParser parser = new JSONParser();
			System.out.println(filePath);
			JSONObject vnfd = (JSONObject) parser.parse(new FileReader(filePath+"\\vnfd.json"));
			//			JSONObject vnfd = (JSONObject) parser.parse(new FileReader("/home/sdnuser/Downloads/openimscore-packages-master/pcscf/vnfd.json"));
			vnfd.put("name", vnfdName);
			vnfd.put("vendor", vendor);
			vnfd.put("version", version);
			vnfd.put("type", type);
			vnfd.put("endpoint", endpoint);

			JSONArray vdu = (JSONArray) vnfd.get("vdu");
			Iterator<JSONObject> vduIterator = vdu.iterator();
			while (vduIterator.hasNext()) {
				JSONObject vduObj = vduIterator.next();
				JSONArray imgArray = new JSONArray();
				imgArray.add(vmImage);
				vduObj.put("vm_image", imgArray);
				JSONArray vimArray = new JSONArray();
				vimArray.add(vim);
				vduObj.put("vimInstanceName", vimArray);
				vduObj.put("scale_in_out", ScaleInOut);
				JSONArray vnfc = (JSONArray) vduObj.get("vnfc");
				Iterator<JSONObject> vnfcIterator = vnfc.iterator();
				while (vnfcIterator.hasNext()) {
					JSONObject vnfcObj = vnfcIterator.next();
					JSONArray connInput = (JSONArray) vnfcObj.get("connection_point");
					Iterator<JSONObject> connInputIterator = connInput.iterator();
					while (connInputIterator.hasNext()) {
						JSONObject connInputObj = connInputIterator.next();
						connInputObj.put("floatingIp", floatingIp);
					}
				}
			}
			JSONArray depFlvr = (JSONArray) vnfd.get("deployment_flavour");
			Iterator<JSONObject> depFlvrIterator = depFlvr.iterator();
			while (depFlvrIterator.hasNext()) {
				JSONObject depFlvrObj = depFlvrIterator.next();
				depFlvrObj.put("flavour_key", flavor);
			}

			FileWriter file = new FileWriter(filePath+"/vnfd.json");
			file.write(vnfd.toString());
			file.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createMetadata(String filePath, String name, String provider, String nfvoVersion){
		try
		{

			File fout = new File(filePath+"\\Metadata.yaml");
			FileOutputStream fos = new FileOutputStream(fout);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write("name: "+name);
			bw.newLine();
			bw.write("provider: "+provider);
			bw.newLine();
			bw.write("nfvo_version: "+nfvoVersion);
			bw.newLine();
			bw.write("image:");
			bw.newLine();
			bw.write("    upload: false");
			bw.newLine();
			bw.write("vim_types:");
			bw.newLine();
			bw.write("    - openstack");
			bw.newLine();

			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createVnfPackage(String filePath, String vnfName){
		Process p;
		try {
			p = Runtime.getRuntime().exec("tar -cf "+vnfName+".tar "+"vnfd.json "+"Metadata.yaml "+"scripts/", null, new File(filePath));
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream())); String errorStream = null; int exitValue;
			exitValue = p.waitFor();
			System.out.println("exitvalue = "+exitValue); while ((errorStream = br.readLine()) != null) { System.out.println("Error in getting response:"+errorStream); }
			
			
			System.out.println("path :::::::: "+filePath+"\\"+vnfName+".tar");
			
			File newFile = new File(filePath+"\\"+vnfName+".tar");
			
			uploadVnfdPackage(newFile, vnfName);
					
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static String listFilesForFolder(final File folder, String vnfName) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				if(fileEntry.getName().equalsIgnoreCase(vnfName)){
					System.out.println(fileEntry.getName());
					//	            listFilesForFolder(fileEntry);
					return fileEntry.getAbsolutePath();
				}
			} else {
				//	            System.out.println(fileEntry.getName());
			}
		}
		return vnfName;
	}
	
	public String uploadVnfdPackage(File f, String vnfName){
		String responseString = "";
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			HttpPost uploadFile = new HttpPost("http://localhost:8090/vnfs/packages/upload");
//			HttpPost uploadFile = new HttpPost("http://10.76.110.89:8080/api/v1/vnf-packages");
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();			
			System.out.println("uploading: "+f);

			builder.addBinaryBody("file", new FileInputStream(f), ContentType.MULTIPART_FORM_DATA, f.getName());

			HttpEntity multipart = builder.build();
			
			uploadFile.setEntity(multipart);
			uploadFile.setHeader("Oss-Registration-Id",getOssRegistrationId());
			
			CloseableHttpResponse response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity);		
			
			System.out.println("uploading status is  "+responseString);
		}catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseString;
	}
	
	public static File convert(MultipartFile file) throws IOException
	{    
	    File convFile = new File(file.getOriginalFilename());
	    convFile.createNewFile(); 
	    FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(file.getBytes());
	    fos.close(); 
	    return convFile;
	}
	
	public String getVnfStatus(String vnfName){
		try{
			URL url = new URL("http://10.76.110.89:8080/api/v1/ns-records/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			conn.setRequestProperty("Authorization", "Bearer "+Token.getToken());
			conn.setRequestProperty("project-id", Urls.projectid);
			conn.setRequestProperty("Accept", "application/json");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer resp = new StringBuffer();
			String status = null;
			while ((inputLine = in.readLine()) != null) {
				resp.append(inputLine);
			}
			in.close();	
			JSONParser parser = new JSONParser();
			JSONArray ar = (JSONArray) parser.parse(resp.toString());
			Iterator<JSONObject> obj = ar.iterator();
			while(obj.hasNext()){
				JSONObject tempObj = obj.next();
				if(tempObj.get("name").toString().equals(vnfName)){
					System.out.println(tempObj.get("name"));
					status = tempObj.get("status").toString();
				}
			}	
			System.out.println(status);
			return status;

		} catch(Exception e){

	}
		return vnfName;
	}
	
	public Map<String,String>  populateNsd(ArrayList<String> vnfdList, String filePath){
		Map<String,String> message = new HashMap<String,String>();
		String vnfData = getAllVnfdData();
		JSONParser parser = new JSONParser();
		JSONArray vnfd;
		try {
			vnfd = (JSONArray) parser.parse(vnfData.toString());
			JSONArray vnfdIdArary = new JSONArray();
			for(String vnfdName : vnfdList){
				Iterator<JSONObject> vnfdIterator = vnfd.iterator();
				while (vnfdIterator.hasNext()) {
					JSONObject vnfdObj = vnfdIterator.next();
					if(vnfdObj.get("name").toString().equalsIgnoreCase(vnfdName)){
						JSONObject idObj = new JSONObject();
						idObj.put("id", vnfdObj.get("id").toString());
						vnfdIdArary.add(idObj);
					}
				}
			}
			
			JSONObject nsd = (JSONObject) parser.parse(new FileReader(filePath+"\\nsd.json"));
			nsd.put("vnfd", vnfdIdArary);
			
			FileWriter file = new FileWriter(filePath+"\\nsd.json");
			file.write(nsd.toJSONString());
			file.flush();
			message.put("type", "sucess");
			message.put("message", "sucessfully populateNsd");
			file.close();
			File f = new File(filePath+"\\nsd.json");
			uploadNsd(f);
			return message;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.put("type", "failure");
			message.put("message", "fail populateNsd");
			return message;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.put("type", "failure");
			message.put("message", "fail populateNsd");
			return message;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.put("type", "failure");
			message.put("message", "fail populateNsd");
			return message;
		}
	}
	
	private String getAllVnfdData(){
		try {
			URL obj = new URL(Urls.vnfdurl);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+Token.getToken());
			con.setRequestProperty("project-id", Urls.projectid);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();	
			return response.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void activateVNF(String vnfId){
		try {
			String data = "{"
					+ "\"virtualNtwk1\":\"EDN\","
					+ "\"virtualNtwk2\": \"data-network\","
					+ "\"vnfDesc\":\"DNS Info Blox\","
					+ "\"vnfName\":\"DNS-Info-Blox\","
					+ "\"vnfPackageId\":" + "\"" + vnfId + "\"" +
					"}";
			URL obj = new URL("http://localhost:8090/vnfs/instances");

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			con.setRequestProperty("Accept","*/*");
			con.setRequestProperty("Content-type", "application/json");
			con.setDoOutput(true);
			OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
			wr.write(data);
			wr.flush();
			int status = con.getResponseCode();
			System.out.println("response code is "+status);
			if (status != 201) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();	
			}
			/*
			JSONParser parser = new JSONParser();
			JSONArray nsdData = (JSONArray) parser.parse(getAllNsdData());
			Iterator nsdIterator = nsdData.iterator();
			while(nsdIterator.hasNext()){
				JSONObject nsdObj = (JSONObject) nsdIterator.next();
				if(nsdObj.get("name").toString().equalsIgnoreCase(nsdName)){
					System.out.println(nsdObj.get("id").toString());

					JSONObject nr = new JSONObject();
					ArrayList<String> kk = new ArrayList<String>();

					kk.add("kafka");
					nr.put("keys", kk);

					ArrayList<String> vim = new ArrayList<String>();
					vim.add("VIM");

					JSONObject vdu = new JSONObject();
					vdu.put("clientVdu1", vim);
					vdu.put("serverVdu1", vim);

					nr.put("vduVimInstances", vdu);
					URL obj = new URL("http://10.76.110.89:8080/api/v1/ns-records/"+nsdObj.get("id").toString());

					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
					con.setRequestProperty("Accept","*");
					con.setRequestProperty("Content-type", "application/json");

					con.setRequestProperty("Authorization", "Bearer "+Token.getToken());
					con.setRequestProperty("project-id", Urls.projectid);
					con.setDoOutput(true);
					OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
					wr.write(nr.toString());
					wr.flush();
					int status = con.getResponseCode();
					System.out.println("response code is "+status);
					if (status != 201) {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(con.getErrorStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();

						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();	

					}

					else{ 
						BufferedReader in = new BufferedReader(
								new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();

						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();	
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//		for(JSONObject ){
		//			
//				}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getAllNsdData(){

		try {
			URL obj = new URL(Urls.nsdurl);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+Token.getToken());
			con.setRequestProperty("project-id", Urls.projectid);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();	
			return (response.toString());	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public  void uploadNsd(File file) throws ParseException{
		try {

			
			
			JSONParser parser = new JSONParser();
			String nsdData = parser.parse(new FileReader(file)).toString();
			URL obj = new URL(Urls.nsdurl);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-type", "application/json");

			con.setRequestProperty("Authorization", "Bearer "+Token.getToken());
			con.setRequestProperty("project-id", Urls.projectid);
			con.setDoOutput(true);
			OutputStreamWriter wr;
			wr = new OutputStreamWriter(con.getOutputStream());

			wr.write(nsdData.toString());
			wr.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();	

			System.out.println("resp after uploading nsd "+ response.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		//UPload a File
		
	public String uploadFile(File f) throws IOException{
		String responseString = "";

		try {
		//validate package structure
		//ValidateZip(f.getAbsolutePath());
		
		//Upload file
		System.out.println(f.getAbsolutePath());
//		boolean filecopyStatus = upload("10.76.110.110", "invlab09", "sdnnfv@123", f.getAbsolutePath(), "/home/invlab09/testupload/"+f.getName());
		boolean filecopyStatus = upload("10.75.14.22", "sftpuser", "Verizon1", f.getAbsolutePath(), "/home/sftpuser/VNF_Package_Repository/"+f.getName());
		System.out.println("filecopyStatus : " + filecopyStatus);
		
//		boolean isFileExists = exist("10.76.110.110", "invlab09", "sdnnfv@123", "/home/invlab09/testupload/"+f.getName());
		boolean isFileExists = exist("10.75.14.22", "sftpuser", "Verizon1", "/home/sftpuser/VNF_Package_Repository/"+f.getName());
		System.out.println("isFileExists : " + isFileExists);
		
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseString;
	}
	
	private static void saveToFile(InputStream inStream, String target)
			throws IOException {
		System.out.println("target::111:"+target);
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		System.out.println("target::222:"+target);
		out.flush();
		out.close();
		
	}

	public boolean triggerJenkinsValidation(String vnfId){
		boolean status = false;
		try { 
			String line;
			String crumbVal ="";
			Process process1 = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "curl --user ranjasu:Verizon1 http://jenkins-orch.vici.verizon.com:8080/crumbIssuer/api/xml?xpath=concat\\(//crumbRequestField,%22:%22,//crumb\\)"});
			process1.waitFor();
			Integer result = process1.exitValue();
			System.out.println(result);
			InputStream stderr = process1.getErrorStream ();
			InputStream stdout = process1.getInputStream ();

			BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
			BufferedReader errorReader = new BufferedReader (new InputStreamReader(stderr));
			crumbVal = reader.readLine();
			System.out.println("CrumbValue: "+crumbVal);
			while ((line = reader.readLine ()) != null) {
				System.out.println ("Stdout: " + line);
			}
			while ((line = errorReader.readLine ()) != null) {
				System.out.println ("Stderr: " + line);
			}

			Process process2 = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "curl X POST -H \""+crumbVal+"\""+" --user ranjasu:Verizon1 --header 'content-type:application/x-www-form-urlencoded' http://jenkins-orch.vici.verizon.com:8080/job/VNF_Onboarding_CICD_Pipeline/job/VNF_Package_Upload/build?token=xyz"});
			//--data-urlencode json='{\"parameter\":[{\"vnfId\":\""+vnfId + "\"}]}'
			//			System.out.println("curl X POST -H \""+crumbVal+"\""+" --user ranjasu:Verizon1 --header 'content-type:application/x-www-form-urlencoded' --data-urlencode json='{\"parameter\":[{\"vnfId\":\""+vnfId+ "\"}]}' http://jenkins-orch.vici.verizon.com:8080/job/VNF_Onboarding_CICD_Pipeline/job/VNF_Package_Upload/build?token=xyz");
			process2.waitFor();
			Integer result2 = process2.exitValue();
			System.out.println(result2);
			InputStream stderr2 = process2.getErrorStream ();
			InputStream stdout2 = process2.getInputStream ();

			BufferedReader reader2 = new BufferedReader (new InputStreamReader(stdout2));
			BufferedReader errorReader2 = new BufferedReader (new InputStreamReader(stderr2));
			crumbVal = reader2.readLine();
			while ((line = reader2.readLine ()) != null) {
				System.out.println ("Stdout: " + line);
			}
			while ((line = errorReader2.readLine ()) != null) {
				System.out.println ("Stderr: " + line);
			}
			status = true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return status;
	}
	
	
	public void testVNF(String vnfId){
		String host = "10.75.46.142";
		String user = "root";
		String password = "Verizon1";
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		Channel channel = null;

		try {
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connecting Ssh ... Session is =  "+session.isConnected());
			channel = session.openChannel("exec");
			String cmd="/root/yardstick/trigger-test.sh";
			System.out.println("Command to be executed = " + cmd);
			((ChannelExec) channel).setCommand(cmd);
			channel.connect();
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exceptions = " +e.getMessage().toString());
			System.out.println(e.getMessage().substring(1, 300).toString());
		}
	}
	
	
	public void ValidateZip(String filePath) {
		try {
			ArrayList<String> set = new ArrayList<String>();
			set.add("ziptest/");
			//set.add("ziptest/test/test.txt");
			//set.add("ziptest/test/");
			set.add("alter_varchar_clob.sql");
			set.add("runtime_config.sql");
			String filename = "forZipTest";
			byte[] buf = new byte[1024];
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new FileInputStream(filePath));
			ArrayList<String> zipNames = new ArrayList<String>();
			ArrayList<String> entryNames = new ArrayList<String>();
//			entryNames.add("")
			while ((zipentry = zipinputstream.getNextEntry()) != null) {
				String entryName = zipentry.getName();
				if (entryName.equals(filename + "/"))
					continue;
				entryNames.add(entryName);
				String s0 = entryName.substring(entryName.indexOf("/") + 1);

				if (s0.indexOf("/") > -1) {
					s0 = s0.substring(0, s0.indexOf("/"));
					if (!zipNames.contains(s0))
						zipNames.add(s0);
				}
			}
			System.out.println("zipNames:" + zipNames);
			System.out.println("entryNames: " + entryNames);
			if (!validate(filename, set, zipNames, entryNames))
				System.out.println("Zip is bad");
			else
				System.out.println("Zip is good");
			zipinputstream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public boolean validate(String filename, ArrayList<String> set, ArrayList<String> zipNames,
			ArrayList<String> entryNames) {
		boolean b = true;
		for (String s : zipNames) {
			for (String s1 : set) {
				String s2 = s1.replaceAll("fileName", filename);
				String s3 = s2.replaceAll("zipName", s);
				System.out.println("s3: " + s3);
				if (!entryNames.contains(s3)) {
					System.out.println("file does not contain " + s3);
					return false;
				}
				entryNames.remove(s3);
			}
		}
		
		if (entryNames.size() > 0) {
			/*if (entryNames.size() != set.size()){
				System.out.println("Size is different");
				return false;
			}*/
			HashMap<String, String> map = new HashMap<String, String>();
		    for (String str : set) {
		        map.put(str, str);
		    }
		    for (String str : entryNames) {
		        if ( ! map.containsKey(str) ) {
		            return false;
		        }
		    }
		}
		return b;
	}
	
	public static boolean upload(String hostName, String username, String password, String localFilePath,
			String remoteFilePath) {
		boolean isFileCopySuccessful = false;
		File file = new File(localFilePath);
		if (!file.exists())
			throw new RuntimeException("Local file not found");

		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			FileObject localFile = manager.resolveFile(file.getAbsolutePath());
			FileObject remoteFile = manager.resolveFile(getURIString(hostName, username, password, remoteFilePath), createDefaultOptions());

			remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
			isFileCopySuccessful = true;
			System.out.println("File upload success");
		} catch (Exception e) {
			isFileCopySuccessful = false;
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
		return isFileCopySuccessful;
	}

    public static boolean exist(String hostName, String username, String password, String remoteFilePath) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(getURIString(hostName, username, password, remoteFilePath), createDefaultOptions());
            System.out.println("File exist: " + remoteFile.exists());
            return remoteFile.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }
    
	public static String getURIString(String hostName, String username, String password,
			String remoteFilePath) throws Exception {
		URI uri = null;
		try {
			uri = new URI("sftp", username + ":" + password, hostName, -1, remoteFilePath, null, null);
		} catch (URISyntaxException urise) {
			System.out.println("Exception while constructing URI : " + urise.getMessage());
			throw new Exception(urise);
		}
		return uri.toString();
	}

	public static FileSystemOptions createDefaultOptions() throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		try {
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
		
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
		// Timeout is count by Milliseconds
		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 300000);
		} catch (org.apache.commons.vfs2.FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return opts;
	}

	public String getOssRegistrationId(){
		try {
			URL url = new URL("http://10.75.14.133:8090/settings/nfvo");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type", "application/json");
			String input = "{ \"sOrchType\": \"HP\", \"sPassword\": \"Welcome@1234\", \"sTargetURL\": \"http://10.75.14.83:8080\", \"sUsername\": \"vdsi_onb_vnf_mgr@vdsi\" }";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output = br.readLine();
			JSONParser parser = new JSONParser();
			JSONObject resultObject = (JSONObject)parser.parse(output);
			System.out.println(resultObject.get("nfvoId"));
			conn.disconnect();


			URL url2 = new URL("http://10.75.14.133:8090/settings/applications");
			HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
			conn2.setRequestMethod("POST");
			conn2.setRequestProperty("Content-type", "application/json");
			String input2 = "{\"appName\": \"MyOSS\", "
					+ "\"domainId\": \"6ea7a82f-1f7c-42d7-abe4-2c6d92d94d30\", "
					+ "\"modeInstanceId\": \"45698bbf-0419-4be4-acfe-4427c00054f7\","
					+ "\"modeInstanceUndeployId\": \"7c10bed4-6aa0-47f2-a54c-00515a410b54\","
					+ "\"nfvoId\": \""+resultObject.get("nfvoId")
					+"\", \"orchType\": \"HP\", \"orgId\": \"b877eb45-c18d-46eb-8a79-d20c3204d23d\","
					+ "\"resourceArtifactId\": \"c4ad5969-f921-3552-8c66-7828a6b5d306\","
					+ "\"tenantId\": \"f8ff51d0-3bac-4dbb-998d-d7a155aaf384\","
					+ "\"vnfGroupId\": \"43b5dfee-ec46-4101-aaa2-ca412f7ba056\" }";

			OutputStream os2 = conn2.getOutputStream();
			os2.write(input2.getBytes());
			os2.flush();

			if (conn2.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn2.getResponseCode());
			}
			BufferedReader br2 = new BufferedReader(new InputStreamReader(
					(conn2.getInputStream())));

			String output2 = br2.readLine();
			JSONObject resultObject2 = (JSONObject)parser.parse(output2);
			System.out.println(resultObject2.get("ossRegistrationId"));

			return resultObject2.get("ossRegistrationId").toString();

		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
}
