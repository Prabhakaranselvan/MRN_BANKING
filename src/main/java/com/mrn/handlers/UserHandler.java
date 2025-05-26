package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.mrn.dao.ClientDAO;
import com.mrn.dao.EmployeeDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.Employee;
import com.mrn.pojos.User;
import com.mrn.pojos.UserWrapper;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Validator;

public class UserHandler 
{	
	

    public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
    {
        try 
        {
            UserWrapper wrapper = (UserWrapper) pojoInstance;
            User user = wrapper.getUser();
            
            // Check for validation errors
            StringBuilder validationErrors = Validator.checkUserWrapper(wrapper);
            if (validationErrors.length() > 0) 
            {
            	throw new InvalidException(validationErrors.toString());
            }
            
            // Extract modifier & target user category
            UserCategory modifierCategory = UserCategory.fromValue((short) attributeMap.get("userCategory"));
            UserCategory targetCategory = UserCategory.fromValue(user.getUserCategory());
            
            // Permission check: performer must be of higher category
            if (modifierCategory.ordinal() <= targetCategory.ordinal()) 
            {
                throw new InvalidException("You don't have permission to create the user category: " + targetCategory);
            }
            
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
            
            short active = (short) Status.ACTIVE.getValue();
            user.setStatus(active);
            
            long modifierId = (long) attributeMap.get("userId");
            user.setModifiedBy(modifierId);

            UserDAO userDAO = new UserDAO();
            long userId = userDAO.addUser(user);
            
            boolean success;
            switch (targetCategory) 
            {
                case CLIENT:
                    Client client = wrapper.getClient();                  
                    client.setClientId(userId);
                    ClientDAO clientDAO = new ClientDAO();
                    success = clientDAO.addClient(client);
                    break;

                case EMPLOYEE:
                case MANAGER:
                    Employee employee = wrapper.getEmployee();
                    employee.setEmployeeId(userId);
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    success = employeeDAO.addEmployee(employee);
                    break;

                default:
                    throw new InvalidException("Invalid User Category found");
            }

            if (success) 
            {
                ConnectionManager.commit();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "User Added Successfully");
                responseMap.put("userId", userId);
                return responseMap;
            } 
            else 
            {
                ConnectionManager.rollback();
                throw new InvalidException("User Addition Failed");
            }
        } 
        catch (InvalidException e) 
        {
            ConnectionManager.rollback();
            throw e;
        } 
        catch (Exception e) 
        {
            ConnectionManager.rollback();
            throw new InvalidException("User Addition failed due to an unexpected error", e);
        } 
        finally 
        {
            ConnectionManager.close();
        }
    }
	
}
