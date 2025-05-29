package com.mrn.utilshub;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

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
        for (EndpointAuthorization rule : config.getAuthorization()) 
        {
            if ( rule.getHeader().equals(headerMethod) && rule.getMethod().equals(method) && matchesPath(path, rule.getEndpoint())) 
            {
                List<Short> roleMap = rule.getRoles();
                if (roleMap.contains(role)) 
                {
                	return true;
                }
            }
        }
        return false;
    }

    private static boolean matchesPath(String path, String rulePath) 
    {
    	Pattern pattern = Pattern.compile(rulePath);
    	Matcher matcher = pattern.matcher(path);
    	return matcher.matches();
    }
    
    public static List<String> loadEndpoints() 
    {
        return config.getAuthorization().stream()
                .map(EndpointAuthorization::getEndpoint)
                .distinct()
                .collect(Collectors.toList());
    }

}
