package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.mrn.dao.ClientDAO;
import com.mrn.dao.EmployeeDAO;
import com.mrn.dao.UserDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.Employee;
import com.mrn.pojos.User;
import com.mrn.pojos.UserWrapper;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Validator;

public class UserHandler extends Handler {	

    @Override
    protected Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
    {
        try 
        {
            UserWrapper wrapper = (UserWrapper) pojoInstance;
            User user = wrapper.getUser();
            Map<String, Object> responseMap = new HashMap<>();
            
// Check for validation errors
            StringBuilder validationErrors = Validator.checkUserWrapper(wrapper);
            if (validationErrors.length() > 0) 
            {
            	throw new InvalidException(validationErrors.toString());
            }
            
 // Extract modifier (performer) details
            long modifierId = (long) attributeMap.get("userId");
            int modifierCategoryValue = (int) attributeMap.get("userCategory");
            UserCategory performerCategory = UserCategory.fromValue(modifierCategoryValue);

            // Get target user category from the incoming user object
            int targetCategory = user.getUserCategory().toUpperCase());

            // Permission check: performer must be of higher category
            if (performerCategory.getValue() <= targetCategory.getValue()) {
                throw new InvalidException("You don't have permission to create this user category.");
            }
            String modifierCategory = (String) attributeMap.get("userCategory");
            String category = user.getUserCategory();
            
            
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
            user.setStatus("Active");
            long modifierId = (long) attributeMap.get("userId");
            user.setModifiedBy(modifierId);

            UserDAO userDAO = new UserDAO();
            long userId = userDAO.addUser(user);
            
            boolean success;

            switch (category) 
            {
                case "Client":
                    Client client = wrapper.getClient();                  
                    client.setClientId(userId);
                    ClientDAO clientDAO = new ClientDAO();
                    success = clientDAO.addClient(client);
                    break;

                case "Employee":
                case "Manager":
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
                responseMap.put("success", true);
                responseMap.put("message", "User Added Successfully");
                responseMap.put("userId", userId);
            } 
            else 
            {
                ConnectionManager.rollback();
                responseMap.put("success", false);
                responseMap.put("message", "User Addition Failed");
            }
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
            throw new InvalidException("User Addition failed due to an unexpected error", e);
        } 
        finally 
        {
            ConnectionManager.close();
        }
    }

	@Override
	protected Map<String, Object> handleGet(Object pojoInstance) throws InvalidException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> handlePut(Object pojoInstance) throws InvalidException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
