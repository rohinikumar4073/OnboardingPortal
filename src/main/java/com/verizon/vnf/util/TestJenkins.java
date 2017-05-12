package com.verizon.vnf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestJenkins {

	public static void main(String[] args) {
		
		System.out.println("curl X POST -H \""+"------"+"\""+" --user ranjasu:Verizon1 --header 'content-type:application/x-www-form-urlencoded' --data-urlencode json='{\"parameter\":[{\"vnfId\":\""+"-------"+ "\"}]}' http://jenkins-orch.vici.verizon.com:8080/job/VNF_Onboarding_CICD_Pipeline/job/VNF_Package_Upload/build?token=xyz");

		
		/*try { 
			String line;
			String crumbVal ="";
			Process process1 = Runtime.getRuntime().exec(new String[] {"curl --user ranjasu:Verizon1 http://jenkins-orch.vici.verizon.com:8080/crumbIssuer/api/xml?xpath=concat\\(//crumbRequestField,%22:%22,//crumb\\)"});
			process1.waitFor();
			Integer result = process1.exitValue();
			System.out.println(result);
			InputStream stderr = process1.getErrorStream ();
			InputStream stdout = process1.getInputStream ();

			BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
			BufferedReader errorReader = new BufferedReader (new InputStreamReader(stderr));
			crumbVal = reader.readLine();
			while ((line = reader.readLine ()) != null) {
				System.out.println ("Stdout: " + line);
			}
			while ((line = errorReader.readLine ()) != null) {
				System.out.println ("Stderr: " + line);
			}
			
			Process process2 = Runtime.getRuntime().exec(new String[] {"curl -X POST -H \""+crumbVal+" --user ranjasu:Verizon1 http://jenkins-orch.vici.verizon.com:8080/job/VNF_Onboarding_CICD_Pipeline/job/VNF_Package_Upload/build?token=xyz"});
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
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */

	}

}
