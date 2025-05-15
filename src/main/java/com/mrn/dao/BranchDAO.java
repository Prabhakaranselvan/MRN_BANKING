package com.mrn.dao;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class BranchDAO {

	public boolean addBranch(Branch branch) throws InvalidException {
		String sql = "INSERT INTO branch (branch_name, branch_location, contact_no, ifsc_code, created_time, modified_time, modified_by) "
				+ "VALUES (?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {

			pstmt.setString(1, branch.getBranchName());
			pstmt.setString(2, branch.getBranchLocation());
			pstmt.setString(3, branch.getContactNo());
			pstmt.setString(4, branch.getIfscCode());
			pstmt.setLong(5, branch.getModifiedBy());

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
	
	public long getNextBranchId() throws InvalidException 
	{
	    String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'MRNBankDB' AND TABLE_NAME = 'branch'";
	    
	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) 
		{
	        if (rs.next()) 
	        {
	            return rs.getLong("AUTO_INCREMENT");
	        } 
	        else 
	        {
	            throw new InvalidException("Unable to retrieve next Branch ID");
	        }

	    } 
		catch (SQLException e) 
		{
	        throw new InvalidException("Database error while fetching next Branch ID", e);
	    }
	}

	

}
