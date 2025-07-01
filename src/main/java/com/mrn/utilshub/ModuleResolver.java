package com.mrn.utilshub;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.mrn.servlet.RequestStrategy;

@SuppressWarnings("unchecked")
public class ModuleResolver
{
	private static final Map<String, Class<?>> POJO_MAP = new HashMap<>();
	private static final Map<String, Class<?>> HANDLER_MAP = new HashMap<>();
	private static final Map<String, RequestStrategy> STRATEGIES = new HashMap<>();

	static
	{
		// Populate POJO class mappings
		POJO_MAP.put("login", com.mrn.pojos.Login.class);
		POJO_MAP.put("signup", com.mrn.pojos.WrapperClientAccount.class);
		POJO_MAP.put("client", com.mrn.pojos.Client.class);
		POJO_MAP.put("employee", com.mrn.pojos.Employee.class);
		POJO_MAP.put("branch", com.mrn.pojos.Branch.class);
		POJO_MAP.put("accounts", com.mrn.pojos.Accounts.class);
		POJO_MAP.put("accountrequest", com.mrn.pojos.AccountRequest.class);
		POJO_MAP.put("accountapproval", com.mrn.pojos.AccountRequest.class);
		POJO_MAP.put("transaction", com.mrn.pojos.Transaction.class);
		POJO_MAP.put("accountstatement", com.mrn.pojos.AccountStatement.class);
		POJO_MAP.put("report", com.mrn.pojos.AccountsReport.class);

		// Populate Handler class mappings
		HANDLER_MAP.put("login", com.mrn.handlers.LoginHandler.class);
		HANDLER_MAP.put("signup", com.mrn.handlers.ClientHandler.class);
		HANDLER_MAP.put("client", com.mrn.handlers.ClientHandler.class);
		HANDLER_MAP.put("employee", com.mrn.handlers.EmployeeHandler.class);
		HANDLER_MAP.put("branch", com.mrn.handlers.BranchHandler.class);
		HANDLER_MAP.put("accounts", com.mrn.handlers.AccountsHandler.class);
		HANDLER_MAP.put("accountrequest", com.mrn.handlers.AccountRequestHandler.class);
		HANDLER_MAP.put("accountapproval", com.mrn.handlers.AccountApprovalHandler.class);
		HANDLER_MAP.put("transaction", com.mrn.handlers.TransactionHandler.class);
		HANDLER_MAP.put("accountstatement", com.mrn.handlers.TransactionHandler.class);
		HANDLER_MAP.put("report", com.mrn.handlers.ReportHandler.class);

		// header method = GET, http method = GET
		STRATEGIES.put("GET|GET", (handler, body, queryParams, session) ->
		{
			if (queryParams.isEmpty())
			{
				Method method = handler.getClass().getMethod("handleGet", Map.class);
				return (Map<String, Object>) method.invoke(handler, session);
			}
			else
			{
				Method method = handler.getClass().getMethod("handleGet", Map.class, Map.class);
				return (Map<String, Object>) method.invoke(handler, queryParams, session);
			}
		});

		// header method = GET, http method = POST
		STRATEGIES.put("GET|POST", (handler, body, queryParams, session) ->
		{
			Method method = handler.getClass().getMethod("handleGet", Object.class, Map.class);
			return (Map<String, Object>) method.invoke(handler, body, session);
		});

		// header method = POST, http method = POST
		STRATEGIES.put("POST|POST", (handler, body, queryParams, session) ->
		{
			Method method = handler.getClass().getMethod("handlePost", Object.class, Map.class);
			return (Map<String, Object>) method.invoke(handler, body, session);
		});

		// header method = PUT, http method = POST
		STRATEGIES.put("PUT|POST", (handler, body, queryParams, session) ->
		{
			Method method = handler.getClass().getMethod("handlePut", Object.class, Map.class);
			return (Map<String, Object>) method.invoke(handler, body, session);
		});
	}

	public static Class<?> getPojoClass(String module, String headerMethod, String httpMethod)
	{
		if ("client".equals(module) && "POST".equals(headerMethod) && "POST".equals(httpMethod))
		{
			return com.mrn.pojos.WrapperClientAccount.class; // only for Add Client
		}

		return POJO_MAP.get(module); // fallback for everything else
	}

	public static Class<?> getHandlerClass(String module)
	{
		return HANDLER_MAP.get(module);
	}

	public static RequestStrategy getStrategy(String key)
	{
		return STRATEGIES.get(key);
	}

}
