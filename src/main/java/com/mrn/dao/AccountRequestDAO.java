package com.mrn.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountRequestDAO
{

	public void addAccountRequest(AccountRequest request) throws InvalidException
	{
		String sql = "INSERT INTO account_request (branch_id, client_id, account_type, status, requested_time, modified_time, modified_by) "
				+ "VALUES (?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ?)";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, request.getBranchId());
			pstmt.setLong(2, request.getClientId());
			pstmt.setInt(3, request.getAccountType());
			pstmt.setInt(4, request.getStatus());
			pstmt.setLong(5, request.getModifiedBy());

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Failed to submit account creation request");
			}

		}
		catch (SQLIntegrityConstraintViolationException e)
		{
			String message = e.getMessage();
			if (message.contains("account_request_branch_id_foreign"))
			{
				throw new InvalidException("Invalid Branch ID.");
			}
			else if (message.contains("account_request_client_id_foreign"))
			{
				throw new InvalidException("Invalid Client ID.");
			}
			else if (message.contains("account_request_modified_by_foreign"))
			{
				throw new InvalidException("Invalid ModifiedBy User ID.");
			}
			else
			{
				throw new InvalidException("Foreign key constraint violation", e);
			}

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error while inserting Account Request", e);
		}
	}

	public List<AccountRequest> getAllAccountRequestsFiltered(Short status, Long branchId, int limit, int offset)
			throws InvalidException
	{
		List<AccountRequest> requests = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT request_id, branch_id, client_id, account_type, status, requested_time "
						+ "FROM account_request WHERE 1=1");

		List<Object> params = new ArrayList<>();

		if (branchId != null)
		{
			sql.append(" AND branch_id = ?");
			params.add(branchId);
		}

		if (status != null)
		{
			sql.append(" AND status = ?");
			params.add(status);
		}

		sql.append(" ORDER BY requested_time ASC LIMIT ? OFFSET ?");
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
					AccountRequest req = new AccountRequest();
					req.setRequestId(rs.getLong("request_id"));
					req.setBranchId(rs.getLong("branch_id"));
					req.setClientId(rs.getLong("client_id"));
					req.setAccountType(rs.getShort("account_type"));
					req.setStatus(rs.getShort("status"));
					req.setRequestedTime(rs.getLong("requested_time"));
					requests.add(req);
				}
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error fetching account requests", e);
		}

		return requests;
	}

	public Accounts getRequestAsAccount(long requestId) throws InvalidException
	{
		String sql = "SELECT client_id, branch_id, account_type, status FROM account_request WHERE request_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setLong(1, requestId);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				Accounts account = new Accounts();
				account.setClientId(rs.getLong("client_id"));
				account.setBranchId(rs.getLong("branch_id"));
				account.setAccountType(rs.getShort("account_type"));
				account.setStatus(rs.getShort("status"));
				account.setBalance(BigDecimal.ZERO);
				return account;
			}
			else
			{
				throw new InvalidException("Request with ID " + requestId + " not found.");
			}
		}
		catch (SQLException e)
		{
			throw new InvalidException("Error while retrieving account request", e);
		}
	}

	public void updateRequestStatus(long requestId, short reviewStatus, long approverId) throws InvalidException
	{
		String sql = "UPDATE account_request SET status = ?, modified_by = ?, modified_time = UNIX_TIMESTAMP() WHERE request_id = ?";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql))
		{
			pstmt.setShort(1, reviewStatus);
			pstmt.setLong(2, approverId);
			pstmt.setLong(3, requestId);

			if (pstmt.executeUpdate() <= 0)
			{
				throw new InvalidException("Failed to update the request status.");
			}

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error updating request status", e);
		}
	}
}
