package com.mrn.utilshub;

import java.util.HashMap;
import java.util.Map;

public class ModuleResolver {
	private static final Map<String, Class<?>> POJO_MAP = new HashMap<>();
	private static final Map<String, Class<?>> HANDLER_MAP = new HashMap<>();

	static {
		// Populate POJO class mappings
		POJO_MAP.put("user", com.mrn.pojos.UserWrapper.class);
		POJO_MAP.put("branch", com.mrn.pojos.Branch.class);
		POJO_MAP.put("login", com.mrn.pojos.Login.class);

		// Populate Handler class mappings
		HANDLER_MAP.put("user", com.mrn.handlers.UserHandler.class);
		HANDLER_MAP.put("branch", com.mrn.handlers.BranchHandler.class);
		HANDLER_MAP.put("login", com.mrn.handlers.LoginHandler.class);
	}

	public static Class<?> getPojoClass(String module) {
		return POJO_MAP.get(module);
	}

	public static Class<?> getHandlerClass(String module) {
		return HANDLER_MAP.get(module);
	}

}
