package com.mrn.config;

import java.util.List;
import java.util.Map;

public class AuthRule 
{
	private String endpoint;
	private String method;
	private String tag;
	private Map<String, List<String>> roles;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Map<String, List<String>> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, List<String>> roles) {
		this.roles = roles;
	}

}
