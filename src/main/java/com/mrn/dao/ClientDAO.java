package com.mrn.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.utilshub.ConnectionManager;

public class ClientDAO
{

	public void addClient(Client client) throws InvalidException
	{
		String sql = "INSERT INTO client (client_id, date_of_birth, aadhar, pan, address) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, client.getUserId());
			pstmt.setDate(2, Date.valueOf(client.getDob()));
			pstmt.setString(3, client.getAadhar());
			pstmt.setString(4, client.getPan());
			pstmt.setString(5, client.getAddress());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Insertion at Client Table Failed");
			}
		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			throw handleConstraintViolation(e);
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occured while insterting Client Details", e);
		}
	}

	public void getClientById(Client client) throws InvalidException
	{
		String sql = "SELECT u.*, c.date_of_birth, c.aadhar, c.pan, c.address FROM user u JOIN client c "
				+ "ON u.user_id = c.client_id WHERE u.user_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			long userId = client.getUserId();
			pstmt.setLong(1, userId);

			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					// From user table
					client.setUserCategory(rs.getShort("user_category"));
					client.setName(rs.getString("name"));
					client.setGender(rs.getString("gender"));
					client.setEmail(rs.getString("email"));
					client.setPhoneNo(rs.getString("phone_no"));
					client.setStatus(rs.getShort("status"));
					client.setCreatedTime(rs.getLong("created_time"));
					client.setModifiedTime(rs.getLong("modified_time"));
					long modifiedByValue = rs.getLong("modified_by");
					client.setModifiedBy(rs.wasNull() ? null : modifiedByValue);

					// From client table
					client.setDob(rs.getDate("date_of_birth").toString()); // or format it if needed
					client.setAadhar(rs.getString("aadhar"));
					client.setPan(rs.getString("pan"));
					client.setAddress(rs.getString("address"));
				}
				else
				{
					throw new InvalidException("Client not found with user_id: " + userId);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching client: " + e.getMessage());
		}
	}

	public void updateByThemself(Client client) throws InvalidException
	{
		String sql = "UPDATE client SET address = ? WHERE client_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{

			pstmt.setString(1, client.getAddress());
			pstmt.setLong(2, client.getUserId());

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
			throw new InvalidException("Error updating address", e);
		}
	}

	public void updateByHigherAuthority(Client client) throws InvalidException
	{
		String sql = "UPDATE client SET date_of_birth = ?, aadhar = ?, pan = ?, address = ? WHERE client_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{

			pstmt.setDate(1, Date.valueOf(client.getDob()));
			pstmt.setString(2, client.getAadhar());
			pstmt.setString(3, client.getPan());
			pstmt.setString(4, client.getAddress());
			pstmt.setLong(5, client.getUserId());

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
			throw new InvalidException("Error updating client details by employee", e);
		}
	}

	private InvalidException handleConstraintViolation(SQLIntegrityConstraintViolationException e)
	{
		String message = e.getMessage();

		if (message.contains("client_aadhar_unique"))
		{
			return new InvalidException("Duplicate Aadhar Number found");
		}
		else if (message.contains("client_pan_unique"))
		{
			return new InvalidException("Duplicate PAN Number found");
		}
		else if (message.contains("client_client_id_foreign"))
		{
			return new InvalidException("Invalid employee Id found as foreign Key");
		}
		else
		{
			return new InvalidException("constraint violation occured", e);
		}
	}

	public boolean exists(Long clientId) throws InvalidException
	{
		String sql = "SELECT 1 FROM client WHERE client_id = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, clientId);
			try (ResultSet rs = pstmt.executeQuery())
			{
				return rs.next();
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error validating client existence", e);
		}
	}

}
