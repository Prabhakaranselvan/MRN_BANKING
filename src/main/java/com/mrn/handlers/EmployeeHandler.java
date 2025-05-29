package com.mrn.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mrn.dao.EmployeeDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Employee;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class EmployeeHandler
{
	UserDAO userDAO = new UserDAO();
	EmployeeDAO employeeDAO = new EmployeeDAO();

	// GET|GET /employee
	// 2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		try
		{
			short sessionRole = (short) session.get("userCategory");
			List<User> employees = new ArrayList<>();
			
			if (sessionRole == UserCategory.MANAGER.ordinal())
			{
				long branchId = (long) session.get("branchId");
				employees.addAll(employeeDAO.getEmployeesByBranchId(branchId));
			}
			else if (sessionRole == UserCategory.GENERAL_MANAGER.ordinal())
			{
				employees.addAll(userDAO.getUsersByCategory((short) UserCategory.EMPLOYEE.ordinal()));
				employees.addAll(userDAO.getUsersByCategory((short) UserCategory.MANAGER.ordinal()));
			}
			ConnectionManager.commit();
			return Utility.createResponse("Employees List Fetched Successfully", "Employees", employees);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch client details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// GET|POST /employee
	// 1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Employee employee = (Employee) pojoInstance;
			long sessionUserId = (long) session.get("userId");
			short sessionRole = (short) session.get("userCategory");

			employeeDAO.getEmployeeById(employee);
			short targetRole = employee.getUserCategory();
			long targetUserId = employee.getUserId();

			if (sessionRole == targetRole)
			{
				if (sessionUserId != targetUserId)
				{
					throw new InvalidException("You can only access your own profile");
				}
			}
			else if (sessionRole > targetRole && sessionRole == UserCategory.MANAGER.ordinal())
			{
				if ((long) session.get("branchId") != employee.getBranchId())
				{
					throw new InvalidException("You can only access employees within your branch");
				}
			}
			ConnectionManager.commit();
			return Utility.createResponse("Employee Detail Fetched Successfully", "Employee", employee);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch Employee details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// POST|POST /employee
	// 2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Employee employee = (Employee) pojoInstance;

			// Check for validation errors
			Utility.checkError(Validator.checkUser(employee));

			// Extract modifier & target user category
			UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));
			UserCategory targetRole = UserCategory.fromValue(employee.getUserCategory());

			if (sessionRole == UserCategory.MANAGER)
			{
				if (targetRole != UserCategory.EMPLOYEE)
				{
					throw new InvalidException("Managers can only create employees, not " + targetRole);
				}
				long modifierBranchId = (long) session.get("branchId");
				long targetBranchId = employee.getBranchId();
				if (targetBranchId != modifierBranchId)
				{
					throw new InvalidException("Managers can only assign employees to their own branch.");
				}
			}

			String hashedPassword = Utility.hashPassword(employee.getPassword());
			employee.setPassword(hashedPassword);

			short active = (short) Status.ACTIVE.getValue();
			employee.setStatus(active);

			long modifierId = (long) session.get("userId");
			employee.setModifiedBy(modifierId);

			long userId = userDAO.addUser(employee);

			employee.setEmployeeId(userId);
			boolean success = employeeDAO.addEmployee(employee);

			if (success)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Employee Added Successfully", "userId", userId);
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Employee Addition Failed");
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
			throw new InvalidException("Employee Addition failed due to an unexpected error", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// PUT|POST /employee
	// 1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{

			Employee updatedEmployee = (Employee) pojoInstance;
			long sessionUserId = (long) session.get("userId");
			short sessionRole = (short) session.get("userCategory");
			short targetRole = updatedEmployee.getUserCategory();
			long targetUserId = updatedEmployee.getUserId();

			updatedEmployee.setModifiedBy(sessionUserId);

			boolean updated = false;

			// Self-update check
			if (sessionRole == targetRole)
			{
				if (sessionUserId != targetUserId)
				{
					throw new InvalidException("You can only edit your own profile");
				}
				Utility.checkError(Validator.checkSelfUpdate(updatedEmployee));
				Utility.validatePassword(updatedEmployee.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
				updated = userDAO.updateByThemself(updatedEmployee);
			}
			else if (sessionRole > targetRole)
			{
				boolean isManager = sessionRole == UserCategory.MANAGER.ordinal();

				if (isManager)
				{
					long managerBranchId = (long) session.get("branchId");
					long employeeBranchId = employeeDAO.getBranchIdByEmployeeId(targetUserId);

					if (employeeBranchId != managerBranchId)
					{
						throw new InvalidException("You can only update employees within your branch");
					}
				}
				Utility.checkError(Validator.checkUser(updatedEmployee));
				Utility.validatePassword(updatedEmployee.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
				updated = userDAO.updateByHigherAuthority(updatedEmployee);
			}
			else
			{
				throw new InvalidException("You don't have permission to update this profile");
			}
			if (updated)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Employee updated Successfully");
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Update failed");
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
			throw new InvalidException("Employee update failed", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

}
