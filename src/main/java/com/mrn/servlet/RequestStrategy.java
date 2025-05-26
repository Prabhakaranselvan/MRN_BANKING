package com.mrn.servlet;

import java.util.Map;

@FunctionalInterface
public interface RequestStrategy 
{
    Map<String, Object> handle(Object handler, Object body, Map<String, Object> session) throws Exception;
}