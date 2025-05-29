package com.mrn.config;

import java.util.List;

public class EndpointAuthorization {
	private String endpoint;
	private String header;
	private String method;
	private List<Short> roles;

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

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public List<Short> getRoles() {
		return roles;
	}

	public void setRoles(List<Short> roles) {
		this.roles = roles;
	}

}
