package com.icicibank.action.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.icici.mid.dao.MIDUpdateDAO;
import com.icicibank.icollect.service.LogManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MIDUpdateServiceActionTest {

	    static JSONObject reqOBJ=null;
	    JSONObject resOBJ= new JSONObject();
	    static MIDUpdateDAO midUpdateDAO=null;
        String icid = null;
        String mid = null;
		String mob_ic_id = null; 
		String status = null;
		String cust_tran_limit = null;  
		String cust_Daily_tran_limit =null;
		String isOnBorded=null;
		boolean flag=false;
		String resIcID=null;
		String resStatus=null;
		String actCode=null;
		String reqURL=null;
		CloseableHttpClient httpClient =null;
		HttpEntity entity=null;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 reqOBJ = new JSONObject();
		 reqOBJ.put("ICID","105163");                                          
		 reqOBJ.put("MID","60002345");                                            
		 reqOBJ.put("IMOB_IC_ID","818690324399");                               
		 reqOBJ.put("Status","2");                                      
		 reqOBJ.put("Cust_Transaction_Limit","1000");          
		 reqOBJ.put("Cust_Daily_TransactionLimit","500");
	}
	
	@Before
	public void  setUP(){
		
		 midUpdateDAO= new MIDUpdateDAO();
	}


	@Test
	public void test01_readJSONParam()throws Exception  {

	    icid = (String) reqOBJ.get("ICID");
        mid = (String) reqOBJ.get("MID");
        mob_ic_id = (String) reqOBJ.get("IMOB_IC_ID");
	    status = (String) reqOBJ.get("Status");
	    cust_tran_limit   =   (String)reqOBJ.get("Cust_Transaction_Limit");
	    cust_Daily_tran_limit   = (String) reqOBJ.get("Cust_Daily_TransactionLimit");
		
		assertNotNull("ICID should not be null", icid);
		assertNotNull("MID should not be null", mid);
		assertNotNull("IMOB_IC_ID should not be null", mob_ic_id);
		assertNotNull("Status should not be null", status);
		assertNotNull("Cust_Transaction_Limit should not be null", cust_tran_limit);
		assertNotNull("Cust_Daily_TransactionLimit should not be null", cust_Daily_tran_limit);
	}
	
	@Test
	public void test02_getDate() {
		SimpleDateFormat fm=new SimpleDateFormat("dd-MMM-yy");
		Date date=new Date();
		String currrentDate=fm.format(date);
		String currentDate=currrentDate.toUpperCase();
		LogManager.logMessage("currentDate::"+currentDate);
		assertNotNull("currentDate should not be null",currentDate);
	}
	
	@Test
	public void test03_isMIDExist() throws Exception{
	   // midUpdateDAO= new MIDUpdateDAO();
		flag=midUpdateDAO.isMIDExist(mid,icid);
		
		if(flag){
			resOBJ.put("Status", "N");
			resOBJ.put("Message", "MID Duplicate");
			resOBJ.put("ResponseDate","17-APR-2017");
			fail("MID already exist");
		}
	}
	
	@Test
	public void test04_updateMIDDetails(){
		if(!flag){
		//midUpdateDAO= new MIDUpdateDAO();
		String response[]=	midUpdateDAO.updateMIDdetails(icid, mid, mob_ic_id, status,cust_tran_limit,cust_Daily_tran_limit);
		assertNotNull("Status should not be null",response[0]);
		assertNotNull("Message should not be null",response[1]);
		assertNotNull("ResponseDate should not be null",response[2]);
		
		
		LogManager.logMessage("MSG::"+response[1]);
		LogManager.logMessage("Status::"+response[0]);
		LogManager.logMessage("Response Date::"+response[2]);

		
		if("Y".equals(response[0])){		
			isOnBorded="A";
		}else{
			isOnBorded="R";
		}
		 LogManager.logMessage("isOnBorded status:"+isOnBorded);
		}
	}
	@Test
	public void test05_gerURL(){
		//midUpdateDAO= new MIDUpdateDAO();
		reqURL=midUpdateDAO.getURL(icid);
		assertNotEquals("reqURL should not be null", null,reqURL);
	}
	@Test
	public void test06_hitMobAppService(){
		try{
		httpClient = HttpClientBuilder.create().build();
		reqURL=midUpdateDAO.getURL(icid);
	    	
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
	 
			 
			 entity = resp.getEntity();
			 LogManager.logMessage("statusCode::"+statusCode);
	 
				 if (statusCode != 200) 
				 {
					 LogManager.logMessage("Failed with HTTP error code : " + statusCode);
					 int updateCnt1=midUpdateDAO.updateIPGMID(resIcID, resStatus, actCode, icid, mid,0);//0 pending
					 LogManager.logMessage("updateCnt1::"+updateCnt1);
				    // throw new RuntimeException("Failed with HTTP error code : " + statusCode);
				     assertTrue("updateCnt1 for pending", updateCnt1==1);
				 }
				 
				 if (entity != null) {/*
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
				    int updateCnt= midUpdateDAO.updateIPGMID(resIcID, resStatus, actCode, icid, mid,2);//2 Sucess
				     assertTrue("", condition);
				     }
					//Amit Awade added 16012016[End]
				 */}
		}catch(Exception e){
			
		}
	}
	@Test
	public void test_07_entityIsNotNull(){
		String message=null;;
		 StringBuffer buff = new StringBuffer(); 
		 try{
			 httpClient = HttpClientBuilder.create().build();
				reqURL=midUpdateDAO.getURL(icid);
			    	
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
					entity = resp.getEntity();

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
	    int updateCnt2= midUpdateDAO.updateIPGMID(resIcID, resStatus, actCode, icid, mid,2);//2 Sucess
	    LogManager.logMessage("updateCnt2::"+updateCnt2);
	     assertTrue("updateCnt for 2", updateCnt2==1);
	     }
		//Amit Awade added 16012016[End]
	 
	}catch(Exception e){
		LogManager.logMessage("Exception::"+e);
		fail("Exception");
	}

}
	
}
