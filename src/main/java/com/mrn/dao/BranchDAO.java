package com.mrn.dao;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class BranchDAO {

	public boolean addBranch(Branch branch) throws InvalidException {
		String sql = "INSERT INTO branch (branch_name, branch_location, contact_no, branch_manager_id, ifsc_code, created_time, "
				+ "modified_time, modified_by) VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {

			pstmt.setString(1, branch.getBranchName());
			pstmt.setString(2, branch.getBranchLocation());
			pstmt.setString(3, branch.getContactNo());

			if (branch.getBranchManagerId() != null) {
				pstmt.setLong(4, branch.getBranchManagerId());
			} else {
				pstmt.setNull(4, java.sql.Types.BIGINT);
			}

			pstmt.setString(5, branch.getIfscCode());
			pstmt.setLong(6, branch.getModifiedBy());

			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;

		} catch (SQLIntegrityConstraintViolationException e) {
			String message = e.getMessage();

			if (message.contains("branch_branch_name_unique")) 
			{
				throw new InvalidException("Duplicate Branch Name found");
			} 
			else if (message.contains("branch_contact_no_unique")) 
			{
				throw new InvalidException("Duplicate Branch Contact Number found");
			} 
			else if (message.contains("branch_branch_manager_id_unique")) 
			{
				throw new InvalidException("Duplicate Branch Manager ID found");
			} 
			else if (message.contains("branch_ifsc_code_unique")) 
			{
				throw new InvalidException("Duplicate Branch IFSC Code found");
			} 
			else 
			{
				throw new InvalidException("constraint violation occured", e);
			}
		} 
		catch (SQLException e) 
		{
			throw new InvalidException("Error occured while insterting Branch Details", e);
		}
	}

}
