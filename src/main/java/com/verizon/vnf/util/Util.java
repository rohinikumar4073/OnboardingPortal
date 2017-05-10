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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;





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
	
	public static void createVnfPackage(String filePath, String vnfName){
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
	
	public static String uploadVnfdPackage(File f, String vnfName){
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
//			uploadFile.setHeader("Accept-Encoding", "gzip, deflate");
//			uploadFile.setHeader("Accept-Language", "en-US,en;q=0.5");
//			uploadFile.setHeader("Authorization", "Bearer "+Token.getToken());
//			uploadFile.setHeader("project-id", Urls.projectid);
			uploadFile.setHeader("Oss-Registration-Id","78bab0f0-4411-3d1a-a0f2-073a03b96f41" );
			//HttpEntity httpEntity = new HttpEntity(uploadFile);
			
			CloseableHttpResponse response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity);		
			/*JSONParser parser = new JSONParser();
			JSONArray ar;
			try {
				ar = (JSONArray) parser.parse(responseString.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Iterator<JSONObject> obj = ar.iterator();
			while(obj.hasNext()){
				JSONObject tempObj = obj.next();
				if(tempObj.get("name").toString().equals("DNS-Info-Blox")){															
					System.out.println(tempObj.get("name"));
					vnfPackageId = tempObj.get("id").toString();
				}
			}*/
			
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
		
	public static String uploadFile(File f){
		String responseString = "";
		String uploadedFileLocation = "/root/vnf_packages/" + f.getName();
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			HttpPost request = new HttpPost("http://localhost:8082/api/scp/uploadPackage");
			
			System.out.println("uploading:::::::::::::: "+f);
			System.out.println("getName::::::::::::: "+f.getName());
			System.out.println("getAbsolutePath::::::::::::: "+f.getAbsolutePath());
					
			

			// Request parameters and other properties.
			
			JSONObject json = new JSONObject();
			
			json.put("host_ip", "10.75.14.22");  
			json.put("username", "sftpuser");
			json.put("password", "Verizon1");
			json.put("source_path",uploadedFileLocation);
			json.put("destination_path", "/home/sftpuser/VNF_Package_Repository");
			StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    saveToFile(new FileInputStream(f),uploadedFileLocation);
			
			CloseableHttpResponse response = httpClient.execute(request);
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


}
