package com.mrn.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountDetails;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountsDAO
{
	public void addAccount(Accounts acc) throws InvalidException
	{
		String sql = "INSERT INTO accounts (branch_id, client_id, account_type, status, balance, created_time, modified_time, "
				+ "modified_by) VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, acc.getBranchId());
			pstmt.setLong(2, acc.getClientId());
			pstmt.setInt(3, acc.getAccountType());
			pstmt.setInt(4, acc.getStatus());
			pstmt.setBigDecimal(5, acc.getBalance());
			pstmt.setLong(6, acc.getModifiedBy());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Account creation failed");
			}
		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			String msg = e.getMessage();
			if (msg.contains("accounts_branch_id_foreign"))
			{
				throw new InvalidException("Invalid Branch ID");
			}
			else if (msg.contains("accounts_client_id_foreign"))
			{
				throw new InvalidException("Invalid Client ID");
			}
			else if (msg.contains("accounts_modified_by_foreign"))
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
			throw new InvalidException("Error occurred while inserting Account", e);
		}
	}

	public long getBranchIdFromAccount(long accNo) throws InvalidException
	{
		return fetchLongField(accNo, "branch_id");
	}

	public long getClientIdFromAccount(long accNo) throws InvalidException
	{
		return fetchLongField(accNo, "client_id");
	}

	private long fetchLongField(long accNo, String fieldName) throws InvalidException
	{
		String sql = "SELECT " + fieldName + " FROM accounts WHERE account_no = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, accNo);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				return rs.getLong(fieldName);
			}
			else
			{
				throw new InvalidException("Account not found.");
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while getting " + fieldName, e);
		}
	}

	public BigDecimal getBalanceWithLock(long accNo) throws InvalidException
	{
		String sql = "SELECT balance FROM accounts WHERE account_no = ? FOR UPDATE";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, accNo);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				return rs.getBigDecimal("balance");
			}
			else
			{
				throw new InvalidException("Account not found for balance check.");
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while getting Balance.", e);
		}
	}

	public void updateBalance(long accNo, BigDecimal newBal, long modifiedBy) throws InvalidException
	{
		String sql = "UPDATE accounts SET balance = ?, modified_time = UNIX_TIMESTAMP(), modified_by = ? WHERE account_no = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setBigDecimal(1, newBal);
			pstmt.setLong(2, modifiedBy);
			pstmt.setLong(3, accNo);
			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Failed to update account balance.");
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while updating Balance.", e);
		}
	}

	public boolean doesAccountExist(long peerAccNo) throws InvalidException 
	{
		String sql = "SELECT 1 FROM accounts WHERE account_no = ?";
		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, peerAccNo);
			ResultSet rs = pstmt.executeQuery();
			return rs.next(); // returns true if a record exists
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error occurred while checking account Existence.", e);
		}
	}
	
	public List<Accounts> getAllAccountsFiltered(Short type, Long branchId, int limit, int offset) throws InvalidException
	{
		List<Accounts> accounts = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT account_no, branch_id, client_id, account_type, status, balance "
				+ "FROM accounts WHERE 1=1");
		
		List<Object> params = new ArrayList<>();

		if (branchId != null)
		{
			sql.append(" AND branch_id = ?");
			params.add(branchId);
		}

		if (type != null)
		{
			sql.append(" AND account_type = ?");
			params.add(type);
		}

		sql.append(" ORDER BY account_no ASC LIMIT ? OFFSET ?");
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
				else if (param instanceof Short)
				{
					pstmt.setShort(i + 1, (Short) param);
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
					Accounts account = new Accounts();
					account.setAccountNo(rs.getLong("account_no"));
					account.setClientId(rs.getLong("client_id"));
					account.setAccountType(rs.getShort("account_type"));
					account.setStatus(rs.getShort("status"));
					account.setBalance(rs.getBigDecimal("balance"));
					accounts.add(account);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching accounts by Filter", e);
		}

		return accounts;
	}

	public AccountDetails getAccountByAccountNo(long accountNo) throws InvalidException {
	    String sql = "SELECT a.account_no, a.branch_id, a.client_id, a.account_type, a.status, a.balance, " +
	                 "a.created_time, b.branch_name, b.ifsc_code " +
	                 "FROM accounts a " +
	                 "JOIN branch b ON a.branch_id = b.branch_id " +
	                 "WHERE a.account_no = ?";

	    try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql)) {
	        pstmt.setLong(1, accountNo);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                // Populate Accounts object
	                Accounts account = new Accounts();
	                account.setAccountNo(rs.getLong("account_no"));
	                account.setBranchId(rs.getLong("branch_id"));
	                account.setClientId(rs.getLong("client_id"));
	                account.setAccountType(rs.getShort("account_type"));
	                account.setStatus(rs.getShort("status"));
	                account.setBalance(rs.getBigDecimal("balance"));
	                account.setCreatedTime(rs.getLong("created_time")); // assumed in Accounts

	                // Populate wrapper DTO
	                AccountDetails details = new AccountDetails();
	                details.setAccount(account);
	                details.setBranchName(rs.getString("branch_name"));
	                details.setIfscCode(rs.getString("ifsc_code"));

	                return details;
	            } else {
	                throw new InvalidException("Account not found for the Account No: " + accountNo);
	            }
	        }
	    } catch (SQLException e) {
	        throw new InvalidException("Error fetching account by account number", e);
	    }
	}


	public List<Accounts> getAccountsByClientId(long clientId) throws InvalidException
	{
		List<Accounts> accounts = new ArrayList<>();
		String sql = "SELECT account_no, branch_id, client_id, account_type, status, balance FROM accounts WHERE client_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, clientId);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					Accounts account = new Accounts();
					account.setAccountNo(rs.getLong("account_no"));
					account.setBranchId(rs.getLong("branch_id"));
					account.setClientId(rs.getLong("client_id"));
					account.setAccountType(rs.getShort("account_type"));
					account.setBalance(rs.getBigDecimal("balance"));
					account.setStatus(rs.getShort("status"));
					accounts.add(account);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching accounts by client ID", e);
		}
		return accounts;
	}

	public void updateAccount(Accounts acc) throws InvalidException
	{
		String sql = "UPDATE accounts SET account_type = ?, status = ?, modified_time = UNIX_TIMESTAMP(),"
				+ " modified_by = ? WHERE account_no = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setInt(1, acc.getAccountType());
			pstmt.setInt(2, acc.getStatus());
			pstmt.setLong(3, acc.getModifiedBy());
			pstmt.setLong(4, acc.getAccountNo());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Account update failed");
			}
		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			String msg = e.getMessage();
			if (msg.contains("accounts_modified_by_foreign"))
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
			throw new InvalidException("Error occurred while updating Account", e);
		}
	}

	public List<Long> getAccountsNoOfClientId(long clientId) throws InvalidException
	{
		String sql = "SELECT account_no FROM accounts WHERE client_id = ?";
		List<Long> accountNumbers = new ArrayList<>();

		try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			stmt.setLong(1, clientId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				accountNumbers.add(rs.getLong("account_no"));
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Failed to fetch accounts for client ID: " + clientId, e);
		}

		return accountNumbers;
	}


}
