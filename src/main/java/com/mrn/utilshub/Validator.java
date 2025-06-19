package com.mrn.utilshub;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mrn.enums.AccountType;
import com.mrn.enums.Status;
import com.mrn.enums.TxnType;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.pojos.Branch;
import com.mrn.pojos.Client;
import com.mrn.pojos.Employee;
import com.mrn.pojos.Login;
import com.mrn.pojos.Transaction;
import com.mrn.pojos.User;

public class Validator
{

	private static final Map<String, String> validationPatterns = new HashMap<>();
	private static StringBuilder errorMsg = new StringBuilder();

	static
	{
		validationPatterns.put("Name", "^[A-Za-z]+(?:[-' ][A-Za-z]+)*$");
		validationPatterns.put("Gender", "^(Male|Female|Other)$");
		validationPatterns.put("Email Address", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		validationPatterns.put("Phone Number", "^\\d{10}$");
		validationPatterns.put("Password", "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,20}$");
		validationPatterns.put("Aadhar Number", "^\\d{12}$");
		validationPatterns.put("PAN", "^[A-Z]{5}\\d{4}[A-Z]$");
		validationPatterns.put("Branch Name", "^[A-Za-z0-9 .'-]{3,50}$");
		validationPatterns.put("Branch Location", "^[A-Za-z .'-]{3,50}$");
	}

	public static StringBuilder checkLoginCredentials(Login credentials)
	{
		errorMsg.setLength(0);

		checkField(credentials.getEmail(), "Email Address");
		checkField(credentials.getPassword(), "Password");

		return errorMsg;
	}

	public static StringBuilder checkUser(User user)
	{
		errorMsg.setLength(0);

		try
		{
			UserCategory.fromValue(user.getUserCategory());
		}
		catch (InvalidException e)
		{
			errorMsg.append(e.getMessage());
		}
		checkField(user.getName(), "Name");
		checkField(user.getGender(), "Gender");
		checkField(user.getEmail(), "Email Address");
		checkField(user.getPhoneNo(), "Phone Number");
		checkField(user.getPassword(), "Password");
		if (user instanceof Client)
		{
			Client client = (Client) user;
			checkEmpty(client.getDob(), "Date of Birth");
			checkField(client.getAadhar(), "Aadhar Number");
			checkField(client.getPan(), "PAN");
			checkEmpty(client.getAddress(), "Address");
		}
		else if (user instanceof Employee)
		{
			Employee employee = (Employee) user;
			checkLongField(employee.getBranchId(), "BranchID");
		}
		return errorMsg;
	}

	public static StringBuilder checkSelfUpdate(User user)
	{
		errorMsg.setLength(0);

		checkField(user.getEmail(), "Email Address");
		checkField(user.getPhoneNo(), "Phone Number");
		checkField(user.getPassword(), "Password");
		if (user instanceof Client)
		{
			Client client = (Client) user;
			checkEmpty(client.getAddress(), "Address");
		}
		return errorMsg;
	}

	public static StringBuilder checkBranch(Branch branch)
	{
		errorMsg.setLength(0);

		checkField(branch.getBranchName(), "Branch Name");
		checkField(branch.getBranchLocation(), "Branch Location");
		checkField(branch.getContactNo(), "Phone Number");
		return errorMsg;
	}

	public static StringBuilder checkAccountCreation(Accounts account)
	{
		errorMsg.setLength(0);

		checkLongField(account.getBranchId(), "Branch ID");
		checkLongField(account.getClientId(), "Client ID");
		validateEnum(() -> AccountType.fromValue(account.getAccountType()));
		checkDecimalField(account.getBalance(), "Balance");
		return errorMsg;
	}

	public static StringBuilder checkAccountUpdate(Accounts account)
	{
		errorMsg.setLength(0);

		validateEnum(() -> AccountType.fromValue(account.getAccountType()));
		validateEnum(() -> Status.fromValue(account.getStatus()));
		return errorMsg;
	}

	public static StringBuilder checkTransaction(Transaction txn)
	{
		errorMsg.setLength(0);

		checkLongField(txn.getAccountNo(), "Account Number");
		checkDecimalField(txn.getAmount(), "Transaction Amount");
		validateEnum(() -> TxnType.fromValue(txn.getTxnType()));
		if (txn.getTxnType() == TxnType.DEBIT.getValue())
		{
			checkLongField(txn.getPeerAccNo(), "Peer Account Number");
		}
		return errorMsg;
	}

	// Helper method that appends the exception message if validation fails
	private static void validateEnum(EnumValidation enumCheck)
	{
		try
		{
			enumCheck.run();
		}
		catch (InvalidException e)
		{
			errorMsg.append(e.getMessage());
		}
	}

	private static boolean checkNull(Object field, String fieldName)
	{
		if (field == null)
		{
			errorMsg.append(fieldName).append(" is required and cannot be left blank.<br/>");
			return true;
		}
		return false;
	}

	private static boolean checkEmpty(String field, String fieldName)
	{
		 if (checkNull(field, fieldName))
		    {
		        return true; // Field is null, so treat as invalid and return
		    }

		    if (field.trim().isEmpty())
		    {
		        errorMsg.append(fieldName).append(" cannot be empty. Please provide a value<br/>");
		        return true;
		    }

		    return false;
	}

	private static void checkField(String field, String fieldName)
	{
		if (!checkEmpty(field, fieldName))
		{
			Pattern pattern = Pattern.compile(validationPatterns.get(fieldName));
			Matcher matcher = pattern.matcher(field);
			if (!matcher.matches())
			{
				errorMsg.append(fieldName).append(" is invalid. Please follow the correct format.<br/>");
			}
		}
	}

	private static void checkLongField(Long value, String fieldName)
	{
		if (!checkNull(value, fieldName) && value < 1)
		{
			errorMsg.append(fieldName).append(" cannot be Zero or Negative.<br/>");
		}
	}

	private static void checkDecimalField(BigDecimal value, String fieldName)
	{
		if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
		{
			errorMsg.append(fieldName).append(" cannot be null or negative.<br/>");
		}
	}

//	private static void checkUserCategory(short category, String fieldName)
//	{
//		if (category < 1 || category > 2)
//		{
//			errorMsg.append(fieldName).append(" should be either 1, or 2.<br/>");
//		}
//	}

//  public static StringBuilder checkClient(Client client) 
//  {
//      errorMsg.setLength(0);
//      
//      checkField(client.getName(), "Name");
//      checkField(client.getGender(), "Gender");
//      checkField(client.getEmail(), "Email Address");
//      checkField(client.getPhoneNo(), "Phone Number");
//      checkField(client.getPassword(), "Password");
//  	checkEmpty(client.getDob(), "Date of Birth");
//      checkField(client.getAadhar(), "Aadhar Number");
//      checkField(client.getPan(), "PAN");
//      checkEmpty(client.getAddress(), "Address");
//
//      return errorMsg;
//  }
//  
//  
//  public static StringBuilder checkEmployee(Employee employee) 
//  {
//      errorMsg.setLength(0);
//      Short category = employee.getUserCategory();
//      
//      checkUserCategory(category, "UserCategory");
//      checkField(employee.getName(), "Name");
//      checkField(employee.getGender(), "Gender");
//      checkField(employee.getEmail(), "Email Address");
//      checkField(employee.getPhoneNo(), "Phone Number");
//      checkField(employee.getPassword(), "Password");
//      checkLongField(employee.getBranchId(), "BranchID");
//      
//      return errorMsg;
//  }
}
