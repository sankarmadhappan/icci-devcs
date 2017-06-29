package com.icici.mid.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.icicibank.icollect.service.LogManager;


public class ConnectionManager {
	
	private static String datasourceName;

	static {
		try {

			datasourceName = FilePropertyManager.getProperty("iCollect.properties",
					"IC_DATASOURCE");

		} catch (Exception e) {
			LogManager.logMessage("Exception loading database property : "
					+ e.getMessage());
		}
	}
	
	
	public static Connection getConnection() {
		Connection orConnection = null;
		try {
			LogManager.logMessage("Getting DB connection in WebLogic");

			Context env = null;

			DataSource pool = null;

			Hashtable ht = new Hashtable();

			
			ht.put(Context.INITIAL_CONTEXT_FACTORY,
					"weblogic.jndi.WLInitialContextFactory");

			env = new InitialContext(ht);

			LogManager.logMessage("datasourceName ::: " + datasourceName);
			
			pool = (DataSource) env.lookup(datasourceName);

			orConnection = pool.getConnection();

		} catch (Exception e) {
			LogManager.logMessage("Exception in getting Weblogic DB connection : " + e.getMessage());
		}

		return orConnection;
	}
	

	
}
