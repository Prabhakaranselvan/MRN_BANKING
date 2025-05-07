package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;

public class AuthenticationDAO {
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
	                return null;
	            }
	        }
	    } 
	    catch (SQLException e) 
	    {
	        throw new InvalidException("Error occurred while fetching Password", e);
	    }
	}
	
	public User getUserByEmailOrPhone(String email, String phoneNo) throws InvalidException {
	    String sql = "SELECT * FROM user WHERE email = ? OR phone_no = ?";
	    
	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, phoneNo);
	        
	        try (ResultSet rs = pstmt.executeQuery()) 
	        {
	            if (rs.next()) 
	            {
	                User user = new User();
	                user.setUserId(rs.getLong("user_id"));
	                user.setUserCategory(rs.getString("user_category"));
	                user.setName(rs.getString("name"));
	                user.setEmail(rs.getString("email"));
	                user.setPhoneNo(rs.getString("phone_no"));
	                user.setPassword(rs.getString("password"));
	                user.setStatus(rs.getString("status"));
	                return user;
	            } 
	            else 
	            {
	                return null;
	            }
	        }
	    } catch (SQLException e) {
	        throw new InvalidException("Error occurred while fetching user", e);
	    }
	}


}
