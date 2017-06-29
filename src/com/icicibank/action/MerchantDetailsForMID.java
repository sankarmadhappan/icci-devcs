package com.icicibank.action;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;


public class MerchantDetailsForMID {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		
		 HttpClient httpClient = HttpClientBuilder.create().build();
		    //DefaultHttpClient httpClient = new DefaultHttpClient();
		    try
		    {
		   	 HttpPost postRequest = new HttpPost("https://141.144.83.239/MIDUpdateMerchantService/rest/MIDUpdateService/MIDUpdateDetails");
	     
	     //Set the API media type in http content-type header
	     postRequest.addHeader("content-type","application/json");
	      
	     //Set the request post body
	     
	     String merchantId="TEST";
	     
	     
	     
	     StringEntity userEntity = new StringEntity(merchantId);
	     postRequest.setEntity(userEntity);
	       
	     //Send the request; It will immediately return the response in HttpResponse object if any
	     HttpResponse response = httpClient.execute(postRequest);
	    
	      
	     //verify the valid error code first
	     int statusCode = response.getStatusLine().getStatusCode();
	     
	     HttpEntity output  = response.getEntity();
	     
	     //InputStream is=  output.getContent();
	     StringBuffer buff = new StringBuffer(); 
	     HttpEntity entity = response.getEntity();
	     System.out.println("statusCode::"+statusCode);
	     System.out.println("MID::"+entity);
	    
	     
	     if (statusCode != 200) 
	     {
	         throw new RuntimeException("Failed with HTTP error code : " + statusCode);
	     }
	     
	    /* if (entity != null) {


	         InputStream is = entity.getContent();
	         InputStreamReader isr = new InputStreamReader(is);
	         BufferedReader br = new BufferedReader(isr, 1024 * 4);
	         String line = null;
	         while ( (line = br.readLine()) != null) {

	             buff.append(line).append("\n");

	         }
	         is.close();
	     
	     }
	     
	     String xmlStringresp= buff.toString();
	     System.out.println("********Response from Service*********");
	     System.out.println(xmlStringresp);
	     */
	   /*  JAXBContext jc = JAXBContext.newInstance( BillFetchResponseType.class);
	     Unmarshaller u = jc.createUnmarshaller();
	    
	     BillFetchResponseType billFetchResponse=(BillFetchResponseType)u.unmarshal( new StreamSource( new StringReader( xmlStringresp ) ) );
	     
	     System.out.println("Response_RefId::"+billFetchResponse.getHead().getRefId());*/
	     
	       } 
		
		 finally
		    {
		        //Important: Close the connect
		        httpClient.getConnectionManager().shutdown();
		    }
		
	}
}
