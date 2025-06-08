package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.ConnectionManager;

public class BranchDAO
{

	public void addBranch(Branch branch) throws InvalidException
	{
		String sql = "INSERT INTO branch (branch_name, branch_location, contact_no, ifsc_code, created_time, modified_time, modified_by) "
				+ "VALUES (?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{

			pstmt.setString(1, branch.getBranchName());
			pstmt.setString(2, branch.getBranchLocation());
			pstmt.setString(3, branch.getContactNo());
			pstmt.setString(4, branch.getIfscCode());
			pstmt.setLong(5, branch.getModifiedBy());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Branch Addition Failed");
			}

		}
		catch (SQLIntegrityConstraintViolationException e)
		{
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

	public List<Branch> getAllBranches() throws InvalidException
	{
		String sql = "SELECT branch_id, branch_name, branch_location, contact_no FROM branch";
		List<Branch> branchList = new ArrayList<>();

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				Branch branch = new Branch();
				branch.setBranchId(rs.getLong("branch_id"));
				branch.setBranchName(rs.getString("branch_name"));
				branch.setBranchLocation(rs.getString("branch_location"));
				branch.setContactNo(rs.getString("contact_no"));
				branchList.add(branch);
			}
			return branchList;

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while fetching all branches", e);
		}
	}

	public Branch getBranchById(long branchId) throws InvalidException
	{
		String sql = "SELECT * FROM branch WHERE branch_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, branchId);

			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					Branch branch = new Branch();
					branch.setBranchId(rs.getLong("branch_id"));
					branch.setBranchName(rs.getString("branch_name"));
					branch.setBranchLocation(rs.getString("branch_location"));
					branch.setContactNo(rs.getString("contact_no"));
					branch.setIfscCode(rs.getString("ifsc_code"));
					branch.setCreatedTime(rs.getLong("created_time"));
					branch.setModifiedTime(rs.getLong("modified_time"));
					branch.setModifiedBy(rs.getLong("modified_by"));
					return branch;
				}
				else
				{
					throw new InvalidException("Branch not found for ID: " + branchId);
				}
			}

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while fetching branch by ID", e);
		}
	}

	public void updateBranchDetails(Branch branch) throws InvalidException
	{
		String sql = "UPDATE branch SET branch_name = ?, branch_location = ?, contact_no = ?, "
				+ "modified_time = UNIX_TIMESTAMP(), modified_by = ? WHERE branch_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{

			pstmt.setString(1, branch.getBranchName());
			pstmt.setString(2, branch.getBranchLocation());
			pstmt.setString(3, branch.getContactNo());
			pstmt.setLong(4, branch.getModifiedBy());
			pstmt.setLong(5, branch.getBranchId());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Branch Updation Failed");
			}

		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			String message = e.getMessage();
			if (message.contains("branch_branch_name_unique"))
			{
				throw new InvalidException("Duplicate Branch Name");
			}
			else if (message.contains("branch_contact_no_unique"))
			{
				throw new InvalidException("Duplicate Contact Number");
			}
			else
			{
				throw new InvalidException("Constraint violation during branch update", e);
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error updating branch details", e);
		}
	}

}
