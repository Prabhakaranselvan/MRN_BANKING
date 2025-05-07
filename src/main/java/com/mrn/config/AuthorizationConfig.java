package com.mrn.config;

import java.util.List;

public class AuthorizationConfig 
{
    private List<AuthRule> authorization;

    public List<AuthRule> getAuthorization() 
    {
        return authorization;
    }

    public void setAuthorization(List<AuthRule> authorization) 
    {
        this.authorization = authorization;
    }
}
