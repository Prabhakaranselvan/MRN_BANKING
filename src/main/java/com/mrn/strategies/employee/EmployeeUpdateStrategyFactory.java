package com.mrn.strategies.employee;

import com.mrn.exception.InvalidException;
import com.mrn.strategies.HigherAuthorityUpdateStrategy;
import com.mrn.strategies.SelfUpdateStrategy;
import com.mrn.strategies.UpdateStrategy;

public class EmployeeUpdateStrategyFactory
{
	public static UpdateStrategy getStrategy(short sessionRole, short targetRole) throws InvalidException
	{
		if (sessionRole == targetRole && sessionRole != 3)
		{
			return new SelfUpdateStrategy();
		}
		else if (sessionRole > targetRole || sessionRole == 3)
		{
			return new HigherAuthorityUpdateStrategy();
		}

		throw new InvalidException("Unauthorized role for update");
	}
}
