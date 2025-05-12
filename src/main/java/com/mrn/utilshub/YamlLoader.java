package com.mrn.utilshub;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import com.mrn.config.AuthRule;
import com.mrn.config.AuthorizationConfig;

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

    public static AuthorizationConfig getConfig() {
        return config;
    }

    public static boolean isAllowed(String path, String method, String role, String tag) 
    {
        if (config == null || config.getAuthorization() == null) return false;

        for (AuthRule rule : config.getAuthorization()) 
        {
            if (rule.getMethod().equals(method) && matchesPath(path, rule.getEndpoint())) 
            {
                Map<String, List<String>> roleMap = rule.getRoles();
                if (roleMap.containsKey(role)) 
                {
                    for (String permission : roleMap.get(role)) 
                    {
                        if (tag == null || permission.contains(tag)) 
                        {
                            return true;
                        }
                    }
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
// Regex support — supports hardcoded regex like /user/\\d+ or dynamic path variable pattern /user/{user_id}
//        String regex = rulePath
//                .replaceAll("\\{[^/]+\\}", "\\\\d+")
//                .replaceAll("/", "\\\\/");
//        Pattern pattern = Pattern.compile("^" + regex + "$");
//        return pattern.matcher(path).matches();

}
