package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Employee;
import com.mrn.utilshub.ConnectionManager;

public class EmployeeDAO {
	public boolean addEmployee(Employee employee) throws InvalidException {
		String sql = "INSERT INTO employee (employee_id, branch_id) VALUES (?, ?)";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
			pstmt.setLong(1, employee.getEmployeeId());
			pstmt.setLong(2, employee.getBranchId());

			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (SQLIntegrityConstraintViolationException e) {
			String message = e.getMessage();

			if (message.contains("employee_employee_id_foreign")) {
				throw new InvalidException("Invalid employee Id found as foreign Key");
			} else if (message.contains("employee_branch_id_foreign")) {
				throw new InvalidException("Invalid branch Id found as foreign Key");
			} else {
				throw new InvalidException("constraint violation occured", e);
			}
		} catch (SQLException e) {
			throw new InvalidException("Error occured while insterting employee Details", e);
		}
	}

}
