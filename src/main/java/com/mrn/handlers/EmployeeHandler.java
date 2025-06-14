package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.accesscontrol.AccessValidator;
import com.mrn.dao.EmployeeDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Employee;
import com.mrn.strategies.UpdateStrategy;
import com.mrn.strategies.employee.EmployeeUpdateStrategyFactory;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class EmployeeHandler
{
	private final UserDAO userDAO = new UserDAO();
	private final EmployeeDAO employeeDAO = new EmployeeDAO();

	// GET|GET /employee
	// 2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Long sessionBranchId = (Long) session.get("branchId");
			List<Employee> employees = employeeDAO.getAllEmployees(sessionBranchId);
			return Utility.createResponse("Employees List Fetched Successfully", "Employees", employees);
		});
	}
	
	// GET|GET /employee\\?.*
	// 3
	public Map<String, Object> handleGet(Map<String, String> queryParams, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			String roleParam = queryParams.get("role");
			String branchIdParam = queryParams.get("branchId");
	
			Short role = (roleParam != null) ? (short) UserCategory.valueOf(roleParam).getValue() : null;
			Long branchId = (branchIdParam != null) ? Long.parseLong(branchIdParam) : null;
			List<Employee> employees = employeeDAO.getEmployeesFiltered(role, branchId);
			return Utility.createResponse("Filtered employee list fetched successfully", "employees", employees);
		});
	}

	// GET|POST /employee 
	// 1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Employee employee = (Employee) pojoInstance;;
			employeeDAO.getEmployeeById(employee);
			AccessValidator.validateGet(pojoInstance, session);
			return Utility.createResponse("Employee Detail Fetched Successfully", "Employee", employee);
		});
	}

	// POST|POST /employee
	// 2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Employee employee = (Employee) pojoInstance;
			Utility.checkError(Validator.checkUser(employee));
			AccessValidator.validatePost(pojoInstance, session);

			employee.setPassword(Utility.hashPassword(employee.getPassword()));
			employee.setStatus((short) Status.ACTIVE.getValue());
			employee.setModifiedBy((long) session.get("userId"));

			long userId = userDAO.addUser(employee);
			employee.setUserId(userId);
			employeeDAO.addEmployee(employee);
			return Utility.createResponse("Employee Added Successfully", "userId", userId);
		});
	}

	// PUT|POST /employee
	// 1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Employee updatedEmp = (Employee) pojoInstance;
			long targetUserId = updatedEmp.getUserId();
			short targetRole = userDAO.getUserCategoryById(targetUserId);
			long targetBranchId = employeeDAO.getBranchIdByEmployeeId(targetUserId);
			updatedEmp.setUserCategory(targetRole);
			updatedEmp.setBranchId(targetBranchId);
			AccessValidator.validatePut(updatedEmp, session);
			short sessionRole = (short) session.get("userCategory");
			
			UpdateStrategy strategy = EmployeeUpdateStrategyFactory.getStrategy(sessionRole, targetRole);
			strategy.update(updatedEmp, session);
			return Utility.createResponse("Employee updated Successfully");
		});
	}

}
