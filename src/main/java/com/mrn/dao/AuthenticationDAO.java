package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;

public class AuthenticationDAO 
{
	public String getPasswordByEmailOrPhone(String email, String phoneNo) throws InvalidException 
	{
	    String sql = "SELECT password FROM user WHERE email = ? OR phone_no = ?";
	    
	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) 
	    {
	        pstmt.setString(1, email);
	        pstmt.setString(2, phoneNo);
	        
	        try (ResultSet rs = pstmt.executeQuery()) 
	        {
	            if (rs.next()) 
	            {
	                return rs.getString("password");
	            } 
	            else 
	            {
	            	throw new InvalidException("User not found with given credentials");
	            }
	        }
	    } 
	    catch (SQLException e) 
	    {
	        throw new InvalidException("Error occurred while fetching Password", e);
	    }
	}
	
	public User getUserByEmailOrPhone(String email, String phoneNo) throws InvalidException 
	{
	    String sql = "SELECT * FROM user WHERE email = ? OR phone_no = ?";
	    
	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) 
	    {
	        pstmt.setString(1, email);
	        pstmt.setString(2, phoneNo);
	        
	        try (ResultSet rs = pstmt.executeQuery()) 
	        {
	            if (rs.next()) 
	            {
	                User user = new User();
	                user.setUserId(rs.getLong("user_id"));
	                user.setUserCategory(rs.getShort("user_category"));
	                user.setName(rs.getString("name"));
	                user.setEmail(rs.getString("email"));
	                user.setPhoneNo(rs.getString("phone_no"));
	                user.setPassword(rs.getString("password"));
	                user.setStatus(rs.getShort("status"));
	                return user;
	            } 
	            else 
	            {
	            	throw new InvalidException("User Details not found with given credentials");
	            }
	        }
	    } 
	    catch (SQLException e) 
	    {
	        throw new InvalidException("Error occurred while fetching User Details", e);
	    }
	}
	
	public long getBranchID(long userID) throws InvalidException 
	{
	    String sql = "SELECT branch_id FROM employee WHERE employee_id = ?";
	    
	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) 
	    {
	        pstmt.setLong(1, userID);
	        
	        try (ResultSet rs = pstmt.executeQuery()) 
	        {
	            if (rs.next()) 
	            {
	                return rs.getLong("branch_id");
	            } 
	            else 
	            {
	                throw new InvalidException("BranchID not found for the given User");
	            }
	        }
	    } 
	    catch (SQLException e) 
	    {
	        throw new InvalidException("Error occurred while fetching branchID", e);
	    }
	}
}
