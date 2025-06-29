package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.AuthenticationDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Login;
import com.mrn.pojos.User;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class LoginHandler
{
	private final AuthenticationDAO auth = new AuthenticationDAO();
	
	public Map<String, Object> handlePost(Object pojoInstance) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Login credentials = (Login) pojoInstance;
			Utility.checkError(Validator.checkLoginCredentials(credentials));

			String email = credentials.getEmail();
			String phoneNo = credentials.getPhoneNo();
			String password = credentials.getPassword();
			String storedPassword = auth.getPasswordByEmailOrPhone(email, phoneNo);
			
			Utility.validatePassword(password, storedPassword);

			User user = auth.getUserByEmailOrPhone(email, phoneNo);
			if (user.getStatus() != Status.ACTIVE.getValue())
			{
				throw new InvalidException("User profile is not active");
			}

			Long userId = user.getUserId();
			Short userCategory = user.getUserCategory();

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("message", "Login successful");
			responseMap.put("userId", userId);
			responseMap.put("userCategory", userCategory);
			responseMap.put("email", user.getEmail());
			responseMap.put("name", user.getName());

			UserCategory category = UserCategory.fromValue(userCategory);
			if (category == UserCategory.EMPLOYEE || category == UserCategory.MANAGER)
			{
				Long branchId = auth.getBranchID(userId);
				responseMap.put("branchId", branchId);
			}
			return responseMap;
		});
	}
}
