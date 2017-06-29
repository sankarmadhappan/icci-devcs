package com.icicibank.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.icici.mid.dao.MIDUpdateDAO;
import com.icicibank.icollect.service.LogManager;

@Path("/MIDUpdateService")
public class MIDUpdateServiceAction {

	
	@POST
	@Path("/MIDUpdateDetails")
	@Produces(MediaType.APPLICATION_JSON)
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response execute(String requestData) throws JSONException {	
	
	LogManager.logMessage("requestData::"+requestData);
	JSONObject resOBJ =null;
	String reqURL=null;
	String isOnBorded=null;
	String resIcID=null;
	String resStatus=null;
	String actCode=null;
	//HttpClient httpClient=null;
	CloseableHttpClient httpClient =null;
	boolean flag;
	resOBJ= new JSONObject();
	try{
		JSONObject reqOBJ = new JSONObject(requestData);
		
		    String icid = (String) reqOBJ.get("ICID");
	        String mid = (String) reqOBJ.get("MID");
	        String mob_ic_id = (String) reqOBJ.get("IMOB_IC_ID");
			String status = (String) reqOBJ.get("Status");
			String cust_tran_limit   =   (String)reqOBJ.get("Cust_Transaction_Limit");
			String cust_Daily_tran_limit   = (String) reqOBJ.get("Cust_Daily_TransactionLimit");
     
		MIDUpdateDAO midUpdateDAO= new MIDUpdateDAO();
		flag=midUpdateDAO.isMIDExist(mid,icid);
		if(!flag){
		String response[]=	midUpdateDAO.updateMIDdetails(icid, mid, mob_ic_id, status,cust_tran_limit,cust_Daily_tran_limit);
		
		resOBJ.put("Status", response[0]);
		resOBJ.put("Message", response[1]);
		resOBJ.put("ResponseDate", response[2]);
		
		if("Y".equals(response[0])){		
			isOnBorded="A";
		}else{
			isOnBorded="R";
		}
		
		
		LogManager.logMessage("MSG::"+response[1]);
		LogManager.logMessage("Status::"+response[0]);
		LogManager.logMessage("Response Date::"+response[2]);
		//Amit Awade added 16012016[Start]
		reqURL=midUpdateDAO.getURL(icid);
		
		//httpClient = HttpClientBuilder.create().build();
		httpClient = HttpClientBuilder.create().build();
		String message=null;;
	    	
			JSONObject reqObject = new JSONObject();
			//reqObject.put("ICID",mob_ic_id );
			reqObject.put("ICID",icid );
			reqObject.put("OnBoarded", isOnBorded);
			reqObject.put("MID", mid);
			LogManager.logMessage("reqObject::"+reqObject);
	    	//System.out.println(reqObject);
	    	HttpPost postRequest = new HttpPost(reqURL);
	   	    
	   	    //Set the API media type in http content-type header
	        postRequest.addHeader("content-type", "application/json");

			//Set the request post body
			StringEntity userEntity = new StringEntity(reqObject.toString());
			postRequest.setEntity(userEntity);
	   
			//Send the request; It will immediately return the response in HttpResponse object if any
			HttpResponse resp = httpClient.execute(postRequest);
	  
			//verify the valid error code first
			int statusCode = resp.getStatusLine().getStatusCode();
	 
			 StringBuffer buff = new StringBuffer(); 
			 HttpEntity entity = resp.getEntity();
			 LogManager.logMessage("statusCode::"+statusCode);
	 
				 if (statusCode != 200) 
				 {
					 LogManager.logMessage("Failed with HTTP error code : " + statusCode);
					 midUpdateDAO.updateIPGMID(resIcID, resStatus, actCode, icid, mid,0);//0 pending
				    // throw new RuntimeException("Failed with HTTP error code : " + statusCode);
				     
				 }
				 
				 if (entity != null) {
				     InputStream is = entity.getContent();
				     InputStreamReader isr = new InputStreamReader(is);
				     BufferedReader br = new BufferedReader(isr, 1024 * 4);
				     String line = null;
				     while ( (line = br.readLine()) != null) {
				         buff.append(line).append("\n");
				     }
				     is.close();
				     message=buff.toString();
				     LogManager.logMessage("Response Message updateOnBoardStatus"+message);
				     
				     JSONObject resObject=new JSONObject(message);
				      resIcID=resObject.getString("ICID");
				      resStatus=resObject.getString("status");
				      actCode=resObject.getString("ActCode");
				      LogManager.logMessage("resIcID::"+resIcID+" resStatus::"+resStatus+" actCode::"+actCode+" icid::"+icid+" mid::"+mid);
				     
				     if(resObject!=null){
				     midUpdateDAO.updateIPGMID(resIcID, resStatus, actCode, icid, mid,2);//2 Sucess
				     }
					//Amit Awade added 16012016[End]

				 }
		}else{
			resOBJ.put("Status", "N");
			resOBJ.put("Message", "MID Duplicate");
			resOBJ.put("ResponseDate",getDate());
		}
				
	}
	catch(JSONException json){
		LogManager.logMessage("Exception in MIDUpdateServiceAction"+json);
	}
	catch(Exception e){
		LogManager.logMessage("Exception in MIDUpdateServiceAction exception"+e);
	}finally
    {
        //Important: Close the connect
        //httpClient.getConnectionManager().shutdown();
		try {
			if(httpClient!=null){
				httpClient.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LogManager.logMessage("Exception "+e);
		}
    }
	 return Response.status(200).entity(resOBJ.toString()).build();	
		
	
	}
	
	public String getDate() {
		SimpleDateFormat fm=new SimpleDateFormat("dd-MMM-yy");
		Date date=new Date();
		String currrentDate=fm.format(date);
		return currrentDate.toUpperCase();
	}
}
