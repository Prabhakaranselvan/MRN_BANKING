package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;

public class UserDAO
{
	public long addUser(User user) throws InvalidException
	{
		String sql = "INSERT INTO user (user_category, name, gender, email, phone_no, password, status, created_time, "
				+ "modified_time, modified_by) VALUES (?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql,
				PreparedStatement.RETURN_GENERATED_KEYS))
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

			if (pstmt.executeUpdate() <= 0)
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
			throw handleConstraintViolation(e);
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occured while insterting user details", e);
		}
	}

	public List<User> getUsersByCategory(short category, int limit, int offset) throws InvalidException
	{
		List<User> users = new ArrayList<>();
		String sql = "SELECT user_id, user_category, name, email, status FROM user WHERE user_category = ? LIMIT ? OFFSET ?";		

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setShort(1, category);
			pstmt.setInt(2, limit);
			pstmt.setInt(3, offset);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					User user = new User();
					user.setUserId(rs.getLong("user_id"));
					user.setUserCategory(rs.getShort("user_category"));
					user.setName(rs.getString("name"));
					user.setEmail(rs.getString("email"));
					user.setStatus(rs.getShort("status"));
					users.add(user);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error in fetching Users List by category", e);
		}
		return users;
	}

	public short getUserCategoryById(long userId) throws InvalidException
	{
		String sql = "SELECT user_category FROM user WHERE user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, userId);
			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					return rs.getShort("user_category");
				}
				else
				{
					throw new InvalidException("User not found with ID: " + userId);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching user category by ID", e);
		}
	}

	public String getPasswordByUserId(long userId) throws InvalidException
	{
		String sql = "SELECT password FROM user WHERE user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, userId);
			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					return rs.getString("password");
				}
				else
				{
					throw new InvalidException("No user found with user_id: " + userId);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error retrieving password for user_id: " + userId, e);
		}
	}

	public void updateByThemself(User user) throws InvalidException
	{
		String sql = "UPDATE user SET email = ?, phone_no = ?, modified_time = UNIX_TIMESTAMP(), modified_by = ? WHERE user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{

			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, user.getPhoneNo());
			pstmt.setLong(3, user.getModifiedBy());
			pstmt.setLong(4, user.getUserId());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Update at User Table Failed");
			}

		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			throw handleConstraintViolation(e);
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error updating email and phone number", e);
		}
	}

	public void updateByHigherAuthority(User user) throws InvalidException
	{
		String sql = "UPDATE user SET name = ?, gender = ?, email = ?, phone_no = ?, status = ?, modified_time = UNIX_TIMESTAMP(), "
				+ "modified_by = ? WHERE user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{

			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getGender());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getPhoneNo());
			pstmt.setShort(5, user.getStatus());
			pstmt.setLong(6, user.getModifiedBy());
			pstmt.setLong(7, user.getUserId());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Update at User Table Failed");
			}

		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			throw handleConstraintViolation(e);
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error updating user details by employee", e);
		}
	}

	private InvalidException handleConstraintViolation(SQLIntegrityConstraintViolationException e)
	{
		String message = e.getMessage();
		if (message.contains("user_email_unique"))
		{
			return new InvalidException("Duplicate Email Address found");
		}
		else if (message.contains("user_phone_no_unique"))
		{
			return new InvalidException("Duplicate Phone Number found");
		}
		else
		{
			return new InvalidException("constraint violation occured", e);
		}
	}

}
