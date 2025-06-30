package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.accesscontrol.AccessValidator;
import com.mrn.dao.EmployeeDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
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

	// GET|GET /employee(?:\\?.*)?
	// Roles: Manager (2), GM (3)
	public Map<String, Object> handleGet(Map<String, String> queryParams, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Long sessionBranchId = (Long) session.get("branchId");
			Short sessionRole = (Short) session.get("userCategory");

			// Validation
			Utility.checkError(Validator.checkEmployeeFilterParams(queryParams));

			// Safe parsing after validation
			Short filterRole = queryParams.containsKey("role") ? Short.parseShort(queryParams.get("role")) : null;
			Long filterBranchId = queryParams.containsKey("branchId") ? Long.parseLong(queryParams.get("branchId")) : null;
			int pageNo = Integer.parseInt(queryParams.getOrDefault("page", "1"));
			int limit = Integer.parseInt(queryParams.getOrDefault("limit", "10"));
			int offset = (pageNo - 1) * limit;

			// Access Restriction
			Short effectiveRole = (sessionRole == 2) ? Short.valueOf((short) 1) : filterRole;
			Long effectiveBranchId = (sessionRole == 2) ? sessionBranchId : filterBranchId;

			List<Employee> employees = employeeDAO.getAllEmployeesFiltered(effectiveRole, effectiveBranchId, limit, offset);

			return Utility.createResponse("Employee list fetched successfully", "employees", employees);
		});
	}


	// GET|POST /employee
	// 1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Employee employee = (Employee) pojoInstance;
			Utility.checkError(Validator.checkUserId(employee.getUserId()));
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
			employee.setStatus(Status.ACTIVE.getValue());
			employee.setModifiedBy((Long) session.get("userId"));

			employee.setUserId(userDAO.addUser(employee));
			employeeDAO.addEmployee(employee);
			return Utility.createResponse("Employee Added Successfully");
		});
	}

	// PUT|POST /employee
	// 1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Employee updatedEmp = (Employee) pojoInstance;
			Long targetUserId = updatedEmp.getUserId();
			Utility.checkError(Validator.checkUserId(targetUserId));
			short targetRole = userDAO.getUserCategoryById(targetUserId);
			long targetBranchId = employeeDAO.getBranchIdByEmployeeId(targetUserId);
			updatedEmp.setUserCategory(targetRole);
			updatedEmp.setBranchId(targetBranchId);
			AccessValidator.validatePut(updatedEmp, session);
			Short sessionRole = (Short) session.get("userCategory");

			UpdateStrategy strategy = EmployeeUpdateStrategyFactory.getStrategy(sessionRole, targetRole);
			strategy.update(updatedEmp, session);
			return Utility.createResponse("Employee updated Successfully");
		});
	}

}
