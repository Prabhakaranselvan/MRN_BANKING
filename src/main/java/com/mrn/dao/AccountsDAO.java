package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountsDAO {

    public boolean addAccount(Accounts account) throws InvalidException 
    {
        String sql = "INSERT INTO accounts (branch_id, client_id, account_type, status, balance, created_time, modified_time, modified_by) "
                   + "VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) 
        {
            pstmt.setLong(1, account.getBranchId());
            pstmt.setLong(2, account.getClientId());
            pstmt.setInt(3, account.getAccountType());
            pstmt.setInt(4, account.getStatus());
            pstmt.setBigDecimal(5, account.getBalance());
            pstmt.setLong(6, account.getModifiedBy());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } 
        catch (SQLIntegrityConstraintViolationException e) 
        {
            String message = e.getMessage();

            if (message.contains("accounts_branch_id_foreign")) 
            {
                throw new InvalidException("Invalid Branch ID");
            } 
            else if (message.contains("accounts_client_id_foreign")) 
            {
                throw new InvalidException("Invalid Client ID");
            } 
            else if (message.contains("accounts_modified_by_foreign")) 
            {
                throw new InvalidException("Invalid Modifier User ID");
            }
            else 
            {
                throw new InvalidException("Constraint violation occurred", e);
            }
        } 
        catch (SQLException e) 
        {
            throw new InvalidException("Error occurred while inserting Account Details", e);
        }
    }
}

