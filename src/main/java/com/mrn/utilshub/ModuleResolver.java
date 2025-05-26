package com.mrn.utilshub;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.mrn.servlet.RequestStrategy;

@SuppressWarnings("unchecked")
public class ModuleResolver {
	private static final Map<String, Class<?>> POJO_MAP = new HashMap<>();
	private static final Map<String, Class<?>> HANDLER_MAP = new HashMap<>();
	private static final Map<String, String> METHOD_MAP = new HashMap<>();
	private static final Map<String, RequestStrategy> STRATEGIES = new HashMap<>();

	static {
		// Populate POJO class mappings
		POJO_MAP.put("login", com.mrn.pojos.Login.class);
		POJO_MAP.put("signup", com.mrn.pojos.Client.class);
		POJO_MAP.put("client", com.mrn.pojos.Client.class);
		POJO_MAP.put("profile", com.mrn.pojos.UserWrapper.class);
		POJO_MAP.put("branch", com.mrn.pojos.Branch.class);
		POJO_MAP.put("accounts", com.mrn.pojos.Accounts.class);
		POJO_MAP.put("accountrequest", com.mrn.pojos.AccountRequest.class);
		POJO_MAP.put("accountapproval", com.mrn.pojos.AccountRequest.class);
		POJO_MAP.put("transaction", com.mrn.pojos.Transaction.class);

		// Populate Handler class mappings
		HANDLER_MAP.put("login", com.mrn.handlers.LoginHandler.class);
		HANDLER_MAP.put("signup", com.mrn.handlers.ClientHandler.class);
		HANDLER_MAP.put("client", com.mrn.handlers.ClientHandler.class);
		HANDLER_MAP.put("profile", com.mrn.handlers.UserHandler.class);
		HANDLER_MAP.put("branch", com.mrn.handlers.BranchHandler.class);
		HANDLER_MAP.put("accounts", com.mrn.handlers.AccountsHandler.class);
		HANDLER_MAP.put("accountrequest", com.mrn.handlers.AccountRequestHandler.class);
		HANDLER_MAP.put("accountapproval", com.mrn.handlers.AccountApprovalHandler.class);
		HANDLER_MAP.put("transaction", com.mrn.handlers.TransactionHandler.class);

		// Populate Method name mappings
		METHOD_MAP.put("POST", "handlePost");
		METHOD_MAP.put("GET", "handleGet");
		METHOD_MAP.put("PUT", "handlePut");

		// header method = GET, http method = GET
		STRATEGIES.put("GET|GET", (handler, body, session) -> {
			Method method = handler.getClass().getMethod("handleGet", Map.class);
			return (Map<String, Object>) method.invoke(handler, session);
		});

		// header method = GET, http method = POST
		STRATEGIES.put("GET|POST", (handler, body, session) -> {
			Method method = handler.getClass().getMethod("handleGet", Object.class, Map.class);
			return (Map<String, Object>) method.invoke(handler, body, session);
		});

		// header method = POST, http method = POST
		STRATEGIES.put("POST|POST", (handler, body, session) -> {
			Method method = handler.getClass().getMethod("handlePost", Object.class, Map.class);
			return (Map<String, Object>) method.invoke(handler, body, session);
		});

		// header method = PUT, http method = POST
		STRATEGIES.put("PUT|POST", (handler, body, session) -> {
			Method method = handler.getClass().getMethod("handlePut", Object.class, Map.class);
			return (Map<String, Object>) method.invoke(handler, body, session);
		});
	}

	public static Class<?> getPojoClass(String module) {
		return POJO_MAP.get(module);
	}

	public static Class<?> getHandlerClass(String module) {
		return HANDLER_MAP.get(module);
	}

	public static String getMethodName(String httpMethod) {
		return METHOD_MAP.get(httpMethod);
	}

	public static RequestStrategy getStrategy(String key) {
		return STRATEGIES.get(key);
	}

}
