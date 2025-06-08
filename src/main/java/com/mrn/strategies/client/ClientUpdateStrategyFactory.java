package com.mrn.strategies.client;

import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;

public class ClientUpdateStrategyFactory
{
	public static ClientUpdateStrategy getStrategy(short sessionRole) throws InvalidException
	{
		if (sessionRole == UserCategory.CLIENT.getValue())
		{
			return new SelfUpdateStrategy();
		}
		else if (sessionRole > UserCategory.CLIENT.getValue())
		{
			return new HigherAuthorityUpdateStrategy();
		}
		throw new InvalidException("Unsupported role for update");
	}
}
