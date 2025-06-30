package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.enums.TxnType;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountStatement;
import com.mrn.pojos.Transaction;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Utility;

public class TransactionDAO
{

	public void addTransaction(Transaction txn) throws InvalidException
	{
		String sql = "INSERT INTO transaction (client_id, account_no, peer_acc_no, amount, txn_type, txn_time, txn_status, "
				+ "txn_ref_No, closing_balance, description, done_by, extra_info) "
				+ "VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), ?, ?, ?, ?, ?, ?)";

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

			if (txn.getDescription() != null && !txn.getDescription().trim().isEmpty())
			{
				pstmt.setString(9, txn.getDescription().trim());
			}
			else
			{
				pstmt.setNull(9, java.sql.Types.VARCHAR);
			}
			pstmt.setLong(10, txn.getDoneBy());
			
			String extraInfo = txn.getExtraInfo();
			if (txn.isExternalTransfer())
			{
				pstmt.setString(11, extraInfo.trim());
			}
			else
			{
				pstmt.setNull(11, java.sql.Types.VARCHAR);
			}
			

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

	public List<Transaction> getTransactionsByAccount(AccountStatement input, boolean hasDateRange)
			throws InvalidException
	{
		return getTransactionsBy("account_no", input.getAccountNo(), input, hasDateRange);
	}

	public List<Transaction> getTransactionsByClientId(AccountStatement input, boolean hasDateRange)
			throws InvalidException
	{
		return getTransactionsBy("client_id", input.getClientId(), input, hasDateRange);
	}

	private List<Transaction> getTransactionsBy(String fieldName, Long fieldValue, AccountStatement input,
			boolean hasDateRange) throws InvalidException
	{

		StringBuilder sql = new StringBuilder("SELECT * FROM transaction WHERE " + fieldName + " = ?");

		List<Object> params = new ArrayList<>();
		params.add(fieldValue);

		Long fromEpoch = null, toEpoch = null;
		if (hasDateRange)
		{
			fromEpoch = Utility.convertDateToEpoch(input.getFromDate());
			toEpoch = Utility.convertDateToEpoch(input.getToDate()) + (24 * 60 * 60) - 1;
			sql.append(" AND txn_time BETWEEN ? AND ?");
			params.add(fromEpoch);
			params.add(toEpoch);
		}

		sql.append(" ORDER BY txn_time DESC LIMIT ? OFFSET ?");
		int limit = (input.getLimit() != null) ? input.getLimit() : 10;
		int page = (input.getPage() != null && input.getPage() > 0) ? input.getPage() : 1;
		int offset = (page - 1) * limit;

		params.add(limit);
		params.add(offset);

		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(sql.toString()))
		{
			for (int i = 0; i < params.size(); i++)
			{
				stmt.setObject(i + 1, params.get(i));
			}

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
			throw new InvalidException("Failed to fetch transactions by " + fieldName + ": " + fieldValue, e);
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

	public List<Transaction> getLatestTransactions(Long branchId, int limit, int offset) throws InvalidException
	{
		List<Transaction> transactions = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT t.* FROM transaction t");

		List<Object> params = new ArrayList<>();

		if (branchId != null)
		{
			sql.append(" JOIN accounts a ON t.account_no = a.account_no WHERE a.branch_id = ?");
			params.add(branchId);
		}
		else
		{
			sql.append(" WHERE 1=1"); // To allow uniform appending of clauses
		}

		sql.append(" ORDER BY t.txn_time DESC LIMIT ? OFFSET ?");
		params.add(limit);
		params.add(offset);

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql.toString()))
		{
			for (int i = 0; i < params.size(); i++)
			{
				Object param = params.get(i);
				if (param instanceof Long)
				{
					pstmt.setLong(i + 1, (Long) param);
				}
				else if (param instanceof Integer)
				{
					pstmt.setInt(i + 1, (Integer) param);
				}
			}

			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					transactions.add(mapRowToTransaction(rs));
				}
			}

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching latest transactions"
					+ (branchId != null ? " for branch ID: " + branchId : "") + ".", e);
		}

		return transactions;
	}
}
