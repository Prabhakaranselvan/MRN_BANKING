package com.mrn.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountsReport;
import com.mrn.utilshub.ConnectionManager;

public class ReportDAO
{
	public List<AccountsReport> getAccountStatistics() throws InvalidException
	{
		List<AccountsReport> stats = new ArrayList<>();

		String query = "SELECT 'Total Accounts' AS category, NULL AS subcategory, COUNT(*) AS count FROM accounts"
					+ " UNION ALL"
					+ " SELECT 'Accounts Per Branch', CAST(branch_id AS CHAR), COUNT(*) FROM accounts GROUP BY branch_id"
					+ " UNION ALL" 
					+ " SELECT 'Accounts By Type'," 
					+ " CASE account_type " 
					+ " WHEN 1 THEN 'Savings' "
					+ " WHEN 2 THEN 'Current'" 
					+ " WHEN 3 THEN 'Fixed Deposit' " 
					+ " ELSE 'Unknown'"
					+ " END, COUNT(*) FROM accounts GROUP BY account_type";

		try (PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(query);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				AccountsReport entry = new AccountsReport();
				entry.setCategory(rs.getString("category"));
				entry.setSubcategory(rs.getString("subcategory"));
				entry.setCount(rs.getInt("count"));
				stats.add(entry);
			}

		}
		catch (SQLException e)
		{
			throw new InvalidException("Error while generating account statistics", e);
		}

		return stats;
	}

}
