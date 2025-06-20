package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.enums.TxnType;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Transaction;
import com.mrn.utilshub.ConnectionManager;

public class TransactionDAO
{

	public void addTransaction(Transaction txn) throws InvalidException
	{
		String sql = "INSERT INTO transaction (client_id, account_no, peer_acc_no, amount, txn_type, txn_time, txn_status, txn_ref_No, closing_balance, done_by) "
				+ "VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), ?, ?, ?, ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, txn.getClientId());
			pstmt.setLong(2, txn.getAccountNo());
			Short txnType = txn.getTxnType();
			if (txnType == TxnType.DEBIT.getValue() || txnType == TxnType.CREDIT.getValue())
			{
				pstmt.setLong(3, txn.getPeerAccNo());
			}
			else
			{
				pstmt.setNull(3, java.sql.Types.BIGINT);
			}
			pstmt.setBigDecimal(4, txn.getAmount());
			pstmt.setShort(5, txnType);
			pstmt.setShort(6, txn.getTxnStatus());
			pstmt.setLong(7, txn.getTxnRefNo());
			pstmt.setBigDecimal(8, txn.getClosingBalance());
			pstmt.setLong(9, txn.getDoneBy());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Transaction failed to persist.");
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error while inserting transaction", e);
		}
	}

	public List<Transaction> getTransactionsByAccount(long accountNo, long fromEpoch, long toEpoch)
			throws InvalidException
	{
		return getTransactionsBy("account_no", accountNo, fromEpoch, toEpoch);
	}

	public List<Transaction> getTransactionsByClientId(long clientId, long fromEpoch, long toEpoch)
			throws InvalidException
	{
		System.out.print(fromEpoch);
		System.out.print(toEpoch);
		return getTransactionsBy("client_id", clientId, fromEpoch, toEpoch);
	}

	private List<Transaction> getTransactionsBy(String fieldName, long value, long fromEpoch, long toEpoch)
			throws InvalidException
	{

		String sql = "SELECT * FROM transaction WHERE " + fieldName
				+ " = ? AND txn_time BETWEEN ? AND ? ORDER BY txn_time DESC";
		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			stmt.setLong(1, value);
			stmt.setLong(2, fromEpoch);
			stmt.setLong(3, toEpoch);
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					transactions.add(mapRowToTransaction(rs));
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Failed to fetch transactions by " + fieldName + ": " + value, e);
		}

		return transactions;
	}

	public List<Transaction> getLastNTransactionsByAccount(long accountNo, int limit) throws InvalidException
	{
		return getLastNTransactionsBy("account_no", accountNo, limit);
	}

	public List<Transaction> getLastNTransactionsByClientId(long clientId, int limit) throws InvalidException
	{
		return getLastNTransactionsBy("client_id", clientId, limit);
	}

	private List<Transaction> getLastNTransactionsBy(String fieldName, long value, int limit) throws InvalidException
	{

		String sql = "SELECT * FROM transaction WHERE " + fieldName + " = ? ORDER BY txn_time DESC LIMIT ?";
		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			stmt.setLong(1, value);
			stmt.setInt(2, limit);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				transactions.add(mapRowToTransaction(rs));
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Failed to fetch last " + limit + " transactions for " + fieldName + " = " + value, e);
		}

		return transactions;
	}

	private Transaction mapRowToTransaction(ResultSet rs) throws SQLException
	{
		Transaction txn = new Transaction();
		txn.setTxnId(rs.getLong("txn_id"));
		txn.setClientId(rs.getLong("client_id"));
		txn.setAccountNo(rs.getLong("account_no"));
		txn.setPeerAccNo(rs.getLong("peer_acc_no"));
		txn.setAmount(rs.getBigDecimal("amount"));
		txn.setTxnType(rs.getShort("txn_type"));
		txn.setTxnTime(rs.getLong("txn_time"));
		txn.setTxnStatus(rs.getShort("txn_status"));
		txn.setTxnRefNo(rs.getLong("txn_ref_No"));
		txn.setClosingBalance(rs.getBigDecimal("closing_balance"));
		txn.setDescription(rs.getString("description"));
		txn.setDoneBy(rs.getLong("done_by"));
		return txn;
	}

}
