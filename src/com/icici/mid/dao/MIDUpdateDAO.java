package com.icici.mid.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.icici.mid.util.ConnectionManager;
import com.icicibank.icollect.service.LogManager;

public class MIDUpdateDAO {

	public String[] updateMIDdetails(String icid,String mid,String mob_ic_id,String Status,String cust_tran_limit,String cust_Daily_tran_limit) {
		
		Connection conn=null;
		CallableStatement ctmt = null;
		String msg = null;
		String flag = null;
		String successDate=null;
		ResultSet rs= null;
		
		String result[] = new String[3];
		
		try{
			conn= ConnectionManager.getConnection();
			//conn= ConnectionManager.getConnection();
			ctmt = conn.prepareCall("{CALL IC_MOBAPP_MID_UPDATE(?,?,?,?,?,?,?,?,?)}");
			
			ctmt.setString(1,icid);
			ctmt.setString(2,mid);
			ctmt.setString(3,mob_ic_id);
			ctmt.setString(4,Status);
			ctmt.setString(5,cust_tran_limit);
			ctmt.setString(6,cust_Daily_tran_limit);
			
			
			ctmt.registerOutParameter(7,Types.VARCHAR);
			ctmt.registerOutParameter(8,Types.VARCHAR);
			ctmt.registerOutParameter(9,Types.VARCHAR);
			ctmt.executeUpdate();
			flag = ctmt.getString(7);
			msg = ctmt.getString(8);
			successDate=ctmt.getString(9);
			result[0]=flag;
			result[1]=msg;
			result[2]=successDate;
			
		}catch(Exception e) {
			LogManager.logMessage("Exception in DAO : "+e.getMessage());
			
			
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(ctmt != null)
					ctmt.close();
				if(conn != null)
					conn.close();
			} catch(Exception e) {
				LogManager.logMessage("unable to close the connection " + e.getMessage());
				
			}
		}     
		return result;
		
	}
	public int updateIPGMID(String mobIcId,String status,String actCode,String icid,String mid,int reqResFlg){

		
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs= null;
		String query="update ic_ipg_mid_update set IPG_MOB_IC_ID_RESP=? ,IPG_STATUS_RESP=?,IPG_ACTCODE_RESP=?,IPG_REQ_RES_FLG=? where IPG_IC_ID=? and IPG_MID=?";
		int updatCnt=0;
		
		try{
			conn= ConnectionManager.getConnection();
			//conn= ConnectionManager.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1,mobIcId);
			ps.setString(2,status);
			ps.setString(3,actCode);
			ps.setInt(4,reqResFlg);
			ps.setString(5,icid);
			ps.setString(6, mid);
		updatCnt=ps.executeUpdate();
		LogManager.logMessage("Update Count::"+updatCnt);	
		}catch(Exception e) {
			LogManager.logMessage("Exception in  updateIPGMID DAO : "+e.getMessage());
			
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
				if(conn != null)
					conn.close();
			} catch(Exception e) {
				LogManager.logMessage("unable to close the connection " + e.getMessage());
				
			}
		}     
		return updatCnt;
	
	}
	
	public String getURL(String icid){
		String reqUrlQry="select IHPD_REQ_URL from IC_H2H_PROP_DETAILS where IHPD_IC_ID =? and IHPD_DEL_FLAG=0";

		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;

		String requestUrl=null;
		try {
		con= ConnectionManager.getConnection();
		//con=ConnectionManager.getConnection();        	 
		ps=con.prepareStatement(reqUrlQry);
		ps.setString(1,"107727");

		rs=ps.executeQuery();
		     while(rs.next()){
		     requestUrl=rs.getString(1);}
		     LogManager.logMessage("requestUrl from db:"+requestUrl);

		} catch (Exception e) {
		          LogManager.logMessage("Exception in URL connection getURL :"+e.getMessage());
		      }finally{
		          try{
		              if(rs !=null)
		                  rs.close();
		              if(ps !=null)
		                  ps.close();
		              if(con !=null)
		                  con.close();
		                 
		          }catch(Exception e)
		          {
		              LogManager.logMessage("Exception URL connection  :"+e.getMessage());
		          }
		      }
		return requestUrl;
	}
	
	
	public boolean isMIDExist(String pushMid,String icid){
		String query="select ICMM_MID from IC_MERCHANT_MASTER_TBL "+
        " where ICMM_MID=? and ICMM_DEL_FLG=0 and ICMM_ENTITY_CRE_FLG=2 and ICMM_IC_ID<> ? ";

		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		boolean flag=false;
		String mid=null;
		try {
		con= ConnectionManager.getConnection();
		//con=ConnectionManager.getConnection();        	 
		ps=con.prepareStatement(query);
		ps.setString(1,pushMid);
		ps.setString(2,icid);
		rs=ps.executeQuery();
		     while(rs.next()){
		     mid=rs.getString(1);
		     }
		     LogManager.logMessage("mid from db:"+mid);
		     if(mid!=null){
		    	 flag=true;
		     }
		} catch (Exception e) {
		          LogManager.logMessage("Exception in URL connection isMIDExist :"+e.getMessage());
		      }finally{
		          try{
		              if(rs !=null)
		                  rs.close();
		              if(ps !=null)
		                  ps.close();
		              if(con !=null)
		                  con.close();
		                 
		          }catch(Exception e)
		          {
		              LogManager.logMessage("Exception URL connection  :"+e.getMessage());
		          }
		      }
		return flag;
	}
	
	
	
}
