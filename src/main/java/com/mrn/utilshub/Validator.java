package com.mrn.utilshub;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.mrn.dao.AccountsDAO;
import com.mrn.dao.BranchDAO;
import com.mrn.dao.ClientDAO;
import com.mrn.enums.AccountType;
import com.mrn.enums.RequestStatus;
import com.mrn.enums.Status;
import com.mrn.enums.TxnType;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
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
	private static final BranchDAO branchDAO = new BranchDAO();
	private static final ClientDAO clientDAO = new ClientDAO();
	private static final AccountsDAO accountsDAO = new AccountsDAO();

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
		validationPatterns.put("Account No", "^\\d{11}$");
	}

	public static StringBuilder checkLoginCredentials(Login credentials)
	{
		errorMsg.setLength(0);

		checkField(credentials.getEmail(), "Email Address");
		checkField(credentials.getPassword(), "Password");

		return errorMsg;
	}

	public static StringBuilder checkUserId(Long userId)
	{
		errorMsg.setLength(0);
		checkLongField(userId, "User ID");
		return errorMsg;
	}

	public static StringBuilder checkUser(User user) throws InvalidException
	{
		errorMsg.setLength(0);

		validateEnum(() -> UserCategory.fromValue(user.getUserCategory()));
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
			Long branchId = employee.getBranchId();
			checkLongField(branchId, "BranchID");
			// validateBranchExists(branchId);
		}
		return errorMsg;
	}

	public static StringBuilder checkSelfUpdate(User user)
	{
		errorMsg.setLength(0);

		checkLongField(user.getUserId(), "User ID");
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

	public static StringBuilder checkHigherAuthorityUpdate(User user) throws InvalidException
	{
		errorMsg.setLength(0);

		checkUser(user);
		checkLongField(user.getUserId(), "User ID");
		validateEnum(() -> Status.fromValue(user.getStatus()));
		return errorMsg;
	}

	public static StringBuilder checkEmployeeFilterParams(Map<String, String> queryParams) throws InvalidException
	{
		errorMsg.setLength(0);

		String roleParam = queryParams.get("role");
		String branchIdParam = queryParams.get("branchId");
		String pageParam = queryParams.get("page");
		String limitParam = queryParams.get("limit");

		// Validate role if present
		if (roleParam != null)
		{
			try
			{
				Short role = Short.parseShort(roleParam);
				validateEnum(() -> UserCategory.fromValue(role));
				if (role == UserCategory.CLIENT.getValue())
				{
					errorMsg.append("Filtering by Client role is not allowed.<br/>");
				}
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Role must be numeric.<br/>");
			}
		}

		// Validate branchId if present
		if (branchIdParam != null)
		{
			try
			{
				Long branchId = Long.parseLong(branchIdParam);
				checkLongField(branchId, "Branch ID");
				validateBranchExists(branchId);
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Branch ID must be numeric.<br/>");
			}
		}

		validatePositiveIntegerParam(pageParam, "Page number");
		validatePositiveIntegerParam(limitParam, "Limit");

		return errorMsg;
	}

	public static StringBuilder checkBranchId(Long branchId)
	{
		errorMsg.setLength(0);
		checkLongField(branchId, "Branch ID");
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

	public static StringBuilder checkBranchUpdate(Branch branch) throws InvalidException
	{
		errorMsg.setLength(0);

		checkBranch(branch);
		Long branchId = branch.getBranchId();
		checkLongField(branchId, "Branch ID");
		validateBranchExists(branchId);
		return errorMsg;
	}

	public static StringBuilder checkAccountRequest(AccountRequest request) throws InvalidException
	{
		errorMsg.setLength(0);

		Long branchId = request.getBranchId();
		checkLongField(branchId, "Branch ID");
		// validateBranchExists(branchId);
		validateEnum(() -> AccountType.fromValue(request.getAccountType()));
		return errorMsg;
	}

	public static StringBuilder checkAccountRequestFilterParams(Map<String, String> queryParams) throws InvalidException
	{
		errorMsg.setLength(0);

		String statusParam = queryParams.get("status");
		String branchIdParam = queryParams.get("branchId");
		String pageParam = queryParams.get("page");
		String limitParam = queryParams.get("limit");

		// Validate status if present
		if (statusParam != null)
		{
			try
			{
				Short status = Short.parseShort(statusParam);
				validateEnum(() -> Status.fromValue(status));
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Status must be numeric.<br/>");
			}
		}

		// Validate branchId if present
		if (branchIdParam != null)
		{
			try
			{
				Long branchId = Long.parseLong(branchIdParam);
				checkLongField(branchId, "Branch ID");
				validateBranchExists(branchId);
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Branch ID must be numeric.<br/>");
			}
		}

		validatePositiveIntegerParam(pageParam, "Page number");
		validatePositiveIntegerParam(limitParam, "Limit");

		return errorMsg;
	}

	public static StringBuilder checkAccountApproval(AccountRequest request) throws InvalidException
	{
		errorMsg.setLength(0);

		checkLongField(request.getRequestId(), "Request ID");
		Short status = request.getStatus();
		validateEnum(() -> RequestStatus.fromValue(status));
		if (status == RequestStatus.PENDING.getValue())
		{
			errorMsg.append("Status cannot be set to 'PENDING' when processing an approval.<br/>");
		}
		return errorMsg;
	}

	public static StringBuilder checkAccountCreation(Accounts account) throws InvalidException
	{
		errorMsg.setLength(0);

		Long branchId = account.getBranchId();
		checkLongField(branchId, "Branch ID");
		// validateBranchExists(branchId);
		checkLongField(account.getClientId(), "Client ID");
		validateEnum(() -> AccountType.fromValue(account.getAccountType()));
		checkDecimalField(account.getBalance(), "Balance");
		return errorMsg;
	}

	public static StringBuilder checkAccountsFilterParams(Map<String, String> queryParams) throws InvalidException
	{
		errorMsg.setLength(0);

		String typeParam = queryParams.get("type");
		String branchIdParam = queryParams.get("branchId");
		String pageParam = queryParams.get("page");
		String limitParam = queryParams.get("limit");

		// Validate account type if present
		if (typeParam != null)
		{
			try
			{
				Short type = Short.parseShort(typeParam);
				validateEnum(() -> AccountType.fromValue(type));
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Account type must be numeric.<br/>");
			}
		}

		// Validate branchId if present
		if (branchIdParam != null)
		{
			try
			{
				Long branchId = Long.parseLong(branchIdParam);
				checkLongField(branchId, "Branch ID");
				validateBranchExists(branchId);
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Branch ID must be numeric.<br/>");
			}
		}

		validatePositiveIntegerParam(pageParam, "Page number");
		validatePositiveIntegerParam(limitParam, "Limit");

		return errorMsg;
	}

	public static StringBuilder checkClientId(Long clientId) throws InvalidException
	{
		errorMsg.setLength(0);
		checkLongField(clientId, "Client ID");
		validateClientExists(clientId);
		return errorMsg;
	}

	public static StringBuilder checkAccountNo(Long accountNo)
	{
		errorMsg.setLength(0);

		// Basic null and positive number check
		checkLongField(accountNo, "Account No");

		// Additional format check if not null and positive
		if (accountNo != null && accountNo > 0)
		{

			Pattern pattern = Pattern.compile(validationPatterns.get("Account No"));
			Matcher matcher = pattern.matcher(accountNo.toString());
			if (!matcher.matches())
			{
				errorMsg.append("Account No is invalid. Please follow the correct format.<br/>");
			}
		}
		return errorMsg;
	}

	public static StringBuilder checkAccountUpdate(Accounts account) throws InvalidException
	{
		errorMsg.setLength(0);

		Long accNo = account.getAccountNo();
		checkAccountNo(accNo);
		validateAccountNotClosed(accNo);
		validateEnum(() -> AccountType.fromValue(account.getAccountType()));
		Short updateStatus = account.getStatus();
		validateEnum(() -> Status.fromValue(updateStatus));

		if (updateStatus == Status.CLOSED.getValue())
		{
			BigDecimal balance = accountsDAO.getAccountBalance(accNo);
			if (balance.compareTo(BigDecimal.ZERO) != 0)
			{
				throw new InvalidException(
						"Account cannot be closed. Balance must be zero, current balance: ₹" + balance);
			}
		}

		return errorMsg;
	}

	public static void validateAccountNotClosed(Long accountNo) throws InvalidException
	{
		if (accountNo != null && accountNo > 0)
		{
			Short status = accountsDAO.getAccountStatus(accountNo);
			if (status == Status.CLOSED.getValue())
			{
				throw new InvalidException("Operation not allowed. The account is closed: " + accountNo);
			}
		}
	}

	public static StringBuilder checkStatementFilterParams(Map<String, String> queryParams) throws InvalidException
	{
		errorMsg.setLength(0);

		String branchIdParam = queryParams.get("branchId");
		String pageParam = queryParams.get("page");
		String limitParam = queryParams.get("limit");

		// Validate branch ID if present
		if (branchIdParam != null)
		{
			try
			{
				Long branchId = Long.parseLong(branchIdParam);
				checkLongField(branchId, "Branch ID");
				validateBranchExists(branchId);
			}
			catch (NumberFormatException e)
			{
				errorMsg.append("Branch ID must be numeric.<br/>");
			}
		}

		validatePositiveIntegerParam(pageParam, "Page number");
		validatePositiveIntegerParam(limitParam, "Limit");

		return errorMsg;
	}

	public static StringBuilder checkTransaction(Transaction txn) throws InvalidException
	{
		errorMsg.setLength(0);

		Long accNo = txn.getAccountNo();
		checkAccountNo(accNo);
		validateAccountStatus(accNo);
		checkDecimalField(txn.getAmount(), "Transaction Amount");
		checkField(txn.getPassword(), "Password");
		validateEnum(() -> TxnType.fromValue(txn.getTxnType()));

		if (errorMsg.length() > 0) return errorMsg; // Stop here if invalid enum

		TxnType txnType = TxnType.fromValue(txn.getTxnType());
		if (txnType == TxnType.DEBIT || txnType == TxnType.CREDIT)
		{
			checkLongField(txn.getPeerAccNo(), "Peer Acc No");
		}

		

		// ✅ Additional Validation for Outside Bank Transfers
		if (txnType == TxnType.DEBIT)
		{
			Long peerAccNo = txn.getPeerAccNo();
			if (peerAccNo != null && !accountsDAO.doesAccountExist(peerAccNo))
			{
				String extraInfo = txn.getExtraInfo();
				if (extraInfo == null || extraInfo.isEmpty())
				{
					errorMsg.append("Extra info is required for outside bank transfer.<br/>");
				}
				else
				{
					try
					{
						JSONObject extra = new JSONObject(extraInfo);
						if (!extra.has("peerBankName") || extra.getString("peerBankName").isBlank())
						{
							errorMsg.append("Peer Bank Name is required in extra info.<br/>");
						}
						if (!extra.has("peerIFSCCode") || extra.getString("peerIFSCCode").isBlank())
						{
							errorMsg.append("Peer IFSC Code is required in extra info.<br/>");
						}
						if (!extra.has("peerName") || extra.getString("peerName").isBlank())
						{
							errorMsg.append("Peer Name is required in extra info.<br/>");
						}
					}
					catch (Exception e)
					{
						errorMsg.append("Invalid extraInfo JSON format for outside bank transfer.<br/>");
					}
				}
			}
		}

		return errorMsg;
	}

	public static void validateAccountStatus(Long accountNo) throws InvalidException
	{
		if (accountNo != null && accountNo > 0)
		{
			Short status = accountsDAO.getAccountStatus(accountNo);
			if (status != Status.ACTIVE.getValue())
			{
				throw new InvalidException("Operation not permitted. Account " + accountNo + " is not active.");
			}
		}
	}

	private static void validateClientExists(Long clientId) throws InvalidException
	{
		if (clientId != null && clientId > 0)
		{
			if (!clientDAO.exists(clientId))
			{
				errorMsg.append("Invalid client ID provided: ").append(clientId).append("<br/>");
			}
		}
	}

	private static void validateBranchExists(Long branchId) throws InvalidException
	{
		// Only validate existence if branchId is valid
		if (branchId != null && branchId > 0)
		{
			if (!branchDAO.exists(branchId))
			{
				errorMsg.append("Invalid branch ID provided: ").append(branchId).append("<br/>");
			}
		}
	}

	private static void validatePositiveIntegerParam(String paramStr, String paramName)
	{
		if (paramStr != null)
		{
			try
			{
				int value = Integer.parseInt(paramStr);
				if (value <= 0)
				{
					errorMsg.append(paramName).append(" must be a positive number.<br/>");
				}
			}
			catch (NumberFormatException e)
			{
				errorMsg.append(paramName).append(" must be numeric.<br/>");
			}
		}
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
			errorMsg.append(e.getMessage() + "<br/>");
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
			errorMsg.append(fieldName).append(" cannot be zero or negative.<br/>");
		}
	}

	private static void checkDecimalField(BigDecimal value, String fieldName)
	{
		if (!checkNull(value, fieldName) && value.compareTo(BigDecimal.ZERO) <= 0)
		{
			errorMsg.append(fieldName).append(" cannot be zero or negative.<br/>");
		}
	}

//	public static StringBuilder checkEmployeeFilterParams(Short role, Long branchId, String pageStr, String limitStr)
//			throws InvalidException
//	{
//		errorMsg.setLength(0);
//
//		// Validate role if present
//		if (role != null)
//		{
//			validateEnum(() -> UserCategory.fromValue(role));
//			if (role == UserCategory.CLIENT.getValue())
//			{
//				errorMsg.append("Filtering by Client role is not allowed.<br/>");
//			}
//		}
//
//		// Validate branchId if present
//		if (branchId != null)
//		{
//			checkLongField(branchId, "Branch ID");
//			validateBranchExists(branchId);
//		}
//
//		validatePositiveIntegerParam(pageStr, "Page number");
//		validatePositiveIntegerParam(limitStr, "Limit");
//
//		return errorMsg;
//	}
}
