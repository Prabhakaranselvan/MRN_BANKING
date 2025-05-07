package com.mrn.utilshub;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mrn.pojos.Client;
import com.mrn.pojos.Login;
import com.mrn.pojos.User;
import com.mrn.pojos.UserWrapper;

public class Validator 
{

    private static final Map<String, String> validationPatterns = new HashMap<>();
    private static StringBuilder errorMsg = new StringBuilder();

    static {
    	validationPatterns.put("UserCategory", "^(Manager|Employee|Client)$");
        validationPatterns.put("Name", "^[A-Za-z]+(?:[-' ][A-Za-z]+)*$");
        validationPatterns.put("Gender", "^(Male|Female|Other)$");
        validationPatterns.put("Email Address", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        validationPatterns.put("Phone Number", "^\\d{10}$");
        validationPatterns.put("Aadhar Number", "^\\d{12}$");
        validationPatterns.put("PAN", "^[A-Z]{5}\\d{4}[A-Z]$");
        validationPatterns.put("Pincode", "^\\d{6}$");
        validationPatterns.put("Password", "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,20}$");
    }

    private static void checkNull(String field, String fieldName) 
    {
        if (field == null) 
        {
            errorMsg.append(fieldName).append(" is required and cannot be left blank.<br/>");
        }
    }

    private static void checkEmpty(String field, String fieldName) 
    {
        checkNull(field, fieldName);
        if (field.trim().isEmpty()) 
        {
            errorMsg.append(fieldName).append(" cannot be empty. Please provide a value<br/>");
        }
    }

    private static void checkField(String field, String fieldName) 
    {
        checkEmpty(field, fieldName);
        Pattern pattern = Pattern.compile(validationPatterns.get(fieldName));
        Matcher matcher = pattern.matcher(field);
        if (!matcher.matches()) 
        {
            errorMsg.append(fieldName).append(" is invalid. Please follow the correct format.<br/>");
        }
    }
    
    public static StringBuilder checkUserWrapper(UserWrapper wrapper) 
    {
        errorMsg.setLength(0);
        User user = wrapper.getUser();
        String category = user.getUserCategory();
        
        checkField(category, "UserCategory");
        checkField(user.getName(), "Name");
        checkField(user.getGender(), "Gender");
        checkField(user.getEmail(), "Email Address");
        checkField(user.getPhoneNo(), "Phone Number");
        checkField(user.getPassword(), "Password");
        
        if ("Client".equals(category))
        {
        	Client client = wrapper.getClient();
        	checkEmpty(client.getdob(), "Date of Birth");
            checkField(client.getAadhar(), "Aadhar Number");
            checkField(client.getPan(), "PAN");
            checkEmpty(client.getAddress(), "Address");
        }
        
        return errorMsg;
    }
    
    public static StringBuilder checkLoginCredentials(Login credentials) 
    {
        errorMsg.setLength(0);
        
        checkField(credentials.getEmail(), "Email Address");
        checkField(credentials.getPassword(), "Password");
        
        return errorMsg;
    }
}

