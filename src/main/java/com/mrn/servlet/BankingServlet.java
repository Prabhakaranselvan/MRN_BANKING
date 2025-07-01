package com.mrn.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mrn.exception.InvalidException;
import com.mrn.utilshub.ModuleResolver;

public class BankingServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		try
		{
			String path = request.getPathInfo();
			String[] parts = path.split("/");
			String module = parts[1];

			if ("logout".equalsIgnoreCase(module))
			{
				handleLogout(request, response);
				return;
			}

			String httpMethod = request.getMethod().toUpperCase();
			String headerMethod = request.getHeader("Method").toUpperCase();

			Object pojoInstance = null;
			Map<String, String> queryParams = new HashMap<>();
			if (!"GET".equals(httpMethod))
			{
				try (BufferedReader reader = request.getReader())
				{
					String jsonString = reader.lines().collect(Collectors.joining());
					Gson gson = new Gson();
					Class<?> pojoClass = ModuleResolver.getPojoClass(module, headerMethod, httpMethod);;
					pojoInstance = gson.fromJson(jsonString, pojoClass);
				}
				catch (JsonSyntaxException e)
				{
					Throwable cause = e.getCause();
					if (cause instanceof NumberFormatException)
					{
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						writeSafeJsonError(response, "Invalid input: numeric value expected. [Cause - " + cause.getMessage() + "] ");
					}
					else
					{
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						writeSafeJsonError(response, "Malformed JSON or invalid data type. [Cause - " + cause.getMessage() + "] ");
					}
					e.printStackTrace();
					return;
				}

			}
			else
			{
				request.getParameterMap().forEach((k, v) -> queryParams.put(k, v[0]));
			}

			Class<?> handlerClass = ModuleResolver.getHandlerClass(module);
			Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();

			Map<String, Object> resultMap = new HashMap<>();
			if ("login".equals(module))
			{
				Method handleMethod = handlerClass.getMethod("handlePost", Object.class);
				resultMap = (Map<String, Object>) handleMethod.invoke(handlerInstance, pojoInstance);
				createSession(request, response, resultMap);
			}
			else
			{
				String key = headerMethod + "|" + httpMethod;
				RequestStrategy strategy = ModuleResolver.getStrategy(key);
				Map<String, Object> sessionMap = getSessionAttributes(request);
				resultMap = strategy.handle(handlerInstance, pojoInstance, queryParams, sessionMap);
			}
			sendResponse(response, resultMap);
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof InvalidException)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writeJsonError(response, cause.getMessage());
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writeJsonError(response, "Unhandled error in method execution");
			}
			e.printStackTrace();
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeJsonError(response, "Unexpected server error");
			e.printStackTrace();
		}
	}

	private void sendResponse(HttpServletResponse response, Map<String, Object> resultMap) throws IOException
	{
		Gson gson = new Gson();
		response.setStatus(HttpServletResponse.SC_OK);

		String json = gson.toJson(resultMap);
		response.getWriter().write(json);
	}
	
	private void writeSafeJsonError(HttpServletResponse response, String errorMessage) throws IOException
	{
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("error", errorMessage);
		String json = new Gson().toJson(errorMap); // safely escapes characters like quotes
		response.getWriter().write(json);
	}

	private void writeJsonError(HttpServletResponse response, String errorMessage) throws IOException
	{
		response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
	}

	private void createSession(HttpServletRequest request, HttpServletResponse response, Map<String, Object> resultMap)
	{
		// Invalidate old session if exists
		HttpSession oldSession = request.getSession(false);
		if (oldSession != null)
		{
			oldSession.invalidate();
		}

		// Create a new session
		HttpSession newSession = request.getSession(true);

		// Define keys you want to set as session attributes
		List<String> sessionKeys = new ArrayList<>(
				Arrays.asList("userId", "userCategory", "branchId", "name", "email"));

		// Loop through and set attributes
		for (String key : sessionKeys)
		{
			Object value = resultMap.get(key);
			if (value != null)
			{
				newSession.setAttribute(key, value);
			}
		}
		newSession.setMaxInactiveInterval(30 * 60); // 30 minutes timeout
	}

	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			session.invalidate();
		}

		// Invalidate the JSESSIONID cookie
		Cookie cookie = new Cookie("JSESSIONID", "");
		cookie.setMaxAge(0); // Expire immediately
		cookie.setPath(request.getContextPath()); // Must match the cookie path
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		response.addCookie(cookie);

		// Send JSON response
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("{\"message\": \"Logged out successfully\"}");
	}

	public static Map<String, Object> getSessionAttributes(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false); // don't create if it doesn't exist
		Map<String, Object> sessionMap = new HashMap<>();

		if (session != null)
		{
			Enumeration<String> attributeNames = session.getAttributeNames();
			while (attributeNames.hasMoreElements())
			{
				String name = attributeNames.nextElement();
				Object value = session.getAttribute(name);
				sessionMap.put(name, value);
			}
		}
		return sessionMap;
	}

}
