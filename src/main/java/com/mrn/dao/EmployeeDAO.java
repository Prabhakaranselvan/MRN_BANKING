package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Employee;
import com.mrn.utilshub.ConnectionManager;

public class EmployeeDAO
{
	public void addEmployee(Employee employee) throws InvalidException
	{
		String sql = "INSERT INTO employee (employee_id, branch_id) VALUES (?, ?)";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, employee.getUserId());
			pstmt.setLong(2, employee.getBranchId());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Insertion at Client Table Failed");
			}
		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			String message = e.getMessage();

			if (message.contains("employee_employee_id_foreign"))
			{
				throw new InvalidException("Invalid employee Id found as foreign Key");
			}
			else if (message.contains("employee_branch_id_foreign"))
			{
				throw new InvalidException("Invalid branch Id found as foreign Key");
			}
			else
			{
				throw new InvalidException("constraint violation occured", e);
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occured while insterting employee Details", e);
		}
	}

	public List<Employee> getAllEmployees(Long branchId) throws InvalidException
	{
		List<Employee> employees = new ArrayList<>();

		StringBuilder sql = new StringBuilder(
				"SELECT u.user_id, u.name, u.email, u.status FROM user u JOIN employee e ON u.user_id = e.employee_id "
						+ "WHERE 1=1");

		if (branchId != null)
		{
			sql.append(" AND e.branch_id = ? AND u.user_category = 1");
		}
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql.toString()))
		{
			if (branchId != null)
			{
				pstmt.setLong(1, branchId);
			}

			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					Employee employee = new Employee();
					employee.setUserId(rs.getLong("user_id"));
					employee.setName(rs.getString("name"));
					employee.setEmail(rs.getString("email"));
					employee.setStatus(rs.getShort("status"));
					employees.add(employee);
				}
			}

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching employees by branch.", e);
		}

		return employees;
	}

	public List<Employee> getEmployeesFiltered(Short role, Long branchId) throws InvalidException
	{
		List<Employee> employees = new ArrayList<>();

		StringBuilder sql = new StringBuilder("SELECT u.user_id, u.name, u.email, u.status FROM user u "
				+ "JOIN employee e ON u.user_id = e.employee_id WHERE 1=1");

		// Use to track index for setting parameters
		List<Object> params = new ArrayList<>();

		if (branchId != null)
		{
			sql.append(" AND e.branch_id = ?");
			params.add(branchId);
		}

		if (role != null)
		{
			sql.append(" AND u.user_category = ?");
			params.add(role);
		}

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql.toString()))
		{
			for (int i = 0; i < params.size(); i++)
			{
				Object param = params.get(i);
				if (param instanceof Long)
				{
					pstmt.setLong(i + 1, (Long) param);
				}
				else if (param instanceof Short)
				{
					pstmt.setShort(i + 1, (Short) param);
				}
			}

			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					Employee employee = new Employee();
					employee.setUserId(rs.getLong("user_id"));
					employee.setName(rs.getString("name"));
					employee.setEmail(rs.getString("email"));
					employee.setStatus(rs.getShort("status"));
					employees.add(employee);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching filtered employees.", e);
		}
		return employees;
	}

	public void getEmployeeById(Employee employee) throws InvalidException
	{
		String sql = "SELECT u.*, e.branch_id FROM user u JOIN employee e ON u.user_id = e.employee_id "
				+ "WHERE u.user_id = ? ";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, employee.getUserId());

			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					employee.setUserId(rs.getLong("user_id"));
					employee.setUserCategory(rs.getShort("user_category"));
					employee.setName(rs.getString("name"));
					employee.setGender(rs.getString("gender"));
					employee.setEmail(rs.getString("email"));
					employee.setPhoneNo(rs.getString("phone_no"));
					employee.setStatus(rs.getShort("status"));
					employee.setCreatedTime(rs.getLong("created_time"));
					employee.setModifiedTime(rs.getLong("modified_time"));

					long modifiedByValue = rs.getLong("modified_by");
					employee.setModifiedBy(rs.wasNull() ? null : modifiedByValue);
					employee.setBranchId(rs.getLong("branch_id"));
				}
				else
				{
					throw new InvalidException("Employee not found with user_id: " + employee.getUserId());
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching Employee by ID. ", e);
		}
	}

	public long getBranchIdByEmployeeId(long employeeId) throws InvalidException
	{
		String sql = "SELECT branch_id FROM employee WHERE employee_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, employeeId);

			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					return rs.getLong("branch_id");
				}
				else
				{
					throw new InvalidException("Employee not found with ID: " + employeeId);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while fetching branch ID for employee", e);
		}
	}

}
