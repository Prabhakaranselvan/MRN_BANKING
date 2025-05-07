package com.mrn.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.utilshub.ConnectionManager;

public class ClientDAO {

	public boolean addClient(Client client) throws InvalidException 
	{
		String sql = "INSERT INTO client (client_id, date_of_birth, aadhar, pan, address) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) 
		{
			pstmt.setLong(1, client.getClientId());
			pstmt.setDate(2, Date.valueOf(client.getdob()));
			pstmt.setString(3, client.getAadhar());
			pstmt.setString(4, client.getPan());
			pstmt.setString(5, client.getAddress());

			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} 
		catch (SQLIntegrityConstraintViolationException e) 
		{
			String message = e.getMessage();

			if (message.contains("client_aadhar_unique")) 
			{
				throw new InvalidException("Duplicate Aadhar Number found");
			} 
			else if (message.contains("client_pan_unique")) 
			{
				throw new InvalidException("Duplicate PAN Number found");
			} 
			else if (message.contains("client_client_id_foreign")) 
			{
				throw new InvalidException("Invalid employee Id found as foreign Key");
			} 
			else 
			{
				throw new InvalidException("constraint violation occured", e);
			}
		} 
		catch (SQLException e) 
		{
			throw new InvalidException("Error occured while insterting Client Details", e);
		}
	}
}
