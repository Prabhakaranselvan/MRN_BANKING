package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.dao.ReportDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountsReport;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;

public class ReportHandler
{
	ReportDAO reportDAO = new ReportDAO();

	// In AccountsHandler.java
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			List<AccountsReport> stats = reportDAO.getAccountStatistics();
			return Utility.createResponse("Account statistics fetched successfully", "Stats", stats);
		});
	}

}
