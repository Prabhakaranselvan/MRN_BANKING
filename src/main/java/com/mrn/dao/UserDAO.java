package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;

public class UserDAO {
	public long addUser(User user) throws InvalidException 
	{
		String sql = "INSERT INTO user (user_category, name, gender, email, phone_no, password, status, created_time, modified_time, modified_by) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";
		
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)) 
		{
			pstmt.setShort(1, user.getUserCategory());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getGender());
			pstmt.setString(4, user.getEmail());
			pstmt.setString(5, user.getPhoneNo());
			pstmt.setString(6, user.getPassword());
			pstmt.setShort(7, user.getStatus());
			Long modifiedBy = user.getModifiedBy();
			if (modifiedBy != null) 
			{
				pstmt.setLong(8, modifiedBy);
			} 
			else 
			{
				pstmt.setNull(8, java.sql.Types.BIGINT);
			}
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows <= 0) 
			{
				throw new InvalidException("Insertion at User Table Failed");
			}

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) 
			{
				if (generatedKeys.next()) 
				{
					return generatedKeys.getLong(1); // Return the auto-generated user_id
				} 
				else 
				{
					throw new InvalidException("Error during user Insertion, no ID obtained.");
				}
			}
		} 
		catch (SQLIntegrityConstraintViolationException e) 
		{
			String message = e.getMessage();

			if (message.contains("user_email_unique")) 
			{
				throw new InvalidException("Duplicate Email Address found");
			} 
			else if (message.contains("user_phone_no_unique")) 
			{
				throw new InvalidException("Duplicate Phone Number found");
			} 
			else 
			{
				throw new InvalidException("constraint violation occured", e);
			}
		} 
		catch (SQLException e) 
		{
			throw new InvalidException("Error occured while insterting user details", e);
		}
	}

}
