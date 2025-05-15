package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.mrn.dao.AuthenticationDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Login;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Validator;

public class LoginHandler {

    public Map<String, Object> handlePost(Object pojoInstance) throws InvalidException 
    {
        try {
            Login credentials = (Login) pojoInstance;
            
            // Check for validation errors
            StringBuilder validationErrors = Validator.checkLoginCredentials(credentials);
            if (validationErrors.length() > 0) 
            {
            	throw new InvalidException(validationErrors.toString());
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
            	if (user.getStatus() != Status.ACTIVE.getValue()) 
                {
            		throw new InvalidException("User account is not active");
                }
            }
            
            long userId = user.getUserId();
        	short userCategory = user.getUserCategory();
        	
        	Map<String, Object> responseMap = new HashMap<>();
        	responseMap.put("message", "Login successful");
            responseMap.put("userId", userId);
            responseMap.put("userCategory", userCategory);
           
            UserCategory category = UserCategory.fromValue(userCategory);
            if (category == UserCategory.EMPLOYEE || category == UserCategory.MANAGER)
        	{
        		long branchId = auth.getBranchID(userId);
        		responseMap.put("branchId", branchId);
        	}
            ConnectionManager.commit();
            return responseMap;
        } 
        catch (InvalidException e) 
        {
        	ConnectionManager.rollback();
            throw e;
        } 
        catch (Exception e) 
        {
        	ConnectionManager.rollback();
            throw new InvalidException("Login failed due to an unexpected error", e);
        }
        finally 
        {
            ConnectionManager.close();
        }
        
        
    }
}
