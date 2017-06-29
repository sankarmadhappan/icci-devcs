package com.icici.mid.util;

import java.util.Properties;

import com.icicibank.icollect.service.LogManager;



public class FilePropertyManager {

	
	private static Properties prop = null;
	public static String getPropertyValue(String propertyFileName,String propertyName) {

		
		
		String propertyValue="";
		
		FilePropertyManager	propload	=	new FilePropertyManager();
		Properties prop					=	propload.propertiesFileLoad();
		propertyValue			=	prop.getProperty(propertyName);
		
		
		return propertyValue; // Returns the property value.
	}
	
	
	
	
	public static String getProperty(String propertyFileName,
			String propertyName) throws Exception {

		
		if (propertyFileName == null || propertyFileName.trim().equals("")) {
			LogManager.logMessage("Property File name is null");
		}
		if (propertyName == null || propertyName.trim().equals("")) {
			LogManager.logMessage("Property name is null");
		}
		return getPropertyValue(propertyFileName, propertyName);

	}
	
	
	public Properties propertiesFileLoad() {
		try{
			if(prop==null)
			{
				prop	=	new Properties();	
				prop.load(getClass().getClassLoader().getResourceAsStream("resources/iCollect.properties"));
			}
		}
		catch(Exception e)
		{
			LogManager.logMessage("Exception occured while loading common properties file"+e);
		}
		return prop;
	}
	
	
	
	


/*
	//for normal property test
	private static ConcurrentHashMap<String, Object> propertyMap = new ConcurrentHashMap<String, Object>();

	public static String getPropertyValue(String propertyFileName,String propertyName) {

		
		String propFileName = "resources/iCollect.properties";
		
		Properties properties = new Properties();
		String propertyValue="";
		InputStream inputstream = FilePropertyManager.class.getClassLoader().getResourceAsStream(propFileName);
		try {
			if (inputstream != null) {
				properties.load(inputstream);
				propertyValue = properties.getProperty(propertyName);
			} else {
				throw new FileNotFoundException("property file '"+ propFileName + "' not found in the classpath");
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			try {
				if (inputstream != null)
					inputstream.close();
			} catch (IOException e) {
				e.getStackTrace();
				LogManager.logMessage(e.getMessage());
			}
		}
		propertyMap.put(propFileName, properties);
		return propertyValue; // Returns the property value.
	}
	
	
	
	
	public static String getProperty(String propertyFileName,
			String propertyName) throws ICollectFatalException {

		if (propertyFileName == null || propertyFileName.trim().equals("")) {
			throw new ICollectFatalException("Property File name is null");
		}
		if (propertyName == null || propertyName.trim().equals("")) {
			throw new ICollectFatalException("Property name is null");
		}
		return getPropertyValue(propertyFileName, propertyName);

	}
	
	
	
	
	//reading outside properties file
	//for UAT & Live
	private static ConcurrentHashMap<String, Object> propertyMap = new ConcurrentHashMap<String, Object>();

	private static synchronized Properties getPropertyObject(String propertyPath) {

		FileInputStream fin = null;
		Properties properties = null;
		File file = null;

		try {
			file = new File(propertyPath);
			//System.out.println(file.getAbsolutePath());
			fin = new FileInputStream(file);
			properties = new Properties();
			properties.load(fin);			
		} catch (FileNotFoundException ef) {			
			ef.printStackTrace();
		} catch (IOException ei) {			
			ei.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (fin != null)
					fin.close();
			} catch (IOException ex) {
				
			}
		}
		propertyMap.put(propertyPath, properties);
	
		return properties;
	}

	private static String getPropertyValue(String propertyFileName,
			String propertyName) throws ICollectFatalException {
		//String propertyPath = "D:\\ICollect\\Conf\\" + propertyFileName;
		String propertyPath = "/IcollectWeb/ICollect/properties/CIB/" + propertyFileName;	
		Properties properties;
		String propertyValue = null;
		properties = (Properties) propertyMap.get(propertyName);
		if (properties == null) {
			properties = getPropertyObject(propertyPath);
		}
		if (properties != null) {
			propertyValue = properties.getProperty(propertyName);
		}
		if (propertyValue == null) {
			throw new ICollectFatalException("Property[ " + propertyName
					+ "]was not found in " + propertyFileName);
		} else {
		
		}
		return propertyValue;
	}

	public static String getProperty(String propertyFileName,
			String propertyName) throws ICollectFatalException {

		if (propertyFileName == null || propertyFileName.trim().equals("")) {
			throw new ICollectFatalException("Property File name is null");
		}
		if (propertyName == null || propertyName.trim().equals("")) {
			throw new ICollectFatalException("Property name is null");
		}
		return getPropertyValue(propertyFileName, propertyName);

	}


*/}
