package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;

public class UserDAO {
	public long addUser(User user) throws InvalidException {
		String sql = "INSERT INTO user (user_category, name, gender, email, phone_no, password, status, created_time, modified_time, modified_by) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql,
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			pstmt.setShort(1, user.getUserCategory());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getGender());
			pstmt.setString(4, user.getEmail());
			pstmt.setString(5, user.getPhoneNo());
			pstmt.setString(6, user.getPassword());
			pstmt.setShort(7, user.getStatus());
			Long modifiedBy = user.getModifiedBy();
			if (modifiedBy != null) {
				pstmt.setLong(8, modifiedBy);
			} else {
				pstmt.setNull(8, java.sql.Types.BIGINT);
			}
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows <= 0) {
				throw new InvalidException("Insertion at User Table Failed");
			}

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1); // Return the auto-generated user_id
				} else {
					throw new InvalidException("Error during user Insertion, no ID obtained.");
				}
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			String message = e.getMessage();

			if (message.contains("user_email_unique")) {
				throw new InvalidException("Duplicate Email Address found");
			} else if (message.contains("user_phone_no_unique")) {
				throw new InvalidException("Duplicate Phone Number found");
			} else {
				throw new InvalidException("constraint violation occured", e);
			}
		} catch (SQLException e) {
			throw new InvalidException("Error occured while insterting user details", e);
		}
	}
	
	public List<User> getUsersByCategory(short category) throws InvalidException {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE user_category = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
			pstmt.setShort(1, category);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					users.add(mapToUser(rs));
				}
			}
		} catch (SQLException e) {
			throw new InvalidException("Error fetching users by category: " + e.getMessage());
		}
		return users;
	}

	public User getUserById(long userId) throws InvalidException {
		String sql = "SELECT * FROM user WHERE user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
			pstmt.setLong(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapToUser(rs);
				} else {
					throw new InvalidException("User not found with ID: " + userId);
				}
			}
		} catch (SQLException e) {
			throw new InvalidException("Database error: " + e.getMessage());
		}
	}

	public String getPasswordByUserId(long userId) throws InvalidException {
		String sql = "SELECT password FROM user WHERE user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
			pstmt.setLong(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("password");
				} else {
					throw new InvalidException("No user found with user_id: " + userId);
				}
			}
		} catch (SQLException e) {
			throw new InvalidException("Error retrieving password for user_id: " + userId, e);
		}
	}

	public boolean updateByClient(Client client) throws InvalidException {
		String updateUserSql = "UPDATE user SET email = ?, phone_no = ?, modified_time = UNIX_TIMESTAMP(), modified_by = ? WHERE user_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(updateUserSql)) {

			pstmt.setString(1, client.getEmail());
			pstmt.setString(2, client.getPhoneNo());
			pstmt.setLong(3, client.getModifiedBy());
			pstmt.setLong(4, client.getUserId());

			int rowsUpdated = pstmt.executeUpdate();
			return rowsUpdated > 0;

		} catch (SQLException e) {
			throw new InvalidException("Error updating email and phone number", e);
		}
	}
	
	public boolean updateByEmployee(Client client) throws InvalidException {
	    String updateUserSql = "UPDATE user SET name = ?, gender = ?, email = ?, phone_no = ?, status = ?, "
	    		+ "modified_time = UNIX_TIMESTAMP(), modified_by = ? WHERE user_id = ?";

	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(updateUserSql)) {

	        pstmt.setString(1, client.getName());
	        pstmt.setString(2, client.getGender());
	        pstmt.setString(3, client.getEmail());
	        pstmt.setString(4, client.getPhoneNo());
	        pstmt.setShort(5, client.getStatus());
	        pstmt.setLong(6, client.getModifiedBy());
	        pstmt.setLong(7, client.getUserId());

	        int rowsUpdated = pstmt.executeUpdate();
	        return rowsUpdated > 0;

	    } catch (SQLException e) {
	        throw new InvalidException("Error updating user details by employee", e);
	    }
	}


//	public List<User> getClients() throws InvalidException {
//		String sql = "SELECT u.* FROM user u JOIN client c ON u.user_id = c.client_id WHERE u.user_category = 0";
//		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
//				ResultSet rs = pstmt.executeQuery()) {
//			List<User> clients = new ArrayList<>();
//			while (rs.next()) {
//				clients.add(mapToUser(rs));
//			}
//			return clients;
//		} catch (SQLException e) {
//			throw new InvalidException("Error fetching clients: " + e.getMessage());
//		}
//
//	}
	
//	public List<User> getEmployeesByBranchId(long branchId) throws InvalidException {
//		List<User> employees = new ArrayList<>();
//		String sql = "SELECT u.* FROM user u JOIN employee e ON u.user_id = e.employee_id WHERE e.branch_id = ? AND u.user_category = 1";
//		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
//
//			pstmt.setLong(1, branchId);
//			try (ResultSet rs = pstmt.executeQuery()) {
//				while (rs.next()) {
//					employees.add(mapToUser(rs));
//				}
//			}
//		} catch (SQLException e) {
//			throw new InvalidException("Error fetching employees: " + e.getMessage());
//		}
//		return employees;
//	}
//
//	
//
//	public List<User> getAllUsers() throws InvalidException {
//		List<User> users = new ArrayList<>();
//		String sql = "SELECT * FROM user";
//		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
//				ResultSet rs = pstmt.executeQuery()) {
//
//			while (rs.next()) {
//				users.add(mapToUser(rs));
//			}
//		} catch (SQLException e) {
//			throw new InvalidException("Error fetching all users: " + e.getMessage());
//		}
//		return users;
//	}

	private User mapToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getLong("user_id"));
		user.setUserCategory(rs.getShort("user_category"));
		user.setName(rs.getString("name"));
		user.setGender(rs.getString("gender"));
		user.setEmail(rs.getString("email"));
		user.setPhoneNo(rs.getString("phone_no"));
		user.setPassword(rs.getString("password"));
		user.setStatus(rs.getShort("status"));
		user.setCreatedTime(rs.getLong("created_time"));
		user.setModifiedTime(rs.getLong("modified_time"));

		long modifiedByValue = rs.getLong("modified_by");
		user.setModifiedBy(rs.wasNull() ? null : modifiedByValue);

		return user;
	}

}
