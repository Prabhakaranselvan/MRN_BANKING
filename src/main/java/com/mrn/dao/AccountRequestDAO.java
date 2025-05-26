package com.mrn.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountRequestDAO {

    public boolean addAccountRequest(AccountRequest request) throws InvalidException {
        String sql = "INSERT INTO account_request (branch_id, client_id, account_type, status, requested_time, modified_time, modified_by) "
                   + "VALUES (?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, request.getBranchId());
            pstmt.setLong(2, request.getClientId());
            pstmt.setInt(3, request.getAccountType());
            pstmt.setInt(4, request.getStatus());
            pstmt.setLong(5, request.getModifiedBy());

            return pstmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            String message = e.getMessage();
            if (message.contains("account_request_branch_id_foreign")) {
                throw new InvalidException("Invalid Branch ID.");
            } else if (message.contains("account_request_client_id_foreign")) {
                throw new InvalidException("Invalid Client ID.");
            } else if (message.contains("account_request_modified_by_foreign")) {
                throw new InvalidException("Invalid ModifiedBy User ID.");
            } else {
                throw new InvalidException("Foreign key constraint violation", e);
            }

        } catch (SQLException e) {
            throw new InvalidException("Error while inserting Account Request", e);
        }
    }

    public Accounts getRequestAsAccount(long requestId) throws InvalidException {
        String sql = "SELECT client_id, branch_id, account_type, status FROM account_request WHERE request_id = ?";

        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, requestId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Accounts account = new Accounts();
                account.setClientId(rs.getLong("client_id"));
                account.setBranchId(rs.getLong("branch_id"));
                account.setAccountType(rs.getShort("account_type"));
                account.setStatus(rs.getShort("status"));
                account.setBalance(BigDecimal.ZERO);
                return account;
            } else {
                throw new InvalidException("Request with ID " + requestId + " not found.");
            }
        } catch (SQLException e) {
            throw new InvalidException("Error while retrieving account request", e);
        }
    }

    public boolean updateRequestStatus(long requestId, short reviewStatus, long approverId) throws InvalidException {
        String sql = "UPDATE account_request SET status = ?, modified_by = ?, modified_time = UNIX_TIMESTAMP() WHERE request_id = ?";

        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setShort(1, reviewStatus);
            pstmt.setLong(2, approverId);
            pstmt.setLong(3, requestId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new InvalidException("Error updating request status", e);
        }
    }
}
