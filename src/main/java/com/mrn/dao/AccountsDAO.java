package com.mrn.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
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
			if (pstmt.executeUpdate() == 0)
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

	public List<Accounts> getAllAccounts(Long branchId) throws InvalidException
	{
		List<Accounts> accounts = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT account_no, branch_id, client_id, account_type, status, balance "
				+ "FROM accounts WHERE 1=1");

		if (branchId != null)
		{
			sql.append(" AND branch_id = ?");
		}

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql.toString()))
		{
			if (branchId != null)
			{
				pstmt.setLong(1, branchId);
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
			throw new InvalidException("Error fetching accounts by branch ID", e);
		}

		return accounts;
	}

	public Accounts getAccountByAccountNo(long accountNo) throws InvalidException
	{
		String sql = "SELECT account_no, branch_id, client_id, account_type, status, balance FROM accounts WHERE account_no = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, accountNo);
			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					Accounts account = new Accounts();
					account.setAccountNo(rs.getLong("account_no"));
					account.setBranchId(rs.getLong("branch_id"));
					account.setClientId(rs.getLong("client_id"));
					account.setAccountType(rs.getShort("account_type"));
					account.setStatus(rs.getShort("status"));
					account.setBalance(rs.getBigDecimal("balance"));
					return account;
				}
				else
				{
					throw new InvalidException("Account not found for the Account No:" + accountNo);
				}
			}
		}
		catch (SQLException e)
		{
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
					account.setStatus(rs.getShort("status"));
					account.setBalance(rs.getBigDecimal("balance"));
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

	public List<Long> getAccountsNoOfClientInBranch(long clientId, long branchId) throws InvalidException
	{
		String sql = "SELECT account_no FROM accounts WHERE client_id = ? AND branch_id = ?";
		List<Long> accountNumbers = new ArrayList<>();

		try (PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			stmt.setLong(1, clientId);
			stmt.setLong(2, branchId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				accountNumbers.add(rs.getLong("account_no"));
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Failed to fetch accounts for client ID and branch", e);
		}

		return accountNumbers;
	}

}
