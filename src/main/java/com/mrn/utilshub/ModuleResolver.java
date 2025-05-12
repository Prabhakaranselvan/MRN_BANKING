package com.mrn.utilshub;

import java.util.HashMap;
import java.util.Map;

public class ModuleResolver 
{
	private static final Map<String, Class<?>> POJO_MAP = new HashMap<>();
	private static final Map<String, Class<?>> HANDLER_MAP = new HashMap<>();
	private static final Map<String, String> METHOD_MAP = new HashMap<>();

	static 
	{
		// Populate POJO class mappings
		POJO_MAP.put("login", com.mrn.pojos.Login.class);
		POJO_MAP.put("signup", com.mrn.pojos.UserWrapper.class);
		POJO_MAP.put("profile", com.mrn.pojos.UserWrapper.class);
		POJO_MAP.put("branch", com.mrn.pojos.Branch.class);
		

		// Populate Handler class mappings
		HANDLER_MAP.put("login", com.mrn.handlers.LoginHandler.class);
		HANDLER_MAP.put("signup", com.mrn.handlers.UserHandler.class);
		HANDLER_MAP.put("profile", com.mrn.handlers.UserHandler.class);
		HANDLER_MAP.put("branch", com.mrn.handlers.BranchHandler.class);
		
		
		// Populate Method name mappings
		METHOD_MAP.put("POST", "handlePost");
		METHOD_MAP.put("GET", "handleGet");
		METHOD_MAP.put("PUT", "handlePut");	
	}

	public static Class<?> getPojoClass(String module) 
	{
		return POJO_MAP.get(module);
	}

	public static Class<?> getHandlerClass(String module) 
	{
		return HANDLER_MAP.get(module);
	}
	
	public static String getMethodName(String httpMethod) 
	{
		return METHOD_MAP.get(httpMethod);
	}

}
