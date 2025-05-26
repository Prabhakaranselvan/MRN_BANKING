package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Transaction;
import com.mrn.utilshub.ConnectionManager;

public class TransactionDAO {

    public boolean addTransaction(Transaction txn) throws InvalidException {
        String sql = "INSERT INTO transaction (client_id, account_no, peer_acc_no, amount, txn_type, txn_time, txn_status, txn_ref_No, closing_balance, done_by) "
                   + "VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), ?, ?, ?, ?)";

        try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, txn.getClientId());
            pstmt.setLong(2, txn.getAccountNo());
            pstmt.setLong(3, txn.getPeerAccNo());
            pstmt.setBigDecimal(4, txn.getAmount());
            pstmt.setShort(5, txn.getTxnType());
            pstmt.setShort(6, txn.getTxnStatus());
            pstmt.setLong(7, txn.getTxnRefNo());
            pstmt.setBigDecimal(8, txn.getClosingBalance());
            pstmt.setLong(9, txn.getDoneBy());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new InvalidException("Error while inserting transaction", e);
        }
    }
}
