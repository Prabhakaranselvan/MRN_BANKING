package com.mrn.utilshub;

import java.util.Map;
import com.mrn.exception.InvalidException;

public class TransactionExecutor
{

	public static Map<String, Object> execute(TransactionalSupplier<Map<String, Object>> action) throws InvalidException
	{
		try
		{
			Map<String, Object> response = action.get();
			ConnectionManager.commit();
			return response;
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Operation failed due to an unexpected error", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}
}
