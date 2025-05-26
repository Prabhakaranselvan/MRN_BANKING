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
import com.mrn.exception.InvalidException;
import com.mrn.utilshub.ModuleResolver;

public class BankingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String path = request.getPathInfo();
			String[] parts = path.split("/");
			String module = parts[1];

			if ("logout".equalsIgnoreCase(module)) {
				handleLogout(request, response);
				return;
			}

			String httpMethod = request.getMethod().toUpperCase();

			Object pojoInstance = null;
			if (!"GET".equals(httpMethod)) {
				try (BufferedReader reader = request.getReader()) {
					String jsonString = reader.lines().collect(Collectors.joining());
					Gson gson = new Gson();
					Class<?> pojoClass = ModuleResolver.getPojoClass(module);
					pojoInstance = gson.fromJson(jsonString, pojoClass);
				}
			}

			Class<?> handlerClass = ModuleResolver.getHandlerClass(module);
			Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();

			Map<String, Object> resultMap = new HashMap<>();
			if ("login".equals(module)) {
				Method handleMethod = handlerClass.getMethod("handlePost", Object.class);
				resultMap = (Map<String, Object>) handleMethod.invoke(handlerInstance, pojoInstance);
				createSession(request, response, resultMap);
			} else {
				String headerMethod = request.getHeader("Method");
				if (headerMethod == null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					writeJsonError(response, "Missing 'Method' header.");
					return;
				}
				String key = headerMethod + "|" + httpMethod;
				RequestStrategy strategy = ModuleResolver.getStrategy(key);
				Map<String, Object> attributeMap = getSessionAttributes(request);
				resultMap = strategy.handle(handlerInstance, pojoInstance, attributeMap);
			}
			sendResponse(response, resultMap);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof InvalidException) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writeJsonError(response, cause.getMessage());
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writeJsonError(response, "Unhandled error in method execution");
			}
			e.printStackTrace();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeJsonError(response, "Unexpected server error");
			e.printStackTrace();
		}
	}

	private void sendResponse(HttpServletResponse response, Map<String, Object> resultMap) throws IOException {
		Gson gson = new Gson();
		response.setStatus(HttpServletResponse.SC_OK);

		String json = gson.toJson(resultMap);
		response.getWriter().write(json);
	}

	private void writeJsonError(HttpServletResponse response, String errorMessage) throws IOException {
		response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
	}

	private void createSession(HttpServletRequest request, HttpServletResponse response,Map<String, Object> resultMap) {
		HttpSession session = request.getSession(); // creates session if not exists

		// Define keys you want to set as session attributes
		List<String> sessionKeys = new ArrayList<>(Arrays.asList("userId", "userCategory", "branchId"));

		// Loop through and set attributes
		for (String key : sessionKeys) {
			Object value = resultMap.get(key);
			if (value != null) {
				session.setAttribute(key, value);
			}
		}
		session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout
	}

	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
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

	public static Map<String, Object> getSessionAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false); // don't create if it doesn't exist
		Map<String, Object> attributeMap = new HashMap<>();

		if (session != null) {
			Enumeration<String> attributeNames = session.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				String name = attributeNames.nextElement();
				Object value = session.getAttribute(name);
				attributeMap.put(name, value);
			}
		}
		return attributeMap;
	}

}
