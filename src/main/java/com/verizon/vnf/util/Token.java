package com.verizon.vnf.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.TrustManager;

public class Token {

	public static String getToken() throws IOException, JSONException
	{
		URL obj = new URL(Urls.tokenurl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		String authStr = "openbatonOSClient"+":"+"secret";

		byte[] bytesEncoded = Base64.encodeBase64(authStr .getBytes());
		String authEncoded = new String(bytesEncoded);

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Authorization", "Basic "+authEncoded);

		String urlParameters = "username=admin&password=openbaton&grant_type=password";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println("response from server is "+response.toString());

		JSONObject resp = new JSONObject(response.toString());
		return (String) resp.get("value");



	}

}
