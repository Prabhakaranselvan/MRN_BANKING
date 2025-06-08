package com.mrn.strategies.employee;

import com.mrn.exception.InvalidException;
import com.mrn.strategies.HigherAuthorityUpdateStrategy;
import com.mrn.strategies.SelfUpdateStrategy;
import com.mrn.strategies.UpdateStrategy;

public class EmployeeUpdateStrategyFactory
{
	public static UpdateStrategy getStrategy(short sessionRole, short targetRole) throws InvalidException
	{
		if (sessionRole == targetRole)
		{
			return new SelfUpdateStrategy();
		}
		else if (sessionRole > targetRole)
		{
			return new HigherAuthorityUpdateStrategy();
		}
		throw new InvalidException("Unauthorized role for update");
	}
}
