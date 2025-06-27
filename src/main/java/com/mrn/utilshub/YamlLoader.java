package com.mrn.utilshub;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.mrn.config.AuthorizationConfig;
import com.mrn.config.EndpointAuthorization;

public class YamlLoader 
{
    private static AuthorizationConfig config;

    static 
    {
        try (InputStream input = YamlLoader.class.getClassLoader().getResourceAsStream("authorization.yaml")) 
        {
            if (input == null) 
            {
                throw new RuntimeException("authorization.yaml not found in resources folder.");
            }
            LoaderOptions options = new LoaderOptions();
            options.setAllowRecursiveKeys(false);
            options.setMaxAliasesForCollections(50);
            Yaml yaml = new Yaml(new Constructor(AuthorizationConfig.class, options));
            config = yaml.load(input);
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Failed to load authorization.yaml: " + e.getMessage(), e);
        }
    }

    public static AuthorizationConfig getConfig() 
    {
        return config;
    }

    public static boolean isAllowed(String path, String headerMethod, String method, short role) 
    {
        if (config == null || config.getAuthorization() == null) 
        {
        	return false;
    	}
        boolean matchedRuleFound = false; 
        for (EndpointAuthorization rule : config.getAuthorization()) 
        {
        	 
            if ( rule.getHeader().equals(headerMethod) && rule.getMethod().equals(method) && matchesPath(path, rule.getEndpoint())) 
            {
            	matchedRuleFound = true;
            	System.out.println("[YamlLoader] Matched Rule => " +
            		    "Endpoint: " + rule.getEndpoint() +
            		    ", Header: " + rule.getHeader() +
            		    ", Method: " + rule.getMethod());
                List<Short> roleMap = rule.getRoles();
                if (roleMap.contains(role)) 
                {
                	System.out.println("[YamlLoader] Access GRANTED");
                	return true;
                }
                else
                {
                	System.out.println("[YamlLoader] Access DENIED - Allowed Roles: " + roleMap);
                }
            }
        }
        if (!matchedRuleFound) {
            System.out.println("[YamlLoader] Access DENIED - No matching rule found");
        }
        return false;
    }

    private static boolean matchesPath(String path, String rulePath) 
    {
    	Pattern pattern = Pattern.compile(rulePath);
    	System.out.println("[DEBUG] Compiled regex: " + pattern.pattern());
    	Matcher matcher = pattern.matcher(path);
    	return matcher.matches();
    }
}
