package com.mrn.config;

import java.util.List;

public class AuthorizationConfig 
{
    private List<EndpointAuthorization> authorization;

    public List<EndpointAuthorization> getAuthorization() 
    {
        return authorization;
    }

    public void setAuthorization(List<EndpointAuthorization> authorization) 
    {
        this.authorization = authorization;
    }
}
