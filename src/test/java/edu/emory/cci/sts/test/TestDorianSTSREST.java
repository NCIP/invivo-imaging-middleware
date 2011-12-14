package edu.emory.cci.sts.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestDorianSTSREST {
	public static String baseUrl = "http://localhost:8080/SecurityTokenServiceDorian/rest/STS";
	public static String issueToken = baseUrl + "/issueToken";
	public static String validateToken = baseUrl + "/validateToken";
	public static String username = "nadir"; // username pulled from configured ldap
	public static String password = "DorianAdmin$1";
	private DefaultHttpClient httpclient = null;
	
	public TestDorianSTSREST()
	{
		httpclient = new DefaultHttpClient();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username,password);
		List<String> authpref = new ArrayList<String>();
		authpref.add(AuthPolicy.BASIC);
		authpref.add(AuthPolicy.DIGEST);
		httpclient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
		HttpHost targetHost = new HttpHost("localhost", 8080, "http"); 
		
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), creds);
		
	}
	
	@Test
	public void issueToken() {
		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("targetService", "http://services.testcorp.org/provider1"));
			String queryString = URLEncodedUtils.format(qparams, "UTF-8");
			String httpGetRequestUrl = issueToken + "?" + queryString;
			
			System.out.println("Executing HttpGet on [" + httpGetRequestUrl + "]");
			HttpResponse response =  httpclient.execute(new HttpGet(httpGetRequestUrl));
			HttpEntity entity = response.getEntity();
			if (entity != null) {
			 
			        System.out.println(EntityUtils.toString(entity));
			    
			}
			else{
				fail();
			}
		} catch (Exception e) {
			fail(e.toString());
		}

	}
	
	@Test
	public void validateToken () {
		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("targetService", "http://services.testcorp.org/provider1"));
			String queryString = URLEncodedUtils.format(qparams, "UTF-8");
			String httpGetRequestUrl = issueToken + "?" + queryString;
			
			System.out.println("Executing HttpGet on [" + httpGetRequestUrl + "]");
			HttpResponse response =  httpclient.execute(new HttpGet(httpGetRequestUrl));
			HttpEntity entity = response.getEntity();
			String content = null;
			if (entity != null) {
					content = EntityUtils.toString(entity);
			        System.out.println(content);
			    
			}else
			{
				fail();
			}
			
			HttpPost validateRequest = new HttpPost(validateToken);
			
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("token", content));
		
			UrlEncodedFormEntity formentity = new UrlEncodedFormEntity(formparams, "UTF-8");
			
			validateRequest.setEntity(formentity);
			HttpResponse serverresponse =  httpclient.execute(validateRequest);
			entity = serverresponse.getEntity();
			if (entity != null) {
					content = EntityUtils.toString(entity);
					assertNotNull(content);
			        System.out.println(content);
			        assertEquals(content, "http://docs.oasis-open.org/ws-sx/ws-trust/200512/status/valid");
			    
			}
			else
			{
				fail();
			}
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
