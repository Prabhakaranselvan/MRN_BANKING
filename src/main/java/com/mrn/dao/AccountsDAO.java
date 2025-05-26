package com.mrn.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountsDAO {

    public boolean addAccount(Accounts acc) throws InvalidException {
        String sql = "INSERT INTO accounts (branch_id, client_id, account_type, status, balance, created_time, modified_time, modified_by) "
                   + "VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, acc.getBranchId());
            pstmt.setLong(2, acc.getClientId());
            pstmt.setInt(3, acc.getAccountType());
            pstmt.setInt(4, acc.getStatus());
            pstmt.setBigDecimal(5, acc.getBalance());
            pstmt.setLong(6, acc.getModifiedBy());

            return pstmt.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            String msg = e.getMessage();
            if (msg.contains("accounts_branch_id_foreign")) {
                throw new InvalidException("Invalid Branch ID");
            } else if (msg.contains("accounts_client_id_foreign")) {
                throw new InvalidException("Invalid Client ID");
            } else if (msg.contains("accounts_modified_by_foreign")) {
                throw new InvalidException("Invalid Modifier User ID");
            } else {
                throw new InvalidException("Constraint violation occurred", e);
            }
        } catch (SQLException e) {
            throw new InvalidException("Error occurred while inserting Account", e);
        }
    }

    public long getBranchIdFromAccount(long accNo) throws SQLException, InvalidException {
        return fetchLongField(accNo, "branch_id");
    }

    public long getClientIdFromAccount(long accNo) throws SQLException, InvalidException {
        return fetchLongField(accNo, "client_id");
    }

    private long fetchLongField(long accNo, String fieldName) throws SQLException, InvalidException {
        String sql = "SELECT " + fieldName + " FROM accounts WHERE account_no = ?";
        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, accNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(fieldName);
            } else {
                throw new InvalidException("Account not found.");
            }
        }
    }

    public BigDecimal getBalanceWithLock(long accNo) throws SQLException, InvalidException {
        String sql = "SELECT balance FROM accounts WHERE account_no = ? FOR UPDATE";
        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, accNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("balance");
            } else {
                throw new InvalidException("Account not found for balance check.");
            }
        }
    }

    public void updateBalance(long accNo, BigDecimal newBal, long modifiedBy) throws SQLException, InvalidException {
        String sql = "UPDATE accounts SET balance = ?, modified_time = UNIX_TIMESTAMP(), modified_by = ? WHERE account_no = ?";
        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBal);
            pstmt.setLong(2, modifiedBy);
            pstmt.setLong(3, accNo);
            if (pstmt.executeUpdate() == 0) {
                throw new InvalidException("Failed to update account balance.");
            }
        }
    }
    
    public boolean doesAccountExist(long peerAccNo) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE account_no = ?";
        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, peerAccNo);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // returns true if a record exists
        }
    }

}

