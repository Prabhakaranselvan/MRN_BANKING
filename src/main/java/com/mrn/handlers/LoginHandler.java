package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.mrn.dao.AuthenticationDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Login;
import com.mrn.pojos.User;
import com.mrn.utilshub.Validator;

public class LoginHandler extends Handler {

    @Override
    protected Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
    {
        Map<String, Object> responseMap = new HashMap<>();
        
        try {
            Login credentials = (Login) pojoInstance;
            StringBuilder validationErrors = Validator.checkLoginCredentials(credentials);
            
            // Check for validation errors
            if (validationErrors.length() > 0) 
            {
                responseMap.put("success", false);
                responseMap.put("message", validationErrors.toString());
                return responseMap;
            }
            
            String email = credentials.getEmail();
            String phoneNo = credentials.getPhoneNo();
            String password = credentials.getPassword();
            
            AuthenticationDAO auth = new AuthenticationDAO();
            String storedPassword = auth.getPasswordByEmailOrPhone(email, phoneNo);
            
            User user;
            
            if (!BCrypt.checkpw(password, storedPassword))
            {
            	throw new InvalidException("Incorrect Password");
            }
            else
            {
            	user = auth.getUserByEmailOrPhone(email, phoneNo);
            	if (!"Active".equals(user.getStatus())) 
                {
            		throw new InvalidException("User account is not active");
                }
            }
            
            long userId = user.getUserId();
        	String userCategory = user.getUserCategory();
        	
        	responseMap.put("message", "Login successful");
            responseMap.put("userId", user.getUserId());
            responseMap.put("userCategory", user.getUserCategory());
            
        	if ("Employee".equals(userCategory) || "Manager".equals(userCategory))
        	{
        		long branchId = auth.getBranchID(userId);
        		responseMap.put("branchId", branchId);
        	}
        } 
        catch (InvalidException e) 
        {
            throw e;
        } 
        catch (Exception e) 
        {
            throw new InvalidException("Login failed due to an unexpected error", e);
        }
        
        return responseMap;
    }

    @Override
    protected Map<String, Object> handleGet(Object pojoInstance) throws InvalidException {
        // Not required for login
        return null;
    }

    @Override
    protected Map<String, Object> handlePut(Object pojoInstance) throws InvalidException {
        // Not required for login
        return null;
    }
}
